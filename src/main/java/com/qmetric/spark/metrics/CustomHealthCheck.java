package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import us.monoid.web.Resty;

import java.net.URL;

public class CustomHealthCheck extends HealthCheck
{
    private final URL url;

    private final String unHealthyMessage;

    private final String healthyMessage;

    public CustomHealthCheck(final URL url)
    {
        this.url = url;
        unHealthyMessage = String.format(HostHealthCheck.UNABLE_TO_CONNECT_TO_HOST_S, url.toString());
        healthyMessage = String.format(HostHealthCheck.PING_SUCCESSFUL, url.toString());
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            final boolean status = new Resty().text(url.toURI()).status(200);
            if (status)
            {
                return Result.healthy(healthyMessage);
            }
            else
            {

                return Result.unhealthy(unHealthyMessage);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(unHealthyMessage);
        }
    }
}
