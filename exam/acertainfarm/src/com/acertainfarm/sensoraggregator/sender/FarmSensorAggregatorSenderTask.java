package com.acertainfarm.sensoraggregator.sender;

import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.util.concurrent.Callable;

/**
 * Created by tudorgk on 18/1/15.
 */
public class FarmSensorAggregatorSenderTask implements Callable<SenderResult> {

    private String fieldUpdateServerAddress;
    private SenderRequest request;
    private HttpClient httpClient;


    public FarmSensorAggregatorSenderTask(String fieldUpdateServerAddress, SenderRequest request, HttpClient httpClient){
        this.fieldUpdateServerAddress = fieldUpdateServerAddress;
        this.request = request;
        this.httpClient = httpClient;
    }

    @Override
    public SenderResult call() throws Exception {
        FarmMessageTag messageTag = request.getMessageType();
        String xmlString = FarmUtility.serializeObjectToXMLString(request.getPayLoad());
        Buffer requestContent = new ByteArrayBuffer(xmlString);
        ContentExchange exchange = new ContentExchange();

        //TODO: Check if the address is correct
        String urlString = fieldUpdateServerAddress + "/" + messageTag;

        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);

        try {
            FarmUtility.SendAndRecv(httpClient, exchange);
        } catch (Exception e) {
            return new SenderResult(fieldUpdateServerAddress, false);
        }
        return new SenderResult(fieldUpdateServerAddress, true);
    }
}
