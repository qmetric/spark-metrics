package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import us.monoid.web.Resty;

import java.net.URL;

public class CustomHealthCheck extends HealthCheck
{
    private final URL url;

    private String message;

    public CustomHealthCheck(final URL url)
    {
        this.url = url;
        message = String.format(HostHealthCheck.UNABLE_TO_CONNECT_TO_HOST_S, url.toString());
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            final boolean status = new Resty().text(url.toURI()).status(200);
            if (status)
            {
                return Result.healthy();
            }
            else
            {

                return Result.unhealthy(message);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(message);
        }
    }
}
