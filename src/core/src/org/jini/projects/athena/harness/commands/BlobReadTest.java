/*
 * athena.jini.org : org.jini.projects.athena.harness.commands
 * 
 * 
 * BlobReadTest.java Created on 29-Oct-2004
 * 
 * BlobReadTest
 *  
 */

package org.jini.projects.athena.harness.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.harness.ReaderHarness;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 * @author calum
 */
public class BlobReadTest implements ReaderHarness {

	public void handleResultSet(AthenaResultSet rs) {
		try {
			// TODO Complete method stub for handleResultSet
			for (int i = 0; i < rs.getRowCount(); i++) {
				rs.next();
				
					System.out.println("Name: " + rs.getField("name"));
					byte[] barr = (byte[]) rs.getField("img");
					File f = File.createTempFile("athena", "blob");
					System.out.println("Blob@R" + i + ":C:" + 2 + ": stored as " + f.getAbsolutePath());
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(barr);
					fos.flush();
					fos.close();
				
			}
		} catch (AthenaException e) {
			// TODO Handle AthenaException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}
	}

	public void populateCommand(Command comm) {
		// TODO Complete method stub for populateCommand
		comm.setCallName("imageread");
	}
}
