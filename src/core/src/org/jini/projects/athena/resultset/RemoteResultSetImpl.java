
package org.jini.projects.athena.resultset;

import java.rmi.RemoteException;

import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.EmptyResultSetException;

/**
 *  Concrete class implementing RemoteResultSet, this class holds some resources
 *  in Athena, but mainly delegates operations to the Host system
 *@author     calum

 */
public class RemoteResultSetImpl implements RemoteResultSet {

    static final long serialVersionUID = 5709790291012275475L;

    private final boolean DEBUG = (System.getProperty("org.jini.projects.athena.debug") != null);
    //Hold a JDBC connection passed from RemoteConnectionImpl
    private SystemConnection conn;

    private SystemResultSet rs;



    /*
	 *  Constructor for the RemoteResultSetImpl object
	 *
	 *  @param  conn                 Description of Parameter
	 *  @param  command              Description of Parameter
	 *  @exception  RemoteException  Description of Exception
	 */
    /**
     *  Constructor for the RemoteResultSetImpl object
     *
     *@param  conn                         Description of Parameter
     *@param  command                      Description of Parameter
     *@exception  CannotExecuteException   Description of Exception
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  RemoteException          Description of Exception
     *@since
     */
    public RemoteResultSetImpl(SystemConnection conn, Object command) throws CannotExecuteException, EmptyResultSetException, RemoteException {
        if (DEBUG) {
            System.err.println(new java.util.Date() + ": RemoteResultSet :Built a RemoteResultSet");
        }
        try {

            Object ret = conn.issueCommand(command);
            if (ret instanceof SystemResultSet) {
                rs = (SystemResultSet) ret;
            } else {
                throw new EmptyResultSetException("no records available");
            }
        } catch (EmptyResultSetException ersex) {
            System.out.println(new java.util.Date() + ": RemoteResultSet :Re-throwing");
            throw ersex;
        } catch (Exception ex) {
            throw new CannotExecuteException("Cannot execute command" + ex.getMessage());
        }
    }


    /*
	 *  Constructor for the RemoteResultSetImpl object
	 *
	 *  @param  conn                 Description of Parameter
	 *  @param  command              Description of Parameter
	 *  @param  params               Description of Parameter
	 *  @exception  RemoteException  Description of Exception
	 */
    /**
     *  Constructor for the RemoteResultSetImpl object
     *
     *@param  conn                         Description of Parameter
     *@param  command                      Description of Parameter
     *@param  params                       Description of Parameter
     *@exception  CannotExecuteException   Description of Exception
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  RemoteException          Description of Exception
     *@since
     */
    public RemoteResultSetImpl(SystemConnection conn, Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, RemoteException {
        if (DEBUG) {
            System.err.println(new java.util.Date() + ": RemoteResultSet :Built a RemoteResultSet");
        }
        try {

            Object ret = conn.issueCommand(command, params);
            if (ret instanceof SystemResultSet) {
                rs = (SystemResultSet) ret;
            } else {
                throw new EmptyResultSetException("no records available");
            }
        } catch (EmptyResultSetException ersex) {
            System.out.println(new java.util.Date() + ": RemoteResultSet :Re-throwing");
            throw ersex;
        } catch (Exception ex) {
            throw new CannotExecuteException("Cannot execute command" + ex.getMessage());
        }
    }


    /**
     *  Constructor for the RemoteResultSetImpl object
     *
     *@param  rs                           Description of Parameter
     *@exception  EmptyResultSetException  Description of Exception
     *@exception  RemoteException          Description of Exception
     *@since
     */
    public RemoteResultSetImpl(SystemResultSet rs) throws EmptyResultSetException, RemoteException {
        if (DEBUG) {
            System.out.println("Assigning a pre-built resultset");
        }
        this.rs = rs;
    }


    public Object getField(String name) throws RemoteException {

        //System.err.println("Requesting a field with name: " + name);
        try {
            return rs.getField(name);
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot get requested field", ex);
        }
    }

    public Integer getConcurrency() throws RemoteException {

        try {
            return rs.getConcurrency();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot obtain concurrency level", ex);
        }
    }

    public Integer getCursorType() throws RemoteException {

        try {
            return new Integer(-1);
        } catch (Exception ex) {
            throw new RemoteException("Cannot obtain cursor type", ex);
        }
    }

    public Object getField(int columnIndex) throws RemoteException {

        try {
            return rs.getField(columnIndex);
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot get object in field " + columnIndex, ex);
        }
        //return new String("Not implemented yet!");
    }

    public String getFieldName(int pos) throws RemoteException {
        try {
            return rs.getFieldName(pos);
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot get field name", ex);
        }

    }

    public int getColumnCount() throws RemoteException {
        try {
            //System.out.println("Gettign column count of " + rs.getClass().getName());
            return rs.getColumnCount();
        } catch (AthenaException ex) {

            throw new RemoteException("Cannot obtain column count", ex);
        }

    }

    public Integer moveAbsolute(int pos) throws RemoteException {

        try {
            return rs.moveAbsolute(pos);
        } catch (Exception ex) {
            throw new RemoteException("Cannot move to given record", ex);
        }
    }

    public void close() throws RemoteException {

        try {
            if (rs != null)
                rs.close();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot close recordset", ex);
        }
    }

    public int findColumn(String colname) throws RemoteException {

        try {
            return rs.findColumn(colname);
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot find column: " + colname, ex);
        }
        //return -1;
    }

    public boolean next() throws RemoteException {

        //System.out.println("running next");
        try {
            return rs.next();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot move to next record", ex);
        }

    }

    public boolean previous() throws RemoteException {

        try {
            return rs.previous();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot move to previous record", ex);
        }
    }

    public boolean first() throws RemoteException {

        try {
            return rs.first();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot move to first record", ex);
        }
    }

    public boolean last() throws RemoteException {

        try {
            return rs.last();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot move to last record", ex);
        }
    }

    public void refreshRow() throws RemoteException {

        try {
            rs.refreshRow();
        } catch (AthenaException ex) {
            throw new RemoteException("Cannot refresh row", ex);
        }
    }

    public void updateObject(int columnindex, Object obj) throws RemoteException {
    }

    public void updateRow() throws RemoteException {
    }

    public void finalize() {
        try {
            this.close();
        } catch (Exception ex) {
            System.out.println("Finalize: Could not close RemoteResultSetImpl");
            System.out.println(new java.util.Date() + ": RemoteResultSet :RemoteResultSet g'ced");
        }
    }

    /**
     * @see org.jini.projects.athena.resultset.RemoteResultSet#getRowCount()
     */
    public long getRowCount() throws RemoteException {
        try {
            return rs.getRowCount();
        } catch (Exception e) {
            throw new RemoteException("Error", e);
        }
    }

}

