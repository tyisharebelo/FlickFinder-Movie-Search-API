package com.flickfinder.model;

public class MovieRating extends Movie {
	private float rating;
	private int votes;
	
	public MovieRating(int id, String title, float rating, int votes, int year) {
		super(id, title, year);
		this.rating = rating;
		this.votes = votes;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
}
