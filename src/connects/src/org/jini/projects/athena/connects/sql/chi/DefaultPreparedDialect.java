/*
 *  TESTP11_Dialect.java
 *
 *  Created on 11 September 2001, 10:04
 */
package org.jini.projects.athena.connects.sql.chi;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.PreparedStatementParameter;
import org.jini.projects.athena.connection.ProcedureDefinition;
import org.jini.projects.athena.connection.ProcedureHandler;

/**
 * Handles dialect processing for Stored Procedures.<br>
 * Uses <code>ProcedureHandler</code>s for working out
 * mapping of fields, instead of XSL, unlike <code>DefaultCTG_Dialect</code> and others.
 */
public class DefaultPreparedDialect implements org.jini.projects.athena.command.dialect.Dialect {
    
    
    Vector returndata = new Vector();
    private Connection conn = null;
    private org.jini.projects.athena.command.Command command;
    private java.util.HashMap params_out = new HashMap();
    private HashMap header = new HashMap();
    PreparedStatement pstmt = null;
    private String SQLCommand = "";
    private ResultSet rset = null;
    private int EXECTYPE;
    private Logger logger;


    /**
     *  Creates new DefaultStoredProc_Dialect, associating it with the Connection to the database
     */
    public DefaultPreparedDialect(Connection conn) {
        this.conn = conn;
        this.logger = Logger.getLogger("org.jini.projects.athena.connects.sql.chi.Dialect");
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
        	System.out.println("Go: Executing");
            pstmt.execute();
            System.out.println("Go: Executed");
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
        	System.out.println("Calling process output");
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        if (pstmt != null) {
            logger.finest("Closing procedure resultset");
            try {
                pstmt.close();
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
            this.pstmt = conn.prepareStatement(sql);
//			OracleProcedureHandler prochand = new OracleProcedureHandler();
            logger.finest("COMMAND: " + command.getCallName());
                       
            List list = (List) command.getParameter("_PREPARE_PARAM_NAMES");
            Iterator iter = list.iterator();
            while(iter.hasNext()){
            	PreparedStatementParameter param = (PreparedStatementParameter) iter.next();            	
            	insertParameter(pstmt, param, command);
            }
           go();
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
    
    private void insertParameter(PreparedStatement stmt, PreparedStatementParameter param, Command comm)throws SQLException{
    	String type = param.getType();
    	String column = param.getColumnName();
    	int p_num = param.getParamNumber();
    	if(type.equals("string"))
    		stmt.setString(p_num,(String)comm.getParameter(column));
    	if(type.equals("short"))
    		stmt.setShort(p_num,((Short)comm.getParameter(column)).shortValue());
    	if(type.equals("int"))
    		stmt.setInt(p_num,((Integer)comm.getParameter(column)).intValue());
    	if(type.equals("long"))
    		stmt.setLong(p_num,((Long)comm.getParameter(column)).longValue());
    	if(type.equals("double"))
    		stmt.setDouble(p_num,((Double)comm.getParameter(column)).doubleValue());
    	if(type.equals("float"))
    		stmt.setFloat(p_num,((Float)comm.getParameter(column)).floatValue());
    	if(type.equals("byte"))
    		stmt.setByte(p_num,((Byte)comm.getParameter(column)).byteValue());
    	if(type.equals("boolean"))
    		stmt.setBoolean(p_num,((Boolean)comm.getParameter(column)).booleanValue());
    	if(type.equals("clob")){
    		String data = (String) comm.getParameter(column);
    		StringReader reader = new StringReader(data);
    		stmt.setCharacterStream(p_num,reader, data.length());
    	}
    	if(type.equals("blob")){
    		byte[] data = (byte[]) comm.getParameter(column);
    		ByteArrayInputStream bair = new ByteArrayInputStream(data);
    		stmt.setBinaryStream(p_num,bair, data.length);
    	}    	
    	if(type.equals("date")){
    		Date dt = (Date) comm.getParameter(column);    		
    		stmt.setDate(p_num,new java.sql.Date(dt.getTime()));
    	}
    		
    	if(type.equals("time")){
    		Date dt = (Date) comm.getParameter(column);    		
    		stmt.setTime(p_num,new java.sql.Time(dt.getTime()));
    	}
    	if(type.equals("timestamp")){
    		Date dt = (Date) comm.getParameter(column);    		
    		stmt.setTimestamp(p_num,new Timestamp(dt.getTime()));
    	}
    	
    }

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
//        if (command.getParameter("_ALIAS") != null)
//            procdef = prochand.getProcedure(((String) command.getParameter("_ALIAS")).trim());
//        else
//            procdef = prochand.getProcedure(command.getCallName().trim());

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
