/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 11:49:33
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.ctg.syshandlers;

import java.util.HashMap;

import org.jini.projects.athena.patterns.Chain;

import com.ibm.ctg.client.ECIReturnCodes;

/**
 * Monitors the number of exceptions and types sent through the system
 */
public class MonitorCTGExceptions implements Chain {
    HashMap details = new HashMap();
    Chain nextInChain;

    /**
     * Add the next item into the chain
     */
    public void addChain(Chain c) {
        nextInChain = c;
    }

    /**
     * Either handle an object or move it along ot the next handler in the chain
     */
    public void sendToChain(Object mesg) {
        if (mesg instanceof Integer) {
            int errorRC = ((Integer) mesg).intValue();
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
            if (data != null) {
                System.out.println("CICS ERR: " + data);
                if (details.containsKey(data)) {
                    int count = ((Integer) details.get(data)).intValue() + 1;
                    details.put(data, new Integer(count));
                } else
                    details.put(data, new Integer(1));
            }
        }
        if (mesg instanceof Exception) {
            Exception ctgex = (Exception) mesg;
            String data = ctgex.getMessage();
            if (details.containsKey(data)) {
                int count = ((Integer) details.get(data)).intValue() + 1;
                details.put(data, new Integer(count));
            } else
                details.put(data, new Integer(1));
        }
        //Always forward along the chain
        if (nextInChain != null)
            nextInChain.sendToChain(mesg);

    }

    /**
     * Get the next handler in the chain
     */
    public Chain getChain() {
        return nextInChain;
    }
}
