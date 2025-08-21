package sa.oalfuraydi.curl.boundery;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import sa.oalfuraydi.curl.controller.RestController;

/**
 *
 * @author oalfuraydi
 */
@Path("/")
public class TestCasePage {

    @Inject
    Template testcases;
    @Inject
    RestController restController;

    @Blocking
    @RunOnVirtualThread
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance testcases() {
        return testcases
                .data("requests", restController.getAllRequests());
    }
    @Path("/testcases/{id}")
    @DELETE
    public String deleteResttestcasebyId(@PathParam(value = "id") long id) {
        return restController.deleteRestRequestById(id);
    }

}
