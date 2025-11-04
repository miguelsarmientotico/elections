package pe.elections.microservices.core.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.core.comment.persistence.CommentRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CommentServiceApplicationTests extends MongoDbTestBase {

    @Autowired
    private WebTestClient client;

    @Autowired
    private CommentRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

	@Test
	void getCommentsByCandidateId() {
        int candidateId = 1;
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        postAndVerifyComment(candidateId, 1, OK);
        postAndVerifyComment(candidateId, 2, OK);
        postAndVerifyComment(candidateId, 3, OK);
        assertEquals(3, repository.findByCandidateId(candidateId).size());
        getAndVerifyCommentByCandidateId(candidateId, OK)
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].candidateId").isEqualTo(candidateId)
            .jsonPath("$[2].commentId").isEqualTo(3);
	}

    @Test
    void duplicateError() {
        int candidateId = 1;
        int newCommentId = 1;
        assertEquals(0, repository.count());
        postAndVerifyComment(candidateId, newCommentId, OK)
            .jsonPath("$.candidateId").isEqualTo(candidateId)
            .jsonPath("$.commentId").isEqualTo(newCommentId);
        assertEquals(1, repository.count());
        postAndVerifyComment(candidateId, newCommentId, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/comment")
            .jsonPath("$.message").isEqualTo("Duplicate key, Candidate Id: 1, Comment Id: 1");
        assertEquals(1, repository.count());
    }

    @Test
    void deleteComment() {
        int candidateId = 1;
        int commentId = 1;
        postAndVerifyComment(candidateId, commentId, OK);
        assertEquals(1, repository.count());
        assertEquals(1, repository.findByCandidateId(candidateId).size());
        deleteAndVerifyCommentByCandidateId(candidateId, OK);
        assertEquals(0, repository.count());
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        deleteAndVerifyCommentByCandidateId(candidateId, OK);
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
        .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyComment(int candidateId, int commentId, HttpStatus expectedStatus) {
        Comment newComment = new Comment(candidateId, commentId, "afsd", "adfa", LocalDateTime.now(), "adr");
        return client.post()
        .uri("/comment")
        .body(just(newComment), Comment.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyCommentByCandidateId(int candidateId, HttpStatus expectedStatus) {
        return client.delete()
        .uri("/comment?candidateId=" + candidateId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectBody();
    }

}
