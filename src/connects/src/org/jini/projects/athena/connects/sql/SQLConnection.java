/**
 *
 *@version 0.9community */

package org.jini.projects.athena.connects.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.HandleEngine;
import org.jini.projects.athena.connects.sql.chi.SQLCall;
import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.PooledConnectionException;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.SystemManager;

import com.sun.jini.constants.TimeConstants;

/**
 * System Connection proxying a JDBC compliant database
 * 
 * @author calum
 *  
 */
public class SQLConnection implements SystemConnection {
	private static int Statref = 0;

	protected Logger log = Logger.getLogger("org.jini.projects.athena.connection");
	/**
	 * File name to pass to <CODE>AthenaLogger.persist()</CODE> and <CODE>
	 * AthenaLogger.restore()</CODE> when state need to be persisted
	 * 
	 * @since
	 */
	public String PersistentFile = null;
	java.sql.Statement stmt = null;
	java.sql.PreparedStatement pstmt = null;
	long MagicNumber = 0L;

	boolean packFrame = false;
	private final boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
	private boolean allocated = false;
	private boolean connected = false;
	private boolean canBeFreed = false;
	private Object txnID ;
	private boolean autoAbort = false;
	private Connection conn = null;
	//    private Statement stmt=null;
	//  private PreparedStatement pstmt = null;
	private String username = null;
	private String password = null;
	private String driver = null;
	private String URL = null;
	private int ref = 0;
	private int numalloc = 0;
	private HostErrorHandler errHandler;
	private static boolean transactionIsolationErrorReported;
	private static Thread checker;

	static {
		checker = new Thread(new CheckConnection());
		checker.start();
	}

	/**
	 * Creates a new Pool with the given parameters
	 * 
	 * @exception PooledConnectionException
	 *                        Description of Exception
	 * @since @throws
	 *             PooledConnectionException if an exception ocurs during
	 *             initialization
	 */

	public SQLConnection() throws PooledConnectionException {
		try {
			this.username = System.getProperty("org.jini.projects.athena.connect.username");
			this.password = System.getProperty("org.jini.projects.athena.connect.password");
			this.URL = System.getProperty("org.jini.projects.athena.connect.url");
			this.driver = System.getProperty("org.jini.projects.athena.connect.driver");
			Class.forName(driver);
			conn = DriverManager.getConnection(URL, username, password);
			//Lines removed to remove dependencies
			/*
			 * if (driver.indexOf("oracle") != -1) {
			 * oracle.jdbc.OracleConnection orconn =
			 * (oracle.jdbc.OracleConnection) conn;
			 * orconn.setDefaultRowPrefetch(500); errHandler = new
			 * OracleSysHandler(); }
			 */
			stmt = conn.createStatement();
			conn.setAutoCommit(false);
			try {
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			} catch (Exception ex) {
				if (!transactionIsolationErrorReported) {
					this.log.warning("Serializable Transaction Isolation Not supported by driver: " + ex);
					transactionIsolationErrorReported = true;
				}
			}
			connected = true;
			ref = SQLConnection.Statref;
			SQLConnection.Statref++;

		} catch (Exception ex) {
			System.err.println(new java.util.Date() + ": SQLConn :Indicating dropped Connection");
			ex.printStackTrace();
			allocated = false;
			connected = false;
			throw new PooledConnectionException(ex.getMessage());
		}
	}

	/**
	 * Constructor for the SQLConnection object
	 * 
	 * @param ref
	 *                  Description of Parameter
	 * @exception PooledConnectionException
	 *                        Description oaf Exception
	 * @since
	 */
	public SQLConnection(int ref) throws PooledConnectionException {
		this();
		this.ref = ref;
	}

	/**
	 * The test program for the SQLConnection class
	 * 
	 * @param args
	 *                  The command line arguments
	 * @since
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
			 * SQLConnection pc = new SQLConnection(USERNAME,PASSWORD,URL,
			 * DRIVER,1);
			 * 
			 * SystemResultSet srs = (SystemResultSet) pc.issueCommand("SELECT *
			 * FROM X"); System.out.println("Columns: "+ srs.getColumnCount());
			 * System.out.println("TRANSNAME:"+ srs.getField("IDCOL"));
			 * srs.close();
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

	public Connection getConnection() {
		allocated = true;
		try {
			System.out.println(new java.util.Date() + ": SQLConn :Getting Connection");
			if (stmt == null) {
				System.out.println(new java.util.Date() + ": SQLConn :Getting Statement");
				stmt = conn.createStatement();
			}
		} catch (Exception ex) {
		}
		return conn;
	}

	public boolean isConnected() {
		return connected;
	}

	public Object issueCommand(Object command) throws Exception {

		Integer DMLreturnval = null;
		try {
			if (DEBUG) {
				System.out.println(new java.util.Date() + ": SQLConn :\tCommand: " + command);
			}

			if (command instanceof String) {
				String strCommand = (String) command;
				if (strCommand.toUpperCase().indexOf("SELECT") != -1) {

					stmt = null;
					return new org.jini.projects.athena.resultset.SQLResultSet(conn.createStatement(), strCommand, this.ref);
				}
				if (stmt == null) {
					if (DEBUG) {
						System.out.println(new java.util.Date() + ": SQLConn :Recreating statement");
					}
					stmt = conn.createStatement();
					if (DEBUG) {
						System.out.println(new java.util.Date() + ": SQLConn :Statement built for connection " + this.ref);
					}
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

			}
			if (command instanceof org.jini.projects.athena.command.StdCommand) {
				org.jini.projects.athena.command.StdCommand sqlcomm = (org.jini.projects.athena.command.StdCommand) command;

				SQLCall sqlcall;
				System.out.println(new java.util.Date() + ": SQLConn : Got a SQL Command");
				sqlcall = new SQLCall(this.conn, sqlcomm);
				Object returnObj = sqlcall.execute();
				/*
				 * if (returnObj instanceof ResultSet) {
				 * System.out.println("Returning a SQLResultSet"); return new
				 * org.jini.projects.org.jini.projects.athena.resultset.SQLResultSet( }
				 */
				if (returnObj instanceof ArrayList) {
					//Assertion: A Vector of HashMaps i.e. a table block
					System.out.println(new java.util.Date() + ": SQLConn :Returning a DisconnectedResultSet");
					return new org.jini.projects.athena.resultset.DisconnectedResultSetImpl((ArrayList) returnObj, sqlcall.getHeader());
				}
				if (returnObj instanceof Integer) {
					System.out.println(new java.util.Date() + ": SQLConn :Returning a Hashmapped Integer");
					HashMap retval = new HashMap();
					retval.put("Modified", (Integer) returnObj);
					return retval;
				}
				if (returnObj instanceof Boolean) {
					System.out.println(new java.util.Date() + ": SQLConn :Returning a Hashmapped Integer");
					HashMap retval = new HashMap();
					retval.put("Success", (Boolean) returnObj);
					return retval;
				}
			}
		} catch (Exception ex) {
			this.autoAbort = true;
			System.out.println(new java.util.Date() + ": SQLConn :Err: " + ex.getMessage());
			log.log(Level.SEVERE, "Execption executing statement", ex);
			ex.printStackTrace();
			if (DEBUG) {
				System.out.println(new java.util.Date() + ": SQLConn :Closing statement");
			}
			if (this.errHandler != null) {
				errHandler.handleHostException(ex);
			}
			throw ex;
		}
		if (stmt != null) {
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

		try {
			if (command instanceof String) {
				String strCommand = (String) command;
				if (DEBUG) {
					System.out.println(new java.util.Date() + ": SQLConn :\tCommand: " + command);
					System.out.println(new java.util.Date() + ": SQLConn :\tNo of Parameters: " + params.length);
				}

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
						System.out.println("Vendor Error code: " + e.getErrorCode());
						if (e.getMessage().indexOf("Connection reset by peer") != -1) {
							System.out.println("A Connection problem!!!!");
							System.out.println("\n\n\t*****Withdraw all connections to oracle and bring service offline");
							System.out.println("\t*****Then add code to try to re-estacblish connection to oracle every n minutes");
							log.warning("Connection " + ref + ": Withdraw all connections to oracle and bring service offline");
							SystemManager.inform(HostEvents.DBCLOSED);
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
					return new Integer(pstmt.executeUpdate());
				}
				if (strCommand.toUpperCase().indexOf("UPDATE") != -1) {
					//System.err.println("Recognised UPDATE");
					return new Integer(pstmt.executeUpdate());
				}
				if (strCommand.toUpperCase().indexOf("DELETE") != -1) {
					//System.err.println("Recognised DELETE");

					return new Integer(pstmt.executeUpdate());
				}
			}
		} catch (Exception ex) {
			System.out.println(new java.util.Date() + ": SQLConn :Err: " + ex.getMessage());
			ex.printStackTrace();
			System.out.println(new java.util.Date() + ": SQLConn :Closing statement");
			pstmt.close();
			pstmt = null;
			if (this.errHandler != null) {
				errHandler.handleHostException(ex);
			}
			throw ex;
		} finally {
			//pstmt.close();
		}

		return null;
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
			System.out.println("Vendor Error code: " + e.getErrorCode());
			if (e.getMessage().indexOf("Connection reset by peer") != -1) {
				System.out.println("A Connection problem!!!!");
				System.out.println("\n\n\t*****Withdraw all connections to oracle and bring service offline");
				System.out.println("\t*****Then add code to try to re-estacblish connection to oracle every n minutes");

			}
			System.out.println("Logging to Eros");
			log.log(Level.WARNING, "Connection " + ref + ": Withdraw all connections to oracle and bring service offline", e);
			if (this.errHandler != null) {
				errHandler.handleHostException(e);
			}
			throw e;
		}
		return true;
	}

	public synchronized boolean rollback() throws Exception {
		try {
			conn.rollback();
		} catch (SQLException e) {

			System.out.println("Vendor Error code: " + e.getErrorCode());
			if (e.getMessage().indexOf("Connection reset by peer") != -1) {
				System.out.println("A Connection problem!!!!");
				System.out.println("\n\n\t*****Withdraw all connections to oracle and bring service offline");
				System.out.println("\t*****Then add code to try to re-estacblish connection to oracle every n minutes");
			}
			System.out.println("Logging to Eros");
			//log.log(Level.WARNING,"Connection " + ref + ":
			// Withdraw all connections to oracle and bring service offline",e)
			// ;
			log.info("Connection " + ref + ": Withdraw all connections to oracle and bring service offline");
			log.warning("Connection " + ref + ": Withdraw all connections to oracle and bring service offline");

			if (this.errHandler != null) {
				errHandler.handleHostException(e);
			}
			throw e;
		}
		catch (NullPointerException e){
			//Postgres will sometimes thow an NPE in 
			//org.postgresql.Connection.ExecSQL(Connection.java:312)
			// Don't know why - this just handles it
			//But check to see if conn is the source of NPE - if it is log error 
			if(conn!=null)	{			
				return true;
			}else {				
				if (e.getMessage().indexOf("Connection reset by peer") != -1) {
					System.out.println("A Connection problem!!!!");
					System.out.println("\n\n\t*****Withdraw all connections to oracle and bring service offline");
					System.out.println("\t*****Then add code to try to re-estacblish connection to oracle every n minutes");
				}
				System.out.println("Logging to Eros");
				//log.log(Level.WARNING,"Connection " + ref + ":
				// Withdraw all connections to oracle and bring service offline",e)
				// ;
				log.info("Connection " + ref + ": Withdraw all connections to oracle and bring service offline");
				log.warning("Connection " + ref + ": Withdraw all connections to oracle and bring service offline");

				if (this.errHandler != null) {
					errHandler.handleHostException(e);
				}
				throw e;
			}
		}
		return true;
	}

	public void connectTo(java.util.Properties connectionProperties) {
	}

	public void connectTo(String User, String Password, String URL, String Driver) throws PooledConnectionException {
		/*
		 * Allow the user to connect to another database if4.... Connection is
		 * not allocated to a client Connection is not connected If connection
		 * _is_ connected - i.e connected to a database but not allocated check
		 * if the connection can be freed before reconnecting to another db
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
				errHandler.handleHostException(ex);
			}
			throw new PooledConnectionException("[" + ex.getClass().getName() + "]: " + ex.getMessage());
		}
	}

	public void release() {
		if (DEBUG) {
			System.out.println(new java.util.Date() + ": SQLConn :Connection returned to pool");
		}
		//Ensure that all cursors are closed

		try {
			if (stmt != null) {
				if (DEBUG) {
					System.out.println(new java.util.Date() + ": SQLConn :Closing cursors");
				}
				stmt.close();
				stmt = null;
			}
			if (pstmt != null) {
				if (DEBUG) {
					System.out.println(new java.util.Date() + ": SQLConn :Closing cursors");
				}
				pstmt.close();
				pstmt = null;
			}
		} catch (Exception ex) {
			if (this.errHandler != null) {
				errHandler.handleHostException(ex);
			}
			System.err.println(new java.util.Date() + ": SQLConn : Cannot close statements for cursors (" + ex.getMessage() + ")");
			ex.printStackTrace();
		}
		stmt = null;
		pstmt = null;
		allocated = false;
		numalloc--;
		if (DEBUG) {
			System.out.println(new java.util.Date() + ": SQLConn :Deallocated: " + this.ref);
		}
	}

	public boolean canFree() {
		return canBeFreed;
	}

	public synchronized void allocate() {
		if (DEBUG) {
			System.out.println(new java.util.Date() + ": SQLConn : Allocated: " + this.ref);
		}
		allocated = true;
		numalloc++;
		if (numalloc > 1) {
			log.log(Level.SEVERE, "SQLConn :Allocated more than once!");
		}
	}

	public synchronized void close() throws AthenaException {
		try {
			if (stmt != null) {
				if (DEBUG)
					log.log(Level.FINE, " SQLConn :Closing a statement");
				stmt.close();
				stmt = null;
			} else {
				if (DEBUG) {
					log.log(Level.FINE, " SQLConn :Statement is null");
				}
			}
			if (pstmt != null) {
				if (DEBUG)
					log.log(Level.FINE, " SQLConn :Closing a PStatement");
				pstmt.close();
				pstmt = null;
			} else {
				if (DEBUG) {
					log.log(Level.FINE, "SQLConn :PStatement is null");
				}
			}
			conn.rollback();
			conn.close();
			this.connected = false;
		} catch (Exception ex) {
			if (this.errHandler != null) {
				errHandler.handleHostException(ex);
			}
			throw new AthenaException(ex);
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
			//PersistentFile = ServiceApplication.NAME + "CONN" + ref_index +
			// ".ser";
			stmt = conn.createStatement();
			connected = true;
		} catch (Exception ex) {
			if (this.errHandler != null) {
				errHandler.handleHostException(ex);
			}
			log.log(Level.SEVERE, "SQLConn.reConnect() :Indicating dropped Connection");
			allocated = false;
			connected = false;
			throw new AthenaException(new PooledConnectionException(ex.getMessage()));
		}

	}

	public void finalize() {

	}

	public Object handleType(Object in) throws AthenaException {
		return in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.org.jini.projects.athena.connection.SystemConnection#getErrorHandler()
	 */
	public HostErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub

		return null;
	}

	public boolean prepare() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearTransactionFlag() throws AthenaException {
		// TODO Auto-generated method stub
		txnID= null;
	}

}