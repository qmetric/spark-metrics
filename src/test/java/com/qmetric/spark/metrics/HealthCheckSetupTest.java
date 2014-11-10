package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckSetupTest
{
    private static final String SERVICE_NAME = "service-name";

    private SparkTestUtil sparkTestUtil;

    @Before
    public void initHelper()
    {
        sparkTestUtil = new SparkTestUtil();
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");
    }

    @Test
    public void shouldInitialiseHealthCheck() throws IOException
    {
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));

        HealthCheckSetup.removeHealthCheck(SERVICE_NAME);
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

        HealthCheckSetup.removeHealthCheck(SERVICE_NAME);
    }

    @Test
    public void registerIsOptional() throws IOException
    {
        HealthCheckSetup.addHealthCheck(SERVICE_NAME, "http://localhost:50001");

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertOutputContainsServiceName(httpResponse);

        HealthCheckSetup.removeHealthCheck(SERVICE_NAME);
    }

    @Test
    public void shouldInitialiseSpecialisedHealthCheck() throws IOException
    {
        final String special = "special";
        final HealthCheck healthCheck = mock(HealthCheck.class);
        when(healthCheck.execute()).thenReturn(HealthCheck.Result.healthy());
        HealthCheckSetup.addHealthCheck(special, healthCheck);

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertOutputContains(httpResponse, special);

        HealthCheckSetup.removeHealthCheck(special);
    }

    @Test
    public void shouldInitialiseCustomHealthCheck() throws IOException
    {
        Spark.get("/custom", (request, response) -> {
            response.raw().setStatus(200);
            return "";
        });


        final String custom = "custom";
        HealthCheckSetup.addHealthCheck(custom, new URL("http://localhost:50001/custom"));

        final HttpResponse httpResponse = getHealthCheckResponse();

       assertOutputContains(httpResponse, custom);

        HealthCheckSetup.removeHealthCheck(custom);
    }

    @Test
    public void shouldInitialiseDBHealthCheck() throws SQLException, IOException
    {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:dbhc");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        final String hcdb = "HCDB";
        HealthCheckSetup.addHealthCheck(hcdb, dataSource, "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");

        final HttpResponse httpResponse = getHealthCheckResponse();

        assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));

        HealthCheckSetup.removeHealthCheck(hcdb);
    }

    @Test
    public void shouldReturnInternalErrorIfUnhealthy() throws Exception
    {
        final String name = "failing-host";
        HealthCheckSetup.addHealthCheck(name, "host");

        final HttpResponse healthCheck = sparkTestUtil.get("/healthcheck");

        assertThat(healthCheck.getStatusLine().getStatusCode(), is(500));

        HealthCheckSetup.removeHealthCheck(name);
    }

    @Test
    public void shouldShowHealthChecks() throws SQLException, IOException
    {
        HealthCheckSetup.addHealthCheck("failing-host", "host");
        HealthCheckSetup.addHealthCheck("failing-db", failingDataSource());

        final HttpResponse healthCheck = sparkTestUtil.get("/healthcheck");

        final String actual = IOUtils.toString(healthCheck.getEntity().getContent());
        assertThat(actual, SameJSONAs.sameJSONAs("{\n" +
                                                 "  \"failing-db\" : {\n" +
                                                 "    \"healthy\" : false,\n" +
                                                 "    \"message\" : \"Unable to connect to database : url username null\"\n" +
                                                 "  },\n" +
                                                 "  \"failing-host\" : {\n" +
                                                 "    \"healthy\" : false,\n" +
                                                 "    \"message\" : \"Unable to Connect to host http://host/ping\"\n" +
                                                 "  }\n" +
                                                 "}").allowingExtraUnexpectedFields());
    }

    private HttpResponse getHealthCheckResponse()
    {
        return sparkTestUtil.get("/healthcheck");
    }

    private void assertOutputContainsServiceName(final HttpResponse httpResponse) throws IOException
    {
        assertThat(IOUtils.toString(httpResponse.getEntity().getContent()), containsString(SERVICE_NAME));
    }

    private void assertOutputContains(final HttpResponse httpResponse, final String content) throws IOException
    {
        assertThat(IOUtils.toString(httpResponse.getEntity().getContent()), containsString(content));
    }
}
