package com.qmetric.spark.metrics;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static com.qmetric.spark.metrics.MockDataSource.failingDataSource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HealthCheckSetupTest
{

    private static final String SERVICE_NAME = "service-name";

    private SparkTestUtil sparkTestUtil;

    @Before
    public void initHelper()
    {
        sparkTestUtil = new SparkTestUtil(SparkConstants.PORT);
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");
    }

    @Test
    public void shouldInitialiseHealthCheck() throws IOException
    {
        final HttpResponse httpResponse = getHealthCheckResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void shouldBeAbleToRegisterMultipleTimes() throws IOException
    {
        HealthCheckSetup.registerRoute();
        HealthCheckSetup.registerRoute();
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertOutputContainsServiceName(httpResponse);
    }

    @Test
    public void registerIsOptional() throws IOException
    {
        final HttpResponse httpResponse = getHealthCheckResponse();

        assertOutputContainsServiceName(httpResponse);
    }

    @Test
    public void shouldInitialiseCustomHealthCheck() throws IOException
    {
        Spark.get(new Route("/custom")
        {
            @Override public Object handle(final Request request, final Response response)
            {
                response.raw().setStatus(200);
                return "hi";
            }
        });

        HealthCheckSetup.addHealthCheck("custom", new URL("http://localhost:50001/custom"));

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertOutputContainsServiceName(httpResponse);
    }

    @Test
    public void shouldInitialiseDBHealthCheck() throws SQLException, IOException
    {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:dbhc");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        HealthCheckSetup.addHealthCheck("HCDB", dataSource, "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void shouldShowHealthChecks() throws SQLException, IOException
    {
        HealthCheckSetup.addHealthCheck("failing-host", "host");
        HealthCheckSetup.addHealthCheck("failing-db", failingDataSource());

        final HttpResponse healthCheck = sparkTestUtil.get(HealthCheckRoute.PATH);

        final String actual = IOUtils.toString(healthCheck.getEntity().getContent());
        assertThat(actual, SameJSONAs.sameJSONAs("{\n" +
                                                 "  \"failing-db\" : {\n" +
                                                 "    \"healthy\" : false,\n" +
                                                 "    \"message\" : \"Unable to connect to database : url username null\"\n" +
                                                 "  },\n" +
                                                 "  \"failing-host\" : {\n" +
                                                 "    \"healthy\" : false,\n" +
                                                 "    \"message\" : \"Unable to Connect to host http://host/ping\",\n" +
                                                 "    \"error\" : {\n" +
                                                 "      \"message\" : \"Stream closed\",\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}").allowingExtraUnexpectedFields());
        assertThat(actual, containsString("stack\" : ["));
    }

    private HttpResponse getHealthCheckResponse()
    {
        return sparkTestUtil.get(HealthCheckRoute.PATH);
    }

    private void assertOutputContainsServiceName(final HttpResponse httpResponse) throws IOException
    {
        assertThat(IOUtils.toString(httpResponse.getEntity().getContent()), containsString(SERVICE_NAME));
    }
}
