/*
 * athena.jini.org : org.jini.projects.athena.harness.commands
 * 
 * 
 * cacheread.java Created on 01-Nov-2004
 * 
 * cacheread
 *  
 */

package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.harness.CommandHarness;
import org.jini.projects.athena.harness.ReaderHarness;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 * @author calum
 */
public class CacheRead implements ReaderHarness {

	public void handleResultSet(AthenaResultSet rs) {
		// TODO Complete method stub for handleResultSet
		System.out.println("ResultSet obtained");
		try {
			System.out.print("Row\t");
			for (int i = 0; i < rs.getColumnCount(); i++) {
				System.out.print(rs.getFieldName(i) + "\t");
			}
			System.out.println();
			int counter=0;
			while (rs.next()) {
				System.out.print(++counter+"\t");
				for (int i = 0; i < rs.getColumnCount(); i++) {
					System.out.print(rs.getField(i) + "\t");
				}
				System.out.println();
			}
		} catch (AthenaException e) {
			// TODO Handle AthenaException
			e.printStackTrace();
		}
		System.out.println();
	}

	/*
	 * @see org.jini.projects.athena.harness.CommandHarness#populateCommand(org.jini.projects.athena.command.Command)
	 */
	public void populateCommand(Command comm) {
		// TODO Complete method stub for populateCommand
		comm.setCallName("cacheread");
		comm.setParameter("name", "Calum");
	}

}
