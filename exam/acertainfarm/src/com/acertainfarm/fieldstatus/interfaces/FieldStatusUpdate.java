package com.acertainfarm.fieldstatus.interfaces;

import com.acertainfarm.data.Event;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;

import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public interface FieldStatusUpdate {
    /**
     * Updates the current state of the fields with the event list given for the
     * time period supplied.
     *
     * @param timePeriod
     *            - The truncated timestamp representing the period in time to
     *            which the event corresponds.
     * @param events
     *            - the averaged measurements for a number of fields over a time
     *            period.
     */
    public void update(long timePeriod, List<Event> events)
            throws AttributeOutOfBoundsException, PrecisionFarmingException;

}
