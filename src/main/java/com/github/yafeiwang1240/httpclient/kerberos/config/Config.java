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
        config.put("kdc", "dig-kerberos-14-209.bj-qa.liepin.inc");
        config.put("useKeyTab", "true");
        config.put("keyTab", "D:\\Work\\hadoop.zrk.keytab");
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

    public static String getConfig(String key, String defaultValue) {
        if (!config.containsKey(key)) {
            return defaultValue;
        }
        return config.get(key);
    }
}
