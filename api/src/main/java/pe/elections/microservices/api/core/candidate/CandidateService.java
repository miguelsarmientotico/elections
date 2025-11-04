package pe.elections.microservices.api.core.candidate;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CandidateService {

    @PostMapping(
        value = "/candidate",
        produces = "application/json",
        consumes = "application/json"
    )
    Candidate createCandidate(@RequestBody Candidate body);

    @GetMapping(
    value = "/candidate/{candidateId}",
    produces = "application/json"
    )
    Candidate getCandidate(@PathVariable int candidateId);

    @DeleteMapping(value = "/candidate/{candidateId}")
    void deleteCandidate(@PathVariable int candidateId);

}
