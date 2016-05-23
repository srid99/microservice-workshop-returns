package in.srid.microservices.returns;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonLoadBalancerConfiguration;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ReturnsConfiguration extends Configuration {

    @NotNull
    @JsonProperty
    private final ConsulFactory consul = new ConsulFactory();

    @NotNull
    @JsonProperty
    private final RibbonLoadBalancerConfiguration billingDownstream = new RibbonLoadBalancerConfiguration();

    @NotNull
    @JsonProperty
    private final RibbonLoadBalancerConfiguration shippingDownstream = new RibbonLoadBalancerConfiguration();

    @NotNull
    @JsonProperty
    private final RibbonLoadBalancerConfiguration returnsDownstream = new RibbonLoadBalancerConfiguration();

    @NotNull
    @JsonProperty
    private final SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    public ConsulFactory getConsulFactory() {
        return consul;
    }

    public RibbonLoadBalancerConfiguration getBillingDownstream() {
        return billingDownstream;
    }

    public RibbonLoadBalancerConfiguration getShippingDownstream() {
        return shippingDownstream;
    }

    public RibbonLoadBalancerConfiguration getReturnsDownstream() {
        return returnsDownstream;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swagger;
    }
}
