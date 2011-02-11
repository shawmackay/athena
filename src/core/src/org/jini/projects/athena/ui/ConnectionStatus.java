/*
 *  ConnectionStatus.java
 *
 *  Created on 09 August 2001, 11:14
 */
package org.jini.projects.athena.ui;

/**
 *  @author calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class ConnectionStatus {
    /**
     *  Description of the Field
     *
     *@since
     */
    public int index = 0;
    /**
     *  Description of the Field
     *
     *@since
     */
    public boolean connected = false;
    /**
     *  Description of the Field
     *
     *@since
     */
    public boolean allocated = false;
    /**
     *  Description of the Field
     *
     *@since
     */
    public boolean inTxn = false;


    public String Username = "none";

    /**
     *  Creates new ConnectionStatus
     *
     *@since
     */
    public ConnectionStatus() {
    }

}

