package pe.elections.microservices.core.newsarticle.services;

import static java.util.logging.Level.FINE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DataIntegrityViolationException;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.core.newsarticle.NewsArticleService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleEntity;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleRepository;
import pe.elections.microservices.util.http.ServiceUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class NewsArticleServiceImpl implements NewsArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsArticleServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final NewsArticleRepository repository;
    private final NewsArticleMapper mapper;

    private final Scheduler jdbcScheduler;

    @Autowired
    public NewsArticleServiceImpl(
        ServiceUtil serviceUtil,
        NewsArticleRepository repository,
        NewsArticleMapper mapper,
        Scheduler jdbcScheduler
    ) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<NewsArticle> createNewsArticle(NewsArticle body) {
        if (body.getCandidateId() < 1) {
            throw new InvalidInputException("Invalid candidateId: " + body.getCandidateId());
        }
        LOG.info("===MESSAGE SERVICE CREATED FUNCTION===");
        return Mono.fromCallable(() -> internalCreateNewsArticle(body))
        .subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<NewsArticle> getNewsArticles(int candidateId) {
        LOG.debug("valor: " + candidateId);
        if (candidateId < 1) {
            LOG.debug("valor negativo");
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        return Mono.fromCallable(() -> internalGetNewsArticles(candidateId))
        .flatMapMany(Flux::fromIterable)
        .log(LOG.getName(), FINE)
        .subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<Void> deleteNewsArticle(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        return Mono.fromRunnable(() -> internalDeleteNewsArticle(candidateId)).subscribeOn(jdbcScheduler).then();
    }

    private NewsArticle internalCreateNewsArticle(NewsArticle body) {
        try {
            LOG.info("Created");
            NewsArticleEntity entity = mapper.apiToEntity(body);
            NewsArticleEntity newEntity = repository.save(entity);
            LOG.info("New entity:" + newEntity.toString());
            LOG.info("candidateId: " + newEntity.getCandidateId());
            return mapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId() + ", NewsArticle Id: " + body.getNewsArticleId());
        }
    }
    private List<NewsArticle> internalGetNewsArticles(int candidateId) {
        List<NewsArticleEntity> entityList = repository.findByCandidateId(candidateId);
        List<NewsArticle> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getNewsArticle: response size: {}", list.size());
        return list;
    }
    public void internalDeleteNewsArticle(int candidateId) {
        LOG.debug("deleteNewsArticle: tries to delete newsArticle for the candidate with candidateId: {}", candidateId);
        repository.deleteAll(repository.findByCandidateId(candidateId));
    }
}
