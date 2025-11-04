package pe.elections.microservices.core.newsarticle.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleEntity;

@Mapper(componentModel = "spring")
public interface NewsArticleMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    NewsArticle entityToApi(NewsArticleEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    NewsArticleEntity apiToEntity(NewsArticle api);

    List<NewsArticle> entityListToApiList(List<NewsArticleEntity> entity);

    List<NewsArticleEntity> apiListToEntityList(List<NewsArticle> api);

}
