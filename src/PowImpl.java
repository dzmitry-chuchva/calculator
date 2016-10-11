/*
 * j2eetraining.test.calc.PowImpl 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc.functions;

import j2eetraining.tests.calc.*;

/**
 * POW function implementation. (From set of predefined functions.)
 */
public class PowImpl extends Function {

	public byte getArgumentsCount() {
		return 2;
	}

	public double call(double[] args) {
		return Math.pow(args[0],args[1]);
	}

}
