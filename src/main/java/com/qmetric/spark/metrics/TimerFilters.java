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

    public TimerFilters(final MetricRegistry metricRegistry, final Class<?> aClass, final String... name)
    {
        beforeFilter = new BeforeFilter(metricRegistry, aClass, name);
        afterFilter = new AfterFilter();
    }

    public BeforeFilter beforeFilter()
    {
        return beforeFilter;
    }

    public AfterFilter afterFilter()
    {
        return afterFilter;
    }

    private class BeforeFilter implements Filter
    {
        private final Timer timer;

        public BeforeFilter(final MetricRegistry metricRegistry, final Class<?> aClass, final String... name)
        {
            timer = metricRegistry.timer(name(aClass, name));
        }

        @Override public void handle(final Request request, final Response response)
        {
            context = timer.time();
        }
    }

    private class AfterFilter implements Filter
    {
        @Override public void handle(final Request request, final Response response)
        {
            context.stop();
        }
    }
}
