package com.githup.yafeiwang1240;

import com.githup.yafeiwang1240.httpclient.HttpClient;
import org.apache.http.client.fluent.Response;

import java.io.IOException;

/**
 * Hello World!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        HttpClient client = HttpClient.getInstance();
        for(int i = 0; i < 10; i++) {
            try {
                Response response = client.execute(HttpClient.EnumHttpMethod.GET, "http://open.iciba.com/dsapi/");
                System.out.println(response.returnContent().asString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
