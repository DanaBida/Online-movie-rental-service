package bgu.spl181.net.impl.impl_interfaces;

import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import bgu.spl181.net.impl.Commands.ACKCommand;
import bgu.spl181.net.impl.Commands.BroadcastCommand;
import bgu.spl181.net.impl.Commands.Command;
import bgu.spl181.net.impl.Commands.ErrorCommand;
import bgu.spl181.net.impl.Commands.RequestCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.AddMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.BalanceAddCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.BalanceInfoCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.ChangePriceCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.InfoCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.RemMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.RentMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.ReturnMovieCommand;
import bgu.spl181.net.impl.database.Movie;
import bgu.spl181.net.impl.database.MoviesList;
import bgu.spl181.net.impl.database.User;
import bgu.spl181.net.impl.database.UsersList;

public class MovieRentalServiceProtocol extends UserServiceTextBasedProtocol {
	
	public MovieRentalServiceProtocol(UsersList users, MoviesList movies) {
		super(users, movies);
		
	}
	protected void executeSpecificRequest(Command message) {
    	RequestCommand command = (RequestCommand)message;
    	Command ans = null;
		switch (command.getname())
   		{
   			case "balance info" :
   			{
   				BalanceInfoCommand c = (BalanceInfoCommand)message;
    			ans = balance_info(c);
        		connections.send(clientid, ans);
   	    		break;
   			}
    			
    			case "balance add" :
    			{
    				BalanceAddCommand c = (BalanceAddCommand)message;
    				ans = balance_add(c);
    	    		connections.send(clientid, ans);
    	    		break;
    			}

    			case "changeprice" :
    			{
    				ChangePriceCommand c = (ChangePriceCommand)message;
    				ans = change_price(c);
    	    		connections.send(clientid, ans);
    	    		if (ans.getCommand().equals("ACK")){
        	    		String movieName = c.getMovieName();
        	    		Integer price = c.getPrice();
        	    		Movie m = movies.get(movieName);
        	    		connections.broadcastLoginClients(new BroadcastCommand("BROADCAST", "movie " + '"' +movieName + '"' + " " + m.getAvailableAmount() + " " + price));//send a broadcast to logged-in users
    	    		}
        	    	break;
    			}
    			
    			case "addmovie" :
    			{
    				AddMovieCommand c = (AddMovieCommand)message;
    				ans = add_movie(c);
    	    		connections.send(clientid, ans);
    	    		if (ans.getCommand().equals("ACK")){
        	    		Integer copyLeft = c.getAmount();
        	    		Integer price = c.getPrice();
        	    		String movieName = c.getMovieName();
        	    		connections.broadcastLoginClients(new BroadcastCommand("BROADCAST", "movie " + '"' + movieName + '"' + " " + copyLeft + " " + price));//send a broadcast to logged-in users
    	    		}
    	    		break;
    			}

    			case "rent" :
    			{
    				RentMovieCommand c = (RentMovieCommand)message;
    				ans = rentMovie(c);
    	    		connections.send(clientid, ans);
    	    		if (ans.getCommand().equals("ACK")){
        	    		String movieName = c.getMovieName();
        	    		Movie m = movies.get(movieName);
        	    		Integer copyLeft = m.getAvailableAmount();
        	    		Integer price = m.getPrice();
        	    		connections.broadcastLoginClients(new BroadcastCommand("BROADCAST", "movie " + '"' + movieName + '"' + " " + copyLeft + " " + price));//send a broadcast to logged-in users
    	    		}
        	    	break;
    			}
    			
    			case "info" :
    			{
    				InfoCommand c = (InfoCommand)message;
    				ans = requestInfo(c);
    	    		connections.send(clientid, ans);
    	    		break;
    			}
    			
    			case "return" :
    			{
    				ReturnMovieCommand c = (ReturnMovieCommand)message;
    				ans = returnMovie(c);
    	    		connections.send(clientid, ans);
    	    		if (ans.getCommand().equals("ACK")){
    	    			String movieName = c.getMovieName();
    	    			Movie m = movies.get(movieName);
    	    			connections.broadcastLoginClients(new BroadcastCommand("BROADCAST", "movie " +  '"' + movieName + '"' + " " + m.getAvailableAmount() + " " + m.getPrice()));//send a broadcast to logged-in users
    	    		}
    	    		break;
    			}
    			
    			case "remmovie" :
    			{
    				RemMovieCommand c = (RemMovieCommand)message;
    				ans = removeMovie(c);
    	    		connections.send(clientid, ans);
    	    		if (ans.getCommand().equals("ACK")){
    	    			String movieName = c.getMovieName();
    	    			connections.broadcastLoginClients(new BroadcastCommand("BROADCAST", "movie " +  '"' + movieName + '"' + " " + "removed"));//send a broadcast to logged-in users
    	    		}
    	    		break;
    			}
   		}
	}
    
		public Command balance_info (BalanceInfoCommand c) 
		{
			return new ACKCommand("ACK", "balance " + users.get(current_user).getBalance());
		}
		/*
		 * Server adds the amount given to the user’s balance.
		 *  The server will return an ACK message: ACK balance <new balance> added <amount> 
		 *  Note: the new balance should be calculated after the added amount. You may assume amount is always a number greater than zero. 
		 */
		public Command balance_add (BalanceAddCommand c) 
		{
			Integer userBalance = users.get(current_user).getBalance();
			users.get(current_user).setBalance(c.getAmount()+userBalance);
			updateUsersJson();
			return new ACKCommand("ACK", "balance " + users.get(current_user).getBalance() + " added " + c.getAmount());
		}
		
		/*
		 * REQUEST changeprice <”movie name”> <price> 
		 * Server changes the price of a movie by the given name. If the request fails an ERROR message is sent. 
		 * Reason to failure: 
		 * 1. User is not an administrator 2. Movie does not exist in the system 3. Price is smaller than or equal to 0 
		 * If the request is successful, 
		 * the admin performing the request will receive an ACK command: ACK changeprice <”movie name”> success.
		 *  The server will also send a broadcast to all logged-in clients: BROADCAST movie <”movie name”> <No. copies left> <price> 
		 */
		public Command change_price (ChangePriceCommand c) 
		{
			User user = users.get(current_user);
			String movie_name = ((ChangePriceCommand) c).getMovieName();
			Integer price = ((ChangePriceCommand) c).getPrice();
			//if the request fails
			if(!user.getType().equals("admin") || !movies.isExist(movie_name) || price<=0)
				return new ErrorCommand("ERROR" , "request changeprice failed");
			//else - commit it and sent an ACK + BROADCAST Command
			Movie movie = movies.get(movie_name);
			movie.setPrice(price);
			updateMoviesJson();
			return new ACKCommand("ACK", "changeprice " + '"' + movie_name + '"' + " success");
		}
		
		/*
		 * REQUEST addmovie <”movie name”> <amount> <price> [“banned country”,…] 
		 *  The server adds a new movie to the system with the given information.
		 *  The new movie ID will be the highest ID in the system + 1. If the request fails an ERROR message is sent. 
		 *  Reason to failure: 
		 *  1. User is not an administrator 2. Movie name already exists in the system  3. Price or Amount are smaller than or equal to 0 (there are no free movies) 
		 *  If the request is successful, 
		 *  the admin performing the request will receive an ACK command: ACK addmovie <”movie name”> success.
		 *  The server will also send a broadcast to all logged-in clients: BROADCAST movie <”movie name”> <No. copies left> <price> 
		 */
		public Command add_movie (AddMovieCommand c) 
		{
			User user = users.get(current_user);
			String movie_name = ((AddMovieCommand) c).getMovieName();
			Integer price = ((AddMovieCommand) c).getPrice();
			Integer amount = ((AddMovieCommand) c).getAmount();
			//if the request fails
			if(!user.getType().equals("admin") || movies.isExist(movie_name) || price <= 0 || amount <= 0)
				return new ErrorCommand("ERROR" , "request addmovie failed");
			//else - commit it and sent an ACK + BROADCAST Command
			movies.getMovies().add(new Movie(movies.getMaxId()+1, movie_name, price, c.getBannedCountries(), Integer.toString(amount), Integer.toString(amount)));
			updateMoviesJson();
			return new ACKCommand("ACK", "addmovie " + '"' + movie_name + '"' + " success");
		}


	    
	    public Command rentMovie (RentMovieCommand c) {
	    	String movieName = c.getMovieName();
	    	User u = users.get(current_user); //current user
	    	if (movies.isExist(movieName) && //movie exist in DB
	    		u.getBalance() > movies.get(movieName).getPrice() &&
	    		movies.get(movieName).getAvailableAmount() > 0 &&//copies are available
	    		!movies.get(movieName).isBannedInCountry(u.getCountry()) && 
	    		!u.isRentingMovie(movieName)){ //user isn't already renting movie
	    			u.getMovies().add(movies.get(movieName));
	    			u.setBalance(u.getBalance()-movies.get(movieName).getPrice());
	    			//reduce available amount by one
	    			movies.get(movieName).setAvailableAmount(movies.get(movieName).getAvailableAmount()-1);
	    			updateMoviesJson();
	    			updateUsersJson();
	    			String message = "rent " + '"' + movieName + '"' + " success";
	    			return new ACKCommand("ACK", message);
	    	}
	    	else
		   		return new ErrorCommand("ERROR", "request rent failed");
	    }
	    
	    public Command returnMovie (ReturnMovieCommand c) {
	    	String movieName = c.getMovieName();
	    	User u = users.get(current_user); //current user
	    	if (u.isRentingMovie(movieName) && movies.isExist(movieName)){
	    		u.returnMovie(movieName);
	    		int amountBefore = movies.get(movieName).getAvailableAmount();
	    		movies.get(movieName).setAvailableAmount(amountBefore+1);
				updateMoviesJson();
				updateUsersJson();
				return new ACKCommand("ACK", "return " + '"' + movieName + '"' + " success");
	    	}
	    	else
		   		return new ErrorCommand("ERROR", "request return failed");
	    }
	    
	    public Command removeMovie (RemMovieCommand c) {
	    	String movieName = c.getMovieName();
	    	User u = users.get(current_user); //current user
	    	if (u.getType().equals("admin") &&
	    		movies.isExist(movieName) &&
	    		!(movies.get(movieName).isMovieRented(movieName))){
	    			//before removing movie, we check if it's the movie with largest ID
	    			boolean isMaxId = (movies.getMaxId()==movies.get(movieName).getId());
	    			movies.removeMovie(movieName);
	    			if (isMaxId) //if we removed the maxID movie, we will recalculate maxID now.
	    				movies.calculateMaxID();
	    			updateMoviesJson();
	    			return new ACKCommand("ACK", "remmovie " + '"' + movieName + '"' + " success");
	    	}
	    	else
		   		return new ErrorCommand("ERROR", "request remmovie failed");
	    }
	    
	    public Command requestInfo (InfoCommand c) {
	    	String movieName = c.getMovieName();
	    	if (movieName==null){ //return info for all movies
	    		String movies = "info " + getMoviesListString();
				return new ACKCommand("ACK", movies);
				}    
	    	else
	    		if (movies.isExist(movieName)){ //movie exist in DB
	    			Movie m = movies.get(movieName);
	    			String message = "info " + '"' + m.getName() + '"' + " " + m.getAvailableAmount() + " " + m.getPrice();
	    			String bannedCountries = m.getBannedCountriesString();
	    			if (bannedCountries.length()>0)
	    				message = message + " " + bannedCountries;
	    			return new ACKCommand("ACK", message);
	    			}   
	    		else{ //movie doesn'movies.lengtht exist in DB
	    			String message = "request info failed";
	    	   		return new ErrorCommand("ERROR", message);
	    		}   	
	    	}
	    

		public String getMoviesListString(){
			String ans = "";
			if (movies.getMovies().size()>0){
				for (int i=0; i<movies.getMovies().size(); i++) {
					ans = ans + '"' + movies.getMovies().get(i).getName() + '"' + " ";
				}
				ans = ans.substring(0, ans.length()-1); //remove space after last movie
			}
			return ans;

		}
		
		protected boolean isDataBlockLegal (String[] dataBlock) {
	    //used in Register. check if received dataBlock is legal. 
	    //In our case, checks if it starts with country=
			if (dataBlock[0]==null)
				return true;
			return (dataBlock[0].startsWith("country="));
	    }
	    
	    protected void handleDataBlock (String[] dataBlock, User u) {
	    //used in Register. this lets the specific implementor use dataBlock if he wishes.
	    //in our case, we put the country in the user 'country'.
			String country = dataBlock[0];
			country = country.substring(9, country.length()-1);//removes the word country and the quotes
			u.setCountry(country);
	    }

		public synchronized void updateMoviesJson(){
			//writes current DB state into movies Json - it is synchronized to prevent reading from json while changing it
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			String jsonInString = gson.toJson(movies);		
			try {
				   FileWriter writer = new FileWriter("Database/Movies.json");
				   writer.write(jsonInString);
				   writer.close();
				  
				  } catch (IOException e) {
				   e.printStackTrace();
				  }
		}
}
