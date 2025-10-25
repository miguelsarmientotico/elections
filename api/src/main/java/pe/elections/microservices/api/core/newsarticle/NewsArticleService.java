package pe.elections.microservices.api.core.newsarticle;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface NewsArticleService {

    @GetMapping(
        value = "/news-article",
        produces = "application/json"
    )
    List<NewsArticle> getNewsArticles(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

}
