package com.qmetric.spark.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.codahale.metrics.MetricRegistry.name;

public class RouteMeterWrapper implements Route
{
    private final Route route;

    private final Meter meter;

    public RouteMeterWrapper(final MetricRegistry metricRegistry, final Route route)
    {
        this.route = route;
        meter = metricRegistry.meter(name(route.getClass(), "meter"));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        meter.mark();
        return route.handle(request, response);
    }
}
