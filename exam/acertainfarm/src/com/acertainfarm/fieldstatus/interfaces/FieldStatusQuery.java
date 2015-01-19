package com.acertainfarm.fieldstatus.interfaces;

import com.acertainfarm.data.FieldState;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;

import java.util.List;

/**
 * Created by tudorgk on 19/1/15.
 */
public interface FieldStatusQuery {
    /**
     * Queries the current state of the fields requested.
     *
     * @param fieldIds
     *            - the identifiers of the fields being queried.
     * @return For each field, the most up-to-date temperature and humidity.
     */
    public List<FieldState> query(List<Integer> fieldIds)
            throws AttributeOutOfBoundsException, PrecisionFarmingException;

}
