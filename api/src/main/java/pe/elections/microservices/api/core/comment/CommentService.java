package pe.elections.microservices.api.core.comment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


public interface CommentService {

    @PostMapping(
        value = "/comment",
        produces = "application/json",
        consumes = "application/json"
    )
    Comment createComment(@RequestBody Comment body);

    @GetMapping(
        value = "/comment",
        produces = "application/json"
    )
    List<Comment> getComments(
        @RequestParam(value = "candidateId", required = true) int candidateId
    );

    @DeleteMapping(value = "/comment")
    void deleteComments(@RequestParam(value = "candidateId", required = true) int candidateId);

}
