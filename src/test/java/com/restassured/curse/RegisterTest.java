package com.restassured.curse;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RegisterTest {

    private static final String BASE_URL = "https://reqres.in/api/register";

    @Test
    public void shouldRegisterSuccessfully() {
        Map<String, Object> requestBody = Map.of(
                "email", "eve.holt@reqres.in",
                "password", "cityslicka"
        );

        given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL)
                .then().log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }

    @Test
    public void shouldFailRegisterWithMissingPassword() {
        Map<String, Object> requestBody = Map.of("email", "sydney@fife");

        given().log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL)
                .then().log().all()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}
