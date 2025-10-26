package pe.elections.microservices.api.core.newsarticle;

import java.time.LocalDateTime;

public class NewsArticle {
    private int candidateId;
    private int newsArticleId;
    private String title;
    private String content;
    private String author;
    private LocalDateTime publishDate;
    private String category;
    private String serviceAddress;

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

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

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

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
/*
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
*/
