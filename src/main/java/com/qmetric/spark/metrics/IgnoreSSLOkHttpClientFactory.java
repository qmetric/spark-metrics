package com.qmetric.spark.metrics;

import com.squareup.okhttp.OkHttpClient;
import retrofit.client.Client;
import retrofit.client.OkClient;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class IgnoreSSLOkHttpClientFactory {
    public static Client getClient() {
        try {
            OkHttpClient client = new OkHttpClient();
            client.setSslSocketFactory(getSSLSocketFactory());
            client.setHostnameVerifier(new AllowAllHostnameVerifier());
            return new OkClient(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAllX509Certificates()}, null);
        return sc.getSocketFactory();
    }

    static class TrustAllX509Certificates implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    static class AllowAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
