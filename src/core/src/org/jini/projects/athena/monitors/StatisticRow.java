/*
 *  StatisticRow.java
 *
 *  Created on 15 January 2002, 14:45
 */
package org.jini.projects.athena.monitors;

/**
 *  @author calum
 *
 *@author     calum
 *     05 March 2002
 *@version 0.9community */
public interface StatisticRow extends java.io.Serializable {
    static final long serialVersionUID = 5916032985601091322L;

    /**
     *  Gets the itemTitles attribute of the StatisticRow object
     *
     *@return    The itemTitles value
     *@since
     */
    public String[] getItemTitles();


    /**
     *  Gets the item attribute of the StatisticRow object
     *
     *@param  title  Description of Parameter
     *@return        The item value
     *@since
     */
    public Object getItem(String title);


    /**
     *  Sets the item attribute of the StatisticRow object
     *
     *@param  title  The new item value
     *@param  value  The new item value
     *@since
     */
    public void setItem(String title, Object value);

}

