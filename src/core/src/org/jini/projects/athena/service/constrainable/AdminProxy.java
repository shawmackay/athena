/*
 * org.jini.projects.athena : org.jini.projects.org.jini.projects.athena.service.constrainable
 *
 *
 * AthenaAdminProxy.java
 * Created on 15-Jan-2004
 *
 * AthenaAdminProxy
 *
 */
package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

import org.jini.projects.athena.service.AthenaAdminProxy;

/**
 * @author calum
 */
public class AdminProxy implements AthenaAdminProxy, Serializable, ReferentUuid {
    public static final long serialVersionUID= -7768778164916544308L;//1411595219131688813L;
    transient Logger l = Logger.getLogger("org.jini.projects.athena.admin");
    final AthenaAdminProxy backend;
    final Uuid id;

    AdminProxy(AthenaAdminProxy backend, Uuid proxyID) {
        super();
        this.backend = backend;
        this.id = proxyID;
        // URGENT Complete constructor stub for AthenaRegistrationProxy
    };

    final static class ConstrainableAdminProxy extends AdminProxy implements RemoteMethodControl {

        public ConstrainableAdminProxy(AthenaAdminProxy server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server, methodConstraints), id);
            l.fine("Creating a secure proxy");
        }

        public RemoteMethodControl setConstraints(MethodConstraints constraints) {
            return new AdminProxy.ConstrainableAdminProxy(backend, id, constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }

    }


    private static AthenaAdminProxy constrainServer(AthenaAdminProxy server, MethodConstraints methodConstraints) {
        return (AthenaAdminProxy) ((RemoteMethodControl) server).setConstraints(methodConstraints);
    }

    

 
    public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
        backend.addLookupAttributes(attrSets);
    }


    public void addLookupGroups(String[] groups) throws RemoteException {
        backend.addLookupGroups(groups);
    }

    
    public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.addLookupLocators(locators);
    }

    
    public void destroy() throws RemoteException {
        backend.destroy();
    }

   

    
    public Entry[] getLookupAttributes() throws RemoteException {
        return backend.getLookupAttributes();
    }

  
    public String[] getLookupGroups() throws RemoteException {
        return backend.getLookupGroups();
    }

  
    public LookupLocator[] getLookupLocators() throws RemoteException {
        return backend.getLookupLocators();
    }

   

   
    public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
        backend.modifyLookupAttributes(attrSetTemplates, attrSets);
    }

    /**
     * @param groups
     * @throws java.rmi.RemoteException
     */
    public void removeLookupGroups(String[] groups) throws RemoteException {
        backend.removeLookupGroups(groups);
    }

    /**
     * @param locators
     * @throws java.rmi.RemoteException
     */
    public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.removeLookupLocators(locators);
    }

   
    public void setLookupGroups(String[] groups) throws RemoteException {
        backend.setLookupGroups(groups);
    }

    /**
     * @param locators
     * @throws java.rmi.RemoteException
     */
    public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.setLookupLocators(locators);
    }

    /* @see java.lang.Object#toString()
     */
    public String toString() {
        return backend.toString();
    }

   
    public Map getTypeInformation() throws RemoteException {
        return backend.getTypeInformation();
    }

   
    public void hibernate() throws RemoteException {
        backend.hibernate();
    }

    
    public void wake() throws RemoteException {
        backend.wake();
    }

    
    public Map getHandlerDetails() throws RemoteException {
        
        return backend.getHandlerDetails();
    }

	/* @see org.jini.projects.athena.service.AthenaAdmin#getDialectDetails()
	 */
	public Map getDialectDetails() throws RemoteException {
		// TODO Complete method stub for getDialectDetails
		return backend.getDialectDetails();
	}
	/* @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return id;
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
