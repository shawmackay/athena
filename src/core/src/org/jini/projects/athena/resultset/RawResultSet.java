/*
 * RawResultSet.java
 *
 * Created on April 8, 2002, 10:42 AM
 */

package org.jini.projects.athena.resultset;

/**
 * Minimum datasets that could be stored in a cache
 * @author  calum
 */
public class RawResultSet {
    /**
     * The table of data
     */
    public java.util.ArrayList table;
    /**
     *The column details
     */
    public java.util.HashMap header;
}
