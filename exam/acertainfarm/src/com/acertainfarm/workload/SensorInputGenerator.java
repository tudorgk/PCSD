package com.acertainfarm.workload;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.utils.FarmUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tudorgk on 19/1/15.
 */
public class SensorInputGenerator {

    public List<Measurement> generateListOfMeasurements(int numOfFields, int maxSensorReadings, int sensorId){
        if (numOfFields < 1)
            //return empty List
            return new ArrayList<Measurement>();

        List<Measurement> listOFMeasurements = new ArrayList<Measurement>();

        for (int i = 0; i<maxSensorReadings; i++){
            listOFMeasurements.add(new Measurement(sensorId, FarmUtility.randInt(1, numOfFields),
                    FarmUtility.randInt(FarmConstants.SENSOR_MIN_TEMP, FarmConstants.SENSOR_MAX_TEMP),
                    FarmUtility.randInt(FarmConstants.SENSOR_MIN_HUMIDITY, FarmConstants.SENSOR_MAX_HUMIDITY)));
        }

        return listOFMeasurements;
    }

}
