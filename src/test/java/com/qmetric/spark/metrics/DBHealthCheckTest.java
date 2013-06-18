package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DBHealthCheckTest
{
    private BasicDataSource dataSource;

    @Before
    public void init()
    {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:dbhc");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
    }

    @Test
    public void shouldUseSpecificQuery() throws Exception
    {
        final DBHealthCheck dbHealthCheck = new DBHealthCheck(dataSource, new DBHealthCheck.HealthCheckQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS"));

        final HealthCheck.Result check = dbHealthCheck.check();

        assertThat(check.isHealthy(), is(true));
    }

    @Test
    public void shouldSendSpecificMessage() throws Exception
    {
        final String message = "Unable to connect to db";
        final DBHealthCheck dbHealthCheck = new DBHealthCheck(failingDataSource(), new DBHealthCheck.HealthCheckQuery("SELECT 1 FROM INFORMATION_SCHEMA"), new DBHealthCheck.UnHealthyMessage(
                message));

        final HealthCheck.Result check = dbHealthCheck.check();

        assertThat(check.isHealthy(), is(false));
        assertThat(check.getMessage(), equalTo(message));
    }

    @Test
    public void shouldCatchExceptionAndSendMessage() throws Exception
    {
        final DBHealthCheck dbHealthCheck = new DBHealthCheck(null);

        final HealthCheck.Result check = dbHealthCheck.check();

        assertThat(check.getError(),  is(instanceOf(NullPointerException.class)));
    }

    private DataSource failingDataSource() throws SQLException
    {
        DataSource ds = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(ds.getConnection()).thenReturn(connection);
        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(false);
        return ds;
    }
}
