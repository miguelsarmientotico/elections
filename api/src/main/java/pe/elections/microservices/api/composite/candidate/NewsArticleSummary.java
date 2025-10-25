package pe.elections.microservices.api.composite.candidate;

import java.time.LocalDateTime;

public class NewsArticleSummary {
    private final int newsArticleId;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime publishDate;
    private final String category;

    public NewsArticleSummary(
        int newsArticleId,
        String title,
        String content,
        String author,
        LocalDateTime publishDate,
        String category
    ) {
        this.newsArticleId = newsArticleId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
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
}

