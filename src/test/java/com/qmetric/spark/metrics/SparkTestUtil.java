package com.qmetric.spark.metrics;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;

import java.io.IOException;

import static com.qmetric.spark.metrics.SparkConstants.PORT;
import static java.lang.String.format;

class SparkTestUtil
{
    private final DefaultHttpClient httpClient;

    private static final String URL_TEMPLATE = "http://localhost:%d/%s";

    public SparkTestUtil(final int port)
    {
        Scheme http = new Scheme("http", port, PlainSocketFactory.getSocketFactory());
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(http);
        ClientConnectionManager connectionManager = new BasicClientConnectionManager(schemeRegistry);
        httpClient = new DefaultHttpClient(connectionManager);
    }

    public HttpResponse get(final String path)
    {
        return sendRequest(new HttpGet(format(URL_TEMPLATE, PORT, cleanPath(path))));
    }

    public HttpResponse delete(final String path)
    {
        return sendRequest(new HttpDelete(format(URL_TEMPLATE, PORT, cleanPath(path))));
    }

    private HttpResponse sendRequest(final HttpUriRequest request)
    {
        try
        {
            return httpClient.execute(request);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String cleanPath(final String path)
    {
        return path.replace("/", "");
    }
}
