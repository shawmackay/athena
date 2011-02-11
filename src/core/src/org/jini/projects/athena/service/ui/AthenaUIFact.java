/*
 *  AthenaUIFact.java
 *
 *  Created on 03 October 2001, 12:04
 */
package org.jini.projects.athena.service.ui;

import javax.swing.JComponent;

import net.jini.core.lookup.ServiceItem;

/**
 *  @author calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class AthenaUIFact implements net.jini.lookup.ui.factory.JComponentFactory, java.io.Serializable {

    /**
     *  Creates new AthenaUIFact
     *
     *        */
    public AthenaUIFact() {
    }


    /**
     *  Returns a <CODE>JComponent</CODE> .
     *
     *@param  roleObject  Description of Parameter
     *@return             The JComponent value
     *        */
    public JComponent getJComponent(Object roleObject) {
        ServiceItem svItem = (ServiceItem) roleObject;
        AthenaPanel atp = new AthenaPanel((org.jini.projects.athena.service.AthenaRegistration) svItem.service);
        return atp;
    }

}

