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
package org.jini.projects.athena.service;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Vector;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionManager;

/**
 *  Instances of this class are persisted for each connection. Upon creating
 *  aconnection during startup, these serialized instances are invaluable in the
 *  recovery procedure. Consequently, every operation made upon a connection is
 *  logged into an instance of this class, which is then serialized. @author
 *  calum
 *
 *@author     calum
 *     09 October 2001
 */
public class StatePersistence implements Serializable {
    /**
     *  No idea!
     *
     *@since
     */
    public int Current_State = 0;

    /**
     *  Description of the Field
     *
     *@since
     */
    public transient TransactionManager tm = null;

    /**
     *  The transaction reference for talking to a TransactionManager instance
     *  during recovery
     *
     *@since
     */
    public transient ServerTransaction tx = null;
    /**
     *  Whether the alwaysAbort flag was set (used in testing)
     *
     *@since
     */
    public boolean alwaysAbort = false;
    /**
     *  The serialized RMI reference object for the Transaction
     *
     *@since
     */
    public MarshalledObject cookedTX = null;
    /**
     *  Crash count, used in recovery
     *
     *@since
     */
    public int crashCount = 0;
    private Vector commands = new Vector();


    /**
     *  Default Constructor for the StatePersistence object
     *
     *@since
     */
    public StatePersistence() {
    }


    /**
     *  Sets the Transaction Manager for the Transaction
     *
     *@param  inTX             The new tx value
     *@exception  IOException  Description of Exception
     *@since
     */
    public void setTx(Transaction inTX) throws IOException {
        tx = (ServerTransaction) inTX;
        cookedTX = new MarshalledObject(inTX);
//        java.rmi.server.RemoteObject objRemote = (java.rmi.server.RemoteObject) inTX;
        //	java.rmi.server.RMIClassLoader.getClassAnnotation(objRemote.getClass());
    }


    /**
     *  Sets the State of the transaction at this point
     *
     *@param  state  The new State value
     *@since
     */
    public void setState(int state) {
        state = Current_State;
    }


    /**
     *  Gets the Command attribute of the StatePersistence object
     *
     *@param  i  Description of Parameter
     *@return    The Command value
     *@since
     */
    public Object getCommand(int i) {
        return commands.get(i);
    }


    /**
     *  Gets the number of commands currently stored
     *
     *@return    The StackSize value
     *@since
     */
    public int getStackSize() {
        return commands.size();
    }


    /**
     *  Gets the State identifier for the current transaction
     *
     *@return    The State value
     *@since
     */
    public int getState() {
        return Current_State;
    }


    /**
     *  Restores the Transaction Manager
     *
     *@exception  IOException             Description of Exception
     *@exception  ClassNotFoundException  Description of Exception
     *@since
     */
    public void restoreTX() throws IOException, ClassNotFoundException {
        if (cookedTX != null) {
            tx = (ServerTransaction) cookedTX.get();
            if (tx != null) {
                tm = tx.mgr;
            }
        }
    }


    /**
     *  Adds a command into the stack
     *
     *@param  command  The feature to be added to the Command attribute
     *@since
     */
    public void addCommand(Object command) {
        commands.add(command);
    }


    /**
     *  Removes all commands from the stack
     *
     *@since
     */
    public void clearStack() {
        commands.clear();
    }
}

