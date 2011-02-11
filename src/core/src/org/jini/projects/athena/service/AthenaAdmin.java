/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 10-Jul-2002
 * Time: 09:50:27
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

package org.jini.projects.athena.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Athena specific administration interface
 * 
 * @author calum
 *  
 */
public interface AthenaAdmin extends Remote {
	/**
	 * Tell Athena to hibernate - go offline
	 */
	public void hibernate() throws RemoteException;

	/**
	 * Tell Athena to wakeup - go online
	 */
	public void wake() throws RemoteException;

	/**
	 * Obtains information about the types currently defined in Athena
	 * 
	 * @throws RemoteException
	 */
	public Map getTypeInformation() throws RemoteException;

	/**
	 * Obtains information about the handlers currently defined in Athena
	 * 
	 * @throws RemoteException
	 */
	public Map getHandlerDetails() throws RemoteException;

	/**
	 * Obtains information about the dialects currently defined in Athena
	 * 
	 * @throws RemoteException
	 */
	public Map getDialectDetails() throws RemoteException;
}