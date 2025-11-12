package pe.elections.microservices.core.comment.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveCrudRepository<CommentEntity, String> {
    Flux<CommentEntity> findByCandidateId(int candidateId);
}
