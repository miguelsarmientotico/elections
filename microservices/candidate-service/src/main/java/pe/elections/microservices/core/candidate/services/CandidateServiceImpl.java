package pe.elections.microservices.core.candidate.services;

import java.util.logging.Level;

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
import reactor.core.publisher.Mono;

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
    public Mono<Candidate> createCandidate(Candidate body) {
        if (body.getCandidateId() < 1) {
            throw new InvalidInputException("Invalida candidateId: " + body.getCandidateId());
        }
        CandidateEntity entity = mapper.apiToEntity(body);
        Mono<Candidate> newEntity = repository.save(entity)
        .log(LOG.getName(), Level.FINE)
        .onErrorMap(
            DuplicateKeyException.class,
            ex -> new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId())
        )
        .map(e -> mapper.entityToApi(e));
        return newEntity;
    }

    @Override
    public Mono<Candidate> getCandidate(int candidateId) {
        LOG.debug("/candidate return the found candidate for candidateId={}", candidateId);
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        return repository.findByCandidateId(candidateId)
        .switchIfEmpty(Mono.error(new NotFoundException("No candidate found for candidateId: " + candidateId)))
        .log(LOG.getName(), Level.FINE)
        .map(e -> mapper.entityToApi(e))
        .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Void> deleteCandidate(int candidateId) {
        return repository.findByCandidateId(candidateId)
        .log(LOG.getName(), Level.FINE)
        .map(e -> repository.delete(e))
        .flatMap(e -> e);
    }

    private Candidate setServiceAddress(Candidate e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }

}
