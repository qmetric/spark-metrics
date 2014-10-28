package com.qmetric.spark.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import spark.Filter;
import spark.Request;
import spark.Response;

import static com.codahale.metrics.MetricRegistry.name;

public class MeterFilters
{
    private final Meter meter;

    private final MeterFilters.BeforeFilter beforeFilter;

    public MeterFilters(final MetricRegistry metricRegistry, final Class<?> aClass, final String timer)
    {
        meter = metricRegistry.meter(name(aClass, timer));
        beforeFilter = new BeforeFilter();
    }

    public Filter beforeFilter()
    {
        return beforeFilter;
    }

    class BeforeFilter implements Filter
    {
        @Override public void handle(final Request request, final Response response)
        {
            meter.mark();
        }
    }
}
