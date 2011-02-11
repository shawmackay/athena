/*
 *  BEAJoltCommand.java
 *
 *  Created on 10 September 2001, 12:51
 */
package org.jini.projects.athena.connects.jolt.chi;

import java.util.TreeMap;
import java.util.Vector;

import org.jini.projects.athena.command.Array;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.CompoundType;
import org.jini.projects.athena.command.StdCommand;


/**
 *   Call Wrapper for executing against a Jolt tuxedo bridge datasource
 *
 *@author     calum
 *@version 0.9community */
public class BEAJoltCommand implements java.io.Serializable, Command {
    static final long serialVersionUID = -427778385564875537L;
    java.util.TreeMap parameters = new java.util.TreeMap();
    String callName;


    /**
     *  Creates new BEAJoltCommand
     */
    public BEAJoltCommand() {

    }


    /**
     *  Sets the Jolt callname
     *  This callname must be setup in the Jolt configuration system, see BEA
     *@param  callName  Sets the callname to <CODE>callName</CODE>
     */
    public BEAJoltCommand(String callName) {
        this.callName = callName;
    }

    /**
     *  Constructor for the BEAJoltCommand object
     *  This callname must be setup in the Jolt configuration system, see BEA
     * @param  callName  Sets the callname to <CODE>callName</CODE>
     */
    public void setCallName(String callName) {
        this.callName = callName;
    }


    public void setParameters(TreeMap parameters) {
        this.parameters = parameters;
    }

    public synchronized void setParameter(String name, Object value) {
        //This method will allow multiple occurences
        if (parameters.containsKey(name)) {
            Object x = parameters.get(name);
            if (x instanceof BEAJoltStorageVector) {
                BEAJoltStorageVector store = (BEAJoltStorageVector) x;
                store.add(value);
                parameters.put(name, store);
            } else {
                BEAJoltStorageVector store = new BEAJoltStorageVector();
                store.add(x);
                store.add(value);
                parameters.put(name, store);
            }
        } else {
            parameters.put(name, value);
        }
    }

    public String getCallName() {
        return this.callName;
    }

    public TreeMap getParameters() {
        return parameters;
    }

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public void clear() {
        parameters.clear();
    }

    public synchronized void removeParameter(String name) {
        parameters.remove(name);
    }

    public CompoundType createCompoundType(String name) {
        return null;
    }

    public CompoundType getCompoundType() {
        return null;
    }

    public Array createArray(String name) {
        return null;
    }

    public Array getArray() {
        return null;
    }


    //
    /**
     *  this class exists as a marker class for multiple occurences of parameters
     *  @author calum
     *
     *@author     calum
     *     09 October 2001
     */
    class BEAJoltStorageVector extends Vector {
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

}

