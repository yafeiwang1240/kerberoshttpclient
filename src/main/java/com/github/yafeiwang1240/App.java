package com.github.yafeiwang1240;

import com.alibaba.fastjson.JSONObject;
import com.github.yafeiwang1240.httpclient.HttpClient;
import org.apache.http.client.fluent.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Hello World!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        HttpClient client = HttpClient.getInstance();
        for(int i = 0; i < 1; i++) {
            try {
                Response response = client.execute(HttpClient.EnumHttpMethod.GET, "http://open.iciba.com/dsapi/");
                Map<String, Object> map = JSONObject.parseObject(response.returnContent().asString(), Map.class);
                System.out.println(map.get("note"));
                System.out.println(map.get("content"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
        }