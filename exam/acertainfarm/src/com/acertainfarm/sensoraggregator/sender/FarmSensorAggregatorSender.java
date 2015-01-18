package com.acertainfarm.sensoraggregator.sender;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.sensoraggregator.interfaces.Sender;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmSensorAggregatorSender implements Sender {
    private ExecutorService executorService;
    private HttpClient senderClient;

    public FarmSensorAggregatorSender(){
        // create an executor service for the requests
        executorService = Executors.newFixedThreadPool(5);

        senderClient = new HttpClient();
        senderClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        senderClient.setMaxConnectionsPerAddress(FarmClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
        senderClient.setThreadPool(new QueuedThreadPool(FarmClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
        senderClient.setTimeout(FarmClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS	);

        try {
            senderClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: tihs method is obsolete
    private List<Future<String>> prepareForSending(Date timePeriod, List<Event> avgMeasurements) {
        //TODO: this will send the avg measurements to the Field Status Server
        System.out.println("time:" + timePeriod.toString() + avgMeasurements.toString());

        //send a map with time and measurements as keys
        Map<String, Object> payload = new HashMap<String, Object>();

        payload.put(FarmConstants.SENDER_KEY_TIMESTAMP, timePeriod.getTime());
        payload.put(FarmConstants.SENDER_KEY_TIMESTAMP, avgMeasurements);

        return null;
    }

    @Override
    public Future<SenderResult> sendUpdateWithPayload(String fieldUpdateServerAddress, SenderRequest request) {
        FarmSensorAggregatorSenderTask task = new FarmSensorAggregatorSenderTask(fieldUpdateServerAddress, request, senderClient);
        return executorService.submit(task);
    }
}
