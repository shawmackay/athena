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

package org.jini.projects.athena.resultset;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for clients for exporting RMI stubs to client, where the actual
 * ResultSet Object resides inside Athena, or in the host system.
 * 
 * @author calum
 * 
 * @author calum
 *  
 */
public interface RemoteResultSet extends Remote {

	/**
	 * Gets the FieldName of the field at the given index.
	 * 
	 * @param field
	 *                  Description of Parameter
	 * @return The FieldName value
	 * @exception RemoteException
	 *                        Description of Exception
	 *  
	 */

	public String getFieldName(int field) throws RemoteException;

	/**
	 * Returns index of column 'colname'
	 * 
	 * @param colname
	 *                  String Name of column to find
	 * @return int Column index
	 * 
	 * @throws RemoteException
	 *                   wraps all exceptions from implementing classes
	 */
	public int findColumn(String colname) throws RemoteException;

	/**
	 * Gets the ColumnCount attribute of the AthenaResultSet object
	 * 
	 * @return The ColumnCount value
	 * @exception RemoteException
	 *                        Description of Exception
	 *  
	 */

	public int getColumnCount() throws RemoteException;

	/**
	 * Returns concurrency model generally, READCOMMITTED or READSERIALIZABLE or
	 * NONE
	 * 
	 * @return Integer
	 * 
	 * @throws RemoteException
	 *                   wraps all exceptions from implementing classes
	 */
	public Integer getConcurrency() throws RemoteException;

	/**
	 * Gets the CursorType attribute of the RemoteResultSet object
	 * 
	 * @return The CursorType value
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public Integer getCursorType() throws RemoteException;

	/**
	 * Closes the resultset
	 * 
	 * @since @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public void close() throws RemoteException;

	/**
	 * Obtains a field for a given name
	 * 
	 * @param name
	 *                  String representing a 'column' name
	 * @return the value of the given field
	 * @since @throws
	 *             RemoteException wraps all exceptions
	 */
	public Object getField(String name) throws RemoteException;

	/**
	 * Obtains a field for a given column index
	 * 
	 * @param columnIndex
	 *                  int
	 * @return the value of the given field
	 * @since @throws
	 *             RemoteException wraps all exceptions
	 */
	public Object getField(int columnIndex) throws RemoteException;

	/**
	 * Moves the record pointer to the next record in the set
	 * 
	 * @return boolean success value
	 * @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public boolean next() throws RemoteException;

	/**
	 * Moves the record pointer to the PREVIOUS record in the set
	 * 
	 * @return boolean success value
	 * @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public boolean previous() throws RemoteException;

	/**
	 * Moves the record pointer to the first record in the set
	 * 
	 * @return boolean success value
	 * @since @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public boolean first() throws RemoteException;

	/**
	 * Moves the record pointer to the last record in the set
	 * 
	 * @return boolean success value
	 * @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public boolean last() throws RemoteException;

	/**
	 * Moves you to record <CODE>pos</CODE>. If pos is positive, the record
	 * pointer is placed on the record <CODE>0+pos</CODE>, if the value is
	 * negative the record pointer is placed on record <CODE>
	 * &gt;setlength&lt;-pos</CODE>;
	 * 
	 * @param pos
	 *                  record number you wish to move to
	 * @return Integer
	 *  @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public Integer moveAbsolute(int pos) throws RemoteException;

	/**
	 * Refreshes the row to flush any updates into it. <BR>
	 * <B>NOTE: </B> Successful implementation of this method is optional.For
	 * JDBC, Some JDBC2.0 drivers support this others do not. <BR>
	 * In the case of other resources the amount of overhead may prove
	 * implementation to be too inefficient. In these cases it is advised that
	 * refreshRow does not yield new information
	 * 
	 *  @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public void refreshRow() throws RemoteException;

	/**
	 * Updates the Object at column <CODE>columnindex</CODE> to value <CODE>
	 * obj</CODE>. <B>NOTE: </B> Successful implementation of this method is
	 * optional. Some JDBC2.0 drivers support this others do not. <BR>
	 * In the case of other resources the amount of overhead may prove
	 * implementation to be too inefficient. In these cases it is advised that
	 * updateObject throws an <CODE>RemoteException <CODE>
	 * 
	 * 
	 * 
	 * @param columnindex
	 *                  Value representing index of field within record
	 * @param obj
	 *                  New value for field
	 * @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public void updateObject(int columnindex, Object obj) throws RemoteException;

	/**
	 * Flushes updates in row to ResultSet the single row that the resultset
	 * record pointer is looking at. <B>NOTE: </B> Successful implementation of
	 * this method is optional. Some JDBC2.0 drivers support this others do not.
	 * <BR>
	 * In the case of other resources the amount of overhead may prove
	 * implementation to be too inefficient. In these cases it is advised that
	 * <CODE>updateRow</CODE> throws an <CODE>RemoteException <CODE>
	 * 
	 * 
	 * 
	 * @throws
	 *             RemoteException wraps all exceptions from implementing classes
	 */
	public void updateRow() throws RemoteException;
	/**
	 * Obtains the total number of rows in the resultset 
	 * @return number of rows
	 * @throws RemoteException
	 */
	public long getRowCount() throws RemoteException;
}

