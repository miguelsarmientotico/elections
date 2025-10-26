package pe.elections.microservices.core.comment;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CommentServiceApplicationTests {

    @Autowired private WebTestClient client;

	@Test
	void getCommentsByCandidateId() {
        int candidateId = 1;
        client.get()
            .uri("/comment?candidateId=" + candidateId)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].candidateId").isEqualTo(candidateId);

	}

}
