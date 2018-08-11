package bgu.spl181.net.impl.BBtpc;
import java.io.BufferedReader;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl181.net.impl.database.MoviesList;
import bgu.spl181.net.impl.database.UsersList;
import bgu.spl181.net.impl.impl_interfaces.MovieRentalServiceEncDec;
import bgu.spl181.net.impl.impl_interfaces.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Server;

public class TPCMain {
	static UsersList users;
	static MoviesList movies;

	public static void main(String[] args) {		
		
		BufferedReader reader = null;
		Gson gson = new GsonBuilder().create();
		
		//try to read the users json file
		try
		{
			reader = new BufferedReader(new FileReader("Database/Users.json"));
	    	users = gson.fromJson(reader, UsersList.class);
		}catch(Exception e){
			System.out.println(e + " we get exception in trying to building json");
		}
	
		//try to read the movies json file
		try
		{
			reader = new BufferedReader(new FileReader("Database/Movies.json"));
			movies = gson.fromJson(reader, MoviesList.class);
		}catch(Exception e){
			System.out.println(e + " we get exception in trying to building json");
		}
		movies.calculateMaxID();
		
		int port = Integer.parseInt(args[0]);
		
		Server.threadPerClient(
				port,
				()-> new MovieRentalServiceProtocol(users, movies),
				()-> new MovieRentalServiceEncDec()).serve();
	}

}
