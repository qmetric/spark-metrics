package com.qmetric.spark.metrics;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

class SparkTestUtil
{

    private final HttpHost targetHost;

    private final DefaultHttpClient httpClient;

    public SparkTestUtil(final int port)
    {
        this.targetHost = new HttpHost("localhost", port);
        this.httpClient = new DefaultHttpClient();
    }

    public HttpResponse get(final String path)
    {
        try
        {
            return httpClient.execute(targetHost, new HttpGet(addRoot(path)));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String addRoot(final String path)
    {
        return path.startsWith("/") ? path : "/" + path;
    }
}
