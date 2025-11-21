package pe.elections.microservices.composite.candidate.services;

import static java.util.logging.Level.FINE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.composite.candidate.CandidateAggregate;
import pe.elections.microservices.api.composite.candidate.CandidateCompositeService;
import pe.elections.microservices.api.composite.candidate.CommentSummary;
import pe.elections.microservices.api.composite.candidate.NewsArticleSummary;
import pe.elections.microservices.api.composite.candidate.ServiceAddresses;
import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.util.http.ServiceUtil;
import reactor.core.publisher.Mono;

@RestController
public class CandidateCompositeServiceImpl implements CandidateCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateCompositeServiceImpl.class);

    private final SecurityContext nullSecCtx = new SecurityContextImpl();

    private final ServiceUtil serviceUtil;
    private final CandidateCompositeIntegration integration;

    public CandidateCompositeServiceImpl(
        ServiceUtil serviceUtil,
        CandidateCompositeIntegration integration
    ){
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<CandidateAggregate> getCandidate(int candidateId) {
        return Mono.zip(
            values -> createCandidateAggregate(
                (SecurityContext) values[0],
                (Candidate) values[1],
                (List<Comment>) values[2], 
                (List<NewsArticle>) values[3],
                serviceUtil.getServiceAddress()
            ),
            getSecurityContextMono(),
            integration.getCandidate(candidateId),
            integration.getComments(candidateId).collectList(),
            integration.getNewsArticles(candidateId).collectList()
        )
        .doOnError(ex -> LOG.warn("getCompositeCandidate failed: {}", ex.toString()))
        .log(LOG.getName(), FINE);
    }

    @Override
    public Mono<Void> createCandidate(CandidateAggregate body) {
            LOG.warn("creando para testing");
        try {
            List<Mono<?>> monoList = new ArrayList<>();
            monoList.add(getLogAuthorizationInfoMono());
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
                getLogAuthorizationInfoMono(),
                integration.deleteCandidate(candidateId),
                integration.deleteComments(candidateId),
                integration.deleteNewsArticle(candidateId)
            ) 
            .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
            .log(LOG.getName(), FINE)
            .then();
        } catch (RuntimeException re) {
            throw re;
        }
    }

    private CandidateAggregate createCandidateAggregate(
        SecurityContext sc,
        Candidate candidate,
        List<Comment> comments,
        List<NewsArticle> newsArticles,
        String serviceAddress
    ) {
        logAuthorizationInfo(sc);
        int candidateId = candidate.getCandidateId();
        String name = candidate.getName();
        int edad = candidate.getEdad();
        List<CommentSummary> commentSummaries =
            (comments == null)
                ? null
                : comments.stream()
                    .map(c -> new CommentSummary(c.getCommentId(), c.getContent(), c.getAuthor(), c.getCreatedAt()))
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

    private Mono<SecurityContext> getLogAuthorizationInfoMono() {
        return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo(sc));
    }
    private Mono<SecurityContext> getSecurityContextMono() {
        return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
    }
    private void logAuthorizationInfo(SecurityContext sc) {
        if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
            Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
            logAuthorizationInfo(jwtToken);
        } else {
            LOG.warn("No se ha proporcionado ninguna autenticación basada en JWT, ¿estamos realizando pruebas?");
        }
    }
    private void logAuthorizationInfo(Jwt jwt) {
        if (jwt == null) {
            LOG.warn("No se proporciono ninguna jwt, corriendo pruebas");
        } else {
            if (LOG.isDebugEnabled()) {
                URL issuer = jwt.getIssuer();
                List<String> audience = jwt.getAudience();
                Object subject = jwt.getClaims().get("sub");
                Object scopes = jwt.getClaims().get("scope");
                Object expires = jwt.getClaims().get("exp");
                LOG.debug("Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
            }
        }
    }
}
