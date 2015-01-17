package com.acertainfarm.data;

/**
 * A field state instance represents the most up-to-date averaged temperature
 * and humidity values for a given field. These instances are the query results
 * returned by the FieldStatus service.
 * 
 * @author vmarcos
 */
public final class FieldState {

	/**
	 * The identifier of the field being queried.
	 */
	private final int fieldId;

	/**
	 * The temperature of the field in the current snapshot.
	 */
	private final int temperature;

	/**
	 * The humidity of the field in the current snapshot.
	 */
	private final int humidity;

	/**
	 * Constructs a new field state instance with the given data.
	 */
	public FieldState(int fieldId, int temperature, int humidity) {
		this.fieldId = fieldId;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	/**
	 * @return the fieldId
	 */
	public int getFieldId() {
		return fieldId;
	}

	/**
	 * @return the temperature
	 */
	public int getTemperature() {
		return temperature;
	}

	/**
	 * @return the humidity
	 */
	public int getHumidity() {
		return humidity;
	}

}
