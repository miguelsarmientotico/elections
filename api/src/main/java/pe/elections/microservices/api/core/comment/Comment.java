package pe.elections.microservices.api.core.comment;

public class Comment {
    private int candidateId;
    private int commentId;
    private String content;
    private String author;
    private Long createdAt;
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
        Long createdAt,
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
