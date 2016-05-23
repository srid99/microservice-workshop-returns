package in.srid.microservices.returns.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
    private static final String EXPECTED_STATE = "returned";

    private final long orderNumber;
    private final String state;

    @JsonCreator
    public State(@JsonProperty("orderNumber") long orderNumber, @JsonProperty("state") final String state) {
        this.orderNumber = orderNumber;
        this.state = state;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public String getState() {
        return state;
    }

    @JsonIgnore
    public boolean isValid() {
        return EXPECTED_STATE.equalsIgnoreCase(state);
    }

    @Override
    public String toString() {
        return "State{" + "orderNumber=" + orderNumber + ", state='" + state + '\'' + '}';
    }
}
