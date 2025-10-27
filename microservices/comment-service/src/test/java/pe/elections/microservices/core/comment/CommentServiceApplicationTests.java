package pe.elections.microservices.core.comment;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CommentServiceApplicationTests {

    @Autowired private WebTestClient client;

	@Test
	void getCommentsByCandidateId() {
        int candidateId = 1;
        client.get()
            .uri("/comment?candidateId=" + candidateId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].candidateId").isEqualTo(candidateId);

	}

    @Test
    void getCommentsMissingParameter() {
        client.get()
            .uri("/comment")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Required query parameter 'candidateId' is not present.");
    }

    @Test
    void getCommentsInvalidParameter() {
        client.get()
            .uri("/comment?candidateId=no-integer")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .consumeWith(response -> {
                // Convierte el cuerpo de la respuesta a un String para imprimirlo en la consola
                String responseBody = new String(response.getResponseBodyContent());
                System.out.println("--------------------------------------------------");
                System.out.println("Respuesta JSON recibida (BAD_REQUEST):");
                System.out.println(responseBody); 
                System.out.println("--------------------------------------------------");
            })
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getCommentsNotFound() {
        int candidateIdNotFound = 113;
        client.get()
            .uri("/comment?candidateId=" + candidateIdNotFound)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void getCommentsInvalidParameterNegativeValue() {
        int candidateIdInvalid = -1;
        client.get()
            .uri("/comment?candidateId=" + candidateIdInvalid)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateIdInvalid);
    }

}
