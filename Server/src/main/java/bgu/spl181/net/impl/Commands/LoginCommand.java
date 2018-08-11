package bgu.spl181.net.impl.Commands;

public class LoginCommand extends Command {
	String username;
	String password;


public LoginCommand(String command, String username, String password) {
	super(command);	
	this.username = username;
	this.password = password;
}

public String getUsername() {
	return username;
}

public String getPassword() {
	return password;
}

}
