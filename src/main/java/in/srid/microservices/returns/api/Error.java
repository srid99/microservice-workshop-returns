package in.srid.microservices.returns.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
    private final int code;
    private final String message;

    @JsonCreator
    public Error(@JsonProperty("code") final int code, @JsonProperty("message") final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
