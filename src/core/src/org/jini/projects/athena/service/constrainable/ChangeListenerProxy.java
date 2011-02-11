/*
 * org.jini.projects.athena : org.jini.projects.org.jini.projects.athena.service.constrainable
 *
 *
 * ChengeListenerProxy.java
 * Created on 16-Jan-2004
 *
 * ChengeListenerProxy
 *
 */
package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

import org.jini.projects.thor.service.ChangeEventListener;



/**
 * @author calum
 */
public class ChangeListenerProxy implements Serializable, ChangeEventListener, ReferentUuid{

    private static final long serialVersionUID = -6027256357904423577L;

    transient Logger l = Logger.getLogger("thor.events");

    final ChangeEventListener backend;
    final Uuid proxyID;


    /**
     *
     */
    ChangeListenerProxy(ChangeEventListener backend, Uuid proxyID) {
        super();
        this.backend = backend;
        this.proxyID = proxyID;
// URGENT Complete constructor stub for AthenaRegistrationProxy
    };


    final static class ConstrainableChangeListenerProxy extends ChangeListenerProxy implements RemoteMethodControl {

        public ConstrainableChangeListenerProxy(ChangeEventListener server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server, methodConstraints), id);
            l.fine("Creating a secure proxy");
        }

        public RemoteMethodControl setConstraints(MethodConstraints constraints) {
            return new ChangeListenerProxy.ConstrainableChangeListenerProxy(backend, proxyID, constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }

    }

    private static ChangeEventListener constrainServer(ChangeEventListener server, MethodConstraints methodConstraints) {
        return (ChangeEventListener) ((RemoteMethodControl) server).setConstraints(methodConstraints);
    }

   

    /**
     * @param theEvent
     * @throws net.jini.core.event.UnknownEventException
     * @throws java.rmi.RemoteException
     */
    public void notify(RemoteEvent theEvent) throws UnknownEventException, RemoteException {
        backend.notify(theEvent);
    }

    /* @see java.lang.Object#toString()
     */
    public String toString() {
        return backend.toString();
    }

    /* @see net.jini.id.ReferentUuid#getReferentUuid()
     */
    public Uuid getReferentUuid() {
        // TODO Complete method stub for getReferentUuid
        return proxyID;
    }
    
     /* @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return ReferentUuids.compare(this, obj);
    }
    
    /* @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return backend.hashCode();
    }
}
