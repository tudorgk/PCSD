package com.acertainfarm.sensoraggregator.interfaces;

import com.acertainfarm.data.Event;


import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by tudorgk on 17/1/15.
 */
public interface Sender {
    public List<Future<String>> prepareForSending(Date timePeriod, List<Event> avgMeasurements);
}
