package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import us.monoid.web.Resty;
import us.monoid.web.TextResource;

import java.util.regex.Pattern;

public class HostHealthCheck extends HealthCheck
{
    static final String UNABLE_TO_CONNECT_TO_HOST_S = "Unable to Connect to host %s";

    static final String PING_SUCCESSFUL = "Ping was successful to %s";

    private static Pattern pattern = Pattern.compile("pong");

    private final String addr;

    private final String unhealthyMessage;

    private final String healthyMessage;

    public HostHealthCheck(final String host)
    {
        addr = makeUrl(host);
        unhealthyMessage = String.format(UNABLE_TO_CONNECT_TO_HOST_S, addr);
        healthyMessage = String.format(PING_SUCCESSFUL, addr);
    }

    public HostHealthCheck(final String host, final String context)
    {
        addr = makeUrl(host, context);
        unhealthyMessage = String.format(UNABLE_TO_CONNECT_TO_HOST_S, addr);
        healthyMessage = String.format(PING_SUCCESSFUL, addr);
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            final TextResource text = new Resty().text(addr);
            if (pattern.matcher(text.toString()).find())
            {
                return Result.healthy(healthyMessage);
            }
            else
            {
                return Result.unhealthy(unhealthyMessage);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(unhealthyMessage);
        }
    }

    private String makeUrl(final String host)
    {
        return host.contains("http") ? String.format("%s/ping", host) : String.format("http://%s/ping", host);
    }

    private String makeUrl(final String host, final String context)
    {
        return host.contains("http") ? String.format("%s/%s/ping", host, context) : String.format("http://%s/%s/ping", host, context);
    }
}
