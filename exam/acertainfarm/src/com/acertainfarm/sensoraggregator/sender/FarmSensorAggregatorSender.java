package com.acertainfarm.sensoraggregator.sender;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.sensoraggregator.interfaces.Sender;
import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
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
        executorService = Executors.newFixedThreadPool(1);

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


    @Override
    public SenderResult sendUpdateWithPayload(String fieldUpdateServerAddress, SenderRequest request) {
//        DONE: check what is not working. It worked but i didnt called 'get' on the future object
//        FarmSensorAggregatorSenderTask task = new FarmSensorAggregatorSenderTask(fieldUpdateServerAddress, request, senderClient);
//        return executorService.submit(task);

        FarmMessageTag messageTag = request.getMessageType();
        System.out.println(request);
        String xmlString = FarmUtility.serializeObjectToXMLString(request.getPayLoad());
        System.out.println(xmlString);
        Buffer requestContent = new ByteArrayBuffer(xmlString);
        ContentExchange exchange = new ContentExchange();

        //DONE: Check if the address is correct! It's correct
        String urlString = fieldUpdateServerAddress + "/" + messageTag;

        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);

        try {
            FarmUtility.SendAndRecv(senderClient, exchange);
        } catch (Exception e) {
            return new SenderResult(fieldUpdateServerAddress, false);
        }
        return new SenderResult(fieldUpdateServerAddress, true);

    }
}
