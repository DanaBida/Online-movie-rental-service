package bgu.spl181.net.impl.Commands.RequestCommands;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class RemMovieCommand extends RequestCommand{
	String movieName;

	public RemMovieCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		movieName = parameters[0];
	}

	public String getMovieName() {
		return movieName;
	}
	
}

