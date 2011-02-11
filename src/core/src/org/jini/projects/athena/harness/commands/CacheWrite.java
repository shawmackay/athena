/*
 * athena.jini.org : org.jini.projects.athena.harness.commands
 * 
 * 
 * cacheread.java
 * Created on 01-Nov-2004
 * 
 * cacheread
 *
 */
package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.harness.CommandHarness;
import org.jini.projects.athena.harness.ReaderHarness;
import org.jini.projects.athena.resultset.AthenaResultSet;

/**
 * @author calum
 */
public class CacheWrite implements CommandHarness {

	/* @see org.jini.projects.athena.harness.CommandHarness#populateCommand(org.jini.projects.athena.command.Command)
	 */
	public void populateCommand(Command comm) {
		// TODO Complete method stub for populateCommand
		comm.setCallName("cacheins");
		comm.setParameter("name","Calum");
		comm.setParameter("description", "thirdperson");
		comm.setParameter("id", new Integer(5454));
		comm.setParameter("enabled", Boolean.valueOf(true));
	}

}
