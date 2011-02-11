/*
 * ResultSetUpdater.java
 *
 * Created on April 8, 2002, 11:01 AM
 */

package org.jini.projects.athena.resultset;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.connection.RemoteConnection;
import org.jini.projects.athena.connection.RemoteConnectionImpl;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.EmptyResultSetException;

/**
 * A Dynamic cache updater.This holds a single connection through which all ResultSet cache updates will occur<br>
 *This connection is not part of the Connection pool, so ensure that at lease one more connections is availabel for your chosen user
 * @author  calum
 */
public class ResultSetUpdater implements org.jini.projects.athena.resources.Updater {
    RemoteConnection sconn;
    
    static SystemConnection staticconn;
    static RemoteConnectionImpl remoConn;
    static {
        String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
        if (connectionClass == null) {
            System.err.println("Cannot connect without a connection class!");
        }
        Logger.getLogger("org.jini.projects.athena.resultset").log(Level.FINE, "Creating Updater Connection ");
        try {
            staticconn = (SystemConnection) Class.forName(connectionClass).newInstance();
            staticconn.setReference(-1);
            remoConn = new RemoteConnectionImpl(staticconn);
            
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Create an updater with the default connection
     */
    public ResultSetUpdater() {

        sconn = ResultSetUpdater.remoConn;

    }

    /**
     * Create an updater with a given System Connection
     */
    public ResultSetUpdater(SystemConnection sconn) {
        if (sconn == null) {

            this.sconn = remoConn;

        } else {
        	staticconn = sconn;
            try {
				this.sconn = new RemoteConnectionImpl(sconn);
			} catch (RemoteException e) {
				// TODO Handle RemoteException
				e.printStackTrace();
			}
        }
    }

    /**
     * Update a given object <i>in</i>, with to the reuslts of invocation<i>apply</i>
     */
    public Object update(Object in, Object apply) {
        //The object in is the old object
        //apply is a Command Object
        //synchronized (sconn) {
        try {
            //Object rvalue = sconn.executeQuery(apply);
            Object retval = sconn.executeObjectQuery(apply);
            System.out.println("Retval type: " + retval.getClass().getName());
            if (retval instanceof SystemResultSet) {
                SystemResultSet rs = (SystemResultSet) retval;                
                return new AthenaResultSetImpl(rs);
            }
            if (retval instanceof AthenaResultSet) {
            	return retval;
            } else {
                throw new EmptyResultSetException("no records available");
            }
        } catch (EmptyResultSetException ersex) {
            System.out.println("EmptyResultSet Exception");
            ersex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Cannot update Cache: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        //in = rvalue;
        //}
        return null;
    }

}
