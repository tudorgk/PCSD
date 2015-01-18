package com.acertainfarm.clients;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusAccessPointHTTPProxy implements SensorAggregator{
    protected HttpClient client;
    protected String serverAddress;

    public FarmFieldStatusAccessPointHTTPProxy(String serverAddress) throws Exception{
        setServerAddress(serverAddress);
        client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(FarmClientConstants.CLIENT_MAX_CONNECTION_ADDRESS); // max
        // concurrent connections to every address
        client.setThreadPool(new QueuedThreadPool(
                FarmClientConstants.CLIENT_MAX_THREADSPOOL_THREADS)); // max
        // threads
        client.setTimeout(FarmClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS); // seconds
        // timeout;
        // if no server reply, the request expires
        client.start();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void newMeasurements(List<Measurement> measurements) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //TODO: implement
        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + "/" + FarmMessageTag.NEWMEASUREMENT;

        String listMeasurementsxmlString = FarmUtility
                .serializeObjectToXMLString(measurements);
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        Buffer requestContent = new ByteArrayBuffer(listMeasurementsxmlString);
        exchange.setRequestContent(requestContent);

        FarmUtility.SendAndRecv(this.client, exchange);
    }

    public void stop() {
        try {
            client.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
