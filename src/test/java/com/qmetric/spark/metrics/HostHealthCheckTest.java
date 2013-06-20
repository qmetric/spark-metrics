package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HostHealthCheckTest
{

    @Test
    public void shouldWorkWithoutProtocol() throws Exception
    {
        final HostHealthCheck hostHealthCheck = new HostHealthCheck("localhost:" + SparkConstants.PORT);

        final HealthCheck.Result check = hostHealthCheck.check();

        assertThat(check.isHealthy(), is(true));
    }

    @Test
    public void shouldWorkWithProtocol() throws Exception
    {
        final HostHealthCheck hostHealthCheck = new HostHealthCheck("http://localhost:" + SparkConstants.PORT);

        final HealthCheck.Result check = hostHealthCheck.check();

        assertThat(check.isHealthy(), is(true));
    }
}
