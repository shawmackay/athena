/*
 *  WrongReturnTypeException.java
 *
 *  Created on 31 January 2002, 10:16
 */
package org.jini.projects.athena.exception;

/**
 * Thrown when the data that Athena should return from an execution
 * is not an instance of a Resultset, when the client has issued ExecuteQuery(), rather
 * than ExecuteObjectQuery() and then casting it at the client
 *
 *@author     calum
 *
 */
public class WrongReturnTypeException extends java.lang.Exception {

    /**
     *  Creates a new instance of <code>WrongReturnTypeException</code> without
     *  detail message.
     *
     *@since
     */
    public WrongReturnTypeException() {
    }


    /**
     *  Constructs an instance of <code>WrongReturnTypeException</code> with the
     *  specified detail message.
     *
     *@param  msg  the detail message.
     *@since
     */
    public WrongReturnTypeException(String msg) {
        super(msg);
    }
}


