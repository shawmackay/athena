/*
 * org.jini.projects.athena : org.jini.projects.org.jini.projects.athena.leasing
 *
 *
 * ConnectionResource.java
 * Created on 07-Jan-2004
 *
 * ConnectionResource
 *
 */
package org.jini.projects.athena.leasing;

import java.util.logging.Logger;

import net.jini.id.Uuid;

import org.jini.projects.athena.connection.AbstractRConnectionImpl;
import org.jini.projects.athena.connection.RemoteConnection;

import com.sun.jini.landlord.LeasedResource;

/**
 * Provides the link between a resource that can be leased, and an Athena Connection.
 * Allows the reaper to forcibly deallocate, and cleanup, a connection from a client. 
 * @author calum
 */
public class ConnectionResource implements LeasedResource {
	private Logger log = Logger.getLogger("org.jini.projects.athena.leasing");
    private RemoteConnection conn;
    private long expiry;
    private Uuid cookie;
    /**
     * Build a resource with a given connection and a cookie id
     */
    public ConnectionResource(AbstractRConnectionImpl conn, Uuid cookie) {
        super();
        this.conn = conn;
        this.cookie = cookie;
        
        // URGENT Complete constructor stub for ConnectionResource
    }

    /* @see com.sun.jini.landlord.LeasedResource#setExpiration(long)
     */
    public void setExpiration(long newExpiration) {
        // TODO Complete method stub for setExpiration
    	expiry = newExpiration;
    }

    /* @see com.sun.jini.landlord.LeasedResource#getExpiration()
     */
    public long getExpiration() {
        // TODO Complete method stub for getExpiration
        return expiry;
    }

    /* @see com.sun.jini.landlord.LeasedResource#getCookie()
     */
    public Uuid getCookie() {
        return cookie;
    }

    /**
     * @return Returns the connection linked to this leased resource.
     */
    public RemoteConnection getConn() {
        return conn;
    }


    /**
     *  Forcibly deallocates and releases a connection in the event of a client failing to renew a lease
     *
     *@return    Description of the Returned Value
     *@since
     */
    public boolean forceDeallocate() {
        log.finer("Trying to deallocate");
        try {
            if (conn.isReleased()) {
               log.finer("Connection appears to be released already");
                return true;
            }

            if (conn.canRelease()) {
                log.finer("releasing");
                conn.release();
                return true;
            } else {
                log.warning("Cannot release");
                return false;
            }
        } catch (Exception ex) {
            log.warning("Err : " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

}
