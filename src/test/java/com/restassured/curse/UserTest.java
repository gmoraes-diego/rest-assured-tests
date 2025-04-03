package com.restassured.curse;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTest {

    private static final String BASE_URL = "https://reqres.in/api/users";

    @Test
    public void shouldReturnListOfUsers() {
        given().log().all()
                .queryParam("page", 2)
                .when()
                .get(BASE_URL)
                .then().log().all()
                .statusCode(200)
                .body("page", is(2))
                .body("data.size()", is(6))
                .body("data[0].id", is(7))
                .body("data[0].email", is("michael.lawson@reqres.in"))
                .body("data[0].first_name", is("Michael"))
                .body("data[0].last_name", is("Lawson"))
                .body("data[0].avatar", is("https://reqres.in/img/faces/7-image.jpg"));
    }

    @Test
    public void shouldReturnSingleUserById() {
        given().log().all()
                .pathParams("id", 2)
                .when()
                .get(BASE_URL + "/{id}")
                .then().log().all()
                .statusCode(200)
                .body("data.id", is(2))
                .body("data.email", is("janet.weaver@reqres.in"))
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"))
                .body("data.avatar", is("https://reqres.in/img/faces/2-image.jpg"))
                .body("support.url", is( "https://contentcaddy.io?utm_source=reqres&utm_medium=json&utm_campaign=referral"))
                .body("support.text", is( "Tired of writing endless social media content? Let Content Caddy generate it for you."));
    }

    @Test
    public void shouldReturnNotFoundForNonexistentUser() {
        given().log().all()
                .pathParams("id", 23)
                .when()
                .get(BASE_URL + "/{id}")
                .then().log().all()
                .statusCode(404)
                .body("$", anEmptyMap()); // Corpo vazio
    }

    @Test
    public void shouldCreateUserSuccessfully() {
        Map<String, Object> requestBody = Map.of(
                "name", "Diego",
                "job", "QA Engineer"
        );

        given().log().all()
                .contentType(ContentType.JSON) // Define o tipo de conteúdo como JSON
                .body(requestBody) // Passa o corpo da requisição (o JSON criado acima)
                .when()
                .post("https://reqres.in/api/users")
                .then().log().all()
                .statusCode(201)
                .body("name", is(requestBody.get("name")))
                // Valida que o campo "name" no response é igual ao valor de "name" da request.
                .body("job", is(requestBody.get("job")))
                // Valida que o campo "job" na response é igual ao valor de "job" da request.
                .body("id", notNullValue()); // Garante que a API retornou um ID para o novo usuário
    }

    @Test
    public void shouldUpdateUserWithPut() {
        Map<String, Object> requestBody = Map.of(
                "name", "Mary",
                "job", "Architect"
        );

        given().log().all()
                .pathParams("id", 2)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(BASE_URL + "/{id}")
                .then().log().all()
                .statusCode(200)
                .body("updatedAt", containsString(LocalDate.now(ZoneOffset.UTC).toString()));
    }

    @Test
    public void shouldPartiallyUpdateUserWithPatch() {
        Map<String, Object> requestBody = Map.of("job", "Senior developer");

        given().log().all()
                .pathParams("id", 3)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(BASE_URL + "/{id}")
                .then().log().all()
                .statusCode(200)
                .body("job", is(requestBody.get("job")))
                .body("updatedAt", containsString(LocalDate.now(ZoneOffset.UTC).toString()));
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        given().log().all()
                .pathParams("id", 4)
                .when()
                .delete(BASE_URL + "/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
