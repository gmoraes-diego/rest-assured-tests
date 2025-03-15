package com.restassured.curse;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @Test
    public void shouldReturnListOfUsers() {
        given().log().all()
                .queryParam("page", 2)
                .when()
                .get("https://reqres.in/api/users")
                .then().log().all()
                .statusCode(200)
                .body("page", is(2))
                .body("per_page", is(6))
                .body("total", is(12))
                .body("total_pages", is(2))
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
                .get("https://reqres.in/api/users/{id}")
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
                .get("https://reqres.in/api/users/{id}")
                .then().log().all()
                .statusCode(404)
                .body("$", anEmptyMap());

    }

    @Test
    public void shouldReturnCorrectResourceListSize() {
        given().log().all()
                .when()
                .get("https://reqres.in/api/unknown")
                .then().log().all()
                .statusCode(200)
                .body("per_page", is(6))
                .body("data.size()", is(6));

    }

    @Test
    public void shouldMatchPerPageWithDataSize() {
        // Faz a requisição e armazena a resposta completa
        Response response = given().log().all()
                .when()
                .get("https://reqres.in/api/unknown")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response(); // Extrai a resposta completa

        // Extrai os valores do JSON
        int perPageValue = response.jsonPath().getInt("per_page"); // Obtém "per_page"
        int dataSize = response.jsonPath().getList("data").size(); // Obtém o tamanho da lista "data"

        // Valida que o tamanho de "data" é igual ao valor de "per_page"
        assertThat(dataSize, is(perPageValue));
    }

    @Test
    public void shouldCreateUserSuccessfully() {
        // Cria um mapa para armazenar os dados que serão enviados no corpo da requisição
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Diego"); // Adiciona o campo "name" com o valor "Márcio"
        requestBody.put("job", "QA Engineer");  // Adiciona o campo "job" com o valor "Motorista"

        given()
                .contentType(ContentType.JSON) // Define o tipo de conteúdo como JSON
                .body(requestBody) // Passa o corpo da requisição (o JSON criado acima)
                .when()
                .post("https://reqres.in/api/users")
                .then().log().all()
                .statusCode(201) // Continua verificando se a criação foi bem-sucedida
                .body("name", is(requestBody.get("name")))
                // Valida que o campo "name" no response é igual ao valor de "name" da request.
                .body("job", is(requestBody.get("job")))
                // Valida que o campo "job" na response é igual ao valor de "job" da request.
                .body("id", notNullValue()) // Garante que a API retornou um ID para o novo usuário
                .body("createdAt", notNullValue()); // Garante que a API retornou a data de criação
    }

    @Test
    public void shouldUpdateUserWithPut() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Mary");
        requestBody.put("job", "Architect");

        given().log().all()
                .pathParams("id", 2)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("https://reqres.in/api/api/users/{id}")
                .then().log().all()
                .statusCode(200)
                .body("updatedAt", containsString(LocalDate.now().toString())); // Valida data do update
    }

    @Test
    public void shouldPartiallyUpdateUserWithPatch() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("job", "Senior developer");

        given().log().all()
                .pathParams("id", 3)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("https://reqres.in/api/api/users/{id}")
                .then().log().all()
                .statusCode(200)
                .body("job", is(requestBody.get("job")))
                .body("updatedAt", containsString(LocalDate.now().toString())); // Valida data do update
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        given().log().all()
                .pathParams("id", 4)
                .when()
                .delete("https://reqres.in/api/api/users/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
