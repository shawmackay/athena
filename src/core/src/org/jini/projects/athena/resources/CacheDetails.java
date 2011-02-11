/*
 * ResourceDetails.java
 *
 * Created on April 25, 2002, 10:59 AM
 */

package org.jini.projects.athena.resources;

import java.util.Iterator;

/**
 * Represents the state of a given cache
 * @author  calum
 */
public class CacheDetails implements java.io.Serializable {

    private int numItems;
    private int totalActivity;
    private int readActivity;
    private int writeActivity;
    private int totalExpirations;
    private String alias;
    private String classType;
    private String[] cachedObjectNames;

    public CacheDetails(String name, CacheManager cman) {
        java.util.Set cacheNames = cman.getCacheKeys();
        Iterator iter = cacheNames.iterator();
        cachedObjectNames = new String[cacheNames.size()];
        int i = 0;
        while (iter.hasNext()) {
            cachedObjectNames[i++] = iter.next().toString();
        }
        alias = name;
        classType = cman.getClassType();
        readActivity = cman.getReadActivity();
        writeActivity = cman.getWriteActivity();
        totalActivity = readActivity + writeActivity;
        totalExpirations = cman.getExpirations();
    }

    public String getAlias() {
        return alias;
    }

    public String getClassType() {
        return classType;
    }

    public int getNumItems() {
        return numItems;
    }

    public int getTotalActivity() {
        return totalActivity;
    }

    public int getReadActivity() {
        return readActivity;
    }

    public int getWriteActivity() {
        return writeActivity;
    }

    public int getTotalExpirations() {
        return totalExpirations;
    }

    public String[] getCachedObjectNames() {
        return cachedObjectNames;
    }

}
