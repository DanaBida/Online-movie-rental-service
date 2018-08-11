package bgu.spl181.net.impl.Commands;

public class RequestCommand extends Command {
	String name;
	String[] parameters;


public RequestCommand(String command, String name, String[] parameters) {
	super(command);	
	this.name = name;
	this.parameters = parameters;
}

public String getname() {
	return name;
}

public String[] getparameters() {
	return parameters;
}
}
