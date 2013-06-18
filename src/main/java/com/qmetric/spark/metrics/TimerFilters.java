package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import spark.Filter;
import spark.Request;
import spark.Response;

import static com.codahale.metrics.MetricRegistry.name;

public class TimerFilters
{
    private final TimerFilters.AfterFilter afterFilter;

    private Timer.Context context;

    private final BeforeFilter beforeFilter;

    public TimerFilters(final String path, final MetricRegistry metricRegistry, final Class<?> aClass, final String... name)
    {
        beforeFilter = new BeforeFilter(path, metricRegistry, aClass, name);
        afterFilter = new AfterFilter(path);
    }

    public BeforeFilter beforeFilter()
    {
        return beforeFilter;
    }

    public AfterFilter afterFilter()
    {
        return afterFilter;
    }

    private class BeforeFilter extends Filter
    {
        private final Timer timer;

        public BeforeFilter(final String path, final MetricRegistry metricRegistry, final Class<?> aClass, final String... name)
        {
            super(path);
            timer = metricRegistry.timer(name(aClass, name));
        }

        @Override public void handle(final Request request, final Response response)
        {
            context = timer.time();
        }
    }

    private class AfterFilter extends Filter
    {
        public AfterFilter(final String path)
        {
            super(path);
        }

        @Override public void handle(final Request request, final Response response)
        {
            context.stop();
        }
    }
}
