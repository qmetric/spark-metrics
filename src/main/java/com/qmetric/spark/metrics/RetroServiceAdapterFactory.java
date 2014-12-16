package com.qmetric.spark.metrics;

import retrofit.Endpoint;
import retrofit.RestAdapter;

public class RetroServiceAdapterFactory {

    public static <T> T service(Class<T> service, Endpoint endpoint) {

        RestAdapter.Builder withEndpoint = new RestAdapter.Builder().setEndpoint(endpoint);

        RestAdapter.Builder withEndpointAndClient = withEndpoint.setClient(IgnoreSSLOkHttpClientFactory.getClient());

        return withEndpointAndClient.build().create(service);
    }
}
