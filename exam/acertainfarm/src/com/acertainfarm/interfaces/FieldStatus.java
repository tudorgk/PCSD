package com.acertainfarm.interfaces;

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
public interface FieldStatus {

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
