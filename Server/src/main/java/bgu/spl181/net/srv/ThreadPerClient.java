package bgu.spl181.net.srv;
import java.util.function.Supplier;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;

/*
 * this class extends BaseServer.
 * it implements execute in the following way:
 * it creates new thread for the handler of the client that connected to the server.
 * when the thread start running, it will execute the run method of the handler;
 * in run, the client keep sending messages non-stop and the handler processing them by the protocol.
 * this process terminates only by close, force close(return -1) or terminate.
 */
public class ThreadPerClient<T> extends BaseServer<T> 
{
	public ThreadPerClient( int port, Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) 
	{
		//creates new BaseServer from the threadPerClient type
        super(port, protocolFactory, encoderDecoderFactory);
    }
    
	@Override
	protected void execute(BlockingConnectionHandler<T> handler)
	{
		//creates a new thread to execute the run method of a specific handler of some client
		new Thread(handler).start();
	}
	
}
