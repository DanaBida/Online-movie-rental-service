/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.api.bidi;

/**
 *
 * @author bennyl
 */
public interface BidiMessagingProtocol<T>  {

	/*
	 *  initiate the protocol with the active connections structure of the server,
	 *   and saves the owner client’s connection id. 
	 */
    void start(int connectionId, Connections<T> connections);
    
    /*
     * process the message and send response via the connections object send function
     */
    void process(T message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
