/*
 *  AthenaConnectionImpl.java
 *
 *  Created on 09 November 2001, 10:48
 */
package org.jini.projects.athena.connection;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;

import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 *  RMI smart proxy for clients. Holds the actual remote connection to Athena and the lease required to maintain interest in the resource
 *@author     calum
 *
 */
public class AthenaConnectionImpl implements AthenaConnection {
    static final long serialVersionUID = -805173904055121314L;


    RemoteConnection rconn;
    private Lease lease;
    Class commClass;

    public boolean canRelease(boolean block) throws Exception {
        return rconn.canRelease(block);
    }

    /**
     *  Creates new AthenaConnectionImpl
     *
     *@param  rconn  The RMI object in org.jini.projects.athena
     *@param  lease  lease granted by org.jini.projects.athena
     *@since
     */
    public AthenaConnectionImpl(RemoteConnection rconn, Lease lease) {
        this.lease = lease;
        this.rconn = rconn;
    }

    /**
     * Sets the connection. Used in pooling to avoid object creation
     */
    public void setConnection(RemoteConnection rconn) {
        this.rconn = rconn;
    }

    /**
     * Sets a new Lease. Used in pooling to avoid object creation
     */
    public void setLease(Lease lease) {
        this.lease = lease;
    }


    /**
     *  Sets the ConnectionType to the type of ResultSets you want generated from
     *  opertaions invoked during this session
     *
     *@param  connectionType  Currently either <CODE>LOCAL</CODE>, <CODE>DISCONNECTED</CODE>
     *      , or <CODE>REMOTE</CODE>
     *@exception  Exception   Description of Exception
     *@since
     */
    public void setConnectionType(int connectionType) throws Exception {
        rconn.setConnectionType(connectionType);
    }


    /**
     *  Sets the ConnectionType to a type outside of the three generic types of
     *  local, disconnected and remote. This type must be configured in the config
     *  files for this instance of Athena, and the class must be resolvable at
     *  run-time, and it must implement either <CODE>AthenaResultSet</CODE> or
     *  <CODE>RemoteResultSet</CODE>. <I>Note: At the moment, this has been
     *  disabled</I>
     *
     *@param  connectionTypeName  String representing an internal AthenaResultSet
     *      value.
     *@exception  Exception       Description of Exception
     *@since
     */
    public void setInternalConnectionType(String connectionTypeName) throws Exception {
        rconn.setInternalConnectionType(connectionTypeName);
    }


    /**
     *  Get the <CODE>Command</CODE> object that the SystemConnection, that Athena
     *  is representing, handles.
     *
     *@return                   A new instance of a <CODE>Command</CODE> object
     *@exception  Exception     Description of Exception
     *@since
     *@throws  java.rmi.RemoteException  thrown if an error occurs in obtaining the <CODE>Command</CODE>
     *      object.
     */
    public org.jini.projects.athena.command.Command getCommand() throws Exception {
        if (this.commClass == null) {
            org.jini.projects.athena.command.Command comm = rconn.getCommand();
            commClass = comm.getClass();
        }
        return (org.jini.projects.athena.command.Command) commClass.newInstance();
    }


    /**
     *  Returns a value indicating the ResultSet Type configured for this session.
     *  Will be a value of:
     *  <ul>
     *    <li> 0 - LOCAL</li>
     *    <li> 1 - DISCONNECTED</li>
     *    <li> 2 - REMOTE</li>
     *    <li> -1 - CUSTOM</li>
     *  </ul>
     *
     *
     *@return                The ConnectionType value
     *@exception  Exception  Description of Exception
     *@since
     */
    public int getConnectionType() throws Exception {
        return rconn.getConnectionType();
    }


    /**
     *  Allows the controlling PoolThread instance for this object to finish once
     *  all cleanup operations have been done
     *
     *@return                   Release status
     *@exception  Exception     Description of Exception
     *@since
     *@throws  java.rmi.RemoteException  Standard Network Error
     */
    public boolean isReleased() throws Exception {
        return rconn.isReleased();
    }


    /**
     *  Gets the InternalConnectionType giving the name representing the custom
     *  AthenaResultSet or RemoteResultSet class that ResultSets will be built as.
     *
     *@return                The InternalConnectionType value
     *@exception  Exception  Description of Exception
     *@since
     */
    public String getInternalConnectionType() throws Exception {
        return rconn.getInternalConnectionType();
    }


    /**
     *  Gets the lease attribute of the AthenaConnectionImpl object
     *
     *@return    The lease value
     *@since
     */
    public net.jini.core.lease.Lease getLease() {
        return lease;
    }


    /**
     *  Allows querying of Release state of this class. Releasing a connection also
     *  releases state associated with that transaction if that stae has not
     *  already been released Therefore, transactions may be still waiting to
     *  complete, even after the client has finished operations on this participant
     *  Querying this allows the client to wait prior to releasing the connection
     *  back to the p
     *
     *@return                Release availability
     *@exception  Exception  Description of Exception
     *@since
     */
    public boolean canRelease() throws Exception {
        return rconn.canRelease();
    }


    /**
     *  Execute a single query, which returns a resultset
     *
     *@param  command                      Description of Parameter
     *@return                              set of results
     *@exception  CannotExecuteException   Description of Exception
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  Exception                Description of Exception
     *@since
     *@throws  Exception             Standard Network Error
     */
    public AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, Exception {
        return rconn.executeQuery(command);
    }


    /**
     *  Execute data modification statements, possibly within a distributed
     *  transaction
     *
     *@param  command                    The object representing the command you
     *      wish to issue against Athena
     *@param  tx                         A Jini transaction object. <BR>
     *      <EM>Note:</EM> If you call this method twice, the first with a
     *      transaction context, and the second without a transaction, the second
     *      excute will automatically commit both operations. Thus, an abort,
     *      either from the client or mahalo will have <u>no</u> effect!
     *@return                            Description of the Returned Value
     *@exception  CannotUpdateException  Description of Exception
     *@exception  Exception              Description of Exception
     *@since
     */
    public Object executeUpdate(Object command, Transaction tx) throws CannotUpdateException, Exception {
        return rconn.executeUpdate(command, tx);
    }


    /**
     *  Releases the connection and performs some clean up
     *
     *@exception  Exception  Description of Exception
     *@since
     */
    public void release() throws Exception {
        rconn.release();
    }


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  command                      Description of Parameter
     *@param  params                       An array of parameters. These parameters
     *      will be placed in order
     *@return                              The resultSet representing the output
     *      from the command
     *@exception  CannotExecuteException   Description of Exception
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  Exception                Description of Exception
     *@since
     *@throws  java.rmi.RemoteException             Standard Network Error
     */
    public AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, Exception {
        return rconn.executeQuery(command, params);
    }


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  commands                     An array of commands
     *@return                              Array of AthenaResultSet representing
     *      the results of each query executed
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  CannotExecuteException   Description of Exception
     *@exception  Exception                Description of Exception
     *@since
     *@throws  java.rmi.RemoteException             Standard Network Error
     */
    public AthenaResultSet[] executeBatchQuery(Object[] commands) throws CannotExecuteException, EmptyResultSetException, Exception {
        return rconn.executeBatchQuery(commands);
    }


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  commands                     An array of commands
     *@param  params                       An array of parameters.<BR>
     *      The length of both the <CODE>commands</CODE> and the <CODE>parms</CODE>
     *      arrays must be the same length
     *@return                              Array of AthenaResultSets
     *@exception  EmptyResultSetException  thrown if the one of the queries
     *      generates an empty resultset
     *@exception  CannotExecuteException   Description of Exception
     *@exception  Exception                Description of Exception
     *     *@throws  java.rmi.RemoteException             Standard Network Error
     */
    public AthenaResultSet[] executeBatchQuery(Object[] commands, Object[][] params) throws CannotExecuteException, EmptyResultSetException, Exception {
        return rconn.executeBatchQuery(commands, params);
    }

    /**
     * @see org.jini.projects.athena.connection.AthenaConnection#executeObjectQuery(Object)
     */
    public Object executeObjectQuery(Object command)
            throws CannotExecuteException, EmptyResultSetException, Exception {
        return rconn.executeObjectQuery(command);
    }

}

