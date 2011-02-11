/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 11:34:02
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.patterns;

/**
 * Basic chain of responsibility interface
 */
public interface Chain {
    /**
     * Add the next item into the chain
     */
    public void addChain(Chain c);

    /**
     * Either handle an object or move it along ot the next handler in the chain
     */
    public void sendToChain(Object mesg);

    /**
     * Get the next handler in the chain
     */
    public Chain getChain();
}
