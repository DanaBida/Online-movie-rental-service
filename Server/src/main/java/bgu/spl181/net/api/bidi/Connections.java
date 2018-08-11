package bgu.spl181.net.api.bidi;

import java.io.IOException;

public interface Connections<T> {

	/*
	 *  try to send a message T to client represented by the given connId 
	 *  if succeeded - return true, else - returns false.
	 */
    boolean send(int connectionId, T msg);

	/*
	 *  sends a message T to all active clients. 
	 *  This includes clients that has not yet completed log-in by the User service text based protocol.  
	 */
    void broadcast(T msg);

	/*
	 * removes active client connId from map. 
	 */
    void disconnect(int connectionId);
}
