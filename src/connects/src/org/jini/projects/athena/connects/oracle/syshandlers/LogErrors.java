/*
 * Created on 17-Mar-2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */

package org.jini.projects.athena.connects.oracle.syshandlers;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jini.projects.athena.patterns.Chain;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.athena.util.builders.LogExceptionWrapper;

/**
 * @author calum
 */
public class LogErrors implements Chain {
	protected Logger log = Logger.getLogger("org.jini.projects.athena.connection");

	/**
	 *  
	 */
	public LogErrors() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.org.jini.projects.athena.patterns.Chain#addChain(org.jini.projects.org.jini.projects.athena.patterns.Chain)
	 */
	Chain nextInChain;

	/**
	 * Add the next item into the chain
	 */
	public void addChain(Chain c) {
		nextInChain = c;
	}

	/**
	 * Either handle an object or move it along ot the next handler in the chain
	 */
	public void sendToChain(Object mesg) {
		if (mesg instanceof SQLException) {
			SQLException sqlex = (SQLException) mesg;
			if (SystemManager.getSystemState() == SystemManager.ONLINE) {
				log.log(Level.SEVERE, sqlex.getMessage(), sqlex);
			} else
				log.log(Level.INFO, sqlex.getMessage(), sqlex);
		}
		if (mesg instanceof LogExceptionWrapper) {
			LogExceptionWrapper wrapperex = (LogExceptionWrapper) mesg;
			Throwable wex = wrapperex.getCause();
			if (wrapperex.isSevere()) {
				System.out.println("Logging as a severe error [" + wex.getMessage() + "]");
				if (wex.getMessage() == null)
					log.log(Level.SEVERE, "null", wex);
				else
					log.log(Level.SEVERE, wex.getMessage(), wex);
			}
			if (wrapperex.isWarning() && !wrapperex.isSevere()) {
				System.out.println("Logging as a warning error");
				if (wex.getMessage() == null)
					log.log(Level.WARNING, "null", wex);
				else
					log.log(Level.WARNING, wex.getMessage(), wex);
			}
			if (!wrapperex.isWarning() && !wrapperex.isSevere())
				log.log(Level.INFO, wex.getMessage(), wex);
		}
		if (nextInChain != null) {
			nextInChain.sendToChain(mesg);
		}
	}

	/**
	 * Get the next handler in the chain
	 */
	public Chain getChain() {
		return nextInChain;
	}
}