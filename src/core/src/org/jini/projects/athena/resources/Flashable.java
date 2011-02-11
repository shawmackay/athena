/*
 * Flashable.java
 *
 * Created on April 8, 2002, 10:23 AM
 */

package org.jini.projects.athena.resources;

/**
 * Marks that an object can be modified in situ, without requiring the full, deallocate, New all objects, and the reinsert into caches.
 * Usually, used in conjunction with an Updater.
 * @see Updater
 * @author  calum
 */
public interface Flashable {
    /**
     * Modifies the object, retruninr the success status
     */
    public boolean applyFlash();
}
