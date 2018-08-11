package bgu.spl181.net.impl.impl_interfaces;

import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.Commands.ACKCommand;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.impl.Commands.ErrorCommand;
import bgu.spl181.net.impl.Commands.LoginCommand;
import bgu.spl181.net.impl.Commands.RegisterCommand;
import bgu.spl181.net.impl.Commands.RequestCommand;
import bgu.spl181.net.impl.Commands.SignoutCommand;
import bgu.spl181.net.impl.database.MoviesList;
import bgu.spl181.net.impl.database.User;
import bgu.spl181.net.impl.database.UsersList;

public abstract class UserServiceTextBasedProtocol implements BidiMessagingProtocol<Command> {
	
	impl_Connections<Command> connections;
	int clientid;//the connected user
	String current_user; //will be filled once user logged in
	boolean shouldterminate = false;
	UsersList users;
	MoviesList movies;

	public UserServiceTextBasedProtocol(UsersList users, MoviesList movies) {	
		this.users=users;
		this.movies=movies;
		current_user="";
	}
	
	@Override
	public void start(int connectionId, Connections<Command> connections) {
		clientid = connectionId;
		this.connections = (impl_Connections<Command>) connections;

	}

	@Override
	public void process(Command message)
	{
		String command = message.getCommand();
        switch (command) {
        case "REGISTER":{
        	Command ans = register((RegisterCommand)message);
        	connections.send(clientid, ans);
        	break;
        }
        case "LOGIN":{
        	LoginCommand l = (LoginCommand)message;
        	Command ans = login(l);
        	if (ans.getCommand().equals("ACK")) //login was successful
        		current_user = l.getUsername(); //save current user name
        	connections.send(clientid, ans);
        	break;
        }
        case "SIGNOUT":{
        	SignoutCommand c = (SignoutCommand)message;
        	Command ans = signout(c);
        	connections.send(clientid, ans);
        	if (ans.getCommand().equals("ACK")) //sign-out was successful
        	{
        		shouldterminate = true;
        	    connections.removeClient(clientid, current_user);
        	}
        	break;
        }
        /*
         * In case of a failure, an ERROR command will be sent by the server: ERROR request <name> failed 
         * Reasons for failure: 1. Client not logged in. 2. Error forced by service requirements (defined in rental service section). 
         *  In case of successful, request an ACK command will be send ( Specific ACK messages are listed on the service specifications)
         */
        case "REQUEST": {       	
    		if(!connections.loginUsers.containsValue(clientid))//in case Client not logged in
    			if(((RequestCommand)message).getname().startsWith("balance"))
    				connections.send(clientid, new ErrorCommand("ERROR", "request balance failed"));
    			else
    				connections.send(clientid, new ErrorCommand("ERROR", "request "  + ((RequestCommand)message).getname() + " failed"));
    		else //if the client is logged in 
    			executeSpecificRequest(message);
    		break;
    		}		
        }
	}
	
	/*
	 * In case of failure, 
	 * an ERROR command will be sent by the server: ERROR signout failed - Reasons for failure: 1. Client not logged in. 
	 * In case of successful,
	 *  sign out                 connections.addClient(connections.getAndIncConnectionId(), (ConnectionHandler<Command>) handler);
an ACK command will be sent: ACK signout succeeded. After a successful ACK for sign out the client should terminate! 
	 */
	public Command signout (SignoutCommand c) 
	{
		if(!connections.loginUsers.containsValue(clientid))//in case Client not logged in
			return new ErrorCommand("ERROR", "signout failed");
		return new ACKCommand("ACK", "signout succeeded");
	}

	
    public Command register (RegisterCommand c) {
    	//the generic part of register is implemented here.
    	//the dataBlock part (which is specific to the movies implementation) is implemented in the movie protocol.
    	//we have two non-specific functions - isDataBlockLegal and handleDataBlock.
    	//any specific implementation can make their specific dataBlock demands and handling there. 
    	
    	String username = c.getUsername();
    	if (!connections.loginUsers.containsKey(current_user) && //user isn't already online
    		!users.isExist(username) && //user doesn't exist
    		!(username==null) && 
    		!(c.getPassword() == null) &&
    		(isDataBlockLegal(c.getDataBlock()))) {
    			//create new user
    			User u = new User(username, c.getPassword(), null);
    			handleDataBlock(c.getDataBlock(), u);
    			//add to db
    			users.getUsers().add(u);  			
    			updateUsersJson();		
    			return new ACKCommand("ACK", "registration succeeded");
    	}
    	else
    		return new ErrorCommand("ERROR", "registration failed");
    }
    
    protected abstract boolean isDataBlockLegal (String[] dataBlock);
    
    protected abstract void handleDataBlock (String[] dataBlock, User u);

    public Command login (LoginCommand c) {
    	String username = c.getUsername();
    	if (!connections.loginUsers.containsKey(username) && //user isn't already online
    		users.isExist(username) && //username exist
    		users.get(username).getPassword().equals(c.getPassword())) //password is right
			{
    			User u = users.get(username);
    			connections.logClient(u.getUsername(), clientid);
    			return new ACKCommand("ACK", "login succeeded");
    	}
   		return new ErrorCommand("ERROR", "login failed");
    }
	
	public synchronized void updateUsersJson(){
		//writes current DB state into users Json - it is synchronized to prevent reading from json while changing it
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonInString = gson.toJson(users);		
		try {
			   FileWriter writer = new FileWriter("Database/Users.json");
			   writer.write(jsonInString);
			   writer.close();
			  
			  } catch (IOException e) {
			   e.printStackTrace();
			  }
	}
    
    
	@Override
	public boolean shouldTerminate() {
		return shouldterminate;
	}
	
	protected abstract void executeSpecificRequest(Command message);

	
}
