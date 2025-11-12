package pe.elections.microservices.core.newsarticle.persistence;

import java.time.Instant;
import static java.lang.String.format;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "news_articles", indexes = { @Index(name = "news_article_unique_idx", unique = true, columnList = "candidateId,newsArticleId")})
public class NewsArticleEntity {
    @Id @GeneratedValue
    private int id;

    @Version
    private int version;

    private int candidateId;

    private int newsArticleId;

    private String title;

    private String content;

    private String author;

    private Instant publishDate;

    private String category;

    public NewsArticleEntity() {
    }

    public NewsArticleEntity(
        int candidateId,
        int newsArticleId,
        String title,
        String content,
        String author,
        Instant publishDate,
        String category
    ){
        this.candidateId = candidateId;
        this.newsArticleId = newsArticleId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
    }

    @Override
    public String toString() {
        return format("NewsArticleEntity: %s/%d", candidateId, newsArticleId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

