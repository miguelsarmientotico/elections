package pe.elections.microservices.api.composite.candidate;

import java.time.Instant;

public class NewsArticleSummary {
    private int newsArticleId;
    private String title;
    private String content;
    private String author;
    private Instant publishDate;
    private String category;

    // Constructor vacío (OBLIGATORIO para Jackson)
    public NewsArticleSummary() {
    }
    
    // Constructor con parámetros (opcional)
    public NewsArticleSummary(
        int newsArticleId,
        String title,
        String content,
        String author,
        Instant publishDate,
        String category
    ) {
        this.newsArticleId = newsArticleId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
    }

    // Getters y setters para TODOS los campos
    public int getNewsArticleId() {
        return newsArticleId;
    }

    public void setNewsArticleId(int newsArticleId) {
        this.newsArticleId = newsArticleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Instant getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
