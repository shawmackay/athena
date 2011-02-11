package org.jini.projects.athena.connects.bdb;

import java.io.UnsupportedEncodingException;

public class BDBString implements BDBObject{

	private String data;
	
	public BDBString(String data){
		this.data = data;
	}
	
	public byte[] getBytes() {
	// TODO Auto-generated method stub
	try {
		return data.getBytes("UTF-8");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	}

	public Object getValue() {
		// TODO Auto-generated method stub
		return data;
	}
}
