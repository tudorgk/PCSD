package com.acertainfarm.tests;

import com.acertainfarm.sensoraggregator.proxies.FarmSensorAggregatorHTTPProxy;
import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by tudorgk on 18/1/15.
 */
public class FarmSensorAggregatorTest {
    private static SensorAggregator sensorAggregator;
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
            } else {
                sensorAggregator = new FarmSensorAggregatorHTTPProxy(
                        "http://localhost:8081/sensoragg");
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

    public Measurement defaultMeasurement(){
        return new Measurement(12,4,42,89);
    }

    public List<Measurement> defaultMeasurementList ()
    {
        List<Measurement> defaultList = new ArrayList<Measurement>();
        defaultList.add(defaultMeasurement());
        defaultList.add(defaultMeasurement());
        defaultList.add(defaultMeasurement());
        defaultList.add(defaultMeasurement());
        return defaultList;
    }

    @Test
    public void testMeasurements () throws AttributeOutOfBoundsException,PrecisionFarmingException{

        Thread client1 = new Thread(new ConcurrentNewMeasurements(defaultMeasurementList()));
        Thread client2 = new Thread(new ConcurrentNewMeasurements(defaultMeasurementList()));


        // run threads
        client1.start();
        client2.start();

        // wait
        try {
            client1.join();
            client2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(true);

    }

    class ConcurrentNewMeasurements implements Runnable{
        List<Measurement> measurementList;
        public ConcurrentNewMeasurements (List<Measurement> measurementList){
            this.measurementList = measurementList;
        }

        @Override
        public void run() {
            try {
                sensorAggregator.newMeasurements(measurementList);
            } catch (PrecisionFarmingException e) {
                e.printStackTrace();
                fail();
            } catch (AttributeOutOfBoundsException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws AttributeOutOfBoundsException,PrecisionFarmingException {

    }
}
