package pe.elections.microservices.core.newsarticle;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class NewsArticleServiceApplicationTests {
    
    @Autowired private WebTestClient client;

	@Test
	void getNewsArticlesByCandidateId() {
        int candidateId = 1;
        client.get()
            .uri("/news-article?candidateId=" + candidateId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].candidateId").isEqualTo(1);
	}

    @Test
    void getNewsArticlesMissingParameter() {
        client.get()
            .uri("/news-article")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Required query parameter 'candidateId' is not present.");
    }

    @Test
    void getNewsArticlesInvalidParameter() {
        client.get()
            .uri("/news-article?candidateId=no-integer")
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
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getNewsArticlesNotFound() {
        int candidateIdNotFound = 113;
        client.get()
            .uri("/news-article?candidateId=" + candidateIdNotFound)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void getNewsArticlesInvalidParameterNegativeValue() {
        int candidateIdInvalid = -1;
        client.get()
            .uri("/news-article?candidateId=" + candidateIdInvalid)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/news-article")
            .jsonPath("$.message").isEqualTo("Invalid candidateId: " + candidateIdInvalid);
    }


}
