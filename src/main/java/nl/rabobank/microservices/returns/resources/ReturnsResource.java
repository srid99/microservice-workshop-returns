package nl.rabobank.microservices.returns.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.netflix.loadbalancer.Server;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClient;

import nl.rabobank.microservices.returns.api.Returns;

@Path("/returns")
@Produces(MediaType.APPLICATION_JSON)
public class ReturnsResource {
    private final RibbonJerseyClient client;

    public ReturnsResource(RibbonJerseyClient client) {
        this.client = client;
    }

    @GET
    @Timed
    public List<Server> getAvailableServers() {
        return client.getAvailableServers();
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Returns returns(Returns returns) {
        return returns;
    }
}
