package com.acertainfarm.fieldstatus.proxies;

import com.acertainfarm.data.Event;
import com.acertainfarm.data.FieldState;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;

import java.util.List;

/**
 * Created by tudorgk on 17/1/15.
 */
public class FarmFieldStatusHTTPProxy implements FieldStatus {
    @Override
    public void update(long timePeriod, List<Event> events) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //TODO: Don't really need this
    }

    @Override
    public List<FieldState> query(List<Integer> fieldIds) throws AttributeOutOfBoundsException, PrecisionFarmingException {
        //TODO: Needs implementation
        return null;
    }
}
