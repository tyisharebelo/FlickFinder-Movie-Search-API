package com.flickfinder.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flickfinder.model.Movie;
import com.flickfinder.model.Person;
import com.flickfinder.util.Database;
import com.flickfinder.util.Seeder;

/**
 * TODO: Implement this class
 */
class PersonDAOTest {
	private PersonDAO personDAO;
	
	Seeder seeder;
	
	@BeforeEach
	void setUp() {
		var url = "jdbc:sqlite::memory:";
		seeder = new Seeder(url);
		Database.getInstance(seeder.getConnection());
		personDAO = new PersonDAO();
		
	}
	@Test
	void testGetAllPeople() {
		try {
			List<Person> people = personDAO.getAllPeople();
			assertEquals(5, people.size(), "Expected 5 movies from the database");
			assertNotNull(people, " Expected non-null movie");
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetPeopleByDefaultLimit() throws SQLException {
		List<Person> person = personDAO.getPeopleByLimit(50);
		assertNotNull(person, "Expected a not-null list");
		assertEquals(5, person.size(), "Expected 5 movies from the database");
	}
	@Test
	void testGetPeopleBySpecificLimit() throws SQLException {
		List<Person> person = personDAO.getPeopleByLimit(3);
		assertNotNull(person);
		assertEquals(3, person.size(), "Expected 3 movies from the database");
	}
	@Test
	void testGetPeopleByExcessiveLimit() throws SQLException {
		List<Person> person = personDAO.getPeopleByLimit(1000);
		assertNotNull(person, "Expected a not-null list");
		assertEquals(5, person.size(), "Expected 5 movies from the database");
	}
	
	@Test
	void testGetPersonById() {
		Person person;
		try {
			person = personDAO.getPersonById(1);
			assertEquals("Tim Robbins", person.getName());
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetPersonByIdInvalidId() {
		// write an assertThrows for a SQLException

		try {
			Person person = personDAO.getPersonById(1000);
			assertEquals(null, person);
		} catch (SQLException e) {
			fail("SQLException thrown");
			e.printStackTrace();
		}
	}
	@Test
	void testGetMoviesStarringPerson() {
		try {
			List<Movie> movies = personDAO.getMoviesStarringPerson(1);
			assertNotNull(movies,"Expected a non-null list of movies");
			assertEquals(1, movies.size(), " Expected 1 movie for person ID 1");
		Movie movie = movies.get(0);
		assertEquals("The Shawshank Redemption", movie.getTitle(), "Expected 'The Shawshank Redemption'");
		} catch (SQLException e){
			fail("SQLException thrown: " + e.getMessage());
		}
	}
	@Test
	void testGetMoviesStarringPersonInvalidPersonId() throws SQLException {
		List<Movie> movies = personDAO.getMoviesStarringPerson(746384543);
		assertNotNull(movies, "Expected not-null result even with invalid person ID");
		assertTrue(movies.isEmpty(), "Expected empty list for invalid person ID");
	}
	@AfterEach
	void tearDown() {
		seeder.closeConnection();
	}
}
