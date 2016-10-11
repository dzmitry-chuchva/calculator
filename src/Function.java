/*
 * j2eetraining.test.calc.Function 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc;

/**
 * Class, which represents a function in calculator and is parent for all concrete functions.
 * This abstract class declares method call(), which developers must define for adding it's own
 * function to calculator. Arguments, which were transfered to function, contains "args" array.
 * <br>Also developers must define getArgumentCount() method, which must return count of arguments
 * of function (for error handling). Function also can take variable count of parameters, it is signaled
 * by special VARIABLE constant.
 */
public abstract class Function extends Element {
	
	public static final byte VARIABLE = -1;
	
	public byte getType() {
		return Element.FUNCTION;		
	}

	abstract public byte getArgumentsCount();
	abstract public double call(double[] args) throws CalcException;
}