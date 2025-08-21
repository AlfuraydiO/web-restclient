package sa.oalfuraydi.curl.boundery;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.stream.Stream;

import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/validation")
public class ValidationRestPage {

  static String regex = "(\n)|(:\s)";
  

  @POST
  @Path("url")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)

  public String ValidationUrl(@RestForm String url) {
    if (url.isBlank() || url.isEmpty()) {
      return "Url is required";
    }
    try {
      new URI(url).toURL();
    } catch (Exception e) {
      return "Malformed Url,please input a valid Url";
    }
    return "";
  }

  @POST
  @Path("headers")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public String ValidationHeaders(@RestForm String headers) {
    if (headers.isEmpty()||headers.isBlank()) {
      return "";
    }
    String[] headerArray = headers.split(regex);
    headerArray= Stream.of(headerArray).map(String::strip).toArray(String[]::new);
    try {
       HttpRequest.newBuilder().headers(headerArray);
    } catch (IllegalArgumentException e) {
       return "Malformed Headers: "+e.getMessage();
    }
    long count = Stream.of(headerArray).filter(e -> !e.isBlank()).count();
    if (count % 2 == 0) {
      return "";
    } else {
      return "Malformed headers";
    }
  }

}
