package pe.elections.microservices.api.composite.candidate;

import java.time.LocalDateTime;

public class CommentSummary {
    private final int commentId;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;

    public CommentSummary(
        int commentId,
        String content,
        String author,
        LocalDateTime createdAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
