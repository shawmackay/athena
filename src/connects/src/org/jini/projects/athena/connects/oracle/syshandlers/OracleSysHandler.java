/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 11:29:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.oracle.syshandlers;

import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.patterns.Chain;

/**
 * Handles specific error codes gained from SQLExceptions from Oracle
 * Forwards the exception through a chain.
 */

public class OracleSysHandler implements HostErrorHandler {
    Chain oracleErrorChain;

    public OracleSysHandler() {
        oracleErrorChain = new MonitorOraExceptions();
        oracleErrorChain.addChain(new HandleDBShutdown());

    }

    /**
     * For a given Exception, take some remedial, or investigative action. <b>Note</b>: the Exception should
     * still be alive when this call is completed i.e.<br>
     * <pre>
     * try {
     * // Some error code here
     * } catch (MyHostException e) {
     *     System.out.println("Error:" + e.getMessage());
     *     myHostErrorHandler.handleHostException(e);
     *     e.printStackTrace();
     *     throw e;
     * }
     *</pre>
     * This may take the form of informaing the system that the database has been shutdown, or some other
     * error.
     */
    public void handleHostException(Object ex) {
        oracleErrorChain.sendToChain(ex);
    }


}
