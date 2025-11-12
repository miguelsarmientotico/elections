package pe.elections.microservices.core.candidate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static pe.elections.microservices.api.event.Event.Type.CREATE;
import static pe.elections.microservices.api.event.Event.Type.DELETE;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.candidate.persistence.CandidateRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CandidateServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CandidateRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Candidate>> messageProcessor;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void getCandidateById() {
        int candidateId = 1;
        assertNull(repository.findByCandidateId(candidateId).block());
        assertEquals(0, (long)repository.count().block());
        sendCreateCandidateEvent(candidateId);
        assertNotNull(repository.findByCandidateId(candidateId).block());
        assertEquals(1, (long)repository.count().block());
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
        assertNull(repository.findByCandidateId(candidateId).block());
        sendCreateCandidateEvent(candidateId);
        assertNotNull(repository.findByCandidateId(candidateId).block());
        InvalidInputException thrown = assertThrows(
            InvalidInputException.class,
            () -> sendCreateCandidateEvent(candidateId),
            "Expected a InvalidInputException here!"
        );
        assertEquals("Duplicate key, Candidate Id: " + candidateId, thrown.getMessage());
    }

    @Test
    void deleteCandidate() {
        int candidateId = 1;
        sendCreateCandidateEvent(candidateId);
        assertNotNull(repository.findByCandidateId(candidateId).block());
        sendDeleteCandidateEvent(candidateId);
        assertNull(repository.findByCandidateId(candidateId).block());
        sendDeleteCandidateEvent(candidateId);
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

    private void sendCreateCandidateEvent(int candidateId) {
        Candidate candidate = new Candidate(candidateId, "name" + candidateId, 30 + candidateId, "adr");
        Event<Integer, Candidate> event = new Event<Integer, Candidate>(CREATE, candidateId, candidate);
        messageProcessor.accept(event);
    }

    private void sendDeleteCandidateEvent(int candidateId) {
        Event<Integer, Candidate> event = new Event<Integer, Candidate>(DELETE, candidateId, null);
        messageProcessor.accept(event);
    }

}
