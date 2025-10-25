package pe.elections.microservices.api.composite.candidate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CandidateCompositeService {

    @GetMapping(
        value = "/candidate-composite/{candidateId}",
        produces = "application/json"
    )
    CandidateAggregate getCandidate(@PathVariable int candidateId);
}
