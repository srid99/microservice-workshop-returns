package in.srid.microservices.returns.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
    private final int code;
    private final String description;

    @JsonCreator
    public Error(@JsonProperty("code") final int code, @JsonProperty("description") final String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
