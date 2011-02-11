/*
   SQLCall.java
 *
 *  Created on 28 January 2002, 13:07
 */
package org.jini.projects.athena.connects.sql.chi;

import java.sql.Connection;
import java.util.HashMap;

import org.jini.projects.athena.command.Call;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.command.dialect.DialectEngine;

/**
 * Call Wrapper for executing against a JDBC compliant datasource
 *
 *@author     calum
 *
 */
public class SQLCall implements Call {

    Command command;
    Connection conn;
    Dialect dialect;


    /**
     *  Creates a new instance of SQLCall
     *
     *@since
     */
    public SQLCall() {
    }


    /**
     *  Constructor for the SQLCall object
     *
     *@param  conn     host connection to run under
     *@param  command  user supplied Command Object
     *@since
     */
    public SQLCall(java.sql.Connection conn, Command command) {
        this.conn = conn;
        this.command = command;
    }


    /**
     *  Returns the field definitions associated with the returnvalue from <CODE>execute()</CODE>
     *  . Usually this delegates to Dialect.
     *
     *@return    Field definition information
     *@since
     */
    public HashMap getHeader() {
        return dialect.getOutputHeader();
    }


    /**
     *  Executes a raw command against a datasource. Similar to the <CODE>Command</CODE>
     *  Pattern
     *
     *@return                Object representing the raw data obtained from the
     *      call.
     *@exception  Exception  Description of Exception
     *@since
     */
    public Object execute() throws Exception {

        try {
            dialect = DialectEngine.getDialect(command.getCallName());
            if (dialect == null) {
                dialect = DialectEngine.getDialect("DefaultSQL");
            }
            System.out.println("Command: " + command);
            if(command.getParameter("_EXECTYPE").equals("prepare")){
            	System.out.println("Prepared dialect set");            
            	dialect = new DefaultPreparedDialect(this.conn);
            }
            dialect.init(new Object[]{conn, command});
            dialect.processInput();            
            //System.out.println("Output:" + dialect.getCallInput());
            /*if (dialect.getExecutionType() == Dialect.READ) {
                return stmt.executeQuery((String) dialect.getCallInput());

            }
            if (dialect.getExecutionType() == Dialect.WRITE) {
                return new Integer(stmt.executeUpdate((String) dialect.getCallInput()));
            }
            if (dialect.getExecutionType() == Dialect.PROGRAM) {
                return new Boolean(stmt.execute((String) dialect.getCallInput()));
            }*/
        } catch (Exception ex) {
            System.out.println("Err happened: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

}

