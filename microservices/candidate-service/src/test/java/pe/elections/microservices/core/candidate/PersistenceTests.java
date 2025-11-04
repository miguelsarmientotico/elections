package pe.elections.microservices.core.candidate;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import pe.elections.microservices.core.candidate.persistence.CandidateEntity;
import pe.elections.microservices.core.candidate.persistence.CandidateRepository;

@DataMongoTest
class PersistenceTests extends MongoDbTestBase {
    @Autowired
    CandidateRepository repository;

    CandidateEntity savedEntity;

    @BeforeEach
    void setupDb() {
        repository.deleteAll();
        CandidateEntity entity = new CandidateEntity(1, "name Candidate", 30);
        savedEntity = repository.save(entity);
        assertEqualsCandidate(entity, savedEntity);
    }

    @Test
    void create() {
        CandidateEntity newEntity = new CandidateEntity(2, "name 2 candidate", 31);
        repository.save(newEntity);
        CandidateEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsCandidate(newEntity, foundEntity);
        assertEquals(2, repository.count());
    }

    @Test
    void update() {
        savedEntity.setName("c2");
        repository.save(savedEntity);
        CandidateEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, foundEntity.getVersion());
        assertEquals("c2", foundEntity.getName());
    }

    @Test
    void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    void getByCandidateId() {
        Optional<CandidateEntity> entity = repository.findByCandidateId(savedEntity.getCandidateId());
        assertTrue(entity.isPresent());
        assertEqualsCandidate(savedEntity, entity.get());
    }

    @Test
    void duplicateError() {
        assertThrows(DuplicateKeyException.class, () -> {
            CandidateEntity entity = new CandidateEntity(1, "ca", 29);
            repository.save(entity);
        });
    }

    @Test
    void optimisticLockError() {
        CandidateEntity entity1 = repository.findById(savedEntity.getId()).get();
        CandidateEntity entity2 = repository.findById(savedEntity.getId()).get();
        entity1.setName("abc");
        repository.save(entity1);
        assertThrows(OptimisticLockingFailureException.class, () -> {
            entity2.setName("zxc");
            repository.save(entity2);
        });
        CandidateEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("abc", updatedEntity.getName());
    }

    private void assertEqualsCandidate(CandidateEntity expectedEntity, CandidateEntity actualEntity) {
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getCandidateId(), actualEntity.getCandidateId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getEdad(), actualEntity.getEdad());
    }
}
