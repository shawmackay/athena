/*
 *  EmptyResultSetException.java
 *
 *  Created on 28 September 2001, 12:03
 */
package org.jini.projects.athena.exception;

/**
 *  Thrown when the system will not return anything, when a query has been executed.
 *  This means that the query has executed properly, however, no rows have been returned to Athena.
 *  This allows Athena to shortcut the build of an empty resultset and the serialization overhead.
 */
public class EmptyResultSetException extends Exception {

    /**
     *  Creates new <code>EmptyResultSetException</code> without detail message.
     *
     *@since
     */
    public EmptyResultSetException() {
    }


    /**
     *  Constructs an <code>EmptyResultSetException</code> with the specified
     *  detail message.
     *
     *@param  msg  the detail message.
     *@since
     */
    public EmptyResultSetException(String msg) {
        super(msg);
    }
}


