/*
 *  SQLResultSet.java
 *
 *  Created on 06 August 2001, 11:47
 */
package org.jini.projects.athena.resultset;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.exception.AthenaException;

/**
 *  Concrete class of SystemResultSet representing data obtained from a JDBC
 *  Compliant Database. Instances of this class are created by the <CODE><a href="../connection/SQLConnection.html">SQLConnection</a>
 *  </CODE> class @author calum
 *
 *@author     calum
 *@version 0.9community */
public class SQLResultSet implements SystemResultSet {
    java.sql.ResultSet rs = null;
    java.sql.Statement tStmt = null;
    java.sql.PreparedStatement tPStmt = null;
    int currentRow = 1;
    private final boolean DEBUG = true; //System.getProperty("org.jini.projects.athena.debug") != null;
    private int connref = 0;
    private Logger log;

    /**
     *  Creates new SQLResultSet, with no parameters
     *
     *@since
     */
    public SQLResultSet() {
        log = Logger.getLogger("org.jini.projects.athena.resultset");
    }

    public SQLResultSet(java.sql.ResultSet rs) {
        this();
        this.rs = rs;
    }

    /**
     *  Constructor for the SQLResultSet object, for a prepared statement, with
     *  parameters
     *
     *@param  pstmt    The prepared Statement from a <CODE>java.sql.Connection</CODE>
     *      Object
     *@param  params   The paremeters to insert into the prepared statement
     *@param  connref  A reference of connection from which this Resultset is
     *      created
     *@since
     */
    public SQLResultSet(java.sql.PreparedStatement pstmt, Object[] params, int connref) throws SQLException {
        this();
        tPStmt = pstmt;

        for (int i = 0; i < params.length; i++) {
            tPStmt.setObject(i + 1, params[i]);
        }
        //Parameters are 1-based, arrays are 0-based
        this.rs = tPStmt.executeQuery();
        //rs.next();

    }


    /**
     *  Constructor for the SQLResultSet object, for a prepared statement, with
     *  parameters
     *
     *@param  stmt     A prepared Statement from a <CODE>java.sql.Connection</CODE>
     *      Object
     *@param  SQL      The SQL Command String to run against the Statement
     *@param  connref  A reference of connection from which this Resultset is
     *      created
     *@since
     */
    public SQLResultSet(java.sql.Statement stmt, String SQL, int connref) throws SQLException {
        this();
        tStmt = stmt;
        
        this.rs = tStmt.executeQuery(SQL);
        //rs.next();

    }


    /**
     *  Gets the field attribute of the SQLResultSet object
     *
     *@param  name                 Description of Parameter
     *@return                      The field value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public Object getField(String name) throws AthenaException {
        try {
            return rs.getObject(name);
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }


    public Integer getConcurrency() throws AthenaException {
        try {
            return new Integer(rs.getConcurrency());
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public Object getField(int columnIndex) throws AthenaException {
        try {

            return rs.getObject(columnIndex + 1);
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public int getColumnCount() throws AthenaException {
        try {
            return rs.getMetaData().getColumnCount();
        } catch (Exception ex) {
            System.out.println("Error getting columncount for connection:" + this.connref);
            System.out.println("error: " + ex.getMessage());
            ex.printStackTrace();
            throw new AthenaException(ex);
        }
    }


    /**
     *  Gets the FieldName attribute of the SQLResultSet object
     *
     *@param  field                Description of Parameter
     *@return                      The FieldName value
     *@exception  AthenaException  Description of Exception
     *@since
     */
    public String getFieldName(int field) throws AthenaException {
        try {
            return rs.getMetaData().getColumnName(field + 1);
        } catch (Exception ex) {
            throw new AthenaException(ex);
        }

    }

    public void updateObject(int columnindex, Object obj) throws AthenaException {
        try {
            rs.updateObject(columnindex + 1, obj);
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }
    }

    public boolean last() throws AthenaException {
        try {
            currentRow = -1;
            return rs.last();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public boolean first() throws AthenaException {
        try {
            currentRow = 1;
            return rs.first();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public boolean next() throws AthenaException {
        try {
            currentRow++;
            return rs.next();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public void updateRow() throws AthenaException {
        try {
            rs.updateRow();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public void refreshRow() throws AthenaException {
        try {
            rs.refreshRow();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public void close() throws AthenaException {
        try {
            if (DEBUG) {
                log.log(Level.FINE, "Closing resultset");
            }
            rs.close();
            if (tStmt != null) {
                if (DEBUG)
                    log.log(Level.FINE, "Closing a Statement");
                tStmt.close();
            }
            if (tPStmt != null) {
                if (DEBUG)
                    log.log(Level.FINE, "Closing a Prepared Statement");
                tPStmt.close();
            }
            rs = null;
            tStmt = null;
            tPStmt = null;

        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public Integer moveAbsolute(int pos) throws AthenaException {
        try {
            rs.absolute(pos);
            return new Integer(rs.getRow());
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public int findColumn(String colname) throws AthenaException {
        try {
            return rs.findColumn(colname);
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public boolean previous() throws AthenaException {
        try {
            currentRow--;
            return rs.previous();
        } catch (Exception ex) {
            throw new org.jini.projects.athena.exception.AthenaException(ex);
        }

    }

    public void finalize() {

    }

    /**
     * @see org.jini.projects.athena.resultset.AthenaResultSet#getRowCount()
     */
    public long getRowCount() throws AthenaException {
        try {
            rs.last();
            long totalRows = (long) rs.getRow();
            rs.absolute(currentRow);
            return totalRows;
        } catch (Exception e) {
            throw new AthenaException(e);
        }
    }

}

