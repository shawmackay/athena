/*
 * ResourcePoolManager.java
 * 
 * Created on March 26, 2002, 10:11 AM
 */

package org.jini.projects.athena.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a set of Pools and PoolManagers. Istance creation and Life cycle
 * issues are the reposibility of the individual PoolManagers
 * 
 * @author calum
 */
public class ResourcePoolManager {
	HashMap pools;
	Class defaultPoolClass;
	private Logger log = Logger.getLogger("org.jini.projects.athena.resources");

	/** Creates a new instance of ResourcePoolManager */
	public ResourcePoolManager() {
		defaultPoolClass = InstanceLifeCyclePool.class;
		pools = new HashMap();
	}

	/**
	 * Creates a new instance of ResourcePoolManager. This allows you to specify
	 * the class used to create the default pools
	 */
	public ResourcePoolManager(Class defaultPoolInstance) {
		if (defaultPoolInstance == SimplePool.class) {
			log.log(Level.WARNING, "Warning! Default pools are now static in size!");
		}
		defaultPoolClass = defaultPoolInstance;
		pools = new HashMap();
	}

	/**
	 * Creates a pool for the given pool of the given size
	 */
	public void addPool(Class clazz, int size) {
		try {
			Object poolObj = defaultPoolClass.newInstance();
			if (poolObj instanceof InstanceLifeCyclePool) {
				((InstanceLifeCyclePool) poolObj).setInstanceClass(clazz);
			}
			((Pool) poolObj).setInitialSize(size);
			pools.put(clazz.getName(), poolObj);
			log.log(Level.FINE, "Added a new ObjectPool for " + clazz.getName() + " objects.");
		} catch (InstantiationException instex) {
			log.log(Level.SEVERE, "Pool cannot be Instantiated");
			instex.printStackTrace();
		} catch (IllegalAccessException illex) {
			log.log(Level.SEVERE, "Pool cannot be Instantiated - illegal access");
		}
	}

	public void addPool(Class clazz, Pool pool) {
		log.log(Level.FINE, "Adding a " + pool.getClass().getName() + " pool, of " + clazz.getName() + " instances");
		pools.put(clazz.getName(), pool);
	}

	public void addPool(String prefix, Pool pool) {
		log.log(Level.FINE, "adding a " + pool.getClass().getName() + " pool, named " + prefix);
		pools.put(prefix, pool);
	}

	public void addPool(String prefix, Class clazz, int size) {
		try {
			Object poolObj = defaultPoolClass.newInstance();
			if (poolObj instanceof InstanceLifeCyclePool) {
				((InstanceLifeCyclePool) poolObj).setInstanceClass(clazz);
			}
			((Pool) poolObj).setInitialSize(size);
			pools.put(prefix, poolObj);
			log.log(Level.FINE, "Added a new ObjectPool for " + clazz.getName() + " objects. Alias " + prefix);
		} catch (InstantiationException instex) {
			log.log(Level.SEVERE, "Pool cannot be Instantiated");
			instex.printStackTrace();
		} catch (IllegalAccessException illex) {
			log.log(Level.SEVERE, "Pool cannot be Instantiated - illegal access");
		}
	}

	public void createPool(String prefix, Object[] objarr) {
		pools.put(prefix, new SimplePool(objarr));
	}

	public void createPool(String prefix, Collection coll) {
		pools.put(prefix, new SimplePool(coll));
	}

	public Object checkOutFromPool(String prefix) {
		Pool objPool = (Pool) pools.get(prefix);
		log.log(Level.FINE, "*****Checking out an object from the " + prefix + "pool");
		return objPool.checkOut();
	}

	public Object checkOutFromPool(Class clazz) {
		Pool objPool = (Pool) pools.get(clazz.getName());
		log.log(Level.FINE, "*****Checking out an object from the " + clazz.getName() + "pool");
		return objPool.checkOut();
	}

	public void checkInToPool(String prefix, Object obj) {
		Pool objPool = (Pool) pools.get(prefix);
		objPool.checkIn(obj);
	}

	public void checkInToPool(Object obj) {
		Pool objPool = (Pool) pools.get(obj.getClass().getName());
		objPool.checkIn(obj);
	}

	public void relinquish() {
	}

	public static void main(String[] args) {
		ResourcePoolManager rpm = new ResourcePoolManager();
		String[] myarr = {"Hello", "There", "!"};
		rpm.addPool(StringBuffer.class, 10);
		StringBuffer fromPool = (StringBuffer) rpm.checkOutFromPool(StringBuffer.class);
		StringBuffer fromPool2;
		fromPool.append("ME");
		rpm.checkInToPool(fromPool);
		fromPool = null;
		fromPool = (StringBuffer) rpm.checkOutFromPool(StringBuffer.class);
		System.out.println("fromPool = " + fromPool);
		fromPool2 = (StringBuffer) rpm.checkOutFromPool(StringBuffer.class);
		System.out.println("fromPool2 = " + fromPool2);
		System.out.println("Getting details");
		PoolDetails[] details = rpm.getPoolDetails();
		for (int i = 0; i < details.length; i++) {
			System.out.println("---------------");
			System.out.println("Pool Name:\t " + details[i].getAlias());
			System.out.println("Pool Class: \t" + details[i].getClassType());
			System.out.println("Available:\t " + details[i].getAvailable());
			System.out.println("Locked:\t " + details[i].getLocked());
		}
		rpm.checkInToPool(fromPool);
		rpm.checkInToPool(fromPool2);
		details = rpm.getPoolDetails();
		for (int i = 0; i < details.length; i++) {
			System.out.println("---------------");
			System.out.println("Pool Name:\t " + details[i].getAlias());
			System.out.println("Pool Class: \t" + details[i].getClassType());
			System.out.println("Available:\t " + details[i].getAvailable());
			System.out.println("Locked:\t " + details[i].getLocked());
		}
		fromPool = (StringBuffer) rpm.checkOutFromPool(StringBuffer.class);
		System.out.println("fromPool = " + fromPool);
		fromPool2 = (StringBuffer) rpm.checkOutFromPool(StringBuffer.class);
		System.out.println("fromPool2 = " + fromPool2);
		rpm.checkInToPool(fromPool);
		rpm.checkInToPool(fromPool2);
		details = rpm.getPoolDetails();
		System.out.println("Checked Into pool");
		for (int i = 0; i < details.length; i++) {
			System.out.println("---------------");
			System.out.println("Pool Name:\t " + details[i].getAlias());
			System.out.println("Pool Class: \t" + details[i].getClassType());
			System.out.println("Available:\t " + details[i].getAvailable());
			System.out.println("Locked:\t " + details[i].getLocked());
		}
	}

	public PoolDetails[] getPoolDetails() {
		PoolDetails[] details = new PoolDetails[pools.size()];
		java.util.Set entr = pools.entrySet();
		Iterator iter = entr.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Map.Entry item = (Map.Entry) iter.next();
			details[i++] = new PoolDetails((String) item.getKey(), (Pool) item.getValue());
		}
		return details;
	}
}