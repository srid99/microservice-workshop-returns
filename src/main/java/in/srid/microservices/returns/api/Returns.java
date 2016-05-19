package in.srid.microservices.returns.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Returns {
    private long orderNumber;

    @JsonCreator
    public Returns(@JsonProperty("orderNumber") long orderNumber) {
        this.orderNumber = orderNumber;
    }

    @JsonProperty
    public long getOrderNumber() {
        return orderNumber;
    }

    @Override
    public String toString() {
        return "Returns{" + "orderNumber=" + orderNumber + '}';
    }
}
