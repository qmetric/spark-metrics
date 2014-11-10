package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;

import java.util.SortedMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RouteTimerWrapperTest
{
    private SparkTestUtil sparkTestUtil;

    private MetricRegistry metricRegistry;

    @Before
    public void init()
    {
        metricRegistry = new MetricRegistry();
        Spark.get("/timer-wrapper", new RouteTimerWrapper(metricRegistry, (request, response) -> ""));

        sparkTestUtil = new SparkTestUtil();
    }

    @Test
    public void shouldTimeRequest()
    {
        for (int i = 0; i < 10; i++)
        {
            final HttpResponse httpResponse = sparkTestUtil.get("timer-wrapper");
            EntityUtils.consumeQuietly(httpResponse.getEntity());
        }
        final SortedMap<String, Timer> timers = metricRegistry.getTimers();

        for (String s : timers.keySet())
        {
            assertThat(timers.get(s).getCount(), is(10l));
        }
    }
}
