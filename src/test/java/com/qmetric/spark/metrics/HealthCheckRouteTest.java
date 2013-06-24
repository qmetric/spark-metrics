package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;
import spark.utils.IOUtils;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

import static com.qmetric.spark.metrics.MockDataSource.failingDataSource;
import static com.qmetric.spark.metrics.SparkConstants.PORT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class HealthCheckRouteTest
{
    private SparkTestUtil sparkTestUtil;

    @Before
    public void init() throws SQLException
    {
        final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
        final DataSource dataSource = failingDataSource();
        healthCheckRegistry.register("db", new DBHealthCheck(dataSource));
        healthCheckRegistry.register("host", new HostHealthCheck("host"));

        Spark.get(new HealthCheckRoute(healthCheckRegistry));

        sparkTestUtil = new SparkTestUtil(PORT);
    }

    @Test
    public void shouldShowHealthChecks() throws IOException
    {
        final HttpResponse healthCheck = sparkTestUtil.get(HealthCheckRoute.PATH);

        final StringWriter writer = new StringWriter();
        IOUtils.copy(healthCheck.getEntity().getContent(), writer);

        assertThat(writer.toString(), SameJSONAs.sameJSONAs("{\n" +
                                                            "  \"db\" : {\n" +
                                                            "    \"healthy\" : false,\n" +
                                                            "    \"message\" : \"Unable to connect to database : url username null\"\n" +
                                                            "  },\n" +
                                                            "  \"host\" : {\n" +
                                                            "    \"healthy\" : false,\n" +
                                                            "    \"message\" : \"Unable to Connect to host http://host/ping\",\n" +
                                                            "    \"error\" : {\n" +
                                                            "      \"message\" : \"Stream closed\",\n" +
                                                            "    }\n" +
                                                            "  }\n" +
                                                            "}").allowingExtraUnexpectedFields());
        assertThat(writer.toString(), containsString("stack\" : ["));
    }
}
