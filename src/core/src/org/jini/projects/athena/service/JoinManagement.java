/**
 *  Title: <p>
 *
 *  Description: <p>
 *
 *  Copyright: Copyright (c) <p>
 *
 *  Company: <p>
 *
 *  @author
 *
 *@version 0.9community */

package org.jini.projects.athena.service;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.factory.JComponentFactory;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.projects.athena.connection.ProcedureHandler;
import org.jini.projects.athena.service.ui.AthenaPanel;
import org.jini.projects.athena.service.ui.AthenaUIFact;
import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.ChangeEventListenerImpl;
import org.jini.projects.thor.service.ThorService;
import org.jini.projects.thor.service.ThorSession;


/**
 * Handles discovery and Join for Athena
 * 
 * @author calum 09 October 2001
 */
public class JoinManagement implements DiscoveryListener, ManagerListener {
	/**
	 * Services' Jini ID
	 * 
	 * @since
	 */
	public static ServiceID servID = null;
	public static boolean DEBUG = System.getProperty("org.jini.projects.athena.debug") != null ? true : false;
	/**
	 * Used internally in discovery
	 * 
	 * @since
	 */
	public LookupDiscoveryManager ldm;
	public LeaseRenewalManager lrm;
	private Logger log;
	/**
	 * Used internally in join
	 * 
	 * @since
	 */
	public JoinManager jm;
	Logger l = Logger.getLogger("org.jini.projects.athena.service");
	/**
	 * The object that is being registered with the lookup service
	 * 
	 * @since
	 */
	public AthenaRegistrationImpl serviceReg;
	String[] GROUPS = {"debug"};
	String[] OFFLINEGROUPS = {"offline"};
	private String nameRegistered = "DBConnector";
	private boolean isjoined = false;
	private Configuration config;

	/**
	 * Allows registration of Service Instance under a given name. <BR>
	 * The name registered allows you to distinguish through a ServiceTemplate,
	 * which services you require
	 * 
	 * @param config
	 *                   Jini Configuration instance to use when configuring this service
	 * @since
	 */
	public JoinManagement(Configuration config) throws ConfigurationException {
		this.config = config;
		nameRegistered = (String) config.getEntry("org.jini.projects.athena", "athenaName", String.class, "Athena");
		log = Logger.getLogger("org.jini.projects.athena.service");
		register();
	}

	/**
	 * Default Constructor, performs registration under the default name
	 * 
	 * @since
	 */
	public JoinManagement() {
		nameRegistered = "Athena";
		log = Logger.getLogger("org.jini.projects.athena.service");
		register();
	}

	/**
	 * Gets the joined attribute of the JoinManagement object
	 * 
	 * @return The joined value
	 * @since
	 */
	public boolean isJoined() {
		return isjoined;
	}

	/**
	 * Performs the join for the service into a Jini network
	 * 
	 * @since
	 */
	public void register() {
		try {
			SystemManager.registerManagerListener(this);
			GROUPS = (String[]) config.getEntry("org.jini.projects.athena", "groups", String[].class);
			Name svcname = new Name(nameRegistered);
			ServiceInfo svcinfo = new ServiceInfo("Athena", "CWA", "CWA", "0.1", "", "");
			AthenaServiceType ast = new AthenaServiceType();
			UIDescriptor athenaDesc = new UIDescriptor(AthenaPanel.ROLE, AthenaUIFact.TOOLKIT, null, new java.rmi.MarshalledObject(new AthenaUIFact()));
			athenaDesc.attributes = new java.util.HashSet();
			serviceReg = new AthenaRegistrationImpl(config);
			
            ExporterManager mgr = DefaultExporterManager.getManager();
            Uuid regProxyID = UuidFactory.generate(); 
            serviceReg.setProxyID(regProxyID);
			Remote proxy = mgr.exportProxy(serviceReg, "Service", regProxyID);
            
			athenaDesc.attributes.add(new net.jini.lookup.ui.attribute.UIFactoryTypes(java.util.Collections.singleton(JComponentFactory.TYPE_NAME)));
			Entry[] attrs = {svcname, svcinfo, athenaDesc, ast};
			ldm = new LookupDiscoveryManager(GROUPS, null, this, config);
            lrm = new LeaseRenewalManager(config);
            try{
            	synchronized(this){
            		wait();
                }
                  
                
            } catch (Exception ex){
            	ex.printStackTrace();
            }
            
            if(!isjoined){
            	System.out.println("Athena's service dependencies could not be met......exiting, check logs/athena.0");
                System.exit(0);
            }
			
			try {
				ServiceID sid = (ServiceID) AthenaLogger.restore(nameRegistered + "servID.per");
				log.info("Rejoining....as " + sid.toString());
				jm = new JoinManager(proxy, attrs, sid, ldm, lrm, config);
				serviceReg.setLookupDiscoveryManager(ldm);
				serviceReg.setJoinManager(jm);				
			} catch (Exception ex) {
				log.info("Joining as a new service....");
				jm = new JoinManager(proxy, attrs, new IDLister(), ldm, lrm, config);
				serviceReg.setLookupDiscoveryManager(ldm);
				serviceReg.setJoinManager(jm);				
			}
            ProcedureHandler.loadDefs();
            serviceReg.configure();                       
            System.out.println("System is now initialised");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Call back from the lookup service
	 * 
	 * @param e
	 *                   the DiscoveryEvent from a lookup service
	 * @since
	 */
	public void discovered(DiscoveryEvent e) {
		ServiceRegistrar[] registrars = e.getRegistrars();
		try {
			for (int i = 0; i < registrars.length; i++) {
				l.info("Registered with: " + registrars[i].getLocator() + "{" + registrars[i].getServiceID().toString() + "}");
			}
			if (!isjoined) {
				l.info("Logging in to Thor");
				Class[] classType = {ThorService.class};
				String thorName = (String) config.getEntry("org.jini.projects.athena", "thorName", String.class, "master");
				String thorbranch = (String) config.getEntry("org.jini.projects.athena", "thorBranch", String.class, "Properties/Athena/" + nameRegistered);
				net.jini.core.entry.Entry[] attrs = {new net.jini.lookup.entry.Name(thorName)};
				net.jini.core.lookup.ServiceTemplate svctmp = new net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
				ThorService thor = (ThorService) registrars[0].lookup(svctmp);
				if (thor == null)
					l.severe("An instance of Thor, named " + thorName + " cannot be found on this registrar: " + registrars[0].getLocator());
				else {
					ProxyPreparer preparer = (ProxyPreparer) config.getEntry("org.jini.projects.athena", "proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
					ThorService preparedThor = (ThorService) preparer.prepareProxy(thor);
					
					ThorSession session = preparedThor.getSession();
					Branch sessionRoot = session.getRoot();
					l.info("Looking at " + thorbranch);
					Branch b = sessionRoot.getBranch(thorbranch);
					if (b == null) {
						l.warning("Thor configuration branch could not be located.....exiting - see logs/athena.0");
						System.exit(0);
					}
					Properties props_in = (Properties) sessionRoot.getBranch(thorbranch).getData();
					try {
						ExporterManager exp = DefaultExporterManager.getManager();
						ChangeEventListener changeHandler = (ChangeEventListener) exp.exportProxy(new ChangeEventListenerImpl(), "ThorListener", UuidFactory.generate());
						net.jini.core.event.EventRegistration evReg = sessionRoot.getBranch(thorbranch).trackChanges(120000L, changeHandler, "");
						if (evReg != null) {
							lrm.renewFor(evReg.getLease(), net.jini.core.lease.Lease.FOREVER, null);
						}
					} catch (net.jini.core.lease.LeaseDeniedException ldex) {
						System.out.println("System would not grant a lease");
					}
					System.getProperties().putAll(props_in);
					if (DEBUG) {
						l.finer("Creating Connection pool using parameters: ");
						Properties props = System.getProperties();
						Set propsenum = props.entrySet();
						Iterator iter = propsenum.iterator();
						while (iter.hasNext()) {
							Map.Entry entr = (Map.Entry) iter.next();
							if (entr.getKey().toString().indexOf("org.jini.projects.athena.") != -1) {
								l.finer("\t" + entr.getKey() + ": " + entr.getValue());
							}
						}
					}
					//Loading procedure defeinitions
					
					isjoined = true;
                    synchronized(this){
                    	notify();
                    }
				}
			}
		} catch (RemoteException ex) {
			System.out.println("Err:" + ex.getMessage());
			ex.printStackTrace();
			System.err.println("Panic: Can't get GROUP list!");
		} catch (ConfigurationException ex) {
			// URGENT Handle ConfigurationException
			ex.printStackTrace();
		}
	}

	/**
	 * Call back from the lookup service
	 * 
	 * @param e
	 *                   the DiscoveryEvent from a lookup service
	 * @since
	 */
	public void discarded(DiscoveryEvent e) {
		System.out.println("Discarded from");
		ServiceRegistrar[] registrars = e.getRegistrars();
		for (int i = 0; i < registrars.length; i++) {
			try {
				System.out.println("Registrar: " + registrars[i].getLocator());
			} catch (Exception ex) {
				System.out.println("Exception on discarded()");
			}
		}
	}

	public void notify(int event) {
		try {
			if (event == HostEvents.DBCLOSED || event == HostEvents.HIBERNATE) {
				System.out.println("Join Manager: about to go offline");
				this.ldm.removeGroups(GROUPS);
				this.ldm.setGroups(OFFLINEGROUPS);
				System.out.println("Groups Removed");
			}
			if (event == HostEvents.DBREOPENED) {
				System.out.println("Join Manager: about to go online in 10 seconds");
				try {
					Thread.sleep(10000);
				} catch (Exception ex){
					ex.printStackTrace();
				}
				this.ldm.removeGroups(OFFLINEGROUPS);
				this.ldm.addGroups(GROUPS);
			}
		} catch (IOException e) {
			System.out.println("Problem with modifying groups");
		}
	}
}