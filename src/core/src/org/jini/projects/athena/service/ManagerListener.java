
package org.jini.projects.athena.service;

/**
 * Enables a class to register interest in receiving event regarding changes in Athena, such as
 * timeout changes, number of active connections, athena's hibernation status etc.
 * 
 * @author calum
 *
 */
public interface ManagerListener {
    public void notify(int event);
}
