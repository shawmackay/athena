package org.jini.projects.athena.connects.bdb;

import com.sleepycat.je.DatabaseEntry;

public interface BDBCommand {
	public static int UNKNOWN=0;
	public static int READ=1;
	public static int WRITE=2;
	public int getType();
	public DatabaseEntry getKey();
	public DatabaseEntry getData();
	public String getReturnType();
}
