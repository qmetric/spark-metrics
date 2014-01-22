package com.qmetric.spark.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.codahale.metrics.MetricRegistry.name;
import static spark.RoutePathReader.path;

public class RouteMeterWrapper extends Route
{

    public static final String METER_NAME_PREFIX = "meter";

    private final Route route;

    private final Meter meter;

    public RouteMeterWrapper(final MetricRegistry metricRegistry, final Route route)
    {
        super(path(route));
        this.route = route;
        this.meter = metricRegistry.meter(name(METER_NAME_PREFIX, path(route).split("/")));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        meter.mark();
        return route.handle(request, response);
    }
}
