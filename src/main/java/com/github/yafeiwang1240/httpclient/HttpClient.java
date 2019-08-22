package com.github.yafeiwang1240.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于apache http二次封装
 *
 * @author wangyafei
 */
public class HttpClient {

    private static HttpClient instance;

    private static Object[] lock = new Object[0];

    public static HttpClient getInstance() {
        if(instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    public enum EnumHttpMethod {
        POST(0),
        GET(1);

        int value;
        EnumHttpMethod(int value){
            this.value = value;
        }
        int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public Response execute(EnumHttpMethod method, String url) throws IOException {
        return execute(method, url, null, null, null);
    }

    public Response execute(EnumHttpMethod method, String url, int timeout) throws IOException {
        return execute(method, url, null, null, timeout);
    }

    public Response execute(EnumHttpMethod method, String url, Object body, Map<String, String> headers) throws IOException {
        return execute(method, url, body, headers, null);
    }

    public Response execute(EnumHttpMethod method, String url, Map<String, String> headers, int timeout) throws IOException {
        return execute(method, url, null, headers, timeout);
    }

    public Response execute(EnumHttpMethod method, String url, Object body, Integer timeout) throws IOException {
        return execute(method, url, body, null, timeout);
    }

    public Response execute(EnumHttpMethod method, String url, Object body, Map<String, String> headers, Integer timeout) throws IOException {
        Request request = null;
        switch (method) {
            case GET:
                request = Request.Get(url);
                break;
            case POST:
                request = Request.Post(url);
                break;
            default:
                throw new IllegalArgumentException("no such method: " + method.toString());
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request = request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if(timeout != null) {
            request.connectTimeout(timeout);
        }
        Response response;
        if (body != null) {
            if (body instanceof String) {
                response = request.bodyString((String) body, ContentType.APPLICATION_JSON).execute();
            } else if (body instanceof Map) {
                List<NameValuePair> data = buildPostData((Map<String, Object>) body);
                response = request.body(new UrlEncodedFormEntity(data, "UTF-8")).execute();
            } else {
                response = request.bodyString(JSONObject.toJSONString(body), ContentType.APPLICATION_JSON).execute();
            }
        } else {
            response = request.execute();
        }
        return response;
    }

    private static List<NameValuePair> buildPostData(Map<String, Object> params) {
        if (params == null || params.size() <= 0) {
            return new ArrayList<NameValuePair>(0);
        }
        List<NameValuePair> ret = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                NameValuePair np = new BasicNameValuePair(key, value.toString());
                ret.add(np);
            }
        }
        return ret;
    }
}
