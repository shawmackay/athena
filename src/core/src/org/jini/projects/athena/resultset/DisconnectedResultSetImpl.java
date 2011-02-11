/*
 *  DisconnectedResultSetImpl.java
 *
 *  Created on 13 August 2001, 13:40
 */
package org.jini.projects.athena.resultset;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  Concrete class implementing RemoteResultSet, this class holds all resource
 *  in Athena, but disconnects from the Host system
 *
 *@author     calum

 *@version 0.9community */
public class DisconnectedResultSetImpl implements RemoteResultSet {
    static final long serialVersionUID = 1550962129947585590L;
    private ArrayList record = null;
    private ArrayList table = null;
    private HashMap columndetails = null;
    private int currRow = 0;
    private RemoteResultSet rrs = null;
    private final boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null;


    /**
     *  Creates new DisconnectedResultSetImpl
     *
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public DisconnectedResultSetImpl() throws RemoteException {
        record = new ArrayList();
        table = new ArrayList();
    }


    /**
     *  Constructor for the DisconnectedResultSetImpl object
     *
     *@param  table                Description of Parameter
     *@param  columndetails        Description of Parameter
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public DisconnectedResultSetImpl(ArrayList table, HashMap columndetails) throws RemoteException {
        setHeader(columndetails);
        setData(table);
        try {
            record = (ArrayList) table.get(0);
            System.out.println("Disc: First rec: " + record);
        } catch (Exception ex) {
        }
    }


    /**
     *  Gets the field attribute of the DisconnectedResultSetImpl object
     *
     *@param  name                 Description of Parameter
     *@return                      The field value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Object getField(String name) throws RemoteException {
        if (record != null) {
            String tname = name.toLowerCase();
            int idx = ((Integer) columndetails.get(tname)).intValue();
            return record.get(idx);
        }
        return null;
    }


    /**
     *  Gets the cursorType attribute of the DisconnectedResultSetImpl object
     *
     *@return                      The cursorType value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Integer getCursorType() throws RemoteException {
        return new Integer(java.sql.ResultSet.FETCH_UNKNOWN);
    }


    /**
     *  Gets the concurrency attribute of the DisconnectedResultSetImpl object
     *
     *@return                      The concurrency value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Integer getConcurrency() throws RemoteException {
        return new Integer(java.sql.ResultSet.CONCUR_READ_ONLY);
    }


    /*
	 *  Gets the FieldName attribute of the DisconnectedResultSetImpl object
	 *
	 *  @param  field                Description of Parameter
	 *  @return                      The FieldName value
	 *  @exception  RemoteException  Description of Exception
	 */
    /**
     *  Gets the fieldName attribute of the DisconnectedResultSetImpl object
     *
     *@param  field                Description of Parameter
     *@return                      The fieldName value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public String getFieldName(int field) throws RemoteException {
        if (columndetails.containsValue(new Integer(field))) {
            Set entries = columndetails.entrySet();
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                Object x = iter.next();
                Map.Entry entr = (Map.Entry) x;
                Integer value = (Integer) entr.getValue();
                if (value.intValue() == field) {
                    return (String) entr.getKey();
                }
            }
        }
        return null;
    }



    /*
	 *  Gets the Field attribute of the DisconnectedResultSetImpl object
	 *
	 *  @param  columnIndex          Description of Parameter
	 *  @return                      The Field value
	 *  @exception  RemoteException  Description of Exception
	 */
    /**
     *  Gets the field attribute of the DisconnectedResultSetImpl object
     *
     *@param  columnIndex          Description of Parameter
     *@return                      The field value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Object getField(int columnIndex) throws RemoteException {
        return record.get(columnIndex);
    }


    /*
	 *  Gets the ColumnCount attribute of the DisconnectedResultSetImpl object
	 *
	 *  @return                      The ColumnCount value
	 *  @exception  RemoteException  Description of Exception
	 */
    /**
     *  Gets the columnCount attribute of the DisconnectedResultSetImpl object
     *
     *@return                      The columnCount value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public int getColumnCount() throws RemoteException {
        return record.size();
    }


    public void updateObject(int columnindex, Object obj) throws RemoteException {
    }

    public boolean last() throws RemoteException {
        record = (ArrayList) table.get(table.size());
        return true;
    }

    public boolean first() throws RemoteException {
        record = (ArrayList) table.get(0);
        return true;
    }

    public boolean next() throws RemoteException {

        try {

            record = (ArrayList) table.get(currRow++);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public void updateRow() throws RemoteException {
    }

    public void refreshRow() throws RemoteException {
    }

    public Integer moveAbsolute(int pos) throws RemoteException {
        if (pos > table.size()) {
            throw new RemoteException("Cannot move beyond resultset boundaries");
        }
        if (currRow + pos >= table.size() || currRow + pos < 0) {
            throw new RemoteException("Cannot move beyond resultset boundaries");
        }
        if (pos < 0) {
            //pos is negative so 1+-2 = -1
            currRow = currRow + pos;
            record = (ArrayList) table.get(currRow + pos);
            return new Integer(currRow);
        }
        return new Integer(0);
    }

    public void close() throws RemoteException {
        record = null;
        table.clear();
        table = null;
    }

    public int findColumn(String colname) throws RemoteException {
        if (columndetails.containsKey(colname)) {
            return ((Integer) columndetails.get(colname)).intValue();
        }
        return -1;
    }

    public boolean previous() throws RemoteException {
        record = (ArrayList) table.get(currRow--);
        return true;
    }

    public void finalize() {
        if (DEBUG) {
            System.out.println("DisconnectedResultSet g'ced");
        }
    }

    public void clean() {
        this.table = null;
        this.columndetails = null;
    }

    public void setData(java.util.ArrayList data) {
        this.table = data;
    }

    public void setHeader(java.util.HashMap header) {
        this.columndetails = header;
    }

    public ArrayList getData() {
        return this.table;
    }

    public HashMap getHeader() {
        return this.columndetails;
    }

    /**
     * @see org.jini.projects.athena.resultset.RemoteResultSet#getRowCount()
     */
    public long getRowCount() throws RemoteException {
        return table.size();
    }

}

