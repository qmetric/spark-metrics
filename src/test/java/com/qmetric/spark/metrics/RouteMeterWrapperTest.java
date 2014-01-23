package com.qmetric.spark.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.qmetric.spark.metrics.RouteMeterWrapper.METER_NAME_PREFIX;
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyInteger;
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static spark.RoutePathReader.path;

public class RouteMeterWrapperTest
{

    final String path = anyLetterString();

    final String expectedResponse = anyLetterString();

    final MetricRegistry metricRegistry = mock(MetricRegistry.class);

    final Meter meter = new Meter();

    {
        when(metricRegistry.meter(any(String.class))).thenReturn(meter);
    }

    final Route delegateRoute = spy(new Route(path)
    {
        @Override public Object handle(final Request request, final Response response)
        {
            return expectedResponse;
        }
    });

    Route route = new RouteMeterWrapper(metricRegistry, delegateRoute);

    @Test
    public void gets_route_path_from_delegate_route()
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
    public void registers_meter_and_invokes_when_handling_request()
    {
        verify(metricRegistry).meter(any(String.class));
        final Integer count = anyInteger(10, 100);
        invokeRoute(count);
        assertThat(meter.getCount()).isEqualTo(count);
    }

    @Test
    public void builds_meter_name_from_route_path()
    {
        ArgumentCaptor<String> meterName = ArgumentCaptor.forClass(String.class);
        verify(metricRegistry).meter(meterName.capture());
        assertThat(meterName.getValue()).contains(path);
        assertThat(meterName.getValue()).startsWith(METER_NAME_PREFIX);
    }

    private void invokeRoute(final Integer invocationsCount)
    {
        for (int i = 0; i < invocationsCount; i++)
        {
            route.handle(mock(Request.class), mock(Response.class));
        }
    }
}
