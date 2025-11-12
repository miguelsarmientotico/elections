package pe.elections.microservices.core.comment;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import pe.elections.microservices.core.comment.persistence.CommentEntity;
import pe.elections.microservices.core.comment.persistence.CommentRepository;

@DataMongoTest
class PersistenceTests extends MongoDbTestBase {

    @Autowired
    private CommentRepository repository;

    private CommentEntity savedEntity;

    private Instant instant = LocalDateTime.of(2024, 1, 15, 14, 30, 0).atZone(ZoneId.of("UTC")).toInstant();

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
        CommentEntity entity = new CommentEntity(1, 1, "Content Comment", "Author Comment", instant);
        savedEntity = repository.save(entity).block();
        assertEqualsComment(entity, savedEntity);
    }

    @Test
    void create() {
        CommentEntity newEntity = new CommentEntity(1, 2, "Content comment 2", "author 2", instant);
        repository.save(newEntity).block();
        CommentEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsComment(newEntity, foundEntity);
        assertEquals(2, repository.count().block());
    }

    @Test
    void update() {
        savedEntity.setAuthor("a2");
        repository.save(savedEntity).block();
        CommentEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    void delete () {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
    void getByCandidateId() {
        List<CommentEntity> entityList = repository.findByCandidateId(savedEntity.getCandidateId()).collectList().block();
        assertEquals(1, entityList.size());
        assertEqualsComment(savedEntity, entityList.get(0));
    }

    @Test
    void duplicateError() {
        assertThrows(DuplicateKeyException.class, () -> {
            CommentEntity entity = new CommentEntity(1, 1, "Content Comment", "Author Comment", instant);
            repository.save(entity).block();
        });
    }

    @Test
    void debugDuplicateError() {
        System.out.println("=== DEBUG DUPLICATE ERROR ===");

        // 1. Verificar cuántos documentos hay
        long countBefore = repository.count().block();
        System.out.println("Documentos en BD antes: " + countBefore);

        // 2. Verificar qué documentos existen
        Iterable<CommentEntity> allCommentsIterable = repository.findAll().collectList().block();
        List<CommentEntity> allComments = new ArrayList<>();
        allCommentsIterable.forEach(allComments::add);
        System.out.println("Documentos encontrados:");
        allComments.forEach(comment -> {
            System.out.println("  - ID: " + comment.getId() + 
                ", candidateId: " + comment.getCandidateId() + 
                ", commentId: " + comment.getCommentId());
        });

        // 3. Intentar crear duplicado
        try {
            CommentEntity duplicate = new CommentEntity(1, 1, "DUPLICATE CONTENT", "DUPLICATE AUTHOR", instant);
            System.out.println("Intentando guardar duplicado: candidateId=1, commentId=1");

            CommentEntity savedDuplicate = repository.save(duplicate).block();
            System.out.println("¡DUPLICADO GUARDADO EXITOSAMENTE! ID: " + savedDuplicate.getId());
            System.out.println("Esto significa que NO HAY ÍNDICE ÚNICO");

        } catch (DuplicateKeyException e) {
            System.out.println("¡DUPLICADO BLOQUEADO! Índice funciona correctamente");
            System.out.println("Error: " + e.getMessage());
        }

        // 4. Verificar contador final
        System.out.println("Documentos en BD después: " + repository.count());
    }

    @Test
    void optimisticLockError() {
        CommentEntity entity1 = repository.findById(savedEntity.getId()).block();
        CommentEntity entity2 = repository.findById(savedEntity.getId()).block();
        entity1.setAuthor("a2");
        repository.save(entity1).block();

        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setAuthor("a3");
            repository.save(entity2).block();
        });
        CommentEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a2", updatedEntity.getAuthor());
    }

    private void assertEqualsComment(CommentEntity expectedEntity, CommentEntity actualEntity) {
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getCandidateId(), actualEntity.getCandidateId());
        assertEquals(expectedEntity.getCommentId(), actualEntity.getCommentId());
        assertEquals(expectedEntity.getContent(), actualEntity.getContent());
        assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
        assertEquals(expectedEntity.getCreatedAt(), actualEntity.getCreatedAt());
    }

}
