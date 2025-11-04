package pe.elections.microservices.api.core.newsarticle;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface NewsArticleService {

    @PostMapping(
        value = "/news-article",
        consumes = "application/json",
        produces = "application/json"
    )
    NewsArticle createNewsArticle(@RequestBody NewsArticle body);

    @GetMapping(
        value = "/news-article",
        produces = "application/json"
    )
    List<NewsArticle> getNewsArticles(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

    @DeleteMapping(value = "/news-article")
    void deleteNewsArticle(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

}
