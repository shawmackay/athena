package org.jini.projects.athena.connects.bdb;

import java.util.Properties;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.StdCommand;
import org.jini.projects.athena.exception.AthenaException;



public class BDBConnectionTest {
 public static void main(String[] args){
	 BDBConnection conn = new BDBConnection(false);
	
	 try {
		 
		 Properties props = new Properties();
		 props.put("org.jini.projects.athena.envName", "/Volumes/Data/Development/Java/bdbEnv");
		 props.put("org.jini.projects.athena.dbName", "AthenaTest");
		 conn.connectTo(props);
		 //conn.setTransactionID("Hello:");
		 Command comm = new StdCommand();
		 comm.setParameter("__RETURNTYPE__", "java.lang.String");
		 comm.setParameter("__TYPE__", "write");
		 comm.setParameter("key", "Hey");
		 comm.setParameter("data", "Far");
		 
		 //BDBCommand command = new SimpleBDBCommand(BDBCommand.WRITE,new BDBString("Hello"), new BDBString("Goodbye"));
		 conn.issueCommand(comm);
		 //comm = new SimpleBDBCommand(BDBCommand.READ,new BDBString("Hello"), null);
		
		 comm = new StdCommand();
		 comm.setParameter("__RETURNTYPE__", "java.lang.String");
		 comm.setParameter("__TYPE__", "read");
		 comm.setParameter("key", "He");
		 System.out.println("Returned data is: " + conn.issueCommand(comm));
		 //conn.commit();
		 
	} catch (AthenaException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		try {
			conn.close();
		} catch (AthenaException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		try {
			conn.close();
		} catch (AthenaException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
 }
}
