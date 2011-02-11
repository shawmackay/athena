/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 15:06:21
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.exception;
/**
 * Thrown when a connection cannot be obtained from the host, either because Athena is offline (because the database is offline)
 * or the connection pool cannot allocate an existing one.
 * @author calum
 *
 */
public class HostException extends Exception {

    public HostException() {
    }


    public HostException(String message) {
        super(message);
    }
}
