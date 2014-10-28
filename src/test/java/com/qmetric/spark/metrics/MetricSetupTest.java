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

import static com.qmetric.spark.metrics.MetricSetup.Verb;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetricSetupTest
{
    private static final String TIMER_TIMED = "timer.timed";

    private static final String METER_METERED = "meter.metered";

    private static final String TIMER = "timer.";

    private static final String METER = "meter.";

    private static final String SLASH = "/";

    private SparkTestUtil sparkTestUtil = new SparkTestUtil(4567);

    @Test
    public void shouldRegisterRoute() throws IOException
    {
        Spark.get("/path", route("/path", Verb.GET));
        final HttpResponse path1 = sparkTestUtil.get("path");
        EntityUtils.consumeQuietly(path1.getEntity());

        final HttpResponse httpResponse = getMetricsResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void shouldAddTimedRoute() throws IOException
    {
        final String timed = "/timed";
        MetricSetup.timeRoute(timed, route(timed, Verb.PUT), Verb.PUT);

        requestTestRoute(timed);

        final HttpResponse httpResponse = getMetricsResponse();

        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.timer");
    }

    @Test
    public void shouldAddMeterRoute() throws IOException
    {
        final String metered = "/metered";
        MetricSetup.meterRoute(metered, route(metered, Verb.PUT), Verb.PUT);

        requestTestRoute(metered);

        final HttpResponse httpResponse = getMetricsResponse();

        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.meter", "com.qmetric.spark.metrics.RouteTimerWrapper.meter");
    }

    @Test
    public void shouldTimeAndMeterRouteForGet() throws IOException
    {
        final String name = "timedAndMeteredGet";
        final String timedAndMetered = SLASH + name;
        MetricSetup.timeAndMeterRoute(timedAndMetered, route(timedAndMetered, Verb.GET), Verb.GET);

        requestTestRoute(timedAndMetered);

        final HttpResponse httpResponse = getMetricsResponse();
        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.timer", "com.qmetric.spark.metrics.RouteTimerWrapper.meter");
    }

    @Test
    public void shouldTimeAndMeterRouteForPost() throws IOException
    {
        final String name = "timedAndMeteredPost";
        final String timedAndMetered = SLASH + name;
        MetricSetup.timeAndMeterRoute(timedAndMetered, route(timedAndMetered, Verb.POST), Verb.POST);

        requestTestRoute(timedAndMetered);

        final HttpResponse httpResponse = getMetricsResponse();
        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.timer", "com.qmetric.spark.metrics.RouteTimerWrapper.meter");
    }

    @Test
    public void shouldTimeAndMeterRouteForPut() throws IOException
    {
        final String name = "timedAndMeteredPut";
        final String timedAndMetered = SLASH + name;
        MetricSetup.timeAndMeterRoute(timedAndMetered, route(timedAndMetered, Verb.PUT), Verb.PUT);

        requestTestRoute(timedAndMetered);

        final HttpResponse httpResponse = getMetricsResponse();
        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.timer", "com.qmetric.spark.metrics.RouteTimerWrapper.meter");
    }

    @Test
    public void shouldTimeAndMeterRouteForDeleteWherePutExistsWithSamePath() throws IOException
    {
        final String name = "timedAndMeteredDeleteWherePutHasSamePath";
        final String timedAndMeteredPath = SLASH + name;

        MetricSetup.timeAndMeterRoute(timedAndMeteredPath, route(timedAndMeteredPath, Verb.PUT), Verb.PUT);
        MetricSetup.timeAndMeterRoute(timedAndMeteredPath, route(timedAndMeteredPath, Verb.DELETE), Verb.DELETE);

        requestTestDeleteRoute(timedAndMeteredPath);

        final HttpResponse httpResponse = getMetricsResponse();
        assertResponseContains(httpResponse, "com.qmetric.spark.metrics.MetricSetupTest$1.timer", "com.qmetric.spark.metrics.RouteTimerWrapper.meter");
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
        return sparkTestUtil.get("/metrics");
    }

    private void requestTestRoute(final String path)
    {
        final HttpResponse getPath = sparkTestUtil.get(path);
        EntityUtils.consumeQuietly(getPath.getEntity());
    }

    private void requestTestDeleteRoute(final String path)
    {
        final HttpResponse deletePath = sparkTestUtil.delete(path);
        EntityUtils.consumeQuietly(deletePath.getEntity());
    }

    private Route route(final String path, final Verb expectedMethod)
    {
        return new Route()
        {
            @Override public Object handle(final Request request, final Response response)
            {
                final Verb method = Verb.valueOf(request.raw().getMethod());
                assertThat(method == expectedMethod, is(true));
                return path;
            }
        };
    }
}
