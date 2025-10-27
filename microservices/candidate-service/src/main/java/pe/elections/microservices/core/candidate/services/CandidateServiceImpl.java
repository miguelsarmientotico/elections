package pe.elections.microservices.core.candidate.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.candidate.CandidateService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.util.http.ServiceUtil;

@RestController
public class CandidateServiceImpl implements CandidateService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);
    private final ServiceUtil serviceUtil;

    @Autowired
    public CandidateServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Candidate getCandidate(int candidateId) {
        LOG.debug("/candidate return the found product for candidateId={}", candidateId);
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        if (candidateId == 113) {
            throw new NotFoundException("No candidate found for candidateId: " + candidateId);
        }
        return new Candidate(candidateId, "NameOfCandidate", 80, serviceUtil.getServiceAddress());
    }
}
