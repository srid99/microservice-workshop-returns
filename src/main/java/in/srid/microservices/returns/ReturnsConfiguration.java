package in.srid.microservices.returns;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonLoadBalancerConfiguration;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ReturnsConfiguration extends Configuration {

    @NotNull
    @Valid
    public final ConsulFactory consul = new ConsulFactory();

    @NotNull
    @Valid
    public final RibbonLoadBalancerConfiguration billingDownstream = new RibbonLoadBalancerConfiguration();

    @NotNull
    @Valid
    public final RibbonLoadBalancerConfiguration shippingDownstream = new RibbonLoadBalancerConfiguration();

    @NotNull
    @Valid
    public final RibbonLoadBalancerConfiguration returnsDownstream = new RibbonLoadBalancerConfiguration();

    @JsonProperty
    public ConsulFactory getConsulFactory() {
        return consul;
    }

    @JsonProperty
    public RibbonLoadBalancerConfiguration getBillingDownstream() {
        return billingDownstream;
    }

    @JsonProperty
    public RibbonLoadBalancerConfiguration getShippingDownstream() {
        return shippingDownstream;
    }

    @JsonProperty
    public RibbonLoadBalancerConfiguration getReturnsDownstream() {
        return returnsDownstream;
    }
    
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;
}
