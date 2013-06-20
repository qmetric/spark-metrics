package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomHealthCheckTest
{
    @Test
    public void shouldConnectToHost() throws Exception
    {
        final URL url = new URL("http://localhost:" + SparkConstants.PORT + "/ping");
        final CustomHealthCheck customHealthCheck = new CustomHealthCheck(url);

        final HealthCheck.Result check = customHealthCheck.check();

        assertThat(check.isHealthy(), is(true));
    }
}
