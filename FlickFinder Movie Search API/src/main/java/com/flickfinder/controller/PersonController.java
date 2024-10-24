package com.flickfinder.controller;

import java.sql.SQLException;
import java.util.List;

import com.flickfinder.dao.PersonDAO;
import com.flickfinder.model.Movie;
import com.flickfinder.model.Person;

import io.javalin.http.Context;

public class PersonController {

	private final PersonDAO personDAO;
	
	public PersonController(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	/**.
	 * 
	 * @param ctx the Javalin context
	 */
	public void getAllPeople(Context ctx) {
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
			List <Person> person = personDAO.getPeopleByLimit(limit);
			ctx.json(person);
		} catch (SQLException e) {
			ctx.status(500);
			ctx.result("Database error");
			e.printStackTrace();
		}
	}
	
	public void getPersonById(Context ctx) {

		int id = Integer.parseInt(ctx.pathParam("id"));
		try {
			Person person = personDAO.getPersonById(id);
			if (person == null) {
				ctx.status(404);
				ctx.result("Person not found");
				return;
			}
			ctx.json(personDAO.getPersonById(id));
		} catch (SQLException e) {
			ctx.status(500);
			ctx.result("Database error");
			e.printStackTrace();
			
		}
	}
	public void getMoviesStarringPerson(Context ctx) {
		
		try {
			int personId = Integer.parseInt(ctx.pathParam("id"));
			List<Movie> stars = personDAO.getMoviesStarringPerson(personId);
			
			if (stars.isEmpty() | stars == null) {
				ctx.status(404).result("No movie found for this Star");
				return;
			}
			
			ctx.json(stars);
		} catch (SQLException e) {
			ctx.status(500).result("Database error : " + e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			ctx.status(400).result("Invalid person ID");
		}
	}
	// to complete the must-have requirements you need to add the following methods:
	// getAllPeople
	// getPersonById
	// you will add further methods for the more advanced tasks; however, ensure your have completed 
	// the must have requirements before you start these.  
	
}
