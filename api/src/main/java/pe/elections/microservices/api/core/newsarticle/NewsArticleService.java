package pe.elections.microservices.api.core.newsarticle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NewsArticleService {

    Mono<NewsArticle> createNewsArticle(NewsArticle body);

    @GetMapping(
        value = "/news-article",
        produces = "application/json"
    )
    Flux<NewsArticle> getNewsArticles(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

    Mono<Void> deleteNewsArticle(int candidateId);

}
