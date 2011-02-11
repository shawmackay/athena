/*
 * ChangeListener.java
 *
 * Created on April 26, 2002, 11:05 AM
 */

package org.jini.projects.athena.service;

import org.jini.projects.thor.service.ChangeEvent;


/**
 *
 * @author  calum
 */
public interface ChangeListener {
    public void notify(ChangeEvent cevt);
}
