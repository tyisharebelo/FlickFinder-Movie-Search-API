package com.flickfinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.flickfinder.controller.PersonController;
import com.flickfinder.model.Movie;
import com.flickfinder.model.MovieRating;
import com.flickfinder.model.Person;
import com.flickfinder.util.Database;

/**
 * The Data Access Object for the Movie table.
 * 
 * This class is responsible for getting data from the Movies table in the
 * database.
 * 
 */
public class MovieDAO {

	/**
	 * The connection to the database.
	 */
	private final Connection connection;

	/**
	 * Constructs a SQLiteMovieDAO object and gets the database connection.
	 * 
	 */
	public MovieDAO() {
		Database database = Database.getInstance();
		connection = database.getConnection();
	}

	/**
	 * Returns a list of all movies in the database.
	 * 
	 * @return a list of all movies in the database
	 * @throws SQLException if a database error occurs
	 */

	public List<Movie> getAllMovies() throws SQLException {
		return getMoviesByLimit(50);
	}
	
	public List<Movie> getMoviesByLimit(int limit) throws SQLException{
		List<Movie> movies = new ArrayList<>();
		String query = "SELECT * FROM movies LIMIT ?";
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, limit);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				movies.add(new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year")));
			}
		}
		return movies;
	}

	/**
	 * Returns the movie with the specified id.
	 * 
	 * @param id the id of the movie
	 * @return the movie with the specified id
	 * @throws SQLException if a database error occurs
	 */
	public Movie getMovieById(int id) throws SQLException {

		String statement = "select * from movies where id = ?";
		PreparedStatement ps = connection.prepareStatement(statement);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {

			return new Movie(rs.getInt("id"), rs.getString("title"), rs.getInt("year"));
		}
		
		// return null if the id does not return a movie.

		return null;

	}
	public List<Person> getPeopleByMovieId(int movieId) throws SQLException{
		String query = "SELECT p.id, p.name, p.birth FROM Stars s" + 
				" JOIN People p ON s.person_id = p.id " +  
				" WHERE s.movie_id = ?";
		List<Person>stars = new ArrayList<>();
		
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, movieId);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				int birth = resultSet.getInt("birth");
				stars.add(new Person(id, name, birth));
			}
		}
		return stars;
				
	}
	public List<MovieRating> getRatingsByYear(int year, int limit, int minVotes) throws SQLException {
		String query = "SELECT m.id, m.title, r.rating, r.votes,  m.year " +
				"FROM movies m " +
				"JOIN ratings r ON m.id = r.movie_id " +
				"WHERE m.year = ? AND r.votes > ? " +
				"ORDER BY r.rating DESC " +
				"LIMIT ?";
		
		List<MovieRating> movieRatings = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, year);
			statement.setInt(2, minVotes);
			statement.setInt(3, limit);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				float rating = resultSet.getFloat("rating");
				int votes = resultSet.getInt("votes");
				int movieYear = resultSet.getInt("year");
				
				movieRatings.add(new MovieRating(id, title, rating, votes, movieYear));
			}
		}
		return movieRatings;
	}

}
