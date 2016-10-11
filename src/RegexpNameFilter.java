/*
 * j2eetraining.test.grep.RegexpNameFilter 0.1, 12/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.grep;

import java.io.*;
import java.util.regex.*;

/**
 * Filters files by mask.
 * 
 * Masking is done with regular expressions: dos mask is translated to regular expression
 * and than is used in method String.matches().
 * 
 */
public class RegexpNameFilter implements FileFilter {
	
	/**
	 * Mask, that is in use.
	 */
	protected String mask;
	
	/**
	 * Constructor of object.
	 * Determines mask for filtering files.
	 * 
	 * @param str - mask to use. 
	 */
	public RegexpNameFilter(String str) {
		mask = str;
		
		/* do some translations */
		if (Pattern.compile("[\\Q|\\/><:\"\\E]").matcher(mask).find()) {
			System.err.println("mask contains invalid chars");
			System.exit(1);
		}
		mask = mask.replace("'","");
		mask = "\\Q" + mask + "\\E";
		mask = mask.replace("*","\\E.*\\Q");
		mask = mask.replace("?","\\E.\\Q");
		mask = "(?i)" + mask;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		boolean result = false;
		try {
			if (f.getName().matches(mask) && f.isFile()) {
				result = true;
			}
		}
		catch (PatternSyntaxException e) {
			System.err.println("check mask, unable to build regexp from mask, using default (*.*)");
			mask = "(?i).*\\..*";
		}
		return result;
	}
	
}


