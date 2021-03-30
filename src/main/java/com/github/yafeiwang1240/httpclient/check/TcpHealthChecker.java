package com.github.yafeiwang1240.httpclient.check;

import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpHealthChecker extends AbstractHealthChecker {

    @Override
    public boolean check(InstanceInfo instanceInfo) {
        boolean valid = false;
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(instanceInfo.getHost(), instanceInfo.getPort()), 2000);
            valid = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valid;
    }
}
