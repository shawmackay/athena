/*
 *  MonitorableVector.java
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
public class MonitorableVector extends java.lang.Object implements Monitorable {

    private java.util.Vector StatisticData = new java.util.Vector();

    private int windowSize = 10;

    private StatisticRow record;


    /**
     *  Creates new MonitorableVector
     *
     *@since
     */
    public MonitorableVector() {
        record = new HashedStatisticRow();
    }


    /**
     *  Constructor for the MonitorableVector object
     *
     *@param  rollingWindowSize  Description of Parameter
     *@since
     */
    public MonitorableVector(int rollingWindowSize) {
        this();
        windowSize = rollingWindowSize;
        
    }


    /**
     *  Gets the statisticGroup attribute of the MonitorableVector object
     *
     *@param  groupname  Description of Parameter
     *@return            The statisticGroup value
     *@since
     */
    public StatisticGroup getStatisticGroup(String groupname) {
        Object x = record.getItem(groupname);
        if (x instanceof StatisticGroup) {
            return (StatisticGroup) x;
        } else {
            return null;
        }
    }


    /**
     *  Gets the statistic attribute of the MonitorableVector object
     *
     *@param  name  Description of Parameter
     *@return       The statistic value
     *@since
     */
    public Object getStatistic(String name) {
        return record.getItem(name);
    }


    /**
     *  Gets the statistics attribute of the MonitorableVector object
     *
     *@return    The statistics value
     *@since
     */
    public Object getStatistics() {
        return StatisticData;
    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@since
     */
    public void createStatisticGroup(String groupname) {
        record.setItem(groupname, new StatisticGroup(groupname, new HashedStatisticRow()));
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void checkpoint() {
        if (StatisticData.size() == this.windowSize) {
            StatisticData.remove(0);
        }
        synchronized (record) {
            StatisticData.add(record);
            record = new HashedStatisticRow();
        }
    }


    public void resize(int newWindowSize) {
        this.windowSize = newWindowSize;

    }

    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void decStatisticBy(String name, double value) {
        Double currentvalue = (Double) record.getItem(name);
        if (currentvalue == null) {
            record.setItem(name, new Double(value));
        } else {
            record.setItem(name, new Double(currentvalue.doubleValue() - value));
        }

    }


    /**
     *  Description of the Method
     *
     *@param  name  Description of Parameter
     *@since
     */
    public void incStatistic(String name) {
        Object x = record.getItem(name);
        if (x != null) {
            if (x instanceof Integer) {
                Integer value = (Integer) x;

                record.setItem(name, new Integer(value.intValue() + 1));

            } else {
                Double value = (Double) x;
                record.setItem(name, new Double(value.doubleValue() + 1.0));
            }
        } else {
            record.setItem(name, new Integer(1));
        }

    }


    /**
     *  Description of the Method
     *
     *@param  name  Description of Parameter
     *@since
     */
    public void decStatistic(String name) {
        Object x = record.getItem(name);
        if (x != null) {
            if (x instanceof Integer) {
                Integer value = (Integer) x;

                record.setItem(name, new Integer(value.intValue() - 1));

            } else {
                Double value = (Double) x;
                record.setItem(name, new Double(value.doubleValue() - 1.0));
            }
        } else {
            record.setItem(name, new Integer(-1));
        }

    }


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void incStatisticBy(String name, int value) {
        Integer currentvalue = (Integer) record.getItem(name);
        if (currentvalue == null) {
            record.setItem(name, new Integer(value));
        } else {
            record.setItem(name, new Integer(currentvalue.intValue() + value));
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void decStatisticBy(String name, int value) {
        Integer currentvalue = (Integer) record.getItem(name);
        if (currentvalue == null) {
            record.setItem(name, new Integer(value));
        } else {
            record.setItem(name, new Integer(currentvalue.intValue() - value));
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name   Description of Parameter
     *@param  value  Description of Parameter
     *@since
     */
    public void incStatisticBy(String name, double value) {
        Double currentvalue = (Double) record.getItem(name);
        if (currentvalue == null) {
            record.setItem(name, new Double(value));
        } else {
            record.setItem(name, new Double(currentvalue.doubleValue() + value));
        }

    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@since
     */
    public void incStatistic(String groupname, String name) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Integer value = (Integer) group.getItem(name);
                group.setItem(name, new Integer(value.intValue() + 1));
            } else {
                group.setItem(name, new Integer(1));
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void incStatisticBy(String groupname, String name, int value) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Integer ivalue = (Integer) group.getItem(name);
                group.setItem(name, new Integer(ivalue.intValue() + value));
            } else {
                group.setItem(name, new Integer(value));
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void incStatisticBy(String groupname, String name, double value) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Double ivalue = (Double) group.getItem(name);
                group.setItem(name, new Double(ivalue.doubleValue() + value));
            } else {
                group.setItem(name, new Double(+value));
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void decStatisticBy(String groupname, String name, int value) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Integer ivalue = (Integer) group.getItem(name);
                group.setItem(name, new Integer(ivalue.intValue() - value));
            } else {
                group.setItem(name, new Integer(-value));
            }
        }

    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@param  value      Description of Parameter
     *@since
     */
    public void decStatisticBy(String groupname, String name, double value) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Double ivalue = (Double) group.getItem(name);
                group.setItem(name, new Double(ivalue.doubleValue() - value));
            } else {
                group.setItem(name, new Double(-value));
            }
        }

    }


    /**
     *  Description of the Method
     *
     *@param  groupname  Description of Parameter
     *@param  name       Description of Parameter
     *@since
     */
    public void decStatistic(String groupname, String name) {
        StatisticGroup group = getStatisticGroup(groupname);
        if (group != null) {
            if (group.getItem(name) != null) {
                Integer ivalue = (Integer) group.getItem(name);
                group.setItem(name, new Integer(ivalue.intValue() - 1));
            } else {
                group.setItem(name, new Integer(-1));
            }
        }
    }

}

