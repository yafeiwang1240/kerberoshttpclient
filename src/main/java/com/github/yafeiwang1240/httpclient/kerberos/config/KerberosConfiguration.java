package com.github.yafeiwang1240.httpclient.kerberos.config;

import com.sun.security.auth.module.Krb5LoginModule;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KerberosConfiguration extends Configuration {

    private AppConfigurationEntry[] ace;

    public KerberosConfiguration(String principal) {
        String ticketCachePath = System.getProperty("java.io.tmpdir")
                + java.io.File.separator + UUID.randomUUID().toString()
                +  "_ticket.cache";
        Map<String, String> options = new HashMap<>();
        options.put("debug", "false");
        options.put("storeKey", "true");
        options.put("useTicketCache", "true");
        options.put("useKeyTab", "false");
        options.put("doNotPrompt", "true");
        options.put("ticketCache", ticketCachePath);
        options.put("isInitiator", "true");
        options.put("refreshKrb5Config", "true");
        options.put("principal", principal);
        options.put("tryFirstPass", "false");
        options.put("useFirstPass", "true");
        options.put("storePass", "true");
        options.put("renewTGT", "true");
        ace = new AppConfigurationEntry[]{new AppConfigurationEntry(Krb5LoginModule.class.getName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                options)};
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        return ace;
    }
}
