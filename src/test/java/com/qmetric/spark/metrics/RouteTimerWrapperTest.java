package com.qmetric.spark.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.qmetric.spark.metrics.RouteTimerWrapper.TIMER_NAME_PREFIX;
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyInteger;
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static spark.RoutePathReader.path;

public class RouteTimerWrapperTest
{

    final String path = anyLetterString();

    final String expectedResponse = anyLetterString();

    final MetricRegistry metricsRegister = mock(MetricRegistry.class);

    Timer timer = new Timer();

    {
        when(metricsRegister.timer(any(String.class))).thenReturn(timer);
    }

    final Route delegateRoute = spy(new Route(path)
    {
        @Override public Object handle(final Request request, final Response response)
        {
            return expectedResponse;
        }
    });

    private Route route = new RouteTimerWrapper(metricsRegister, delegateRoute);

    @Test
    public void gets_route_from_delegate_route()
    {
        assertThat(path(route)).isEqualTo(path(delegateRoute));
    }

    @Test
    public void delegates_handling_to_inner_route()
    {
        final Request request = mock(Request.class);
        final Response response = mock(Response.class);
        final Object responseText = route.handle(request, response);
        assertThat(responseText).isEqualTo(expectedResponse);
        verify(delegateRoute).handle(request, response);
    }

    @Test
    public void registers_timer_and_invokes_when_handling_request()
    {
        verify(metricsRegister).timer(any(String.class));
        final Integer count = anyInteger(10, 100);
        invokeRoute(count);
        assertThat(timer.getCount()).isEqualTo(count);
    }

    @Test
    public void builds_meter_name_from_route_path()
    {
        ArgumentCaptor<String> timerName = ArgumentCaptor.forClass(String.class);
        verify(metricsRegister).timer(timerName.capture());
        assertThat(timerName.getValue()).contains(path);
        assertThat(timerName.getValue()).startsWith(TIMER_NAME_PREFIX);
    }

    private void invokeRoute(final Integer invocationsCount)
    {
        for (int i = 0; i < invocationsCount; i++)
        {
            route.handle(mock(Request.class), mock(Response.class));
        }
    }
}
