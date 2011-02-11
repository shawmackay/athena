/*
 * InstanceLifeCyclePool.java
 * 
 * Created on March 26, 2002, 11:05 AM
 */

package org.jini.projects.athena.resources;

/**
 * Manages the lifecycle of a pool of objects, allowing new items to be created
 * on demand, and destroyed when necessary
 * 
 * @author calum
 */
public class InstanceLifeCyclePool extends LCRObjectPool {
	private Class instanceClass;

	/** Creates a new instance of InstanceLifeCyclePool */
	public InstanceLifeCyclePool() {
	}

	public InstanceLifeCyclePool(Class clazz) {
		instanceClass = clazz;
		classType = clazz.getName();
	}

	public void setInstanceClass(Class clazz) {
		instanceClass = clazz;
		classType = clazz.getName();
	}

	public void checkIn(Object o) {
		super.checkIn(o);
		reset(o);
	}

	public void setInitialSize(int size) {
		if (available.size() == 0 && locked.size() == 0) {
			System.out.print("Creating default pool");
			synchronized (available) {
				long now = System.currentTimeMillis();
				for (int i = 0; i < size; i++) {
					System.out.print('.');
					available.put(create(), new Long(now));
				}
				System.out.println();
			}
		}
	}

	public Object create() {
		try {
			return instanceClass.newInstance();
		} catch (InstantiationException instex) {
			System.out.println("Cannot Instantiate");
			instex.printStackTrace();
		} catch (IllegalAccessException instex) {
			System.out.println("Cannot Instantiate- Illegal Access");
			instex.printStackTrace();
		}
		return null;
	}

	public void destroy(Object obj) {
		System.out.println("Destroying an instance of " + obj.getClass().getName());
	}

	public boolean validate(Object o) {
		return true;
	}

	public void reset(Object o) {
	}
}