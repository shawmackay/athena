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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.ProxyAccessor;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lookup.JoinManager;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ServerProxyTrust;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.connection.AthenaConnectionImpl;
import org.jini.projects.athena.connection.ConnectionPool;
import org.jini.projects.athena.connection.RemoteConnection;
import org.jini.projects.athena.connection.RemoteConnectionImpl;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.HostException;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.service.constrainable.AthenaRegistrationProxy;


/**
 * Represents concrete class of registration class into Athena. The stub for
 * this class is given to the Jini LUS.
 * 
 * @author calum
 * 
 * @author calum 09 October 2001
 */
public class AthenaRegistrationImpl implements Remote, AthenaRegistration, ServerProxyTrust, ProxyAccessor {
	public ConnectionPool cpool;
	org.jini.projects.athena.leasing.ConnectionLandlord landlord;
	Properties props = System.getProperties();
	String un = props.getProperty("org.jini.projects.athena.connect.username");
	String pw = props.getProperty("org.jini.projects.athena.connect.password");
	String dr = props.getProperty("org.jini.projects.athena.connect.driver");
	String ur = props.getProperty("org.jini.projects.athena.connect.url");
	int nc = 0;
	private LookupDiscoveryManager ldm = null;
	private JoinManager jm = null;
	private Configuration config;
	private Uuid regProxyID;

	/**
	 * Default Constructor
	 * 
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since @throws
	 *             RemoteException if class cannot be corrected
	 */
	public AthenaRegistrationImpl(Configuration config) throws RemoteException {
		this.config = config;
	}

	public void setJoinManager(JoinManager jm) {
		this.jm = jm;
	}

	/**
	 * Sets the LookupDiscoveryManager attribute of the AthenaRegistrationImpl
	 * object
	 * 
	 * @param ldm
	 *                  The new LookupDiscoveryManager value
	 * @since
	 */
	public void setLookupDiscoveryManager(LookupDiscoveryManager ldm) {
		this.ldm = ldm;
	}

	/**
	 * Obtains an instance of AthenaConnection and exports it to the client
	 * 
	 * @param duration
	 *                  Description of Parameter
	 * @return AthenaConnection
	 * @exception LeaseDeniedException
	 *                        Description of Exception
	 * @since @throws
	 *             RemoteException if a connection cannot be assigned
	 */
	public AthenaConnection getConnection(long duration) throws RemoteException, LeaseDeniedException, HostException {
		RemoteConnectionImpl rimpl;
		SystemConnection sconn;
		StatisticMonitor.addConnection();
		synchronized (cpool) {
			sconn = cpool.getConnection(null);
		}
		if (sconn != null) {
			rimpl = (RemoteConnectionImpl) ResourceManager.getResourceManager().checkOutFromPool("RCONN");
			//rimpl = new RemoteConnectionImpl(sconn);//(RemoteConnectionImpl)
			// ResourceManager.getResourceManager().checkOutFromPool("RCONN");
			if (rimpl == null)
				System.out.println("CONNECTION PANIC!!!!!!!!!!!!!");
			rimpl.setConnection(sconn);
			Lease returnlease = landlord.newLease(rimpl, duration);
			StatisticMonitor.addAllocation();
			RemoteConnection r = (RemoteConnection) DefaultExporterManager.getManager().exportProxy(rimpl, "Connection", UuidFactory.generate());
			return new AthenaConnectionImpl(r, returnlease);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * Obtains an instance of AthenaConnection and exports it to the client and
	 * attempts to link it to given transaction ID Currently unimplemented
	 * 
	 * @param asUser
	 *                  name to run Under
	 * @param duration
	 *                  Description of Parameter
	 * @return AthenaConnection
	 * @exception LeaseDeniedException
	 *                        Description of Exception
	 * @since @throws
	 *             RemoteException if a connection cannot be assigned
	 */
	public AthenaConnection getConnection(String asUser, long duration) throws RemoteException, LeaseDeniedException, HostException {
		try {
			if (SystemManager.getSystemState() == SystemManager.SCHEDULEDDOWNTIME)
				throw new HostException("Scheduled Downtime");
			if (SystemManager.getSystemState() == SystemManager.DBOFFLINE)
				throw new HostException("Host Offline");
			//RemoteConnectionImpl rimpl = new
			// org.jini.projects.org.jini.projects.athena.connection.RemoteConnectionImpl(cpool.getConnection());
			Logger log = Logger.getLogger("org.jini.projects.athena.connection");
			log.fine("[" + asUser + "]" + "Obtaining a Remote Connection from the pool");
			RemoteConnectionImpl rimpl = (RemoteConnectionImpl) ResourceManager.getResourceManager().checkOutFromPool("RCONN");
			log.finer("[" + asUser + "]" + "Pooled Remote Connection retrieved");
			//PoolThread pt = new PoolThread(rimpl);
			//System.out.println("Thread built");
			// pt.start();
			log.finest("[" + asUser + "]" + "Setting the Host connection ");
			rimpl.setConnection(cpool.getConnection(asUser));
			log.finer("[" + asUser + "]" + "Host connection retrieved");
			Lease returnlease = landlord.newLease(rimpl, duration);
			log.finer("[" + asUser + "]" + "Lease obtained");
			StatisticMonitor.addAllocation();
			//AthenaConnectionImpl aconn = (AthenaConnectionImpl)
			// ResourceManager.getResourceManager().checkOutFromPool("ACONN");
			//aconn.setConnection(rimpl);
			//aconn.setLease(returnlease);
			log.finer("[" + asUser + "]" + "Returning new Connection to Client");
			Uuid theConnectionProxyID = UuidFactory.generate();
			RemoteConnection conn = (RemoteConnection) DefaultExporterManager.getManager().exportProxy(rimpl, "Connection", theConnectionProxyID);
			rimpl.setProxyID(theConnectionProxyID);
			AthenaConnection atconn = new AthenaConnectionImpl(conn, returnlease);
			return atconn;
		} catch (LeaseDeniedException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
		//PoolThread pt = new PoolThread(atconn);
		//pt.start();
	}

	/**
	 * Obtains the load that the service is under i.e. number of allocated
	 * connections
	 * 
	 * @return int load
	 * @since @throws
	 *             RemoteException if an RMI error occurs
	 */
	public int getLoad() throws RemoteException {
		int loadvalue = 0;
		synchronized (cpool) {
			for (int i = 0; i < cpool.getSize(); i++) {
				if (cpool.getStatusString(i).substring(1, 1).equals("A")) {
					System.out.println("Connection " + i + " is allocated");
					loadvalue++;
				}
			}
		}
		return loadvalue;
	}

	/**
	 * Gets the Statistics attribute of the AthenaRegistrationImpl object
	 * 
	 * @return The Statistics value
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public java.util.Vector getStatistics() throws RemoteException {
		return org.jini.projects.athena.service.StatisticMonitor.getStatistics();
	}

	/**
	 * Gets the Admin attribute of the AthenaRegistrationImpl object
	 * 
	 * @return The Admin value
	 * @exception java.rmi.RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public java.lang.Object getAdmin() throws java.rmi.RemoteException {
		if (adminObject == null) {
			while (jm == null) {
				Object ob = new Integer(0);
				synchronized (ob) {
					try {
						wait(100);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			System.out.println((jm == null ? "JoinManager is null" : "JoinManager is not null"));
			System.out.println((ldm == null ? "LDManager is null" : "LDManager is not null"));
			System.out.println("JM==NULL? " + (jm == null));
			adminObject = (AthenaAdminProxy) DefaultExporterManager.getManager().exportProxy(new AthenaAdminImpl(this.jm, this.ldm), "Service", UuidFactory.generate());
		}
		return adminObject;
		//        return new AthenaAdminImpl(this.jm, this.ldm);
	}

	private AthenaAdminProxy adminObject;

	public java.util.Properties getSystemConfig() throws RemoteException {
		Properties props = System.getProperties();
		Properties returnvalue = new Properties();
		Set propsenum = props.entrySet();
		Iterator iter = propsenum.iterator();
		while (iter.hasNext()) {
			Map.Entry entr = (Map.Entry) iter.next();
			if (entr.getKey().toString().indexOf("org.jini.projects.athena.") != -1) {
				//System.out.println("\t" + entr.getKey() + ": " +
				// entr.getValue());
				returnvalue.put(entr.getKey(), entr.getValue());
			}
		}
		returnvalue.put("os.name", System.getProperty("os.name"));
		returnvalue.put("os.arch", System.getProperty("os.arch"));
		returnvalue.put("os.version", System.getProperty("os.version"));
		returnvalue.put("java.vm.version", System.getProperty("java.vm.version"));
		returnvalue.put("user.name", System.getProperty("user.name"));
		return returnvalue;
	}

	/**
	 * Description of the Method
	 * 
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public void configure() throws RemoteException {
		nc = Integer.parseInt(props.getProperty("org.jini.projects.athena.service.numconnect"));
		cpool = new ConnectionPool(nc);
		StatisticMonitor.setConnectionPool(cpool);
		landlord = new org.jini.projects.athena.leasing.ConnectionLandlord();
		SystemManager.SYSTEMLANDLORD = landlord;
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			/*
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				// TODO Complete method stub for run
				System.out.println("Unexporting objects");
				//	DefaultExporterManager.getManager().relinquishAll();
				System.out.println("Terminating JoinManager");
				try {
					if (jm != null) {
						jm.terminate();
					}
					System.out.println("Terminating LookupDiscoveryManager");
					if (ldm != null) {
						ldm.terminate();
					}
				} catch (Exception ex) {
					System.err.println("System shutdown problem");
				}
				System.out.println("Killing ID File");
				//System.out.println(System.getProperty("user.dir") + "/" +
				// System.getProperty("org.jini.projects.athena.service.name") +
				// "servID.per");
				java.io.File file = new java.io.File(System.getProperty("user.dir") + "/" + System.getProperty("org.jini.projects.athena.service.name") + "servID.per");
				file.delete();
				if(System.getProperty("org.jini.projects.athena.shutdownClass")!=null){
					try {
						ShutdownOperations sd = (ShutdownOperations) Class.forName(System.getProperty("org.jini.projects.athena.shutdownClass")).newInstance();
						sd.execute();
					} catch (InstantiationException e) {
						// TODO Handle InstantiationException
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Handle IllegalAccessException
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Handle ClassNotFoundException
						e.printStackTrace();
					}
				}
			}
		}));
	}

	/**
	 * Description of the Method
	 * 
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 */
	/*
	 * public void destroy() throws RemoteException { System.out.println("Remote
	 * termination"); //this.terminate(); term = true; try { Thread.sleep(1); }
	 * catch (Exception ex) { } }
	 */
	/**
	 * Adds a feature to the LookupAttributes attribute of the
	 * AthenaRegistrationImpl object
	 * 
	 * @param entry
	 *                  The feature to be added to the LookupAttributes attribute
	 * @exception java.rmi.RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public void addLookupAttributes(net.jini.core.entry.Entry[] entry) throws java.rmi.RemoteException {
		this.jm.addAttributes(entry);
	}

	/**
	 * Gets the runtime Statistics for the current Athena instance
	 * 
	 * @return The Statistics value
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 */
	public HashMap getResourceDetails() throws RemoteException {
		HashMap map = new HashMap();
		map.put("Cache", ResourceManager.getResourceManager().getCachingDetails());
		map.put("Pool", ResourceManager.getResourceManager().getPoolingDetails());
		return map;
	}

	/*
	 * @see net.jini.security.proxytrust.ServerProxyTrust#getProxyVerifier()
	 */
	public TrustVerifier getProxyVerifier() throws RemoteException {
		// TODO Complete method stub for getProxyVerifier
		Object ob = DefaultExporterManager.getManager().getExportedProxy(regProxyID);
		return new AthenaRegistrationProxy.Verifier((AthenaRegistration) ob);
	}

	/*
	 * @see net.jini.export.ProxyAccessor#getProxy()
	 */
	public Object getProxy() {
		return DefaultExporterManager.getManager().getExportedProxy(regProxyID);
	}

	public void setProxyID(Uuid ID) {
		regProxyID = ID;
	}
}