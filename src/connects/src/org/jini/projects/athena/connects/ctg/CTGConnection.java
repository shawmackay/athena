package org.jini.projects.athena.connects.ctg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;


import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.StdCommand;
import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.connects.ctg.chi.CTGCall;
import org.jini.projects.athena.connects.ctg.syshandlers.CTGSysHandler;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.PooledConnectionException;
import org.jini.projects.athena.service.StatisticMonitor;


import com.ibm.ctg.client.*;

// Created as: 02-Jan-2003 : enclosing_type :CTGConnection.java
// In org.jini.projects.org.jini.projects.athena.connection

/**
 *  System Connection class representing a connection to a IBM CTG system.
 *  This class handles talking to IBM CICS Transaction Gateway.
 * @author calum
 */
public class CTGConnection implements SystemConnection {
    private JavaGateway javaGatewayObject;
    ECIRequest eciRequest = null;

    private static boolean DEBUG = (System.getProperty("org.jini.projects.athena.debug") != null ? true : false);
    private String strServerName = "NULL";
    private String strUserId = null;
    private byte[] abCommarea;
    private String strPassword = null;
    private Vector commands = new Vector();
    private String strJGateName;
    private int iJGatePort;
    private HostErrorHandler errHandler = new CTGSysHandler();

    public HostErrorHandler getErrorHandler() {
        return new CTGSysHandler();
    }

    private String strClientSecurity = null;
    private String strServerSecurity = null;

    private boolean allocated = false;
    private boolean connected = false;
    private boolean canBeFreed = false;
    private boolean autoAbort = false;
    private boolean inTxn = false;
    private int numalloc;
    private int ref;

    private String PersistentFile;

    public CTGConnection() throws PooledConnectionException {
        try {
            reConnect();
        } catch (Exception ex) {
            System.err.println("Indicating dropped Connection");
            allocated = false;
            connected = false;
            throw new PooledConnectionException(ex.getMessage());
        }
    }

    public CTGConnection(int ref) throws PooledConnectionException {
        this();
        setReference(ref);
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#getPersistentFileName()
     */
    public String getPersistentFileName() {
        return this.PersistentFile;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#commit()
     */
    public boolean commit() throws Exception {
        try {
            /**
             * Use the existing eciRequest since it contains
             * any relevant Luw_Token
             */
            eciRequest.Extend_Mode = ECIRequest.ECI_COMMIT;

            displayMsg("About to attempt a commit" + "\n" + "  Extend_Mode : " + eciRequest.Extend_Mode + "\n" + "  Luw_Token   : " + eciRequest.Luw_Token);

            javaGatewayObject.flow(eciRequest);

            displayRc(eciRequest);
        } catch (IOException eBack) {
            displayMsg("Exception during backout : " + eBack);
            throw eBack;
        }
        eciRequest = null;
        /*try {
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Vendor Error code: " + e.getErrorCode());
            if (e.getMessage().indexOf("Connection reset by peer") != -1) {
                System.out.println("A Connection problem!!!!");
                System.out.println("\n\n\t*****Withdraw all connections to oracle and bring service offline");
                System.out.println("\t*****Then add code to try to re-estacblish connection to oracle every n minutes");

            }
            System.out.println("Logging to Eros");
            SystemManager.LOG.log(Level.WARNING, "Connection " + ref + ": Withdraw all connections to oracle and bring service offline", e);
            if (this.errHandler != null) {
                errHandler.handleHostException(e);
            }
            throw e;
        }*/
        return true;

    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#rollback()
     */
    public boolean rollback() throws Exception {
        if ((javaGatewayObject != null) && (eciRequest != null)) {
            try {
                /**
                 * Use the existing eciRequest since it contains
                 * any relevant Luw_Token
                 */
                eciRequest.Extend_Mode = ECIRequest.ECI_BACKOUT;
                if (DEBUG)
                    displayMsg("About to attempt a backout" + "\n" + "  Extend_Mode : " + eciRequest.Extend_Mode + "\n" + "  Luw_Token   : " + eciRequest.Luw_Token);

                javaGatewayObject.flow(eciRequest);
                eciRequest = null;
                displayRc(eciRequest);
            } catch (IOException eBack) {
                displayMsg("Exception during backout : " + eBack);
                throw eBack;
            }
        }

        return true;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#issueCommand(java.lang.Object)
     */
    public Object issueCommand(Object command) throws Exception {
        Integer DMLreturnval = null;

        if (eciRequest == null) {

            if (DEBUG) {
                System.out.println("CREATING A REQUEST!!!");
            }

            //If this is in a transaction - set up an extended ECIRequest and store the LUW
            eciRequest = new ECIRequest(strServerName, // CICS Server
                    strUserId, // UserId, null for none
                    strPassword, // Password, null for none
                    null, // Program name
                    abCommarea, // Commarea
                    ECIRequest.ECI_EXTENDED,
                    ECIRequest.ECI_LUW_NEW);

        }

        try {
            commands.add(command);
            Vector table;
            if (DEBUG) {
                System.out.println("Command: [" + command.getClass().getName() + "]");
            }

            /*
             *
             * Change this to handle ECI - need new classes like CTGCall
             *
             */

            if (command instanceof org.jini.projects.athena.command.Command) {
                org.jini.projects.athena.command.Command cicsCommand = (org.jini.projects.athena.command.Command) command;
                if (DEBUG)
                    System.out.println("Obtained a remote call for [" + cicsCommand.getCallName() + "]");


                CTGCall cicsCall;
                if (DEBUG)
                    System.out.println("got a Std Command");
                if (cicsCommand.getParameter("_DIALECT") != null) {
                    Dialect dialect = (Dialect) cicsCommand.getParameter("_DIALECT");
                    if (DEBUG)
                        System.out.println("Prespecified Dialect is supplied");
                    cicsCommand.removeParameter("_DIALECT");
                    cicsCall = new CTGCall(javaGatewayObject, eciRequest, cicsCommand, dialect);
                } else
                    cicsCall = new CTGCall(javaGatewayObject, eciRequest, cicsCommand);
                if (DEBUG)
                    System.out.println("Trans: " + (eciRequest.Extend_Mode != ECIRequest.ECI_EXTENDED ? "NULL!" : "ALIVE"));
                Object returnObj = cicsCall.execute();
                if (eciRequest.getCicsRc() != ECIReturnCodes.ECI_NO_ERROR) {
                    Integer errorRC = new Integer(eciRequest.getCicsRc());
                    errHandler.handleHostException(errorRC);
                    Exception toThrow = throwOnRC(eciRequest.getCicsRc());
                    if (toThrow != null)
                        throw toThrow;
                }
                if (returnObj instanceof HashMap) {
                    if (DEBUG)
                        System.out.println("Returning a HashMap");
                    HashMap retval = (HashMap) returnObj;
                    return retval;
                }
                if (returnObj instanceof Vector) {
                    //Assertion: A Vector of HashMaps i.e. a table block
                    if (DEBUG)
                        System.out.println("Returning a Vector");
                    return new org.jini.projects.athena.resultset.VofHResultSet((Vector) returnObj, cicsCall.getHeader());
                }
            }
        } catch (Exception ex) {
            this.autoAbort = true;
            StatisticMonitor.addFailure();
            System.out.println("Sending Error to HostHandler");
            if (this.errHandler != null) {
                errHandler.handleHostException(ex);
            }
            throw ex;
        }
        return DMLreturnval;

    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#issueCommand(java.lang.Object, java.lang.Object[])
     */
    public Object issueCommand(Object command, Object[] params) throws Exception {
        return null;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#connectTo(java.util.Properties)
     */
    public void connectTo(Properties connectionprops) throws AthenaException {
    }

    private Exception throwOnRC(int errorRC) {
        //TODO: Throw an exception
        String data = null;
        switch (errorRC) {
            case ECIReturnCodes.ECI_ERR_SYSTEM_ERROR:
                data = "System Error - save and consult CTG Error log";
                break;
            case ECIReturnCodes.ECI_ERR_NO_CICS:
                data = "NO CICS available";
                break;
            case ECIReturnCodes.ECI_ERR_SECURITY_ERROR:
                data = "Security Error";
                break;
            case ECIReturnCodes.ECI_ERR_TRANSACTION_ABEND:
                data = "Transaction Abend";
                break;
            case ECIReturnCodes.ECI_ERR_RESOURCE_SHORTAGE:
                data = "Resource Shortage at CTG or Client Daemon";
                break;
            case ECIReturnCodes.ECI_ERR_REQUEST_TIMEOUT:
                data = "Request Timeout";
                break;
            case ECIReturnCodes.ECI_ERR_INVALID_EXTEND_MODE:
                data = "Invalid extend mode for LUW";
                break;
            case ECIReturnCodes.ECI_ERR_INVALID_DATA_LENGTH:
                data = "Commarea data length is invalid or does not match commarea size";
                break;
            case ECIReturnCodes.ECI_ERR_LUW_TOKEN:
                data = "LUW token is invalid";
                break;
            case ECIReturnCodes.ECI_ERR_ALREADY_ACTIVE:
                data = "Already Active - Call already in progress for an LUW (asynchronous)";
                break;
            case ECIReturnCodes.ECI_ERR_NO_SESSIONS:
                data = "No sessions - Number of outstanding LUW exceeded";
                break;
            case ECIReturnCodes.ECI_ERR_INVALID_DATA_AREA:
                data = "Comm area data area is invalid";
                break;
            case ECIReturnCodes.ECI_ERR_ROLLEDBACK:
                data = "Server unable to commit LUW so changes have been backed out";
                break;
        }
        if (data != null)
            return new AthenaException(data);
        else
            return null;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#reConnect()
     */
    public void reConnect() throws AthenaException {
        try {

            this.strUserId = null; //System.getProperty("org.jini.projects.athena.connect.username");
            this.strPassword = null;//System.getProperty("org.jini.projects.athena.connect.password");
            this.strServerName = System.getProperty("org.jini.projects.athena.connect.servername");
            this.strJGateName = System.getProperty("org.jini.projects.athena.connect.host");
            this.iJGatePort = Integer.parseInt(System.getProperty("org.jini.projects.athena.connect.port"));
            javaGatewayObject = new JavaGateway(strJGateName, iJGatePort, null, null);
            connected = true;
        } catch (IOException e) {
        }
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#isAllocated()
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#canFree()
     */
    public boolean canFree() {
        return false;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#allocate()
     */
    public void allocate() throws AthenaException {
        if (DEBUG) {
            System.out.println("Allocated: " + this.ref);
        }
        allocated = true;
        numalloc++;
        commands.clear();
        if (numalloc > 1) {
            System.out.println("Allocated more than once!");
        }
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#setTransactionFlag(boolean)
     */
    public void setTransactionFlag(boolean flag) throws AthenaException {
        this.inTxn = flag;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#inTransaction()
     */
    public boolean inTransaction() throws AthenaException {
        return inTxn;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#isConnected()
     */
    public boolean isConnected() {
        //return javaGatewayObject.isOpen();
        return connected;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#release()
     */
    public void release() throws AthenaException {
        if (DEBUG) {
            System.out.println("Connection returned to pool");
        }
        synchronized (this) {
            allocated = false;
            numalloc--;
        }
        if (DEBUG) {
            eciRequest = null;
            System.out.println("Deallocated: " + this.ref);
            try {
                this.javaGatewayObject.close();

            } catch (IOException e) {
                // TODO Handle IOException
                e.printStackTrace();
            }
            reConnect();
        }
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#close()
     */
    public void close() throws AthenaException {
        try {
            if (eciRequest != null)
                rollback();


        } catch (Exception e) {
            throw new AthenaException(e.getMessage());
        } finally {
            try {
                javaGatewayObject.close();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
        connected = false;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#isAutoAbortSet()
     */
    public boolean isAutoAbortSet() throws AthenaException {
        return this.autoAbort;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#setReference(int)
     */
    public void setReference(int ref) {
        PersistentFile = System.getProperty("org.jini.projects.athena.service.name") + "CONN" + ref + ".ser";
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#resetAutoAbort()
     */
    public void resetAutoAbort() throws AthenaException {
        autoAbort = false;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#getSystemCommand()
     */
    public Command getSystemCommand() throws AthenaException {
        return new StdCommand();
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#setAutoAbort()
     */
    public void setAutoAbort() {
        this.autoAbort = true;
    }

    /**
     * @see org.jini.projects.athena.connection.SystemConnection#handleType(java.lang.Object)
     */
    public Object handleType(Object in) throws AthenaException {
        return in;
    }

    /*private void boilerplate() {
            eciRequest = new ECIRequest(strServerName, // CICS Server
        strUserId, // UserId, null for none
        strPassword, // Password, null for none
        null, // Program name
        abCommarea, // Commarea
    ECIRequest.ECI_NO_EXTEND, ECIRequest.ECI_LUW_NEW);
        for (int iCallLoop = 0; iCallLoop < iNoOfProgNames; iCallLoop++) {
            eciRequest.Cics_Rc = 0;

            /**
                * Set the program name in the eciRequest
                *

            eciRequest.Program = astrProgNames[iCallLoop];
            eciRequest.Extend_Mode = ECIRequest.ECI_EXTENDED;

            /**
                * Flow the request via the JGate to CICS
                *

            displayMsg("About to call : " + eciRequest.Program);
            if (eciRequest.Commarea != null) {
                if (bDataConv) {
                    displayMsg("  Commarea    : " + new String(eciRequest.Commarea, "ASCII"));
                } else {
                    displayMsg("  Commarea    : " + new String(eciRequest.Commarea));
                }
            }
            displayMsg("  Extend_Mode : " + eciRequest.Extend_Mode + "\n" + "  Luw_Token   : " + eciRequest.Luw_Token);

            javaGatewayObject.flow(eciRequest);

            if (eciRequest.Commarea != null) {
                if (bDataConv) {
                    displayMsg("  Commarea    : " + new String(eciRequest.Commarea, "ASCII"));
                } else {
                    displayMsg("  Commarea    : " + new String(eciRequest.Commarea));
                }
            }

            displayRc(eciRequest);
        }

        /**
            * Commit the logical unit of work
            * eciRequest already contains LUW token unless above call failed.
            *
        if (eciRequest.Luw_Token != 0) {
            displayMsg("About to commit LUW");
            eciRequest.Cics_Rc = 0;
            eciRequest.Extend_Mode = ECIRequest.ECI_COMMIT;

            javaGatewayObject.flow(eciRequest);

            displayRc(eciRequest);
        }
    }*/

    void displayMsg(String message) {
        System.out.println(message);
    }

    void displayRc(ECIRequest eciRequest) {
        displayMsg("Return code   : " + eciRequest.getCicsRcString() + "(" + eciRequest.getCicsRc() + ")");
        displayMsg("Abend code    : " + eciRequest.Abend_Code);
    }

}
