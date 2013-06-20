package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.qmetric.spark.metrics.DBHealthCheck.HealthCheckQuery.MYSQL_HEALTH_CHECK_QUERY;

public class DBHealthCheck extends HealthCheck
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DBHealthCheck.class);
    private final DataSource dataSource;

    private final UnHealthyMessage unHealthyMessage;

    private final HealthCheckQuery healthCheckQuery;

    private String url;

    public DBHealthCheck(final DataSource dataSource)
    {
        this.dataSource = dataSource;
        setUrl();
        healthCheckQuery = MYSQL_HEALTH_CHECK_QUERY;
        unHealthyMessage = new UnHealthyMessage(url);
    }

    private void setUrl()
    {
        try
        {
            url = dataSource.getConnection().getMetaData().getURL();
        }
        catch (SQLException e)
        {
            LOGGER.error("Unable to get db Url");
        }
    }

    public DBHealthCheck(final DataSource dataSource, final HealthCheckQuery healthCheckQuery)
    {
        this.dataSource = dataSource;
        setUrl();
        this.healthCheckQuery = healthCheckQuery;
        unHealthyMessage = new UnHealthyMessage(url);
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
        public final String message;

        public UnHealthyMessage(final String url)
        {
            message = String.format("Unable to connect to database : %s", url);
        }
    }
}
