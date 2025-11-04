package pe.elections.microservices.core.comment.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.core.comment.persistence.CommentEntity;
import pe.elections.microservices.core.comment.persistence.CommentRepository;
import pe.elections.microservices.util.http.ServiceUtil;

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
    public Comment createComment(Comment body) {
        try {
            LOG.debug("apicandidateId {}", body.getCandidateId());
            LOG.debug("apicommentId {}", body.getCommentId());
            LOG.debug("apicontent {}", body.getContent());
            LOG.debug("apiauthor {}", body.getAuthor());
            LOG.debug("apicreatedAt {}", body.getCreatedAt());
            CommentEntity entity = mapper.apiToEntity(body);
            LOG.debug("candidateId {}", entity.getCandidateId());
            LOG.debug("commentId {}", entity.getCommentId());
            LOG.debug("content {}", entity.getContent());
            LOG.debug("author {}", entity.getAuthor());
            LOG.debug("createdAt {}", entity.getCreatedAt());
            CommentEntity newEntity = repository.save(entity);
            LOG.debug("createcomment: created a comment entity: {}/{}", body.getCandidateId(), body.getCommentId());
            return mapper.entityToApi(newEntity);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidInputException("Duplicate key, Candidate Id: " + body.getCandidateId() + ", Comment Id: " + body.getCommentId());
        }
    }

    @Override
    public List<Comment> getComments(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        List<CommentEntity> entityList = repository.findByCandidateId(candidateId);
        List<Comment> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        return list;
    }

    @Override
    public void deleteComments(int candidateId) {
        repository.deleteAll(repository.findByCandidateId(candidateId));
    }

}
