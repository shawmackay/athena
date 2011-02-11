/*
 *  Command.java
 *
 *  Created on 10 September 2001, 13:07
 */
package org.jini.projects.athena.command;

import java.util.TreeMap;

/**
 *  Represents the basic generic Command structure, that should be implemented
 *  for all SystemConnections. This allows a completely generic way of talking
 *  to datasources. 
 *
 *@author     calum
 */
public interface Command {
    /**
     *  Clears all the parameters associated with the command. By calling this
     *  method, after <CODE>executeQuery()</CODE> or <CODE>executeUpdate()</CODE>,
     *  a client can re-use this instance for a new call, without having to call
     *  <CODE>AthenaConnection.getCommand()</CODE> again
     *
     */
    public void clear();


    /**
     *  Get the CallName. Usually the name of the command and dialect that you want
     *  to ulitimately run against the datasource
     *
     *@return    The CallName value    
     */
    public String getCallName();


    /**
     *  Sets the CallName. Usually the name of the command and dialect that you
     *  want to ulitimately run against the datasource
     *
     *@param  callName  The name of the handler you wish to call
     *@since
     */
    public void setCallName(String callName);


    /**
     *  Gets all the current Parameters of the Command
     *
     *@return    Map containing key/value pairs of the surrent set parameters
     *     */
    public TreeMap getParameters();


    /**
     *  Sets the Parameters of the command. This will replace all current
     *  parameters
     *
     *@param  parameters  The new Parameters value
     
     */
    public void setParameters(TreeMap parameters);


    /**
     *  Sets the named Parameter to a value
     *
     *@param  name   The name used to identify the parameter 
     *@param  value  The value of the parameter     
     */
    public void setParameter(String name, Object value);


    /**
     *  Gets the named Parameter from the command
     *
     *@param  name  The key of the parameter you want to return
     *@return       The currently set value bound to the given name, or null     
     */
    public Object getParameter(String name);


    /**
     *  Removes the named parameter from the parameter list
     *
     *@param  name  Name of the parameter you want to remove
     *@since
     */
    public void removeParameter(String name);

    /**
     * Returns a compound type bound to the given name and pre inserted
     * into the parameter list
     * @param name
     * @return the new pre-bound compound type
     */
    public CompoundType createCompoundType(String name);

    /**
     * Returns a unbound array type, specific to this command type
     * @return a newly defined, unbound Compound Type 
     */
    public CompoundType getCompoundType();

    /**
     * Returns an array type bound to the given name and pre inserted
     * into the parameter list
     * @param name
     * @return the new pre-bound compound type
     */
    public Array createArray(String name);

    /**
     * Returns a unbound array type, specific to this command type
     * @return a newly defined, unbound Array
     */
    public Array getArray();
}

