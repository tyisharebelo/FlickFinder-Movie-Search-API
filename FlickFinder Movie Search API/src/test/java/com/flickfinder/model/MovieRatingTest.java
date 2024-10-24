package com.flickfinder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MovieRatingTest {
	
	@Test
	public void setUp() { 
		MovieRating movieRating = new MovieRating(1, "The Shawshank Redemption", 9.3f, 2200000, 1994);
		assertEquals(1, movieRating.getId(), "ID should be 1");
		assertEquals("The Shawshank Redemption", movieRating.getTitle(), "Title should be 'The Shawshank Redemption'");
		assertEquals(1994, movieRating.getYear(), "Year should be 1994");
		assertEquals(9.3f, movieRating.getRating(), 0.01, "Rating should be 9.3");
		assertEquals(2200000, movieRating.getVotes(), "Votes should be 2,200,000");
	}
	
	@Test
	public void testMovieRatingSetters() { 
		MovieRating movieRating = new MovieRating(1, "The Shawshank Redemption", 9.3f, 2200000, 1994);
		movieRating.setRating(7.2f);
		assertEquals(7.2f, movieRating.getRating(), 0.01, "Rating should be 7.2");
		movieRating.setVotes(20000);
		assertEquals(20000, movieRating.getVotes(), "Votes should be 20,000");

	}
	
	@Test
	public void testMovieExtend() { 
		MovieRating movieRating = new MovieRating(1, "The Shawshank Redemption", 9.3f, 2200000, 1994);
		movieRating.setTitle("New Title");
		assertEquals("New Title", movieRating.getTitle(), "Title should be 'New Title'");
		movieRating.setYear(2000);
		assertEquals(2000, movieRating.getYear(), "Year should be 2000");

	}
}
