/*
 *  StatisticGroup.java
 *
 *  Created on 15 January 2002, 14:46
 */
package org.jini.projects.athena.monitors;

/**
 *  @author calum
 *
 *@author     calum
 *     05 March 2002
 *@version 0.9community */
public class StatisticGroup implements StatisticRow {

    boolean autoAggregate = false;

    StatisticRow datarow;

    String name;


    /**
     *  Creates new StatisticGroup
     *
     *@param  name  Description of Parameter
     *@param  row   Description of Parameter
     *@since
     */
    public StatisticGroup(String name, StatisticRow row) {
        this.name = name;
        this.datarow = row;
    }


    /**
     *  Constructor for the StatisticGroup object
     *
     *@since
     */
    public StatisticGroup() {
    }


    /**
     *  Sets the item attribute of the StatisticGroup object
     *
     *@param  name   The new item value
     *@param  value  The new item value
     *@since
     */
    public void setItem(String name, Object value) {
        datarow.setItem(name, value);
    }


    /**
     *  Gets the groupName attribute of the StatisticGroup object
     *
     *@return    The groupName value
     *@since
     */
    public String getGroupName() {
        return this.name;
    }


    /**
     *  Gets the item attribute of the StatisticGroup object
     *
     *@param  name  Description of Parameter
     *@return       The item value
     *@since
     */
    public Object getItem(String name) {
        return datarow.getItem(name);
    }


    /**
     *  Gets the itemTitles attribute of the StatisticGroup object
     *
     *@return    The itemTitles value
     *@since
     */
    public String[] getItemTitles() {
        return datarow.getItemTitles();
    }


    /**
     *  Description of the Method
     *
     *@param  autoagg  Description of Parameter
     *@since
     */
    public void autoAggregate(boolean autoagg) {
        autoAggregate = autoagg;
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void runAggregate() {
    }
}

