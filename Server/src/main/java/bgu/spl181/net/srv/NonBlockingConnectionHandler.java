package bgu.spl181.net.srv;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.impl_interfaces.impl_Connections;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor<T> reactor;
    
    private int connectionId;
    private impl_Connections<T> connections;
    private boolean isStarted = false;//ensure the protocol connect to the owner client
    private volatile boolean isConnected = true;//to ensure the client havn't been disconnected by the close method

    public NonBlockingConnectionHandler(
            MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol,
            SocketChannel chan,
            Reactor<T> reactor,
            int connectionId,
            impl_Connections<T> connections) 
    {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = (BidiMessagingProtocol<T>) protocol;
        this.reactor = reactor;
        this.connectionId = connectionId;
        this.connections = connections;
    }

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer();

        boolean success = false;
        try {
            success = chan.read(buf) != -1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (success) {
            buf.flip();
            if(!isStarted)
            {
            	protocol.start(connectionId, (Connections<T>) connections);
            	isStarted = true;
            }
            return () -> {
                try {
                    while (buf.hasRemaining() && isConnected) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) 
                        	//the process build the corresponding answer, and sent it through the connection
                        	//(call the send method of the handler with the response)
                            protocol.process(nextMessage);
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };
        } else {
            releaseBuffer(buf);
            close();
            return null;
        }

    }

    public void close() {
        try {
        	isConnected = false;
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    public void continueWrite() {
        while (!writeQueue.isEmpty()) {
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top);
                if (top.hasRemaining()) {
                    return;
                } else {
                    writeQueue.remove();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            if (protocol.shouldTerminate()) close();
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }

    @Override
	public void send(T msg) {
		
		 if (msg != null){
			 writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));//add msg to be written to the current client
	         reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// the client must read the message after it will be written, and then write a response
	     }		
	}
}