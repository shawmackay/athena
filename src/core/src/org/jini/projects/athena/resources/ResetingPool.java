/*
 * ResetingPool.java
 *
 * Created on March 26, 2002, 11:36 AM
 */

package org.jini.projects.athena.resources;

/**
 * Allows an objetc to be validated and reset to a predetermined state
 * @author  calum
 */
public interface ResetingPool extends Pool {
    public void reset(Object o);

    public boolean validate(Object o);
}
