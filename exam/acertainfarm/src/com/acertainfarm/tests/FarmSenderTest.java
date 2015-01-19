package com.acertainfarm.tests;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.fieldstatus.server.FarmFieldStatus;
import com.acertainfarm.sensoraggregator.interfaces.Sender;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.sensoraggregator.proxies.FarmSensorAggregatorHTTPProxy;
import com.acertainfarm.sensoraggregator.sender.FarmSensorAggregatorSender;
import com.acertainfarm.sensoraggregator.sender.SenderRequest;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import com.acertainfarm.utils.FarmMessageTag;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmSenderTest {
    private static SensorAggregator sensorAggregator;
    private static FieldStatus fieldStatus;
    private static boolean localTest = false;
    private static Sender sender;
    private static String fieldStatusAddress = "http://localhost:8082/fieldstatus";

    @BeforeClass
    public static void setUpBeforeClass() {

        try {
            String localTestProperty = System
                    .getProperty(FarmConstants.PROPERTY_KEY_LOCAL_TEST);
            localTest = (localTestProperty != null) ? Boolean
                    .parseBoolean(localTestProperty) : localTest;
            if (localTest) {
                sensorAggregator = new FarmSensorAggregator();
                sender = new FarmSensorAggregatorSender();
                fieldStatus = new FarmFieldStatus();
            } else {
                sensorAggregator = new FarmSensorAggregatorHTTPProxy(
                        "http://localhost:8081/sensoragg");
                sender = new FarmSensorAggregatorSender();
                //no field status proxy required because
                //I am sending the message directly to the server
                fieldStatus = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Before
    public void initializeSensorAggregator(){

    }

    @After
    public void cleanupSensorAggregator(){

    }

    public Event defaultEvent(){
        return new Event(12,4,42);
    }

    public List<Event> defaultEventList ()
    {
        List<Event> defaultList = new ArrayList<Event>();
        defaultList.add(defaultEvent());
        defaultList.add(defaultEvent());
        defaultList.add(defaultEvent());
        defaultList.add(defaultEvent());
        return defaultList;
    }


    @Test
    public void testSender () throws AttributeOutOfBoundsException,PrecisionFarmingException {

        Map payload = new HashMap();
        payload.put(FarmConstants.SENDER_KEY_TIMESTAMP,214124);
        payload.put(FarmConstants.SENDER_KEY_EVENTLIST,defaultEventList());

        SenderRequest request = new SenderRequest(payload, FarmMessageTag.UPDATE);

        sender.sendUpdateWithPayload(fieldStatusAddress,request);
//        Thread client1 = new Thread(new ConcurrentNewMeasurements(defaultMeasurementList()));
//        Thread client2 = new Thread(new ConcurrentNewMeasurements(defaultMeasurementList()));
//
//
//        // run threads
//        client1.start();
//        client2.start();
//
//        // wait
//        try {
//            client1.join();
//            client2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            fail();
//        }
//
        assertTrue(true);
    }

    @AfterClass
    public static void tearDownAfterClass() throws AttributeOutOfBoundsException,PrecisionFarmingException {

    }
}
