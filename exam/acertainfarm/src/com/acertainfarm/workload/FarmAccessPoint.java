package com.acertainfarm.workload;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;

import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public class FarmAccessPoint extends Thread {
    protected SensorAggregator sensorAggregator;
    protected SensorInputGenerator generator;
    protected int sensorId;

    public FarmAccessPoint(SensorAggregator sensorAggregator, int sensorId){
        this.sensorAggregator = sensorAggregator;
        this.sensorId = sensorId;
        generator = new SensorInputGenerator();
    }

    @Override
    public void run() {
        do {
            //run indefinitely
            List<Measurement> generatedMeasurements = generator.generateListOfMeasurements(FarmConstants.MAX_NO_FIELDS,
                    5, sensorId);
            try {
                sensorAggregator.newMeasurements(generatedMeasurements);
            } catch (AttributeOutOfBoundsException e) {
                e.printStackTrace();
            } catch (PrecisionFarmingException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (true);
    }

}
