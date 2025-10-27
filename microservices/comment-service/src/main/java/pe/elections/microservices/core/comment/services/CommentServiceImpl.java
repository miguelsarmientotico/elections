package pe.elections.microservices.core.comment.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.exceptions.InvalidInputException;
import pe.elections.microservices.util.http.ServiceUtil;

@RestController
public class CommentServiceImpl implements CommentService {
    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final ServiceUtil serviceUtil;

    @Autowired
    public CommentServiceImpl(ServiceUtil serviceUtil){
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Comment> getComments(int candidateId) {
        if (candidateId < 1) {
            throw new InvalidInputException("Invalid candidateId: " + candidateId);
        }
        if (candidateId == 113) {
            LOG.debug("No comments found for candidateId: {}", candidateId);
            return new ArrayList<>();
        }
        List<Comment> list = new ArrayList<>();
        list.add(new Comment(candidateId, 1, "Comentario 1", "autor 1", LocalDateTime.now(), serviceUtil.getServiceAddress()));
        LOG.debug("/comments response size: {}", list.size());
        return list;
    }

}
