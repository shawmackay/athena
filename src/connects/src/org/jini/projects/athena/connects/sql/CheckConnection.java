/*
 * athena.support.jini.org : org.jini.projects.athena.connects.sql
 * 
 * 
 * CheckConnection.java
 * Created on 25-May-2004
 * 
 * CheckConnection
 *
 */

package org.jini.projects.athena.connects.sql;

import java.util.logging.Logger;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.SystemManager;
import com.sun.jini.constants.TimeConstants;

/**
 * @author calum
 */
public class CheckConnection implements Runnable {
	public void run() {
		boolean connected = true;
		SystemConnection aliveConnection = null;
		Logger.getLogger("org.jini.projects.athena.connects.sql").info("Starting connection check thread in 10secs");
		try {
			Thread.sleep(10000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Checking");
		Logger.getLogger("org.jini.projects.athena.connects.sql").info("Starting connection checking");
		for (;;) {
			while (connected) {
				String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
				if (connectionClass == null) {
					System.err.println("Cannot connect without a connection class!");
				}
				try {
					aliveConnection = (SystemConnection) Class.forName(connectionClass).newInstance();
					aliveConnection.setReference(-1);
					String ctype = System.getProperties().getProperty("org.jini.projects.athena.connect.type");
					connected = true;
					aliveConnection.close();
					try {
						Thread.sleep(10 * TimeConstants.SECONDS);
					} catch (InterruptedException e) {
					}
				} catch (Exception ex) {
					System.err.println("Error creating ping connection: going offline");
					SystemManager.inform(HostEvents.DBCLOSED);
					connected = false;
				}
			}
			while (!connected) {
				System.out.println("Attempting reconnection");
				String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
				if (connectionClass == null) {
					System.err.println("Cannot connect without a connection class!");
				}
				try {
					aliveConnection = (SystemConnection) Class.forName(connectionClass).newInstance();
					aliveConnection.setReference(-1);
					String ctype = System.getProperties().getProperty("org.jini.projects.athena.connect.type");
					connected = true;
					aliveConnection.close();
					SystemManager.inform(HostEvents.DBREOPENED);
					aliveConnection = null;
				} catch (Exception ex) {
					try {
						Thread.sleep(10 * TimeConstants.SECONDS);
					} catch (InterruptedException e) {
					}
				}
				try {
					if (aliveConnection!=null)
					aliveConnection.close();
					aliveConnection = null;
				} catch (AthenaException e) {
					System.out.println("ArgggghhhhhH!!!!!");
				}
			}
		}
	}
}