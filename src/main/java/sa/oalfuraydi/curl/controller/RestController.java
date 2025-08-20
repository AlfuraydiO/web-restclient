package sa.oalfuraydi.curl.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
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

    if (requestEntity.requestHeader == null || requestEntity.requestHeader.isBlank()) {
      requestEntity.requestHeader = "Content-Type: application/json";
    }
    String[] headerArray = requestEntity.requestHeader.split(regex);
    headerArray= Stream.of(headerArray).map(String::strip).toArray(String[]::new);
    
    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(requestEntity.url))
        .timeout(Duration.ofMinutes(1))
        .method(requestEntity.verb.name(), HttpRequest.BodyPublishers.ofString(requestEntity.request))
        .headers(headerArray)
        .build();

    if (!requestEntity.userName.isEmpty() && !requestEntity.password.isEmpty()) {
      httpRequest = setAuthorizationHeader(requestEntity, httpRequest);
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
    } catch (IOException | InterruptedException ex) {
      Log.errorf("Error: %s", ex.getMessage());
    }
    return requestEntity;
  }

  public HttpRequest setAuthorizationHeader(RestRequest requestEntity, HttpRequest httpRequest) {
    if (httpRequest.headers().map().containsKey("Authorization")) {
      return httpRequest;
    } else {
      String basicauth = requestEntity.userName + ":" + requestEntity.password;
      String encodeToString = Base64.getEncoder().encodeToString(basicauth.getBytes());
      return HttpRequest.newBuilder(httpRequest, (n, v) -> true)
          .header("Authorization", "Basic " + encodeToString)
          .build();
    }
  }

  public List<RestRequest> getAllRequests() {
    return RestRequest.listAll();
  }

  @Transactional
  public RestRequest saveRequest(RestRequest request) {
    try {
      RestRequest.persist(request);
    } catch (Exception e) {
      Log.errorf("Issue persisting entity %s", e);
    }
    return request;
  }

  @Transactional
  public void deleteAllRestRequests() {
    try {
      RestRequest.deleteAll();
    } catch (Exception e) {
      Log.errorf("Issue deleting all entities %s", e);
    }
  }

  @Transactional
  public String deleteRestRequestById(Long id) {
    try {
      boolean result = RestRequest.deleteById(id);
      return Boolean.toString(result);
    } catch (Exception e) {
      Log.errorf("Issue deleteing By Id  %s", e);
      return e.getMessage();
    }
  }

}
