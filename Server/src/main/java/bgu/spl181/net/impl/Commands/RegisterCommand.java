package bgu.spl181.net.impl.Commands;

public class RegisterCommand extends Command {
	String username;
	String password;
	String[] dataBlock;


public RegisterCommand(String command, String username, String password, String[] dataBlock) {
	super(command);	
	this.username = username;
	this.password = password;
	this.dataBlock = dataBlock;
}

public String getUsername() {
	return username;
}

public String getPassword() {
	return password;
}

public String[] getDataBlock() {
	return dataBlock;
}
}
