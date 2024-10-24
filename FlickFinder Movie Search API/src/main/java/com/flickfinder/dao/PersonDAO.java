package com.flickfinder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.flickfinder.model.Movie;
import com.flickfinder.model.Person;
import com.flickfinder.util.Database;

/**
 * TODO: Implement this class
 * 
 */
public class PersonDAO {
	
	private final Connection connection;
	
	public PersonDAO() {
		Database database = Database.getInstance();
		connection = database.getConnection();
	}
	
	public List<Person> getAllPeople() throws SQLException {
		return getPeopleByLimit(50);
	}
		public List<Person> getPeopleByLimit(int limit) throws SQLException{
			List<Person> person = new ArrayList<>();
			String query = "SELECT * FROM people LIMIT ?";
			try (PreparedStatement statement = connection.prepareStatement(query)){
				statement.setInt(1, limit);
				ResultSet rs = statement.executeQuery();
				while (rs.next()) {
					person.add(new Person(rs.getInt("id"), rs.getString("name"), rs.getInt("birth")));
				}
			}
			return person;
		}
	
	public Person getPersonById (int id) throws SQLException {

		String statement = "select * from people where id = ?";
		PreparedStatement ps = connection.prepareStatement(statement);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {

			return new Person(rs.getInt("id"), rs.getString("name"), rs.getInt("birth"));
		}
		
		// return null if the id does not return a movie.

		return null;
	}
	public List<Movie> getMoviesStarringPerson(int personId) throws SQLException{
		String query = "SELECT m.id, m.title, m.year FROM Movies m" + 
				" JOIN Stars s ON s.movie_id = m.id " +  
				" WHERE s.person_id = ?";
		List<Movie>stars = new ArrayList<>();
		
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, personId);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				int year = resultSet.getInt("year");
				stars.add(new Movie(id, title, year));
			}
		}
		return stars;

	// for the must have requirements, you will need to implement the following
	// methods:
	// - getAllPeople()
	// - getPersonById(int id)
	// you will add further methods for the more advanced tasks; however, ensure your have completed 
	// the must have requirements before you start these. 
	}
}
