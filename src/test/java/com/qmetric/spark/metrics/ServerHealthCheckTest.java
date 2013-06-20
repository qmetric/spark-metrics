package com.qmetric.spark.metrics;

import com.codahale.metrics.health.HealthCheck;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServerHealthCheckTest
{
    private static Server server;

    @BeforeClass
    public static void startServer() throws Exception
    {
        ContextHandler ping = new ContextHandler();
        ping.setContextPath("/ping");
        ping.setHandler(new PingHandler());

        ContextHandler pingWithAppContext = new ContextHandler();
        pingWithAppContext.setContextPath("/app-context/ping");
        pingWithAppContext.setHandler(new PingHandler());

        HandlerCollection handlerCollection =  new HandlerCollection();
        handlerCollection.addHandler(ping);
        handlerCollection.addHandler(pingWithAppContext);

        server = new Server(47563);
        server.setHandler(handlerCollection);
        server.start();
    }

    @Test
    public void shouldCheckIfServerIsUp() throws Exception
    {
        final HostHealthCheck hostHealthCheck = new HostHealthCheck("localhost:47563");

        assertHostIsHealthy(hostHealthCheck);
    }

    private void assertHostIsHealthy(final HostHealthCheck hostHealthCheck) throws Exception
    {
        final HealthCheck.Result check = hostHealthCheck.check();
        assertThat(check.isHealthy(), is(true));
    }

    @Test
    public void shouldUseAppContextIfRequired() throws Exception
    {
        final HostHealthCheck hostHealthCheck = new HostHealthCheck("localhost:47563", "app-context");
        assertHostIsHealthy(hostHealthCheck);
    }

    @Test
    public void willFailIfCannotConnectToServer() throws Exception
    {
        final HostHealthCheck hostHealthCheck = new HostHealthCheck("localhost:47564");
        final HealthCheck.Result check = hostHealthCheck.check();
        assertThat(check.isHealthy(), is(false));
        assertThat(check.getMessage(), notNullValue());
        assertThat(check.getError(), notNullValue());
    }

    @AfterClass
    public static void stopServer() throws Exception
    {
        server.stop();
    }

    static class PingHandler extends AbstractHandler
    {
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            response.setContentType("text/plain;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().write("pong\n");
            response.getWriter().flush();
        }
    }
}
