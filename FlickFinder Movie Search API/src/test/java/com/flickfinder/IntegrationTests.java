package com.flickfinder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flickfinder.util.Database;
import com.flickfinder.util.Seeder;

import io.javalin.Javalin;

/**
 * These are our integration tests.
 * We are testing the application as a whole, including the database.
 */
class IntegrationTests {

	/**
	 * The Javalin app.*
	 */
	Javalin app;

	/**
	 * The seeder object.
	 */
	Seeder seeder;

	/**
	 * The port number. Try and use a different port number from your main
	 * application.
	 */
	int port = 6000;

	/**
	 * The base URL for our test application.
	 */
	String baseURL = "http://localhost:" + port;

	/**
	 * Bootstraps the application before each test.
	 */
	@BeforeEach
	void setUp() {
		var url = "jdbc:sqlite::memory:";
		seeder = new Seeder(url);
		Database.getInstance(seeder.getConnection());
		app = AppConfig.startServer(port);
	}

	/**
	 * Test that the application retrieves a list of all movies.
	 * Notice how we are checking the actual content of the list.
	 * At this higher level, we are not concerned with the implementation details.
	 */

	@Test
	void retrieves_a_list_of_all_movies() {
		given().when().get(baseURL + "/movies").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", hasItems(1, 2, 3, 4, 5))
				.body("title", hasItems("The Shawshank Redemption", "The Godfather",
						"The Godfather: Part II", "The Dark Knight", "12 Angry Men"))
				.body("year", hasItems(1994, 1972, 1974, 2008, 1957));
	}

	@Test
	void retrieves_a_single_movie_by_id() {

		given().when().get(baseURL + "/movies/1").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", equalTo(1))
				.body("title", equalTo("The Shawshank Redemption"))
				.body("year", equalTo(1994));
	}
	@Test
	void retrieves_a_list_of_all_people() {
		given().when().get(baseURL + "/people").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", hasItems(1, 2, 3, 4, 5))
				.body("name", hasItems("Tim Robbins", "Morgan Freeman",
						"Christopher Nolan", "Al Pacino", "Henry Fonda"))
				.body("birth", hasItems(1958, 1937, 1970, 1940, 1905));
	}

	@Test
	void retrieves_a_single_person_by_id() {

		given().when().get(baseURL + "/people/1").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", equalTo(1))
				.body("name", equalTo("Tim Robbins"))
				.body("birth", equalTo(1958));
	}
	
	@Test
	void retrieves_all_people_by_movieid() {

		given().when().get(baseURL + "/movies/1/stars").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", hasItems(1, 2))
				.body("name", hasItems("Tim Robbins", "Morgan Freeman"))
				.body("birth", hasItems(1958, 1937));
	}
	
	@Test
	void retrieves_all_movies_by_personid() {

		given().when().get(baseURL + "/people/1/movies").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("id", hasItems(1))
				.body("title", hasItems("The Shawshank Redemption"))
				.body("year", hasItems(1994));
	}
	
	@Test
	void retrieves_a_list_of_movies_by_default_limit() {

		given().when().get(baseURL + "/movies").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("size()", lessThanOrEqualTo(50));
	}
	
	@Test
	void retrieves_a_list_of_movies_by_specific_limit() {

		given().when().get(baseURL + "/movies?limit=3").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("size()", equalTo(3));
	}
	
	@Test
	void returns_400_for_invalid_limit_movies() {

		given().when().get(baseURL + "/movies?limit=-5").then().assertThat().statusCode(400). // Assuming a successful
												// response returns HTTP
												// 200
				body(equalTo("Invalid limit"));
	}
	
	@Test
	void retrieves_a_list_of_people_by_default_limit() {

		given().when().get(baseURL + "/people").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("size()", lessThanOrEqualTo(50));
	}
	
	@Test
	void retrieves_a_list_of_people_by_specific_limit() {

		given().when().get(baseURL + "/people?limit=3").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
				body("size()", equalTo(3));
	}
	
	@Test
	void returns_400_for_invalid_limit_people() {

		given().when().get(baseURL + "/people?limit=-5").then().assertThat().statusCode(400). // Assuming a successful
												// response returns HTTP
												// 200
				body(equalTo("Invalid limit"));
	}
	
	@Test
	void returns_movie_ratings_by_year_with_default_limit() {

		given().when().get(baseURL + "/movies/ratings/1994").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
			body("size()", lessThanOrEqualTo(50))
			.body("title", hasItems("The Shawshank Redemption"))
			.body("rating", hasItems(greaterThan(0.0f)))
			.body("votes", hasItems(greaterThan(1000)));
	}
	
	@Test
	void returns_400_for_invalid_limit_ratings() {

		given().when().get(baseURL + "/movies/ratings/1994?limit=-1").then().assertThat().statusCode(400). // Assuming a successful
												// response returns HTTP
												// 200
				body(equalTo("Limit must be an integer greater than 0"));
	}
	
	@Test
	void returns_movie_ratings_by_year_minVotes() {

		given().when().get(baseURL + "/movies/ratings/1994?votes=20000").then().assertThat().statusCode(200). // Assuming a successful
												// response returns HTTP
												// 200
			body("size()", lessThanOrEqualTo(50))
			.body("title", hasItems("The Shawshank Redemption"))
			.body("rating", hasItems(greaterThan(0.0f)))
			.body("votes", hasItems(greaterThan(20000)));
	}
	
	@Test
	void returns_400_for_invalid_year_in_ratings() {

		given().when().get(baseURL + "/movies/ratings/invalid").then().assertThat().statusCode(400). // Assuming a successful
												// response returns HTTP
												// 200
				body(equalTo("Invalid year, limit or minVotes"));
	}
	/**
	 * Tears down the application after each test.
	 * We want to make sure that each test runs in isolation.
	 */
	@AfterEach
	void tearDown() {
		seeder.closeConnection();
		app.stop();
	}

}
