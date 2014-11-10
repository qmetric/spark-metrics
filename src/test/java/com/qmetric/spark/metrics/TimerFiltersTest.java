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

public class TimerFiltersTest
{
    private SparkTestUtil sparkTestUtil;

    private MetricRegistry metricRegistry;

    @Before
    public void init()
    {
        Spark.get("/home", (request, response) -> "sweet home");

        metricRegistry = new MetricRegistry();
        final TimerFilters timerFilters = new TimerFilters(metricRegistry, this.getClass(), "metrics");

        Spark.before(timerFilters.beforeFilter());

        Spark.after(timerFilters.afterFilter());

        sparkTestUtil = new SparkTestUtil();
    }

    @Test
    public void shouldRunFilter()
    {
        for (int i=0;i < 10; i++)
        {
            final HttpResponse httpResponse = sparkTestUtil.get("home");
            EntityUtils.consumeQuietly(httpResponse.getEntity());
        }

        final SortedMap<String,Timer> timers = metricRegistry.getTimers();

        for (String s : timers.keySet())
        {
            assertThat(timers.get(s).getCount(), is(10l));
        }
    }
}
