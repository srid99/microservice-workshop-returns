package nl.rabobank.microservices.returns;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.consul.ConsulFactory;
import com.smoketurner.dropwizard.consul.ribbon.RibbonLoadBalancerConfiguration;

import io.dropwizard.Configuration;

public class ReturnsConfiguration extends Configuration {

    @NotNull
    @Valid
    public final ConsulFactory consul = new ConsulFactory();

    @NotNull
    @Valid
    public final RibbonLoadBalancerConfiguration downstream = new RibbonLoadBalancerConfiguration();

    @JsonProperty
    public ConsulFactory getConsulFactory() {
        return consul;
    }

    @JsonProperty
    public RibbonLoadBalancerConfiguration getDownstream() {
        return downstream;
    }
}
