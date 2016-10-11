/*
 * j2eetraining.test.calc.Element 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc;

/**
 * Class, which represents abstract element of calculator.
 * Elements must specify their types, so method getType() is here. Also defined
 * two type constants (constant/function) - that's all what is supported in current moment. 
 */
public abstract class Element {
	public static final byte CONSTANT = 1;
	public static final byte FUNCTION = 2;
	
	abstract public byte getType();
}