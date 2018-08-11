package bgu.spl181.net.impl.database;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class MoviesList {
	//Array object is ONLY used when reading Json. The actual work is done in HashMap.
	@Expose ArrayList<Movie> movies;
	int maxId; //saves the current maximum ID in DB
	
	public MoviesList() {
		movies = new ArrayList<Movie>();
	}

	public Integer getMaxId ()
	{
		return maxId;
	}
	
	public ArrayList<Movie> getMovies() {
		return movies;
	}
	
	//return true if the movie m exists in the system, else - return false.
	public boolean isExist(String m){
		for(int i=0 ; i<movies.size() ; i++)
			if(movies.get(i).getName().equalsIgnoreCase(m))
				return true;
		return false;
	}
	
	//return the movie with the name movieName
	public Movie get (String movieName){
		for(int i=0 ; i<movies.size() ; i++)
			if(movies.get(i).getName().equalsIgnoreCase(movieName))
				return movies.get(i);
		return null;
	}
	
	//return the movie with the name movieName
	public void removeMovie (String movieName){
		for(int i=0 ; i<movies.size() ; i++)
			if(movies.get(i).getName().equalsIgnoreCase(movieName))
				movies.remove(i);
	}
	
	public void calculateMaxID (){
	//this fucntion recalcaulates the max ID in DB. 
	//It's called only when we removed the movie with maximum ID
		maxId = 0;
		for(int i=0 ; i<movies.size() ; i++)
			if(movies.get(i).getId()>maxId)
				maxId=movies.get(i).getId();
	}
}


