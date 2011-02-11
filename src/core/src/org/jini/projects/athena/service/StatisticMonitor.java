/*
 *  StatisticMonitor.java
 *
 *  Created on 03 October 2001, 10:48
 */
package org.jini.projects.athena.service;

import java.util.Vector;

import org.jini.projects.athena.connection.ConnectionPool;
import org.jini.projects.athena.monitors.MonitorableVector;

/**
 *  @author calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class StatisticMonitor {
    static int avgloop = 0;
    static long operations = 0;
    static long allocations = 0;
    static long txns = 0;
    static long failures = 0;
    static long totalConnections = 0;
    static long commits = 0;
    static long rollbacks = 0;
    //long totalAllocations=0;
    static int numtimes = 20;
    static MonitorableVector mvec;

    static long totalAllAllocations = 0;

    static float avgConnectionsPerMinute = 0.0f;
    static float avgConnectionsNeeded = 0.0f;
    static float avgTxnsPerMinute = 0.0f;
    static float avgTxnsPerAllocation = 0.0f;
    static float avgFailuresPerMinute = 0.0f;
    static float avgFailuresPerAllocation = 0.0f;
    static float avgOperationsPerMinute = 0.0f;
    static float avgOperationsPerAllocation = 0.0f;
    static ConnectionPool cpool = null;
    private static java.util.Vector details = new java.util.Vector();
    private static Monitor mon = new Monitor();
    private static long captureRate = 1000;
    private static int captureSize = 60;

    /**
     *  Constructor for the StatisticMonitor object
     *
     *@since
     */
    public StatisticMonitor() {
    }

    public static synchronized void setCaptureRate(long millis) {
        captureRate = millis;
    }

    /**
     *  Sets the ConnectionPool used for getting stats from
     *
     *@param  pool  The new ConnectionPool value
     *@since
     */
    public static synchronized void setConnectionPool(ConnectionPool pool) {
        cpool = pool;
    }


    /**
     *  Gets the set of Statistics
     *
     *@return    The Statistics value
     *@since
     */
    public static synchronized Vector getStatistics() {
        return (Vector) mvec.getStatistics();
    }


    /**
     *  Increments the operation count
     *
     *@since
     */
    public static synchronized void addOperation() {
        operations++;
        mvec.incStatistic("ops");
    }


    /**
     *  Increments the transaction count
     *
     *@since
     */
    public static synchronized void addTransaction() {
        txns++;
        mvec.incStatistic("txn");
    }


    /**
     *  Increments the rollbacks count
     *
     *@since
     */
    public static synchronized void addRollback() {
        rollbacks++;
        mvec.incStatistic("rollbacks");
    }


    /**
     *  Increments the commits count
     *
     *@since
     */
    public static synchronized void addCommit() {
        commits++;
        mvec.incStatistic("commits");
    }


    /**
     *  Increments the allocation count
     *
     *@since
     */
    public static synchronized void addAllocation() {
        allocations++;
        mvec.incStatistic("alloc");
    }


    /**
     *  Increments the failure count
     *
     *@since
     */
    public static synchronized void addFailure() {
        failures++;
        mvec.incStatistic("fail");
    }


    /**
     *  Adds a feature to the Connection attribute of the StatisticMonitor class
     *
     *@since
     */
    public static synchronized void addConnection() {
        mvec.incStatistic("conn");
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public static synchronized void removeConnection() {
        mvec.decStatistic("conn");
    }


    /**
     *  Internal System monitor. This class runs for the lifetime of the VM. It
     *  constantly re-evaluates all the statistics @author calum
     *
     *@author     calum
     *     09 October 2001
     */
    static class Monitor extends Thread {
        /**
         *  Main processing method for the Monitor object
         *
         *@since
         */
        public void run() {

            for (int i = 0; i < numtimes; i++) {
                for (; ;) {
                    numtimes++;
                    try {
                        sleep(captureRate);
                    } catch (Exception ex) {
                    }
                    mvec.checkpoint();
                }
            }
        }
    }


    static {        
        mvec = new MonitorableVector(captureSize);
    }

    static {
        mon.start();
    }
}

