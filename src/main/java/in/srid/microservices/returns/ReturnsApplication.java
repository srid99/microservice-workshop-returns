package in.srid.microservices.returns;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.hystrix.bundle.HystrixBundle;

import com.basistech.metrics.reporting.Statsd;
import com.basistech.metrics.reporting.StatsdReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;
import com.smoketurner.dropwizard.consul.ConsulBundle;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClient;
import com.smoketurner.dropwizard.consul.ribbon.RibbonJerseyClientBuilder;
import com.smoketurner.dropwizard.consul.ribbon.RibbonLoadBalancerConfiguration;

import in.srid.microservices.returns.resources.ReturnsResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ReturnsApplication extends Application<ReturnsConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(ReturnsApplication.class);

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

        bootstrap.addBundle(new SwaggerBundle<ReturnsConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ReturnsConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        bootstrap.addBundle(HystrixBundle.withDefaultSettings());
    }

    @Override
    public void run(ReturnsConfiguration configuration, Environment environment) throws Exception {
        final Consul consul = configuration.getConsulFactory().build();

        final RibbonLoadBalancerConfiguration billing = configuration.getBillingDownstream();
        final RibbonJerseyClient billingClient = new RibbonJerseyClientBuilder(environment, consul).build(billing);

        final RibbonLoadBalancerConfiguration shipping = configuration.getShippingDownstream();
        final RibbonJerseyClient shippingClient = new RibbonJerseyClientBuilder(environment, consul).build(shipping);

        final MetricRegistry metrics = environment.metrics();

        final ReturnsResource resource = new ReturnsResource(metrics, shippingClient, billingClient);

        reporter(consul, metrics).start(5, TimeUnit.SECONDS);

        environment.jersey().register(resource);
    }

    private ScheduledReporter reporter(final Consul consul, final MetricRegistry metricRegistry) {
        final HealthClient healthClient = consul.healthClient();
        final ConsulResponse<List<ServiceHealth>> metrics = healthClient.getAllServiceInstances("metrics");

        if (!metrics.getResponse().isEmpty()) {
            final ServiceHealth serviceHealth = metrics.getResponse().get(0);
            final String host = serviceHealth.getNode().getAddress();
            final int port = serviceHealth.getService().getPort();
            LOG.info("StatsD server[{}:{}] found", host, port);
            final Statsd statsd = new Statsd(host, port);

            return StatsdReporter.forRegistry(metricRegistry) //
                .prefixedWith("returns") //
                .convertDurationsTo(TimeUnit.MILLISECONDS) //
                .convertRatesTo(TimeUnit.SECONDS) //
                .filter(MetricFilter.ALL) //
                .build(statsd);
        }

        LOG.info("No StatsD server found. Fallback to SLF4J reporter");
        return Slf4jReporter.forRegistry(metricRegistry).build();
    }
}
