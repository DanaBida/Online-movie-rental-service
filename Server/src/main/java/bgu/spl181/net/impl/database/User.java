package bgu.spl181.net.impl.database;
import java.util.ArrayList;

public class User {
	String username;
	String password;
	String type;
	String country;
	ArrayList<Movie> movies;
	String balance;
	
	public User(String username, String password, String country) {
		this.username = username;
		this.password = password;
		this.country = country;
		balance = "0";
		movies = new ArrayList<Movie>();
		type = "normal";
	}


	public String getType() {
		return type;
	}

	public String getCountry() {
		return country;
	}

	public ArrayList<Movie> getMovies() {
		return movies;
	}

	public void setBalance(Integer balance) {
		this.balance = Integer.toString(balance);
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Integer getBalance() {
		return Integer.parseInt(balance);
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isRentingMovie (String movieName){
		for (int i=0; i<movies.size(); i++)
			if (movies.get(i).getName().equalsIgnoreCase(movieName))
				return true;
		return false;
	}
	
	//return the movie with the name movieName
	public void returnMovie (String movieName){
		for(int i=0 ; i<movies.size() ; i++)
			if(movies.get(i).getName().equalsIgnoreCase(movieName))
				movies.remove(i);
	}
	
}
