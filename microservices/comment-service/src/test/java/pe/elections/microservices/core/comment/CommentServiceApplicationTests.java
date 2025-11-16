package pe.elections.microservices.core.comment;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.comment.persistence.CommentRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
    "spring.cloud.stream.defaultBinder=rabbit",
    "logging.level.pe.elections.microservices=DEBUG",
    "eureka.client.enabled=false"
})
class CommentServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CommentRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Comment>> messageProcessor;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

	@Test
	void getCommentsByCandidateId() {
        int candidateId = 1;
        assertEquals(0, repository.findByCandidateId(candidateId).count().block());
        sendCreateCommentEvent(candidateId, 1);
        sendCreateCommentEvent(candidateId, 2);
        sendCreateCommentEvent(candidateId, 3);
        assertEquals(3, repository.findByCandidateId(candidateId).count().block());
        getAndVerifyCommentByCandidateId(candidateId, OK)
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].candidateId").isEqualTo(candidateId)
            .jsonPath("$[2].commentId").isEqualTo(3);
	}

    @Test
    void duplicateError() {
        int candidateId = 1;
        int commentId = 1;
        assertEquals(0, repository.count().block());
        sendCreateCommentEvent(candidateId, commentId);
        assertEquals(1, (long)repository.count().block());
        InvalidInputException thown = assertThrows(
            InvalidInputException.class,
            () -> sendCreateCommentEvent(candidateId, commentId),
            "Expected a InvalidInputException here!"
        );
        assertEquals("Duplicate key, Candidate Id: 1, Comment Id: 1", thown.getMessage());
        assertEquals(1, (long)repository.count().block());
    }

    @Test
    void deleteComment() {
        int candidateId = 1;
        int commentId = 1;
        sendCreateCommentEvent(candidateId, commentId);
        assertEquals(1, repository.count().block());
        assertEquals(1, repository.findByCandidateId(candidateId).count().block());
        sendDeleteCommentEvent(candidateId);
        assertEquals(0, repository.count().block());
        assertEquals(0, repository.findByCandidateId(candidateId).count().block());
        sendDeleteCommentEvent(candidateId);
    }

    @Test
    void getCommentsMissingParameter() {
        getAndVerifyCommentByCandidateId("", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Required query parameter 'candidateId' is not present.");
    }

    @Test
    void getCommentsInvalidParameter() {
        getAndVerifyCommentByCandidateId("?candidateId=no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getCommentsNotFound() {
        int candidateIdNotFound = 113;
        getAndVerifyCommentByCandidateId(candidateIdNotFound, OK)
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void getCommentsInvalidParameterNegativeValue() {
        int candidateIdInvalid = -1;
        getAndVerifyCommentByCandidateId(candidateIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCommentByCandidateId(int candidateId, HttpStatus expectedStatus) {
        return getAndVerifyCommentByCandidateId("?candidateId=" + candidateId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCommentByCandidateId(String candidateIdQuery, HttpStatus expectedStatus) {
        return client.get()
        .uri("/comment" + candidateIdQuery)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody()
        .consumeWith(result -> {
            String responseBody = new String(result.getResponseBody());
            System.out.println("=== ACTUAL RESPONSE ===");
            System.out.println("/comment" + candidateIdQuery);
            System.out.println(responseBody);
            System.out.println("=== END RESPONSE ===");
        });
    }

    private void sendCreateCommentEvent(int candidateId, int commentId) {

        Comment newComment = new Comment(candidateId, commentId, "afsd", "adfa", System.currentTimeMillis(), "adr");
        Event<Integer, Comment> event = new Event<>(CREATE, candidateId, newComment);
        messageProcessor.accept(event);
    }

    private void sendDeleteCommentEvent(int candidateId) {
        Event<Integer, Comment> event = new Event<>(DELETE, candidateId, null);
        messageProcessor.accept(event);
    }
}
