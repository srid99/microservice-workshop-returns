package in.srid.microservices.returns.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import in.srid.microservices.returns.api.Returns;

@Path("/returns")
@Produces(MediaType.APPLICATION_JSON)
public class ReturnsResource {
    private static final Logger LOG = LoggerFactory.getLogger(ReturnsResource.class);

    private final Client client;

    public ReturnsResource(Client client) {
        this.client = client;
    }

    @GET
    @Timed
    @Path("/test")
    public Returns testRibbon() {
        final WebTarget target = client.target("http://whatever/");
        final WebTarget path = target.path("/returns");

        LOG.info("Testing the path: {}", path.getUri());

        return path.request().post(Entity.json(new Returns(1)), Returns.class);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Returns returns(Returns returns) {
        return returns;
    }
}
