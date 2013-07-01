package com.qmetric.spark.metrics;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import spark.Spark;

import static com.qmetric.spark.metrics.SparkConstants.PORT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
                     DBHealthCheckTest.class, //
                     MeterFiltersTest.class, //
                     MetricsRouteTest.class, //
                     PingRouteTest.class, //
                     RouteMeterWrapperTest.class, //
                     RouteTimerWrapperTest.class, //
                     ServerHealthCheckTest.class, //
                     TimerFiltersTest.class, //
                     HostHealthCheckTest.class, //
                     CustomHealthCheckTest.class, //
                     HealthCheckSetupTest.class, //
                     MetricSetupTest.class  //
                    })
public class StartSparkServer
{
    @ClassRule
    public static ExternalResource externalResource = new ExternalResource()
    {
        @Override protected void before() throws Throwable
        {
            super.before();
            Spark.setPort(PORT);
            HealthCheckSetup.registerRoute();
            MetricSetup.registerRoute();
            MetricSetup.timeAndMeterRoute("/ping", new PingRoute(), MetricSetup.Verb.GET);
            Thread.sleep(500);
        }

        @Override protected void after()
        {
            super.after();
        }
    };
}
