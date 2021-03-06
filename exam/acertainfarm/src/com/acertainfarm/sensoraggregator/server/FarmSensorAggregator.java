package com.acertainfarm.sensoraggregator.server;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.data.Event;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.sensoraggregator.sender.FarmSensorAggregatorSender;
import com.acertainfarm.sensoraggregator.sender.SenderRequest;
import com.acertainfarm.sensoraggregator.sender.SenderResult;
import com.acertainfarm.utils.FarmMessageTag;
import com.acertainfarm.utils.FarmUtility;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmSensorAggregator implements SensorAggregator {

    private ConcurrentMap<Integer, List<Measurement>> measurementsMap = null;
    private Date lastFieldStatusUpdate = null;
    private FarmSensorAggregatorSender sender = null;
    private String filePath = "aggregator.config"; //we will need this to get the field status
                                                    // server address and other fields
    private String fieldUpdateServerAddress = "http://localhost:8082/fieldstatus";
    private int numberOfFields = FarmConstants.MAX_NO_FIELDS;

    private final ReadWriteLock myRWLock = new ReentrantReadWriteLock();

    public FarmSensorAggregator (){

        if (measurementsMap == null){
            //initialize the map
            //caution! the map has no initialized lists
            measurementsMap = new ConcurrentHashMap<Integer, List<Measurement>>();
        }

        lastFieldStatusUpdate = new Date();

        //init the sender (just a client that sends
        // the avg measurements to the field status server
        sender = new FarmSensorAggregatorSender();


    }

    @Override
    public void newMeasurements(List<Measurement> measurements) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //sanity checks first -> all or nothing atomicity
        //when we get the measurements we assign timestamps and add then check the interval
        // that has been read from the file
        //if the interval has not passed we add the to the map
        //else we call out to the measurement sender to to send the values from the map,
        //remove the data, and add the current on

        //lock writing lock
        myRWLock.writeLock().lock();


        Date currentTime = new Date();

        for (Measurement measure : measurements){
            //sanity checks
            //just hard coding for now
            if (measure.getSensorId() < 0 )
                throw new AttributeOutOfBoundsException("SensorID is negative");
            if (measure.getFieldId() > numberOfFields || measure.getFieldId() < 1){
                throw new AttributeOutOfBoundsException("FieldID is invalid");
            }
            if (measure.getCurrentTemperature() < -50 || measure.getCurrentTemperature() > 50)
                throw new AttributeOutOfBoundsException("Temperature is invalid");
            if (measure.getCurrentHumidity() < 0 || measure.getCurrentHumidity() > 100)
                throw new AttributeOutOfBoundsException("Humidity is invalid");
        }

        long timeDiff = FarmUtility.getDateDiff(lastFieldStatusUpdate,currentTime, TimeUnit.MILLISECONDS);

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

            }

            //DONE: Sender - Send payload - Needs testing! - Tested
            //send the payload to the field update server

            Map payload = new HashMap();
            payload.put(FarmConstants.SENDER_KEY_TIMESTAMP, currentTime.getTime());
            payload.put(FarmConstants.SENDER_KEY_EVENTLIST, measurementsToSend);
            SenderRequest request = new SenderRequest(payload, FarmMessageTag.UPDATE);

            //DONE: do this in separate thread. Already in separate thread
            sender.sendUpdateWithPayload(fieldUpdateServerAddress, request);

            //clear the measurements map
            measurementsMap.clear();


            //add the new measurements to the map
            addMeasurementBatchToMap(measurements);

            //modify the last update
            lastFieldStatusUpdate = currentTime;

        }else {
            //the time period has not passed
            //add the values to the list
            addMeasurementBatchToMap(measurements);

        }

        //unlock write lock
        myRWLock.writeLock().unlock();
    }

    private void addMeasurementBatchToMap(List<Measurement> batch){
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
    }

    /*NOT USED FROM THIS POINT*/

    public synchronized void clearDataStore(){
        measurementsMap.clear();
    }

    public synchronized void waitForSenderConfirmation(Future<SenderResult> response){
        SenderResult result = null;
        try {
            // block until the future result is available
            System.out.println("Waiting for sender");
            result = response.get();
            System.out.println("Done waiting");
            // the exceptions are being ignored without over complicating
            // failure modes, startup and recovery
        } catch (InterruptedException e) {
            // Current thread was interrupted
            e.printStackTrace();
        } catch (ExecutionException e) {
            // This should never be thrown
            System.out.println("Execution exception");
            e.printStackTrace();
        }

        if (!result.isSendingSuccessful()){
            System.out.println("Sending failed! Field Update Server is down!");
        }

    }
}
