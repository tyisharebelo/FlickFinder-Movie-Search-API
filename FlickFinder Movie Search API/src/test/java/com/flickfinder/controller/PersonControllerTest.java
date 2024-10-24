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
import com.flickfinder.dao.PersonDAO;
import com.flickfinder.model.Movie;
import com.flickfinder.model.Person;

import io.javalin.http.Context;

/**
 * TODO: Implement this class
 */
class PersonControllerTest {
	private Context ctx;
	
	private PersonDAO personDAO;
	
	private PersonController personController;

	
	@BeforeEach
	void setUp() {
		personDAO = mock(PersonDAO.class);
		ctx = mock(Context.class);

		personController = new PersonController(personDAO);
		
	}
	
	@Test
	void testGetAllPeople() throws SQLException {
		personController.getAllPeople(ctx);
		verify(personDAO).getPeopleByLimit(50);
	}
	
	@Test
	void testGetAllPeopleByLimit() throws SQLException {
		when(ctx.queryParam("limit")).thenReturn("10");
		personController.getAllPeople(ctx);
		verify(personDAO).getPeopleByLimit(10);
	}

	@Test
	void testGetAllPeopleByInvalidLimit() throws SQLException {
		when(ctx.queryParam("limit")).thenReturn("-1");
		when(ctx.status(400)).thenReturn(ctx);
		when(ctx.result("Invalid limit parameter")).thenReturn(ctx);

		personController.getAllPeople(ctx);
		verify(ctx).status(400);
		verify(ctx).result("Invalid limit");
	}
	@Test
	void testThrows500ExceptionWhenGetAllDatabaseError() throws SQLException {
		when(personDAO.getPeopleByLimit(50)).thenThrow(new SQLException());
		personController.getAllPeople(ctx);
		verify(ctx).status(500);
	}
	@Test
	void testGetPersonById() {
		when(ctx.pathParam("id")).thenReturn("1");
		personController.getPersonById(ctx);
		try {
			verify(personDAO).getPersonById(1);
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	@Test
	void testThrows500ExceptionWhenGetByIdDatabaseError() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(personDAO.getPersonById(1)).thenThrow(new SQLException());
		personController.getPersonById(ctx);
		verify(ctx).status(500);
	}
	@Test
	void testThrows404ExceptionWhenNoPersonFound() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(personDAO.getPersonById(1)).thenReturn(null);
		personController.getPersonById(ctx);
		verify(ctx).status(404);
	}
	@Test
	void testGetMoviesStarringPerson() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		List<Movie> movies = new ArrayList<>();
		movies.add(new Movie(1, "The Shawshank Redemption", 1994));
		when(personDAO.getMoviesStarringPerson(1)).thenReturn(movies);
		personController.getMoviesStarringPerson(ctx);
		verify(ctx).json(movies);
		verify(personDAO).getMoviesStarringPerson(1);
	}
	@Test
	void testGetMoviesStarringPersonDatabaseError() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(ctx.status(500)).thenReturn(ctx);
		when(personDAO.getMoviesStarringPerson(1)).thenThrow(new SQLException("Database error"));
		personController.getMoviesStarringPerson(ctx);
		verify(ctx).status(500);
		verify(ctx).result("Database error : Database error");
	}
	@Test
	void testGetMoviesStarringPersonNoMoviesFound() throws SQLException {
		when(ctx.pathParam("id")).thenReturn("1");
		when(ctx.status(404)).thenReturn(ctx);
		when(personDAO.getMoviesStarringPerson(1)).thenReturn(new ArrayList<>());
		personController.getMoviesStarringPerson(ctx);
		verify(ctx).status(404);
		verify(ctx).result("No movie found for this Star");
	}
}
