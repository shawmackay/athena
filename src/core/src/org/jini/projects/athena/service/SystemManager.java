/*
 * Created by IntelliJ IDEA. User: calum Date: 29-May-02 Time: 10:05:25 To
 * change template for new class use Code Style | Class Templates options (Tools |
 * IDE Options).
 */

package org.jini.projects.athena.service;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.ErosService;

//TODO: Alter Class to singleton Pattern and remove Static references
/**
 * Initialises Eros logging, and manages the list of listeners to be informed when a change in Athena's configuration or status occurs.
 * 
 * @author calum
 *
 */
public class SystemManager {
	//private static LookupCache cache;
	//private static Thread checkConn;
	public static final int DBOFFLINE = 2;
	//private static boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
	private static ErosLogger eLogger;
	private static ErosService eros;
	public static final int FATAL = 4;
	protected static LookupDiscoveryManager ldm;
	//private  java.util.logging.Logger LOG = null;
    private static Logger stdLogger;
	private static Handler logHandler;
	private static ArrayList managementEvents = new ArrayList();
	public static final int ONLINE = 1;
	public static final int SCHEDULEDDOWNTIME = 8;
	protected static ServiceDiscoveryManager sdm;
	private static SystemConfigChangeListener SYSTEMCHANGELISTENER;
	public static org.jini.projects.athena.leasing.ConnectionLandlord SYSTEMLANDLORD;
	private static int Systemstate = ONLINE;
	final ArrayList externalEvents = new ArrayList();
	Configuration athenaConfig;
	

	

	static class ErosFilter implements ServiceItemFilter {
		public boolean check(ServiceItem item) {
			if (item.service instanceof ErosService) {
				return true;
			}
			return false;
		}
	}

	public static SystemConfigChangeListener getSystemChangeListener() {
		return SYSTEMCHANGELISTENER;
	}

	public static int getSystemState() {
		return Systemstate;
	}

	
	
	/**
	 * Tells the system manager that an event has occured that effects the
	 * system outside of it's overall scope.
	 */
	public static void inform(int event) {
		//The first thing has to be a change in the System State
		if (event == HostEvents.DBCLOSED)
			Systemstate = DBOFFLINE;
		if (event == HostEvents.HIBERNATE)
			Systemstate = SCHEDULEDDOWNTIME;
		for (int i = 0; i < managementEvents.size(); i++) {
			ManagerListener listen = (ManagerListener) managementEvents.get(i);
			new Thread(new InformerThread(event, listen)).start();
		}
		/*
		 * If The manger is informed that the source is closed, start requesting
		 * new connections immediately However, a scheduled shutdown, i.e. a
		 * HIBERNATE will not cause the system to try to reconnect Instead the
		 * system will wait until the WAKE event is fired and then will attempt
		 * to restart
		 */
		stdLogger.log(Level.FINE, "Checking.....event Type\n\n");
//		if (event == HostEvents.DBCLOSED || event == HostEvents.WAKE) {
//			if (event == HostEvents.DBCLOSED)
//				//SystemManager.LOG.warning("Host System has disconnected -
//				// going offline");
//				stdLogger.log(Level.FINE, "System Manager: Will close all DB Connections and bring system offline");
//			if (checkConn == null || !(checkConn.isAlive())) {
//				checkConn = new Thread(new CheckHostConnection());
//				checkConn.start();
//			}
//		}
		if (event == HostEvents.DBREOPENED)
			Systemstate = ONLINE;
	}

	/**
	 * Causes the classloader to run the static initiliasre block above Negates
	 * need for Class.forName();
	 */
	public static void initialise(Configuration config) {
        try {
            System.out.println("Initialising SystemManager");
            SystemManager.SYSTEMCHANGELISTENER = new SystemConfigChangeListener();
            String[] GROUPS = (String[]) config.getEntry("org.jini.projects.athena", "groups", String[].class);
            ldm = new LookupDiscoveryManager(GROUPS, null, null,config);
            sdm = new ServiceDiscoveryManager(ldm, null, config);
            ServiceTemplate temp = new ServiceTemplate(null, new Class[]{ErosService.class}, null);

            ServiceItem svc = sdm.lookup(temp, new ErosFilter(), 3000);
            if (svc != null) {
                if (svc.service != null && svc.service instanceof ErosService) {
                    
                    try {
                        ProxyPreparer preparer = (ProxyPreparer) config.getEntry("org.jini.projects.athena","proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
                        eros = (ErosService) preparer.prepareProxy(svc.service);
                        eLogger = eros.getLogger();                      
                        eLogger.initialise("Athena");                        
                        logHandler = (Handler) eLogger.getLoggingHandler();
                        
                        int Kb = 1024;
                        FileHandler fhandler = new FileHandler("logs/athena", 500 * Kb, 40);
                        fhandler.setFormatter(new LogFormatter());
                        
                        String consoleLogLevel = (String) config.getEntry("org.jini.projects.athena", "consoleLogLevel", String.class);
                        String fileLogLevel = (String) config.getEntry("org.jini.projects.athena", "fileLogLevel", String.class);
                       stdLogger = java.util.logging.Logger.getLogger("org.jini.projects.athena");
                        stdLogger.setLevel(Level.parse(fileLogLevel));
                        
                        
                        stdLogger.setUseParentHandlers(false);
                        fhandler.setLevel(Level.parse(fileLogLevel));
                        logHandler.setLevel(Level.parse(consoleLogLevel));
                        stdLogger.addHandler(fhandler);
                        stdLogger.addHandler(logHandler);
                        stdLogger.info("Eros Logging enabled");

                    } catch (RemoteException e) {
                        System.err.println("Exception occured whilst interacting with Eros");
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        System.err.println("Security Exception occured whilst interacting with Eros");
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("Exception occured whilst interacting with Eros");
                        e.printStackTrace();
                    }
                    
                }
            } else {
            	System.out.println("Eros could not be found....default logging will be provided");
            	  int Kb = 1024;
            	 File logDir = new File("logs");
            	 if (!logDir.exists())
            		 logDir.mkdir();
                  FileHandler fhandler = new FileHandler("logs/athena", 500 * Kb, 40);
                  fhandler.setFormatter(new LogFormatter());
                  
                  String consoleLogLevel = (String) config.getEntry("org.jini.projects.athena", "consoleLogLevel", String.class);
                  String fileLogLevel = (String) config.getEntry("org.jini.projects.athena", "fileLogLevel", String.class);
                 stdLogger = java.util.logging.Logger.getLogger("org.jini.projects.athena");
                  stdLogger.setLevel(Level.parse(fileLogLevel));
                  
                  ConsoleHandler logHandler = new ConsoleHandler();
                  logHandler.setFormatter(new LogFormatter());
                  stdLogger.setUseParentHandlers(false);
                  fhandler.setLevel(Level.parse(fileLogLevel));
                  logHandler.setLevel(Level.parse(consoleLogLevel));
                  stdLogger.addHandler(fhandler);
                  stdLogger.addHandler(logHandler);
            }
        } catch (IOException e) {
            System.out.println("System cannot perform lookup for Eros");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("System cannot perform lookup for Eros");
            e.printStackTrace();
        }
		stdLogger.info("Initialised SystemManager");
	}

	public static void registerManagerListener(ManagerListener listen) {		
		managementEvents.add(listen);
	}

	public SystemManager() {
	}
}