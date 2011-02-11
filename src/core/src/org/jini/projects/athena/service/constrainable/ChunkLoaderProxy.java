
package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ProxyTrustIterator;
import net.jini.security.proxytrust.SingletonProxyTrustIterator;
import net.jini.security.proxytrust.TrustEquivalence;

import org.jini.projects.athena.resultset.ChunkLoader;

/**
 * Jini 2.0 Smart proxy for ChunkLoader
 * 
 * @author calum
 */
public class ChunkLoaderProxy implements ChunkLoader, Serializable, ReferentUuid {
	transient Logger l = Logger.getLogger("org.jini.projects.athena.service.proxies");
	ChunkLoader backend;
	Uuid id;

	ChunkLoaderProxy(ChunkLoader backend, Uuid proxyID) {
		super();
		this.backend = backend;
		this.id = proxyID;
		// URGENT Complete constructor stub for AthenaRegistrationProxy
	};

	final static class ConstrainableChunkLoaderProxy extends ChunkLoaderProxy implements RemoteMethodControl {
		public ConstrainableChunkLoaderProxy(ChunkLoader server, Uuid id, MethodConstraints methodConstraints) {
			super(constrainServer(server, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new ChunkLoaderProxy.ConstrainableChunkLoaderProxy(backend, id, constraints);
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
		public Verifier(ChunkLoader serverProxy) {
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
		    } else if (!(obj instanceof ConstrainableChunkLoaderProxy)) {
			return false;
		    }
		    RemoteMethodControl otherServerProxy =
			(RemoteMethodControl) ((ConstrainableChunkLoaderProxy) obj).backend;
		    MethodConstraints mc = otherServerProxy.getConstraints();
		    TrustEquivalence trusted =
			(TrustEquivalence) serverProxy.setConstraints(mc);
		    return trusted.checkTrustEquivalence(otherServerProxy);
		}
	    }

	private static ChunkLoader constrainServer(ChunkLoader server, MethodConstraints methodConstraints) {
		return (ChunkLoader) ((RemoteMethodControl) server).setConstraints(methodConstraints);
	}

	/**
	 * @throws java.rmi.RemoteException
	 */
	public void cleanup() throws RemoteException {
		backend.cleanup();
	}

	/**
	 * @param i
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getChunk(int i) throws RemoteException {
		return backend.getChunk(i);
	}

	/**
	 * @param record
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getChunkFor(int record) throws RemoteException {
		return backend.getChunkFor(record);
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public int getChunkSize() throws RemoteException {
		return backend.getChunkSize();
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getFirstChunk() throws RemoteException {
		return backend.getFirstChunk();
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getLastChunk() throws RemoteException {
		return backend.getLastChunk();
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getNextChunk() throws RemoteException {
		return backend.getNextChunk();
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public ArrayList getPreviousChunk() throws RemoteException {
		return backend.getPreviousChunk();
	}

	/**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
	public int numberofChunks() throws RemoteException {
		return backend.numberofChunks();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return backend.toString();
	}

	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return id;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return ReferentUuids.compare(this, obj);
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return backend.hashCode();
	}
}