/**
 *  Title: <p>
 *
 *  Description: <p>
 *
 *  Copyright: Copyright (c) <p>
 *
 *  Company: <p>
 *
 *  @author
 *
 *@version 0.9community */
package org.jini.projects.athena.exception;

/**
 *  Class representing a Standard Exception for handling other exceptions thrown
 *  by the Athena system
 *
 *@author     calum
 */
public class AthenaException extends Exception {
    String message = null;


    /**
     *  Standard Constructor
     *
     *@since
     */
    public AthenaException() {
    }


    /**
     *  Allows wrappering an existing Exception
     *
     *@param  ex  Exception whose message you want to absorbb
     *@since
     */
    public AthenaException(Exception ex) {
        message = ex.getMessage();
    }


    /**
     *  Creates a new instance with the given message
     *
     *@param  message  String to create Exception message
     *@since
     */
    public AthenaException(String message) {
        this.message = message;
    }


    /**
     *  get the message for this Exception
     *
     *@return    String summarising Exception thrown
     *@since
     */
    public String getMessage() {
        return message;
    }
}

