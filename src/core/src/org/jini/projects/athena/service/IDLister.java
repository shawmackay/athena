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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;

/**
 *  Provides notification to System.out of receipt of Service ID @author calum
 *
 *@author     calum
 *     09 October 2001
 */
public class IDLister implements ServiceIDListener {

    /**
     *  Default Constructor
     *
     *@since
     */
    public IDLister() {
    }


    /**
     *  Callback from Lookup Service notifying Service of it's serviceID
     *
     *@param  serviceID  Service's Jini ID
     *@since
     */
    public void serviceIDNotify(ServiceID serviceID) {

        Logger.getLogger("org.jini.projects.athena.service").log(Level.FINE, "Service Registered as: " + serviceID.toString());
        JoinManagement.servID = serviceID;
        try {
            String svcname = System.getProperty("org.jini.projects.athena.service.name");
            AthenaLogger.persist(svcname + "servID.per", serviceID);
        } catch (Exception ex) {
        }
    }
}

