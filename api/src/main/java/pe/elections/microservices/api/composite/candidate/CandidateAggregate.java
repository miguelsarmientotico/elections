package pe.elections.microservices.api.composite.candidate;

import java.util.List;

public class CandidateAggregate {
    private final int candidateId;
    private final String name;
    private final int edad;
    private final List<CommentSummary> comments;
    private final List<NewsArticleSummary> newsArticles;
    private final ServiceAddresses serviceAddresses;
    
    public CandidateAggregate(
        int candidateId,
        String name,
        int edad,
        List<CommentSummary> comments,
        List<NewsArticleSummary> newsArticles,
        ServiceAddresses serviceAddresses
    ){
        this.candidateId = candidateId;
        this.name = name;
        this.edad = edad;
        this.comments = comments;
        this.newsArticles = newsArticles;
        this.serviceAddresses = serviceAddresses;
    }
    public int getCandidateId() {
        return candidateId;
    }
    public String getName() {
        return name;
    }
    public int getEdad() {
        return edad;
    }
    public List<CommentSummary> getComments() {
        return comments;
    }
    public List<NewsArticleSummary> getNewsArticles() {
        return newsArticles;
    }
    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }
}
