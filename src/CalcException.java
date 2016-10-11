/*
 * j2eetraining.test.calc.CalcException 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc;


/**
 * Application defined exception for errors managing purposes.
 */
public class CalcException extends Exception {
	
	/**
	 * Constructs an exception with given error message.
	 * @param message message which describes error
	 */
	public CalcException(String message) {
		super(message);
	}
}
