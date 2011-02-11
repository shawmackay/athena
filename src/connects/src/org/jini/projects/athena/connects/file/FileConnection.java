/*
 * athena.support.jini.org : org.jini.projects.athena.connects.file
 * 
 * 
 * FileConnection.java Created on 02-Nov-2004
 * 
 * FileConnection
 *  
 */

package org.jini.projects.athena.connects.file;

import java.util.Properties;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.AthenaException;

/**
 * @author calum
 */
public class FileConnection implements SystemConnection {
	public String persistentFile = null;

	private boolean allocated = false;
	private boolean connected = false;
	private boolean canBeFreed = false;
	private boolean inTxn = false;
	private boolean autoAbort = false;

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#getPersistentFileName()
	 */
	public String getPersistentFileName() {
		// TODO Complete method stub for getPersistentFileName
		return persistentFile;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#commit()
	 */
	public boolean commit() throws Exception {
		// TODO Complete method stub for commit
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#getErrorHandler()
	 */
	public HostErrorHandler getErrorHandler() {
		// TODO Complete method stub for getErrorHandler
		return null;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#rollback()
	 */
	public boolean rollback() throws Exception {
		// TODO Complete method stub for rollback
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#issueCommand(java.lang.Object)
	 */
	public Object issueCommand(Object command) throws Exception {
		// TODO Complete method stub for issueCommand
		
		return null;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#issueCommand(java.lang.Object,
	 *           java.lang.Object[])
	 */
	public Object issueCommand(Object command, Object[] params) throws Exception {
		// TODO Complete method stub for issueCommand
		return null;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#connectTo(java.util.Properties)
	 */
	public void connectTo(Properties connectionprops) throws AthenaException {
		// TODO Complete method stub for connectTo

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#reConnect()
	 */
	public void reConnect() throws AthenaException {
		// TODO Complete method stub for reConnect

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#isAllocated()
	 */
	public boolean isAllocated() {
		// TODO Complete method stub for isAllocated
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#canFree()
	 */
	public boolean canFree() {
		// TODO Complete method stub for canFree
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#allocate()
	 */
	public void allocate() throws AthenaException {
		// TODO Complete method stub for allocate

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#setTransactionFlag(boolean)
	 */
	public void setTransactionID(Object ID) throws AthenaException {
		// TODO Complete method stub for setTransactionFlag

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#inTransaction()
	 */
	public boolean inTransaction() throws AthenaException {
		// TODO Complete method stub for inTransaction
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#isConnected()
	 */
	public boolean isConnected() {
		// TODO Complete method stub for isConnected
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#release()
	 */
	public void release() throws AthenaException {
		// TODO Complete method stub for release

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#close()
	 */
	public void close() throws AthenaException {
		// TODO Complete method stub for close

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#isAutoAbortSet()
	 */
	public boolean isAutoAbortSet() throws AthenaException {
		// TODO Complete method stub for isAutoAbortSet
		return false;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#setReference(int)
	 */
	public void setReference(int ref) {
		// TODO Complete method stub for setReference

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#resetAutoAbort()
	 */
	public void resetAutoAbort() throws AthenaException {
		// TODO Complete method stub for resetAutoAbort

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#getSystemCommand()
	 */
	public Command getSystemCommand() throws AthenaException {
		// TODO Complete method stub for getSystemCommand
		return null;
	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#setAutoAbort()
	 */
	public void setAutoAbort() {
		// TODO Complete method stub for setAutoAbort

	}

	/*
	 * @see org.jini.projects.athena.connection.SystemConnection#handleType(java.lang.Object)
	 */
	public Object handleType(Object in) throws AthenaException {
		// TODO Complete method stub for handleType
		return null;
	}

	public boolean prepare() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearTransactionFlag() throws AthenaException {
		// TODO Auto-generated method stub
		
	}

}
