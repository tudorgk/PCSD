package com.acertainfarm.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmHTTPServerUtility {
    /**
     * Creates a server on the port and blocks the calling thread
     */
    public static boolean createServer(int port, AbstractHandler handler) {
        Server server = new Server(port);
        if (handler != null) {
            server.setHandler(handler);
        }

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Creates a server on the InetAddress and blocks the calling thread
     */
    public static boolean createServer(String ipAddress, int port,
                                       AbstractHandler handler) {
        InetAddress inetIpAddress;
        InetSocketAddress address;
        Server server;

        if (ipAddress == null)
            return false;

        try {
            inetIpAddress = InetAddress.getByName(ipAddress);
            address = new InetSocketAddress(inetIpAddress, port);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return false;
        }

        server = new Server(address);
        if (handler != null) {
            server.setHandler(handler);
        }

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
