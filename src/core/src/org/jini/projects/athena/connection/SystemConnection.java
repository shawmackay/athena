/*
 *  SystemConnection.java
 *
 *  Created on 02 August 2001, 15:34
 */
package org.jini.projects.athena.connection;

import org.jini.projects.athena.exception.AthenaException;


/**
 *  Interface for a backward facing connection (i.e. a connection to a host datasource)
 *  @author calum
 *
 */
public interface SystemConnection {

    /**
     *  Gets the filename to which the connection wil store it's txn recovery information
     *
     *@return    The persistentFileName value
     *@since
     */
    public String getPersistentFileName();

    
    /**
     *  Signals to the host system to prepare any changes to permanent store.
     *  This is used in host systems that use two phase commit 
     *@return                whether that host could commit the changes
     *@exception  Exception  Description of Exception
     *@since
     */
    public boolean prepare() throws Exception;
    
    /**
     *  Signals to the host system to commit any changes to permanent store.
     *
     *@return                whether that host could commit the changes
     *@exception  Exception  Description of Exception
     *@since
     */
    public boolean commit()
            throws Exception;


    /**
     * Returns an ErrorHandler, that the system will delegate error processing to.
     * @return a Top_level error handler
     */
    public HostErrorHandler getErrorHandler();

    /**
     *  Signals to the host system to rollback any changes that may have affected
     *  the permanent store
     *
     *@return                whether that host could rollback the changes
     *@exception  Exception  Description of Exception
     *@since
     */
    public boolean rollback()
            throws Exception;


    /**
     *  Issues a command against the host system
     *
     *@param  command        Description of Parameter
     *@return                Description of the Returned Value
     *@exception  Exception  Description of Exception
     *@since
     */
    public Object issueCommand(Object command)
            throws Exception;


    /**
     *  Issues a parameterized command against the host system. <BR>
     *  If you are using <CODE>AthenaConnection.getCommand()</CODE> you will not
     *  need this method as parameter support is already built into the <CODE>Command</CODE>
     *  object
     *
     *@param  command        Description of Parameter
     *@param  params         Description of Parameter
     *@return                Description of the Returned Value
     *@exception  Exception  Description of Exception
     *@since
     */
    public Object issueCommand(Object command, Object[] params)
            throws Exception;


    /**
     *  If the connection is freeable, i.e. the physical host connection can be
     *  redefined at run-time, by clients, this method will allow you to connect to
     *  a different system
     *
     *@param  connectionprops      Description of Parameter
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void connectTo(java.util.Properties connectionprops) throws AthenaException;


    /**
     *  Will reconnect to the host as defined by the configuration that Athena used
     *  when starting up
     *
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void reConnect() throws AthenaException;


    /**
     *  get The allocation status of this connection
     *
     *@return    The Allocated valuetrue, the connection is allocated; false, the
     *      connection is available for allocation to a client
     *@since
     */
    public boolean isAllocated();


    /**
     *  Get the value of whether or not the connection can be freed and it's
     *  connection redefinable.
     *
     *@return    Description of the Returned Valuetrue or false
     *@since
     */
    public boolean canFree();


    /**
     *  Mark this connection as allocated
     *
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void allocate() throws AthenaException;


    /**
     *  Sets the TransactionFlag attribute of this Connection
     *
     *@param  ID                 The new TransactionFlag value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void setTransactionID(Object ID) throws AthenaException;


    
    public void clearTransactionFlag() throws AthenaException;
    /**
     *  Gets the Transaction flag
     *
     *@return                      Indicator of whether this connection is
     *      currently in a distributed transaction
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public boolean inTransaction() throws AthenaException;


    /**
     *  Gets the Connected status
     *
     *@return    Indicator of whether this Connection is connected to the host
     *      system, and thus taking up resources on that host system
     *@since
     */
    public boolean isConnected();


    /**
     *  Releases, de-allocates, this connection. <BR>
     *  <EM>Note:</EM> this should only be called once the connection does not have
     *  a transaction associated with it. To do this use the following code: <PRE>
     *        ....
     *        ....operations....
     *        while (!myconnection.canRelease()){
     *            try{
     *                Thread.wait(50);
     *            } catch (Exception ex) { }
     *        }
     *        myconnection.release();
     * </PRE>
     *
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void release() throws AthenaException;


    /**
     *  Closes the connection to the hosts system and cleans up any associated
     *  resources
     *
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void close() throws AthenaException;


    /**
     *  Gets the AutoAbort flag
     *
     *@return                      Indicator of whether there has been a failure in
     *      issuing a command and thus Athena should return the <CODE>ABORT</CODE>
     *      constant during the <CODE>prepare()</CODE> call from the transation
     *      manager.
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public boolean isAutoAbortSet() throws AthenaException;


    /**
     *  Sets the Reference attribute, indicating what number in the pool this
     *  connection is
     *
     *@param  ref  The new Reference value
     *@since
     */
    public void setReference(int ref);


    /**
     *  resets the autoabort flag
     *
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public void resetAutoAbort() throws AthenaException;


    /**
     *  Gets the SystemCommand object that is returned to the client in <CODE>AthenaConnection.getCommand()</CODE>
     *
     *@return                      The SystemCommand value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public org.jini.projects.athena.command.Command getSystemCommand() throws AthenaException;

    /**
     * Turns on the autoabort flag
     *
     */
    public void setAutoAbort();

    /**
     * A host connection has to be able to translate some objects into their representation.
     * Either the object will be transformed, or expanded, or the same object will be returned
     * @param in
     * @return translated or expanded representation of an object
     * @throws AthenaException
     */
    public Object handleType(Object in) throws AthenaException;
}

