package com.acertainfarm.data;

/**
 * A measurement is a tuple <sensorID, fieldID, currentTemperature,
 * currentHumidity> relayed by a field access point from field sensors.
 * 
 * @author vmarcos
 */
public final class Measurement {

	/**
	 * The identifier for the sensor that originated the measurement.
	 */
	private final int sensorId;
	
	/**
	 * The identifier for the field where the measurement was generated.
	 */
	private final int fieldId;
	
	/**
	 * The temperature collected at the field for this measurement.
	 */
	private final int currentTemperature;
	
	/**
	 * The humidity collected at the field for this measurement.
	 */
	private final int currentHumidity;
	
	/**
	 * Constructs a new measurement with all necessary data.
	 */
	public Measurement(int sensorId, int fieldId, int currentTemperature, int currentHumidity) {
		this.sensorId = sensorId;
		this.fieldId = fieldId;
		this.currentTemperature = currentTemperature;
		this.currentHumidity = currentHumidity;
	}

	/**
	 * @return the sensorId
	 */
	public int getSensorId() {
		return sensorId;
	}

	/**
	 * @return the fieldId
	 */
	public int getFieldId() {
		return fieldId;
	}

	/**
	 * @return the currentTemperature
	 */
	public int getCurrentTemperature() {
		return currentTemperature;
	}

	/**
	 * @return the currentHumidity
	 */
	public int getCurrentHumidity() {
		return currentHumidity;
	}
	
}
