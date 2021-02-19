package com.github.yafeiwang1240.httpclient.kerberos.Login;

import com.github.yafeiwang1240.httpclient.kerberos.config.Config;
import com.github.yafeiwang1240.httpclient.kerberos.config.KerberosConfiguration;
import com.github.yafeiwang1240.httpclient.kerberos.handler.KerberosCallbackHandler;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.ccache.CredentialsCache;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
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
//                Subject subject = loginContext.getSubject();
//                loginContext.logout();
//                loginContext = new LoginContext("Krb5Login", subject,
//                        new KerberosCallbackHandler(Config.getConfig("principal"), Config.getConfig("password")),
//                        new KerberosConfiguration(Config.getConfig("principal")));
                loginContext.login();
                Iterator<Object> iterator = loginContext.getSubject()
                        .getPrivateCredentials().iterator();
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (next instanceof KerberosTicket) {
                        KerberosTicket ticket = (KerberosTicket) next;
                        if (ticket.getEndTime().getTime() < System.currentTimeMillis()) {
                            iterator.remove();
                            try {
                                ticket.destroy();
                            } catch (DestroyFailedException e) {
                                // ignore
                            }
                        }
                    }
                }
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

    /**
     * cache ticket to file
     * @throws IOException
     * @throws KrbException
     */
    @Deprecated
    private void cache() throws IOException, KrbException {
        String ticketCachePath = !System.getProperty("java.io.tmpdir").endsWith(File.separator) ?
                System.getProperty("java.io.tmpdir") + File.separator + "10010" +  "_ticket.cache" :
                System.getProperty("java.io.tmpdir") + "10010" +  "_ticket.cache" ;
        Set<KerberosTicket> privateCredentials = loginContext.getSubject().getPrivateCredentials(KerberosTicket.class);
        Iterator<KerberosTicket> ticketIterator = privateCredentials.iterator();
        KerberosTicket ticket = null;
        KerberosPrincipal principal = null;
        while (ticketIterator.hasNext()) {
            ticket = ticketIterator.next();
        }
        Set<KerberosPrincipal> principals = loginContext.getSubject().getPrincipals(KerberosPrincipal.class);
        Iterator<KerberosPrincipal> principalIterator = principals.iterator();
        while (principalIterator.hasNext()) {
            principal = principalIterator.next();
        }
        File file = new File(ticketCachePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        CredentialsCache credentialsCache = CredentialsCache.getInstance();
        if (credentialsCache == null) {
            credentialsCache = CredentialsCache.create(new PrincipalName(principal.getName()), ticketCachePath);
        }
        Credentials credentials = Krb5Util.ticketToCreds(ticket);
        credentialsCache.update(new sun.security.krb5.internal.ccache.Credentials(
                credentials.getClient(),
                credentials.getServer(),
                credentials.getSessionKey(),
                new KerberosTime(credentials.getAuthTime()),
                new KerberosTime(credentials.getStartTime()),
                new KerberosTime(credentials.getEndTime()),
                new KerberosTime(Objects.isNull(credentials.getRenewTill()) ? System.currentTimeMillis() : credentials.getRenewTill().getTime()),
                false,
                credentials.getTicketFlags(),
                null,
                credentials.getAuthzData(),
                credentials.getTicket(),
                null
        ));
        credentialsCache.save();
    }
}
