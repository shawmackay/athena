/**
 *  Title: <p>
 *
 *  Description: <p>
 *
 *  Copyright: Copyright (c) <p>
 *
 *  Company: <p>
 *
 *  @author
 *
 *@version 0.9community */
package org.jini.projects.athena.connection;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.transaction.Transaction;

import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.exception.WrongReturnTypeException;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 *  Remote Interface representing the client side interaction with a Connection
 *  Object inside Athena
 *
 *@author     calum
 *     09 October 2001
 */
public interface RemoteConnection extends Remote {
    /**
     *  <CODE>LocalResultSet</CODE>s are requested by Client operations. This
     *  indicates that ResultSets generated through this Connection, will be stored
     *  and manipualted through the Client Proxy
     *
     *@since
     */
    public final static int LOCAL = 0;
    /**
     *  <CODE>DisconnectedResultSet</CODE>s are requested by Client operations.
     *  This indicates that ResultSets generated through this Connection, will be
     *  stored and manipulated in the Athena Service VM, but will not keep a
     *  dedicated Raw connection to the datasource
     *
     *@since
     */
    public final static int DISCONNECTED = 1;
    /**
     *  <CODE>RemoteResultSet</CODE>s are requested by Client operations. This
     *  indicated that ResultSets generated through this Connection, will be stored
     *  and manipulated in the Athena Service VM, but will keep a dedicated Raw
     *  connection to the datasource, and thus all operations will be fed through
     *  to the host datasource.
     *
     *@since
     */
    public final static int REMOTE = 2;


    /**
     *  Get the <CODE>Command</CODE> object that the SystemConnection, that Athena
     *  is representing, handles.
     *
     *@return                   A new instance of a <CODE>Command</CODE> object
     *@exception  Exception     Description of Exception
     *@since
     *@throws  RemoteException  thrown if an error occurs in obtaining the <CODE>Command</CODE>
     *      object.
     */

    public org.jini.projects.athena.command.Command getCommand() throws RemoteException, Exception;


    /**
     *  Sets the ConnectionType to the type of ResultSets you want generated from
     *  opertaions invoked during this session
     *
     *@param  connectionType       Currently either <CODE>LOCAL</CODE>, <CODE>DISCONNECTED</CODE>
     *      , or <CODE>REMOTE</CODE>
     *@exception  RemoteException  thrown if an error occurs
     *@since
     */
    public void setConnectionType(int connectionType) throws RemoteException;


    /**
     *  Sets the ConnectionType to a type outside of the three generic types of
     *  local, disconnected and remote. This type must be configured in the config
     *  files for this instance of Athena, and the class must be resolvable at
     *  run-time, and it must implement either <CODE>AthenaResultSet</CODE> or
     *  <CODE>RemoteResultSet</CODE>. <I>Note: At the moment, this has been
     *  disabled</I>
     *
     *@param  connectionTypeName   String representing an internal AthenaResultSet
     *      value.
     *@exception  RemoteException  thrown if an error occurs or the class cannot be
     *      loaded
     *@since
     */
    public void setInternalConnectionType(String connectionTypeName) throws RemoteException;


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
     *@return                      The ConnectionType value
     *@exception  RemoteException  thrown if an error occurs during RMI
     *@since
     */
    public int getConnectionType() throws RemoteException;


    /**
     *  Gets the InternalConnectionType giving the name representing the custom
     *  AthenaResultSet or RemoteResultSet class that ResultSets will be built as.
     *
     *@return                      The InternalConnectionType value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public String getInternalConnectionType() throws RemoteException;


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
     *@exception  RemoteException        Description of Exception
     *@exception  CannotUpdateException  Description of Exception
     *@since
     */
    public Object executeUpdate(Object command, Transaction tx) throws CannotUpdateException, RemoteException;


    /**
     *  Execute a single query, which returns a resultset
     *
     *@param  command                       Description of Parameter
     *@return                               set of results
     *@exception  CannotExecuteException    Description of Exception
     *@exception  EmptyResultSetException   Description of Exception
     *@exception  WrongReturnTypeException  Description of Exception
     *@since
     *@throws  RemoteException              Standard Network Error
     */
    public AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;

    /**
     *  Execute a single query, which returns an arbitrary object
     *
     *@param  command                       Description of Parameter
     *@return                               Object retruned from Host call
     *@exception  CannotExecuteException    Description of Exception
     *@exception  EmptyResultSetException   Description of Exception
     *@exception  WrongReturnTypeException  Description of Exception
     *@since
     *@throws  RemoteException              Standard Network Error
     */
    public Object executeObjectQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  command                       Description of Parameter
     *@param  params                        An array of parameters. These
     *      parameters will be placed in order
     *@return                               The resultSet representing the output
     *      from the command
     *@exception  CannotExecuteException    Description of Exception
     *@exception  EmptyResultSetException   Description of Exception
     *@exception  WrongReturnTypeException  Description of Exception
     *@since
     *@throws  RemoteException              Standard Network Error
     */

    public AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  commands                      An array of commands
     *@return                               Array of AthenaResultSet representing
     *      the results of each query executed
     *@exception  EmptyResultSetException   Description of Exception
     *@exception  CannotExecuteException    Description of Exception
     *@exception  WrongReturnTypeException  Description of Exception
     *@since
     *@throws  RemoteException              Standard Network Error
     */

    public AthenaResultSet[] executeBatchQuery(Object[] commands) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;


    /**
     *  Execute a batch of queries. These will be sent back as Local ResultSets for
     *  querying on the client
     *
     *@param  commands                      An array of commands
     *@param  params                        An array of parameters.<BR>
     *      The length of both the <CODE>commands</CODE> and the <CODE>parms</CODE>
     *      arrays must be the same length
     *@return                               Array of AthenaResultSets
     *@exception  EmptyResultSetException   thrown if the one of the queries
     *      generates an empty resultset
     *@exception  CannotExecuteException    Description of Exception
     *@exception  WrongReturnTypeException  Description of Exception
     *@since
     *@throws  RemoteException              Standard Network Error
     */

    public AthenaResultSet[] executeBatchQuery(Object[] commands, Object[][] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException;


    /**
     *  Allows querying of Release state of this class. Releasing a connection also
     *  releases state associated with that transaction if that stae has not
     *  already been released Therefore, transactions may be still waiting to
     *  complete, even after the client has finished operations on this participant
     *  Querying this allows the client to wait prior to releasing the connection
     *  back to the p
     *
     *@return                      Release availability
     *@exception  RemoteException  Description of Exception
     *@since
     */

    public boolean canRelease() throws RemoteException;

    /**
     *  Allows querying of Release state of this class. Releasing a connection also
     *  releases state associated with that transaction if that stae has not
     *  already been released Therefore, transactions may be still waiting to
     *  complete, even after the client has finished operations on this participant
     *  Querying this allows the client to wait prior to releasing the connection
     *  back to the p
     *
     *@return                      Release availability
     *@exception  RemoteException  Description of Exception
     *@since
     */


    public boolean canRelease(boolean block) throws RemoteException;

    /**
     *  Releases the connection and performs some clean up
     *
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public void release() throws RemoteException;


    /**
     *  Allows the controlling PoolThread instance for this object to finish once
     *  all cleanup operations have been done
     *
     *@return                   Release status
     *@since
     *@throws  RemoteException  Standard Network Error
     */
    public boolean isReleased() throws RemoteException;

}

