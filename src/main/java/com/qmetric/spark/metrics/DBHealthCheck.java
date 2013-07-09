package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static com.qmetric.spark.metrics.DBHealthCheck.HealthCheckQuery.MYSQL_HEALTH_CHECK_QUERY;

public class DBHealthCheck extends HealthCheck
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DBHealthCheck.class);
    private final DataSource dataSource;

    private final UnHealthyMessage unHealthyMessage;

    private final HealthyMessage healthyMessage;

    private final HealthCheckQuery healthCheckQuery;

    private String url;

    private String userName;

    public DBHealthCheck(final DataSource dataSource) throws SQLException
    {
        this.dataSource = dataSource;
        setDBConnectionInfo();
        healthCheckQuery = MYSQL_HEALTH_CHECK_QUERY;
        unHealthyMessage = new UnHealthyMessage(url, userName);
        healthyMessage = new HealthyMessage(url, userName);
    }

    public DBHealthCheck(final DataSource dataSource, final HealthCheckQuery healthCheckQuery) throws SQLException
    {
        this.dataSource = dataSource;
        setDBConnectionInfo();
        this.healthCheckQuery = healthCheckQuery;
        unHealthyMessage = new UnHealthyMessage(url, userName);
        healthyMessage = new HealthyMessage(url, userName);
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
                return Result.healthy(healthyMessage.message);
            }
            else
            {
                return Result.unhealthy(unHealthyMessage.message);
            }
        }
        catch (Exception e)
        {
            return Result.unhealthy(unHealthyMessage.message);
        }
    }

    private void setDBConnectionInfo()
    {
        try
        {
            final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            url = metaData.getURL();
            userName = metaData.getUserName();
        }
        catch (SQLException e)
        {
            LOGGER.error("Unable to get db Url");
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

        public UnHealthyMessage(final String url, final String userName) throws SQLException
        {
            message = String.format("Unable to connect to database : %s username %s", url, userName);
        }
    }

    static class HealthyMessage
    {
        public final String message;

        public HealthyMessage(final String url, final String userName) throws SQLException
        {
            message = String.format("Connection to database successful Host: %s username %s", url, userName);
        }

    }
}
