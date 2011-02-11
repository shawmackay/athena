package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.harness.ObjectHarness;
import org.jini.projects.athena.resultset.AthenaResultSet;

public class bdbread implements ObjectHarness{
public void populateCommand(Command comm) {
	// TODO Auto-generated method stub
	comm.setCallName("readtest");
	comm.setParameter("key", "Hey");
	
	
}

public void handleReturnObject(Object o) {
	// TODO Auto-generated method stub
	System.out.println("Retunred Object is: " + o.toString());
}
}
