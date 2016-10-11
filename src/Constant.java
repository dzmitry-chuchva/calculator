/*
 * j2eetraining.test.calc.Constant 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc;

/**
 * Class, which represents a constant in calculator and is parent for all concrete constants.
 * This abstract class declares method value(), which developers must define for adding it's own
 * constant to calculator.
 */
public abstract class Constant extends Element {
	
	abstract public double value() throws CalcException;
	
	/* (non-Javadoc)
	 * @see j2eetraining.tests.calc.Element#getType()
	 */
	public byte getType() {
		return Element.CONSTANT;		
	}
}