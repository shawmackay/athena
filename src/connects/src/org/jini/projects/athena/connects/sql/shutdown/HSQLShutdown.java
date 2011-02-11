/*
 * athena.support.jini.org : org.jini.projects.athena.connects.sql.shutdown
 * 
 * 
 * HSQLShutdown.java
 * Created on 15-Sep-2004
 * 
 * HSQLShutdown
 *
 */
package org.jini.projects.athena.connects.sql.shutdown;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.jini.projects.athena.service.ShutdownOperations;

/**
 * @author calum
 */
public class HSQLShutdown implements ShutdownOperations {

	/* @see org.jini.projects.athena.service.ShutdownOperations#execute()
	 */
	public void execute() {
		String username = System.getProperty("org.jini.projects.athena.connect.username");
		String password = System.getProperty("org.jini.projects.athena.connect.password");
		String  URL = System.getProperty("org.jini.projects.athena.connect.url");
		String driver = System.getProperty("org.jini.projects.athena.connect.driver");
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(URL, username, password);
			Statement stmt = conn.createStatement();
			stmt.execute("shutdown compact");
			System.out.println("Embedded HSQLDatabase shutdown");
		} catch (ClassNotFoundException e) {
			// TODO Handle ClassNotFoundException
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Handle SQLException
			e.printStackTrace();
		}
	}

}
