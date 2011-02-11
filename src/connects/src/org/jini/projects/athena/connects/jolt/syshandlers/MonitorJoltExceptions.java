/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 11:49:33
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.jolt.syshandlers;

import org.jini.projects.athena.patterns.Chain;


import java.util.HashMap;

import bea.jolt.JoltException;

/**
 * Monitors the number of exceptions and types sent through the system
 */
public class MonitorJoltExceptions implements Chain {
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
        if (mesg instanceof JoltException) {
            JoltException joltex = (JoltException) mesg;
            String data = String.valueOf(joltex.getErrno()) + ": " + joltex.getMessage();
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
