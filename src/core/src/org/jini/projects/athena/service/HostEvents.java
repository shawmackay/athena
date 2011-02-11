/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 14:47:46
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.service;

public interface HostEvents {
    public static final int CONNCLOSED = 1;
    public static final int DBCLOSED = 2;
    public static final int DBREOPENED = 3;
    public static final int CONNDROP = 4;
    public static final int RESTART = 5;
    public static final int HIBERNATE = 6;
    public static final int WAKE = 7;
}
