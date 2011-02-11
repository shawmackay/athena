/*
 * athena.jini.org : org.jini.projects.athena.harness.commands
 * 
 * 
 * BlobWriteTest.java
 * Created on 29-Oct-2004
 * 
 * BlobWriteTest
 *
 */
package org.jini.projects.athena.harness.commands;

		
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.harness.CommandHarness;

/**
 * @author calum
 */
public class BlobWriteTest implements CommandHarness {

	/* @see org.jini.projects.athena.harness.CommandHarness#populateCommand(org.jini.projects.athena.command.Command)
	 */
	public void populateCommand(Command comm) {
		// TODO Complete method stub for populateCommand
		comm.setCallName("imagetest");
		comm.setParameter("name","icon");
		try {
			File f = new File("icon.gif");
			byte[] bindata = new byte[(int)f.length()];
			BufferedInputStream bair = new BufferedInputStream(new FileInputStream("icon.gif"));
			int i = bair.read(bindata);
			if (i==bindata.length)
				System.out.println("File succesfully loaded");
			comm.setParameter("image", bindata);
		} catch (FileNotFoundException e) {
			// TODO Handle FileNotFoundException
			comm.setParameter("image", "No File".getBytes());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			comm.setParameter("image", "General IO Problem".getBytes());
			e.printStackTrace();
		}
		
	}

}
