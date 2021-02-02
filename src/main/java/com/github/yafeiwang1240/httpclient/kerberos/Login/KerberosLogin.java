package com.github.yafeiwang1240.httpclient.kerberos.Login;

import com.github.yafeiwang1240.httpclient.kerberos.config.Config;
import com.github.yafeiwang1240.httpclient.kerberos.config.KerberosConfiguration;
import com.github.yafeiwang1240.httpclient.kerberos.handler.KerberosCallbackHandler;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * kerberos login an close
 * @author wangyafei
 * @date 2021-01-19
 */
public class KerberosLogin implements Closeable {

    static {
        System.setProperty("java.security.krb5.realm", Config.getConfig("realm"));
        System.setProperty("java.security.krb5.kdc", Config.getConfig("kdc"));
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("sun.security.krb5.principal", Config.getConfig("principal"));
    }

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private LoginContext loginContext;

    public KerberosLogin() {
        try {
            loginContext = new LoginContext("Krb5Login", null,
                    new KerberosCallbackHandler(Config.getConfig("principal"), Config.getConfig("password")),
                    new KerberosConfiguration(Config.getConfig("principal")));
            loginContext.login();
        } catch (LoginException e) {
            throw new RuntimeException("登录失败", e);
        }
        service.scheduleWithFixedDelay(() -> {
            try {
                Subject subject = loginContext.getSubject();
                loginContext.logout();
                loginContext = new LoginContext("Krb5Login", subject,
                        new KerberosCallbackHandler(Config.getConfig("principal"), Config.getConfig("password")),
                        new KerberosConfiguration(Config.getConfig("principal")));
                loginContext.login();
            } catch (LoginException e) {
                throw new RuntimeException("登录失败", e);
            }
        }, 10, 10, TimeUnit.HOURS);
    }

    public Subject getSubject() {
        return loginContext.getSubject();
    }

    @Override
    public void close() throws IOException {
        service.shutdown();
        try {
            loginContext.logout();
        } catch (LoginException e) {
            throw new IOException("登出失败", e);
        }
    }
}
