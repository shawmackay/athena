
package org.jini.projects.athena.exception;

/**
 * Thrown if vaidation of a command fails
 *@author     calum
 */
public class ValidationException extends java.lang.Exception {

    /**
     *  Creates a new instance of <code>ValidationException</code> without detail
     *  message.
     *
     *@since
     */
    public ValidationException() {
    }


    /**
     *  Constructs an instance of <code>ValidationException</code> with the
     *  specified detail message.
     *
     *@param  msg  the detail message.
     *@since
     */
    public ValidationException(String msg) {
        super(msg);
    }
}


