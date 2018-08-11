package bgu.spl181.net.impl.impl_interfaces;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.impl.Commands.ACKCommand;
import bgu.spl181.net.impl.Commands.BroadcastCommand;
import bgu.spl181.net.impl.Commands.ErrorCommand;
import bgu.spl181.net.impl.Commands.LoginCommand;
import bgu.spl181.net.impl.Commands.RegisterCommand;
import bgu.spl181.net.impl.Commands.RequestCommand;
import bgu.spl181.net.impl.Commands.SignoutCommand;

public abstract class UserServiceTextBasedEncDec implements MessageEncoderDecoder <Command> {

    protected byte[] bytes = new byte[1 << 10]; //start with 1k
    protected int len = 0;
    
	@Override
	public Command decodeNextByte(byte nextByte) {

        if (nextByte == '\n')//if we finish reading the line
        {
            String message = popString();
            String[] messageSplit = message.split(" ");//creates array with the words of the string that between the spaces
            String command = messageSplit[0];
            switch (command) {
            
            case "REQUEST":{
            	String msg = message.substring(8);//removes the name of the command from the message	
               	return getSpecificReguest(msg);
            }
               	
            case "REGISTER":{
            	String username = null;
            	String password = null;
            	if (messageSplit.length>2){
	            	username = messageSplit[1];
	            	password = messageSplit[2];
            	}
            	String[] dataBlock = new String[1];
            	if (messageSplit.length>3) {
                	String data = message.substring(message.lastIndexOf(password)+password.length()+1);
                	dataBlock[0] = data;
            	}
               	return new RegisterCommand(command, username, password, dataBlock);
            }
            
            case "LOGIN":{
            	String username = messageSplit[1];
            	String password = messageSplit[2];
            	return new LoginCommand(command, username, password);
            }
            
            case "SIGNOUT":{
            	return new SignoutCommand(command);           
            }
            }

        }
        //if we still not finish to read the line - keep reading
        pushByte(nextByte);
        return null; //not a line yet
	}
	
	 protected void pushByte(byte nextByte) {
	        if (len >= bytes.length) {
	            bytes = Arrays.copyOf(bytes, len * 2);
	        }

	        bytes[len++] = nextByte;
	}
	
    protected String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

	@Override
	public byte[] encode(Command message) {
		
		String command = message.getCommand();
			
        switch (command) {
        case "ERROR":{
        	ErrorCommand e = (ErrorCommand)message;
        	String ans = "ERROR " + e.geterrorMessage();
        	return (ans + "\n").getBytes();
        }
        
        case "ACK":{
        	ACKCommand e = (ACKCommand)message;
        	String ans = "ACK " + e.getMessage();
        	return (ans + "\n").getBytes(); 
        }     
        
	    case "BROADCAST":{
	    	BroadcastCommand e = (BroadcastCommand)message;
	    	String ans = "BROADCAST " + e.getBroadcastMessage();
	    	return (ans + "\n").getBytes();
	    }     
	    }		
        
		return null;
	}

	//returns the specific request that the client ask for
	protected abstract RequestCommand getSpecificReguest(String message);

}
