package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import spark.Route;
import spark.Spark;

public class MetricSetup
{
    public enum Verb
    {
        GET, POST, PUT, DELETE
    }

    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static void registerRoute()
    {
        Spark.get("/metrics", new MetricsRoute(METRIC_REGISTRY));
    }

    public static void timeRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(path, makeTimerRoute(route));break;
            case POST:Spark.post(path, makeTimerRoute(route));break;
            case PUT:Spark.put(path, makeTimerRoute(route));break;
            case DELETE:Spark.delete(path, makeTimerRoute(route));break;
        }
    }

    public static Route makeTimerRoute(final Route route)
    {
        return new RouteTimerWrapper(METRIC_REGISTRY, route);
    }

    public static void meterRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(path, makeMeterRoute(route));break;
            case POST:Spark.post(path, makeMeterRoute(route));break;
            case PUT:Spark.put(path, makeMeterRoute(route));break;
            case DELETE:Spark.delete(path, makeMeterRoute(route));break;
        }
    }

    public static Route makeMeterRoute(final Route route)
    {
        return new RouteMeterWrapper(METRIC_REGISTRY, route);
    }

    public static void timeAndMeterRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(path, makeMeterRoute(makeTimerRoute(route)));break;
            case POST:Spark.post(path, makeMeterRoute(makeTimerRoute(route)));break;
            case PUT:Spark.put(path, makeMeterRoute(makeTimerRoute(route)));break;
            case DELETE:Spark.delete(path, makeMeterRoute(makeTimerRoute(route)));break;
        }
    }
}
