package in.srid.microservices.returns.resources;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static javax.ws.rs.client.Entity.json;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.ServerSocket;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClient;

import in.srid.microservices.returns.api.Error;
import in.srid.microservices.returns.api.Returns;
import io.dropwizard.testing.junit.ResourceTestRule;

public class ReturnsResourceIT {
    private static final int CLIENT_PORT = freePort();
    private static final Server SERVER = new Server("localhost", CLIENT_PORT);
    private static final ZoneAwareLoadBalancer<Server> LOAD_BALANCER = new ZoneAwareLoadBalancer<Server>() {
        @Override
        public Server chooseServer() {
            return SERVER;
        }
    };
    private static final JerseyClient JERSEY_CLIENT = new JerseyClientBuilder().build();
    private static final RibbonJerseyClient CLIENT = new RibbonJerseyClient(LOAD_BALANCER, JERSEY_CLIENT);

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
        .addResource(new ReturnsResource(CLIENT, CLIENT)) //
        .setTestContainerFactory(new GrizzlyWebTestContainerFactory()) //
        .build();

    @ClassRule
    public static final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(CLIENT_PORT));

    @Test
    public void ok() {
        stubShippingResponse("returned");
        stubBillingResponse("returned");

        final Response response = callReturns();

        assertThat(response.getStatus(), is(200));
        assertThat(response.readEntity(Returns.class).getOrderNumber(), is(1L));
    }

    @Test
    public void serviceFailsWhenShippingStateIsNotReturned() {
        stubShippingResponse("processing");
        stubBillingResponse("returned");

        final Response response = callReturns();

        final Error error = response.readEntity(Error.class);
        assertThat(response.getStatus(), is(500));
        assertThat(error.getCode(), is(100));
        assertThat(error.getDescription(), is("Shipping could not be returned"));
    }

    @Test
    public void serviceFailsWhenBillingStateIsNotReturned() {
        stubShippingResponse("returned");
        stubBillingResponse("processing");

        final Response response = callReturns();

        final Error error = response.readEntity(Error.class);
        assertThat(response.getStatus(), is(500));
        assertThat(error.getCode(), is(100));
        assertThat(error.getDescription(), is("Billing could not be returned"));
    }

    @Test
    public void serviceFailsWhenShippingResponseIsDelayed() {
        stubFor(post(urlEqualTo("/shipments/returns")) //
            .willReturn(aResponse() //
                .withStatus(200) //
                .withFixedDelay(2000)));
        stubBillingResponse("returned");

        final Response response = callReturns();

        final Error error = response.readEntity(Error.class);
        assertThat(response.getStatus(), is(500));
        assertThat(error.getCode(), is(100));
        assertThat(error.getDescription(), is("Shipping could not be returned"));
    }

    @Test
    public void serviceFailsWhenBillingResponseIsDelayed() {
        stubShippingResponse("returned");
        stubFor(post(urlEqualTo("/bills/returns")) //
            .willReturn(aResponse() //
                .withStatus(200) //
                .withFixedDelay(2000)));

        final Response response = callReturns();

        final Error error = response.readEntity(Error.class);
        assertThat(response.getStatus(), is(500));
        assertThat(error.getCode(), is(100));
        assertThat(error.getDescription(), is("Billing could not be returned"));
    }

    private void stubShippingResponse(final String state) {
        stubFor(post(urlEqualTo("/shipments/returns")) //
            .willReturn(aResponse() //
                .withHeader("Content-Type", "application/json") //
                .withBody("{\"orderNumber\": 1, \"state\": \"" + state + "\"}")));
    }

    private void stubBillingResponse(final String state) {
        stubFor(post(urlEqualTo("/bills/returns")) //
            .willReturn(aResponse() //
                .withHeader("Content-Type", "application/json") //
                .withBody("{\"orderNumber\": 1, \"state\": \"" + state + "\"}")));
    }

    private Response callReturns() {
        return RULE.getJerseyTest().target("/returns").request().post(json(new Returns(1L)));
    }

    private static Integer freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (final IOException e) {
            return 0;
        }
    }
}
