/*
 *  AthenaServiceType.java
 *
 *  Created on 11 October 2001, 10:01
 */
package org.jini.projects.athena.service;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Provides some inframtion about Athena for service browsers.
 *  @author calum
 */
public class AthenaServiceType extends net.jini.lookup.entry.ServiceType {


    /**
     *  Creates new AthenaServiceType
     *
     *@since
     */
    public AthenaServiceType() {
    }


    /**
     *  Gets the displayName attribute of the AthenaServiceType object
     *
     *@return    The displayName value
     *@since
     */
    public java.lang.String getDisplayName() {
        return "Athena";
    }


    /**
     *  Gets the icon attribute of the AthenaServiceType object
     *
     *@param  param  Description of Parameter
     *@return        The icon value
     *@since
     */
    public java.awt.Image getIcon(int param) {
        ImageIcon imic = new ImageIcon(this.getClass().getResource("athena.gif"));
        ImageIcon imicmono = new ImageIcon(this.getClass().getResource("athenamono.gif"));
        if (param == java.beans.BeanInfo.ICON_COLOR_16x16) {
            return imic.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        }
        if (param == java.beans.BeanInfo.ICON_COLOR_32x32) {
            return imic.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        }

        if (param == java.beans.BeanInfo.ICON_MONO_16x16) {
            return imicmono.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        }
        if (param == java.beans.BeanInfo.ICON_MONO_32x32) {
            return imicmono.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        }
        return imic.getImage();
    }


    /**
     *  Gets the shortDescription attribute of the AthenaServiceType object
     *
     *@return    The shortDescription value
     *@since
     */
    public java.lang.String getShortDescription() {
        return "Allows transactional systems to take part in Distributed Transactions ";
    }

}

