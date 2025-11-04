package pe.elections.microservices.core.newsarticle.services;

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

@RestController
public class NewsArticleServiceImpl implements NewsArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsArticleServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final NewsArticleRepository repository;
    private final NewsArticleMapper mapper;

    @Autowired
    public NewsArticleServiceImpl(
        ServiceUtil serviceUtil,
        NewsArticleRepository repository,
        NewsArticleMapper mapper
    ) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public NewsArticle createNewsArticle(NewsArticle body) {
        try {
            LOG.debug("apicandidateId {}", body.getCandidateId());
            LOG.debug("apinewsArticleId {}", body.getNewsArticleId());
            LOG.debug("apititle {}", body.getTitle());
            LOG.debug("apicontent {}", body.getContent());
            LOG.debug("apiauthor {}", body.getAuthor());
            LOG.debug("apipublishDate {}", body.getPublishDate());
            LOG.debug("apicategory {}", body.getCategory());
            NewsArticleEntity entity = mapper.apiToEntity(body);
            LOG.debug("candidateId {}", entity.getCandidateId());
            LOG.debug("newsArticleId {}", entity.getNewsArticleId());
            LOG.debug("title {}", entity.getTitle());
            LOG.debug("content {}", entity.getContent());
            LOG.debug("author {}", entity.getAuthor());
            LOG.debug("publishDate {}", entity.getPublishDate());
            LOG.debug("category {}", entity.getCategory());
            NewsArticleEntity newEntity = repository.save(entity);
            LOG.debug("createNewsArticle: created a newsArticle entity: {}/{}", body.getCandidateId(), body.getNewsArticleId());
            return mapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId() + ", NewsArticle Id: " + body.getNewsArticleId());
        }
    }

    @Override
    public List<NewsArticle> getNewsArticles(int candidateId) {
        LOG.debug("valor: " + candidateId);
        if (candidateId < 1) {
            LOG.debug("valor negativo");
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        List<NewsArticleEntity> entityList = repository.findByCandidateId(candidateId);
        List<NewsArticle> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getNewsArticle: response size: {}", list.size());
        return list;
    }

    @Override
    public void deleteNewsArticle(int candidateId) {
        LOG.debug("deleteNewsArticle: tries to delete newsArticle for the candidate with candidateId: {}", candidateId);
        repository.deleteAll(repository.findByCandidateId(candidateId));
    }

}
