/*
 * athena.jini.org : org.jini.projects.athena.harness.commands
 * 
 * 
 * preptest_harness.java
 * Created on 28-Oct-2004
 * 
 * preptest_harness
 *
 */
package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.harness.CommandHarness;

/**
 * @author calum
 */
public class preptest_harness implements CommandHarness {

	/* @see org.jini.projects.athena.harness.CommandHarness#populateCommand(org.jini.projects.athena.command.Command)
	 */
	public void populateCommand(Command comm) {
		comm.setCallName("preptest");
		// TODO Complete method stub for populateCommand
		comm.setParameter("quantity", new Integer(4) );
		comm.setParameter("code", "DEF");
		comm.setParameter("description", "Prepared Earlier" );
	}

}
