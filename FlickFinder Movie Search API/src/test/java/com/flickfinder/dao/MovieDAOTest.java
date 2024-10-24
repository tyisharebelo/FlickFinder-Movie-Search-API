package com.flickfinder.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flickfinder.model.Movie;
import com.flickfinder.model.MovieRating;
import com.flickfinder.model.Person;
import com.flickfinder.util.Database;
import com.flickfinder.util.Seeder;

/**
 * Test for the Movie Data Access Object.
 * This uses an in-memory database for testing purposes.
 */

class MovieDAOTest {

	/**
	 * The movie data access object.
	 */

	private MovieDAO movieDAO;

	/**
	 * Seeder
	 */

	Seeder seeder;

	/**
	 * Sets up the database connection and creates the tables.
	 * We are using an in-memory database for testing purposes.
	 * This gets passed to the Database class to get a connection to the database.
	 * As it's a singleton class, the entire application will use the same
	 * connection.
	 */
	@BeforeEach
	void setUp() {
		var url = "jdbc:sqlite::memory:";
		seeder = new Seeder(url);
		Database.getInstance(seeder.getConnection());
		movieDAO = new MovieDAO();

	}

	/**
	 * Tests the getAllMovies method.
	 * We expect to get a list of all movies in the database.
	 * We have seeded the database with 5 movies, so we expect to get 5 movies back.
	 * At this point, we avoid checking the actual content of the list.
	 */
	@Test
	void testGetAllMovies() {
		try {
			List<Movie> movie = movieDAO.getAllMovies();
			assertEquals(5, movie.size(), "Expected 5 movies from the database");
			assertNotNull(movie, " Expected non-null movie");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}

	/**
	 * Tests the getMovieById method.
	 * We expect to get the movie with the specified id.
	 */
	@Test
	void testGetMovieById() {
		Movie movie;
		try {
			movie = movieDAO.getMovieById(1);
			assertEquals("The Shawshank Redemption", movie.getTitle());
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetMoviesByDefaultLimit() throws SQLException {
	try {
		List<Movie> movie = movieDAO.getMoviesByLimit(50);
		assertNotNull(movie, "Expected a not-null list");
		assertEquals(5, movie.size(), "Expected 5 movies from the database");
	} catch (SQLException e) {
		fail("SQLException thrown");
		e.printStackTrace();
	}
	}
	@Test
	void testGetMoviesBySpecificLimit() throws SQLException {
		try {
		List<Movie> movie = movieDAO.getMoviesByLimit(3);
		assertNotNull(movie, "Expected a not-null list");
		assertEquals(3, movie.size(), "Expected 3 movies from the database");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetMoviesByExcessiveLimit() throws SQLException {
		try {
		List<Movie> movie = movieDAO.getMoviesByLimit(1000);
		assertNotNull(movie, "Expected a not-null list");
		assertEquals(5, movie.size(), "Expected 5 movies from the database");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	/**
	 * Tests the getMovieById method with an invalid id. Null should be returned.
	 */
	@Test
	void testGetMovieByIdInvalidId() {
		// write an assertThrows for a SQLException

		try {
			Movie movie = movieDAO.getMovieById(1000);
			assertEquals(null, movie);
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetPeopleByMovieId() throws SQLException {
		try { 
		List<Person> people = movieDAO.getPeopleByMovieId(1);
		assertNotNull(people, "Expected not-null list of people");
		assertEquals(2, people.size(), "Expected 2 people starring in movie ID 1");
		Person person1 = people.get(0);
		assertEquals("Tim Robbins", person1.getName(),"Expected Tim Robbins as first star");
		Person person2 = people.get(1);
		assertEquals("Morgan Freeman", person2.getName(),"Expected Morgan Freeman as second star");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetPeopleByInvalidMovieId() throws SQLException {
		try { 
		List<Person> people = movieDAO.getPeopleByMovieId(76548939);
		assertNotNull(people, "Expected not-null result even with invalid movie ID");
		assertTrue(people.isEmpty(), "Expected empty list for invalid movie ID");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetRatingsByYear() throws SQLException {
		try {
		List<MovieRating> movie = movieDAO.getRatingsByYear(1994, 10, 1000);
		assertNotNull(movie, "Expected a list of non-null movies.");
		assertFalse(movie.isEmpty(), "Expected at least one movie in the list.");
		assertTrue(movie.get(0).getVotes()>1000, "Expected more than 1000 votes for the movies.");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetRatingsByYearDefualtLimit() throws SQLException {
	try {
		List<MovieRating> movie = movieDAO.getRatingsByYear(1994, 50, 1000);
		assertNotNull(movie, "Expected a not-null list");
		assertTrue(movie.size() <= 50, "Expected at most 50 movies in the List.");
	} catch (SQLException e) {
		fail("SQLException thrown");
		e.printStackTrace();
	}
	}
	@Test
	void testGetRatingsByYearBySpecificLimit() throws SQLException {
		try {
		List<MovieRating> movie = movieDAO.getRatingsByYear(1994, 1, 1000);
		assertNotNull(movie, "Expected a not-null list");
		assertEquals(1, movie.size(), "Expected 1 movie from the database");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetRatingsByYearExcessiveLimit() throws SQLException {
		try {
		List<MovieRating> movie = movieDAO.getRatingsByYear(1994, 1000, 1000);
		assertNotNull(movie, "Expected a not-null list");
		assertEquals(1, movie.size(), "Expected 1 movie from the database");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@AfterEach
	void tearDown() {
		seeder.closeConnection();
	}

}
