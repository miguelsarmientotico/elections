package pe.elections.microservices.core.newsarticle.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.core.newsarticle.NewsArticleService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.util.http.ServiceUtil;

@RestController
public class NewsArticleServiceImpl implements NewsArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsArticleServiceImpl.class);
    private final ServiceUtil serviceUtil;

    @Autowired
    public NewsArticleServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<NewsArticle> getNewsArticles(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        if (candidateId == 113) {
            LOG.debug("No news found for candidateId: {}", candidateId);
            return new ArrayList<>();
        }
        List<NewsArticle> list = new ArrayList<>();
        list.add(new NewsArticle(candidateId, 1, "Noticia 1", "noticia contenido 1", "autor 1", LocalDateTime.now(), "farandula", serviceUtil.getServiceAddress()));
        LOG.debug("/news-article response size: {}", list.size());
        return list;
    }

}
