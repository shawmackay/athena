/*
 * athena.jini.org : org.jini.projects.athena.service
 * 
 * 
 * CommandLogViewer.java
 * Created on 25-Aug-2004
 * 
 * CommandLogViewer
 *
 */

package org.jini.projects.athena.service;

import java.io.IOException;

/**
 * Displays the listof commands in a command log file.
 * Command log files contain the sets of commands sent through Athena
 * under a single transaction. They are held in &ltathenaroot>/data
 * and have the filename format &lt;athenaname>CONN&ltconnnumber>.ser
 * 
 * @author calum
 */
public class CommandLogViewer {

	public static void main(String[] args) {
		if (args.length == 1) {
			StatePersistence sp=null;
			try {
				sp = (StatePersistence) AthenaLogger.restore(args[0]);
			} catch (IOException e) {
				// TODO Handle IOException
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Handle ClassNotFoundException
				e.printStackTrace();
			}
			System.out.println("Current State: " + sp.getState());
			System.out.println("StackSize: " + sp.getStackSize());
			System.out.println("In Txn? : " + sp.cookedTX != null);
			System.out.println("Commands:");
			for (int i = 0; i < sp.getStackSize(); i++) {
				System.out.println("\t" + sp.getCommand(i).toString());
			}
		}

	}
}