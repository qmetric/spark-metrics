package com.qmetric.spark.metrics;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import spark.Spark;

import static com.qmetric.spark.metrics.SparkConstants.PORT;

@RunWith(Suite.class) @Suite.SuiteClasses(
        {DBHealthCheckTest.class, HealthCheckRouteTest.class, MeterFiltersTest.class, MetricsRouteTest.class, PingRouteTest.class, RouteMeterWrapperTest.class,
         RouteTimerWrapperTest.class, ServerHealthCheckTest.class, TimerFiltersTest.class, HostHealthCheckTest.class})
public class StartSparkServer
{
    @ClassRule
    public static ExternalResource externalResource = new ExternalResource()
    {
        @Override protected void before() throws Throwable
        {
            super.before();
            Spark.setPort(PORT);
        }

        @Override protected void after()
        {
            super.after();
        }
    };
}
