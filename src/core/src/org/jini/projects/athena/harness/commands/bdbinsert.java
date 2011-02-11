package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.harness.CommandHarness;

public class bdbinsert implements CommandHarness{
public void populateCommand(Command comm) {
	// TODO Auto-generated method stub
	comm.setCallName("athenatest");
	comm.setParameter("key", "Finlay");
	comm.setParameter("data","Shaw-Mackay");
	
}
}
