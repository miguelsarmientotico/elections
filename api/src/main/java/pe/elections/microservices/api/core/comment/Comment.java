package pe.elections.microservices.api.core.comment;

import java.time.LocalDateTime;

public class Comment {
    private int candidateId;
    private int commentId;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private String serviceAddress;

    public Comment() {
        candidateId = 0;
        commentId = 0;
        content = null;
        author = null;
        createdAt = null;
        serviceAddress = null;
    }

    public Comment(
        int candidateId,
        int commentId,
        String content,
        String author,
        LocalDateTime createdAt,
        String serviceAddress
    ) {
        this.candidateId = candidateId;
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.serviceAddress = serviceAddress;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
/*
package pe.elections.microservices.api.core.comment;

import java.time.LocalDateTime;

public class Comment {
    private final int candidateId;
    private final int commentId;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;
    private final String serviceAddress;

    public Comment() {
        candidateId = 0;
        commentId = 0;
        content = null;
        author = null;
        createdAt = null;
        serviceAddress = null;
    }

    public Comment(
        int candidateId,
        int commentId,
        String content,
        String author,
        LocalDateTime createdAt,
        String serviceAddress
    ) {
        this.candidateId = candidateId;
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.serviceAddress = serviceAddress;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

}
*/
