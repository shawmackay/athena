/*
 * CachedObject.java
 *
 * Created on March 20, 2002, 11:05 AM
 */

package org.jini.projects.athena.resources;

import java.util.Calendar;

/**
 * Contains an object, giving it an expiration and
 * allowing a cache to manage it.
 * @author  Internet
 */
public class CachedObject implements Cacheable {

    private java.util.Date timeofExpiration = null;

    private Object identifier = null;

    protected Object obj = null;

    /** Creates a new instance of CachedObject
     * Bad performance cut down on Obj creation
     */

    public CachedObject(Object obj, Object id, int minutesToLive) {
        this.obj = obj;
        this.identifier = id;
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

    public Object getObject() {
        return this.obj;
    }

}
