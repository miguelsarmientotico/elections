package pe.elections.microservices.core.candidate.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.candidate.CandidateService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.api.exceptions.NotFoundException;
import pe.elections.microservices.core.candidate.persistence.CandidateEntity;
import pe.elections.microservices.core.candidate.persistence.CandidateRepository;
import pe.elections.microservices.util.http.ServiceUtil;

@RestController
public class CandidateServiceImpl implements CandidateService {
    private static final Logger LOG = LoggerFactory.getLogger(CandidateServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final CandidateRepository repository;
    private final CandidateMapper mapper;

    @Autowired
    public CandidateServiceImpl(
        CandidateRepository repository,
        CandidateMapper mapper,
        ServiceUtil serviceUtil
    ) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Candidate createCandidate(Candidate body) {
        try {
            CandidateEntity entity = mapper.apiToEntity(body);
            CandidateEntity newEntity = repository.save(entity);
            return mapper.entityToApi(newEntity);
        } catch (DuplicateKeyException dke) {
            LOG.debug("error duplicate");
            throw new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId());
        }
    }

    @Override
    public Candidate getCandidate(int candidateId) {
        LOG.debug("/candidate return the found product for candidateId={}", candidateId);
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        CandidateEntity entity = repository.findByCandidateId(candidateId)
        .orElseThrow(() -> new NotFoundException("No candidate found for candidateId: " + candidateId));
        Candidate response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());
        return response;
    }

    @Override
    public void deleteCandidate(int candidateId) {
        repository.findByCandidateId(candidateId).ifPresent(e -> repository.delete(e));
    }

}
