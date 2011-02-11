/*
 *  HashedStatisticRow.java
 *
 *  Created on 15 January 2002, 15:22
 */
package org.jini.projects.athena.monitors;

import java.util.Iterator;
import java.util.Set;

/**
 *  @author calum
 *
 *@author     calum
 *     05 March 2002
 *@version 0.9community */


public class HashedStatisticRow extends java.util.HashMap implements StatisticRow {
    static final long serialVersionUID = -5190526887789060503L;

    /**
     *  Creates new HashedStatisticRow
     *
     *@since
     */
    public HashedStatisticRow() {
    }


    /**
     *  Sets the item attribute of the HashedStatisticRow object
     *
     *@param  title  The new item value
     *@param  value  The new item value
     *@since
     */
    public void setItem(String title, Object value) {
        put(title, value);
    }


    /**
     *  Gets the item attribute of the HashedStatisticRow object
     *
     *@param  title  Description of Parameter
     *@return        The item value
     *@since
     */
    public Object getItem(String title) {

        return get(title);
    }


    /**
     *  Gets the itemTitles attribute of the HashedStatisticRow object
     *
     *@return    The itemTitles value
     *@since
     */
    public String[] getItemTitles() {
        Set keys = this.keySet();
        String[] titles = new String[this.size()];
        int i = 0;
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            titles[i++] = (String) iter.next();
        }
        return titles;
    }

}

