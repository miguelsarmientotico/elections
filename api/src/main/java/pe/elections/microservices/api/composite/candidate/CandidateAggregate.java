package pe.elections.microservices.api.composite.candidate;

import java.util.List;

public class CandidateAggregate {
    private int candidateId;
    private String name;
    private int edad;
    private List<CommentSummary> comments;
    private List<NewsArticleSummary> newsArticles;
    private ServiceAddresses serviceAddresses;
    
    public CandidateAggregate() {
    }
    
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
    
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getEdad() {
        return edad;
    }
    
    public void setEdad(int edad) {
        this.edad = edad;
    }
    
    public List<CommentSummary> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentSummary> comments) {
        this.comments = comments;
    }
    
    public List<NewsArticleSummary> getNewsArticles() {
        return newsArticles;
    }
    
    public void setNewsArticles(List<NewsArticleSummary> newsArticles) {
        this.newsArticles = newsArticles;
    }
    
    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }
    
    public void setServiceAddresses(ServiceAddresses serviceAddresses) {
        this.serviceAddresses = serviceAddresses;
    }
}
