package bgu.spl181.net.impl.Commands.RequestCommands;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class ChangePriceCommand extends RequestCommand{
	Integer price;
	String movieName;

	public ChangePriceCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		movieName = parameters[0];
		price = Integer.parseInt(parameters[1]);
	}

	public Integer getPrice() {
		return price;
	}
	
	public String getMovieName() {
		return movieName;
	}

}

