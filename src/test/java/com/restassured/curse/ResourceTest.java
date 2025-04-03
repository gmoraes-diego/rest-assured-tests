package com.restassured.curse;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ResourceTest {

    private static final String BASE_URL = "https://reqres.in/api/unknown";

    @Test
    public void shouldReturnCorrectResourceListSize() {
        given().log().all()
                .when()
                .get(BASE_URL)
                .then().log().all()
                .statusCode(200)
                .body("per_page", is(6))
                .body("data.size()", is(6));
    }

    @Test
    public void shouldMatchPerPageWithDataSize() {
        Response response = given().log().all()
                .when()
                .get(BASE_URL)
                .then().log().all()
                .statusCode(200)
                .extract()  // Extrai a resposta para manipulá-la posteriormente
                .response();  // Armazena a resposta completa em um objeto Response

        // Extrai o valor de "per_page" do JSON da resposta e armazena em uma variável inteira
        int perPageValue = response.jsonPath().getInt("per_page");
        // Extrai a lista "data" do JSON da resposta e obtém o tamanho dessa lista, armazenando o valor em dataSize
        int dataSize = response.jsonPath().getList("data").size();
        // Asserção para verificar se o tamanho da lista "data" é igual ao valor de "per_page"
        assertThat(dataSize, is(perPageValue));

    }
}
