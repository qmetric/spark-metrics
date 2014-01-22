package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.servlet.http.HttpServletResponse.SC_OK;

public class MetricsRoute extends Route
{
    public static final String PATH = "/metrics";

    private final MetricRegistry metricRegistry;

    private final ObjectMapper objectMapper;

    public MetricsRoute(final MetricRegistry metricRegistry)
    {
        super(PATH);
        this.metricRegistry = metricRegistry;
        this.objectMapper = new ObjectMapper().registerModule(new MetricsModule(SECONDS, MILLISECONDS, true));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        response.header("Content-Type", "application/json");
        response.header("Cache-Control", "must-revalidate,no-cache,no-store");
        response.status(SC_OK);
        try
        {
            writeMetrics(response);
        }
        catch (IOException e)
        {
            return e.getMessage();
        }
        return null;
    }

    private void writeMetrics(final Response response) throws IOException
    {
        final ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();
        writer.writeValue(response.raw().getOutputStream(), metricRegistry);
    }
}
