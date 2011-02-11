/*
 *  Monitorable.java
 *
 *  Created on 15 January 2002, 14:22
 */
package org.jini.projects.athena.monitors;

/**
 *  @author calum
 *
 *@author     calum
 *     05 March 2002
 *@version 0.9community */
public interface Monitorable {

    /**
     *  Gets the statistics attribute of the Monitorable object
     *
     *@return    The statistics value
     *@since
     */
    public Object getStatistics();


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@since
     */
    public void createStatisticGroup(String groupname);


    /**
     *  Gets the statisticGroup attribute of the Monitorable object
     *
     *@param  groupname  Description of Parameter
     *@return            The statisticGroup value
     *@since
     */
    public StatisticGroup getStatisticGroup(String groupname);


    /**
     *  Description of the Method
     *
     *@since
     */
    public void checkpoint();

    /**
     * Resizes the size of the monitor window
     * @param newWindowSize
     */
    public void resize(int newWindowSize);

    /**
     *  Description of the Method
     *
     *@param  name  Description of Parameter
     *@since
     */
    public void incStatistic(String name);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@since
     */
    public void incStatistic(String groupname, String name);


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void incStatisticBy(String name, int value);


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void incStatisticBy(String name, double value);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void incStatisticBy(String groupname, String name, int value);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void incStatisticBy(String groupname, String name, double value);


    /**
     *  Description of the Method
     *
     *@param  name  Description of Parameter
     *@since
     */
    public void decStatistic(String name);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@since
     */
    public void decStatistic(String groupname, String name);


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void decStatisticBy(String name, int value);


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void decStatisticBy(String name, double value);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void decStatisticBy(String groupname, String name, int value);


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void decStatisticBy(String groupname, String name, double value);

}

