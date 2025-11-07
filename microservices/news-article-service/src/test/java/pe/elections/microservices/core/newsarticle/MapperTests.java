package pe.elections.microservices.core.newsarticle;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleEntity;
import pe.elections.microservices.core.newsarticle.services.NewsArticleMapper;

class MapperTests {
    private NewsArticleMapper mapper = Mappers.getMapper(NewsArticleMapper.class);

    @Test
    void mapperTests() {
        System.out.println("=== EJECUTANDO MAPPER TESTS ===");
        System.out.println("Mapper instance: " + mapper);
        System.out.println("Mapper class: " + mapper.getClass());
        assertNotNull(mapper);
        NewsArticle api = new NewsArticle(1, 2, "a", "b", "c", LocalDateTime.now(), "d", "adr");
        NewsArticleEntity entity = mapper.apiToEntity(api);
        assertEquals(api.getCandidateId(), entity.getCandidateId());
        assertEquals(api.getNewsArticleId(), entity.getNewsArticleId());
        assertEquals(api.getTitle(), entity.getTitle());
        assertEquals(api.getContent(), entity.getContent());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getPublishDate(), entity.getPublishDate());
        assertEquals(api.getCategory(), entity.getCategory());
        NewsArticle api2 = mapper.entityToApi(entity);
        assertEquals(api.getCandidateId(), api2.getCandidateId());
        assertEquals(api.getNewsArticleId(), api2.getNewsArticleId());
        assertEquals(api.getTitle(), api2.getTitle());
        assertEquals(api.getContent(), api2.getContent());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getPublishDate(), api2.getPublishDate());
        assertEquals(api.getCategory(), api2.getCategory());
        assertNull(api2.getServiceAddress());
    }

    @Test
    void mapperListTests() {
        assertNotNull(mapper);

        LocalDateTime publishDate = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        NewsArticle api = new NewsArticle(1, 2, "a", "b", "c", publishDate, "d", "adr");
        List<NewsArticle> apiList = Collections.singletonList(api);
        List<NewsArticleEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());
        NewsArticleEntity entity = entityList.get(0);
        assertEquals(api.getCandidateId(), entity.getCandidateId());
        assertEquals(api.getNewsArticleId(), entity.getNewsArticleId());
        assertEquals(api.getTitle(), entity.getTitle());
        assertEquals(api.getContent(), entity.getContent());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getPublishDate(), entity.getPublishDate());
        assertEquals(api.getCategory(), entity.getCategory());
        List<NewsArticle> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());
        NewsArticle api2 = api2List.get(0);
        assertEquals(api.getCandidateId(), api2.getCandidateId());
        assertEquals(api.getNewsArticleId(), api2.getNewsArticleId());
        assertEquals(api.getTitle(), api2.getTitle());
        assertEquals(api.getContent(), api2.getContent());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getPublishDate(), api2.getPublishDate());
        assertEquals(api.getCategory(), api2.getCategory());
        assertNull(api2.getServiceAddress());
    }
}
