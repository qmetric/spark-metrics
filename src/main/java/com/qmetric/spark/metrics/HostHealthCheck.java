package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import retrofit.Endpoints;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Headers;

public class HostHealthCheck extends HealthCheck {
    static final String PING = "/ping";

    static final String UNABLE_TO_CONNECT_TO_HOST_S = "Unable to Connect to host %s%s";

    static final String PING_SUCCESSFUL = "Ping was successful to %s%s";

    private final String unhealthyMessage;

    private final String healthyMessage;

    private PingService pingService;

    public HostHealthCheck(final String host) {
        final String endpoint = makeUrl(host);

        pingService = RetroServiceAdapterFactory.service(PingService.class, Endpoints.newFixedEndpoint(endpoint));

        unhealthyMessage = String.format(UNABLE_TO_CONNECT_TO_HOST_S, endpoint, PING);

        healthyMessage = String.format(PING_SUCCESSFUL, endpoint, PING);
    }

    public HostHealthCheck(final String host, final String context) {
        this(makeUrl(host, context));
    }

    @Override
    protected Result check() throws Exception {
        try {
            pingService.ping();

            return Result.healthy(healthyMessage);
        } catch (Exception e) {
            return Result.unhealthy(unhealthyMessage);
        }
    }

    private static String makeUrl(final String host) {
        return host.contains("http") ? String.format("%s", host) : String.format("http://%s", host);
    }

    private static String makeUrl(final String host, final String context) {
        return host.contains("http") ? String.format("%s/%s", host, context) : String.format("http://%s/%s", host, context);
    }

    private interface PingService {
        @Headers({"Accept: */*"})
        @GET(PING)
        Response ping();
    }
}
