package com.flickfinder.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flickfinder.dao.MovieDAO;
import com.flickfinder.model.Movie;
import com.flickfinder.model.MovieRating;
import com.flickfinder.model.Person;

import io.javalin.http.Context;

/**
 * The controller for the movie endpoints.
 * 
 * The controller acts as an intermediary between the HTTP routes and the DAO.
 * 
 * As you can see each method in the controller class is responsible for
 * handling a specific HTTP request.
 * 
 * Methods a Javalin Context object as a parameter and uses it to send a
 * response back to the client.
 * We also handle business logic in the controller, such as validating input and
 * handling errors.
 *
 * Notice that the methods don't return anything. Instead, they use the Javalin
 * Context object to send a response back to the client.
 */

public class MovieController {

	/**
	 * The movie data access object.
	 */

	private final MovieDAO movieDAO;

	/**
	 * Constructs a MovieController object and initializes the movieDAO.
	 */
	public MovieController(MovieDAO movieDAO) {
		this.movieDAO = movieDAO;
	}

	/**
	 * Returns a list of all movies in the database.
	 * 
	 * @param ctx the Javalin context
	 */
	public void getAllMovies(Context ctx) {
		try {
			String limitParam = ctx.queryParam("limit");
			int limit = 50;
			if (limitParam != null) {
				try {
					limit = Integer.parseInt(limitParam);
					if (limit < 1) {
						throw new NumberFormatException("Limit must be an integer greater than 0");
					}
				} catch (NumberFormatException e){
					ctx.status(400).result("Invalid limit");
					return;
				}
			}
			List <Movie> movies = movieDAO.getMoviesByLimit(limit);
			ctx.json(movies);
		} catch (SQLException e) {
			ctx.status(500);
			ctx.result("Database error");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the movie with the specified id.
	 * 
	 * @param ctx the Javalin context
	 */
	public void getMovieById(Context ctx) {

		int id = Integer.parseInt(ctx.pathParam("id"));
		try {
			Movie movie = movieDAO.getMovieById(id);
			if (movie == null) {
				ctx.status(404);
				ctx.result("Movie not found");
				return;
			}
			ctx.json(movieDAO.getMovieById(id));
		} catch (SQLException e) {
			ctx.status(500);
			ctx.result("Database error");
			e.printStackTrace();
		}
	}
	public void getPeopleByMovieId(Context ctx) {
		
		try {
			int movieId = Integer.parseInt(ctx.pathParam("id"));
			List<Person> stars = movieDAO.getPeopleByMovieId(movieId);
			
			if (stars.isEmpty() | stars == null) {
				ctx.status(404).result("No stars found for this Movie");
				return;
			}
			
			ctx.json(stars);
		} catch (SQLException e) {
			ctx.status(500).result("Database error : " + e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			ctx.status(400).result("Invalid or missing movie ID");
		}
	}
	public void getRatingsByYear(Context ctx) {
		try {
			String yearS = ctx.pathParam("year");
			int year = Integer.parseInt(yearS);
			
			String limitS = ctx.queryParam("limit");
			int limit = (limitS == null) ? 50 : Integer.parseInt(limitS);
			if (limit < 1) {
				ctx.status(400).result("Limit must be an integer greater than 0");
				return;
				}
			String minVotesS = ctx.queryParam("votes");
			int minVotes = (minVotesS == null)? 1000 : Integer.parseInt(minVotesS);
			if (minVotes < 1) {
				ctx.status(400).result("Vote must be an integer greater than 0");
				return;
				}
			List<MovieRating> movies = movieDAO.getRatingsByYear(year, limit, minVotes);
			
			ctx.json(movies);
			} catch (NumberFormatException e){
			ctx.status(400).result("Invalid year, limit or minVotes");
			} catch (SQLException e) {
			ctx.status(500).result("Database error");
			e.printStackTrace();
		}
		
	}
}
