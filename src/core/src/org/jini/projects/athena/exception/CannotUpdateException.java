/*
 *  CannotUpdateException.java
 *
 *  Created on 16 October 2001, 14:08
 */
package org.jini.projects.athena.exception;

/**
 * Thrown if a command fails during it's execution, due to another exception type
 * such as <code>SQLException</code> or a failure in validation. This is thrown in
 * update mode, generally signalling that the client should rollback the txn, if there is one.
 */
public class CannotUpdateException extends java.lang.Exception {

    /**
     *  Creates new <code>CannotUpdateException</code> without detail message.
     *
     *@since
     */
    public CannotUpdateException() {
        super();
    }


    /**
     *  Constructs an <code>CannotUpdateException</code> with the specified detail
     *  message.
     *
     *@param  msg  the detail message.
     *@since
     */
    public CannotUpdateException(String msg) {
        super(msg);
    }
	/**
	 * @param arg0
	 * @param arg1
	 */
	public CannotUpdateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CannotUpdateException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}


