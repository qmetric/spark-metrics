package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;

import static com.qmetric.spark.metrics.DBHealthCheck.HealthCheckQuery.MYSQL_HEALTH_CHECK_QUERY;

public class DBHealthCheck extends HealthCheck
{
    private final DataSource dataSource;

    private final UnHealthyMessage unHealthyMessage;

    private final HealthCheckQuery healthCheckQuery;

    public DBHealthCheck(final DataSource dataSource)
    {
        this.dataSource = dataSource;
        healthCheckQuery = MYSQL_HEALTH_CHECK_QUERY;
        unHealthyMessage = new UnHealthyMessage();
    }

    public DBHealthCheck(final DataSource dataSource, final HealthCheckQuery healthCheckQuery)
    {
        this.dataSource = dataSource;
        this.healthCheckQuery = healthCheckQuery;
        unHealthyMessage = new UnHealthyMessage();
    }

    public DBHealthCheck(final DataSource dataSource, final UnHealthyMessage unHealthyMessage)
    {
        healthCheckQuery = MYSQL_HEALTH_CHECK_QUERY;
        this.dataSource = dataSource;
        this.unHealthyMessage = unHealthyMessage;
    }

    public DBHealthCheck(final DataSource dataSource, final HealthCheckQuery healthCheckQuery, final UnHealthyMessage unHealthyMessage)
    {

        this.dataSource = dataSource;
        this.healthCheckQuery = healthCheckQuery;
        this.unHealthyMessage = unHealthyMessage;
    }

    @Override protected Result check() throws Exception
    {
        try
        {
            final Connection connection = dataSource.getConnection();

            final Statement statement = connection.createStatement();

            final boolean execute = statement.execute(healthCheckQuery.sql);

            if (execute)
            {
                return Result.healthy();
            }
            else
            {
                return Result.unhealthy(unHealthyMessage.message);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(e);
        }
    }

    static class HealthCheckQuery
    {
        static HealthCheckQuery MYSQL_HEALTH_CHECK_QUERY = new HealthCheckQuery("SELECT 1");

        private final String sql;

        public HealthCheckQuery(final String sql)
        {
            this.sql = sql;
        }
    }

    static class UnHealthyMessage
    {
        public String message = "Unable to connect to database";

        public UnHealthyMessage()
        {
        }

        public UnHealthyMessage(final String message)
        {
            this.message = message;
        }
    }
}
