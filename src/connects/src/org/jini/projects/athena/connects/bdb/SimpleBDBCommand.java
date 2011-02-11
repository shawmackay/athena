package org.jini.projects.athena.connects.bdb;

import com.sleepycat.je.DatabaseEntry;

public class SimpleBDBCommand implements BDBCommand{

	private int type;
	private DatabaseEntry key;
	private DatabaseEntry data;
	private String returnType;
	
	public SimpleBDBCommand(int type, DatabaseEntry key, DatabaseEntry data, String returnType){
		this.type = type;
		this.key = key;
		this.data = data;
		this.returnType = returnType;
	}

	public int getType() {
		return type;
	}

	public DatabaseEntry getKey() {
		return key;
	}

	public DatabaseEntry getData() {
		return data;
	}

	public String getReturnType() {
		// TODO Auto-generated method stub
		return returnType;
	}
	
	
	
	
}
