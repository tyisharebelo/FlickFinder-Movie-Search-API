package com.flickfinder.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flickfinder.dao.MovieDAO;
import com.flickfinder.model.MovieRating;
import com.flickfinder.model.Person;

import io.javalin.http.Context;

/**
 * Test for the Movie Controller.
 */

class MovieControllerTest {

	/**
	 *
	 * The context object, later we will mock it.
	 */
	private Context ctx;

	/**
	 * The movie data access object.
	 */
	private MovieDAO movieDAO;

	/**
	 * The movie controller.
	 */

	private MovieController movieController;

	@BeforeEach
	void setUp() {
		// We create a mock of the MovieDAO class.
		movieDAO = mock(MovieDAO.class);
		// We create a mock of the Context class.
		ctx = mock(Context.class);

		// We create an instance of the MovieController class and pass the mock object
		movieController = new MovieController(movieDAO);
	}

	/**
	 * Tests the getAllMovies method.
	 * We expect to get a list of all movies in the database.
	 */

	@Test
	void testGetAllMovies() throws SQLException {
		movieController.getAllMovies(ctx);
		verify(movieDAO).getMoviesByLimit(50);
	}
	
	@Test
	void testGetAllMoviesByLimit() throws SQLException {
		when(ctx.queryParam("limit")).thenReturn("10");
		movieController.getAllMovies(ctx);
		verify(movieDAO).getMoviesByLimit(10);
	}

	@Test
	void testGetAllMoviesByInvalidLimit() throws SQLException {
		when(ctx.queryParam("limit")).thenReturn("-1");
		when(ctx.status(400)).thenReturn(ctx);
		when(ctx.result("Invalid limit")).thenReturn(ctx);

		movieController.getAllMovies(ctx);
		verify(ctx).status(400);
		verify(ctx).result("Invalid limit");
	}
	/**
	 * Test that the controller returns a 500 status code when a database error
	 * occurs
	 * 
	 * @throws SQLException
	 */
	@Test
	void testThrows500ExceptionWhenGetAllDatabaseError() throws SQLException {
		when(movieDAO.getMoviesByLimit(50)).thenThrow(new SQLException());
		movieController.getAllMovies(ctx);
		verify(ctx).status(500);
		verify(ctx).result("Database error");
	}

	/**
	 * Tests the getMovieById method.
	 * We expect to get the movie with the specified id.
	 */

	@Test
	void testGetMovieById() {
		when(ctx.pathParam("id")).thenReturn("1");
		movieController.getMovieById(ctx);
		try {
			verify(movieDAO).getMovieById(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test a 500 status code is returned when a database error occurs.
	 * 
	 * @throws SQLException
	 */

	@Test
	void testThrows500ExceptionWhenGetByIdDatabaseError() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(movieDAO.getMovieById(1)).thenThrow(new SQLException());
		movieController.getMovieById(ctx);
		verify(ctx).status(500);
	}

	/**
	 * Test that the controller returns a 404 status code when a movie is not found
	 * or
	 * database error.
	 * 
	 * @throws SQLException
	 */

	@Test
	void testThrows404ExceptionWhenNoMovieFound() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(movieDAO.getMovieById(1)).thenReturn(null);
		movieController.getMovieById(ctx);
		verify(ctx).status(404);
	}
	@Test
	void testGetPeopleByMovieId() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		
		List<Person> people = new ArrayList<>();
		people.add(new Person(1, "Tim Robbins", 1958));
		when(movieDAO.getPeopleByMovieId(1)).thenReturn(people);
		movieController.getPeopleByMovieId(ctx);
		verify(ctx).json(people);
	}
	@Test
	void testGetPeopleByMovieIdNoStarFound() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(ctx.status(404)).thenReturn(ctx);
		when(ctx.result("No stars found for this Movie")).thenReturn(ctx);
		when(movieDAO.getPeopleByMovieId(1)).thenReturn(new ArrayList<>());
		movieController.getPeopleByMovieId(ctx);
		verify(ctx).status(404);
		verify(ctx).result("No stars found for this Movie");
	}
	@Test
	void testGetPeopleByMovieIdInvalidId() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("invalid");
		when(ctx.status(400)).thenReturn(ctx);
		when(ctx.result("Invalid or missing movie ID")).thenReturn(ctx);
		movieController.getPeopleByMovieId(ctx);
		verify(ctx).status(400);
		verify(ctx).result("Invalid or missing movie ID");
	}
	@Test
	void testGetRatingsByYear() throws SQLException {
		when(ctx.pathParam("year")).thenReturn("1994");
		when(ctx.queryParam("limit")).thenReturn("10");
		when(ctx.queryParam("votes")).thenReturn("1000");
		List<MovieRating> movieRatings = new ArrayList<>();
		movieRatings.add(new MovieRating(1, "The Shawshank Redemption", 9.3f, 2200000, 1994));
		when (movieDAO.getRatingsByYear(1994, 10, 1000)).thenReturn(movieRatings);
		movieController.getRatingsByYear(ctx);
		verify(ctx).json(movieRatings);
		
	}
	@Test
	void testGetRatingsByYearHandlesSQLException() throws SQLException {
		when(ctx.pathParam("year")).thenReturn("1994");
		when(ctx.queryParam("limit")).thenReturn("10");
		when(ctx.queryParam("votes")).thenReturn("1000");
		when (movieDAO.getRatingsByYear(1994, 10, 1000)).thenThrow(new SQLException());
		when(ctx.status(500)).thenReturn(ctx);
		when(ctx.result("Database error")).thenReturn(ctx);
		movieController.getRatingsByYear(ctx);
		verify(ctx).status(500);
		verify(ctx).result("Database error");
		
	}
	@Test
	void testGetRatingsByYearWithInvalidYearLimitorMinVotes() throws SQLException {
		when(ctx.pathParam("year")).thenReturn("invalid");
		when(ctx.queryParam("limit")).thenReturn("-1");
		when(ctx.queryParam("votes")).thenReturn("999");
		when(ctx.status(400)).thenReturn(ctx);
		when(ctx.result("Invalid year, limit or minVotes")).thenReturn(ctx);
		movieController.getRatingsByYear(ctx);
		verify(ctx).status(400);
		verify(ctx).result("Invalid year, limit or minVotes");
	}
}
