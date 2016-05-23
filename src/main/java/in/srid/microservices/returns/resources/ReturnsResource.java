package in.srid.microservices.returns.resources;

import static javax.ws.rs.client.Entity.json;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import in.srid.microservices.returns.api.Error;
import in.srid.microservices.returns.api.Returns;
import in.srid.microservices.returns.api.State;
import rx.Observable;
import rx.Subscriber;

@Path("/returns")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "returns")
public class ReturnsResource {
    private final static Logger LOG = LoggerFactory.getLogger(ReturnsResource.class);

    private final static String CONSUL_URL = "http://consul";

    private final Client shippingClient;
    private final Client billingClient;

    public ReturnsResource(Client shippingClient, Client billingClient) {
        this.shippingClient = shippingClient;
        this.billingClient = billingClient;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return an order", notes = "Simply post a returns object")
    public Response returns(Returns returns) {
        final Observable<State> shipping = new ShippingStateCommand(shippingClient, returns).observe();
        final Observable<State> billing = new BillingStateCommand(billingClient, returns).observe();

        return Observable.zip(shipping, billing, (shippingState, billingState) -> {
            if (!shippingState.isValid()) {
                LOG.warn("Shipping state [{}] is not valid", shippingState);
                return errorResponse("Shipping could not be returned");
            }
            if (!billingState.isValid()) {
                LOG.warn("Billing state [{}] is not valid", billingState);
                return errorResponse("Billing could not be returned");
            }
            return Response.ok(returns).build();
        }).toBlocking().first();
    }

    private Response errorResponse(String message) {
        return Response.status(500).entity(new Error(100, message)).build();
    }

    private class ShippingStateCommand extends StateCommand {
        private final static String SHIPMENTS_PATH = "/shipments/returns";

        protected ShippingStateCommand(Client client, Returns returns) {
            super(client, SHIPMENTS_PATH, returns);
        }
    }

    private class BillingStateCommand extends StateCommand {
        private final static String BILLS_PATH = "/bills/returns";

        protected BillingStateCommand(Client client, Returns returns) {
            super(client, BILLS_PATH, returns);
        }
    }

    private static class StateCommand extends HystrixObservableCommand<State> {
        private static final State FALLBACK_STATE = new State(-1, "fallback");

        private final Client client;
        private final String path;
        private final Returns returns;

        protected StateCommand(Client client, String path, Returns returns) {
            super(HystrixCommandGroupKey.Factory.asKey(path));
            this.client = client;
            this.path = path;
            this.returns = returns;
        }

        @Override
        protected Observable<State> construct() {
            return Observable.create(new Observable.OnSubscribe<State>() {
                @Override
                public void call(Subscriber<? super State> subscriber) {
                    final WebTarget target = client.target(CONSUL_URL).path(path);
                    target.request().async().post(json(returns), new InvocationCallback<State>() {
                        @Override
                        public void completed(State response) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(response);
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void failed(Throwable throwable) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(throwable);
                            }
                        }
                    });
                }
            });
        }

        @Override
        protected Observable<State> resumeWithFallback() {
            return Observable.create(new Observable.OnSubscribe<State>() {
                @Override
                public void call(final Subscriber<? super State> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(FALLBACK_STATE);
                        subscriber.onCompleted();
                    }
                }
            });
        }
    }
}
