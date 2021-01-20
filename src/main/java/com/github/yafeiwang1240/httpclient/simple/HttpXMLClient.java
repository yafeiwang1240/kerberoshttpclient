package com.github.yafeiwang1240.httpclient.simple;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpXMLClient {
    public static String post(String address, Map<String, String> headers, Map<String, Object> params) throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().setConnectionTimeToLive(30, TimeUnit.SECONDS).build();
        try {
            HttpPost request = new HttpPost(address);
            for (String key : headers.keySet()) {
                request.setHeader(key, headers.get(key));
            }
            List<NameValuePair> data = buildPostData(params);
            request.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStreamReader reader = new InputStreamReader(entity.getContent());
            BufferedReader br = new BufferedReader(reader);
            String str = br.readLine();
            StringBuilder stb = new StringBuilder();
            while (str != null) {
                stb.append(str);
                str = br.readLine();
            }
            br.close();
            response.close();

            return stb.toString();
        } finally {
            client.close();
        }
    }

    public static String post(String address, Map<String, Object> params) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return post(address, headers, params);
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
