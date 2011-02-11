/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 06-Jun-02
 * Time: 12:06:43
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.connects.oracle.syshandlers;

import java.sql.SQLException;

import org.jini.projects.athena.patterns.Chain;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.athena.util.builders.LogExceptionWrapper;

public class HandleDBShutdown implements Chain {
    Chain nextInChain;

    public HandleDBShutdown() {
        addChain(new LogErrors());
    }

    /**
     * Add the next item into the chain
     */
    public void addChain(Chain c) {
        nextInChain = c;
    }

    /**
     * Either handle an object or move it along ot the next handler in the chain
     */
    public void sendToChain(Object mesg) {
        if (mesg instanceof SQLException) {
            SQLException sqlex = (SQLException) mesg;
            handleSQLEx(sqlex);
        }
        if (mesg instanceof LogExceptionWrapper) {


            LogExceptionWrapper lew = (LogExceptionWrapper) mesg;
            if (lew.getCause() instanceof SQLException)
                handleSQLEx((SQLException) lew.getCause());
        }
        if (nextInChain != null) {
            nextInChain.sendToChain(mesg);
        }

    }


    private void handleSQLEx(SQLException sqlex) {
        if (sqlex.getErrorCode() == 17002 || sqlex.getErrorCode() == 17410
                || (sqlex.getMessage().indexOf("Connection reset by peer") != -1) ||
                (sqlex.getMessage().indexOf("Connection closed") != -1) ||
                (sqlex.getMessage().indexOf("Closed Connection") != -1) ||
                (sqlex.getMessage().indexOf("No more data to read from socket") != -1) ||
                (sqlex.getMessage().indexOf("Broken pipe") != -1)
        ) {
            if (SystemManager.getSystemState() == SystemManager.ONLINE) {
                System.out.println("************ THE SYSTEM IS GOING OFFLINE **********");
                System.out.println("Network error :" + sqlex.getMessage());
                System.out.println("This will cause the system to shutdown!!!!!!");
                SystemManager.inform(HostEvents.DBCLOSED);
            }
        } else
            System.out.println("Unknown error : " + sqlex.getErrorCode());

    }

    /**
     * Get the next handler in the chain
     */
    public Chain getChain() {
        return nextInChain;
    }
}
