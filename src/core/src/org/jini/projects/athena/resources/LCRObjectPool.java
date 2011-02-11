/*
 * ObjectPool.java
 * 
 * Created on March 25, 2002, 3:05 PM
 */

package org.jini.projects.athena.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstract base class for pools that wish to handle object lifecycle and
 * re-initialisation of their contained objects.
 * 
 * @author calum
 */
public abstract class LCRObjectPool implements LifeCyclePool, ResetingPool {
	private static final long MINUTES = 60 * 1000L;
	protected long expirationTime = 5 * MINUTES;
	static boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
	protected String classType;
	protected java.util.HashMap available;
	protected java.util.HashMap locked;

	/** Creates a new instance of ObjectPool */
	public LCRObjectPool() {
		//expirationTime=60000L;
		available = new HashMap();
		locked = new HashMap();
	}

	public LCRObjectPool(long expirationTime) {
		this();
		this.expirationTime = expirationTime;
	}

	public synchronized void checkIn(Object o) {
		locked.remove(o);
		available.put(o, new Long(System.currentTimeMillis()));
	}

	public synchronized Object checkOut() {
		Object o = null;
		long now = System.currentTimeMillis();
		boolean removeit = false;
		if (available.size() > 0) {
			Set keySet = available.keySet();
			Iterator iter;
			//synchronized(keySet){
			iter = keySet.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				//if (((Long) available.get(o)).longValue())!=0
				if ((now - ((Long) available.get(o)).longValue()) > expirationTime) {
					if (DEBUG)
						System.out.println("Object from Pool has Expired");
					removeit = true;
					iter.remove();
					destroy(o);
					o = null;
				}
			}
			//}
			keySet = available.keySet();
			iter = null;
			//synchronized(keySet){
			iter = keySet.iterator();
			while (iter.hasNext()) {
				o = iter.next();
				if (validate(o)) {
					iter.remove();
					locked.put(o, new Long(now));
					break;
				} else {
					//Object failed validation
					iter.remove();
					destroy(o);
					o = null;
				}
			}
			//}
			//if (removeit)
			//available.remove(o);
		}
		if (o != null)
			return o;
		o = create();
		locked.put(o, new Long(now));
		return o;
	}

	public abstract Object create();

	public abstract void destroy(Object o);

	public abstract boolean validate(Object o);

	public abstract void reset(Object o);

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