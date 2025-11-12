package pe.elections.microservices.composite.candidate.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.composite.candidate.CandidateAggregate;
import pe.elections.microservices.api.composite.candidate.CandidateCompositeService;
import pe.elections.microservices.api.composite.candidate.CommentSummary;
import pe.elections.microservices.api.composite.candidate.NewsArticleSummary;
import pe.elections.microservices.api.composite.candidate.ServiceAddresses;
import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.util.http.ServiceUtil;
import reactor.core.publisher.Mono;

@RestController
public class CandidateCompositeServiceImpl implements CandidateCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private CandidateCompositeIntegration integration;

    @Autowired
    public CandidateCompositeServiceImpl(
        ServiceUtil serviceUtil,
        CandidateCompositeIntegration integration
    ){
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public Mono<CandidateAggregate> getCandidate(int candidateId) {
        return Mono.zip(
            values -> createCandidateAggregate(
                (Candidate) values[0],
                (List<Comment>) values[1], 
                (List<NewsArticle>) values[2],
                serviceUtil.getServiceAddress()
            ),
            integration.getCandidate(candidateId),
            integration.getComments(candidateId).collectList(),
            integration.getNewsArticles(candidateId).collectList()
        )
        .doOnError(ex -> LOG.warn("getCompositeCandidate failed: {}", ex.toString()))
        .log(LOG.getName(), Level.FINE);
    }

    @Override
    public Mono<Void> createCandidate(CandidateAggregate body) {
            LOG.warn("creando para testing");
        try {
            List<Mono<?>> monoList = new ArrayList<>();
            Candidate candidate = new Candidate(body.getCandidateId(), body.getName(), body.getEdad(), null);
            monoList.add(integration.createCandidate(candidate));

            if (body.getComments() != null) {
                body.getComments().forEach(r -> {
                    Comment comment = new Comment(body.getCandidateId(), r.getCommentId(), r.getContent(), r.getAuthor(), r.getCreatedAt(), null);
                    monoList.add(integration.createComment(comment));
                });
            }
            if (body.getNewsArticles() != null) {
                body.getNewsArticles().forEach(r -> {
                    NewsArticle newsArticle = new NewsArticle(body.getCandidateId(), r.getNewsArticleId(), r.getTitle(), r.getContent(), r.getAuthor(), r.getPublishDate(), r.getCategory(), null);
                    monoList.add(integration.createNewsArticle(newsArticle));
                });
            }
            return Mono
            .zip(
                r -> "",
                monoList.toArray(new Mono[0])
            )
            .doOnError(ex -> LOG.warn("createCompositeCandidate failed: {}", ex.toString()))
            .then();
        } catch (RuntimeException re) {
            throw re;
        }
    }

    @Override
    public Mono<Void> deleteCandidate(int candidateId) {
        try {
            return Mono.zip(
                r -> "",
                integration.deleteCandidate(candidateId),
                integration.deleteComments(candidateId),
                integration.deleteNewsArticle(candidateId)
            ) 
            .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
            .log(LOG.getName(), Level.FINE)
            .then();
        } catch (RuntimeException re) {
            throw re;
        }
    }

    private CandidateAggregate createCandidateAggregate(
        Candidate candidate,
        List<Comment> comments,
        List<NewsArticle> newsArticles,
        String serviceAddress
    ) {
        int candidateId = candidate.getCandidateId();
        String name = candidate.getName();
        int edad = candidate.getEdad();
        List<CommentSummary> commentSummaries =
            (comments == null)
                ? null
                : comments.stream()
                    .map(c -> new CommentSummary(c.getCandidateId(), c.getContent(), c.getAuthor(), c.getCreatedAt()))
                    .collect(Collectors.toList());
        List<NewsArticleSummary> newsArticleSummaries =
            (newsArticles == null)
                ? null
                : newsArticles.stream()
                    .map(n -> new NewsArticleSummary(n.getNewsArticleId(), n.getTitle(), n.getContent(), n.getAuthor(), n.getPublishDate(), n.getCategory()))
                    .collect(Collectors.toList());
        String candidateAddress = candidate.getServiceAddress();
        String commentAddress = (comments != null && comments.size() > 0) ? comments.get(0).getServiceAddress() : "";
        String newsArticleAddress = (newsArticles != null && newsArticles.size() > 0) ? newsArticles.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, candidateAddress, commentAddress, newsArticleAddress);
        return new CandidateAggregate(candidateId, name, edad, commentSummaries, newsArticleSummaries, serviceAddresses);
    }
}
