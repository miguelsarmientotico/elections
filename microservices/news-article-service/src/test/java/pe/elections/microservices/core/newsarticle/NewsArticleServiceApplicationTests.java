package pe.elections.microservices.core.newsarticle;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleEntity;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleRepository;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class NewsArticleServiceApplicationTests extends MySqlTestBase {
    
    @Autowired
    private WebTestClient client;

    @Autowired
    private NewsArticleRepository repository;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
    }

	@Test
	void getNewsArticlesByCandidateId() {
        int candidateId = 1;
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        postAndVerifyNewsArticle(candidateId, 1, OK);
        postAndVerifyNewsArticle(candidateId, 2, OK);
        postAndVerifyNewsArticle(candidateId, 3, OK);
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
        postAndVerifyNewsArticle(candidateId, newsArticleId, OK)
            .jsonPath("$.candidateId").isEqualTo(candidateId)
            .jsonPath("$.newsArticleId").isEqualTo(newsArticleId);
        assertEquals(1, repository.count());
        postAndVerifyNewsArticle(candidateId, newsArticleId, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Duplicate key, Candidate Id: 1, NewsArticle Id: 1");
        assertEquals(1, repository.count());
    }

    @Test
    void deleteNewsArticle() {
        int candidateId = 1;
        int newsArticleId = 1;
        postAndVerifyNewsArticle(candidateId, newsArticleId, OK);
        assertEquals(1, repository.findByCandidateId(candidateId).size());
        deleteAndVerifyNewsArticleByCandidateId(candidateId, OK);
        assertEquals(0, repository.findByCandidateId(candidateId).size());
        deleteAndVerifyNewsArticleByCandidateId(candidateId, OK);
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

    private WebTestClient.BodyContentSpec postAndVerifyNewsArticle(int candidateId, int newsArticleId, HttpStatus expectedStatus) {
        NewsArticle newsArticle = new NewsArticle(candidateId, newsArticleId, "title " + newsArticleId, "content" + newsArticleId, "author" + newsArticleId, LocalDateTime.now(), "category" + newsArticleId, "SA");
        return client.post()
        .uri("/news-article")
        .body(Mono.just(newsArticle), NewsArticle.class)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyNewsArticleByCandidateId(int candidateId, HttpStatus expectedStatus) {
        return client.delete()
        .uri("/news-article?candidateId=" + candidateId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectBody();
    }

}
