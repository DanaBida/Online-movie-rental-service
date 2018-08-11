package bgu.spl181.net.impl.Commands.RequestCommands;

import java.util.ArrayList;

import bgu.spl181.net.impl.Commands.RequestCommand;

public class AddMovieCommand extends RequestCommand{
	String movieName;
	Integer amount;
	Integer price;
	ArrayList<String> bannedCountries = new ArrayList<>();

	public AddMovieCommand(String command, String name, String[] parameters) {
		super(command, name, parameters);
		movieName = parameters[0];
		amount = Integer.parseInt(parameters[1]);
		price = Integer.parseInt(parameters[2]);
		if (parameters[3]!=""){
			String [] arr = parameters[3].split(",");//creates array with the words of the string that between the commas
			for( int i=0; i< arr.length; i++)
				bannedCountries.add(arr[i]);
		}			    
	}
	
	public String getMovieName() {
		return movieName;
	}

	public Integer getPrice() {
		return price;
	}

	public ArrayList<String> getBannedCountries() {
		return bannedCountries;
	}

	public int getAmount() {
		return amount;
	}
}

