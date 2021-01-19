package com.github.yafeiwang1240.httpclient.kerberos.handler;

import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * kerberos callback
 * @author wangyafei
 * @date 2021-01-19
 */
public class KerberosCallbackHandler implements CallbackHandler {

    private final String principal;
    private final String password;

    public KerberosCallbackHandler(String principal, String password) {
        this.principal = principal;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(principal);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unknown Callback");
            }
        }
    }
}
