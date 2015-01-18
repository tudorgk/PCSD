package com.acertainfarm.fieldstatus.server;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregatorHTTPMessageHandler;
import com.acertainfarm.utils.FarmHTTPServerUtility;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusServer {

    public static void main(String[] args) {
        FarmFieldStatus fieldStatus= new FarmFieldStatus();
        int listen_on_port = 8082;
        FarmFieldStatusHTTPHandler handler = new FarmFieldStatusHTTPHandler(
                fieldStatus);
        String server_port_string = System
                .getProperty(FarmConstants.PROPERTY_KEY_SERVER_PORT);
        if (server_port_string != null) {
            try {
                listen_on_port = Integer.parseInt(server_port_string);
            } catch (NumberFormatException ex) {
                System.err.println(ex);
            }
        }
        if (FarmHTTPServerUtility.createServer(listen_on_port, handler)) {
            ;
        }

    }
}
