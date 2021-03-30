package com.github.yafeiwang1240.httpclient.check;

import com.github.yafeiwang1240.httpclient.simple.HttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Response;

public class HttpHealthChecker extends AbstractHealthChecker {

    private String url;

    public HttpHealthChecker(String url) {
        if (!url.startsWith("/")) {
            url = "/" + url.trim();
        }
        this.url = url;
    }

    @Override
    public boolean check(InstanceInfo instanceInfo) {
        boolean valid = false;
        try {
            String uri = "http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + url;
            Response execute = HttpClient.getInstance().execute(HttpClient.EnumHttpMethod.GET, uri, 2000);
            int statusCode = execute.returnResponse().getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                valid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valid;
    }
}
