package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResultModifierTest
{
    @Test
    public void shouldCreateResult()
    {
        final boolean isHealthy = false;
        final String message = "my message";
        final NullPointerException error = new NullPointerException("Exception Message");
        final HealthCheck.Result result = ResultModifier.newResult(isHealthy, message, error);

        assertThat(result.isHealthy(), is(isHealthy));
        assertThat(result.getMessage(), equalTo(message));
        assertThat((NullPointerException)result.getError(), equalTo(error));
    }
}
