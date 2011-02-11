/*
 *  BEAJoltCall.java
 *
 *  Created on 10 September 2001, 13:14
 */
package org.jini.projects.athena.connects.jolt.chi;

import java.util.HashMap;

import org.jini.projects.athena.command.Call;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.command.dialect.DialectEngine;

import bea.jolt.JoltRemoteService;
import bea.jolt.JoltTransaction;

/**
 *  Executes and handles Calls against BEA Jolt 1.2 and then onto Tuxedo
 *  @author Calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class BEAJoltCall implements Call {
    Command joltCommand;
    JoltRemoteService joltService;
    JoltTransaction joltTransaction = null;
    Dialect dialect = null;


    /**
     *  Default Constructor
     *
     *@since
     */
    public BEAJoltCall() {
    }


    /**
     *  Passes the JoltService and JoltCommand as parameters, these are then sent
     *  through to the Dialect class
     *
     *@param  joltService  Instance of the service you wish to call
     *@param  joltCommand  <CODE>Command</CODE> Object implementing the paremeters
     *      that need to be placed in the joltService prior to <CODE>execute()</CODE>
     *@since
     */
    public BEAJoltCall(JoltRemoteService joltService, Command joltCommand) {
        this.joltService = joltService;
        this.joltCommand = joltCommand;
    }


    /**
     *  Passes the JoltService and JoltCommand as parameters, these are then sent
     *  through to the Dialect class. Also a transactional context is passed in so
     *  the call can be attributed as part of a global transaction, and rolled back
     *  or commited as necessary
     *
     *@param  joltService      Instance of the service you wish to call
     *@param  joltCommand      <CODE>Command</CODE> Object implementing the
     *      paremeters that need to be placed in the joltService prior to <CODE>execute()</CODE>
     *@param  joltTransaction  Global transaction instance
     *@since
     */
    public BEAJoltCall(JoltRemoteService joltService, Command joltCommand, JoltTransaction joltTransaction) {
        this.joltService = joltService;
        this.joltCommand = joltCommand;
        this.joltTransaction = joltTransaction;
    }

    public BEAJoltCall(JoltRemoteService joltService, Command joltCommand, Dialect dialect, JoltTransaction joltTransaction) {
        this.joltService = joltService;
        this.joltCommand = joltCommand;
        this.joltTransaction = joltTransaction;

        if (dialect != null) {
            this.dialect = dialect;
        }

    }

    /**
     *  Gets the header, which describes the field names and field index
     * @return table header
     */
    public HashMap getHeader() {
        return dialect.getOutputHeader();
    }


    public Object execute() throws Exception {

        try {
            if (dialect == null) {
                dialect = DialectEngine.getDialect(joltCommand.getCallName());
                if (dialect == null) {
                    dialect = DialectEngine.getDialect("JoltXSL");
                    ((JoltXSL_Dialect) dialect).setCallName(joltCommand.getCallName());
                }
            }
            dialect.init(new Object[]{joltService, joltCommand.getParameters()});
            dialect.processInput();
            joltService.call(joltTransaction);
            dialect.processOutput();
            return dialect.getCallOutput();
        } catch (Exception ex) {
            System.err.println("Err happened: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }

    }

}

