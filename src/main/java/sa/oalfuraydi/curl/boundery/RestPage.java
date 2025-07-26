package sa.oalfuraydi.curl.boundery;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestForm;
import sa.oalfuraydi.curl.controller.RestController;
import sa.oalfuraydi.curl.entity.RestRequest;
import sa.oalfuraydi.curl.entity.Verb;

/**
 *
 * @author oalfuraydi
 */
@Path("/rest")
public class RestPage {

    @Inject
    Template rest;
    @Inject
    RestController restController;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getRest() {

        return rest
                .data("request", new RestRequest());
    }

    @Blocking
    @RunOnVirtualThread
    @Path("{id}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object getResttestcasebyId(@PathParam(value = "id") long id) {
        RestRequest request=RestRequest.findById(id);
        if(request==null){
             return Response.status(404).build();
        }
        return rest
                .data("request", request);
    }


    @Path("send-request")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String sendRest(@RestForm String url, @RestForm String method, @RestForm String userName,
            @RestForm String password, @RestForm String headers, @RestForm String body) {
        Log.info("Started");
        Log.info(method);
        Log.info(body);
        Log.info(url);
        RestRequest requestEntity = new RestRequest(Verb.valueOf(method), body, headers, url, userName, password);
        requestEntity = restController.invokeRest(requestEntity);
        return rest.getFragment("request_output")
                .data("request", requestEntity)
                .render();
    }

    @Path("save-request")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String saveRest(@RestForm String url, @RestForm String method, @RestForm String userName,
            @RestForm String password, @RestForm String headers, @RestForm String body, @RestForm String status,
            @RestForm String response, @RestForm String responseHeader, @RestForm String collection) {
        Log.info("------Saved-----------");

        RestRequest requestEntity = new RestRequest(Verb.valueOf(method), body, headers, url, userName, password);
        requestEntity.status = status;
        requestEntity.responseHeader = responseHeader;
        requestEntity.response = response;
        requestEntity.collection = collection;
        restController.savRequest(requestEntity);
        return "TestCase Saved";
    }

}
