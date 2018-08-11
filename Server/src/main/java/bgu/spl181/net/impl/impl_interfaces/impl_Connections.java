package bgu.spl181.net.impl.impl_interfaces;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.srv.ConnectionHandler;

public class impl_Connections<T> implements Connections<Command>
{

	ConcurrentHashMap<Integer, ConnectionHandler<Command>> connectionsHandlers ;//the clients that connections to the network
	int connectionId;
	ConcurrentHashMap<String, Integer> loginUsers;//enable to send broadcast only to clients that login
	
	public impl_Connections(){
		connectionsHandlers = new ConcurrentHashMap<>();//the clients that connections to the network
		connectionId = 0;
		loginUsers = new ConcurrentHashMap<>();//enable to send broadcast only to clients that login

	}
	public boolean send(int connectionId, Command msg)
	{
		if(connectionsHandlers.containsKey(connectionId))
		{
			connectionsHandlers.get(connectionId).send(msg);
			return true;
		}
		return false;
	}
	
	public void broadcast(Command msg)
	{
		for (ConnectionHandler<Command> i : connectionsHandlers.values())
			i.send(msg);
	}

	public void disconnect(int connectionId)
	{
		connectionsHandlers.get(connectionId);
		connectionsHandlers.remove(connectionId);
/*		catch(IOException e){//error in the network connection - the connection can't be close
			System.out.println("cannot close the connection");
		*/
	}
	
	//send msg to all the login clients
	public void broadcastLoginClients(Command msg)
	{
		for(int i : loginUsers.values())
			send(i, msg);
	}
	
	//register client - add client to the list of clients that connect to the network
	public void addClient(Integer id, ConnectionHandler<Command> handler)
	{
		connectionsHandlers.put(id, handler);
	}

	//login client - add client to the list of clients that login, 
	//and returns it's id if the added succeeded
	public void logClient(String name, Integer id)
	{
		loginUsers.put(name, id);
	}

	//sign out client - disconnect the client from the network and logout the client.
	public void removeClient(int connectionId, String name)
	{
		loginUsers.remove(name);
		disconnect(connectionId);
	}
	
	public ConcurrentHashMap<Integer, ConnectionHandler<Command>> getConnectionsHandlers() 
	{
		return connectionsHandlers;
	}

	public ConcurrentHashMap<String, Integer> getLoginUsers() 
	{
		return loginUsers;
	}
	
	public int getConnectionId() 
	{
		return connectionId;
	}

	//returns the current connectionId, and then increment it by 1
	public int getAndIncConnectionId()
	{
		return connectionId++;
	}

}
