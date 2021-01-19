package com.github.yafeiwang1240.kerberos.handler;

import com.github.yafeiwang1240.kerberos.Login.KerberosLogin;
import com.github.yafeiwang1240.kerberos.config.Config;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.security.auth.Subject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedAction;

public class InvokeHandler implements InvocationHandler {

    private KerberosLogin kerberosLogin;
    private CloseableHttpClient httpclient;

    public InvokeHandler() {
        this(HttpClients.custom());
    }

    public InvokeHandler(HttpClientBuilder builder) {
        kerberosLogin = new KerberosLogin();
        Credentials credentials = new Credentials() {
            public String getPassword() {
                return Config.getConfig("password");
            }

            public Principal getUserPrincipal() {
                return new BasicUserPrincipal(Config.getConfig("principal"));
            }
        };
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM,
                AuthSchemes.SPNEGO), credentials);
        Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();
        httpclient = builder.setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultCredentialsProvider(credentialsProvider).build();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("close")) {
            kerberosLogin.close();
            httpclient.close();
            return null;
        }
        return Subject.doAs(kerberosLogin.getSubject(), (PrivilegedAction<Object>) () -> {
            try {
                return method.invoke(httpclient, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }
}
