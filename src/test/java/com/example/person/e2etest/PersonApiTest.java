package com.example.person.e2etest;

import com.example.person.dto.AddressDto;
import com.example.person.dto.PersonDto;
import com.example.person.entity.AddressType;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonApiE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/persons";
    }

    @Test
    void createPerson_ShouldReturn200_AndReturnSavedData() {
        PersonDto newPerson = new PersonDto();
        newPerson.setName("Restassured Ricsi");

        AddressDto address = new AddressDto();
        address.setCity("Veszprém");
        address.setType(AddressType.PERMANENT);
        newPerson.setAddresses(List.of(address));

        given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("name", equalTo("Restassured Ricsi"))
                .body("id", notNullValue())
                .body("addresses[0].city", equalTo("Veszprém"));
    }

    @Test
    void createPerson_ShouldReturn400_WhenTooManyAddresses() {
        PersonDto invalidPerson = new PersonDto();
        invalidPerson.setName("Túlsokcím Tamás");

        AddressDto a1 = new AddressDto(); a1.setType(AddressType.PERMANENT);
        AddressDto a2 = new AddressDto(); a2.setType(AddressType.TEMPORARY);
        AddressDto a3 = new AddressDto(); a3.setType(AddressType.TEMPORARY);

        invalidPerson.setAddresses(List.of(a1, a2, a3));

        given()
                .contentType(ContentType.JSON)
                .body(invalidPerson)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body(equalTo("Egy személynek maximum 2 címe lehet!"));
    }

    @Test
    void workflow_CreateAndGet() {
        PersonDto person = new PersonDto();
        person.setName("Workflow Walid");

        int id = given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().path("id");

        given()
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Workflow Walid"));
    }
}