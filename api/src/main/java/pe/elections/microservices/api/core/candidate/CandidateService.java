package pe.elections.microservices.api.core.candidate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CandidateService {

  /**
   * Sample usage: "curl $HOST:$PORT/candidate/1".
   *
   * @param candidateId Id of the candidate
   * @return the candidate, if found, else null
   */
    @GetMapping(
    value = "/candidate/{candidateId}",
    produces = "application/json"
    )
    Candidate getCandidate(@PathVariable int candidateId);

}
