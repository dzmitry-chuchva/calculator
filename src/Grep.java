/*
 * j2eetraining.test.grep.Grep 0.1, 12/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */

package j2eetraining.tests.grep;

import java.util.regex.*;
import java.io.*;

/**
 * Main class, performs all file processing. 
 *
 * Execution starts with method main of this class. Here come command line
 * parsing instructions, than the threads are starting up. One thread is a
 * {@link FileFinder} class, which implements {@link Runnable} interface. 
 * This thread searches for files and accumulates them for future use by 
 * parsing threads, which are samples of class Grep, because it also 
 * implements {@link Runnable} interface. Parse results are delivered by 
 * each thread to FileFinder, and FileFinder prints out all results in 
 * right order. After all that stuff ends method main continues execution 
 * and summarizes work results in some statistics, printed out at end.
 * 
 * <br><strong>Excuse me for my English, especially grammar. :)</strong> 
 * 
 * @see FileFinder
 * 
 */
public class Grep implements Runnable {
	
	public static long filesTotal = 0;
	public static long linesTotal = 0;
	public static long linesMatchedTotal = 0;
	
	/**
	 * Mutex object. This object serves for synch access to variables filesTotal, linesTotal, linesMatchedTotal. 
	 */
	private static Object mutex = new Object();
	
	/**
	 * String, representing commited thru command line regular expression for search. 
	 */
	protected static String regexp = null;
	/**
	 * Output file object. Used for result output, default - console, maybe file.
	 */
	private static PrintStream outfile = System.out;
	private static Pattern p = null;
	
	/**
	 * FileFinder object. FileFinder is used for searching files, that are matching mask. Searching
	 * happens in separate thread for improving performance. Also, this object collects result of
	 * parsing from grep threads, and print out them in right order.
	 */
	private static FileFinder finder;
		
	/**
	 * Entry point.
	 * 
	 * Entry point of Java application determines class with method main defined (so called main class).
	 * 
	 * @param args command line arguments represented in array form 
	 */
	public static void main(String[] args) {
		int i = 0;
		
		/* variables denoting default values */
		boolean caseSens = true;
		boolean recursive = true;
		String mask = "*.*";
		int threadCount = 5;
		
		File temp = null;
		String outputFileName = null;
		
		/* here comes command line parsing */
		while (i < args.length)
		{
			if (args[i].charAt(0) == '-') {
				if (args[i].length() != 2) {
					System.err.println("unknown option " + args[i]);
					usage(System.err);
					System.exit(1);
				}
				switch (args[i].charAt(1)) {
				case 'i':
					caseSens = true;
					break;
				case 'I':
					caseSens = false;
					break;
				case 'r':
					recursive = false;
					break;
				case 'R':
					recursive = true;
					break;
				case 'm':
					try {
						mask = args[++i];
					}
					catch (IndexOutOfBoundsException e) {
						System.err.println("expected mask");
						usage(System.err);
						System.exit(1);
					}
					break;
				case 't':
					try {
						threadCount = Integer.parseInt(args[++i]);
					}
					catch (IndexOutOfBoundsException e) {
						System.err.println("expected thread count");
						usage(System.err);
						System.exit(1);
					}
					catch (NumberFormatException e) {
						System.err.println("invalid thread count");
						usage(System.err);
						System.exit(1);						
					}
					if (threadCount <= 0) {
						System.err.println("invalid thread count");
						usage(System.err);
						System.exit(1);
					}
					break;
				case 'o':
					try {
						/* create temp file for results */
						temp = File.createTempFile("tmp",".tmp");
						outfile = new PrintStream(temp.getCanonicalFile());
						outputFileName = args[++i];						
					}
					catch (IndexOutOfBoundsException e) {
						System.err.println("expected file name");
						usage(System.err);
						System.exit(1);
					}
					catch (FileNotFoundException e) {
						System.err.println(e.toString());
						System.exit(1);
					}
					catch (IOException e) {
						System.err.println(e.toString());
						System.exit(1);												
					}
					break;
				default:
					System.err.println("unknown option: -" + args[i].charAt(1));
					usage(System.err);
					System.exit(1);
					break;
				}
			}
			else {
				if (i != args.length - 1) {
					System.err.println("unexpected " + args[i]);
					usage(System.err);
					System.exit(1);					
				}
				regexp = args[i];
			}
			i++;
		}
		
		if (regexp == null) {
			usage(System.out);
			System.exit(1);
		}
		
		if (!caseSens) {
			/* add special char sequences for case insensitive search, if needed */
			regexp = "(?iu)" + regexp;
		}
		
		/* prepare our regular expression to work */
		p = Pattern.compile(regexp);
		
		/* create and start FileFinder thread with requested parameters */
		finder = new FileFinder(".",mask,recursive,outfile);
		Thread thread = new Thread(finder);
		thread.start();
				
		/* create and start "grepping" threads */
		Thread[] samples = new Thread[threadCount];
		for (i = 0; i < threadCount; i++) {
			samples[i] = new Thread(new Grep());
			samples[i].start();
		}
				
		/* wait for all "grepping" threads terminate */
		boolean alive;
		while (true) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
			}
			
			alive = false;			
			for (i = 0; i < threadCount; i++) {
				if (samples[i].isAlive()) {
					alive = true;
					break;
				}
			}
			if (!alive)
				break;
		}
		
		/* output statistics */
		outfile.println("-----");
		outfile.println("Total files: " + filesTotal);
		outfile.println("Total lines: " + linesTotal);
		outfile.println("Total matching lines found: " + linesMatchedTotal);
		outfile.close();
		
		/* to exclude output file from processing, we create a temp file
		 * now we need to put it on it's place in current directory
		 */
		if (temp != null) {
			File out = new File(outputFileName);
			if (!temp.renameTo(out)) {
				out.delete();
				if (!temp.renameTo(out)) {
					System.err.println("unable to save output file");
				}
			}
		}
	}
	
	/**
	 * Prints usage information.
	 * 
	 * @param out printstream, which will print information.
	 */
	public static void usage(PrintStream out) {
		out.println("usage: Grep [-i|I] [-r|R] [-m mask] [-t threadCount] [-o file] regexp");		
	}

	/**
	 * Threads execution starts and ends with this function.
	 */
	public void run() {
		String newfile = null;
		
		/* process files while it is possible */
		while ((newfile = finder.requestFile()) != "") {
			if (newfile == null) {
				try {
					Thread.sleep(100);					
				}
				catch (InterruptedException e) {
				}
				continue;
			}
			FileReader f = null;
			BufferedReader bf = null;
			File path = new File(newfile);
			if (path.exists() && path.isFile()) {
				try {
					f = new FileReader(newfile);
					bf = new BufferedReader(f);
					String line = null;
					int linesMatched = 0;
							
					/* syncronize main variables modification */
					synchronized (mutex) {
						filesTotal++;
					}
					while ((line = bf.readLine()) != null) {
						/* using Matcher for regular expressions */
						Matcher m = p.matcher(line);
						synchronized (mutex) {
							if (m.find()) {
								linesMatched++;
							}
							linesTotal++;
						}
					}
					linesMatchedTotal += linesMatched;
					
					/* deliver results back to FileFinder object */
					finder.takeInfo(newfile,linesMatched);
				}
				catch (FileNotFoundException e) {
					System.err.println("Error: " + e.toString());					
				}
				catch (IOException e) {
					System.err.println("Error: " + e.toString());					
				}
				finally {
					try {
						bf.close();						
					}
					catch (IOException e) {
						System.err.println("Error: " + e.toString());																								
					}
				}
			}
			else {
				/* deliver fake results back to FileFinder object to avoid cycling */
				finder.takeInfo(newfile,0);				
			}
		} // while ((newfile ...
	}
}
