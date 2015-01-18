package com.acertainfarm.utils;

import com.acertainfarm.constants.FarmClientConstants;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmUtility {

    public static boolean isInvalidISBN(int isbn) {
        return (isbn < 1);
    }

    public static boolean isInvalidRating(int rating) {
        return (rating < 0 || rating > 5);
    }

    public static boolean isInvalidNoCopies(int copies) {
        return (copies < 1);
    }

    /**
     * Checks if a string is empty or null
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || str.isEmpty());
    }

    /**
     * Converts a string to a float if possible else it returns the signal value
     * for failure passed as parameter
     *
     * @param str
     * @param failureSignal
     * @return
     */
    public static float convertStringToFloat(String str, float failureSignal) {
        float returnValue = failureSignal;
        try {
            returnValue = Float.parseFloat(str);

        } catch (NumberFormatException ex) {
            ;
        } catch (NullPointerException ex) {
            ;
        }
        return returnValue;
    }

    /**
     * Converts a string to a int if possible else it returns the signal value
     * for failure passed as parameter
     *
     * @param str
     * @param failureSignal
     * @return
     */
    public static int convertStringToInt(String str) throws Exception {
        int returnValue = 0;
        try {
            returnValue = Integer.parseInt(str);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return returnValue;
    }

    /**
     * Convert a request URI to the message tags supported in CertainBookStore
     *
     * @param requestURI
     * @return
     */
    public static FarmMessageTag convertURItoMessageTag(String requestURI) {

        try {
            FarmMessageTag messageTag = FarmMessageTag
                    .valueOf(requestURI.substring(1).toUpperCase());
            return messageTag;
        } catch (IllegalArgumentException ex) {
            ; // Enum type matching failed so non supported message
        } catch (NullPointerException ex) {
            ; // RequestURI was empty
        }
        return null;
    }

    /**
     * Serializes an object to an xml string
     *
     * @param object
     * @return
     */
    public static String serializeObjectToXMLString(Object object) {
        String xmlString;
        XStream xmlStream = new XStream(new StaxDriver());
        xmlString = xmlStream.toXML(object);
        return xmlString;
    }

    /**
     * De-serializes an xml string to object
     *
     * @param xmlObject
     * @return
     */
    public static Object deserializeXMLStringToObject(String xmlObject) {
        Object dataObject = null;
        XStream xmlStream = new XStream(new StaxDriver());
        dataObject = xmlStream.fromXML(xmlObject);
        return dataObject;
    }

    /**
     * Manages the sending of an exchange through the client, waits for the
     * response and unpacks the response
     *
     * @param client
     * @param exchange
     * @return
     * @throws java.lang.Exception
     */
    public static FarmResult SendAndRecv(HttpClient client,
                                              ContentExchange exchange) throws AttributeOutOfBoundsException,PrecisionFarmingException {
        int exchangeState;
        try {
            client.send(exchange);
        } catch (IOException ex) {
            throw new AttributeOutOfBoundsException(
                    FarmClientConstants.strERR_CLIENT_REQUEST_SENDING, ex);
        }

        try {
            exchangeState = exchange.waitForDone(); // block until the response
            // is available
        } catch (InterruptedException ex) {
            throw new AttributeOutOfBoundsException(
                    FarmClientConstants.strERR_CLIENT_REQUEST_SENDING, ex);
        }

        if (exchangeState == HttpExchange.STATUS_COMPLETED) {
            try {
                FarmResponse farmResponse = (FarmResponse) FarmUtility
                        .deserializeXMLStringToObject(exchange
                                .getResponseContent().trim());
                if (farmResponse  == null) {
                    throw new AttributeOutOfBoundsException(
                            FarmClientConstants.strERR_CLIENT_RESPONSE_DECODING);
                }
                AttributeOutOfBoundsException ex = farmResponse.getException();
                if (ex != null) {
                    throw ex;
                }
                return farmResponse .getResult();

            } catch (UnsupportedEncodingException ex) {
                throw new AttributeOutOfBoundsException(
                        FarmClientConstants.strERR_CLIENT_RESPONSE_DECODING,
                        ex);
            }
        } else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
            throw new AttributeOutOfBoundsException(
                    FarmClientConstants.strERR_CLIENT_REQUEST_EXCEPTION);
        } else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
            throw new AttributeOutOfBoundsException(
                    FarmClientConstants.strERR_CLIENT_REQUEST_TIMEOUT);
        } else {
            throw new AttributeOutOfBoundsException(
                    FarmClientConstants.strERR_CLIENT_UNKNOWN);
        }
    }

    /**
     * Returns the message of the request as a string
     *
     * @param request
     * @return xml string
     * @throws IOException
     */
    public static String extractPOSTDataFromRequest(HttpServletRequest request)
            throws IOException {
        Reader reader = request.getReader();
        int len = request.getContentLength();

        // Request must be read into a char[] first
        char res[] = new char[len];
        reader.read(res);
        reader.close();
        return new String(res);
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
