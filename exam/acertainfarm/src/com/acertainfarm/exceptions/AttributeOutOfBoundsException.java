package com.acertainfarm.exceptions;

/**
 * This exception is used to signal an out-of-bounds condition on any attribute
 * in a measurement or event, including sensorID, fieldID, temperature, or
 * humidity.
 * 
 * @author vmarcos
 */
@SuppressWarnings("serial")
public class AttributeOutOfBoundsException extends Exception {

	/**
	 * Constructor based on Exception constructors.
	 */
	public AttributeOutOfBoundsException() {
		super();
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public AttributeOutOfBoundsException(String message) {
		super(message);
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public AttributeOutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public AttributeOutOfBoundsException(Throwable ex) {
		super(ex);
	}

}
