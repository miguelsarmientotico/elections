package pe.elections.microservices.core.candidate.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface CandidateRepository extends ReactiveCrudRepository<CandidateEntity, String> {
    Mono<CandidateEntity> findByCandidateId(int candidateId);
}
