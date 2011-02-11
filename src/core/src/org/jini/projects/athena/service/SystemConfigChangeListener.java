/*
 * SystemConfigChangeListener.java
 *
 * Created on April 23, 2002, 3:15 PM
 */

package org.jini.projects.athena.service;

import java.util.ArrayList;

import org.jini.projects.thor.service.ChangeConstants;
import org.jini.projects.thor.service.ChangeEvent;



/**
 *
 * @author  calum
 */
public class SystemConfigChangeListener implements org.jini.projects.thor.service.ChangeEventListener, ChangeConstants {

    private int chType = ChangeConstants.WRITE;
    private ArrayList changelisteners = new ArrayList();

    /** Creates a new instance of SystemConfigChangeListener */
    public SystemConfigChangeListener() throws java.rmi.RemoteException {
    }

    public int getChangeType() throws java.rmi.RemoteException {
        System.out.println("Server is enquiring ChangeType");
        return chType;
    }

    public void notify(net.jini.core.event.RemoteEvent remoteEvent) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException {
        if (!(remoteEvent instanceof ChangeEvent))
            throw new net.jini.core.event.UnknownEventException("Unexpected Event Type");
        ChangeEvent cev = (ChangeEvent) remoteEvent;

        System.out.println("A change has occured ( Change No. " + cev.getSequenceNumber() + ", Type " + cev.getChangeType() + ")");
        for (int i = 0; i < changelisteners.size(); i++)
            ((ChangeListener) changelisteners.get(i)).notify(cev);
    }

    public void registerChangeListener(ChangeListener listener) {
        changelisteners.add(listener);
    }
}
