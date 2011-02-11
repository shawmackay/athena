/*
 * ResourceManager.java
 *
 * Created on March 25, 2002, 2:59 PM
 */

package org.jini.projects.athena.resources;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all managed resources for Athena, specifically any caches and Pools the system uses.
 * Implemented as a facade class
 * @author  Internet
 */
public class ResourceManager {
    private static boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
    private static ResourceManager resourcemanager;
    private Logger log = Logger.getLogger("org.jini.projects.athena.resources");
    ResourceCacheManager rcm = new ResourceCacheManager();
    ResourcePoolManager rpm = new ResourcePoolManager();

    /** Creates a new instance of ResourceManager */
    private ResourceManager() {

    }

    public static ResourceManager getResourceManager() {
        if (resourcemanager == null)
            resourcemanager = new ResourceManager();
        return resourcemanager;
    }

    public CacheDetails[] getCachingDetails() {
        return rcm.getCacheDetails();
    }

    public PoolDetails[] getPoolingDetails() {
        return rpm.getPoolDetails();
    }

    public void addCache(Class clazz) {
        rcm.addCache(clazz);
    }

    public void addCache(Class clazz, String prefix) {
        rcm.addCache(clazz, prefix);
    }

    public void addCache(Class clazz, String prefix, CacheManager cacheManager) {
        rcm.addCache(clazz, prefix, cacheManager);
    }

    public void addObjectToCache(Class clazz, Cacheable obj) {
        rcm.addObjectToCache(clazz, obj);
    }

    public void addObjectToCache(String prefix, Cacheable obj) {
        rcm.addObjectToCache(prefix, obj);
    }

    public void addObjectToCache(CachedObject obj) {
        rcm.addObjectToCache(obj);
    }

    public void addObjectToCache(String CachePrefix, Object obj, Object id, int ttl) {
        rcm.addObjectToCache(CachePrefix, obj, id, ttl);
    }

    public Cacheable enquireCache(String prefix, Object key) {
        return rcm.enquireCache(prefix, key);
    }

    public Cacheable enquireCache(Class clazz, Object key) {
        return rcm.enquireCache(clazz, key);
    }

    public Cacheable enquireCache(String stringifiedID) {
        return rcm.enquireCache(stringifiedID);
    }

    public int updateCachesLike(String pattern, String prefix) {
        log.log(Level.FINER, "ResourceManger: update caches " + pattern);
        return rcm.updateCachesLike(pattern, prefix);
    }

    //Pool methods


    public void addPool(Class clazz, int size) {
        rpm.addPool(clazz, size);
    }

    public void addPool(Class clazz, Pool pool) {
        rpm.addPool(clazz, pool);
    }


    public void addPool(String prefix, Pool pool) {
        rpm.addPool(prefix, pool);
    }

    public void addPool(String prefix, Class clazz, int size) {
        log.log(Level.FINER, "Initialising a Pool......");
        rpm.addPool(prefix, clazz, size);

    }

    public void createPool(String prefix, Object[] objarr) {
        rpm.createPool(prefix, objarr);
    }

    public void createPool(String prefix, Collection coll) {
        rpm.createPool(prefix, coll);
    }

    public Object checkOutFromPool(String prefix) {
        return rpm.checkOutFromPool(prefix);
    }

    public Object checkOutFromPool(Class clazz) {
        return rpm.checkOutFromPool(clazz);
    }

    public void checkInToPool(String prefix, Object obj) {
        rpm.checkInToPool(prefix, obj);
    }

    public void checkInToPool(Object obj) {
        rpm.checkInToPool(obj);
    }


    public void relinquish() {
        rcm.relinquish();
    }

    public static void main(String[] args) {
        System.out.println("Starting....");
        ResourceManager rm = ResourceManager.getResourceManager();
        rm.addCache(Integer.class, "INTG");
        rm.addObjectToCache("INTG", new CachedObject(new Integer(2), "myid", 1));
        Object obj;
        obj = ((CachedObject) rm.enquireCache("INTG@myid")).getObject();
        System.out.println("Obj out: " + obj.toString());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        rm.relinquish();
    }
}
