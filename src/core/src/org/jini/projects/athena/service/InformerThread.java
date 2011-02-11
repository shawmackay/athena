/*
 * athena.jini.org : org.jini.projects.athena.service
 * 
 * 
 * InformerThread.java
 * Created on 25-May-2004
 * 
 * InformerThread
 *
 */
package org.jini.projects.athena.service;

/**
 * Informs interested parties of any changes fired from Thor
 * @author calum
 */
public class InformerThread implements Runnable{
	private int event;
	private ManagerListener listen;
	InformerThread(int event, ManagerListener listen){
		this.event = event;
		this.listen = listen;
	}
	/* @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Complete method stub for run
		listen.notify(event);
	}
}