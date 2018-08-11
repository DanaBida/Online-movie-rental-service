package bgu.spl181.net.impl.database;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class Movie {
	@Expose Integer id;
	@Expose String name;
	@Expose Integer price;
	@Expose ArrayList<String> bannedCountries;
	@Expose String availableAmount;
	@Expose String totalAmount;
	//constructor
	public Movie(Integer id, String name, Integer price, ArrayList<String> bannedCountries, String availableAmount,
			String totalAmount) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.bannedCountries = bannedCountries;
		this.availableAmount = availableAmount;
		this.totalAmount = totalAmount;
	}
	//copy constructor
	/*
	public Movie(Movie m) {
		this.id = m.id;
		this.name = m.name;
		this.price = m.price;
		this.bannedCountries = new ArrayList<String>();
		for(int i=0; i<bannedCountries.size(); i++)
			this.bannedCountries.set(i, m.bannedCountries.get(i));
		this.availableAmount = m.availableAmount;
		this.totalAmount = m.totalAmount;
	}*/
	
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Integer getPrice() {
		return price;
	}
	
	public void setPrice(Integer price) {
		this.price = price;
	}
	
	public ArrayList<String> getBannedCountries() {
		return bannedCountries;
	}
	
	public synchronized Integer getAvailableAmount() {
		return Integer.parseInt(availableAmount);
	}
	public Integer getTotalAmount() {
		return Integer.parseInt(totalAmount);
	}
	
	public boolean isBannedInCountry (String country){
		for (int i=0; i<bannedCountries.size(); i++)
			if (bannedCountries.get(i).equalsIgnoreCase(country))
				return true;
		return false;
	}
	public synchronized void setAvailableAmount(Integer availableAmount) {
		this.availableAmount = Integer.toString(availableAmount);
	}
	
	public String getBannedCountriesString(){
		//returns string of banned countries seperated by commas. if no banned countries, return empty string
		String ans = "";
		if (bannedCountries.size()>0){
			for (int i=0; i<bannedCountries.size(); i++){
				ans = ans + '"' + bannedCountries.get(i) + '"' + " ";
			}
			ans = ans.substring(0, ans.length()-1); //remove space after last country
		}
		return ans;
	}
	
	public boolean isMovieRented (String movieName){
	//returns true if somebody is now renting this movie. otherwise false
		return (Integer.parseInt(totalAmount)-Integer.parseInt(availableAmount)!=0);
	}
	
	public void addQuotationMarks(){
		name = '"' + name + '"';
		for (int i=0; i<bannedCountries.size(); i++){
			bannedCountries.set(i, '"' + bannedCountries.get(i) + '"');
		}
	}
}