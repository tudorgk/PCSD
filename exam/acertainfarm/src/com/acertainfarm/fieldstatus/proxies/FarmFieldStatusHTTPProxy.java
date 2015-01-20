package com.acertainfarm.fieldstatus.proxies;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.data.FieldState;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmResult;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.awt.print.Book;
import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusHTTPProxy implements FieldStatus{
    protected HttpClient client;
    protected String serverAddress = null;

    public FarmFieldStatusHTTPProxy(String serverAddress) throws Exception {
        this.serverAddress = serverAddress;

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

    @Override
    public List<FieldState> query(List<Integer> fieldIds) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        String listFieldIDssxmlString = FarmUtility
                .serializeObjectToXMLString(fieldIds);
        Buffer requestContent = new ByteArrayBuffer(listFieldIDssxmlString);

        FarmResult result = null;
        ContentExchange exchange = new ContentExchange();
        String urlString = serverAddress + "/"
                 + FarmMessageTag.QUERY;
        exchange.setMethod("POST");
        exchange.setURL(urlString);
        exchange.setRequestContent(requestContent);
        try {
            return (List<FieldState>) FarmUtility.SendAndRecv(this.client, exchange);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PrecisionFarmingException(e);
        }
    }

    @Override
    public void update(long timePeriod, List<Event> events) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //Not used
    }

    public void stop() {
        try {
            client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
