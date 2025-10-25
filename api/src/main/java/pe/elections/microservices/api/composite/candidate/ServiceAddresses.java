package pe.elections.microservices.api.composite.candidate;

public class ServiceAddresses {
    private final String cmp;
    private final String can;
    private final String com;
    private final String nws;

    public ServiceAddresses() {
        cmp = null;
        can = null;
        com = null;
        nws = null;
    }

    public ServiceAddresses(
        String compositeAddress,
        String candidateAddress,
        String commentAddress,
        String NewsArticleAddress
    ) {
        this.cmp = commentAddress;
        this.can = candidateAddress;
        this.com = commentAddress;
        this.nws = NewsArticleAddress;
    }

    public String getCmp() {
        return cmp;
    }

    public String getCan() {
        return can;
    }

    public String getCom() {
        return com;
    }

    public String getNws() {
        return nws;
    }
}
