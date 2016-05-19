package nl.rabobank.microservices.returns;

import com.orbitz.consul.Consul;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClient;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClientBuilder;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.rabobank.microservices.returns.resources.ReturnsResource;

public class ReturnsApplication extends Application<ReturnsConfiguration> {

    public static void main(String[] args) throws Exception {
        new ReturnsApplication().run(args);
    }

    @Override
    public String getName() {
        return "returns";
    }

    @Override
    public void initialize(Bootstrap<ReturnsConfiguration> bootstrap) {
        bootstrap.addBundle(new ConsulBundle<ReturnsConfiguration>(getName()) {
            @Override
            public ConsulFactory getConsulFactory(ReturnsConfiguration configuration) {
                return configuration.getConsulFactory();
            }
        });
    }

    @Override
    public void run(ReturnsConfiguration configuration, Environment environment) throws Exception {

        final Consul consul = configuration.getConsulFactory().build();
        final RibbonJerseyClientBuilder builder = new RibbonJerseyClientBuilder(environment, consul);
        final RibbonJerseyClient loadBalancingClient = builder.build(configuration.getDownstream());

        final ReturnsResource resource = new ReturnsResource(loadBalancingClient);
        environment.jersey().register(resource);
    }
}
