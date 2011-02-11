/*
 *  Call.java
 *
 *  Created on 10 September 2001, 13:12
 */
package org.jini.projects.athena.command;

import java.util.HashMap;

/**
 *  Interface representing the basic needs of executing a command directly
 *  against a SystemConnection. @author Calum
 *
 *@author     calum
 *@version 0.9community */
public interface Call {
    /**
     *  Executes a raw command against a datasource. Similar to the <CODE>Command</CODE>
     *  Pattern
     *
     *@return                Object representing the raw data obtained from the
     *      call.
     *@exception  Exception  Thrown if an error occurs suring the execution of the call
    
     */
    public Object execute() throws Exception;


    /**
     *  Returns the field definitions associated with the returnvalue from <CODE>execute()</CODE>
     *  . Usually this delegates being defined through a Dialect.
     *
     *@return    Field definition information
     *@since
     */
    public HashMap getHeader();
}

