package com.acertainfarm.clients;

import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;

import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusAccessPointHTTPProxy implements SensorAggregator{
    @Override
    public void newMeasurements(List<Measurement> measurements) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        
    }
}
