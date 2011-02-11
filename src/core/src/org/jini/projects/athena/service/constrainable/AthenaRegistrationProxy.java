/*
 * org.jini.projects.athena :
 * org.jini.projects.org.jini.projects.athena.service.constrainable
 * 
 * 
 * AthenaRegistrationProxy.java Created on 07-Jan-2004
 * 
 * AthenaRegistrationProxy
 *  
 */
package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ProxyTrustIterator;
import net.jini.security.proxytrust.SingletonProxyTrustIterator;
import net.jini.security.proxytrust.TrustEquivalence;

import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.exception.HostException;
import org.jini.projects.athena.service.AthenaRegistration;

/**
 * @author calum
 */
public class AthenaRegistrationProxy implements AthenaRegistration, Administrable, Serializable, ReferentUuid {
    final static class ConstrainableAthenaRegistrationProxy extends AthenaRegistrationProxy implements RemoteMethodControl {

        public ConstrainableAthenaRegistrationProxy(AthenaRegistration server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server, methodConstraints), id);
            l.fine("Creating a secure proxy");
        }

        public RemoteMethodControl setConstraints(MethodConstraints constraints) {
            return new AthenaRegistrationProxy.ConstrainableAthenaRegistrationProxy(backend, proxyID, constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }

        /*
    	 * Provide access to the underlying server proxy to permit the
    	 * ProxyTrustVerifier class to verify the proxy.
    	 */
    	private ProxyTrustIterator getProxyTrustIterator() {
    	    return new SingletonProxyTrustIterator(backend);
    	}
        }

        /** A trust verifier for secure smart proxies. */
        public final static class Verifier implements TrustVerifier, Serializable {
    	private final RemoteMethodControl serverProxy;
        
    	/**
    	 * Create the verifier, throwing UnsupportedOperationException if the
    	 * server proxy does not implement both RemoteMethodControl and
    	 * TrustEquivalence.
    	 */
    	public Verifier(AthenaRegistration serverProxy) {
    	    if (serverProxy instanceof RemoteMethodControl &&
    		serverProxy instanceof TrustEquivalence)
    	    {
    		this.serverProxy = (RemoteMethodControl) serverProxy;
    	    } else {
    		throw new UnsupportedOperationException();
    	    }
    	}

    	/** Implement TrustVerifier */
    	public boolean isTrustedObject(Object obj, TrustVerifier.Context ctx)
    	    throws RemoteException
    	{
    	    if (obj == null || ctx == null) {
    		throw new NullPointerException();
    	    } else if (!(obj instanceof ConstrainableAthenaRegistrationProxy)) {
    		return false;
    	    }
    	    RemoteMethodControl otherServerProxy =
    		(RemoteMethodControl) ((ConstrainableAthenaRegistrationProxy) obj).backend;
    	    MethodConstraints mc = otherServerProxy.getConstraints();
    	    TrustEquivalence trusted =
    		(TrustEquivalence) serverProxy.setConstraints(mc);
    	    return trusted.checkTrustEquivalence(otherServerProxy);
    	}
        }

    private static final long serialVersionUID = 267682616263L;
    transient Logger l = Logger.getLogger("org.jini.projects.athena.service.proxies");

    final AthenaRegistration backend;
    final Uuid proxyID;


    /**
	 *  
	 */
    public AthenaRegistrationProxy(AthenaRegistration backend, Uuid proxyID) {
        super();
        this.backend = backend;
        this.proxyID = proxyID;
        // URGENT Complete constructor stub for AthenaRegistrationProxy
    }

    
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public Object getAdmin() throws RemoteException {
        return backend.getAdmin();
    }

    /**
	 * @param duration
	 * @return @throws
	 *              RemoteException
	 * @throws LeaseDeniedException
	 * @throws HostException
	 */
    public AthenaConnection getConnection(long duration) throws RemoteException, LeaseDeniedException, HostException {
        return backend.getConnection(duration);
    }

    /**
	 * @param asUser
	 * @param duration
	 * @return @throws
	 *              RemoteException
	 * @throws LeaseDeniedException
	 * @throws HostException
	 */
    public AthenaConnection getConnection(String asUser, long duration) throws RemoteException, LeaseDeniedException, HostException {
        return backend.getConnection(asUser, duration);
    }

    /**
	 * @return @throws
	 *              RemoteException
	 */
    public int getLoad() throws RemoteException {
        return backend.getLoad();
    }

    /**
	 * @return @throws
	 *              RemoteException
	 */
    public HashMap getResourceDetails() throws RemoteException {
        return backend.getResourceDetails();
    }

    /**
	 * @return @throws
	 *              RemoteException
	 */
    public Vector getStatistics() throws RemoteException {
        return backend.getStatistics();
    }

    /**
	 * @return @throws
	 *              RemoteException
	 */
    public Properties getSystemConfig() throws RemoteException {
        return backend.getSystemConfig();
    }

   
    /*
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return backend.toString();
    }

    private static AthenaRegistration constrainServer(AthenaRegistration server, MethodConstraints methodConstraints){
		return (AthenaRegistration) ((RemoteMethodControl) server).setConstraints(methodConstraints);
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
