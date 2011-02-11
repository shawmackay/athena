/*
 * SQLCall.java
 *
 * Created on 28 January 2002, 13:07
 */
package org.jini.projects.athena.connects.ctg.chi;

import java.util.HashMap;
import java.util.logging.Logger;

import org.jini.projects.athena.command.Call;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.command.dialect.DialectEngine;

import com.ibm.ctg.client.ECIRequest;
import com.ibm.ctg.client.JavaGateway;

/**
 * Call Wrapper for executing a CICS transaction through IBM
 * CICS Transaction Gateway
 *
 * @author calum
 *
 */
public class CTGCall implements Call {
    Logger l = Logger.getLogger("org.jini.projects.athena.command");
    JavaGateway jgate;
    Command command;
    ECIRequest req;
    Dialect dialect;

    /**
     * Creates a new instance of CTGCall
     *
     * @since
     */
    public CTGCall() {
    }

    /**
     * Constructor for the CTGCall object, that automatically selects the default CTG dialect for pre and post processing
     *
     *  @param jgate  The current gateway connection, that you want this command to execute under
     * @param req
     *                   host ECIRequest to flow thorugh the gateway
     * @param command
     *                   user supplied Command Object
     * @since
     */
    public CTGCall(JavaGateway jgate, ECIRequest req, Command command) {
        this.req = req;
        this.command = command;
        this.jgate = jgate;
    }

    /**
     * Creates a Call wrapper, for command execution using CTG
     * @param jgate The current gateway connection, that you want this command to execute under
     * @param req host ECIRequest to flow thorugh the gateway
     * @param command user supplied Command Object
     * @param dialect a dialect used for pre- and post-processing of parameters and commareas
     */
    public CTGCall(JavaGateway jgate, ECIRequest req, Command command, Dialect dialect) {
        this.req = req;
        this.command = command;
        this.jgate = jgate;
        this.dialect = dialect;
    }

    public HashMap getHeader() {
        return dialect.getOutputHeader();
    }

    public Object execute() throws Exception {

        try {
            if (dialect == null) {
                dialect = DialectEngine.getDialect(command.getCallName());
                l.finest("Dialect to be run is: " + dialect.getClass().getName());
                if (dialect == null) {
                    l.finest("Default Dialect Set");
                    dialect = DialectEngine.getDialect("DefaultCTG");
                    //((DefaultCTG_Dialect)
                    // dialect).setCallName(command.getCallName());
                }
            }

            dialect.init(new Object[]{req, command.getParameters()});
            l.finest("The program is set to: " + req.Program);
            req.Program = (String) command.getParameter("_ALIAS");
            l.finest("The program is now set to: " + req.Program);
            dialect.processInput();
            l.finest("Output:" + dialect.getCallInput());

            jgate.flow(req);
            l.finest("Beginning Output Processing");
            dialect.processOutput();
            return dialect.getCallOutput();
        } catch (Exception ex) {
            System.out.println("Err happened: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }

    }

}
