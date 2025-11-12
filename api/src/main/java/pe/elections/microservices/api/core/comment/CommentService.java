package pe.elections.microservices.api.core.comment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {

    Mono<Comment> createComment(Comment body);

    @GetMapping(
        value = "/comment",
        produces = "application/json"
    )
    Flux<Comment> getComments(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

    Mono<Void> deleteComments(int candidateId);

}
