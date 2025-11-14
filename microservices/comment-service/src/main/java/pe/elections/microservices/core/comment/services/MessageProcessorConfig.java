package pe.elections.microservices.core.comment.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.elections.microservices.api.core.comment.Comment;
import pe.elections.microservices.api.core.comment.CommentService;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CommentService commentService;

    @Autowired
    public MessageProcessorConfig(CommentService commentService) {
        this.commentService = commentService;
    }

    @Bean
    public Consumer<Event<Integer, Comment>> messageProcessor() {
        LOG.info("entra el messageProcessor");
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    LOG.debug("crear comment");
                    Comment comment = event.getData();
                    commentService.createComment(comment).block();
                    break;
                case DELETE:
                    LOG.debug("delete comment");
                    int candidateId = event.getKey();
                    commentService.deleteComments(candidateId).block();
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    throw new EventProcessingException(errorMessage);
            }
        };
    }
}
