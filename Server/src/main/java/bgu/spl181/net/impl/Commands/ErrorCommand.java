package bgu.spl181.net.impl.Commands;

public class ErrorCommand extends Command {
	String errorMessage;

public ErrorCommand(String command, String errorMessage)
{
	super(command);
	this.errorMessage = errorMessage;
}

public String geterrorMessage() {
	return errorMessage;
}
}
