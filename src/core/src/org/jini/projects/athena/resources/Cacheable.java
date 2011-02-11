/*
 * Cacheable.java
 *
 * Created on March 20, 2002, 10:11 AM
 */

package org.jini.projects.athena.resources;

/**
 * Interface which implies that an object may cache another object, and
 *  be associated with an expiration
 *
 * @author  calum
 */
public interface Cacheable {

    boolean isExpired();

    Object getIdentifier();

    Object getObject();


}
