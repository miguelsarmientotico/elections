package pe.elections.microservices.api.core.comment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface CommentService {

    @GetMapping(
        value = "/comment",
        produces = "application/json"
    )
    List<Comment> getComments(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

}
