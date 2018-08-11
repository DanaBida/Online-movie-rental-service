package bgu.spl181.net.impl.database;

import java.util.ArrayList;

public class UsersList {
	//Array object is ONLY used when reading Json. The actual work is done in HashMap.
	ArrayList<User> users;
	
	public UsersList() {
		users = new ArrayList<User>();
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}

	//return true if the user u exists in the system, else - return false.
	public boolean isExist(String u){
		for(int i=0 ; i<users.size() ; i++)
			if(users.get(i).getUsername().equals(u))
				return true;
		return false;
	}
	
	//return the user with the name userName
	public User get (String userName){
		for(int i=0 ; i<users.size() ; i++)
			if(users.get(i).username.equals(userName))
				return users.get(i);
		return null;
	}
	
}
