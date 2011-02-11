
package org.jini.projects.athena;

import java.security.PrivilegedExceptionAction;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.swing.UIManager;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.service.JoinManagement;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.athena.ui.CleanFrame;
import org.jini.projects.athena.ui.ServiceFrame;



import com.sun.jini.config.Config;

/**
 * Main class - Athena entry point
 * 
 * @author calum
 */
public class ServiceApplication {
	// public static int NUMCONNECTIONS = 0;
	public static String NAME = "DBConnector";
	boolean packFrame = false;
	JoinManagement jmg;
    private Logger log; 

	
    Configuration athenaConfig;
    private static Configuration CONFIG;
    LoginContext loginContext=null;
    private String componentName="org.jini.projects.athena";
    
    /**
	 * Handles the startup of the system. Configures the following:
	 * <ol>
	 * <li>Codebase and policy information from Configuration File
	 * <li>Setting the Security Manager</li>
	 * <li>Creating the object pools and caches</li>
	 * <li>Starting the Dialect and Handler Engines</li>
	 * <li>Initiating the Jini join mechanism</li>
	 * <li>Building the UI if one is required</li>
	 * <ol>
	 * 
	 * @since 1.0b
	 */
	public ServiceApplication(Configuration athenaConfig) throws Exception {
		String codebase = (String) Config.getNonNullEntry(athenaConfig, componentName, "codebase", String.class);
		String policy = (String) Config.getNonNullEntry(athenaConfig, componentName, "policy", String.class);
		
        this.athenaConfig = athenaConfig;
        System.out.println("Setting policy to: " + policy);
        System.out.println("Setting codebase to: " + codebase);
        System.setProperty("java.security.policy", policy);
        System.setProperty("java.rmi.server.codebase", codebase);
		if (System.getSecurityManager() == null) {			
			System.setSecurityManager(new java.rmi.RMISecurityManager());
		}
				
		CONFIG = athenaConfig;
		try {
			loginContext = (LoginContext) athenaConfig.getEntry( componentName, "loginContext", LoginContext.class);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		if (loginContext == null)
			doInit(athenaConfig);
		else
			initWithLogin(athenaConfig, loginContext);
	}
	
	private void initWithLogin(final Configuration config, LoginContext context) throws Exception {
		context.login();
		try {
			Subject.doAsPrivileged(loginContext.getSubject(), new PrivilegedExceptionAction() {
				/*
				 * @see java.security.PrivilegedExceptionAction#run()
				 */
				public Object run() throws Exception {
					doInit(config);
					return null;
				}
			}, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	void doInit(Configuration config){
        DefaultExporterManager.loadManager("default", "config/exportmgr.config");
		SystemManager.initialise(config);
        log = Logger.getLogger("org.jini.projects.athena");
		log.info("Initiliasing Object Pools");
		ResourceManager.getResourceManager().addPool("RCONN", org.jini.projects.athena.connection.RemoteConnectionImpl.class, 7);
		log.info("Initialising System Caches");
		org.jini.projects.athena.resources.FlashableCacheManager fcm = new org.jini.projects.athena.resources.FlashableCacheManager(false);
		ResourceManager.getResourceManager().addCache(org.jini.projects.athena.resultset.AthenaResultSetImpl.class, "RSET", fcm);
		log.info("Initialising Engines");
		try {
			org.jini.projects.athena.command.dialect.DialectEngine.initialise(athenaConfig);
			org.jini.projects.athena.command.HandleEngine.initialise(athenaConfig);
			org.jini.projects.athena.command.types.TypeEngine.initialise(athenaConfig);
		} catch (Exception ex) {
			log.severe("Problem initialising Engines: " + ex.getMessage());
			System.exit(1);
		}
        
		try {
			jmg = new JoinManagement(athenaConfig);
		} catch (ConfigurationException e) {
			// URGENT Handle ConfigurationException
			e.printStackTrace();
		}
		while (!jmg.isJoined()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException inex) {
                inex.printStackTrace();
			}
		}
		log.info("System is joined!");
		//OracleProcedureHandler proch = new OracleProcedureHandler();
		//proch.addProcedureDefinition("SALESUPP", "PKG_CORRESPONDENCE",
		// "CORRES_MAINT");
        
		CleanFrame clfr = null;
		ServiceFrame svcFr = null;
		try {
			Class.forName("org.jini.projects.athena.resultset.ResultSetUpdater");
            initUI(clfr, svcFr);
		} catch (Exception ex) {
            ex.printStackTrace();
		}
		
	}

	void initUI(CleanFrame clfr, ServiceFrame svcFr) throws ConfigurationException {
        String athena_ui = (String) athenaConfig.getEntry("org.jini.projects.athena", "ui", String.class); 
		if (athena_ui!= null) {
            if(athena_ui.equals("clean"))
            	clfr = new CleanFrame(jmg.jm, jmg.ldm);
            else if(athena_ui.equals("lights"))
            	svcFr = new ServiceFrame(jmg.jm, jmg.ldm);
		}
		//Validate frames that have preset sizes
		//Pack frames that have useful preferred size info, e.g. from their
		// layout
		if (packFrame) {
			if (svcFr != null) {
				svcFr.pack();
			}
			if (clfr != null) {
				clfr.pack();
			}
		} else {
			if (svcFr != null) {
				svcFr.validate();
			}
			if (clfr != null) {
				clfr.validate();
			}
		}
		if (svcFr != null) {
			svcFr.setVisible(true);
		}
		if (clfr != null) {
			clfr.setVisible(true);
		}
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {
            //Don't do anything here
		}
		if (svcFr != null) {
			svcFr.setConnectionPool(jmg.serviceReg.cpool);
		}
		if (clfr != null) {
			clfr.setConnectionPool(jmg.serviceReg.cpool);
		}
		if (svcFr != null) {
			svcFr.startCheck();
		}
		if (clfr != null) {
			clfr.startCheck();
		}
	}

	/**
	 * The main program for the ServiceApplication class
	 * 
	 * @param args
	 *                   The command line arguments
	 * @since
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length >= 1) {
			if (args[0].equals("/?") || args[0].equals("/h") || args[0].equals("--help") || args[0].equals("-h")) {
				System.out.println("Command Line Syntax: ");
				System.out.println("\tServiceApplication <setnamefile>");
				System.exit(0);
			}
		}
	
		
        net.jini.config.Configuration config = ConfigurationProvider.getInstance(args);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new ServiceApplication(config);
	}
}