package pe.elections.microservices.api.core.candidate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

public interface CandidateService {

    Mono<Candidate> createCandidate(Candidate body);

    @GetMapping(
        value = "/candidate/{candidateId}",
        produces = "application/json")
    Mono<Candidate> getCandidate(@PathVariable int candidateId);

    Mono<Void> deleteCandidate(int candidateId);

}
