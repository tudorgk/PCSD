package com.acertainfarm.sensoraggregator.proxy;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.sensoraggregator.interfaces.Sender;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmSensorAggregatorSenderProxy implements Sender {
    private ExecutorService executorService;
    private HttpClient replicationClient;

    public FarmSensorAggregatorSenderProxy(){
        // create an executor service for the requests
        executorService = Executors.newFixedThreadPool(1);

        replicationClient = new HttpClient();
        replicationClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        replicationClient.setMaxConnectionsPerAddress(FarmClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        replicationClient.setThreadPool(new QueuedThreadPool(FarmClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        replicationClient.setTimeout(FarmClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS	);

        try {
            replicationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Future<String>> prepareForSending(Date timePeriod, List<Event> avgMeasurements) {
        //TODO: this will send the avg measurements to the Field Status Server
        return null;
    }
}
