package pe.elections.microservices.api.core.candidate;

public class Candidate {
    private int candidateId;
    private String name;
    private int edad;
    private String serviceAddress;

    public Candidate() {
        candidateId = 0;
        name = null;
        edad = 0;
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

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
/*
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
*/
