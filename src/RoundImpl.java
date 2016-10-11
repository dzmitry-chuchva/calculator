/*
 * j2eetraining.test.calc.RoundImpl 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc.functions;

import j2eetraining.tests.calc.*;

/**
 * ROUND function implementation. (From set of predefined functions.)
 */
public class RoundImpl extends Function {

	public byte getArgumentsCount() {
		return 1;
	}

	public double call(double[] args) {
		return Math.round(args[0]);
	}

}
