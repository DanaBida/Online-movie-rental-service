package bgu.spl181.net.impl.Commands.RequestCommands;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class BalanceAddCommand extends RequestCommand{
	Integer amount;

	public BalanceAddCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		amount = Integer.parseInt(parameters[0]);
	}
	
	public int getAmount() {
		return amount;
	}
}
