package bgu.spl181.net.impl.Commands;

public class ACKCommand extends Command {
	String message;

public ACKCommand(String command, String message)
{
	super(command);
	this.message = message;
}

public String getMessage() {
	return message;
}
}
