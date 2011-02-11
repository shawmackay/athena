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
package org.jini.projects.athena.connects.oracle;

import org.jini.projects.athena.command.Array;
import org.jini.projects.athena.command.CompoundType;
import org.jini.projects.athena.command.StdArray;
import org.jini.projects.athena.command.StdCompoundType;
import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.connects.oracle.syshandlers.OracleSysHandler;
import org.jini.projects.athena.connects.sql.CheckConnection;
import org.jini.projects.athena.connects.sql.chi.DefaultStoredProc_Dialect;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.PooledConnectionException;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.athena.util.builders.LogExceptionWrapper;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import java.io.Reader;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  System Connection proxying an Oracle compliant database.<br>
 *  Confirmed usage on 8i (8.1.7.2.1)<br>
 *  Tested on 9i<br>
 *@author     calum

 */
public class OracleConnection implements SystemConnection {
    private static int Statref = 0;

    Logger log = Logger.getLogger("org.jini.projects.athena.connection");

    /**
     *  File name to pass to <CODE>AthenaLogger.persist()</CODE> and <CODE>
     *  AthenaLogger.restore()</CODE> when state need to be persisted
     */
    public String PersistentFile = null;
    java.sql.Statement stmt = null;
    java.sql.PreparedStatement pstmt = null;
    CallableStatement cs = null;
    long MagicNumber = 0L;

    boolean packFrame = false;
    private final boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
    private boolean allocated = false;
    private boolean connected = false;
    private boolean canBeFreed = false;
    private Object txnID ;

    private int ref = 0;
    private boolean autoAbort = false;
    private Connection conn = null;
    //    private Statement stmt=null;
    //  private PreparedStatement pstmt = null;
    private String username = null;
    private String password = null;
    private String driver = null;
    private String URL = null;
   
    private int numalloc = 0;
    private HostErrorHandler errHandler;
    private static Thread checker;

    static {
    	checker = new Thread(new CheckConnection());
    	checker.start();
    }

    public Object handleType(Object in) throws AthenaException {
        //todo : Add Recursion facilities and Possibly move this to Connection class and Interfaces

        // log.finest("DataType: " + in.getClass().getName());

        try {
            if (in instanceof oracle.sql.STRUCT) {
                CompoundType map = null;
                STRUCT oracleSTRUCT = (STRUCT) in;

                log.finest("SQL Type: " + oracleSTRUCT.getSQLTypeName());
                ResultSetMetaData smd = oracleSTRUCT.getDescriptor().getMetaData();
                map = new StdCompoundType();
                Object[] objarr = oracleSTRUCT.getAttributes();
                //   System.out.println("Data: ");
                for (int i = 0; i < objarr.length; i++) {
                    map.setField(smd.getColumnName(i + 1), objarr[i]);
                    log.finest("\tType: " + objarr[i].getClass().getName() + "; Value: " + objarr[i]);
                }
                return map;
            }
            if (in instanceof java.sql.Array) {
                //java.sql.Array oarr = (java.sql.Array) in;
                ARRAY oarr = (ARRAY) in;
                log.finest("Oracle ARRAY Basetype: " + oarr.getBaseTypeName());
                Object[] objarr = (Object[]) oarr.getArray();
                log.finest(objarr[0].getClass().getName());
                Array map = new StdArray();
                for (int i = 0; i < objarr.length; i++)
                    map.add(handleType(objarr[i]));
                return map;
            }
            if (in instanceof Clob) {
                Clob clob = (Clob) in;
                //char[] buf = new char[clob.length()];
                StringBuffer returnBuffer = new StringBuffer();
                int buflength = 4096;
                char[] buffer;
                Reader buf = clob.getCharacterStream();
                try {
                    buffer = new char[buflength];
                    int a = buf.read(buffer);
                    while (a != -1) {
                        returnBuffer.append(buffer, 0, a);
                        buffer = new char[buflength];
                        a = buf.read(buffer);
                    }
                } catch (Exception e) {
                    log.severe("Clob conversion Error: " + e.getMessage());
                    e.printStackTrace();
                }
                return returnBuffer.toString().trim();

            }
            if (in instanceof Blob) {
                Blob blob = (Blob) in;
                byte[] buf = blob.getBytes(0,(int)blob.length());                
                return buf;
            }
            else {
                return in;

            }
        } catch (SQLException e) {
            throw new AthenaException(e.getMessage());
        }    
    }


    /**
     *  Creates a new Pool with the given parameters
     *
     *@exception  PooledConnectionException  Description of Exception
     *@since
     *@throws  PooledConnectionException     if an exception ocurs during
     *      initialization
     */

    public OracleConnection() throws PooledConnectionException {
        try {
            this.username = System.getProperty("org.jini.projects.athena.connect.username");
            this.password = System.getProperty("org.jini.projects.athena.connect.password");
            this.URL = System.getProperty("org.jini.projects.athena.connect.url");
            this.driver = System.getProperty("org.jini.projects.athena.connect.driver");
            Class.forName(driver);
            conn = DriverManager.getConnection(URL, username, password);
            if (driver.indexOf("oracle") != -1) {

                oracle.jdbc.OracleConnection orconn = (oracle.jdbc.OracleConnection) conn;
                orconn.setDefaultRowPrefetch(500);
                errHandler = new OracleSysHandler();
            }
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connected = true;
            ref = OracleConnection.Statref;
            OracleConnection.Statref++;

        } catch (Exception ex) {
            System.err.println("OracleConn :Indicating dropped Connection");
            ex.printStackTrace();
            allocated = false;
            connected = false;
            throw new PooledConnectionException(ex.getMessage());
        }
    }

    /**
     *  Constructor for the SQLConnection object
     *
     *@param  ref                            Description of Parameter
     *@exception  PooledConnectionException  Description oaf Exception
     *@since
     */
    public OracleConnection(int ref) throws PooledConnectionException {
        this();
        this.ref = ref;
    }

    /**
     *  The test program for the SQLConnection class
     *
     *@param  args  The command line arguments
     *@since
     */
    public static void main(String[] args) {
        String DRIVER = "oracle.jdbc.driver.OracleDriver";
        String USERNAME = "system";
        String PASSWORD = "calum";
        int NUMCONNECTIONS = 5;
        String NAME = "DBConnector";
        String URL = "jdbc:oracle:thin:@nts4_004.countrywide-assured.co.uk:1521:CMDB";
        try {
            /*
             *  SQLConnection pc = new SQLConnection(USERNAME,PASSWORD,URL, DRIVER,1);
             *
             *  SystemResultSet srs = (SystemResultSet) pc.issueCommand("SELECT * FROM X");
             *  System.out.println("Columns: "+ srs.getColumnCount());
             *  System.out.println("TRANSNAME:"+ srs.getField("IDCOL"));
             *  srs.close();
             */
        } catch (Exception ex) {
            System.err.println("exc: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void setTransactionID(Object ID) throws AthenaException {
        txnID = ID;
    }

    public void setReference(int ref) {
        PersistentFile = System.getProperty("org.jini.projects.athena.service.name") + "CONN" + ref + ".ser";
    }

    public String getPersistentFileName() {
        return this.PersistentFile;
    }

//    public Connection getConnection() {
//        allocated = true;
//        try {
//            log.fine("Getting Connection");
//            if (stmt == null) {
//                log.fine("SQLConn :Getting Statement");
//                stmt = conn.createStatement();
//            }
//        } catch (Exception ex) {
//        }
//        return conn;
//    }

    public boolean isConnected() {
        return connected;
    }

    public Object issueCommand(Object command) throws Exception {

        Object DMLreturnval = null;
        try {
            log.fine("Command: " + command);

            if (command instanceof String) {
                String strCommand = (String) command;
                if (strCommand.toUpperCase().indexOf("SELECT") != -1) {
                    stmt = null;
                    return new org.jini.projects.athena.resultset.SQLResultSet(conn.createStatement(), strCommand, this.ref);
                }
                if (stmt == null) {
                    log.fine("Recreating statement");

                    stmt = conn.createStatement();
                    log.fine("SQLConn :Statement built for connection " + this.ref);
                }
                if (strCommand.toUpperCase().indexOf("INSERT") != -1) {
                    //System.err.println("Recognised INSERT");
                    DMLreturnval = new Integer(stmt.executeUpdate(strCommand));
                }
                if (strCommand.toUpperCase().indexOf("UPDATE") != -1) {
                    //System.err.println("Recognised UPDATE");
                    DMLreturnval = new Integer(stmt.executeUpdate(strCommand));
                }
                if (strCommand.toUpperCase().indexOf("DELETE") != -1) {
                    //System.err.println("Recognised DELETE");
                    DMLreturnval = new Integer(stmt.executeUpdate(strCommand));
                }

                if (strCommand.toUpperCase().indexOf("BEGIN") != -1) {
                    // todo: Handle Stored Procedures Objects here
                    log.finer("Running callable statement");
                    cs = conn.prepareCall(strCommand);
                    cs.execute();
                    ResultSet rs = cs.getResultSet();
                    if (rs != null)
                        DMLreturnval = new org.jini.projects.athena.resultset.SQLResultSet(rs);
                    //pstmt.close();
                }
            }

            if (command instanceof org.jini.projects.athena.command.StdCommand) {
                org.jini.projects.athena.command.StdCommand sqlcomm = (org.jini.projects.athena.command.StdCommand) command;
                /*
                                SQLCall sqlcall;
                                System.out.println(new java.util.Date() + ": SQLConn : Got a SQL Command");
                                sqlcall = new SQLCall(this.conn, sqlcomm);
                                Object returnObj = sqlcall.execute();
                                /*
                                 *  if (returnObj instanceof ResultSet) {
                                 *  System.out.println("Returning a SQLResultSet");
                                 *  return new org.jini.projects.org.jini.projects.athena.resultset.SQLResultSet(
                                 *  }
                                 */
                //if (sqlcomm.getParameter( ))
                DefaultStoredProc_Dialect app = new DefaultStoredProc_Dialect(this.conn);
                Object[] parms = {conn, sqlcomm};
                app.init(parms);
                log.finest("Processing input");
                app.processInput();

                
                String call = (String) app.getCallInput();
                app.go();

                log.finest("Processing output");
                app.processOutput();
                log.fine("Output processing finished");
                
                DMLreturnval = app.getCallOutput();
            }
        } catch (Exception ex) {
            this.autoAbort = true;
            LogExceptionWrapper lex;
            if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                lex = new LogExceptionWrapper(ex, true, false);
            else
                lex = new LogExceptionWrapper(ex, false, false);
            errHandler.handleHostException(lex);

            throw ex;
        }
        if (stmt != null) {
            log.finest("OracleConnection: Statement Close");
            stmt.close();
            stmt = null;
        }
        return DMLreturnval;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAutoAbort() {
        this.autoAbort = true;
    }

    public Object issueCommand(Object command, Object[] params) throws Exception {
        Object DMLreturnval = null;
        try {
            if (command instanceof String) {
                String strCommand = (String) command;
                log.fine("Command: " + command);
                log.fine("No of Parameters: " + params.length);


                if (strCommand.toUpperCase().indexOf("SELECT") != -1) {
                    //System.err.println("Recognised SELECT");
                    //java.sql.ResultSet rs = pstmt.executeQuery();
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    pstmt = null;

                    try {
                        return new org.jini.projects.athena.resultset.SQLResultSet(conn.prepareStatement((String) command), params, this.ref);
                    } catch (SQLException e) {
                        if (this.errHandler != null) {
                            LogExceptionWrapper lex;
                            if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                                lex = new LogExceptionWrapper(e, true, false);
                            else
                                lex = new LogExceptionWrapper(e, false, false);
                            errHandler.handleHostException(lex);
                        } else {
                            System.out.println("Errhandler is null!");
                        }

                        throw e;
                    }
                }
                if (pstmt == null) {
                    pstmt = conn.prepareStatement(strCommand);
                }

                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }

                if (strCommand.toUpperCase().indexOf("INSERT") != -1) {
                    //System.err.println("Recognised INSERT");
                    DMLreturnval = new Integer(pstmt.executeUpdate());
                }
                if (strCommand.toUpperCase().indexOf("UPDATE") != -1) {
                    //System.err.println("Recognised UPDATE");
                    DMLreturnval = new Integer(pstmt.executeUpdate());
                }
                if (strCommand.toUpperCase().indexOf("DELETE") != -1) {
                    //System.err.println("Recognised DELETE");

                    DMLreturnval = new Integer(pstmt.executeUpdate());
                }
            }
        } catch (Exception ex) {
            System.out.println(new java.util.Date() + ": SQLConn :Err: " + ex.getMessage());
            ex.printStackTrace();
            System.out.println(new java.util.Date() + ": SQLConn :Closing statement");
            pstmt.close();
            pstmt = null;
            LogExceptionWrapper lex;
            if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                lex = new LogExceptionWrapper(ex, true, false);
            else
                lex = new LogExceptionWrapper(ex, false, false);
            errHandler.handleHostException(lex);

            throw ex;
        }
        pstmt.close();
        return DMLreturnval;
    }

    public boolean isAutoAbortSet() throws AthenaException {
        return autoAbort;
    }

    public org.jini.projects.athena.command.Command getSystemCommand() throws AthenaException {
        return new org.jini.projects.athena.command.StdCommand();
    }

    public synchronized boolean commit() throws Exception {
        try {
            conn.commit();
        } catch (SQLException e) {
            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(e, true, false);
                else
                    lex = new LogExceptionWrapper(e, false, false);
                errHandler.handleHostException(lex);
            }

            throw e;
        }
        return true;
    }

    public synchronized boolean rollback() throws Exception {
        try {
            conn.rollback();
        } catch (SQLException e) {

            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(e, true, false);
                else
                    lex = new LogExceptionWrapper(e, false, false);
                errHandler.handleHostException(lex);
            }

            throw e;
        }
        return true;
    }

    public void connectTo(java.util.Properties connectionProperties) {
    }

    public void connectTo(String User, String Password, String URL, String Driver) throws PooledConnectionException {
        /*
         *  Allow the user to connect to another database if4....
         *  Connection is not allocated to a client
         *  Connection is not connected
         *  If connection _is_ connected - i.e connected to a database but not allocated
         *  check if the connection can be freed before reconnecting to another db
         */
        //
        try {
            if (!allocated && connected && canBeFreed) {
                //Disconnect
                stmt.close();

                stmt = null;
                conn.close();
                connected = false;
            }
            if (!allocated && !connected) {
                Class.forName(Driver);
                conn = DriverManager.getConnection(URL, User, Password);
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connected = true;

            }
        } catch (Exception ex) {
            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(ex, true, false);
                else
                    lex = new LogExceptionWrapper(ex, false, false);
                errHandler.handleHostException(lex);
            }

            throw new PooledConnectionException("[" + ex.getClass().getName() + "]: " + ex.getMessage());
        }
    }

    public void release() {
        log.fine("Connection returned to pool");

        //Ensure that all cursors are closed

        try {
            if (stmt != null) {
                log.fine("Closing cursors");

                stmt.close();
                stmt = null;
            }
            if (pstmt != null) {
                log.fine("Closing cursors");

                pstmt.close();
                pstmt = null;
            }
        } catch (Exception ex) {
            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(ex, true, false);
                else
                    lex = new LogExceptionWrapper(ex, false, false);
                errHandler.handleHostException(lex);
            }

        }
        stmt = null;
        pstmt = null;
        allocated = false;
        numalloc--;
        log.fine("Deallocated: " + this.ref);

    }

    public boolean canFree() {
        return canBeFreed;
    }

    public synchronized void allocate() {
        log.fine("Allocated: " + this.ref);

        allocated = true;
        numalloc++;
        if (numalloc > 1) {
            log.warning("Allocated more than once!");
        }
    }

    public synchronized void close() throws AthenaException {
        try {
            if (stmt != null) {                
                stmt.close();
                stmt = null;
            } 
            if (cs != null) {
                cs.close();
                cs = null;
            }
            if (pstmt != null) {
                
                pstmt.close();
                pstmt = null;
            } 
            conn.rollback();
            conn.close();
            log.log(Level.FINEST, "Disconnecting.....");

        } catch (Exception ex) {

            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(ex, true, false);
                else
                    lex = new LogExceptionWrapper(ex, false, false);
                errHandler.handleHostException(lex);
            }
            throw new AthenaException(ex);

        } finally {
            this.connected = false;
        }
    }

    public boolean inTransaction() throws AthenaException {
        return txnID!=null;
    }

    public void resetAutoAbort() throws AthenaException {
        autoAbort = false;
    }

    public void reConnect() throws AthenaException {
        try {
            Class.forName(this.driver);
            conn = DriverManager.getConnection(this.URL, this.username, this.password);
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            //PersistentFile = ServiceApplication.NAME + "CONN" + ref_index + ".ser";
            stmt = conn.createStatement();
            connected = true;
        } catch (Exception ex) {
            if (this.errHandler != null) {
                LogExceptionWrapper lex;
                if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
                    lex = new LogExceptionWrapper(ex, true, false);
                else
                    lex = new LogExceptionWrapper(ex, false, false);
                errHandler.handleHostException(lex);
            }

            allocated = false;
            connected = false;
            throw new AthenaException(new PooledConnectionException(ex.getMessage()));
        }

    }

    public void finalize() {
        log.finest("Finalization: SQLConnection");
    }

    /* (non-Javadoc)
     * @see org.jini.projects.org.jini.projects.athena.connection.SystemConnection#getErrorHandler()
     */
    public HostErrorHandler getErrorHandler() {
        return errHandler;
    }


	public boolean prepare() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}


	public void clearTransactionFlag() throws AthenaException {
		// TODO Auto-generated method stub
		
	}

}
