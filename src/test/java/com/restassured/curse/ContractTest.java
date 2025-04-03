package com.restassured.curse;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class ContractTest {

    private static final String BASE_URL = "https://reqres.in/api/users";

    @Test
    public void shouldMatchUserSchema() throws IOException { // Declara um teste que pode lançar IOException

        // Usa try-with-resources para garantir que o InputStream seja fechado corretamente após o uso
        try (InputStream userSchemaStream = getClass().getClassLoader().getResourceAsStream("contracts/userSchema.json")) {

            // Verifica se o arquivo foi encontrado no caminho especificado
            if (userSchemaStream == null) {
                throw new RuntimeException("O arquivo userSchema.json não foi encontrado! Verifique o caminho.");
                // Lança uma exceção caso o arquivo não seja encontrado
            }

            // Converte o conteúdo do arquivo JSON (InputStream) para uma String legível
            String userSchema = new String(userSchemaStream.readAllBytes(), StandardCharsets.UTF_8);


            given().log().all()
                    .pathParam("id", 2)
                    .contentType(ContentType.JSON)
                    .when()
                    .get(BASE_URL + "/{id}")
                    .then().log().all()
                    .statusCode(200)
                    .body(matchesJsonSchema(userSchema)); // Valida se a resposta segue o esquema JSON esperado
        }
    }


}
