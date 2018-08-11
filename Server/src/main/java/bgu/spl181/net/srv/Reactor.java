package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.impl.impl_interfaces.impl_Connections;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.logging.Handler;

/*
 * this class implements Server.
 * in the class, there is a selector that his thread select non stop keys from it table. 
 * the class creates a serverSocketChannel that entered the selector table with the key 'accept'.
 * when a client connect to the serverSocket, 
 * the acceptHadler method will creates new handler for him of the type NonBlocking, 
 * and added a task to the threadPool to add the client and start the protocol.
 * in the end, the acceptHadler added the clientChannel to the selector table with the key 'read'.
 * when the selector select the key read and handle it, 
 * the handler will added the keys read&write to the table, and move on...
 * until the thread pool shutdown and the selector thread interrupted.
 */
public class Reactor<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> readerFactory;
    private final ActorThreadPool pool;
    private Selector selector;
    private Thread selectorThread;
    private final ConcurrentLinkedQueue<Runnable> selectorTasks = new ConcurrentLinkedQueue<>();
    private final impl_Connections<T> connections = new impl_Connections<>();

    public Reactor(
            int numThreads,
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> readerFactory) {

        this.pool = new ActorThreadPool(numThreads);
        this.port = port;
        this.protocolFactory = (Supplier<BidiMessagingProtocol<T>>)protocolFactory;
        this.readerFactory = readerFactory;
    }

    @Override
    public void serve()
    {
    	selectorThread = Thread.currentThread();
    	
        try (	Selector selector = Selector.open();
        		ServerSocketChannel serverSock = ServerSocketChannel.open() ) {
        	
            this.selector = selector; //just to be able to close
            
            serverSock.bind(new InetSocketAddress(port));
            serverSock.configureBlocking(false);
            serverSock.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server started");

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                runSelectionThreadTasks();

                for (SelectionKey key : selector.selectedKeys()) {

                    if (!key.isValid()) {
                        continue;
                    } else if (key.isAcceptable()) {
                        handleAccept(serverSock, selector);
                    } else {
                        handleReadWrite(key);
                    }
                }

                selector.selectedKeys().clear(); //clear the selected keys set so that we can know about new events

            }

        } catch (ClosedSelectorException ex) {
            //do nothing - server was requested to be closed
        } catch (IOException ex) {
            //this is an error
            ex.printStackTrace();
        }

        System.out.println("server closed!!!");
        pool.shutdown();
    }

    /*package*/ void updateInterestedOps(SocketChannel chan, int ops) 
    {
        final SelectionKey key = chan.keyFor(selector);
        if (Thread.currentThread() == selectorThread) {
            key.interestOps(ops);
        } else {
            selectorTasks.add(() -> {
                key.interestOps(ops);
            });
            selector.wakeup();
        }
    }

    private void handleAccept(ServerSocketChannel serverChan, Selector selector) throws IOException 
    {
        SocketChannel clientChan = serverChan.accept();
        //a client connect to the server, so we need to add him to the connections list, and creates his own handler
        clientChan.configureBlocking(false);
        BidiMessagingProtocol<T> bidiMessagingProtocol = protocolFactory.get();
        final NonBlockingConnectionHandler<T> handler = new NonBlockingConnectionHandler<T>(
        		readerFactory.get(), bidiMessagingProtocol, clientChan, this,
        		connections.getConnectionId(), connections);
        clientChan.register(selector, SelectionKey.OP_READ, handler);
        //will pick a thread from the pool to add the client to the list and connect the messaging protocol to the client
        pool.submit(handler, ()->{
        	bidiMessagingProtocol.start(connections.getConnectionId(), (Connections<T>) connections);
        	connections.addClient(connections.getAndIncConnectionId(), (ConnectionHandler<Command>) handler);
        	});
    }

    private void handleReadWrite(SelectionKey key) 
    {
        NonBlockingConnectionHandler<T> handler = (NonBlockingConnectionHandler<T>) key.attachment();

        if (key.isReadable()) {
            Runnable task = handler.continueRead();
            if (task != null) {
            	//to keep the correct order of read(submit will add the task to a queue)
                pool.submit(handler, task);
            }
        }

	    if (key.isValid() && key.isWritable()) {
            handler.continueWrite();
        }
    }

    private void runSelectionThreadTasks() {
        while (!selectorTasks.isEmpty()) {
            selectorTasks.remove().run();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

}
