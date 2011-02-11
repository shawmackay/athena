
/*
 * org.jini.projects.athena : org.jini.projects.org.jini.projects.athena.service.constrainable
 *
 *
 * AthenaConnectionProxy.java Created on 16-Jan-2004
 *
 * AthenaConnectionProxy
 *
 */

package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.transaction.Transaction;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ProxyTrustIterator;
import net.jini.security.proxytrust.SingletonProxyTrustIterator;
import net.jini.security.proxytrust.TrustEquivalence;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.RemoteConnection;
import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.exception.WrongReturnTypeException;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 * @author calum
 */
public class AthenaConnectionProxy implements Serializable, RemoteConnection, ReferentUuid {
	final static class ConstrainableAthenaConnectionProxy extends AthenaConnectionProxy implements RemoteMethodControl {
		public ConstrainableAthenaConnectionProxy(RemoteConnection server, Uuid id, MethodConstraints methodConstraints) {
			super(constrainServer(server, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new AthenaConnectionProxy.ConstrainableAthenaConnectionProxy(connection, proxyID, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) connection).getConstraints();
		}
		/*
		 * Provide access to the underlying server proxy to permit the
		 * ProxyTrustVerifier class to verify the proxy.
		 */
		private ProxyTrustIterator getProxyTrustIterator() {
		    return new SingletonProxyTrustIterator(connection);
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
		public Verifier(RemoteConnection serverProxy) {
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
		    } else if (!(obj instanceof ConstrainableAthenaConnectionProxy)) {
			return false;
		    }
		    RemoteMethodControl otherServerProxy =
			(RemoteMethodControl) ((ConstrainableAthenaConnectionProxy) obj).connection;
		    MethodConstraints mc = otherServerProxy.getConstraints();
		    TrustEquivalence trusted =
			(TrustEquivalence) serverProxy.setConstraints(mc);
		    return trusted.checkTrustEquivalence(otherServerProxy);
		}
	    }
	AthenaConnectionProxy(RemoteConnection conn, Uuid proxyID) {
		this.connection = conn;
		this.proxyID = proxyID;
	}

	private static final long serialVersionUID = 267682616263L;
	transient Logger l = Logger.getLogger("org.jini.projects.athena.connection");
	final RemoteConnection connection;
	final Uuid proxyID;

	private static RemoteConnection constrainServer(RemoteConnection server, MethodConstraints methodConstraints) {
		return (RemoteConnection) ((RemoteMethodControl) server).setConstraints(methodConstraints);
	}

	/**
	 * @return @throws
	 *              RemoteException
	 */
	public boolean canRelease() throws RemoteException {
		return connection.canRelease();
	}

	/**
	 * @param block
	 * @return @throws
	 *              RemoteException
	 */
	public boolean canRelease(boolean block) throws RemoteException {
		return connection.canRelease(block);
	}

	/**
	 * @param commands
	 * @return @throws
	 *              CannotExecuteException
	 * @throws EmptyResultSetException
	 * @throws WrongReturnTypeException
	 * @throws RemoteException
	 */
	public AthenaResultSet[] executeBatchQuery(Object[] commands) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		return connection.executeBatchQuery(commands);
	}

	/**
	 * @param commands
	 * @param params
	 * @return @throws
	 *              CannotExecuteException
	 * @throws EmptyResultSetException
	 * @throws WrongReturnTypeException
	 * @throws RemoteException
	 */
	public AthenaResultSet[] executeBatchQuery(Object[] commands, Object[][] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		return connection.executeBatchQuery(commands, params);
	}

	/**
	 * @param command
	 * @return @throws
	 *              CannotExecuteException
	 * @throws EmptyResultSetException
	 * @throws WrongReturnTypeException
	 * @throws RemoteException
	 */
	public Object executeObjectQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		return connection.executeObjectQuery(command);
	}

	/**
	 * @param command
	 * @return @throws
	 *              CannotExecuteException
	 * @throws EmptyResultSetException
	 * @throws WrongReturnTypeException
	 * @throws RemoteException
	 */
	public AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		return connection.executeQuery(command);
	}

	/**
	 * @param command
	 * @param params
	 * @return @throws
	 *              CannotExecuteException
	 * @throws EmptyResultSetException
	 * @throws WrongReturnTypeException
	 * @throws RemoteException
	 */
	public AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		return connection.executeQuery(command, params);
	}

	/**
	 * @param command
	 * @param tx
	 * @return @throws
	 *              CannotUpdateException
	 * @throws RemoteException
	 */
	public Object executeUpdate(Object command, Transaction tx) throws CannotUpdateException, RemoteException {
		return connection.executeUpdate(command, tx);
	}

	/**
	 * @return @throws
	 *              RemoteException
	 * @throws Exception
	 */
	public Command getCommand() throws RemoteException, Exception {
		return connection.getCommand();
	}

	/**
	 * @return @throws
	 *              RemoteException
	 */
	public int getConnectionType() throws RemoteException {
		return connection.getConnectionType();
	}

	/**
	 * @return @throws
	 *              RemoteException
	 */
	public String getInternalConnectionType() throws RemoteException {
		return connection.getInternalConnectionType();
	}

	/**
	 * @return @throws
	 *              RemoteException
	 */
	public boolean isReleased() throws RemoteException {
		return connection.isReleased();
	}

	/**
	 * @throws RemoteException
	 */
	public void release() throws RemoteException {
		connection.release();
	}

	/**
	 * @param connectionType
	 * @throws RemoteException
	 */
	public void setConnectionType(int connectionType) throws RemoteException {
		connection.setConnectionType(connectionType);
	}

	/**
	 * @param connectionTypeName
	 * @throws RemoteException
	 */
	public void setInternalConnectionType(String connectionTypeName) throws RemoteException {
		connection.setInternalConnectionType(connectionTypeName);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return connection.toString();
	}

	/*
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return proxyID;
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
		return connection.hashCode();
	}
}