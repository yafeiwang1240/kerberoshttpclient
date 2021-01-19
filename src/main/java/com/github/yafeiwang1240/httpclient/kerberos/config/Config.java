package com.github.yafeiwang1240.httpclient.kerberos.config;

import java.util.Hashtable;
import java.util.Map;

/**
 * 公共配置
 * @author wangyafei
 * @date 2021-01-19
 */
public class Config {

    private static Map<String, String> config = new Hashtable<>();

    static {
        config.put("principal", "hadoop/zrk@EXAMPLE.COM");
        config.put("password", "zrk1234567");
        config.put("realm", "EXAMPLE.COM");
        config.put("kdc", "10.110.14.209");
    }

    public static void setConfig(String key, String value) {
        config.put(key, value);
    }

    public static String getConfig(String key) {
        if (!config.containsKey(key)) {
            throw new IllegalArgumentException("no such config: " + key);
        }
        return config.get(key);
    }
}
