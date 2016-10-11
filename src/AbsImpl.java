/*
 * j2eetraining.test.calc.AbsImpl 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc.functions;

import j2eetraining.tests.calc.*;

/**
 * ABS function implementation. (From set of predefined functions.)
 */
public class AbsImpl extends Function {
	
	public double call(double[] args) {
		return Math.abs(args[0]);
	}

	public byte getArgumentsCount() {
		return 1;
	}

}
