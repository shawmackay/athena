/**
 * Title:
 * <p>
 * 
 * Description:
 * <p>
 * 
 * Copyright: Copyright (c)
 * <p>
 * 
 * Company:
 * <p>
 * 
 * @author
 * 
 * @version 0.9community
 */

package org.jini.projects.athena.service;

//

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

/**
 * Simple persistency class for storing and restoring an object to/from a file
 * 
 * @author calum 09 October 2001
 */
public class AthenaLogger {
	private static String logDir = System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar;
	private static Logger l = Logger.getLogger("org.jini.projects.athena.service");

	static {
		File f = new File(logDir);
		if (!f.exists()) {
			System.out.println("Creating data directory");
			f.mkdir();
		}
	}

	/**
	 * Set the base directory to which log will be stored
	 * 
	 * @param dir
	 *                  Name of directory
	 * @since
	 */
	public static void setLogDir(String dir) {
		l.info("Setting log directory to " + dir);
		logDir = dir;
	}

	/**
	 * Persist an object to file. Persists a given object <CODE>item</CODE> to
	 * file <CODE>filename</CODE> relative to the base directory
	 * 
	 * @param filename
	 *                  File to persist into
	 * @param item
	 *                  Object to persist
	 * @since
	 * @throws IOException
	 *                   thrown if an error occurs during writing
	 */
	public synchronized static void persist(String filename, Object item) throws IOException {
		l.finest("Trying to persist the Log File!!!");
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(logDir + filename));
		l.finest("Stream opened - writing object");
		os.writeObject(item);
		l.finest("Object Written - Flushing");
		os.flush();
		l.finest("Flushed - Closing");
		os.close();
		l.finest("Log File Persisted!!!");
	}

	/**
	 * Restore an object from file. Restore object stored in file <CODE>
	 * filename</CODE> relative to the base directory
	 * 
	 * @param filename
	 *                  File to restore
	 * @return Object stored in file
	 * @since
	 * @throws IOException
	 *                   if an error occurs during reading the file
	 * @throws ClassNotFoundException
	 *                   If the object cannot be successfully deserialised
	 */
	public static synchronized Object restore(String filename) throws IOException, ClassNotFoundException {
		ObjectInputStream os = new ObjectInputStream(new FileInputStream(logDir + filename));
		Object retval = os.readObject();
		os.close();
		return retval;
	}
}
