package pe.elections.microservices.composite.candidate;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.time.LocalDateTime;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.composite.candidate.services.CandidateCompositeIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CandidateCompositeServiceApplicationTests {

    private static final int CANDIDATE_ID_OK = 1;
    private static final int CANDIDATE_ID_NOT_FOUND = 113;
    private static final int CANDIDATE_ID_INVALID = -1;

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private CandidateCompositeIntegration compositeIntegration;

    @BeforeEach
    void setup() {
        when(compositeIntegration.getCandidate(CANDIDATE_ID_OK))
            .thenReturn(Mono.just(new Candidate(CANDIDATE_ID_OK, "nombre del candidato", 35, "mock-address")));
        when(compositeIntegration.getComments(CANDIDATE_ID_OK))
            .thenReturn(Flux.fromIterable(singletonList(new Comment(CANDIDATE_ID_OK, 1, "contenido del comentario 1", "autor comentario 1", System.currentTimeMillis(), "mock address"))));
        when(compositeIntegration.getNewsArticles(CANDIDATE_ID_OK))
            .thenReturn(Flux.fromIterable(singletonList(new NewsArticle(CANDIDATE_ID_OK, 1, "titulo de la noticia 1", "contenido de la noticia 1", "autor de la noticia 1", Instant.now(), "informativo", "mock address"))));
        when(compositeIntegration.getCandidate(CANDIDATE_ID_NOT_FOUND))
            .thenThrow(new NotFoundException("NOT FOUND: " + CANDIDATE_ID_NOT_FOUND));
        when(compositeIntegration.getCandidate(CANDIDATE_ID_INVALID))
            .thenThrow(new InvalidInputException("INVALID: " + CANDIDATE_ID_INVALID));
    }

    @Test
    void getCandidateById() {
        getAndVerifyCandidate(CANDIDATE_ID_OK, OK)
            .jsonPath("$.candidateId").isEqualTo(CANDIDATE_ID_OK)
            .jsonPath("$.comments.length()").isEqualTo(1)
            .jsonPath("$.newsArticles.length()").isEqualTo(1);
    }

    @Test
    void getCandidateNotFound() {
        getAndVerifyCandidate(CANDIDATE_ID_NOT_FOUND, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/candidate-composite/" + CANDIDATE_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + CANDIDATE_ID_NOT_FOUND);
    }

    @Test
    void getCandidateInvalidInput() {
        getAndVerifyCandidate(CANDIDATE_ID_INVALID, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/candidate-composite/" + CANDIDATE_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + CANDIDATE_ID_INVALID);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCandidate(int candidateId, HttpStatus expectedStatus) {
        return client.get()
        .uri("/candidate-composite/" + candidateId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

}
