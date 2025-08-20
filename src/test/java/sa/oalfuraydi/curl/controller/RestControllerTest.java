package sa.oalfuraydi.curl.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 
import io.quarkus.test.junit.QuarkusTest;
import sa.oalfuraydi.curl.entity.RestRequest;
import sa.oalfuraydi.curl.entity.Verb;

@QuarkusTest
public class RestControllerTest {

    @InjectMocks
    private RestController restController;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        restController.client = httpClient;
    }

    @Test
    public void testInvokeRest() throws IOException, InterruptedException {
        // Given
        RestRequest request = new RestRequest();
        request.url = "http://example.com";
        request.verb = Verb.GET;
        request.request = "";
        request.userName = "";
        request.password = "";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"key\":\"value\"}");
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(Collections.emptyMap(), (a, b) -> true));

        // When
        RestRequest result = restController.invokeRest(request);

        // Then
        assertEquals("Content-Type:  application/json", result.requestHeader);
        assertEquals("200 OK", result.status);
        assertEquals("{\"key\":\"value\"}", result.response);

    }

    @Test
    public void testHeaderParsing() throws IOException, InterruptedException {
        // Given
        RestRequest request = new RestRequest();
        request.url = "http://example.com";
        request.verb = Verb.GET;
        request.request = "";
        request.userName = "";
        request.password = "";
        request.requestHeader = """
 Content-Type: application/json; charset=ISO-8859-4
    Content-Encoding: gzip
        Date: Tue, 15 Nov 1994 08:12:31 GMT
                        """;

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"key\":\"value\"}");
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(Collections.emptyMap(), (a, b) -> true));

        RestRequest result = assertDoesNotThrow(() -> restController.invokeRest(request));
       
         
        assertEquals("200 OK", result.status);
        assertEquals("{\"key\":\"value\"}", result.response);

    }

    @Test
    public void setAuthorizationHeader() throws IOException, InterruptedException {
        // Given
        RestRequest request = new RestRequest();
        request.url = "http://example.com";
        request.verb = Verb.GET;
        request.request = "";
        request.userName = "usr";
        request.password = "pass";
        request.requestHeader = "";
        HttpRequest httprequest = HttpRequest.newBuilder().uri(URI.create(request.url)).build();
        httprequest = restController.setAuthorizationHeader(request, httprequest);

        assertTrue(httprequest.headers().map().keySet()
                .contains("Authorization"));
        assertEquals("Basic dXNyOnBhc3M=", httprequest.headers().map().get("Authorization").get(0));

    }
    @Test
    public void setAuthorizationHeader_whenHeaderAlreadyExists() throws IOException, InterruptedException {
        // Given
        RestRequest request = new RestRequest();
        request.url = "http://example.com";
        request.verb = Verb.GET;
        request.request = "";
        request.userName = "usr";
        request.password = "pass2";
        request.requestHeader = "";
        HttpRequest httprequest = HttpRequest.newBuilder().uri(URI.create(request.url))
        .header("Authorization", "Basic dXNyOnBhc3M=").build();
        httprequest = restController.setAuthorizationHeader(request, httprequest);

        assertTrue(httprequest.headers().map().keySet()
                .contains("Authorization"));
        assertEquals("Basic dXNyOnBhc3M=", httprequest.headers().map().get("Authorization").get(0));

    }
}
