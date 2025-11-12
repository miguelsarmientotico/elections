package pe.elections.microservices.core.comment.persistence;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@CompoundIndex(name = "cand-com-id", unique = true, def = "{'candidateId': 1, 'commentId': 1}")
public class CommentEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int candidateId;
    private int commentId;
    private String content;
    private String author;
    private Instant createdAt;

    public CommentEntity() {}

    public CommentEntity(
        int candidateId,
        int commentId,
        String content,
        String author,
        Instant createdAt
    ) {
        this.candidateId = candidateId;
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
