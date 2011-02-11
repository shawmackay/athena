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

package org.jini.projects.athena.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.HostException;
import org.jini.projects.athena.service.AthenaLogger;
import org.jini.projects.athena.service.ChangeListener;
import org.jini.projects.athena.service.HostEvents;
import org.jini.projects.athena.service.ManagerListener;
import org.jini.projects.athena.service.StatePersistence;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.thor.service.ChangeEvent;

/**
 * Represents a Pool of connections to some source
 * 
 * @author calum 09 October 2001
 */
public class ConnectionPool implements ManagerListener {
	private long numrequests = 0L;
	
	private Vector PooledConnections = null;
	private int max_connections = -1;
	//new Vector(5);
	private ArrayList timeouts = new ArrayList();
	private long deftimeout = 120000L;
//	private String curUser;
//	private String curPass;
//	private String curURL;
//	private String curDriver;
	private int initialSize = 0;
	private Thread toutpoller = new Thread(new timeoutpoller());
	private boolean allowAllocations = true;
	private HashMap users = new HashMap();
	private boolean allocationAvailable = true;
	private Logger log = Logger.getLogger("org.jini.projects.athena.connection");

	//protected boolean canChooseConnection = false;
	/**
	 * Creates a set of empty connections and allows classes outside ths system
	 * to define
	 * 
	 * @since
	 */
	public ConnectionPool() {
		//Register a shutdown hook to terminate connections.
		log.info("Connection Shutdown hook added");
		Runtime.getRuntime().addShutdownHook(new DropConnections());
		if (System.getProperty("org.jini.projects.athena.connection.adhoctimeout") != null) {
			deftimeout = Integer.parseInt(System.getProperty("org.jini.projects.athena.connection.adhoctimeout"));
		}
		max_connections = System.getProperty("org.jini.projects.athena.connection.max") != null ? Integer.parseInt(System.getProperty("org.jini.projects.athena.connection.max")) : -1;
		if (max_connections == -1) {
			log.info("Connection Pool: Unlimited connections available.");
		} else
			log.info("Connection Pool: " + max_connections + " connections available.");
		toutpoller.start();
		SystemManager.getSystemChangeListener().registerChangeListener(new ConnectionPool.ConnectionChangeHook());
		SystemManager.registerManagerListener(this);
	}

	/**
	 * Creates a new Pool with the given parameters
	 * 
	 * @param size
	 *                   Number of connections to initially create
	 * @since
	 */
	public ConnectionPool(int size) {
		this();
		
		if (max_connections != -1 && max_connections < size) {
			log.warning("Connection Pool limited to current size");
			max_connections = size;
		}
		initialSize = size;
		PooledConnections = new Vector(size);
		String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
		if (connectionClass == null) {
			log.severe("Cannot connect without a connection class!");
			System.exit(1);
		}
		for (int j = 0; j < PooledConnections.capacity(); j++) {
			try {
				//SQLConnection pconn = new SQLConnection(Username,Password,
				// URL, Driver, j);
				SystemConnection sconn = (SystemConnection) Class.forName(connectionClass).newInstance();
				sconn.setReference(j);
				try {
					StatePersistence sp = (StatePersistence) AthenaLogger.restore(sconn.getPersistentFileName());
					sp.restoreTX();
					if (sp.getStackSize() != 0) {
						log.info("Conn. Txn: " + sp.tx.id);
						log.info("Restoring file: " + sconn.getPersistentFileName());
						log.info("Restoring state from " + sconn.getPersistentFileName() + " for Connection: " + j);
						RemoteConnectionImpl rconn = new RemoteConnectionImpl(sconn);
						try {
							rconn.restoreAll(sp);
						} catch (Exception ex) {
							log.severe("Restoration exception: " + ex.getMessage());
							ex.printStackTrace();
						}
						while (!rconn.canRelease()) {
							Thread.yield();
						}
						rconn.release();
					}
				} catch (java.io.IOException ioex) {
				}
				PooledConnections.add(sconn);
				timeouts.add(new Long(0L));
				log.info("Creating connection " + j + "....");
			} catch (Exception pcex) {
				log.severe("Connection " + j + ": " + pcex.getMessage());
				pcex.printStackTrace();
			}
		}
	}

	/**
	 * Obtains and allocates a free connection from the pool
	 * 
	 * @return An instance of a connection controlled by this connection pool
	 * @since
	 */
	public synchronized SystemConnection getConnection(String userName) throws HostException {
		numrequests++;
		boolean failedAcquire = false;
		if (allowAllocations) {
			try {
				synchronized (PooledConnections) {
					while (!failedAcquire) {
						for (int i = 0; i < PooledConnections.size(); i++) {
							SystemConnection pconn = (SystemConnection) PooledConnections.get(i);
							
							log.finest("Checking Connection " + i + " for " + userName);
							synchronized (pconn) {
								if (pconn.isConnected() && !pconn.isAllocated()) {
							
									log.finest("Request " + numrequests + ": .Allocating connection " + i + "to " + userName + ".........");
									try {
										pconn.allocate();
									} catch (Exception athex) {
										System.err.println(athex.getMessage());
										athex.printStackTrace();
									}
									log.finest("Returning connection now [" + userName + "]");
									
									if (userName != null)
										this.users.put(new Integer(i), userName);
		 							return pconn;
								}
								if (!pconn.isConnected()) {
									try {
										log.finest("Reconnecting connection " + i);
										
										pconn.reConnect();
										//Re-establish the timeout
										timeouts.set(i, new Long(System.currentTimeMillis() + deftimeout));
										pconn.allocate();
										if (userName != null)
											this.users.put(new Integer(i), userName);
										return pconn;
									} catch (Exception ex) {
										System.err.println(new java.util.Date() + ": " + ex.getMessage());
										ex.printStackTrace();
										log.fine("[" + userName + "]" + "Connection to host unavailable");
										throw new HostException("Connection to Host unavailable");
									}
								}
							}
						}
						if (max_connections != -1 && PooledConnections.size() >= max_connections) {							
							throw new HostException("A Connection is not available");
						} else {
							synchronized (PooledConnections) {
								try {
									int pos = PooledConnections.size();
									String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
									if (connectionClass == null) {
										System.err.println("Cannot connect without a connection class!");
										log.severe("[" + userName + "]" + "Cannot connect without a connection class");
										System.exit(1);
									}
									log.finer("*****" + numrequests + ": Creating ad-hoc connection \n" + pos);									
									SystemConnection sconn = (SystemConnection) Class.forName(connectionClass).newInstance();
									sconn.setReference(pos);
									try {
										StatePersistence sp = (StatePersistence) AthenaLogger.restore(sconn.getPersistentFileName());
										if (sp.getStackSize() != 0) {
											log.finer("Conn. Txn: " + sp.tx.id);
											log.finer("Restoring file: " + sconn.getPersistentFileName());
											log.finer("Restoring state from " + sconn.getPersistentFileName() + " for Connection: " + pos);
											RemoteConnectionImpl rconn = new RemoteConnectionImpl(sconn);
											rconn.restoreAll(sp);
											while (!rconn.canRelease()) {
												Thread.yield();
											}
											rconn.release();
										}
									} catch (java.io.IOException ioex) {
										//     log.finer(ioex.getMessage());
										//  ioex.printStackTrace();
									}
									sconn.allocate();
									Thread.yield();
									synchronized (PooledConnections) {
										PooledConnections.add(sconn);
										timeouts.add(new Long(System.currentTimeMillis() + deftimeout));
									}
									if (userName != null)
										this.users.put(new Integer(pos), userName);
									
									log.finer("*****" + numrequests + ": ad-hoc connection  created\n" + new Integer(PooledConnections.size() + 1));
									return sconn;
								} catch (Exception ex) {
									log.severe("Connection creation failed!");
									
									throw new HostException("Connection to Host unavailable");
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				//Failthrough for the wait on maximum connections
				log.finer("System interrupted returning null");
			}
		} else
			throw new HostException("Connection to Host unavailable");
		return null;
	}

	/**
	 * Obtains size of Connection Pool
	 * 
	 * @return current number of connections in the pool
	 * @since
	 */
	public synchronized int getSize() {
		return PooledConnections.size();
	}

	/**
	 * Returns the status string for one connection in the pool
	 * 
	 * @param poolIdx
	 *                   Index of the connection in the pool you want status
	 *                   information for
	 * @return status of requested connection
	 * @since
	 */
	public String getStatusString(int poolIdx) {
		SystemConnection pconn = (SystemConnection) PooledConnections.get(poolIdx);
		StringBuffer sbuff = new StringBuffer(4);
		try {
			sbuff.insert(0, pconn.isAllocated() ? "A" : "-");
			sbuff.insert(1, pconn.isConnected() ? "C" : "-");
			sbuff.insert(2, pconn.canFree() ? "D" : "S");
			sbuff.insert(3, pconn.inTransaction() ? "T" : "-");
			if (!pconn.isAllocated())
				users.put(new Integer(poolIdx), null);
			else if (users.get(new Integer(poolIdx)) != null)
				sbuff.append(users.get(new Integer(poolIdx)));
		} catch (Exception ex) {
		}
		return sbuff.toString();
	}

	/**
	 * Gets the allocated attribute of the ConnectionPool object
	 * 
	 * @param ConnectionIndex
	 *                   Description of Parameter
	 * @return The allocated value
	 * @since
	 */
	public boolean isAllocated(int ConnectionIndex) {
		SystemConnection conn = (SystemConnection) PooledConnections.get(ConnectionIndex);
		if (!conn.isAllocated()) {
			users.remove(new Integer(ConnectionIndex));
			return false;
		} else
			return true;
	}

	/**
	 * Gets the inTransaction attribute of the ConnectionPool object
	 * 
	 * @param ConnectionIndex
	 *                   Description of Parameter
	 * @return The inTransaction value
	 * @since
	 */
	public boolean isInTransaction(int ConnectionIndex) {
		try {
			SystemConnection conn = (SystemConnection) PooledConnections.get(ConnectionIndex);
			return conn.inTransaction();
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Prints the status of all connections in the pool
	 * 
	 * @since
	 */
	public void PrintStatus() {
		log.finer("Connection Pool Status");
		log.finer("----------------------");
		Iterator iter = PooledConnections.iterator();
		int i = 0;
		while (iter.hasNext()) {
			System.out.print(i++ + ": ");
			SystemConnection pconn = (SystemConnection) iter.next();
			System.out.print(pconn.isAllocated() ? "Allocated," : "Not Allocated, ");
			System.out.print(pconn.isConnected() ? "Connected," : "Not Connected, ");
			log.finer(pconn.canFree() ? "Dynamic" : "Static");
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param ConnectionIndex
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public synchronized boolean forceDeallocate(int ConnectionIndex) {
		try {
			SystemConnection conn = (SystemConnection) PooledConnections.get(ConnectionIndex);
			conn.release();
			return true;
		} catch (Exception ex) {
			log.finer(new java.util.Date() + ":  Err: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}


	
	
	public void notify(int event) {
		if (event == HostEvents.HIBERNATE) {
			log.finer("ConnectionPool: Will stop allocating connections");
			allocationAvailable = false;
			for (int i = 0; i < PooledConnections.size(); i++) {
				SystemConnection sconn = (SystemConnection) PooledConnections.get(i);
				try {
					if (sconn.inTransaction())
						sconn.rollback();
					sconn.setAutoAbort();
					sconn.close();
				} catch (Exception e) {
				}
			}
		}
		if (event == HostEvents.DBCLOSED) {
			log.finer("ConnectionPool: Will stop allocating connections");
			allocationAvailable = false;
			for (int i = 0; i < PooledConnections.size(); i++) {
				log.finer("Shutting down connection from reference " + i);
				SystemConnection sconn = (SystemConnection) PooledConnections.get(i);
				try {
					if (sconn.inTransaction())
						sconn.rollback();
					sconn.close();
				} catch (Exception e) {
					log.finer(e.getMessage());
					e.printStackTrace();
				} finally {
					sconn.setAutoAbort();
				}
			}
		}
		if (event == HostEvents.DBREOPENED) {
			log.finer("Reconnecting all connections");
			for (int i = 0; i < PooledConnections.size(); i++) {
				SystemConnection sconn = (SystemConnection) PooledConnections.get(i);
				try {
					sconn.reConnect();
				} catch (AthenaException e) {
					log.finer("Problem when Reconnecting : " + e.getMessage());
					e.printStackTrace();
				}
			}
			allocationAvailable = true;
		}
		if (event == HostEvents.CONNDROP) {
			for (int i = 0; i < PooledConnections.size(); i++) {
				log.finer("Closing all non-allocated connections");
				SystemConnection connection = (SystemConnection) PooledConnections.elementAt(i);
				if (!connection.isAllocated())
					try {
						connection.close();
					} catch (AthenaException e) {
						log.finer("Exception on closing");
					}
			}
		}
	}

	/**
	 * Description of the Class
	 * 
	 * @author calum
	 * 
	 * @author calum 09 October 2001
	 */
	class DropConnections extends Thread {
		/**
		 * Allos this class to be started within a thread
		 * 
		 * @since
		 */
		public void run() {
			log.log(Level.FINE, "Shutting down " + PooledConnections.size() + " connections.");
			for (int i = 0; i < PooledConnections.size(); i++) {
				SystemConnection pconn = (SystemConnection) PooledConnections.get(i);
				try {
					log.log(Level.FINE, "Rolling back");
					if (pconn.inTransaction())
						;
					pconn.rollback();
				} catch (Exception ex) {
					log.log(Level.SEVERE, "Cannot rollback connection " + i, ex);
				}
				try {
					log.log(Level.FINE, "Closing");
					pconn.close();
				} catch (Exception ex) {
					log.log(Level.SEVERE, "Cannot close connection " + i, ex);
				}
			}
		}
	}

	/**
	 * Description of the Class
	 * 
	 * @author calum
	 * 
	 * @author calum 09 October 2001
	 */
	class timeoutpoller implements Runnable {
		/**
		 * Main processing method for the timeoutpoller object
		 * 
		 * @since
		 */
		public void run() {
			SystemConnection conn = null;
			Long val = new Long(0L);
			for (;;) {
				try {
					Thread.sleep(deftimeout);
				} catch (InterruptedException inex) {
				}
				for (int i = 0; i < timeouts.size(); i++) {
					val = (Long) timeouts.get(i);
					if (val.longValue() > 0L) {
						//i.e is not defined through the numconnections
						// property or it has already been closed
						if (System.currentTimeMillis() > val.longValue()) {
							conn = (SystemConnection) PooledConnections.get(i);
							if (conn.isAllocated()) {
								timeouts.set(i, new Long(System.currentTimeMillis() + deftimeout * 2));
								log.finer("\t\t*******Timeout extended*******");
							} else {
								try {
									log.finer("\t\t*******Timeout expired - closing connection*******");
									conn.close();
									timeouts.set(i, new Long(-1));
								} catch (Exception ex) {
									System.err.println(new java.util.Date() + ": cannot close =>" + ex.getMessage());
								}
							}
						}
						//of System.currentTimeMillis()> val
					}
					// of val>0L
				}
				//of for loop 28*
			}
			//of for loop 1
		}
		//of run()
	}

	public class ConnectionChangeHook implements ChangeListener {
		public void notify(ChangeEvent cevt) {
			log.finer("The Connection Pool has been informed of a change");
			log.finer("Somebody has modified the following value: " + cevt.getItem().toString());
			log.finer("\tIt has now been set to : " + (cevt.getValue() != null ? cevt.getValue() : "<<null>>"));
			String itemname = (String) cevt.getItem();
			int newsize = 0;
			if (itemname.equals("org.jini.projects.athena.connection.adhoctimeout")) {
				System.getProperties().put("org.jini.projects.athena.connection.adhoctimeout", cevt.getValue());
				deftimeout = Integer.parseInt(System.getProperty("org.jini.projects.athena.connection.adhoctimeout"));
				log.finer("Set adhoctimeout to " + deftimeout + "ms .....this will apply to new connections");
			}
			if (itemname.equals("org.jini.projects.athena.service.numconnect")) {
				try {
					System.getProperties().put("org.jini.projects.athena.service.numconnect", cevt.getValue());
					newsize = Integer.parseInt((String) cevt.getValue());
					if (newsize > initialSize) {
						for (int i = initialSize; i < newsize; i++) {
							if (PooledConnections.size() >= newsize) {
								SystemConnection pconn = (SystemConnection) PooledConnections.get(i);
								if (!pconn.isConnected()) {
									pconn.reConnect();
								}
								timeouts.set(i, new Long(0L));
							} else {
								int pos = PooledConnections.size();
								String connectionClass = System.getProperty("org.jini.projects.athena.connection.class");
								if (connectionClass == null) {
									System.err.println("Cannot connect without a connection class!");
									System.exit(1);
								}
								log.finer("*****" + numrequests + ": Creating connection " + pos);
								SystemConnection sconn = (SystemConnection) Class.forName(connectionClass).newInstance();
								sconn.setReference(pos);
								try {
									StatePersistence sp = (StatePersistence) AthenaLogger.restore(sconn.getPersistentFileName());
									if (sp.getStackSize() != 0) {
										log.finer("Conn. Txn: " + sp.tx.id);
										log.finer("Restoring file: " + sconn.getPersistentFileName());
										log.finer("Restoring state from " + sconn.getPersistentFileName() + " for Connection: " + pos);
										RemoteConnectionImpl rconn = new RemoteConnectionImpl(sconn);
										rconn.restoreAll(sp);
										while (!rconn.canRelease()) {
											Thread.yield();
										}
										rconn.release();
									}
								} catch (java.io.IOException ioex) {
									//     log.finer(ioex.getMessage());
									//  ioex.printStackTrace();
								}
								synchronized (PooledConnections) {
									PooledConnections.add(sconn);
									timeouts.add(new Long(0L));
								}
							}
						}
					} else if (newsize < initialSize) {
						for (int i = initialSize; i > newsize; i--) {
							timeouts.set(i - 1, new Long(System.currentTimeMillis() + 100));
						}
					}
					initialSize = newsize;
					log.finer("Set number of always-on connections to: " + initialSize + ".....connections will deallocate presently");
				} catch (Exception ex) {
					log.finer("Error:" + ex.getMessage());
				}
			}
		}
	}
}