package com.qmetric.spark.metrics;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockDataSource
{
    public static DataSource failingDataSource() throws SQLException
    {
        DataSource ds = mock(DataSource.class);
        final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        when(databaseMetaData.getURL()).thenReturn("url");

        final Connection connection = mock(Connection.class);
        when(ds.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);

        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenReturn(false);
        return ds;
    }

    @SuppressWarnings("unchecked") public static DataSource exceptionThrowingDataSource() throws SQLException
    {
        DataSource ds = mock(DataSource.class);
        final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        when(databaseMetaData.getURL()).thenReturn("url");

        final Connection connection = mock(Connection.class);
        when(ds.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);

        when(connection.createStatement()).thenThrow(Exception.class);
        return ds;
    }
}
