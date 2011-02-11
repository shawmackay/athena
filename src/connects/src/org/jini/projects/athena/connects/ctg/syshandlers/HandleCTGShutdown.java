/*
 * Created by IntelliJ IDEA. User: calum Date: 06-Jun-02 Time: 12:06:43 To
 * change template for new class use Code Style | Class Templates options
 * (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.ctg.syshandlers;

import org.jini.projects.athena.patterns.Chain;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.SystemManager;

import com.ibm.ctg.client.ECIRequest;
import com.ibm.ctg.client.ECIReturnCodes;

public class HandleCTGShutdown implements Chain {
    Chain nextInChain;

    /**
     * Add the next item into the chain
     */
    public void addChain(Chain c) {
        nextInChain = c;
    }

    /**
     * Either handle an object or move it along ot the next handler in the
     * chain
     */
    public void sendToChain(Object mesg) {
        boolean handled = false;
        if (mesg instanceof ECIRequest) {
            ECIRequest request = (ECIRequest) mesg;
            if (request.Cics_Rc == ECIRequest.ECI_ERR_CICS_DIED) {
                System.out.println("CICS DIED");
            }
            if (request.Cics_Rc == ECIRequest.ECI_ERR_NO_CICS) {
                System.out.println("CICS IS NOT AVAILABLE");
            }
            if (request.Cics_Rc == ECIRequest.ECI_ERR_TRANSACTION_ABEND) {
                System.out.println("Transaction abended");
            }
            if (request.Cics_Rc == ECIRequest.ECI_ERR_LUW_TOKEN) {
                System.out.println("Bad LUW token");
            }
            if (request.Cics_Rc == ECIRequest.ECI_ERR_INVALID_DATA_LENGTH) {
                System.out.println("Invalid Data Length");
            }
        }
        if (mesg instanceof Integer) {
            int errorRC = ((Integer) mesg).intValue();
            String data = null;
            switch (errorRC) {
                case ECIReturnCodes.ECI_ERR_SYSTEM_ERROR:
                    data = "System Error - save and consult CTG Error log";
                    if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                        System.out.println("************ THE SYSTEM IS GOING OFFLINE **********");
                        System.out.println("Network error :" + data);
                        System.out.println("This will cause the system to shutdown!!!!!!");
                        SystemManager.inform(HostEvents.DBCLOSED);
                    }
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
                //TODO : Check error codes to see if system needs hibernating
            }
        }
        /*
		 * if (joltex.getErrno() == JoltException.TPESVCFAIL) { if
		 * (SystemManager.getSystemState() == SystemManager.ONLINE) {
		 * System.out.println("Service Failure error ");
		 * System.out.println("This will cause the system to shutdown!!!!!!");
		 * SystemManager.inform(HostEvents.DBCLOSED); handled = true; } } if
		 * (joltex.getErrno() == JoltException.TPESYSTEM) { if
		 * (SystemManager.getSystemState() == SystemManager.ONLINE) {
		 * System.out.println("System Failure"); System.out.println("System is
		 * being brought offline!!!!!");
		 * SystemManager.inform(HostEvents.DBCLOSED); handled = true; } } if
		 * (joltex.getMessage().indexOf("Connection send error") != -1) { if
		 * (SystemManager.getSystemState() == SystemManager.ONLINE) {
		 * System.out.println("Jolt is unavailable");
		 * SystemManager.inform(HostEvents.DBCLOSED); handled = true; } } if
		 * (!handled) System.out.println("Unknown error : " + joltex.getErrno() + " " +
		 * joltex.getMessage()); } if(mesg instanceof ApplicationException) {
		 * ApplicationException ex = (ApplicationException) mesg; if
		 * (SystemManager.getSystemState() == SystemManager.ONLINE) {
		 * System.out.println("System Failure"); System.out.println("System is
		 * being brought offline!!!!!");
		 * SystemManager.inform(HostEvents.DBCLOSED); handled = true; }
		 */
        else if (!handled)
            System.out.println("\t\tUnknown Obj: " + mesg.getClass().getName());
        if (nextInChain != null) {
            nextInChain.sendToChain(mesg);
        }

    }

    /**
     * Get the next handler in the chain
     */
    public Chain getChain() {
        return nextInChain;
    }
}
