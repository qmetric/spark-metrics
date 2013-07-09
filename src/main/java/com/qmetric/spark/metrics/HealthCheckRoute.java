package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.util.Map;
import java.util.SortedMap;

public class HealthCheckRoute extends Route
{
    public static final String PATH = "/healthcheck";

    private final HealthCheckRegistry registry;

    private final ObjectMapper objectMapper;

    public HealthCheckRoute(final HealthCheckRegistry registry)
    {
        super(PATH);

        this.registry = registry;
        this.objectMapper = new ObjectMapper().registerModule(new HealthCheckModule());
    }

    @Override public Object handle(final Request request, final Response response)
    {
        final HttpServletResponse resp = response.raw();
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        final SortedMap<String, HealthCheck.Result> results = runHealthChecks();
        if (results.isEmpty())
        {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
        else
        {
            if (isAllHealthy(results))
            {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else
            {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (OutputStream output = resp.getOutputStream())
        {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(output, results);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
        return null;
    }

    private SortedMap<String, HealthCheck.Result> runHealthChecks()
    {
        return registry.runHealthChecks();
    }

    private static boolean isAllHealthy(Map<String, HealthCheck.Result> results)
    {
        for (HealthCheck.Result result : results.values())
        {
            if (!result.isHealthy())
            {
                return false;
            }
        }
        return true;
    }
}
