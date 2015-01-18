package com.acertainfarm.data;

/**
 * Created by tudorgk on 17/1/15.
 */
public class SensorAggregatorMeasurement {
    private final long timestampMils;
    private final Measurement fieldMeasurement;

    public SensorAggregatorMeasurement( long timestampMils, Measurement fieldMeasurement1){
        this.timestampMils = timestampMils;
        this.fieldMeasurement = fieldMeasurement1;
    }

    public long getTimestampMils(){
        return this.timestampMils;
    }

    public Measurement getFieldMeasurement(){
        return this.fieldMeasurement;
    }

}
