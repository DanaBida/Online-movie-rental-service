package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.impl.impl_interfaces.impl_Connections;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

/*
 * this class implements Server.
 * it creates new serverSocket that non stop running (until it will be closed/interrupted) and block on accept until getting new client.
 * when a client connect to him, a new socket created and the supplier supplies new encDec and bidiMesseging objects. 
 * after that, new handler of type Blocking created, the client been added to the connections and the protocol start.
 * in the end - the execute method (that the threadPerClient implements in this case) called.
 * see the implements and the continue of the explanation in threadPerClient class.
 */
public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private impl_Connections<T> connections = new impl_Connections<>();

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
	
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {
            	
                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<T> protocol = protocolFactory.get();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(
                        clientSock, encdecFactory.get(), protocol, connections.getConnectionId(), connections);
                
                //be aware !!!! - the start and add client actions performs only after we send the handler the data
                protocol.start(connections.getConnectionId(), (Connections<T>) connections);
                connections.addClient(connections.getAndIncConnectionId(), (ConnectionHandler<Command>) handler);
                
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
