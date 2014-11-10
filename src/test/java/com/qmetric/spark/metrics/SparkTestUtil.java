package com.qmetric.spark.metrics;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

import static com.qmetric.spark.metrics.SparkConstants.PORT;
import static java.lang.String.format;

class SparkTestUtil
{
    private static final String URL_TEMPLATE = "http://localhost:%d/%s";

    private final CloseableHttpClient httpClient;

    public SparkTestUtil()
    {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();

        final PoolingHttpClientConnectionManager connectionManager1 = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        httpClient = HttpClients.createMinimal(connectionManager1);
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
