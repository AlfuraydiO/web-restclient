package sa.oalfuraydi.curl.controller;

import io.quarkus.logging.Log;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.resteasy.reactive.RestResponse;
import sa.oalfuraydi.curl.entity.RestRequest;

/**
 *
 * @author oalfuraydi
 */
@RequestScoped
public class RestController {
 
    HttpClient client = HttpClient.newHttpClient();
    static String regex = "(\n)|(:\s)";

    public RestRequest invokeRest(RestRequest requestEntity) {
        
        if (requestEntity.requestHeader==null||requestEntity.requestHeader.isEmpty()) {
            requestEntity.requestHeader = "Content-Type: application/json";
        }
       
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(requestEntity.url))
                .timeout(Duration.ofMinutes(1))
                .method(requestEntity.verb.name(), HttpRequest.BodyPublishers.ofString(requestEntity.request))
                .headers(requestEntity.requestHeader.split(regex))
                .build();
        if (!requestEntity.userName.isEmpty() && !requestEntity.password.isEmpty()) {

            String basicauth = requestEntity.userName + ":" + requestEntity.password;
            String encodeToString = Base64.getEncoder().withoutPadding().encodeToString(basicauth.getBytes());
            httpRequest = HttpRequest.newBuilder(httpRequest, (n, v) -> true).header("Authorization", "Basic "+encodeToString)
                    .build();
             
        }

        try {
            HttpResponse<String> sent = client.send(httpRequest, BodyHandlers.ofString());

            requestEntity.response = sent.body();
            requestEntity.status = sent.statusCode() + " "
                    + Response.Status.fromStatusCode(sent.statusCode()).getReasonPhrase();
            String resultSingleLinePerHeader = sent.headers().map().entrySet().stream()
                    .map(e -> e.getKey() + ":  " + e.getValue().stream().collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n"));
            requestEntity.responseHeader = resultSingleLinePerHeader;
            requestEntity.requestHeader = httpRequest.headers()
                    .map().entrySet().stream()
                    .map(e -> e.getKey() + ":  " + e.getValue().stream().collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n"));
            Log.info("Incoked");
            Log.info(requestEntity.toString());
        } catch (IOException | InterruptedException ex) {
           Log.errorf("Error: %s",ex.getMessage());
        }
        return requestEntity;

    }

    public List<RestRequest> getAllRequests() {
        return RestRequest.listAll();
    }

    @Transactional
    public void savRequest(RestRequest request) {
        try {

            RestRequest.persist(request);
            

        } catch (Exception e) {
            Log.errorf("Issue persisting entity %s", e);
        }
    }

     @Transactional
    public boolean deleteRestRequestById(Long id) {
        try {

            boolean result=RestRequest.deleteById(id);
            
            return result;

        } catch (Exception e) {
            Log.errorf("Issue deleteing By Id  %s", e);
            return false;
        }
    }

}
