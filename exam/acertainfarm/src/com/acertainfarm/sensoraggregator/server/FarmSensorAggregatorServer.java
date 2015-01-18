package com.acertainfarm.sensoraggregator.server;


import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.utils.FarmHTTPServerUtility;

public class FarmSensorAggregatorServer {

    public static void main(String[] args) {
        FarmSensorAggregator sensorAggregator= new FarmSensorAggregator();
        int listen_on_port = 8081;
        FarmSensorAggregatorHTTPMessageHandler handler = new FarmSensorAggregatorHTTPMessageHandler(
                sensorAggregator);
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
