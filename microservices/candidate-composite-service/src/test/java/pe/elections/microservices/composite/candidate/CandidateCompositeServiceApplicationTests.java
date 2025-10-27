package pe.elections.microservices.composite.candidate;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.composite.candidate.services.CandidateCompositeIntegration;

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
            .thenReturn(new Candidate(CANDIDATE_ID_OK, "nombre del candidato", 35, "mock-address"));
        when(compositeIntegration.getComments(CANDIDATE_ID_OK))
            .thenReturn(singletonList(new Comment(CANDIDATE_ID_OK, 1, "contenido del comentario 1", "autor comentario 1", LocalDateTime.now(), "mock address")));
        when(compositeIntegration.getNewsArticles(CANDIDATE_ID_OK))
            .thenReturn(singletonList(new NewsArticle(CANDIDATE_ID_OK, 1, "titulo de la noticia 1", "contenido de la noticia 1", "autor de la noticia 1", LocalDateTime.now(), "informativo", "mock address")));

        when(compositeIntegration.getCandidate(CANDIDATE_ID_NOT_FOUND))
            .thenThrow(new NotFoundException("NOT FOUND: " + CANDIDATE_ID_NOT_FOUND));

        when(compositeIntegration.getCandidate(CANDIDATE_ID_INVALID))
            .thenThrow(new InvalidInputException("INVALID: " + CANDIDATE_ID_INVALID));
    }

    @Test
    void getCandidateById() {
        client.get()
            .uri("/candidate-composite/" + CANDIDATE_ID_OK)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.candidateId").isEqualTo(1)
            .jsonPath("$.comments.length()").isEqualTo(1)
            .jsonPath("$.newsArticles.length()").isEqualTo(1);
    }

    @Test
    void getCandidateNotFound() {
        client.get()
            .uri("/candidate-composite/" + CANDIDATE_ID_NOT_FOUND)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/candidate-composite/" + CANDIDATE_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + CANDIDATE_ID_NOT_FOUND);
    }

    @Test
    void getCandidateInvalidInput() {
        client.get()
            .uri("/candidate-composite/" + CANDIDATE_ID_INVALID)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/candidate-composite/" + CANDIDATE_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + CANDIDATE_ID_INVALID);
    }

}
