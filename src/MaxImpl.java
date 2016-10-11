/*
 * j2eetraining.test.calc.MaxImpl 0.1, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.calc.functions;

import j2eetraining.tests.calc.*;
import java.util.*;

/**
 * MAX function implementation. (From set of predefined functions.)
 * This function takes variable count of arguments ( >=1 ).
 */
public class MaxImpl extends Function {

	public byte getArgumentsCount() {
		return VARIABLE;
	}

	public double call(double[] args) throws CalcException {
		if (args.length == 0) {
			throw new CalcException("error: function call is undefined without arguments");
		}
		Arrays.sort(args);
		return args[args.length - 1];
	}

}
