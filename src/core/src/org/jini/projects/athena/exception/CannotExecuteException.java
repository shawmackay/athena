/*
 *  CannotExecuteException.java
 *
 *  Created on 16 October 2001, 14:09
 */
package org.jini.projects.athena.exception;

/**
 * Thrown if a command fails during it's execution, due to another exception type
 * such as <code>SQLException</code> or a failure in validation. This is thrown in
 * query mode, rather than when requesting an update.
 *
 *@author     calum
 *@version 0.9community * @see java.sql.SQLException
 */
public class CannotExecuteException extends java.lang.Exception {

    /**
     *  Creates new <code>CannotExecuteException</code> without detail message.
     *
     *@since
     */
    public CannotExecuteException() {
        super();
    }


    /**
     *  Constructs an <code>CannotExecuteException</code> with the specified detail
     *  message.
     *
     *@param  msg  the detail message.
     *@since
     */
    public CannotExecuteException(String msg) {
        super(msg);
    }
	/**
	 * @param arg0
	 * @param arg1
	 */
	public CannotExecuteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CannotExecuteException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}


