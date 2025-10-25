package pe.elections.microservices.api.core.candidate;

public class Candidate {
    private final int candidateId;
    private final String name;
    private final int edad;
    private final String serviceAddress;

    public Candidate() {
        candidateId = 0;
        name = null;
        edad = 0 ;
        serviceAddress = null;
    }

    public Candidate(int candidateId, String name, int edad, String serviceAddress) {
        this.candidateId = candidateId;
        this.name = name;
        this.edad = edad;
        this.serviceAddress = serviceAddress;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

}
