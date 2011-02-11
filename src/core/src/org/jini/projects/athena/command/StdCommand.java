package org.jini.projects.athena.command;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *   Command Wrapper that uses only single item names. This class could be used by other Connection types
 * Some datasources allow folding of named items, where an item is added once, it is a singular entity, when it is added again, it is not overwritten, but
 * the items are folded together into an array 
 *@author     calum
 *     05 March 2002
 */

public class StdCommand extends java.lang.Object implements Command, java.io.Serializable {
    static final long serialVersionUID = 1307094549230011620L;
    java.util.TreeMap parameters = new java.util.TreeMap();
    String callName;


    /*
	 *  constructor
	 */
    /**
     *  Create an empty Command instance
     *
     *@since 0.9community
     */
    public StdCommand() {
    }


    /**
     *  Sets the named Parameter to a value
     *
     *@param  name   The Parameter name
     *@param  value  The Parameter value
     *@since
     */
    public void setParameter(String name, Object value) {
        parameters.put(name.toLowerCase(), value);
    }


    /**
     *  Sets the Parameters of the command. This iwll replace all current
     *  parameters
     *
     *@param  parameters  The new Parameters value
     *@since
     */
    public void setParameters(TreeMap parameters) {
        this.parameters = parameters;
    }


    /**
     *  Sets the CallName. Usually the name of the command and dialect that you
     *  want to ultimately run against the datasource
     *
     *@param  callName  The new CallName value
     *@since
     */
    public void setCallName(String callName) {
        this.callName = callName;
    }


    /**
     *  Gets all the current Parameters of the Command
     *
     *@return    The Parameters value
     *@since
     */
    public TreeMap getParameters() {
        return parameters;
    }


    /**
     *  Gets the named Parameter from the command
     *
     *@param  name  The key of the parameter you want to return
     *@return       The Parameter name as the argument
     *@since
     */
    public Object getParameter(String name) {
        try {
            return parameters.get(name.toLowerCase());
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     *  Get the CallName. Usually the name of the command and dialect that you want
     *  to ulitimately run against the datasource
     *
     *@return    The CallName value
     *@since
     */
    public String getCallName() {
        return this.callName;
    }


    /**
     *  Clears all the parameters associated with the command. By calling this
     *  method, after <CODE>executeQuery()</CODE> or <CODE>executeUpdate()</CODE>,
     *  a client can re-use this instance for a new call, without having to call
     *  <CODE>AthenaConnection.getCommand()</CODE> again
     *
     *@since
     */
    public void clear() {
        parameters.clear();
    }


    /**
     *  Removes the named parameter from the parameter list
     *
     *@param  name  Key of the parameter you want removing
     *@since
     */
    public void removeParameter(String name) {
        parameters.remove(name);

    }

    public CompoundType createCompoundType(String name) {
        StdCompoundType comptype = new StdCompoundType();
        parameters.put(name.toLowerCase(), comptype);
        return comptype;

    }

    public CompoundType getCompoundType() {
        return new StdCompoundType();
    }

    public Array createArray(String name) {
        StdArray arr = new StdArray();
        parameters.put(name.toLowerCase(), arr);
        return arr;
    }

    public Array getArray() {
        return new StdArray();
    }

    public boolean equals(Object obj) {
        if (obj instanceof StdCommand) {
            Command comm = (Command) obj;
            if (this.getCallName().equals(comm.getCallName()))
                if (this.getParameters().equals(comm.getParameters()))
                    return true;
        }
        return false;
    }

    public String toString(){
    	StringBuffer buff = new StringBuffer();
		try {
		buff.append(this.callName +"(");
        for(Iterator iter = parameters.entrySet().iterator();iter.hasNext();){
        	Map.Entry entry  = (Map.Entry) iter.next();
        	if (entry.getValue()==null)
        		buff.append("[" + entry.getKey() + ": null]");
        	else
        		buff.append("[" + entry.getKey() + ": " + entry.getValue().toString() + "]");
            if(iter.hasNext())
                buff.append(",");
        }
         buff.append(")");
		} catch (Exception ex) {
			System.out.println("Exception in toString(): " + ex.getMessage());
			ex.printStackTrace();
			System.out.println(buff.toString());
		}
         return buff.toString();
    }

}

