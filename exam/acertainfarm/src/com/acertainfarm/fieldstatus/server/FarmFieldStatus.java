package com.acertainfarm.fieldstatus.server;

import com.acertainfarm.data.Event;
import com.acertainfarm.data.FieldState;
import com.acertainfarm.data.Measurement;
import com.acertainfarm.data.TimedEvent;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.fieldstatus.log.FieldStatusLogManager;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatus implements FieldStatus {

    private long snapshotID = 0;
    private int numberOfFields = 10;
    private long lastUpdateTime = 0;
    ConcurrentMap<Integer,TimedEvent> aggregatedEventsMap = null;
    private String logFilePath = "field_status.log";
    private FieldStatusLogManager logManager = null;

    public FarmFieldStatus(){
        this.aggregatedEventsMap = new ConcurrentHashMap<Integer, TimedEvent>();
        try {
            logManager = new FieldStatusLogManager(logFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void update(long timePeriod, List<Event> events) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //TODO: implement
        //sanity checks
        for (Event ev: events) {
            if (ev.getFieldId() > numberOfFields || ev.getFieldId() < 1)
                throw new AttributeOutOfBoundsException("FieldID is invalid");
            if (ev.getAvgTemperature() < -50 || ev.getAvgTemperature() > 50)
                throw new AttributeOutOfBoundsException("Avg Temperature is invalid");
            if (ev.getAvgHumidity() < 0 || ev.getAvgHumidity() > 100)
                throw new AttributeOutOfBoundsException("Avg Humidity is invalid");
        }
        //update the snapshot
        snapshotID ++;

        for(Event ev: events){
            //add the timed events to the map. we replace the ol event object
            if (aggregatedEventsMap.get(ev.getFieldId())!=null){
                //if the time period is smaller in the database, add the new event
                if(aggregatedEventsMap.get(ev.getFieldId()).getTimePeriod() < timePeriod){
                    aggregatedEventsMap.put(ev.getFieldId(),new TimedEvent(timePeriod,ev));
                }
            }else{
                aggregatedEventsMap.put(ev.getFieldId(),new TimedEvent(timePeriod,ev));
            }
        }

        //TODO: Do not forget about the log manager // write on the log after each update


    }

    @Override
    public synchronized List<FieldState> query(List<Integer> fieldIds) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        if (fieldIds.isEmpty() || fieldIds==null){
            throw new PrecisionFarmingException("Input Field Id list is empty");
        }

        //TODO: check implementation
        List<FieldState> fieldStateList = new ArrayList<FieldState>();

        for (Integer fieldID : fieldIds){
            //sanity checks
            if (fieldID > numberOfFields || fieldID < 1)
                throw new AttributeOutOfBoundsException("FieldID is invalid");
            if (aggregatedEventsMap.get(fieldID) == null){
                throw new PrecisionFarmingException("Field ID is not monitored");
            }
            //get the event
            TimedEvent timedEvent = aggregatedEventsMap.get(fieldID);

            //construct the field state object for the response
            FieldState state = new FieldState(fieldID,timedEvent.getEvent().getAvgTemperature(),
                    timedEvent.getEvent().getAvgHumidity());
            fieldStateList.add(state);
        }

        //return the list with field state
        return fieldStateList;
    }
}
