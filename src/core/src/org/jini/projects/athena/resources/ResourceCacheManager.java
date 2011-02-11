/*
 * ResourceCacheManager.java
 *
 * Created on March 26, 2002, 10:10 AM
 */

package org.jini.projects.athena.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Manages a set of caches. Periodically requesting the caches to be updated.
 * @author  calum
 */
public class ResourceCacheManager {
    private final int waitTime = 10000;
    /** Creates a new instance of ResourceCacheManager */
    Thread reaper = new Thread(new CacheReaper());
    private boolean relinquished = false;
    
    private Logger log = Logger.getLogger("org.jini.projects.athena.resources");
    private HashMap caches;

    /**
     * Runs through all the caches in the set removing any expired items
     */
    protected class CacheReaper implements Runnable {
        public void run() {
            while (!relinquished)
                try {
                    Collection cacheSet = caches.values();
                    Iterator iter = cacheSet.iterator();
                    while (iter.hasNext()) {
                        CacheManager tCache = (CacheManager) iter.next();
                        tCache.cleanCache();
                    }
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ex) {
                        System.out.println("Thread Wake-up bomb");
                    }
                } catch (Exception ex) {
                    System.out.println("Err: " + ex.getMessage());
                    ex.printStackTrace();
                }

        }
    }

    public ResourceCacheManager() {
        caches = new HashMap();
        reaper.setPriority(Thread.MIN_PRIORITY);
        reaper.start();
    }

    /**
     * Add a cache of a certain type, allowing clients to reference it thorugh the fully qualified class name
     */
    public void addCache(Class clazz) {
        log.log(Level.FINE, "Adding a Cache of type: " + clazz.getName());
        caches.put(clazz.getName(), new CacheManager(false));
    }

    /**
     * Add a cache of a certain type, but allows a client of the cache to reference it with a the given prefix name
     */
    public void addCache(Class clazz, String prefix) {
       log.log(Level.FINE, "Adding a Cache of type: " + clazz.getName() + ". Alias " + prefix);
        caches.put(prefix, new CacheManager(false));
    }

    /**
     * Add ane xisiting cachemanager of a certain type, allowing a client of the cache to reference it with a the given prefix name
     */
    public void addCache(Class clazz, String prefix, CacheManager cacheManager) {
        log.log(Level.FINE, "Adding a " + cacheManager.getClass().getName() + " of type: " + clazz.getName() + ". Alias " + prefix);
        try {
            caches.put(prefix, cacheManager);
        } catch (Exception ex) {
            System.err.println("Err: CacheManager not found " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Add an object of a given type to it's appropriate cache. <b>Note:</b> The cache must exist previously
     */
    public void addObjectToCache(Class clazz, Cacheable obj) {
        CacheManager man = (CacheManager) caches.get(clazz.getName());
        man.putCache(obj);
    }

    /**
     * Add an object to the given named cache
     */
    public void addObjectToCache(String prefix, Cacheable obj) {
        CacheManager man = (CacheManager) caches.get(prefix);
        man.putCache(obj);
    }

    /**
     * Add an object to a cache. The appropriate cache is determined from the cached object type.
     */
    public void addObjectToCache(CachedObject obj) {
        CacheManager man = (CacheManager) caches.get(obj.obj.getClass().getName());
        man.putCache(obj);
    }

    /**
     * Add an object to a cache, creating the cache wrapper on-the-fly.
     */
    public void addObjectToCache(String CachePrefix, Object obj, Object id, int ttl) {
        CacheManager man = (CacheManager) caches.get(CachePrefix);
        man.putCache(new CachedObject(obj, id, ttl));
    }

    /**
     * Enquires whether the named cache is updatable
     */
    public boolean isCacheUpdatable(String prefix) {
        return false;
    }

    /**
     * Enquires whether the class-named cache is updatable
     */
    public boolean isCacheUpdatable(Class clazz) {
        return false;
    }

    /**
     * Updates that items, matching the given pattern, in the named cache, be updated.
     */
    public int updateCachesLike(String pattern, String prefix) {
        try {
            FlashableCacheManager man = (FlashableCacheManager) caches.get(prefix);
            return man.flashPatternCache(pattern);
        } catch (ClassCastException ccex) {
            System.out.println("This cache is not updatable: " + caches.get(prefix).getClass().getName());
        }
        return 0;
    }

    /**
     * Get an item out of the named cache
     */
    public Cacheable enquireCache(String prefix, Object key) {
        CacheManager man = (CacheManager) caches.get(prefix);
        return man.getCache(key);
    }

    /**
     * Get an item out of the class-named cache
     */
    public Cacheable enquireCache(Class clazz, Object key) {
        CacheManager man = (CacheManager) caches.get(clazz.getName());
        return man.getCache(key);
    }

    /**
     * Get an item, using a stringified reference.<br>
     * Stringified references are in the form of either
     * class-name@id or cache-name@id<br>
     * i.e.
     * java.lang.string@SYSTEMNAME
     * or
     * RSET@TESTP1240504R
     */
    public Cacheable enquireCache(String stringifiedID) {
        String x = stringifiedID.substring(0, stringifiedID.indexOf("@"));
        String tail = stringifiedID.substring(stringifiedID.indexOf("@") + 1);
        System.out.println(x);
        System.out.println(tail);
        return enquireCache(x, tail);
    }

    /**
     * Informs all contained caches to stop expiring (if they are handling their own lifecycle) and
     * stop the overall cache reaper
     */
    public void relinquish() {
        relinquished = true;
        System.out.println("Stopping Resource cleaner");
        reaper.interrupt();
        Iterator iter = caches.values().iterator();

        while (iter.hasNext()) {
            CacheManager man = (CacheManager) iter.next();
            man.stopExpiring();
        }
    }

    /**
     * Get an array of all details for all the caches
     */
    public CacheDetails[] getCacheDetails() {
        CacheDetails[] details = new CacheDetails[caches.size()];
        java.util.Set entr = caches.entrySet();
        Iterator iter = entr.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry item = (Map.Entry) iter.next();
            details[i++] = new CacheDetails((String) item.getKey(), (CacheManager) item.getValue());
        }
        return details;
    }

    /**
     * Test method
     */
    public static void main(String[] args) {
        ResourceCacheManager rcm = new ResourceCacheManager();
        rcm.addCache(String.class, "DATA");
        rcm.addCache(String.class, "DATA2");
        rcm.addCache(String.class, "DATA3");
        String label1 = "Entry";
        int x = 0;
        java.util.Random ran = new java.util.Random();
        for (int i = 0; i < 10; i++) {
            rcm.addObjectToCache("DATA", new String(label1 + i), new String(label1 + i), ran.nextInt(220));
            rcm.addObjectToCache("DATA2", new String(label1 + (i * 2)), new String(label1 + i), ran.nextInt(220));
            rcm.addObjectToCache("DATA3", new String(label1 + (i * 3)), new String(label1 + i), ran.nextInt(220));
        }
        for (int a = 0; a < 6; a++) {
            CacheDetails[] details = rcm.getCacheDetails();
            for (int i = 0; i < details.length; i++) {
                System.out.println("---------------");
                System.out.println("Cache Name:\t " + details[i].getAlias());
                System.out.println("Cache Class: \t" + details[i].getClassType());
                System.out.println("Activity:\t " + details[i].getTotalActivity() + "(R: " + details[i].getReadActivity() + " / W: " + details[i].getWriteActivity() + ")");
                System.out.println("Expirations:\t " + details[i].getTotalExpirations());
                String[] names = details[i].getCachedObjectNames();
                System.out.println("Object keys");
                for (int j = 0; j < names.length; j++) {
                    System.out.println("\t\t\t" + names[j]);
                }
            }
            try {
                System.out.println("Sleeping for one minute");
                Thread.sleep(60000);
            } catch (Exception ex) {
            }
        }
        rcm.relinquish();
    }
}