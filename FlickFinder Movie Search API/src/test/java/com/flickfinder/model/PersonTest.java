package com.flickfinder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * TODO: Implement this class
 * 
 */

class PersonTest {

	private Person person;
	
	@BeforeEach
	public void setUp() {
		person = new Person(1, "Fred Astaire", 1899);
	}
	@Test
	public void testPersonCreated() {
		assertEquals(1, person.getId());
		assertEquals("Fred Astaire", person.getName());
		assertEquals(1899, person.getBirth());
	}
	@Test
	public void testPersonSetters() {
		person.setId(2);
		person.setName("Lauren Bacall");
		person.setBirth(1924);
		assertEquals(2, person.getId());
		assertEquals("Lauren Bacall", person.getName());
		assertEquals(1924, person.getBirth());
	}
}
