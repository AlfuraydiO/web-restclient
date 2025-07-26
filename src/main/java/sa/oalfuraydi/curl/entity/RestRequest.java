package sa.oalfuraydi.curl.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class RestRequest extends PanacheEntity {

    public Verb verb;
    @Column(length = 2500)
    public String request;
    @Column(length = 2500)
    public String response;
    @Column(length = 2500)
    public String requestHeader;
    @Column(length = 2500)
    public String responseHeader;
    public String url;
    public String userName;
    public String password;
    public String status;
    public String collection;

    public RestRequest() {
    }

    public RestRequest(Verb verb, String request, String requestHeader, String url, String userName, String password) {
        this.verb = verb;
        this.request = request;
        this.requestHeader = requestHeader;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RestRequest{");
        sb.append("verb=").append(verb);
        sb.append(", requrst=").append(request);
        sb.append(", response=").append(response);
        sb.append(", requestHeader=").append(requestHeader);
        sb.append(", responseHeader=").append(responseHeader);
        sb.append(", url=").append(url);
        sb.append(", userName=").append(userName);
        sb.append(", password=").append(password);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

}