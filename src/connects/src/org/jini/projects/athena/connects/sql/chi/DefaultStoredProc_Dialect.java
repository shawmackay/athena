/*
 *  TESTP11_Dialect.java
 *
 *  Created on 11 September 2001, 10:04
 */
package org.jini.projects.athena.connects.sql.chi;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.ProcedureDefinition;
import org.jini.projects.athena.connection.ProcedureHandler;

/**
 * Handles dialect processing for Stored Procedures.<br>
 * Uses <code>ProcedureHandler</code>s for working out
 * mapping of fields, instead of XSL, unlike <code>DefaultCTG_Dialect</code> and others.
 */
public class DefaultStoredProc_Dialect implements org.jini.projects.athena.command.dialect.Dialect {
    ProcedureHandler prochand = ProcedureHandler.getInstance();
    ProcedureDefinition procdef;
    Vector returndata = new Vector();
    private Connection conn = null;
    private org.jini.projects.athena.command.Command command;
    private java.util.HashMap params_out = new HashMap();
    private HashMap header = new HashMap();
    CallableStatement cs = null;
    private String SQLCommand = "";
    private ResultSet rset = null;
    private int EXECTYPE;
    private Logger logger;


    /**
     *  Creates new DefaultStoredProc_Dialect, associating it with the Connection to the database
     */
    public DefaultStoredProc_Dialect(Connection conn) {
        this.conn = conn;
        this.logger = Logger.getLogger("org.jini.projects.athena.connects.oracle.chi.Dialect");
    }



    /**
     *  Sets the call return to a resultset
     *@param  obj  The new call return value (must be castable to java.sql.ResultSet)
     *@since
     */
    public void setCallReturn(Object obj) {
        rset = (ResultSet) obj;
    }

    /**
     * Returns either a Vector (table or traversed cursor) or a HashMap (single row, output parameters)
     *@return    The callOutput value
     *@since
     */
    public Object getCallOutput() {

        if (returndata.size() > 0) {
            return returndata;
        } else {
            return params_out;
        }
    }

    /**
     *  Returns the SQL statement used to in the call
     *
     *@return    The callInput value
     *@since
     */
    public Object getCallInput() {
        return SQLCommand;
    }

    /**
     *  Gets the output Header
     *
     *@return    The outputHeader value
     *@since
     */
    public java.util.HashMap getOutputHeader() {


        return header;
    }

    public void go() {
        try {
            cs.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  Gets the execution type
     *
     *@return    The executionType value     
     */
    public int getExecutionType() {
        //if (this.
        return EXECTYPE;
    }

    /**
     *  Processes the output and IN/out parameters from the stored procedure.
     
     */
    public void processOutput() {
        try {


            //procdef = prochand.setWorkingProcedure(command.getCallName());

            HashMap outparms = procdef.getOutParams();
            Iterator iter = outparms.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry ent = (Map.Entry) iter.next();
                ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();
                logger.finest("Getting: " + (String) ent.getKey() + "," + parm.colIndex);
                params_out.put((String) ent.getKey(), cs.getObject(parm.colIndex));
            }
            outparms = procdef.getInOutParams();
            iter = outparms.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry ent = (Map.Entry) iter.next();
                ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();
                logger.finest("Getting: " + (String) ent.getKey() + "," + parm.colIndex);
                params_out.put((String) ent.getKey(), cs.getObject(parm.colIndex));
            }

        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        if (cs != null) {
            logger.finest("Closing procedure resultset");
            try {
                cs.close();
            } catch (SQLException e) {
                logger.warning("Could not close procedure resultset: " + e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*
            Vector vec = new Vector();
            ResultSetMetaData meta = rset.getMetaData();
            while (rset.next()) {

                HashMap record = new HashMap();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    record.put(meta.getColumnName(i), rset.getObject(i));
                    header.put(meta.getColumnName(i), new Integer(i));
                }
                vec.add(record);
            }
            //Add params_out details here
        } catch (Exception ex) {

        }*/
    }

    /**
     *  Description of the Method
     *
     *@since
     */
    public void processInput() {
        try {
            int fieldLoop = 0;
            String sql = "";
            Object ob = this.command.getParameter("_BASESQL");
            if (ob instanceof String)
                sql = (String) ob;
            if (ob instanceof StringBuffer)
                sql = ((StringBuffer) ob).toString();

           logger.finest("SQL : " + sql);
            this.cs = conn.prepareCall(sql);
//			OracleProcedureHandler prochand = new OracleProcedureHandler();
            logger.finest("COMMAND: " + command.getCallName());

            HashMap outparms = procdef.getOutParams();
            Iterator iter = outparms.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry ent = (Map.Entry) iter.next();                
                ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();
                logger.finest("Registering OUT param: " + (String) ent.getKey() + "," + parm.colIndex);
                cs.registerOutParameter(parm.colIndex, parm.colType);
                header.put(ent.getKey(), new Integer(fieldLoop++));
            }
            outparms = procdef.getInOutParams();
            iter = outparms.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry ent = (Map.Entry) iter.next();
                ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();
                logger.finest("Registering INOUT param: " + (String) ent.getKey() + "," + parm.colIndex);
                cs.registerOutParameter(parm.colIndex, parm.colType);
                header.put(ent.getKey(), new Integer(fieldLoop++));
            }
            setInParams();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println("ERR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void setInParams() {
//		OracleProcedureHandler prochand = new OracleProcedureHandler();
//		prochand.setWorkingProcedure(command.getCallName());
        HashMap inparms = procdef.getInParams();
        Iterator iter = inparms.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry) iter.next();            
            ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();

            try {
                logger.finest("Setting IN param: " + (String) ent.getKey() + "," + parm.colIndex + ":" + command.getParameter((String) ent.getKey()));
                Object data = (command.getParameter((String) ent.getKey()) != null ? command.getParameter((String) ent.getKey()) : null);
                cs.setObject(parm.colIndex, data, parm.colType);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        inparms = procdef.getInOutParams();
        iter = inparms.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry) iter.next();
            
            ProcedureDefinition.StoredProcedureParameter parm = (ProcedureDefinition.StoredProcedureParameter) ent.getValue();
            try {
                System.out.println("Setting: INOUT param" + (String) ent.getKey() + "," + parm.colIndex+ ":" + command.getParameter((String) ent.getKey()));
                cs.setObject(parm.colIndex, command.getParameter((String) ent.getKey()), parm.colType);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /*
            Transformer processor = DialectEngine.getEngine().getInputTransform(command.getCallName());
            Set paramsSet = command.getParameters().entrySet();
            Iterator iter = paramsSet.iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                System.out.println("Adding " + (String) entry.getKey());
                processor.setParameter((String) entry.getKey(), entry.getValue());
            }
            StringBuffer outputBuffer = new StringBuffer();
            String wrapper = "<INPUT></INPUT>";
            StreamSource input = new StreamSource(new StringReader(wrapper));
            StringWriter outdest = new StringWriter();
            StreamResult output = new StreamResult(outdest);
            System.out.println("Starting input transform @ " + new java.util.Date());
            try {
                processor.transform(input, output);
            } catch (Exception ex) {
                System.out.println("Err: " + ex.getMessage());
                ex.printStackTrace();
            }

            System.out.println("Completed input transform @ " + new java.util.Date());
            System.out.println("Processed as:[" + outdest.getBuffer().toString() + "]");
            SQLCommand = outdest.getBuffer().toString();
            if (SQLCommand.indexOf("INSERT") != -1) {
                System.out.println("Setting command type to WRITE");
                this.EXECTYPE = WRITE;
            } else {
                this.EXECTYPE = READ;
            }
    */

    /**
     *  Description of the Method
     *
     *@param  initials  Description of Parameter
     *@since
     */
    public void init(Object[] initials) {
        //   conn = (JoltRemoteService) initials[0];
        conn = (Connection) initials[0];
        command = (Command) initials[1];
        if (command.getParameter("_ALIAS") != null)
            procdef = prochand.getProcedure(((String) command.getParameter("_ALIAS")).trim());
        else
            procdef = prochand.getProcedure(command.getCallName().trim());

    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallName(java.lang.String)
     */
    public void setCallName(String callName) {
    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallAlias(java.lang.String)
     */
    public void setCallAlias(String callAlias) {
    }

}
