package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.RoutePathReader;

import static com.codahale.metrics.MetricRegistry.name;

public class RouteTimerWrapper extends Route
{

    public static final String TIMER_NAME_PREFIX = "timer";

    private final Route route;

    private final Timer timer;

    public RouteTimerWrapper(final MetricRegistry metricRegistry, final Route route)
    {
        super(RoutePathReader.path(route));
        this.route = route;
        this.timer = metricRegistry.timer(name(TIMER_NAME_PREFIX, RoutePathReader.path(route).split("/")));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        final Timer.Context context = timer.time();
        try
        {
            return route.handle(request, response);
        }
        finally
        {
            context.stop();
        }
    }
}
