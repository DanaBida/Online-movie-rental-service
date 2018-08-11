package bgu.spl181.net.impl.Commands.RequestCommands;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class RentMovieCommand extends RequestCommand{
	String movieName;

	public RentMovieCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		movieName = parameters[0];
	}

	public String getMovieName() {
		return movieName;
	}
}
