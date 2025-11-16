package pe.elections.microservices.composite.candidate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static pe.elections.microservices.composite.candidate.IsSameEvent.sameEventExceptCreatedAt;
import static reactor.core.publisher.Mono.just;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static java.util.Collections.singletonList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.elections.microservices.api.composite.candidate.CandidateAggregate;
import pe.elections.microservices.api.composite.candidate.CommentSummary;
import pe.elections.microservices.api.composite.candidate.NewsArticleSummary;
import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.event.Event.Type;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "eureka.client.enabled=false"
    }
)
@Import({TestChannelBinderConfiguration.class})
class MessagingTests {

    public static final Logger LOG = LoggerFactory.getLogger(MessagingTests.class);

    @Autowired
    private WebTestClient client;
    
    @Autowired
    private OutputDestination target;

    @BeforeEach
    void setup() {
        purgeMessage("candidates");
        purgeMessage("comments");
        purgeMessage("newsarticles");
    }

    @Test
    void createCompositeCandidate1() {
        CandidateAggregate composite = new CandidateAggregate(1, "name", 30, null, null, null);
        postAndVerifyCandidate(composite, HttpStatus.ACCEPTED);
        final List<String> candidateMessages = getMessages("candidates");
        final List<String> commentMessages = getMessages("comments");
        final List<String> newsArticleMessages = getMessages("newsarticles");
        assertEquals(1, candidateMessages.size());
        Event<Integer, Candidate> expectedEvent = new Event<Integer, Candidate>(
            Type.CREATE,
            composite.getCandidateId(),
            new Candidate(composite.getCandidateId(), composite.getName(), composite.getEdad(), null)
        );
        assertThat(candidateMessages.get(0), is(sameEventExceptCreatedAt(expectedEvent)));
        assertEquals(0, commentMessages.size());
        assertEquals(0, newsArticleMessages.size());
    }

    @Test
    void createCompositeCandidate2() {
        CandidateAggregate composite = new CandidateAggregate(1, "name", 30,
            singletonList(new CommentSummary(1, "a", "b", System.currentTimeMillis())),
            singletonList(new NewsArticleSummary(1, "a", "b", "c", Instant.now(), "d")),
            null);
        postAndVerifyCandidate(composite, ACCEPTED);
        final List<String> candidateMessages = getMessages("candidates");
        final List<String> commentMessages = getMessages("comments");
        final List<String> newsArticleMessages = getMessages("newsarticles");
        assertEquals(1, candidateMessages.size());
        Event<Integer, Candidate> expectedCandidateEvent = new Event<Integer, Candidate>(
            Type.CREATE,
            composite.getCandidateId(),
            new Candidate(composite.getCandidateId(), composite.getName(), composite.getEdad(), null)
        );
        assertThat(candidateMessages.get(0), is(sameEventExceptCreatedAt(expectedCandidateEvent)));
        assertEquals(1, commentMessages.size());
        CommentSummary cmn = composite.getComments().get(0);
        Event<Integer, Comment> expectedCommentEvent = new Event<Integer, Comment>(
            Type.CREATE,
            composite.getCandidateId(),
            new Comment(composite.getCandidateId(), cmn.getCommentId(), cmn.getContent(), cmn.getAuthor(), cmn.getCreatedAt(), null)
        );
        assertThat(commentMessages.get(0), is(sameEventExceptCreatedAt(expectedCommentEvent)));
        assertEquals(1, newsArticleMessages.size());
        NewsArticleSummary news = composite.getNewsArticles().get(0);
        Event<Integer, NewsArticle> expectedNewsArticleEvent = new Event<Integer, NewsArticle>(
            Type.CREATE,
            composite.getCandidateId(),
            new NewsArticle(composite.getCandidateId(), news.getNewsArticleId(), news.getTitle(), news.getContent(), news.getAuthor(), news.getPublishDate(), news.getCategory(), null)
        );
        assertThat(newsArticleMessages.get(0), is(sameEventExceptCreatedAt(expectedNewsArticleEvent)));
    }

    @Test
    void deleteCompositeCandidate() {
        deleteAndVerifyCandidate(1, ACCEPTED);
        final List<String> candidateMessages = getMessages("candidates");
        final List<String> commentMessages = getMessages("comments");
        final List<String> newsArticleMessages = getMessages("newsarticles");
        assertEquals(1, candidateMessages.size());
        Event<Integer, Candidate> expectedCandidateEvent = new Event<Integer,Candidate>(Type.DELETE, 1, null);
        assertThat(candidateMessages.get(0), is(sameEventExceptCreatedAt(expectedCandidateEvent)));
        assertEquals(1, commentMessages.size());
        Event<Integer, Comment> expectedCommentEvent = new Event<Integer,Comment>(Type.DELETE, 1, null);
        assertThat(commentMessages.get(0), is(sameEventExceptCreatedAt(expectedCommentEvent)));
        assertEquals(1, newsArticleMessages.size());
        Event<Integer, NewsArticle> expectedNewsArticleEvent = new Event<Integer, NewsArticle>(Type.DELETE, 1, null);
        assertThat(newsArticleMessages.get(0), is(sameEventExceptCreatedAt(expectedNewsArticleEvent)));
    }

    private void purgeMessage(String bindingName) {
        getMessages(bindingName);
    }

    private List<String> getMessages(String bindingName) {
        List<String> messages = new ArrayList<>();
        boolean anyMoreMessages = true;
        while (anyMoreMessages) {
            Message<byte[]> message = getMessage(bindingName);
            if (message == null) {
                anyMoreMessages = false;
            } else {
                messages.add(new String(message.getPayload()));
            }
        }
        return messages;
    }

    private Message<byte[]> getMessage(String bindingName) {
        try {
            return target.receive(0, bindingName);
        } catch (NullPointerException npe) {
            LOG.error("getMessage() received a NPE with binding = {}", bindingName);
            return null;
        }
    }

    private void postAndVerifyCandidate(CandidateAggregate compositeCandidate, HttpStatus expectedStatus) {
        client.post()
            .uri("/candidate-composite")
            .body(just(compositeCandidate), CandidateAggregate.class)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyCandidate(int candidateId, HttpStatus expectedStatus) {
        client.delete()
            .uri("/candidate-composite/" + candidateId)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus);
    }
}
