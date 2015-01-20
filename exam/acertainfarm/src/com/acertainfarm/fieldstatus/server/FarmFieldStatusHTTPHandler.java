package com.acertainfarm.fieldstatus.server;

import com.acertainfarm.data.Event;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusHTTPHandler extends AbstractHandler {
    private FarmFieldStatus fieldStatus = null;

    public FarmFieldStatusHTTPHandler(FarmFieldStatus fieldStatus) {
        this.fieldStatus = fieldStatus;
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
                && requestURI.toLowerCase().startsWith("/fieldstatus")) {
            messageTag = FarmUtility.convertURItoMessageTag(requestURI
                    .substring(12)); // the request is from store
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
                case UPDATE:
                    //DONE: implement and check! Checked and works alright
                    System.out.println("update handler");
                    String xml = FarmUtility.extractPOSTDataFromRequest(httpServletRequest);
                    Map<?,?> payload = (Map<?,?>) FarmUtility
                            .deserializeXMLStringToObject(new String(xml));

                    long timePeriod = ((Number)payload.get("time")).longValue();
                    List<Event> eventList = (List<Event>)payload.get("events");

                    System.out.println("Events " + eventList.toString());

                    // Make the purchase
                    farmResponse = new FarmResponse();
                    try {
                        fieldStatus.update(timePeriod,eventList);
                    } catch (PrecisionFarmingException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    } catch (AttributeOutOfBoundsException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    }
                    String listFarmxmlString = FarmUtility
                            .serializeObjectToXMLString(farmResponse);
                    httpServletResponse.getWriter().println(listFarmxmlString);
                    break;
                case QUERY:
                    xml = FarmUtility.extractPOSTDataFromRequest(httpServletRequest);
                    List<Integer> fieldIDList = (List<Integer>) FarmUtility
                            .deserializeXMLStringToObject(xml);

                    farmResponse= new FarmResponse();
                    try {
                        farmResponse.setResult(fieldStatus.query(fieldIDList));
                    } catch (PrecisionFarmingException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    } catch (AttributeOutOfBoundsException e) {
                        farmResponse.setException(e);
                        e.printStackTrace();
                    }
                    listFarmxmlString = FarmUtility
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
