package pe.elections.microservices.api.composite.candidate;

public class CommentSummary {
    private int commentId;
    private String content;
    private String author;
    private Long createdAt;

    // Constructor vacío (OBLIGATORIO para Jackson)
    public CommentSummary() {
    }
    
    // Constructor con parámetros (opcional)
    public CommentSummary(
        int commentId,
        String content,
        String author,
        Long createdAt
    ) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getters y setters para TODOS los campos
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
}
