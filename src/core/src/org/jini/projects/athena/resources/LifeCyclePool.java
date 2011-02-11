/*
 * LifeCyclePool.java
 *
 * Created on March 25, 2002, 3:27 PM
 */

package org.jini.projects.athena.resources;

/**
 * Additional Pool methods for calsses wanting to handle the Object LifeCycle of it's contained objects
 * @author  calum
 */
public interface LifeCyclePool extends Pool {
    /**
     * Create a new object. The type of object created will be specified in the defining class.
     */
    public Object create();

    /**
     * Allow the givenobject to be garbage collected, by removing it from the pool, when all other live references have bee dropped
     */
    public void destroy(Object o);
}
