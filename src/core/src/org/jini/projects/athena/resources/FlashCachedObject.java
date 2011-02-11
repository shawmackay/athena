/*
 * FlashCachedObject.java
 *
 * Created on April 8, 2002, 10:25 AM
 */

package org.jini.projects.athena.resources;

import java.util.Calendar;

/**
 * Represents an object that can be placed into a cache for future reference, but
 * can be dynamically updated on demand.
 * @author  calum
 */
public class FlashCachedObject implements Cacheable, Flashable {

    private java.util.Date timeofExpiration = null;

    private Object identifier = null;

    public Object obj = null;

    private Updater updater = null;

    private Object initiator = null;

    /** Creates a new instance of CachedObject
     * Similar to <code>CachedObject</code> but adds the update and initiation objects
     * The updater is the code that will be run in order to update this cached object, the initiator
     * is the object that was originally run to create the object that was placed in the cache, in the first place.
     */

    public FlashCachedObject(Object obj, Object id, int minutesToLive, Updater updater, Object initiator) {
        this.obj = obj;
        this.identifier = id;
        this.updater = updater;
        this.initiator = initiator;
        if (minutesToLive != 0) {
            timeofExpiration = new java.util.Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(timeofExpiration);
            cal.add(Calendar.MINUTE, minutesToLive);
            timeofExpiration = cal.getTime();
        }
    }

    public Object getIdentifier() {
        return identifier;
    }

    public boolean isExpired() {
        if (timeofExpiration != null) {
            if (timeofExpiration.before(new java.util.Date())) {
                System.out.println("Cached object is expired");
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    /**
     * Returns the object that was executed in order to create the initial cache entry.
     */

    Object getInitiator() {
        return initiator;
    }

    /**
     * Requests that the object is updated.
     * The Updater is used to re-execute the original initiator object, storing the new
     * results back into the cache in-situ. <b>Note:</b> You should not change the initiator object, or the updater.
     */

    public boolean applyFlash() {
        Object ob = updater.update(this.obj, this.initiator);
        if (ob != null) {
            this.obj = ob;
            return true;
        } else {
            System.out.println("Update failed");
            return false;
        }

    }

    public Object getObject() {
        return this.obj;
    }

}