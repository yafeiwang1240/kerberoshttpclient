package com.github.yafeiwang1240.httpclient.simple;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProxyHttpClient {

    public static HttpClient client;

    public static String rpcPost(String address, Map<String, Object> params) {
        String api = address;
        URI uri = getURI(api);
        HttpPost httpPost = new HttpPost(uri);
        config(httpPost);
        try {
            List<NameValuePair> data = buildPostData(params);
            httpPost.setEntity(new UrlEncodedFormEntity(data, Consts.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
            HttpResponse httpResponse = execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, Consts.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private static RequestConfig config;

    private static void config(HttpPost request) {
        if (config == null) {
            final RequestConfig.Builder builder;
            if (client instanceof Configurable) {
                builder = RequestConfig.copy(((Configurable) client).getConfig());
            } else {
                builder = RequestConfig.custom();
            }
            builder.setContentCompressionEnabled(false);
            config = builder.build();
        }
        request.setConfig(config);
    }

    public static HttpResponse execute(HttpUriRequest request) throws IOException {
        return execute(request, false);
    }

    public static HttpResponse execute(HttpUriRequest request, boolean doPreserveHost) throws IOException {
        HttpHost httpHost = getHttpHost(request.getURI());
        if (!doPreserveHost) {
            String headerValue = httpHost.getHostName();
            if (httpHost.getPort() != -1)
                headerValue += ":"+httpHost.getPort();
            request.setHeader(HttpHeaders.HOST, headerValue);
        }
        return client.execute(httpHost, request);
    }

    private static ConcurrentMap<String, URI> uriCache = new ConcurrentHashMap<String, URI>();

    private static URI getURI(String address) {
        URI uri = uriCache.get(address);
        if (uri == null) {
            uriCache.put(address, uri = URI.create(address));
        }
        return uri;
    }

    private static ConcurrentMap<URI, HttpHost> httpHostCache = new ConcurrentHashMap<URI, HttpHost>();

    private static HttpHost getHttpHost(URI uri) {
        HttpHost httpHost = httpHostCache.get(uri);
        if (httpHost == null) {
            httpHostCache.put(uri, httpHost = URIUtils.extractHost(uri));
        }
        return httpHost;
    }

    private static List<NameValuePair> buildPostData(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return new ArrayList<NameValuePair>(0);
        }
        List<NameValuePair> ret = new ArrayList<NameValuePair>(params.size());
        for (String key : params.keySet()) {
            Object p = params.get(key);
            if (key != null && p != null) {
                NameValuePair np = new BasicNameValuePair(key, p.toString());
                ret.add(np);
            }
        }
        return ret;
    }
}
