/*
 * j2eetraining.test.calc.SqrtImpl 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc.functions;

import j2eetraining.tests.calc.*;

/**
 * SQRT function implementation. (From set of predefined functions.)
 */
public class SqrtImpl extends Function {

	public byte getArgumentsCount() {
		return 1;
	}

	public double call(double[] args) {
		return Math.sqrt(args[0]);
	}

}
