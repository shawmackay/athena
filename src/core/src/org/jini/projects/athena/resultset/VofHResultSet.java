/*
 *  VofHResultSet.java
 *
 *  Created on 11 September 2001, 12:45
 */
package org.jini.projects.athena.resultset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jini.projects.athena.exception.AthenaException;

/**
 *  Concrete class of SystemResultSet representing data obtained from through a Vector of HashMaps.
 *  Instances of this class are created by the <CODE>BeaJoltConnection</CODE>and <CODE>CTGConnection</CODE> classes
 *  @author Calum

 *@version 0.9community */
public class VofHResultSet implements org.jini.projects.athena.resultset.SystemResultSet {
    private HashMap header;
    private String[] inorder;
    private Vector table;
    private Vector currow;
    private int rowRef = -1;
    private final boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null;


    /**
     *  Creates new VofHResultSet, from a table of data and a definition object
     *
     *@param  table   the data table
     *@param  header  contains the field definitions
     *@since
     */
    public VofHResultSet(Vector table, HashMap header) {
        if (DEBUG)
            System.out.println("Building VofHResultSet");
        try {
            this.header = (HashMap) header.clone();
        } catch (Exception ex) {
        }
        java.util.Set keys = this.header.entrySet();
        inorder = new String[keys.size()];
        Iterator iter = keys.iterator();
        Vector newtable = new Vector();
        while (iter.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
            Integer idx = (Integer) entry.getValue();
            inorder[idx.intValue()] = (String) entry.getKey();
        }
        for (int i = 0; i < table.size(); i++) {
            Vector newRow = new Vector();
            HashMap oldRow = (HashMap) table.get(i);
            for (int j = 0; j < inorder.length; j++) {
                newRow.add(oldRow.get(inorder[j]));
            }
            newtable.add(newRow);
        }
        this.table = newtable;
        if (DEBUG)
            System.out.println("VofHResultSet built:");
    }


    /**
     *  Gets the field attribute of the VofHResultSet object
     *
     *@param  name                 Description of Parameter
     *@return                      The field value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public Object getField(String name) throws AthenaException {
        return currow.get(((Integer) header.get(name)).intValue());
    }


    /**
     *  Gets the concurrency attribute of the VofHResultSet object
     *
     *@return                      The concurrency value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public Integer getConcurrency() throws AthenaException {
        return new Integer(0);
    }


    /**
     *  Gets the fieldName attribute of the VofHResultSet object
     *
     *@param  field                Description of Parameter
     *@return                      The fieldName value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public String getFieldName(int field) throws AthenaException {
        return inorder[field];
    }


    /**
     *  Gets the field attribute of the VofHResultSet object
     *
     *@param  columnIndex          Description of Parameter
     *@return                      The field value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public Object getField(int columnIndex) throws AthenaException {
        return currow.get(columnIndex);
    }


    /**
     *  Gets the columnCount attribute of the VofHResultSet object
     *
     *@return                      The columnCount value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public int getColumnCount() throws AthenaException {
        return inorder.length;
    }

    public void updateObject(int columnindex, Object obj) throws AthenaException {
    }

    public boolean last() throws AthenaException {
        try {
            currow = (Vector) table.get(table.size());
            rowRef = table.size();
            return true;
        } catch (Exception ex) {
            return false;
        }

    }


    public boolean first() throws AthenaException {
        try {
            currow = (Vector) table.get(0);
            rowRef = 0;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean next() throws AthenaException {
        try {
            rowRef++;
            currow = (Vector) table.get(rowRef);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void updateRow() throws AthenaException {
    }

    public void refreshRow() throws AthenaException {
    }

    public void close() throws AthenaException {
        if (DEBUG)
            System.out.println("Closing");
        table = null;
        header = null;
        inorder = null;
        if (DEBUG)
            System.out.println("Closed");
    }

    public Integer moveAbsolute(int pos) throws AthenaException {
        if (pos > table.size()) {
            throw new AthenaException("Cannot move beyond resultset boundaries");
        }
        if (rowRef + pos >= table.size() || rowRef + pos < 0) {
            throw new AthenaException("Cannot move beyond resultset boundaries");
        }
        if (pos < 0) {
            //pos is negative so 1+-2 = -1
            rowRef = rowRef + pos;
            currow = (Vector) table.get(rowRef + pos);
            return new Integer(rowRef);
        }
        return new Integer(0);
    }

    public int findColumn(String colname) throws AthenaException {
        return ((Integer) header.get(colname)).intValue();
    }

    public boolean previous() throws AthenaException {
        try {
            rowRef--;
            currow = (Vector) table.get(rowRef);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void finalize() {
        if (DEBUG) {
            System.out.println("BEAJoltReusltSet g'ced");
        }
    }

    /**
     * @see org.jini.projects.athena.resultset.AthenaResultSet#getRowCount()
     */
    public long getRowCount() throws AthenaException {
        return table.size();
    }

}

