package pe.elections.microservices.core.candidate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import pe.elections.microservices.core.candidate.persistence.CandidateEntity;
import pe.elections.microservices.core.candidate.persistence.CandidateRepository;
import reactor.test.StepVerifier;

@DataMongoTest
class PersistenceTests extends MongoDbTestBase {
    @Autowired
    private CandidateRepository repository;

    private CandidateEntity savedEntity;

    @BeforeEach
    void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        CandidateEntity entity = new CandidateEntity(1, "name Candidate", 30);
        StepVerifier.create(repository.save(entity))
            .expectNextMatches(createdEntity -> {
                savedEntity = createdEntity;
                return areCandidateEqual(entity, savedEntity);
            })
            .verifyComplete();
    }

    @Test
    void create() {
        CandidateEntity newEntity = new CandidateEntity(2, "name 2 candidate", 31);
        StepVerifier.create(repository.save(newEntity))
            .expectNextMatches(createdEntity -> newEntity.getCandidateId() == createdEntity.getCandidateId())
            .verifyComplete();
        StepVerifier.create(repository.findById(newEntity.getId()))
            .expectNextMatches(foundEntity -> areCandidateEqual(newEntity, foundEntity))
            .verifyComplete();
        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void update() {
        savedEntity.setName("n2");
        StepVerifier.create(repository.save(savedEntity))
            .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
            .verifyComplete();
        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                foundEntity.getVersion() == 1
                && foundEntity.getName().equals("n2"))
            .verifyComplete();
    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId()))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void getByCandidateId() {
        StepVerifier.create(repository.findByCandidateId(savedEntity.getCandidateId()))
            .expectNextMatches(foundEntity -> areCandidateEqual(savedEntity, foundEntity))
            .verifyComplete();
    }

    @Test
    void duplicateError() {
        CandidateEntity entity = new CandidateEntity(savedEntity.getCandidateId(), "name 2 candidate", 31);
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
    void optimisticLockError() {
        CandidateEntity entity1 = repository.findById(savedEntity.getId()).block();
        CandidateEntity entity2 = repository.findById(savedEntity.getId()).block();
        entity1.setName("abc");
        repository.save(entity1).block();

        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();
        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity -> 
                foundEntity.getVersion() == 1
                && foundEntity.getName().equals("abc")
            )
            .verifyComplete();
    }

    private boolean areCandidateEqual(CandidateEntity expectedEntity, CandidateEntity actualEntity) {
        return (expectedEntity.getId().equals(actualEntity.getId()))
        && (expectedEntity.getVersion() == actualEntity.getVersion())
        && (expectedEntity.getCandidateId() == actualEntity.getCandidateId())
        && (expectedEntity.getName().equals(actualEntity.getName()))
        && (expectedEntity.getEdad() == actualEntity.getEdad());
    }
}
