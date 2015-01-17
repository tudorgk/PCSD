package com.acertainfarm.exceptions;

/**
 * This exception is the root of the exception hierarchy for the precision
 * farming scenario, and should be thrown whenever a more specialized exception
 * is not available.
 * 
 * @author vmarcos
 */
@SuppressWarnings("serial")
public class PrecisionFarmingException extends Exception {

	/**
	 * Constructor based on Exception constructors.
	 */
	public PrecisionFarmingException() {
		super();
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public PrecisionFarmingException(String message) {
		super(message);
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public PrecisionFarmingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor based on Exception constructors.
	 */
	public PrecisionFarmingException(Throwable ex) {
		super(ex);
	}
	
}
