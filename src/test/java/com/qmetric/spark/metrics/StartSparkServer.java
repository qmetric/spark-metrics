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
                     MetricSetUpTest.class,  //
                     ResultModifierTest.class  //
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
            MetricSetUp.registerRoute();
            MetricSetUp.timeAndMeterRoute("/ping", new PingRoute(), MetricSetUp.Verb.GET);
            Thread.sleep(500);
        }

        @Override protected void after()
        {
            super.after();
        }
    };
}
