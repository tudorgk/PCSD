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
import com.acertainfarm.workload.FarmAccessPoint;
import com.acertainfarm.workload.FarmClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmNormalOperationTest {
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

    @Test
    public void testNormalOperation() throws InterruptedException {
        //create a list of 5 farm access points
        List<FarmAccessPoint> accessPoints = new ArrayList<FarmAccessPoint>();
        for (int i = 0; i < 20; i++){
            accessPoints.add(new FarmAccessPoint(sensorAggregator,i+1));
        }

        //start the access points
        for (FarmAccessPoint accessPoint : accessPoints){
            accessPoint.start();
        }

        //create 2 clients
        List<FarmClient> farmClientList = new ArrayList<FarmClient>();
        for (int i = 0; i< 2; i++){
            farmClientList.add(new FarmClient(fieldStatus));
        }

        for (FarmClient client : farmClientList){
            client.start();
        }

        for (FarmAccessPoint accessPoint : accessPoints){
            accessPoint.join();
        }

        for (FarmClient client : farmClientList){
            client.join();
        }



    }
}
