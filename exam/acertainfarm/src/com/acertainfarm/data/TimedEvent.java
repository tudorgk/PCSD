package com.acertainfarm.data;

/**
 * Created by tudorgk on 17/1/15.
 */
public final class TimedEvent {
    private final long timePeriod;
    private final Event event;

    public TimedEvent(long timePeriod, Event event){
        this.timePeriod = timePeriod;
        this.event = event;
    }

    public long getTimePeriod(){
        return this.timePeriod;
    }

    public Event getEvent(){
        return this.event;
    }

    @Override
    public String toString() {
        return "time:" + timePeriod + "; fieldID = "+ event.getFieldId() +"; avgTemp = " + event.getAvgTemperature()+";avgHum = " + event.getAvgHumidity();
    }

}
