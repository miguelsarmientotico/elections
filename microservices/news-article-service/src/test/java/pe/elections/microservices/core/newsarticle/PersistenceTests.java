package pe.elections.microservices.core.newsarticle;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.elections.microservices.core.newsarticle.persistence.NewsArticleEntity;
import pe.elections.microservices.core.newsarticle.persistence.NewsArticleRepository;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersistenceTests extends MySqlTestBase {


    @Autowired
    private NewsArticleRepository repository;

    private NewsArticleEntity savedEntity; 

    private Instant instant = LocalDateTime.of(2024, 1, 15, 14, 30, 0).atZone(ZoneId.of("UTC")).toInstant();



    @BeforeEach
    void setupDb() {
        repository.deleteAll();
        NewsArticleEntity entity = new NewsArticleEntity(
            1,
            2,
            "titulo del articulo",
            "contenido del articulo",
            "autor del articulo",
            instant,
            "farandula"
        );
        savedEntity = repository.save(entity);
        assertEqualsNewsArticle(entity, savedEntity);
    }


    @Test
    void create() {
        NewsArticleEntity newEntity = new NewsArticleEntity(
            1,
            3,
            "a",
            "s",
            "c",
            instant,
            "c"
        );
        repository.save(newEntity);

        NewsArticleEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsNewsArticle(newEntity, foundEntity);

        assertEquals(2, repository.count());

    }

    @Test
    void update() {
        savedEntity.setAuthor("nuevo autor");
        repository.save(savedEntity);
        NewsArticleEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("nuevo autor", foundEntity.getAuthor());
    }

    @Test
    void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    void getByCandidateId() {
        List<NewsArticleEntity> entityList = repository.findByCandidateId(savedEntity.getCandidateId());
        assertEquals(1, entityList.size());
        assertEqualsNewsArticle(savedEntity, entityList.get(0));
    }

    @Test
    void duplicateError() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            NewsArticleEntity entity = new NewsArticleEntity(
                1,
                2,
                "titulo del articulo",
                "contenido del articulo",
                "autor del articulo",
                instant,
                "farandula"
            );
            repository.save(entity);
        });
    }

    @Test
    void optimisticLockError() {
        NewsArticleEntity entity1 = repository.findById(savedEntity.getId()).get();
        NewsArticleEntity entity2 = repository.findById(savedEntity.getId()).get();
        entity1.setAuthor("a1");
        repository.save(entity1);
        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setAuthor("a2");
            repository.save(entity2);
        });
        NewsArticleEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsNewsArticle(NewsArticleEntity expectedEntity, NewsArticleEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getCandidateId(), actualEntity.getCandidateId());
        assertEquals(expectedEntity.getNewsArticleId(), actualEntity.getNewsArticleId());
        assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
        assertEquals(expectedEntity.getContent(), actualEntity.getContent());
        assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
        assertEquals(expectedEntity.getPublishDate(), actualEntity.getPublishDate());
        assertEquals(expectedEntity.getCategory(), actualEntity.getCategory());
    }
}
