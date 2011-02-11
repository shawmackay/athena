/*
 * athena.jini.org : org.jini.projects.athena.service
 * 
 * 
 * ShutdownOperations.java
 * Created on 15-Sep-2004
 * 
 * ShutdownOperations
 *
 */
package org.jini.projects.athena.service;

/**
 * Executes during the shutdown hooks, in order
 * to execute some datasource specific shutdown procedures
 * @author calum
 */
public interface ShutdownOperations {

	public void execute();
	
}
