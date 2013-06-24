package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import spark.Spark;

import javax.sql.DataSource;

import java.net.URL;
import java.sql.SQLException;

public class HealthCheckSetup
{
    private static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();

    public static void registerRoute()
    {
        Spark.get(new HealthCheckRoute(HEALTH_CHECK_REGISTRY));
    }

    public static void addHealthCheck(final String name, final String host)
    {
        registerRoute();
        HEALTH_CHECK_REGISTRY.register(name, new HostHealthCheck(host));
    }

    public static void addHealthCheck(final String custom, final URL url)
    {
        registerRoute();
        HEALTH_CHECK_REGISTRY.register(custom, new CustomHealthCheck(url));
    }

    public static void addHealthCheck(final String db, final DataSource dataSource, final String query) throws SQLException
    {
        registerRoute();
        HEALTH_CHECK_REGISTRY.register(db, new DBHealthCheck(dataSource, new DBHealthCheck.HealthCheckQuery(query)));
    }

    public static void addHealthCheck(final String db, final DataSource dataSource) throws SQLException
    {
        registerRoute();
        HEALTH_CHECK_REGISTRY.register(db, new DBHealthCheck(dataSource));
    }
}
