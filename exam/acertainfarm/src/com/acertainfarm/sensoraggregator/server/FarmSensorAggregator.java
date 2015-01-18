package com.acertainfarm.sensoraggregator.server;

import com.acertainfarm.data.Event;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.data.SensorAggregatorMeasurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.Sender;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.utils.FarmUtility;
import org.eclipse.jetty.server.Request;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmSensorAggregator implements SensorAggregator {

    private Map<Integer, List<Measurement>> measurementsMap = null;
    private Date lastFieldStatusUpdate = null;
    private FarmSensorAggregatorSender sender = null;
    private String filePath = "aggregator.config"; //we will need this to get the field status
                                                    // server address and other fields
    private int numberOfFields = 10;

    public FarmSensorAggregator (){

        if (measurementsMap == null){
            //initialize the map
            //caution! the map has no initialized lists
            measurementsMap = new HashMap<Integer, List<Measurement>>();
        }

        lastFieldStatusUpdate = new Date();

        //init the sender (just a client that sends
        // the avg measurements to the field status server
        sender = new FarmSensorAggregatorSender();

        //TODO: get the field status url from the config file
    }

    @Override
    public synchronized void newMeasurements(List<Measurement> measurements) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //sanity checks first -> all or nothing atomicity
        //when we get the measurements we assign timestamps and add then check the interval
        // that has been read from the file
        //if the interval has not passed we add the to the map
        //else we call out to the measurement sender to to send the values from the map,
        //remove the data, and add the current on

        Date currentTime = new Date();

        for (Measurement measure : measurements){
            //sanity checks
            //just hard coding for now
            if (measure.getSensorId() < 0 )
                throw new AttributeOutOfBoundsException("SensorID is negative");
            if (measure.getFieldId() > numberOfFields || measure.getFieldId() < 1)
                throw new AttributeOutOfBoundsException("FieldID is invalid");
            if (measure.getCurrentTemperature() < -50 || measure.getCurrentTemperature() > 50)
                throw new AttributeOutOfBoundsException("Temperature is invalid");
            if (measure.getCurrentHumidity() < 0 || measure.getCurrentHumidity() > 100)
                throw new AttributeOutOfBoundsException("Humidity is invalid");
        }

        long timeDiff = FarmUtility.getDateDiff(currentTime,lastFieldStatusUpdate, TimeUnit.MILLISECONDS);

        if (timeDiff > 5000){
            //just hard coding for now
            //we must compute the averages for the inputs,
            // send them
            //remove all of them
            //add the new batch to the map
            List<Event> measurementsToSend = new ArrayList<Event>();

            for (Integer key : measurementsMap.keySet()){
                //now we are in the list of measurements

                int tempSum= 0;
                int humiditySum=0;
                int i = 0;

                for (Measurement measure : measurementsMap.get(key)){
                    //iterate through the list for field ID 'key'
                    tempSum+= measure.getCurrentTemperature();
                    humiditySum+= measure.getCurrentHumidity();
                    i++;
                }

                //add the avg measurement to the list
                measurementsToSend.add(new Event(key,tempSum/i,humiditySum/i));

                //remove the list from the measurements map
                measurementsMap.remove(key);
            }

            //send it the sender
            sender.prepareForSending(currentTime,measurementsToSend);

            //add the measurements to the map
            addMeasurementBatchToMap(measurements);

        }else {
            //the time period has not passed
            //add the values to the list
            addMeasurementBatchToMap(measurements);
        }

    }

    private synchronized void addMeasurementBatchToMap(List<Measurement> batch){
        for(Measurement measure: batch){
            List<Measurement> whereToAdd = measurementsMap.get(measure.getFieldId());
            if (whereToAdd != null){
                whereToAdd.add(measure);
            }else{
                List<Measurement> newList = new ArrayList<Measurement>();
                newList.add(measure);
                measurementsMap.put(measure.getFieldId(),newList);
            }
        }

        System.out.println(measurementsMap.toString());
    }


}
