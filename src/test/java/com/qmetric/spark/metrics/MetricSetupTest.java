package com.qmetric.spark.metrics;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetricSetupTest
{
    private static final String TIMER_TIMED = "timer.timed";

    private static final String METER_METERED = "meter.metered";

    private SparkTestUtil sparkTestUtil = new SparkTestUtil(SparkConstants.PORT);

    @Test
    public void shouldRegisterRoute() throws IOException
    {
        Spark.get(route("/path"));
        final HttpResponse path1 = sparkTestUtil.get("path");
        EntityUtils.consumeQuietly(path1.getEntity());

        final HttpResponse httpResponse = getMetricsResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void shouldAddTimedRoute() throws IOException
    {
        final String timed = "/timed";
        MetricSetup.timeRoute(timed, route(timed), MetricSetup.Verb.GET);

        requestTestRoute(timed);

        final HttpResponse httpResponse = getMetricsResponse();

        assertResponseContains(httpResponse, TIMER_TIMED);
    }

    @Test
    public void shouldAddMeterRoute() throws IOException
    {
        final String metered = "/metered";
        MetricSetup.meterRoute(metered, route(metered), MetricSetup.Verb.GET);

        requestTestRoute(metered);

        final HttpResponse httpResponse = getMetricsResponse();

        assertResponseContains(httpResponse, METER_METERED);
    }

    @Test
    public void shouldTimeAndMeterRoute() throws IOException
    {
        final String timedAndMetered = "/timedAndMetered";
        MetricSetup.timeAndMeterRoute(timedAndMetered, route(timedAndMetered), MetricSetup.Verb.GET);

        requestTestRoute(timedAndMetered);

        final HttpResponse httpResponse = getMetricsResponse();
        assertResponseContains(httpResponse, TIMER_TIMED, METER_METERED);
    }

    private void assertResponseContains(final HttpResponse httpResponse, final String... expected) throws IOException
    {
        final String actual = IOUtils.toString(httpResponse.getEntity().getContent());
        for (String s : expected)
        {
            assertThat(actual, containsString(s));
        }
    }

    private HttpResponse getMetricsResponse()
    {
        return sparkTestUtil.get(MetricsRoute.PATH);
    }

    private void requestTestRoute(final String path)
    {
        final HttpResponse getPath = sparkTestUtil.get(path);
        EntityUtils.consumeQuietly(getPath.getEntity());
    }

    private Route route(final String path)
    {
        return new Route(path)
        {
            @Override public Object handle(final Request request, final Response response)
            {
                return "path";
            }
        };
    }
}
