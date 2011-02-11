/*
 * ResourceDetails.java
 *
 * Created on April 25, 2002, 10:59 AM
 */

package org.jini.projects.athena.resources;


/**
 * Obtain details of a pool instance
 * @author  calum
 */
public class PoolDetails implements java.io.Serializable {

    private int totalActivity;
    private int locked;
    private int available;
    private String alias;
    private String classType;
    private String[] cachedObjectNames;

    public PoolDetails(String name, Pool pool) {
        locked = pool.getLocked();
        available = pool.getAvailable();
        alias = name;
        classType = pool.getClassType();
    }

    /**
     * Get the name by which this pool can be identified by through the Resource Manager
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Get the object type that this pool can handle
     */
    public String getClassType() {
        return classType;
    }

    /**
     * Get the number of objects in the pool that are currently in use
     */
    public int getLocked() {
        return locked;
    }

    /**
     * Get the number of objects in the pool that are currently not in use
     */
    public int getAvailable() {
        return available;
    }

}
