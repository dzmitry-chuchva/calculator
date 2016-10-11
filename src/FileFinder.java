/*
 * j2eetraining.test.grep.FileFinder 0.1, 12/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.grep;

import java.util.*;
import java.io.*;

/**
 * FileFinder class, does all searching and some output work.
 * 
 * FileFinder class is pretty complex, while author was unable to provide elegant
 * method for synchronizing output and achieve correct order (from current directory downto
 * subdirectories in alphabetical order). Output function was transfered from Grep thread
 * class. To synchronize output FileFinder searchs files in current directory and waits
 * until "grepping" threads finish processing them, only than FileFinder outputs results
 * delivered by working threads and downsteps to some subdirectory. This wait slows 
 * performance very much :(.       
 * 
 */
public final class FileFinder implements Runnable {
	
	/**
	 * Queue of files waiting for processing by "grepping" threads
	 */
	private LinkedList files;
	
	/* some flags and parameters that determine work of concrete object */
	private boolean searchDone = false, synchDone = false;
	private String path, mask;
	private boolean recursive;
	private PrintStream out;
	
	/* variables used in ordering and outputing engine */
	/**
	 * Total number of files, that are subject to process in current directory 
	 */
	private int number_issued = 0;
	
	/**
	 * Names of files, issued to "grepping" threads for processing
	 */
	private String[] issued;
	
	/**
	 * Results of processing of each issued file
	 */
	private long[] info;
	
	/**
	 * 
	 * Constructor creates concrete object of class.
	 * 
	 * @param startFrom specifies directory from which to start searching
	 * @param searchMask determines search mask for filtering file names
	 * @param recursive setups FileFinder for recursive searching
	 * @param out PrintStream, used for printing results
	 */
	public FileFinder(String startFrom, String searchMask, boolean recursive, PrintStream out) {
		path = startFrom;
		mask = searchMask;
		this.recursive = recursive;
		files = new LinkedList();
		this.out = out;
	}
	
	/**
	 * "Grepping" threads are using this method of FileFinder to get another
	 * file to process.
	 * @return relative filename for processing
	 */
	public String requestFile() {
		String result = null;
		
		synchronized (files) {
			try {
				/* get filename from head of queue */
				result = (String) files.remove();
			}
			catch (NoSuchElementException e) {
				/* this exception occur when search is done or is suspended */
				if (searchDone && files.size() == 0) {
					/* if done and all rest files are processed */
					result = "";
				}
				else {
					/* if suspended */
					result = null;
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * This method "grepping" threads are using for deliver results to output engine.
	 * 
	 * @param file filename, that was processed
	 * @param linesMatched result - number of matched lines in file
	 */
	public void takeInfo(String file, long linesMatched) {
		for (int i = 0; i < number_issued; i++) {
			if (file.equals(issued[i])) {
				/* setup info by index */
				info[i] = linesMatched;
				break;
			}
		}
		synchronized (info) {
			/* check, if all results are delivered */
			for (int i = 0; i < number_issued; i++) {
				if (info[i] == -1) {
					return;
				}
			}
			/* than, output them. issued array has already right 
			 * order, so we dont need to sort anything.
			 */
			for (int i = 0; i < number_issued; i++) {
				if (info[i] == 0) {
					out.println(issued[i] + ": no matching lines found");
				} else {
					out.println(issued[i] + ": " + info[i] + " matching lines found");
				}
			}
			/* tell, that search can continue with subdirectories */
			synchDone = true;
		}
	}
	
	/**
	 * 
	 * Method for finding files recursively in subdirectories.
	 * 
	 * @param f current directory where search is done 
	 * @param filter object, represeting mask for files   
	 */
	private void findFilesRecursively(File f, FileFilter filter) {
		
		/* listFiles does not guarantee any order of array, but on Windows
		 * it outputs in order of Windows codepage (i.e. aAbBcCdD and so on).
		 * i think, it is suitable for our problem :). 
		 */ 
		File[] list = f.listFiles(filter);
		File[] dirs = f.listFiles(new DirectoryFilter());
		
		if (list.length > 0) {
			number_issued = list.length;
			issued = new String[number_issued];
			info = new long[number_issued];
			Arrays.fill(info,-1);
						
			for (int i = 0; i < list.length; i++) {
				File pathf = new File(path);
				String relative = null;
				try {
					/* extract relative path */
					String fullpath = pathf.getCanonicalPath();
					String filepath = list[i].getCanonicalPath();
					relative = filepath.replace(fullpath + File.separator,"");
					issued[i] = relative;
				}
				catch (IOException e) {
					System.err.println(e.toString());
					System.exit(1);
				}
			}
			Arrays.sort(issued);
			for (int i = 0; i < issued.length; i++)
			{
				/* put file on process queue */
				synchronized (files) {
					files.add(issued[i]);
				}
			}
			/* wait until all queue will be processed */ 
			while (!synchDone) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
				}
			}
			synchDone = false;
		}
		/* continue searching in subdirectories */
		for (int i = 0; i < dirs.length; i++) {
			findFilesRecursively(dirs[i],filter);
		}
	}
	
	/**
	 * 
	 * Non-recursive version of findFiles.
	 * 
	 * @param f directory to search in
	 * @param filter object, representing mask for file filtering
	 */
	private void findFiles(File f, FileFilter filter) {
		if (f.isDirectory()) {
			File[] list = f.listFiles(filter);
			
			number_issued = list.length;
			issued = new String[number_issued];
			info = new long[number_issued];
			Arrays.fill(info,-1);
			for (int i = 0; i < number_issued; i++) {
				issued[i] = list[i].getName(); 
			}
			
			for (int i = 0; i < list.length; i++) {
				synchronized (files) {
					files.add(list[i].getName());
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		File p = new File(path);
		FileFilter filter = new RegexpNameFilter(mask);
		if (recursive) {
			findFilesRecursively(p,filter);
		}
		else {
			findFiles(p,filter);			
		}		
	
		/* signalize, that search is done */
		searchDone = true;
	}
	
}