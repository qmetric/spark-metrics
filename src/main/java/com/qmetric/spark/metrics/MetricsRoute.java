package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MetricsRoute implements Route
{
    private final MetricRegistry metricRegistry;

    private final ObjectMapper objectMapper;

    public MetricsRoute(final MetricRegistry metricRegistry)
    {
        this(metricRegistry, true);
    }

    public MetricsRoute(final MetricRegistry metricRegistry, boolean showSamples)
    {
        this.metricRegistry = metricRegistry;
        objectMapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, showSamples));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        response.raw().setContentType("application/json");
        response.raw().setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        response.raw().setStatus(HttpServletResponse.SC_OK);
        try (ServletOutputStream outputStream = response.raw().getOutputStream())
        {
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(outputStream, metricRegistry);
        }
        catch (IOException e)
        {
            return e.getMessage();
        }
        return null;
    }
}
