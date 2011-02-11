/*
 * Updater.java
 *
 * Created on April 8, 2002, 10:56 AM
 */

package org.jini.projects.athena.resources;

/**
 * Interface that allows an object to update itself.
 *
 *
 * @author  calum
 */
public interface Updater {
    /**
     * Updates the object, given a reference to itself, and an object that
     * represents the update to be made, returning the new object
     */
    public Object update(Object in, Object apply);
}
