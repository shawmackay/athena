/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 11-Jun-02
 * Time: 13:53:20
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.ctg.syshandlers;

import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.patterns.Chain;

public class CTGSysHandler implements HostErrorHandler {

    Chain joltErrorChain;

    public CTGSysHandler() {
        joltErrorChain = new MonitorCTGExceptions();
        joltErrorChain.addChain(new HandleCTGShutdown());
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
     */
    public void handleHostException(Object ex) {

        joltErrorChain.sendToChain(ex);
    }
}
