package com.github.yafeiwang1240.httpclient.kerberos;

import com.github.yafeiwang1240.httpclient.kerberos.handler.InvokeHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * kerberos httpclient factory
 * @author wangyafei
 * @date 2021-01-19
 */
public class KerberosHttpclientFactory {

    private volatile static KerberosHttpclient httpclient;

    /**
     * get kerberos httpclient
     * @return
     */
    public synchronized static KerberosHttpclient getKerberosHttpclient() {
        if (httpclient != null) {
            return httpclient;
        }
        httpclient = (KerberosHttpclient) Proxy.newProxyInstance(KerberosHttpclientFactory.class.getClassLoader(),
                new Class[]{KerberosHttpclient.class}, new InvokeHandler());
        return httpclient;
    }

    /**
     * get kerberos httpclient with builder
     * @param builder
     * @return
     */
    public synchronized static KerberosHttpclient getKerberosHttpclient(HttpClientBuilder builder) {
        if (httpclient != null) {
            return httpclient;
        }
        httpclient = (KerberosHttpclient) Proxy.newProxyInstance(KerberosHttpclientFactory.class.getClassLoader(),
                new Class[]{KerberosHttpclient.class}, new InvokeHandler(builder));
        return httpclient;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        KerberosHttpclient httpclient = getKerberosHttpclient();
        for (int i = 0; i < 30; i++) {
            HttpUriRequest request = new HttpGet("http://10.110.13.197:50070/dfshealth.html#tab-overview");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println("----------------------------------------");

            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                    "[STATUS] " + response.getStatusLine());

            EntityUtils.consume(entity);
            Thread.sleep(1000);
        }
        httpclient.close();
    }
}
