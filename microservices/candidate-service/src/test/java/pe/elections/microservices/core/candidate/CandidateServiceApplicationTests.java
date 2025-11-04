package pe.elections.microservices.core.candidate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.core.candidate.persistence.CandidateRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CandidateServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CandidateRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

    @Test
    void getCandidateById() {
        int candidateId = 1;
        postAndVerifyCandidate(candidateId, OK);
        assertTrue(repository.findByCandidateId(candidateId).isPresent());
        getAndVerifyCandidate(candidateId, OK)
            .jsonPath("$.candidateId").isEqualTo(candidateId);
    }

    @Test
    void getCandidateInvalidParameterString() {
        getAndVerifyCandidate("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/candidate/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getCandidateNotFound() {
        int candidateIdNotFound = 113;
        getAndVerifyCandidate(candidateIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/candidate/" + candidateIdNotFound)
            .jsonPath("$.message").isEqualTo("No candidate found for candidateId: " + candidateIdNotFound);
    }

    @Test
    void getCandidateInvalidParameterNegativeValue() {
        int candidateInvalid = -1;
        getAndVerifyCandidate(candidateInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/candidate/" + candidateInvalid)
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateInvalid);
    }

    @Test
    void duplicateError() {
        int candidateId = 1;
        postAndVerifyCandidate(candidateId, OK);
        assertTrue(repository.findByCandidateId(candidateId).isPresent());
        postAndVerifyCandidate(candidateId, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/candidate")
            .jsonPath("$.message").isEqualTo("Duplicate key, Candidate Id: " + candidateId);
    }

    @Test
    void deleteCandidate() {
        int candidateId = 1;
        postAndVerifyCandidate(candidateId, OK);
        assertTrue(repository.findByCandidateId(candidateId).isPresent());
        deleteAndVerifyCandidate(candidateId, OK);
        assertFalse(repository.findByCandidateId(candidateId).isPresent());
        deleteAndVerifyCandidate(candidateId, OK);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCandidate(int candidateId, HttpStatus expectedStatus) {
        return getAndVerifyCandidate("/" + candidateId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCandidate(String candidateIdQuery, HttpStatus expectedStatus) {
        return client.get()
        .uri("/candidate" + candidateIdQuery)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyCandidate(int candidateId, HttpStatus expectedStatus) {
        Candidate newCandidate = new Candidate(candidateId, "dfsa", 30, "adr");
        return client.post()
        .uri("/candidate")
        .body(just(newCandidate), Candidate.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyCandidate(int candidateId, HttpStatus expectedStatus) {
        return client.delete()
        .uri("/candidate/" + candidateId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectBody();
    }

}
