package com.github.yafeiwang1240.httpclient.kerberos;

import org.apache.http.client.HttpClient;

import java.io.Closeable;

/**
 * kerberos http client
 * @author wangyafei
 * @date 2021-01-19
 */
public interface KerberosHttpclient extends HttpClient, Closeable {
}
