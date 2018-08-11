package bgu.spl181.net.impl.Commands;

public class BroadcastCommand extends Command {
	String BroadcastMessage;

public BroadcastCommand(String command, String BroadcastMessage)
{
	super(command);
	this.BroadcastMessage = BroadcastMessage;
}

public String getBroadcastMessage() {
	return BroadcastMessage;
}
}
