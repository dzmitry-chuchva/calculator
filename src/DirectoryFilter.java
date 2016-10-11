/*
 * j2eetraining.test.grep.DirectoryFilter 0.1, 12/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.grep;

import java.io.*;

/**
 * Class for filtering directories.
 *
 * It is used while search, for keeping right output order (directories are searched after current files
 * of current directory are processed). 
 */
public class DirectoryFilter implements FileFilter {

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		if (pathname.isDirectory()) {
			return true;
		}
		return false;
	}
	
}