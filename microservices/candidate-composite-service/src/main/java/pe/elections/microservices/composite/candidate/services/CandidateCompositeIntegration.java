package pe.elections.microservices.composite.candidate.services;

import static pe.elections.microservices.api.event.Event.Type.CREATE;
import static pe.elections.microservices.api.event.Event.Type.DELETE;
import static reactor.core.publisher.Mono.empty;

import java.io.IOException;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.candidate.CandidateService;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.core.newsarticle.NewsArticleService;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.util.http.HttpErrorInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class CandidateCompositeIntegration implements CandidateService, CommentService, NewsArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateCompositeIntegration.class);
    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final String candidateServiceUrl;
    private final String commentServiceUrl;
    private final String newsArticleServiceUrl;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Autowired
    public CandidateCompositeIntegration(
        @Qualifier("publishEventScheduler")
        Scheduler publishEventScheduler,
        StreamBridge streamBridge,
        WebClient.Builder webClient,
        ObjectMapper mapper,
        @Value("${app.candidate-service.host}") String candidateServiceHost,
        @Value("${app.candidate-service.port}") String candidateServicePort,
        @Value("${app.comment-service.host}") String commentServiceHost,
        @Value("${app.comment-service.port}") String commentServicePort,
        @Value("${app.newsarticle-service.host}") String newsArticleServiceHost,
        @Value("${app.newsarticle-service.port}") String newsArticleServicePort
    ) {
        this.publishEventScheduler = publishEventScheduler;
        this.streamBridge = streamBridge;
        this.webClient = webClient.build();
        this.mapper = mapper;
        candidateServiceUrl = "http://" + candidateServiceHost + ":" + candidateServicePort;
        commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort;
        newsArticleServiceUrl = "http://" + newsArticleServiceHost + ":" + newsArticleServicePort;
    }

    @Override
    public Mono<Candidate> createCandidate(Candidate body) {
        return Mono.fromCallable(() -> {
            sendMessage("candidates-out-0", new Event<Integer, Candidate>(CREATE, body.getCandidateId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Candidate> getCandidate(int candidateId) {
        String url = candidateServiceUrl + "/candidate/" + candidateId;
        return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToMono(Candidate.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public Mono<Void> deleteCandidate(int candidateId) {
        return Mono.fromRunnable(() -> sendMessage(
            "candidates-out-0",
            new Event<Integer, Candidate>(DELETE, candidateId, null)
        ))
        .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<Comment> createComment(Comment body) {
        return Mono.fromCallable(() -> {
            sendMessage("comments-out-0", new Event<Integer, Comment>(CREATE, body.getCandidateId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Flux<Comment> getComments(int candidateId) {
        String url = commentServiceUrl + "/comment?candidateId=" + candidateId;
        return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToFlux(Comment.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorResume(error -> empty());
    }

    @Override
    public Mono<Void> deleteComments(int candidateId) {
        return Mono.fromRunnable(() -> sendMessage(
            "comments-out-0",
            new Event<Integer, Comment>(DELETE, candidateId, null)
        ))
        .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Mono<NewsArticle> createNewsArticle(NewsArticle body) {
        return Mono.fromCallable(() -> {
            sendMessage("newsarticles-out-0", new Event<Integer, NewsArticle>(CREATE, body.getCandidateId(), body));
            return body;
        })
        .subscribeOn(publishEventScheduler);
    }

    @Override
    public Flux<NewsArticle> getNewsArticles(int candidateId) {
        String url = newsArticleServiceUrl + "/news-article?candidateId=" + candidateId;
        return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToFlux(NewsArticle.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorResume(error -> empty());
    }

    @Override
    public Mono<Void> deleteNewsArticle(int candidateId) {
        return Mono.fromRunnable(() -> sendMessage(
            "newsarticles-out-0",
            new Event<Integer, NewsArticle>(DELETE, candidateId, null)
        ))
        .subscribeOn(publishEventScheduler)
        .then();
    }

    public Mono<Health> getCandidateHealth() {
        LOG.debug("candidate url:" + candidateServiceUrl);
        return getHealth(candidateServiceUrl);
    }

    public Mono<Health> getCommentHealth() {
        LOG.debug("comment url:" + candidateServiceUrl);
        return getHealth(commentServiceUrl);
    }

    public Mono<Health> getNewsArticleHealth() {
        LOG.debug("newsArticle url:" + candidateServiceUrl);
        return getHealth(newsArticleServiceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .map(s -> new Health.Builder().up().build())
        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
        .log(LOG.getName(), Level.FINE);
    }

    private void sendMessage(String bindingName, Event<?, ?> event) {
        Message<?> message = MessageBuilder.withPayload(event)
        .setHeader("partitionKey", event.getKey())
        .build();
        streamBridge.send(bindingName, message);
    }
    
    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }
        WebClientResponseException wcre = (WebClientResponseException)ex;
        switch (HttpStatus.resolve(wcre.getStatusCode().value())) {
            case NOT_FOUND:
                return  new NotFoundException(getErrorMessage(wcre));
            case UNPROCESSABLE_ENTITY:
                return  new InvalidInputException(getErrorMessage(wcre));
            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex){
        try {
            LOG.info("getErrorMessage");
            LOG.info(ex.getResponseBodyAsString());
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}
