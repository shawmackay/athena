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

import org.jini.projects.athena.exception.AthenaException;

/**
 *  All resultsets regardless of the ultimate source must be mapped to this
 *  interface. Although by now means complete, a stripped down version of <CODE>
 *  java.sql.ResultSet</CODE> allows for forms of data, as well as RDMBS, to be
 *  used as datasources. @author calum
 *
 *@author     calum
 *     09 October 2001
 */
public interface AthenaResultSet {
    /**
     *  Obtains a field for a given name
     *
     *@param  name              String representing a 'column' name
     *@return                   the value of the given field
     *@since
     *@throws  AthenaException  wraps all exceptions
     */
    public Object getField(String name) throws AthenaException;


    /**
     *  Returns concurrency model generally, READCOMMITTED or READSERIALIZABLE or
     *  NONE
     *
     *@return                   Integer
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public Integer getConcurrency() throws AthenaException;


    /**
     *  Moves you to record <CODE>pos</CODE> . If pos is positive, the record
     *  pointer is placed on the record <CODE>0+pos</CODE> , if the value is
     *  negative the record pointer is placed on record <CODE>&gt;setlength&lt;-pos
     *  </CODE>;
     *
     *@param  pos               record number you wish to move to
     *@return                   Integer
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public Integer moveAbsolute(int pos) throws AthenaException;


    /**
     *  Closes the resultset
     *
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void close() throws AthenaException;


    /**
     *  Returns index of column 'colname'
     *
     *@param  colname           String Name of column to find
     *@return                   int Column index
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public int findColumn(String colname) throws AthenaException;


    /**
     *  Obtains a field for a given column index
     *
     *@param  columnIndex       int
     *@return                   the value of the given field
     *@since
     *@throws  AthenaException  wraps all exceptions
     */
    public Object getField(int columnIndex) throws AthenaException;


    /**
     *  Moves the record pointer to the next record in the set
     *
     *@return                   boolean success value
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean next() throws AthenaException;


    /**
     *  Moves the record pointer to the PREVIOUS record in the set
     *
     *@return                   boolean success value
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean previous() throws AthenaException;


    /**
     *  Moves the record pointer to the first record in the set
     *
     *@return                   boolean success value
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean first() throws AthenaException;


    /**
     *  Moves the record pointer to the last record in the set
     *
     *@return                   boolean success value
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public boolean last() throws AthenaException;


    /**
     *  Refreshes the row to flush any updates into it. <BR>
     *  <B>NOTE:</B> Successful implementation of this method is optional.For JDBC,
     *  Some JDBC2.0 drivers support this others do not. <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  refreshRow does not yield new information
     *
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void refreshRow() throws AthenaException;


    /**
     *  Updates the Object at column <CODE>columnindex</CODE> to value <CODE>obj
     *  </CODE>. <B>NOTE:</B> Successful implementation of this method is optional.
     *  Some JDBC2.0 drivers support this others do not. <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  updateObject throws an <CODE>AthenaException <CODE>
     *
     *
     *
     *@param  columnindex       Value representing index of field within record
     *@param  obj               New value for field
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void updateObject(int columnindex, Object obj) throws AthenaException;


    /**
     *  Flushes updates in row to ResultSet the single row that the resultset
     *  record pointer is looking at. <B>NOTE:</B> Successful implementation of
     *  this method is optional. Some JDBC2.0 drivers support this others do not.
     *  <BR>
     *  In the case of other resources the amount of overhead may prove
     *  implementation to be too inefficient. In these cases it is advised that
     *  <CODE>updateRow</CODE> throws an <CODE>AthenaException <CODE>
     *
     *
     *
     *@since
     *@throws  AthenaException  wraps all exceptions from implementing classes
     */
    public void updateRow() throws AthenaException;


    /**
     *  Gets the ColumnCount attribute of the AthenaResultSet object
     *
     *@return                      The ColumnCount value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public int getColumnCount() throws AthenaException;


    /**
     *  Gets the FieldName of the field at the given index.
     *
     *@param  field                Description of Parameter
     *@return                      The FieldName value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public String getFieldName(int field) throws AthenaException;

    public long getRowCount() throws AthenaException;
}

