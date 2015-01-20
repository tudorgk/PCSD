package com.acertainfarm.sensoraggregator.server;

import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmResponse;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmSensorAggregatorHTTPMessageHandler extends AbstractHandler {

    private FarmSensorAggregator sensorAggregator = null;

    public FarmSensorAggregatorHTTPMessageHandler(FarmSensorAggregator sensorAggregator){
        this.sensorAggregator = sensorAggregator;
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {

        FarmMessageTag messageTag;
        String requestURI;
        FarmResponse farmResponse= null;

        httpServletResponse.setContentType("text/html;charset=utf-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        requestURI = httpServletRequest.getRequestURI();


        // Need to do request multi-plexing
        if (!FarmUtility.isEmpty(requestURI)
                && requestURI.toLowerCase().startsWith("/sensoragg")) {
            messageTag = FarmUtility.convertURItoMessageTag(requestURI
                    .substring(10)); // the request is from store
            // manager, more
            // sophisticated security
            // features could be added
            // here
        } else {
            messageTag = FarmUtility.convertURItoMessageTag(requestURI);
        }
        // the RequestURI before the switch
        if (messageTag == null) {
            System.out.println("Unknown message tag");
        } else {
            switch (messageTag){
                case NEWMEASUREMENT:
                    String xml = FarmUtility.extractPOSTDataFromRequest(httpServletRequest);
                    List<Measurement> newMeasurementList = (List<Measurement>) FarmUtility
                            .deserializeXMLStringToObject(new String(xml));

                    // Make the purchase
                    farmResponse = new FarmResponse();
                    try {
                        sensorAggregator.newMeasurements(newMeasurementList);
                    } catch (AttributeOutOfBoundsException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    } catch (PrecisionFarmingException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    }
                    String listFarmxmlString = FarmUtility
                            .serializeObjectToXMLString(farmResponse);
                    httpServletResponse.getWriter().println(listFarmxmlString);
                    break;
                default:
                    System.out.println("Unhandled message tag");
                    break;
            }
        }
        // Mark the request as handled so that the HTTP response can be sent
        baseRequest.setHandled(true);
    }
}
