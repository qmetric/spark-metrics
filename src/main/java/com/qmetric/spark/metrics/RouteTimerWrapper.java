package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.codahale.metrics.MetricRegistry.name;

public class RouteTimerWrapper extends Route
{

    private final Route route;

    private final Timer timer;

    public RouteTimerWrapper(final String path, final MetricRegistry metricRegistry, final Route route)
    {
        super(path);
        this.route = route;
        timer = metricRegistry.timer(name("timer", path.replace("/","")));
    }

    @Override public Object handle(final Request request, final Response response)
    {
        final Timer.Context context = timer.time();
        try {
            return route.handle(request, response);
        }finally {
            context.stop();
        }
    }
}
