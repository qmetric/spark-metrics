package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import us.monoid.web.Resty;
import us.monoid.web.TextResource;

import java.util.regex.Pattern;

public class HostHealthCheck extends HealthCheck
{
    private final Resty resty;

    private final String addr;

    private final String unhealthyMessage;

    private static Pattern pattern = Pattern.compile("pong");

    public HostHealthCheck(final String host)
    {
        addr = String.format("http://%s/ping", host);
        unhealthyMessage = String.format("Unable to Connect to host %s", host);
        resty = new Resty();
    }

    public HostHealthCheck(final String host, final String context)
    {
        addr = String.format("http://%s/%s/ping", host, context);
        unhealthyMessage = String.format("Unable to Connect to host %s", host);
        resty = new Resty();
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            final TextResource text = resty.text(addr);
            if (pattern.matcher(text.toString()).find())
            {
                return Result.healthy();
            }
            else
            {
                return Result.unhealthy(unhealthyMessage);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(e);
        }
    }
}
