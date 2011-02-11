/*
 * Pool.java
 *
 * Created on March 25, 2002, 3:01 PM
 */

package org.jini.projects.athena.resources;

/**
 * Basic Pool interface, all Pool objects must either extend or implement this interface
 *
 * @author  Internet
 */
public interface Pool {
    /**
     * Set the initial size of the pool to <code>size</code>
     */
    public void setInitialSize(int size);

    /**
     * Get the number of objects in the pool that are locked, i.e. in use.
     */
    public int getLocked();

    /**
     * Get the number of objects in the pool that are available, i.e. not in use.
     */
    public int getAvailable();

    /**
     * Each pool can only handle one type of class, this allows pre-deterministic behaviour.
     */
    public String getClassType();

    /**
     * Obtain an item from the pool
     */
    public Object checkOut();

    /**
     * Return an object to the pool
     */
    public void checkIn(Object o);
}
