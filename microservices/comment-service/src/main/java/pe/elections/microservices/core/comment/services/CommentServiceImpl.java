package pe.elections.microservices.core.comment.services;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.comment.persistence.CommentEntity;
import pe.elections.microservices.core.comment.persistence.CommentRepository;
import pe.elections.microservices.util.http.ServiceUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CommentServiceImpl implements CommentService {
    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final CommentRepository repository;
    private final CommentMapper mapper;

    @Autowired
    public CommentServiceImpl(
        ServiceUtil serviceUtil,
        CommentRepository repository,
        CommentMapper mapper
    ){
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Comment> createComment(Comment body) {
        if (body.getCandidateId() < 1) {
            throw new InvalidInputException("Invalid candidateId: " + body.getCandidateId());
        }
        CommentEntity entity = mapper.apiToEntity(body);
        Mono<Comment> newEntity = repository.save(entity)
        .log(LOG.getName(), Level.FINE)
        .onErrorMap(
            DuplicateKeyException.class,
            ex -> new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId() + ", Comment Id: " + body.getCommentId())
        )
        .map(e -> mapper.entityToApi(e));
        return newEntity;
    }

    @Override
    public Flux<Comment> getComments(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        return repository.findByCandidateId(candidateId)
        .log(LOG.getName(), Level.FINE)
        .map(e -> mapper.entityToApi(e))
        .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Void> deleteComments(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        return repository.deleteAll(repository.findByCandidateId(candidateId));
    }

    private Comment setServiceAddress(Comment e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }

}
