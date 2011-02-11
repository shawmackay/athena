/**
 *  Title: <p>
 *
 *  Description: <p>
 *
 *  Copyright: Copyright (c) <p>
 *
 *  Company: <p>
 *
 *  @author calum
 *
 *@version 0.9community */
package org.jini.projects.athena.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.admin.Administrable;
import net.jini.core.lease.LeaseDeniedException;

import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.exception.HostException;

/**
 *  Client facing interface for interacting with the AthenaRegistration object
 *  and for obtaining Connections @author calum
 *
 *@author     calum

 */
public interface AthenaRegistration extends Remote, Administrable {
    /**
     *  Obtains an instance of AthenaConnection and exports it to the client
     *
     *@param  duration                  Description of Parameter
     *@return                           AthenaConnection
     *@exception  LeaseDeniedException  Description of Exception
     *     *@throws  RemoteException          if a connection cannot be assigned
     */
    public AthenaConnection getConnection(long duration) throws RemoteException, LeaseDeniedException, HostException;


    /**
     *  Obtains an instance of AthenaConnection and exports it to the client and
     *  attempts to link it to given transaction ID Currently unimplemented
     *
     *@param  asUser                    The User name to display and exeucte under
     *@param  duration                  Requested duration of the lease
     *@return                           AthenaConnection
     *@exception  LeaseDeniedException  if the Lease cannot be given to the client
     *@since
     *@throws  RemoteException          if a connection cannot be assigned
     */
    public AthenaConnection getConnection(String asUser, long duration) throws RemoteException, LeaseDeniedException, HostException;


    /**
     *  Obtains the load that the service is under i.e. number of allocated
     *  connections
     *
     *@return                   int load
     *@since
     *@throws  RemoteException  if an RMI error occurs
     */
    public int getLoad() throws RemoteException;


    /**
     *  Gets the runtime Statistics for the current Athena instance
     *
     *@return                      The Statistics value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public java.util.Vector getStatistics() throws RemoteException;

    /**
     *  Gets the runtime Statistics for the current Athena instance
     *
     *@return                      The Statistics value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public java.util.HashMap getResourceDetails() throws RemoteException;


    /**
     *  Gets the system configuration for the current Athena instance
     *
     *@return                      The system configuration properties
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public java.util.Properties getSystemConfig() throws RemoteException;


    public java.lang.Object getAdmin() throws java.rmi.RemoteException;

}

