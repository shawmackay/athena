/*
 * CacheManager.java
 *
 * Created on March 21, 2002, 3:49 PM
 */

package org.jini.projects.athena.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a cache of objects, these objects are all instances of the same class
 * i.e. a cache of Strings, or a cache of Properties.
 * Can control the expiration of the items within it's cache, or may delegate this duty to
 * another class, however it is up to the 'managing' class to ensure that the cache is cleaned regularly
 * @author  Internet
 */
public class CacheManager {

    protected java.util.HashMap cacheHashMap = new java.util.HashMap();
    
    private Thread threadCleaner;
    private Cleaner clean = new Cleaner();
    private String holdsClassesOf;
    private int activity_in = 0;
    private int activity_out = 0;
    private int expirations = 0;
    private Logger log = Logger.getLogger("org.jini.projects.athena.resources");
    /** Creates a new instance of CacheManager, indicating whether it should control the lifecycle of the objects it contains */
    public CacheManager(boolean createthread) {
        if (createthread) {
            try {
                threadCleaner = new Thread(clean);

                threadCleaner.setPriority(Thread.MIN_PRIORITY);
                threadCleaner.start();
            } catch (Exception ex) {
                System.out.println("CacheManager Static init: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Obtains the list off all the cache keys, which map to the identifier of any objects which are placed in the cache.
     */
    public java.util.Set getCacheKeys() {
        return cacheHashMap.keySet();
    }

    /**
     * Inserts an item into the cache.
     */
    public void putCache(Cacheable object) {        
         log.log(Level.FINE, "Adding an object to the cache");
        if (holdsClassesOf == null) {
            holdsClassesOf = object.getObject().getClass().getName();
        }
        cacheHashMap.put(object.getIdentifier(), object);
        activity_in++;
    }

    /**
     * Obtains an item from a cache, but does not remove it, therefore keeping it's reference alive.
     * Uses the given identifier to locate the correct object.
     */
    public Cacheable getCache(Object identifier) {
        Cacheable object = (Cacheable) cacheHashMap.get(identifier);
        if (object == null)
            return null;

        if (object.isExpired()) {
            cacheHashMap.remove(identifier);
            expirations++;
            return null;
        } else {
            log.log(Level.FINE, "Adding a read activity");
            activity_out++;
            return object;
        }
    }

    /**
     * Returns the number of times objects have been written to the cache.
     */
    public int getWriteActivity() {
        return activity_in;
    }

    /**
     * Returns the number of times objects have been read from
     *  the cache.
     */
    public int getReadActivity() {
        return activity_out;
    }

    /**
     * Returns the number of expirations that have occured
     */
    public int getExpirations() {
        return expirations;
    }

    /**
     * Returns the type of class that this object will handle
     */
    public String getClassType() {
        return holdsClassesOf;
    }

    /**
     * Requests that the cache be cleaned, removing any items that have expired
     */

    public void cleanCache() {
        java.util.Set keySet = cacheHashMap.keySet();
        java.util.Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Cacheable value = (Cacheable) cacheHashMap.get(key);

            if (value.isExpired()) {
                keys.remove();
                expirations++;
            }
        }
    }

    /**
     * Tells the cache to stop the expiry thread. There is no way to restart this thread.
     *  This should only be called as part of your system shutdown processes.
     */
    public void stopExpiring() {
        clean.stopScanning();
    }

    protected class Cleaner implements Runnable {
        int milliSecondSleepTime = 5000;

        private boolean stopnow = false;

        public void stopScanning() {
            stopnow = true;
        }

        public void run() {
            try {
                while (!stopnow) {
                    cleanCache();
                    Thread.sleep(milliSecondSleepTime);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
    }

}
