package pe.elections.microservices.core.newsarticle;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Consumer;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.event.Event.Type;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
    "spring.cloud.stream.defaultBinder=rabbit",
    "logging.level.pe.elections.microservices=DEBUG"
})
class NewsArticleServiceApplicationTests extends MySqlTestBase {
    
    @Autowired
    private WebTestClient client;

    @Autowired
    private NewsArticleRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, NewsArticle>> messageProcessor;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

	@Test
	void getNewsArticlesByCandidateId() {
        int candidateId = 1;
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        sendCreateNewsArticleEvent(candidateId, 1);
        sendCreateNewsArticleEvent(candidateId, 2);
        sendCreateNewsArticleEvent(candidateId, 3);
        assertEquals(3, repository.findByCandidateId(candidateId).size());
        getAndVerifyNewsArticleByCandidateId(candidateId, OK)
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].candidateId").isEqualTo(candidateId)
            .jsonPath("$[2].newsArticleId").isEqualTo(3);
	}

    @Test
    void duplicateError() {
        int candidateId = 1;
        int newsArticleId = 1;
        assertEquals(0, repository.count());
        sendCreateNewsArticleEvent(candidateId, newsArticleId);
        assertEquals(1, repository.count());
        InvalidInputException thrown = assertThrows(
            InvalidInputException.class,
            () -> sendCreateNewsArticleEvent(candidateId, newsArticleId),
            "Expected a InvalidInputException here!"
        );
        assertEquals("Duplicate key, Candidate Id: 1, NewsArticle Id: 1", thrown.getMessage());
        assertEquals(1, repository.count());
    }

    @Test
    void deleteNewsArticle() {
        int candidateId = 1;
        int newsArticleId = 1;
        sendCreateNewsArticleEvent(candidateId, newsArticleId);
        assertEquals(1, repository.findByCandidateId(candidateId).size());
        sendDeleteNewsArticleEvent(candidateId);
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        sendDeleteNewsArticleEvent(candidateId);
    }

    @Test
    void getNewsArticlesMissingParameter() {
        getAndVerifyNewsArticleByCandidateId("", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Required query parameter 'candidateId' is not present.");
    }

    @Test
    void getNewsArticlesInvalidParameter() {
        getAndVerifyNewsArticleByCandidateId("?candidateId=no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getNewsArticlesNotFound() {
        getAndVerifyNewsArticleByCandidateId("?candidateId=213", OK)
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void getNewsArticlesInvalidParameterNegativeValue() {
        int candidateIdInvalid = -1;
        getAndVerifyNewsArticleByCandidateId("?candidateId=" + candidateIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyNewsArticleByCandidateId(int candidateId, HttpStatus expectedStatus) {
        return getAndVerifyNewsArticleByCandidateId("?candidateId=" + candidateId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyNewsArticleByCandidateId(String candidateIdQuery, HttpStatus expectedStatus) {
        return client.get()
        .uri("/news-article" + candidateIdQuery)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

    private void sendCreateNewsArticleEvent(int candidateId, int newsArticleId) {
        LocalDateTime publishDate = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        Instant instant = publishDate.atZone(ZoneId.of("UTC")).toInstant();
        NewsArticle newsArticle = new NewsArticle(candidateId, newsArticleId, "title " + newsArticleId, "content" + newsArticleId, "author" + newsArticleId, instant, "category" + newsArticleId, "SA");
        Event<Integer, NewsArticle> event = new Event<Integer, NewsArticle>(Type.CREATE, candidateId, newsArticle);
        messageProcessor.accept(event);
    }

    private void sendDeleteNewsArticleEvent(int candidateId) {
        Event<Integer, NewsArticle> event = new Event<Integer, NewsArticle>(Type.DELETE, candidateId, null);
        messageProcessor.accept(event);
    }
}
