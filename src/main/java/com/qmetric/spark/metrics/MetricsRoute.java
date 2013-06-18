package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.MimeTypes;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MetricsRoute extends Route
{
    public static final String PATH = "/metrics";

    private final MetricRegistry metricRegistry;

    public MetricsRoute(final MetricRegistry metricRegistry)
    {
        super(PATH);
        this.metricRegistry = metricRegistry;
    }

    @Override public Object handle(final Request request, final Response response)
    {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, true));

        response.raw().setContentType(MimeTypes.Type.TEXT_JSON.asString());
        response.raw().setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        response.raw().setStatus(HttpServletResponse.SC_OK);
        try
        {
            final ServletOutputStream outputStream = response.raw().getOutputStream();
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(outputStream, metricRegistry);
        }
        catch (IOException e)
        {
            return e.getMessage();
        }
        return null;
    }
}
