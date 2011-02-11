/*
 * AbstractRConnectionImpl.java
 * 
 * Created on March 26, 2002, 11:51 AM
 */

package org.jini.projects.athena.connection;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.transaction.Transaction;
import net.jini.id.Uuid;

import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.exception.WrongReturnTypeException;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.service.StatePersistence;
import org.jini.projects.athena.service.StatisticMonitor;
import org.jini.projects.athena.service.SystemManager;

/**
 * Base class implementing release mechanisms, connection types and
 * LeasedResource requirements
 * 
 * @author calum
 */
public abstract class AbstractRConnectionImpl implements RemoteConnection {
	
	protected String STATEFILE = null;
	protected static final int TRYINGTOCOMMIT = 1;
	protected static final int TRYINGTOABORT = 1;
	protected static final int NO_STANDING_TX_PROCESSES = 0;
	protected int index_ref;
	protected long expiry;
	protected int TxProcessState = NO_STANDING_TX_PROCESSES;
	protected Logger l = Logger.getLogger("org.jini.projects.athena.connection");
	protected int reldisp = 0;
	protected Uuid leaseCookie;
	protected StatePersistence sp = new StatePersistence();
	protected SystemConnection conn;
	protected boolean isreleased = false;
	protected boolean canrelease = true;
	protected boolean tuned = false;
	protected int conntype = 2;

	protected String conntypename = "Remote";

	protected HashMap tuning;

	/** Abstract class */
	public AbstractRConnectionImpl() {
	}

	/**
	 * Sets the connection type to a named conenction, i.e. cutsom connections
	 * 
	 * @param connectionTypeName
	 *                   The new internal connection name
	 * @exception RemoteException
	 *                         Description of Exception
	 * @since
	 */
	public void setInternalConnectionType(String connectionTypeName) throws RemoteException {
		conntypename = connectionTypeName;
	}

	/**
	 * Sets the connection type to one of the internal connection types
	 * 
	 * @param connectionType
	 *                   The new connectionType value
	 * @since
	 */
	public void setConnectionType(int connectionType) {
		conntype = connectionType;
		if (connectionType == AthenaConnection.LOCAL) {
			conntypename = "Local";
		}
		if (connectionType == AthenaConnection.DISCONNECTED) {
			conntypename = "Disconnected";
		}
		if (connectionType == AthenaConnection.REMOTE) {
			conntypename = "Remote";
		}
	}

	/**
	 * Sets the lease expiration
	 * 
	 * @param expiry
	 *                   The new expiration value
	 * @since
	 */
	public void setExpiration(long expiry) {
		this.expiry = expiry;
	}

	//    /**
	//     * Sets the lease cookie
	//     *
	//     *@param obj The new cookie value
	//     *@since
	//     */
	//    public void setCookie(Object obj) {
	//        leaseCookie = (Integer) obj;
	//    }

	/**
	 * Informs the caller as to whther the connection has been realeased and is
	 * unallocated
	 * 
	 * @return The released value
	 * @exception RemoteException
	 *                         Description of Exception
	 * @since
	 */
	public boolean isReleased() throws RemoteException {
		return isreleased;
	}

	/**
	 * Returns the connection type
	 * 
	 * @return The connectionType value
	 * @exception RemoteException
	 *                         Description of Exception
	 * @since
	 */
	public int getConnectionType() throws RemoteException {
		return conntype;
	}

	/**
	 * Returns the internal connection type name
	 * 
	 * @return The internalConnectionType value
	 * @exception RemoteException
	 *                         Description of Exception
	 * @since
	 */
	public String getInternalConnectionType() throws RemoteException {
		return conntypename;
	}

	/**
	 * Returns the lease cookie
	 * 
	 * @return The cookie value
	 * @since
	 */
	public Uuid getCookie() {
		return leaseCookie;
	}

	/**
	 * Returns the lease cookie
	 * 
	 * @return The cookie value
	 * @since
	 */
	public void setCookie(Uuid cookie) {
		leaseCookie = cookie;
	}

	
	/**
	 * Returns the currentl lease expiration value.
	 * 
	 * @return The expiration value
	 * @since
	 */
	public long getExpiration() {
		return expiry;
	}

	/**
	 * Returns the current status of the release availability. If a connection
	 * is in a transaction it will not release
	 * 
	 * @return Description of the Returned Value
	 * @since
	 */
	public boolean canRelease() throws RemoteException {
		//Checks if connection is currently within a transaction
		if (reldisp == 100) {
			l.finest("Checking release state on connection " + conn.getPersistentFileName());
			reldisp = 0;
		}
		reldisp++;
		Thread.yield();
		if (TxProcessState == NO_STANDING_TX_PROCESSES)
			return canrelease;
		else
			return false;
		 }

	/**
	 * Initiates release
	 * 
	 * @since
	 */
	public void release() throws RemoteException {
		
			l.finest("Releasing Connection");
		
		
		try {
			if (conn != null) {
				conn.release();
				conn.resetAutoAbort();
			} else {
				l.info("Connection is already NULL!!!!!!!!!!!!!!");
				l.info("\t for " + this.conn.getPersistentFileName());
			}

		} catch (Exception ex) {
			StatisticMonitor.addFailure();
			l.log(Level.SEVERE, "Error in releasing", ex);
			ex.printStackTrace();
		}

		try {
			
				l.finest("killing the lease");
			//leaseCookie may be null when a connection is restored in a
			// transaction failure scenario
			// Normal operation of allocate/operate/commit/deallocate will have
			// to occur
			//However a lease will not have been granted - normally allocated
			// i.e. client connections
			// will always have a lease
			if (this.leaseCookie != null)
				SystemManager.SYSTEMLANDLORD.killLease(this.leaseCookie);
		} catch (net.jini.core.lease.UnknownLeaseException ex) {
			
			l.finest("Lease unknown: " + this.leaseCookie);
		}

		//Reset all these connections
		conn = null;
		sp = null;
		this.tuning = null;

		StatisticMonitor.removeConnection();
		isreleased = true;
		canrelease = true;

	}

	/**
	 * Execute a batch of queries. These will be sent back as Local ResultSets
	 * for querying on the client
	 * 
	 * @param commands
	 *                   An array of commands
	 * @return Array of AthenaResultSet representing the results of each query
	 *               executed
	 * @exception EmptyResultSetException
	 *                         Description of Exception
	 * @exception CannotExecuteException
	 *                         Description of Exception
	 * @exception WrongReturnTypeException
	 *                         Description of Exception
	 * @since @throws
	 *             RemoteException Standard Network Error
	 */
	public abstract AthenaResultSet[] executeBatchQuery(Object[] commands) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

	/**
	 * Execute a batch of queries. These will be sent back as Local ResultSets
	 * for querying on the client
	 * 
	 * @param commands
	 *                   An array of commands
	 * @param params
	 *                   An array of parameters. <BR>The length of both the <CODE>
	 *                   commands</CODE> and the <CODE>parms</CODE> arrays must be
	 *                   the same length
	 * @return Array of AthenaResultSets
	 * @exception EmptyResultSetException
	 *                         thrown if the one of the queries generates an empty
	 *                         resultset
	 * @exception CannotExecuteException
	 *                         Description of Exception
	 * @exception WrongReturnTypeException
	 *                         Description of Exception
	 * @since @throws
	 *             RemoteException Standard Network Error
	 */
	public abstract AthenaResultSet[] executeBatchQuery(Object[] commands, Object[][] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

	/**
	 * Execute a single query, which returns a resultset
	 * 
	 * @param command
	 *                   Description of Parameter
	 * @return set of results
	 * @exception CannotExecuteException
	 *                         Description of Exception
	 * @exception EmptyResultSetException
	 *                         Description of Exception
	 * @exception WrongReturnTypeException
	 *                         Description of Exception
	 * @since @throws
	 *             RemoteException Standard Network Error
	 */
	public abstract AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

	/**
	 * Execute a batch of queries. These will be sent back as Local ResultSets
	 * for querying on the client
	 * 
	 * @param command
	 *                   Description of Parameter
	 * @param params
	 *                   An array of parameters. These parameters will be placed in
	 *                   order
	 * @return The resultSet representing the output from the command
	 * @exception CannotExecuteException
	 *                         Description of Exception
	 * @exception EmptyResultSetException
	 *                         Description of Exception
	 * @exception WrongReturnTypeException
	 *                         Description of Exception
	 * @since @throws
	 *             RemoteException Standard Network Error
	 */
	public abstract AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

	/**
	 * Execute data modification statements, possibly within a distributed
	 * transaction
	 * 
	 * @param command
	 *                   The object representing the command you wish to issue against
	 *                   Athena
	 * @param tx
	 *                   A Jini transaction object. <BR><EM>Note:</EM> If you call
	 *                   this method twice, the first with a transaction context, and
	 *                   the second without a transaction, the second excute will
	 *                   automatically commit both operations. Thus, an abort, either
	 *                   from the client or mahalo will have <u>no</u> effect!
	 * @return Description of the Returned Value
	 * @exception RemoteException
	 *                         Description of Exception
	 * @exception CannotUpdateException
	 *                         Description of Exception
	 * @since
	 */
	public abstract Object executeUpdate(Object command, Transaction tx) throws CannotUpdateException, RemoteException;

	/**
	 * Get the <CODE>Command</CODE> object that the SystemConnection, that
	 * Athena is representing, handles.
	 * 
	 * @return A new instance of a <CODE>Command</CODE> object
	 * @exception Exception
	 *                         Description of Exception
	 * @since @throws
	 *             RemoteException thrown if an error occurs in obtaining the <CODE>
	 *             Command</CODE> object.
	 */
	public abstract org.jini.projects.athena.command.Command getCommand() throws RemoteException, Exception;

	public abstract Object executeObjectQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

}
