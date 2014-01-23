package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import static org.fest.assertions.Assertions.assertThat;

public class ExamplesAndIntegrationTest
{

    private static final int PORT = 51000;

    private final Route baseRoute = new Route("/ping")
    {
        @Override public Object handle(final Request request, final Response response)
        {
            return "pong";
        }
    };

    private final MetricRegistry registry = new MetricRegistry();

    private final SparkTestUtil sparkTestUtil = new SparkTestUtil(PORT);

    @Before
    public void startSpark()
    {
        Spark.setPort(PORT);
    }

    @Test
    public void meter() throws InterruptedException
    {
        Spark.get(new RouteMeterWrapper(registry, baseRoute));
        final HttpResponse response = sparkTestUtil.get("/ping");
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(meterCount("meter.ping")).isEqualTo(1);
    }

    @Test
    public void timer() throws InterruptedException
    {
        Spark.get(new RouteTimerWrapper(registry, baseRoute));
        final HttpResponse response = sparkTestUtil.get("/ping");
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(timerCount("timer.ping")).isEqualTo(1);
    }

    private long timerCount(final String key)
    {
        return registry.getTimers().get(key).getCount();
    }

    private long meterCount(final String key)
    {
        return registry.getMeters().get(key).getCount();
    }
}
