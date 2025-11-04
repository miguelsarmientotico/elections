package pe.elections.microservices.composite.candidate.services;

import java.util.List;
import java.util.stream.Collectors;

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

@RestController
public class CandidateCompositeServiceImpl implements CandidateCompositeService {
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
    public CandidateAggregate getCandidate(int candidateId) {
        Candidate candidate = integration.getCandidate(candidateId);
        if (candidate == null) {
            throw new NotFoundException("No candidate found for candidateId: " + candidateId);
        }
        List<Comment> comments = integration.getComments(candidateId);
        List<NewsArticle> newsArticles = integration.getNewsArticles(candidateId);
        return createCandidateAggregate(candidate, comments, newsArticles, serviceUtil.getServiceAddress());
    }

    @Override
    public void createCandidate(CandidateAggregate body) {
        try {
            Candidate candidate = new Candidate(body.getCandidateId(), body.getName(), body.getEdad(), null);
            integration.createCandidate(candidate);
            if (body.getComments() != null) {
                body.getComments().forEach(r -> {
                    Comment comment = new Comment(body.getCandidateId(), r.getCommentId(), r.getContent(), r.getAuthor(), r.getCreatedAt(), null);
                    integration.createComment(comment);
                });
            }
            if (body.getNewsArticles() != null) {
                body.getNewsArticles().forEach(r -> {
                    NewsArticle newsArticle = new NewsArticle(body.getCandidateId(), r.getNewsArticleId(), r.getTitle(), r.getContent(), r.getAuthor(), r.getPublishDate(), r.getCategory(), null);
                    integration.createNewsArticle(newsArticle);
                });
            }
        } catch (RuntimeException re) {
            throw re;
        }
    }

    @Override
    public void deleteCandidate(int candidateId) {
        integration.deleteCandidate(candidateId);
        integration.deleteComments(candidateId);
        integration.deleteNewsArticle(candidateId);
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
