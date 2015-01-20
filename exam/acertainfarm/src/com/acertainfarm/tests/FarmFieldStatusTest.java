package com.acertainfarm.tests;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.fieldstatus.proxies.FarmFieldStatusHTTPProxy;
import com.acertainfarm.fieldstatus.server.FarmFieldStatus;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.sensoraggregator.proxies.FarmSensorAggregatorHTTPProxy;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmFieldStatusTest {
    private static SensorAggregator sensorAggregator;
    private static FieldStatus fieldStatus;
    private static boolean localTest = false;

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            String localTestProperty = System
                    .getProperty(FarmConstants.PROPERTY_KEY_LOCAL_TEST);
            localTest = (localTestProperty != null) ? Boolean
                    .parseBoolean(localTestProperty) : localTest;
            if (localTest) {
                sensorAggregator = new FarmSensorAggregator();
                fieldStatus = new FarmFieldStatus();
            } else {
                sensorAggregator = new FarmSensorAggregatorHTTPProxy(
                        "http://localhost:8081/sensoragg");
                fieldStatus = new FarmFieldStatusHTTPProxy(
                        "http://localhost:8082/fieldstatus");
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

    @Test
    public void testQuery(){

        List<Integer> fieldIds = new ArrayList<Integer>();

        try {
            System.out.println(fieldStatus.query(fieldIds));
        } catch (AttributeOutOfBoundsException e) {
            e.printStackTrace();
        } catch (PrecisionFarmingException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws AttributeOutOfBoundsException,PrecisionFarmingException {

    }
}
