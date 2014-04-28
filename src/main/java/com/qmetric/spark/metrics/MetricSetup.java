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
        Spark.get(new MetricsRoute(METRIC_REGISTRY));
    }

    public static void timeRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(makeTimerRoute(path, route));break;
            case POST:Spark.post(makeTimerRoute(path, route));break;
            case PUT:Spark.put(makeTimerRoute(path, route));break;
            case DELETE:Spark.delete(makeTimerRoute(path, route));break;
        }
    }

    public static Route makeTimerRoute(final String path, final Route route)
    {
        return new RouteTimerWrapper(path, METRIC_REGISTRY, route);
    }

    public static void meterRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(makeMeterRoute(path, route));break;
            case POST:Spark.post(makeMeterRoute(path, route));break;
            case PUT:Spark.put(makeMeterRoute(path, route));break;
            case DELETE:Spark.delete(makeMeterRoute(path, route));break;
        }
    }

    public static Route makeMeterRoute(final String path, final Route route)
    {
        return new RouteMeterWrapper(path, METRIC_REGISTRY, route);
    }

    public static void timeAndMeterRoute(final String path, final Route route, final Verb verb)
    {
        registerRoute();
        switch (verb)
        {
            case GET:Spark.get(makeMeterRoute(path, makeTimerRoute(path, route)));break;
            case POST:Spark.post(makeMeterRoute(path, makeTimerRoute(path, route)));break;
            case PUT:Spark.put(makeMeterRoute(path, makeTimerRoute(path, route)));break;
            case DELETE:Spark.delete(makeMeterRoute(path, makeTimerRoute(path, route)));break;
        }
    }
}
