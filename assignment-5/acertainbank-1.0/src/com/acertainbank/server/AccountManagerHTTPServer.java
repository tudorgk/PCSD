package com.acertainbank.server;

import com.acertainbank.business.CertainAccountManager;
import com.acertainbank.utils.AccountManagerConstants;

/**
 * Created by tudorgk on 8/1/15.
 */
public class AccountManagerHTTPServer {
    /**
     * @param args
     */
    public static void main(String[] args) {
        CertainAccountManager accountManager= new CertainAccountManager();
        int listen_on_port = 8081;
        BookStoreHTTPMessageHandler handler = new BookStoreHTTPMessageHandler(
                accountManager);
        String server_port_string = System.getProperty(AccountManagerConstants.PROPERTY_KEY_SERVER_PORT);
        if(server_port_string != null) {
            try {
                listen_on_port = Integer.parseInt(server_port_string);
            } catch(NumberFormatException ex) {
                System.err.println(ex);
            }
        }
        if (BookStoreHTTPServerUtility.createServer(listen_on_port, handler)) {
            ;
        }
    }
}
