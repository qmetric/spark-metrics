package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import us.monoid.web.Resty;

import java.net.URL;

public class CustomHealthCheck extends HealthCheck
{
    private final URL url;

    public CustomHealthCheck(final URL url)
    {
        this.url = url;
    }

    @Override protected Result check() throws Exception
    {
        final boolean status = new Resty().text(url.toURI()).status(200);
        if(status)
        {
            return Result.healthy();
        }
        else
        {
            return Result.unhealthy(String.format("Cannot connect to : %s", url));
        }
    }
}
