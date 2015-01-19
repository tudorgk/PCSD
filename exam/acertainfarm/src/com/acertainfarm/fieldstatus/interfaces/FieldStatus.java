package com.acertainfarm.fieldstatus.interfaces;

import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.data.Event;
import com.acertainfarm.data.FieldState;
import com.acertainfarm.exceptions.PrecisionFarmingException;

import java.util.List;

/**
 * The FieldStatus service provides a query service to farm clients which offers
 * the most up-to-date snapshot of the state of the fields. The FieldStatus
 * service processes event updates every time period produced by the
 * SensorAggregator.
 * 
 * @author vmarcos
 */
public interface FieldStatus extends FieldStatusQuery,FieldStatusUpdate{

}
