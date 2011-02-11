
package org.jini.projects.athena.util.builders;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.resultset.RemoteResultSet;
import org.jini.projects.athena.service.StatisticMonitor;

/**
 * @author calum
 * 
 * org.jini.projects.athena
 */

public class LocalResultSetBuilder {

	private static Logger log = Logger.getLogger("org.jini.projects.athena.util.builders");

	/**
	 * Description of the Method
	 * 
	 * @param rrs
	 *                  Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public static ArrayList buildlocal(SystemConnection conn, RemoteResultSet rrs, int Capacity) {

		log.log(Level.FINE, "RemoteConnection :\tBuilding local resultset..........");

		try {
			ArrayList table = new ArrayList(Capacity);
			Vector header = new Vector();
			if (rrs.next()) {
				for (int i = 0; i < rrs.getColumnCount(); i++) {
					// System.out.println(rrs.getFieldName(i));
					header.add(rrs.getFieldName(i));
				}
				// System.out.println("Header: " + header);
				do {
					ArrayList row = new ArrayList();
					for (int i = 0; i < header.size(); i++) {
						// todo: Handle Type Conversions here
						row.add(conn.handleType(rrs.getField(i)));
					}
					table.add(row);
				} while (rrs.next());

				log.log(Level.FINEST, "finished!");

				return table;
			} else
				return null;
		} catch (Exception ex) {
			System.err.println(new java.util.Date() + ": RemoteConnection :Can't build table because: " + ex.getMessage());

			log.log(Level.SEVERE, "Cannot build table because: " + ex.getMessage(), ex);
			StatisticMonitor.addFailure();
			// ex.printStackTrace();
			System.out.println(new java.util.Date() + ": RemoteConnection :Returning empty Object");
			return null;
		}

	}

	public static ArrayList buildlocal(ResultSet rs, int Capacity) {

		log.log(Level.FINER, "RemoteConnection :\tBuilding local resultset..........");

		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			ArrayList table = new ArrayList(Capacity);
			Vector header = new Vector();
			if (rs.next()) {
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					// System.out.println(rsmd.getColumnName(i+1));
					header.add(rsmd.getColumnName(i + 1));
				}
				// System.out.println("Header: " + header);
				do {
					ArrayList row = new ArrayList();
					for (int i = 0; i < header.size(); i++) {
						// todo: Handle Type Conversions here
						row.add(rs.getObject(i + 1));
					}
					table.add(row);
				} while (rs.next());

				log.log(Level.FINEST, "finished!");

				return table;
			} else
				return null;
		} catch (Exception ex) {
			System.err.println(new java.util.Date() + ": RemoteConnection :Can't build table because: " + ex.getMessage());

			log.log(Level.SEVERE, "Cannot build table because: " + ex.getMessage(), ex);
			StatisticMonitor.addFailure();
			// ex.printStackTrace();
			System.out.println(new java.util.Date() + ": RemoteConnection :Returning empty Object");
			return null;
		}

	}

	/**
	 * Obtains the names and indexes of the columns in the sql resultset
	 * 
	 * @param rrs
	 *                  RemoteResultSet to se in order to build the
	 * @return Description of the Returned Value
	 * @since
	 */
	public static HashMap buildColDetails(RemoteResultSet rrs) {
		HashMap returntable = new HashMap();
		// System.out.println("Building Column details"); i
		try {
			for (int i = 0; i < rrs.getColumnCount(); i++) {
				returntable.put(rrs.getFieldName(i).toLowerCase(), new Integer(i));
			}
		} catch (Exception ex) {

			log.log(Level.SEVERE, "No column details", ex);
		}
		// System.out.println(returntable);
		return returntable;
	}

	/**
	 * 
	 * 
	 * @param rs
	 *                  Builds a
	 * @return Description of the Returned Value
	 * @since
	 */
	public static HashMap buildColDetails(ResultSet rs) {
		HashMap returntable = new HashMap();

		// System.out.println("Building Column details"); i
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				returntable.put(rsmd.getColumnName(i + 1).toLowerCase(), new Integer(i));
			}
		} catch (Exception ex) {

			log.log(Level.SEVERE, "No column details", ex);
		}
		// System.out.println(returntable);
		return returntable;
	}

}
