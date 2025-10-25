package pe.elections.microservices.api.core.newsarticle;

import java.time.LocalDateTime;

public class NewsArticle {
    private final int candidateId;
    private final int newsArticleId;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime publishDate;
    private final String category;
    private final String serviceAddress;

    public NewsArticle() {
        candidateId = 0;
        newsArticleId = 0;
        title = null;
        content = null;
        author = null;
        publishDate = null;
        category = null;
        serviceAddress = null;
    }

    public NewsArticle(
        int candidateId,
        int newsArticleId,
        String title,
        String content,
        String author,
        LocalDateTime publishDate,
        String category,
        String serviceAddress
    ) {
        this.candidateId = candidateId;
        this.newsArticleId = newsArticleId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
        this.serviceAddress = serviceAddress;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public int getNewsArticleId() {
        return newsArticleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public String getCategory() {
        return category;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

}
