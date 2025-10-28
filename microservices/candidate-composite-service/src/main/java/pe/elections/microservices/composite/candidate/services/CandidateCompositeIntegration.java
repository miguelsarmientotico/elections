package pe.elections.microservices.composite.candidate.services;

import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.candidate.CandidateService;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.core.newsarticle.NewsArticleService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.util.http.HttpErrorInfo;

@Component
public class CandidateCompositeIntegration implements CandidateService, CommentService, NewsArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateCompositeIntegration.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String candidateServiceUrl;
    private final String commentServiceUrl;
    private final String newsArticleServiceUrl;

    @Autowired
    public CandidateCompositeIntegration(
        RestTemplate restTemplate,
        ObjectMapper mapper,
        @Value("${app.candidate-service.host}") String candidateServiceHost,
        @Value("${app.candidate-service.port}") String candidateServicePort,
        @Value("${app.comment-service.host}") String commentServiceHost,
        @Value("${app.comment-service.port}") String commentServicePort,
        @Value("${app.newsarticle-service.host}") String newsArticleServiceHost,
        @Value("${app.newsarticle-service.port}") String newsArticleServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        candidateServiceUrl = "http://" + candidateServiceHost + ":" + candidateServicePort + "/candidate/";
        commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment?candidateId=";
        newsArticleServiceUrl = "http://" + newsArticleServiceHost + ":" + newsArticleServicePort + "/news-article?candidateId=";
    }

    public Candidate getCandidate(int candidateId) {
        try {
            String url = candidateServiceUrl + candidateId;
            LOG.debug("Will call getCandidate API on URL: {}", url);
            Candidate candidate = restTemplate.getForObject(url, Candidate.class);
            LOG.info("Response of the API for {}: {}", url, candidate);
            LOG.debug("Found a candidate with id: {}", candidate.getCandidateId());
            return candidate;
        } catch (HttpClientErrorException ex) {
            switch (HttpStatus.resolve(ex.getStatusCode().value())) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex){
        try {
            LOG.info("getErrorMessage");
            LOG.info(ex.getResponseBodyAsString());
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


    @Override
    public List<Comment> getComments(int candidateId) {
        try {
            String url = commentServiceUrl + candidateId;
            LOG.debug("Will call getComments API on URL: {}", url);
            List<Comment> comments = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Comment>>(){}).getBody();
            LOG.debug("Found {} comments for a candidate with id: {}", comments.size(), candidateId);
            return comments;
        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting comments, return zero news: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<NewsArticle> getNewsArticles(int candidateId) {
        try {
            String url = newsArticleServiceUrl + candidateId;
            LOG.debug("Will call getNewsArticles API on URL: {}", url);
            List<NewsArticle> newsArticles = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<NewsArticle>>(){}).getBody();
            LOG.debug("Found {} news for a candidate with id: {}", newsArticles.size(), candidateId);
            return newsArticles;
        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting news, return zero news: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

}
