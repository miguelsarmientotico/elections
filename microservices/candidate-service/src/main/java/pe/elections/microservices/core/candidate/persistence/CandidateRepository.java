package pe.elections.microservices.core.candidate.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CandidateRepository extends PagingAndSortingRepository<CandidateEntity, String>, CrudRepository<CandidateEntity, String> {

    Optional<CandidateEntity> findByCandidateId(int candidateId);
}
