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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.service.StatisticMonitor;

/**
 *  Concrete implementation of the AthenaResultSet interface. This class is
 *  designed as a Smart Proxy, if a RemoteResultSet reference is held,
 *  invocations of methods will be passed through to Athena via RMI otherwise,
 *  the 'table' is assumed to be held in this class, and is manipulated locally
 *
 *@author     calum
 *
 */
public class AthenaResultSetImpl implements AthenaResultSet, java.io.Serializable {
    private ArrayList record = null;
    private ArrayList table = null;
    private HashMap columndetails = null;
    private int currRow = 0;
    private RemoteResultSet rrs = null;
    static final long serialVersionUID = 2188013553185784211L;

    /**
     *  Constructor for the AthenaResultSetImpl object
     *
     *@since 1.0b
     */
    public AthenaResultSetImpl() {
        record = new ArrayList();
        table = new ArrayList();
        columndetails = null;
    }


    /**
     *  Constructor for the AthenaResultSetImpl which initialises the remote
     *  reference. This indicates that the resultset is held remotely
     *
     *@param  rrs  A stub instance to a RemoteResultSet object
     *@since 1.0b
     */
    public AthenaResultSetImpl(RemoteResultSet rrs) {
        this.rrs = rrs;
    }

    public AthenaResultSetImpl(SystemResultSet srs) {
        table = buildlocal(srs, 10);
        columndetails = buildColDetails(srs);
    }

    /**
     *  Constructor for the AthenaResultSetImpl which initialises the local data.
     *  This indicates that this is a <CODE>LOCAL</CODE> resultset
     *
     *@param  table          Description of Parameter
     *@param  columndetails  Description of Parameter
     *@since 1.0b
     */
    public AthenaResultSetImpl(ArrayList table, HashMap columndetails) {
        setData(table);
        setHeader(columndetails);


        try {
            record = (ArrayList) table.get(0);
        } catch (Exception ex) {
            System.err.println(new java.util.Date() + ": No records in table");
            ex.printStackTrace();
        }
    }


    public Object getField(String name) throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.getField(name);
            } else {
                if (record != null) {
                    String tname = name.toLowerCase();
                    int idx = ((Integer) columndetails.get(tname)).intValue();
                    return record.get(idx);
                }
                return null;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            throw new AthenaException(ex);
        }
    }

    public Integer getConcurrency() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.getConcurrency();
            } else {
                return new Integer(java.sql.ResultSet.CONCUR_READ_ONLY);
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public Integer getCursorType() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.getCursorType();
            } else {
                return new Integer(java.sql.ResultSet.FETCH_UNKNOWN);
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public Object getField(int columnIndex) throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.getField(columnIndex);
            } else if (record != null) {
                return record.get(columnIndex);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }

    }

    public String getFieldName(int field) throws AthenaException {
        try {
            if (rrs != null) {
                return rrs.getFieldName(field);
            }
        } catch (Exception es) {
            throw new AthenaException(es);
        }
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

    public int getColumnCount() throws AthenaException {
        try {
            if (rrs != null) {
                return rrs.getColumnCount();
            }
        } catch (Exception es) {
            throw new AthenaException(es);
        }
        return record.size();
    }


    public Integer moveAbsolute(int pos) throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.moveAbsolute(pos);
            } else {
                if (pos > table.size()) {
                    throw new AthenaException("Cannot move beyond resultset boundaries");
                }
                if (pos <= table.size() && pos >= 0) {
                    currRow = pos;

                    record = (ArrayList) table.get(currRow);
                    return new Integer(currRow);
                }
                if (pos < 0) {
                    //pos is negative so 1+-2 = -1
                    currRow = table.size() - pos;
                    record = (ArrayList) table.get(currRow);
                    return new Integer(currRow);
                }
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
        return new Integer(-1);
    }

    public void close() throws AthenaException {

        try {
            if (rrs != null) {
                rrs.close();
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public int findColumn(String colname) throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.findColumn(colname);
            }
            if (columndetails.containsKey(colname)) {
                return ((Integer) columndetails.get(colname)).intValue();
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
        return -1;
    }

    public boolean next() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.next();
            }
            record = (ArrayList) table.get(currRow++);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public boolean previous() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.previous();
            }
            record = (ArrayList) table.get(currRow--);
            return true;
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
        //return false;
    }

    public boolean first() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.first();
            }
            record = (ArrayList) table.get(0);
            currRow = 0;
            return true;
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }

    }

    public boolean last() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.last();
            }
            record = (ArrayList) table.get(table.size());
            return true;
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public void refreshRow() throws AthenaException {

        try {
            if (rrs != null) {
                rrs.refreshRow();
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public void updateObject(int columnindex, Object obj) throws AthenaException {

        try {
            if (rrs != null) {
                rrs.updateObject(columnindex, obj);
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    public void updateRow() throws AthenaException {

        try {
            if (rrs != null) {
                rrs.updateRow();
            }
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }
    }

    /**
     * Wipes the table and any column details from memory - used in pooling
     */
    public void clean() {
        this.table = null;
        this.columndetails = null;
    }

    /**
     * Assigns up a new set of data- used in pooling
     */
    public void setData(java.util.ArrayList data) {
        this.table = data;
    }

    /**
     * Assigns up a new set of column details- used in pooling
     */
    public void setHeader(java.util.HashMap header) {
        this.columndetails = header;
    }


    private ArrayList buildlocal(SystemResultSet rrs, int Capacity) {

        try {
            ArrayList table = new ArrayList(Capacity);
            Vector header = new Vector();
            rrs.next();
            for (int i = 0; i < rrs.getColumnCount(); i++) {

                header.add(rrs.getFieldName(i));
            }

            do {
                ArrayList row = new ArrayList();
                for (int i = 0; i < header.size(); i++) {
                    row.add(rrs.getField(i));
                }
                table.add(row);
            } while (rrs.next());

            return table;
        } catch (Exception ex) {
            System.err.println(new java.util.Date() + ": RemoteConnection :Can't build table because: " + ex.getMessage());
            StatisticMonitor.addFailure();
            ex.printStackTrace();
            System.out.println(new java.util.Date() + ": RemoteConnection :Returning empty Object");
            return null;
        }

    }


    /**
     *  Description of the Method
     *
     *@param  rrs  Description of Parameter
     *@return      Description of the Returned Value
     *@since
     */
    private HashMap buildColDetails(SystemResultSet rrs) {
        HashMap returntable = new HashMap();
        try {
            for (int i = 0; i < rrs.getColumnCount(); i++) {
                returntable.put(rrs.getFieldName(i).toLowerCase(), new Integer(i));
            }
        } catch (Exception ex) {
            System.out.println("RemoteConnection :No column details: " + ex.getMessage());
        }
        return returntable;
    }

    /**
     * @see org.jini.projects.athena.resultset.AthenaResultSet#getRowCount()
     */
    public long getRowCount() throws AthenaException {

        try {
            if (rrs != null) {
                return rrs.getRowCount();
            } else
                return this.table.size();
        } catch (Exception e) {
            System.err.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

}

