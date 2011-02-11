/*
 *  TESTP11_Dialect.java
 *
 *  Created on 11 September 2001, 10:04
 */
package org.jini.projects.athena.connects.sql.chi;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.dialect.DialectEngine;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class DefaultSQL_Dialect implements org.jini.projects.athena.command.dialect.Dialect {
    Vector returndata;
    private org.jini.projects.athena.command.Command command;
    private java.util.HashMap params_out = new HashMap();
    private HashMap header = new HashMap();
    private String SQLCommand = "";
    private ResultSet rset = null;
    private int EXECTYPE;


    /**
     *  Creates new DefaultSQL_Dialect
     *
     *@since
     */
    public DefaultSQL_Dialect() {
    }


    /**
     *  The test program for the DefaultSQL_Dialect class
     *
     *@param  args  The command line arguments
     *@since
     */
    public static void main(String[] args) {
        Connection conn = null;
        try {
            String URL = "jdbc:oracle:thin:@nts4_004.countrywide-assured.co.uk:1521:CMDB";
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(URL, "system", "calum");

            System.out.println("DialectEngine initialised @ " + new java.util.Date());
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("Getting JoltSession @ " + new java.util.Date());
        System.out.println("Building Parameters @ " + new java.util.Date());
        DefaultSQL_Dialect app = new DefaultSQL_Dialect();
        org.jini.projects.athena.command.StdCommand comm = new org.jini.projects.athena.command.StdCommand();
        comm.setCallName("INSERTX");
        TreeMap params = new TreeMap();
        params.put("transname", "a");
        params.put("entry", "b");
        params.put("results", "c");
        comm.setParameters(params);
        Object[] parms = {comm};
        app.init(parms);
        System.out.println("Processing input @ " + new java.util.Date());
        app.processInput();
        try {
            System.out.println(app.getCallInput());
            String call = (String) app.getCallInput();
            System.out.println("this will call oracle@ " + new java.util.Date());
            if (app.getExecutionType() == WRITE) {
                System.out.println("Doing an update");
                System.out.println("Return code" + conn.createStatement().executeUpdate(call.trim()));
            } else {
                System.out.println("Firing a query");
                ResultSet rset = conn.createStatement().executeQuery(call.trim());
                app.setCallReturn(rset);

                System.out.println("Processing output @ " + new java.util.Date());
                app.processOutput();
                System.out.println("Output processing finished @ " + new java.util.Date());
                System.out.println(app.getCallOutput());
            }
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("Finished");
    }


    /**
     *  Sets the callReturn attribute of the DefaultSQL_Dialect object
     *
     *@param  obj  The new callReturn value
     *@since
     */
    public void setCallReturn(Object obj) {
        rset = (ResultSet) obj;
    }


    /**
     *  Gets the callOutput attribute of the DefaultSQL_Dialect object
     *
     *@return    The callOutput value
     *@since
     */
    public Object getCallOutput() {
        if (returndata.size() > 0) {
            System.out.println("Returning a vector of size " + returndata.size());
            return returndata;
        } else {
            System.out.println("Returning a HashMap");
            return params_out;
        }
    }


    /**
     *  Gets the callInput attribute of the DefaultSQL_Dialect object
     *
     *@return    The callInput value
     *@since
     */
    public Object getCallInput() {
        return SQLCommand;
    }


    /**
     *  Gets the outputHeader attribute of the DefaultSQL_Dialect object
     *
     *@return    The outputHeader value
     *@since
     */
    public java.util.HashMap getOutputHeader() {

        System.out.println("Getting Dialect header");
        return header;
    }


    /**
     *  Gets the executionType attribute of the DefaultSQL_Dialect object
     *
     *@return    The executionType value
     *@since
     */
    public int getExecutionType() {
        //if (this.
        return EXECTYPE;
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void processOutput() {
        try {
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
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void processInput() {
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

    }


    /**
     *  Description of the Method
     *
     *@param  initials  Description of Parameter
     *@since
     */
    public void init(Object[] initials) {
        //   conn = (JoltRemoteService) initials[0];

        command = (Command) initials[1];
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

