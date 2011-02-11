/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 11:30:16
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connection;

/**
 * A host operation may throw a failure exception at any point,
 *  typically these exceptions are fairly generic, for instance
 * a SQLException is called whether a Query is wrong, or the database is dead.
 *  In the first scenario, remedial action would be requested from the user of the individual connection. however
 *  the second scenario wqould most likely affect all users of the system, with action being needed by sys admins.
 * Athena handles these situations by passing each exception through a chain of handlers, which may escalate the problem
 * to the more global classes, like the Join Manager or System Manager, or ConnectionPool classes to deal with on a broader scale.
 * In any case this is not meant to physically 'catch' the exception, but the Exception is meant to be thrown and caught in whatever means

 */
public interface HostErrorHandler {
    /**
     * For a given Exception, take some remedial, or investigative action. <b>Note</b>: the Exception should
     * still be alive when this call is completed i.e.<br>
     * <pre>
     * try {
     * // Some error code here
     * } catch (MyHostException e) {
     *     System.out.println("Error:" + e.getMessage());
     *     myHostErrorHandler.handleHostException(e);
     *     e.printStackTrace();
     *     throw e;
     * }
     *</pre>
     */
    public void handleHostException(Object ex);
}
