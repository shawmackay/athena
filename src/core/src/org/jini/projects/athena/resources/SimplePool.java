/*
 * SimplePool.java
 * 
 * Created on March 25, 2002, 3:33 PM
 */

package org.jini.projects.athena.resources;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Non synchronized, represents a simple n-sized, non-resizable Pool
 * 
 * @author calum
 */
public class SimplePool implements Pool {
	private java.util.ArrayList available;
	private String classType;
	private java.util.ArrayList locked;

	/** Creates a new instance of SimplePool */
	private SimplePool() {
	}

	public SimplePool(Collection coll) {
		this(coll.toArray());
	}

	public SimplePool(Object[] arr) {
		classType = arr[0].getClass().getName();
		available = new ArrayList(arr.length);
		locked = new ArrayList();
		for (int i = 0; i < arr.length; i++)
			available.add(arr[i]);
	}

	public void checkIn(Object o) {
		synchronized(this) {
			locked.remove(o);
			available.add(o);
		}
	}

	public Object checkOut() {
		synchronized (this) {
			Object o;
			if (available.size() > 0) {
				o = available.remove(0);
				locked.add(o);
				return o;
			}
		}
		return null;
	}

	public void setInitialSize(int size) {
	}

	public int getAvailable() {
		return available.size();
	}

	public String getClassType() {
		return classType;
	}

	public int getLocked() {
		return locked.size();
	}

}
