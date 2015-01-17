package com.acertainfarm.data;

/**
 * An event is a tuple <fieldID, timePeriod, avgTemperature, avgHumidity>, and
 * represents an average of multiple measurements for a field over the length of
 * a time period.
 * 
 * @author vmarcos
 */
public final class Event {

	/**
	 * The identifier of the field for the event.
	 */
	private final int fieldId;

	/**
	 * The rounded average of temperatures included in the event.
	 */
	private final int avgTemperature;

	/**
	 * The rounded average of humidity values included in the event.
	 */
	private final int avgHumidity;

	/**
	 * Constructs a new event with the given data.
	 */
	public Event(int fieldId, int avgTemperature, int avgHumidity) {
		this.fieldId = fieldId;
		this.avgTemperature = avgTemperature;
		this.avgHumidity = avgHumidity;
	}

	/**
	 * @return the fieldId
	 */
	public int getFieldId() {
		return fieldId;
	}

	/**
	 * @return the avgTemperature
	 */
	public int getAvgTemperature() {
		return avgTemperature;
	}

	/**
	 * @return the avgHumidity
	 */
	public int getAvgHumidity() {
		return avgHumidity;
	}

}
