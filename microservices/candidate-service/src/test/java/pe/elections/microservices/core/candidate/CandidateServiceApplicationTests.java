package pe.elections.microservices.core.candidate;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CandidateServiceApplicationTests {

    @Autowired private WebTestClient client;

    @Test
    void getCandidateById() {
        int candidateId = 1;
        client.get()
            .uri("/candidate/" + candidateId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.candidateId").isEqualTo(candidateId);
    }

    @Test
    void getCandidateInvalidParameterString() {
        client.get()
            .uri("/candidate/no-integer")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/candidate/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getCandidateNotFound() {
        int candidateIdNotFound = 113;
        client.get()
            .uri("/candidate/" + candidateIdNotFound)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/candidate/" + candidateIdNotFound)
            .jsonPath("$.message").isEqualTo("No candidate found for candidateId: " + candidateIdNotFound);
    }

    @Test
    void getCandidateInvalidParameterNegativeValue() {
        int candidateIdInvalid = -1;
        client.get()
            .uri("/candidate/" + candidateIdInvalid)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/candidate/" + candidateIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateIdInvalid);
    }

}
