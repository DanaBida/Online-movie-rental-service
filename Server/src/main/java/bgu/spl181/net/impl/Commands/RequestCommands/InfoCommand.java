package bgu.spl181.net.impl.Commands.RequestCommands;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class InfoCommand extends RequestCommand{
	String movieName;

	public InfoCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		if (parameters!=null)
			movieName = parameters[0];
		else
			movieName = null;
	}

	public String getMovieName() {
		return movieName;
	}
}
