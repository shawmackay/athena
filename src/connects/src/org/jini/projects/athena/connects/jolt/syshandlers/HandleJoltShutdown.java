/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 12:06:43
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.jolt.syshandlers;

import org.jini.projects.athena.patterns.Chain;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.SystemManager;
import bea.jolt.ApplicationException;
import bea.jolt.JoltException;


public class HandleJoltShutdown implements Chain {
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
        boolean handled = false;
        if (mesg instanceof JoltException) {
            JoltException joltex = (JoltException) mesg;
            System.out.println("Exception Message is: " + joltex.getMessage());

            if (joltex.getErrno() == JoltException.TPESVCFAIL) {
                if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                    System.out.println("Service Failure error ");
                    System.out.println("This will cause the system to shutdown!!!!!!");
                    SystemManager.inform(HostEvents.DBCLOSED);
                    handled = true;
                }
            }
            if (joltex.getErrno() == JoltException.TPESYSTEM) {
                if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                    System.out.println("System Failure");
                    System.out.println("System is being brought offline!!!!!");
                    SystemManager.inform(HostEvents.DBCLOSED);
                    handled = true;
                }
            }
            if (joltex.getMessage().indexOf("Connection send error") != -1) {
                if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                    System.out.println("Jolt is unavailable");
                    SystemManager.inform(HostEvents.DBCLOSED);
                    handled = true;
                }
            }
            if (!handled)
                System.out.println("Unknown error : " + joltex.getErrno() + " " + joltex.getMessage());
        }
        if (mesg instanceof ApplicationException) {
            ApplicationException ex = (ApplicationException) mesg;
            if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                System.out.println("System Failure");
                System.out.println("System is being brought offline!!!!!");
                SystemManager.inform(HostEvents.DBCLOSED);
                handled = true;
            }
        } else if (!handled)
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
