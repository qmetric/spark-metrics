package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static com.qmetric.spark.metrics.MockDataSource.exceptionThrowingDataSource;
import static com.qmetric.spark.metrics.MockDataSource.failingDataSource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        final String message = "Unable to connect to database : url";
        final DBHealthCheck dbHealthCheck = new DBHealthCheck(failingDataSource(), new DBHealthCheck.HealthCheckQuery("SELECT 1 FROM INFORMATION_SCHEMA"));

        final HealthCheck.Result check = dbHealthCheck.check();

        assertThat(check.isHealthy(), is(false));
        assertThat(check.getMessage(), equalTo(message));
    }

    @Test
    public void shouldCatchExceptionAndSendMessage() throws Exception
    {
        final DataSource ds = exceptionThrowingDataSource();
        final DBHealthCheck dbHealthCheck = new DBHealthCheck(ds);

        final HealthCheck.Result check = dbHealthCheck.check();

        assertThat(check.isHealthy(), is(false));
        assertThat(check.getError(),  is(instanceOf(Exception.class)));
    }
}
