package com.qmetric.spark.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.codahale.metrics.MetricRegistry.name;

public class RouteMeterWrapper extends Route
{

    private final Route route;

    private final Meter meter;

    protected RouteMeterWrapper(final String path, final MetricRegistry metricRegistry, final Route route)
    {
        super(path);
        this.route = route;
        meter = metricRegistry.meter(name("meter", path.replace("/","")));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        meter.mark();
        return route.handle(request, response);
    }
}
