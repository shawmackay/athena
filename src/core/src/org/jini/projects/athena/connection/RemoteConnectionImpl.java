/**
 * Title:
 * <p>
 * 
 * Description:
 * <p>
 * 
 * Copyright: Copyright (c)
 * <p>
 * 
 * Company:
 * <p>
 * 
 * @author
 * 
 * @version 0.9community
 */

package org.jini.projects.athena.connection;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.export.ProxyAccessor;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ServerProxyTrust;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.HandleEngine;
import org.jini.projects.athena.command.Handler;
import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.exception.WrongReturnTypeException;
import org.jini.projects.athena.resources.Cacheable;
import org.jini.projects.athena.resources.ChunkedObject;
import org.jini.projects.athena.resources.FlashCachedObject;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.resultset.AthenaResultSetImpl;
import org.jini.projects.athena.resultset.ChunkLoader;
import org.jini.projects.athena.resultset.ChunkLoaderImpl;
import org.jini.projects.athena.resultset.ChunkedResultSetImpl;
import org.jini.projects.athena.resultset.DisconnectedResultSetImpl;
import org.jini.projects.athena.resultset.RemoteResultSet;
import org.jini.projects.athena.resultset.RemoteResultSetImpl;
import org.jini.projects.athena.resultset.ResultSetUpdater;
import org.jini.projects.athena.resultset.SystemResultSet;
import org.jini.projects.athena.scripting.Environment;
import org.jini.projects.athena.service.AthenaLogger;
import org.jini.projects.athena.service.StatePersistence;
import org.jini.projects.athena.service.StatisticMonitor;
import org.jini.projects.athena.service.SystemManager;
import org.jini.projects.athena.service.constrainable.AthenaConnectionProxy;
import org.jini.projects.athena.util.builders.LocalResultSetBuilder;
import org.jini.projects.athena.util.builders.LogExceptionWrapper;


 
/**
 * Concrete class implementing AthenaConnection and TransactionParticipant. This
 * class is analogous to java.sql.Connection and java.sql.Statement
 * 
 * @author calum
 *  
 */
public class RemoteConnectionImpl extends AbstractRConnectionImpl implements TransactionParticipant, ServerProxyTrust, ProxyAccessor {

	private Uuid proxyID;
	static final long serialVersionUID = -6912030408605116261L;
	Logger log = Logger.getLogger("org.jini.projects.athena.connection");
	static final int TEST = 1;
	private HostErrorHandler errHandler = null;
	// Keep a list of all chunk objects that have been created
	private ArrayList onlinechunks = new ArrayList();
	private Uuid participantProxyUuid;

	/**
	 * Default Constructor
	 * 
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public RemoteConnectionImpl() throws RemoteException {

	}

	/**
	 * Sets the connection. Used in caching to avoid object creation
	 */
	public void setConnection(SystemConnection pc) {
		log.finer("Object reused with PConn");
		STATEFILE = pc.getPersistentFileName();
		// leaseCookie = new AthenaCookie(index,2000);
		log.finer("WIll log to: " + STATEFILE + "(" + pc.getPersistentFileName() + ")");
		sp = new StatePersistence();
		try {
			pc.resetAutoAbort();
		} catch (AthenaException e) {
			// URGENT Handle AthenaException
			e.printStackTrace();
		}
		try {
			sp = (StatePersistence) AthenaLogger.restore(STATEFILE);
		} catch (java.io.IOException ex) {
			// A connection which has not had a transaction run through it
			// will not hava a state file
			if (ex.getMessage().indexOf("The system cannot find the file specified") == -1) {
				System.err.println(new java.util.Date() + ": I/O Error =>" + ex.getMessage());
			}
		} catch (ClassNotFoundException cnfex) {
			System.err.println("LOG PANIC!!!!");
			cnfex.printStackTrace();
		} catch (Exception ex) {
			System.err.println(new java.util.Date() + ": RemoteConnection: Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		if (sp == null) {
			sp = new StatePersistence();
		}
		isreleased = false;
		conn = pc;
		errHandler = conn.getErrorHandler();
	}

	/**
	 * Object is created and passed a PooledConnection. This will also restore
	 * state
	 * 
	 * @param pc
	 *                  PooledConnection to assign to this instance
	 * @exception RemoteException
	 *                        Description of Exception
	 * @since
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public RemoteConnectionImpl(SystemConnection pc) throws RemoteException {
		this();

		l.finest("Object created with existing connection");

		setConnection(pc);
	}

	public boolean canRelease(boolean block) throws RemoteException {
		if (block) {
			long start = System.currentTimeMillis();
			long end = start;
			while (!canRelease() && (end - start < 5000)) {
				Thread.yield();
				end = System.currentTimeMillis();
			}
		}
		return canRelease();
	}

	public void release() throws RemoteException {
		super.release();
		log.finest("Checking into Object Pool");
		for (Iterator iter = onlinechunks.iterator(); iter.hasNext();) {
			((ChunkedObject) iter.next()).cleanup();
		}
		ResourceManager.getResourceManager().checkInToPool("RCONN", this);
		DefaultExporterManager.getManager("default").relinquish("Connection", this.proxyID);
	}

	public org.jini.projects.athena.command.Command getCommand() throws RemoteException {
		try {
			return conn.getSystemCommand();
		} catch (Exception ex) {
			LogExceptionWrapper lex;
			if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
				lex = new LogExceptionWrapper(ex, true, false);
			else
				lex = new LogExceptionWrapper(ex, false, false);
			errHandler.handleHostException(lex);
			throw new RemoteException("Command Cannot be factoried", ex);
		}
	}

	public void setProxyID(Uuid proxyID) {
		this.proxyID = proxyID;
	}

	public synchronized Object executeUpdate(Object command, Transaction tx) throws CannotUpdateException, RemoteException {

		if (SystemManager.getSystemState() == SystemManager.SCHEDULEDDOWNTIME)
			throw new CannotUpdateException("Scheduled Downtime");
		if (SystemManager.getSystemState() == SystemManager.DBOFFLINE)
			throw new CannotUpdateException("Host Offline");
		// If a client inititates a transaction, which requires a retry on the
		// rollforward of the SystemConnection
		// a situation can occur when mahalo returns that a commit has happened
		// but transaction.commit does not
		// block until the actual rollforward of data has occured.
		// What may happen is that a client may create & commit a transaction,
		// and then issue more commands on that connection
		// before the transaction is rolled-forward properly, this may result
		// in a CannotExecute/Update Exception, or if addditional commands
		// added to the command stack used to reinitialise the rollforward.
		// The following code blocks the call until TxProcessState ==
		// NO_STANDING_TX_PROCESSES
		while (TxProcessState != NO_STANDING_TX_PROCESSES) {
			Thread.yield();
		}

		if (tx == null) {
			try {
				if (command instanceof Command) {
					org.jini.projects.athena.command.Command comm = (org.jini.projects.athena.command.Command) command;
					Handler handle = HandleEngine.getEngine().getHandlerFor(comm.getCallName());
					// Object xvalue = handleCommand(comm, handle);
					while (handle.nextCommand()) {
						StatisticMonitor.addOperation();
						Object xvalue;
						xvalue = handleCommand(comm, handle);
						System.out.println("XVALUE class: " + xvalue.getClass().getName());
						if (xvalue instanceof String) {
							log.log(Level.FINEST, "Executing a string");
							conn.issueCommand((String) xvalue);
						}
						if (xvalue instanceof org.jini.projects.athena.command.dialect.Dialect) {
							log.log(Level.FINEST, "Executing a Dialect");
							return processDialect(comm, (Dialect) xvalue);
						}
						if(xvalue instanceof Environment){
						    log.info("Running script");
						    Environment scriptEnv = (Environment) xvalue;
						    scriptEnv.setVariable("connection", this.conn);
						    scriptEnv.setVariable("command", command);
						    try {
						        System.out.println("Executing script");
		                        scriptEnv.execute();
		                    } catch (Exception e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                    }
						}
						if (xvalue instanceof Command) {
							log.log(Level.FINEST, "Executing a Command");
							try {
								return conn.issueCommand(xvalue);
							} catch (Exception ex) {
								System.out.println("Error during execution");
								throw new RemoteException("Error during Execution", ex);
							}
						}
					}
				
				} else {
					StatisticMonitor.addOperation();
					conn.issueCommand(command);
				}
			} catch (Exception ex) {
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper(ex, true, false);
				else
					lex = new LogExceptionWrapper(ex, false, false);
				errHandler.handleHostException(lex);
				rollBack();
				throw new CannotUpdateException("Error during Update - " + ex.getMessage());
			}
			rollForward();
			return new Boolean(true);
		}

		ServerTransaction srv = (ServerTransaction) tx;

		if (sp.tx != null) {
			if (sp.tx.id != srv.id)
				throw new CannotUpdateException("Please finalise one transaction before beginning another");
		} else {
			try {
				log.log(Level.FINER, "Waiting to join transaction!");
				participantProxyUuid = UuidFactory.generate();
				TransactionParticipant proxy = (TransactionParticipant) DefaultExporterManager.getManager().exportProxy((TransactionParticipant) this, "Participants", participantProxyUuid);
				srv.join(proxy, sp.crashCount);
				log.log(Level.FINER, "Transaction Joined");
				// Bugfix: 13/01/03 - Ensures that the transaction data is not
				// stored before joining.
				sp.setTx(srv);
				AthenaXid athenaXid = generateXid();
				conn.setTransactionID(athenaXid);
				StatisticMonitor.addTransaction();
			} catch (CannotJoinException cjex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Cannot join transaction", cjex, true, false);
				else
					lex = new LogExceptionWrapper("Cannot join transaction", cjex, false, false);
				errHandler.handleHostException(lex);
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: Cannot join transaction (" + cjex.getMessage() + ")");
				return new Boolean(false);
			} catch (UnknownTransactionException utex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Transaction not available to join", utex, true, false);
				else
					lex = new LogExceptionWrapper("Transaction not available to join", utex, false, false);
				errHandler.handleHostException(lex);
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: Transaction not available (" + utex.getMessage() + ")");
				return new Boolean(false);
			} catch (net.jini.core.transaction.server.CrashCountException ccex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Crash count failure", ccex, true, false);
				else
					lex = new LogExceptionWrapper("Crash count failure", ccex, false, false);
				errHandler.handleHostException(lex);
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: Crash count failure (" + ccex.getMessage() + ")");
				return new Boolean(false);
			} catch (org.jini.projects.athena.exception.AthenaException athex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Athena Failure", athex, true, false);
				else
					lex = new LogExceptionWrapper("Athena Failure", athex, false, false);
				errHandler.handleHostException(lex);
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: Athenafailure (" + athex.getMessage() + ")");
				return new Boolean(false);
			} catch (Exception ex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("General exception", ex, true, false);
				else
					lex = new LogExceptionWrapper("General exception", ex, false, false);
				if(errHandler!=null)
				errHandler.handleHostException(lex);
				else {
					ex.printStackTrace();
				}
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: " + ex.getMessage());
				ex.printStackTrace();
				return new Boolean(false);
			}
		}

		try {
			// Stop release of connection until commit/rollback
			canrelease = false;
			log.log(Level.FINEST, "Current command stack is: " + sp.getStackSize());
			sp.addCommand(command);
			log.log(Level.FINEST, "Commands in Buffer: " + sp.getStackSize());
			log.log(Level.FINEST, "New command stack is: " + sp.getStackSize());
			log.log(Level.FINEST, "\tCommand: " + command.toString());
			log.log(Level.FINEST, "\tTransaction ID: " + srv.id);
			log.log(Level.FINEST, "\tValid passed tx: " + (tx == null ? false : true));
			log.log(Level.FINEST, "\tValid txmgr: " + (sp.tx.mgr == null ? false : true));
			log.log(Level.FINEST, "\tCrashCount: " + sp.crashCount);
			// conn.setTransactionFlag(true);
			try {
				if (command instanceof Command) {
					org.jini.projects.athena.command.Command comm = (org.jini.projects.athena.command.Command) command;
					Handler handle = HandleEngine.getEngine().getHandlerFor(comm.getCallName());
					// Object xvalue = handleCommand(comm, handle);
					while (handle.nextCommand()) {
						StatisticMonitor.addOperation();
						Object xvalue;
						xvalue = handleCommand(comm, handle);
						log.info("XVALUE class: " + xvalue.getClass().getName());
						if (xvalue instanceof String) {
							log.log(Level.FINEST, "Executing a string");
							conn.issueCommand((String) xvalue);
						}
						if (xvalue instanceof org.jini.projects.athena.command.dialect.Dialect) {
							return processDialect(comm, (Dialect) xvalue);
						}
						if(xvalue instanceof Environment){
						    log.info("Running script");
						    Environment scriptEnv = (Environment) xvalue;
						    System.out.println("Env class: " + scriptEnv.getClass().getName());
						    scriptEnv.setVariable("connection", this.conn);
						    scriptEnv.setVariable("command", command);
						    try {
						        System.out.println("Executing script");
		                        scriptEnv.execute();
		                    } catch (Exception e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                    }
						}
						if (xvalue instanceof Command) {
							log.log(Level.FINEST, "Executing a Command");
							try {
								return conn.issueCommand(xvalue);
							} catch (Exception ex) {
								System.err.println("Error during execution");
								return null;
							}
						}
					}
				} else {
					StatisticMonitor.addOperation();
					conn.issueCommand(command);
				}
			} catch (Exception issueex) {
				System.err.println("Issuing Exception: " + issueex.getMessage() + "......aborting txn");
				log.log(Level.SEVERE, "Can't issue command: " + srv.id, issueex);
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Can't issue command", issueex, true, false);
				else
					lex = new LogExceptionWrapper("Can't issue command", issueex, false, false);
				errHandler.handleHostException(lex);
				// issueex.printStackTrace();
				// conn.setTransactionFlag(false);
				throw new CannotUpdateException("Error in issuing command - " + issueex.getMessage());
			}
			// } end of synchronized(conn)
			AthenaLogger.persist(STATEFILE, sp);
			log.log(Level.FINEST, "Persisted state");
		} catch (Exception ex) {
			StatisticMonitor.addFailure();
			System.out.println(new java.util.Date() + ": RemoteConnection: Error: " + ex.getMessage());
			log.log(Level.SEVERE, "Error in update", ex);
			LogExceptionWrapper lex;
			if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
				lex = new LogExceptionWrapper("Error in update", ex, true, false);
			else
				lex = new LogExceptionWrapper("Error in update", ex, false, false);
			if(errHandler != null)
			errHandler.handleHostException(lex);
			throw new RemoteException("Error in update", ex);
		}
		return new Boolean(true);
	}

	private AthenaXid generateXid() {
		Long longtxId = new Long(sp.tx.id);
		byte[] globalID = toBytes(longtxId);
		byte[] mostSig = toBytes(participantProxyUuid.getMostSignificantBits());
		byte[] leastSig = toBytes(participantProxyUuid.getLeastSignificantBits());
		byte[] branchQualifier =new byte[mostSig.length + leastSig.length];
		for(int i=0;i<mostSig.length;i++){
			branchQualifier[i] = mostSig[i];
		}
		for(int i=0;i<leastSig.length;i++){
			branchQualifier[i + mostSig.length-1] = leastSig[i];
		}
		AthenaXid athenaXid = new AthenaXid(0,globalID, branchQualifier);
		return athenaXid;
	}

	public synchronized AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		if (SystemManager.getSystemState() == SystemManager.SCHEDULEDDOWNTIME)
			throw new CannotExecuteException("Scheduled Downtime");
		if (SystemManager.getSystemState() == SystemManager.DBOFFLINE)
			throw new CannotExecuteException("Host Offline");
		Object x = exec(command);
		if (x != null)
			if (x instanceof AthenaResultSet)
				return (AthenaResultSet) x;
			else {
				log.log(Level.WARNING, "Wrong result type");
				throw new WrongReturnTypeException("Cannot build a ResultSet from this host object");
			}
		return null;
	}

	/*
	 * TODO: Add capability for multiple commands Currently broken - only the
	 * last resultset is returned to the user
	 */
	private Object exec(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		// If a client inititates a transaction, which requires a retry on the
		// rollforward of the SystemConnection
		// a situation can occur when mahalo returns that a commit has happened
		// but transaction.commit does not
		// block until the actual rollforward of data has occured.
		// What may happen is that a client may create & commit a transaction,
		// and then issue more commands on that connection
		// before the transaction is rolled-forward properly, this may result
		// in a CannotExecute/Update Exception, or if addditional commands
		// added to the command stack used to reinitialise the rollforward.
		// The following code blocks the call until TxProcessState ==
		// NO_STANDING_TX_PROCESSES
		boolean runnow = true;
		Handler handle = null;
		StringBuffer cacheKey = new StringBuffer(30);
		while (TxProcessState != NO_STANDING_TX_PROCESSES) {
			try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
			if (runnow) {
				log.log(Level.FINE, "RemoteConnection: System waiting for tx process completion");
				runnow = !runnow;
			}
		}
		StatisticMonitor.addOperation();
		log.log(Level.FINEST, "RemoteConnection: Executing a query");
		RemoteResultSet rrs = null;
		if (command instanceof org.jini.projects.athena.command.Command) {
			log.log(Level.FINEST, "execing a command");
			org.jini.projects.athena.command.Command comm = (org.jini.projects.athena.command.Command) command;
			handle = HandleEngine.getEngine().getHandlerFor(comm.getCallName());
			while (handle.nextCommand()) {
				Object xvalue;
				StatisticMonitor.addOperation();
				AthenaResultSet inCache = checkCache(cacheKey, comm);
				if (inCache != null) {
					return inCache;
				}
				xvalue = handleCommand(comm, handle);
				System.out.println("Query XVALUE class: " + xvalue.getClass().getName());
				if (xvalue instanceof String) {
					log.log(Level.FINEST, "RemoteConnection: Executing a Dialect");
					rrs = new RemoteResultSetImpl(conn, (String) xvalue);
				}
				if (xvalue instanceof org.jini.projects.athena.command.dialect.Dialect) {
					Object dialectResult = processDialect(comm, (Dialect) xvalue);
					if (dialectResult instanceof RemoteResultSet)
						rrs = (RemoteResultSet) dialectResult;
					else
						return dialectResult;
				}
				if (xvalue instanceof Command) {
					log.log(Level.FINEST, "RemoteConnection: Executing a Command");
					try {
						return conn.issueCommand(xvalue);
					} catch (Exception ex) {
						LogExceptionWrapper lex;
						if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
							lex = new LogExceptionWrapper("Error during execution", ex, true, false);
						else
							lex = new LogExceptionWrapper("Error during execution", ex, false, false);
						if(errHandler!=null)
						errHandler.handleHostException(lex);
						else 
							ex.printStackTrace();
						return null;
					}
				}
				if(xvalue instanceof Environment){
				    log.info("Running script");
				    Environment scriptEnv = (Environment) xvalue;
				    scriptEnv.setVariable("connection", this.conn);
				    scriptEnv.setVariable("command", command);
				    try {
				        System.out.println("Executing script");
                        scriptEnv.execute();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
				}
			}
		} else if (command instanceof String) {
			log.log(Level.FINER, "Executing a string");
			StatisticMonitor.addOperation();
			rrs = new RemoteResultSetImpl(conn, (String) command);
		}
		if (rrs == null) {
			log.log(Level.FINE, "No records...returning null");
			return null;
		}
		if (this.conntype == REMOTE) {
			log.info("Exporting resultset");
			RemoteResultSet proxy = (RemoteResultSet) DefaultExporterManager.getManager().exportProxy(rrs, "ResultSet", UuidFactory.generate());
			return new AthenaResultSetImpl(proxy);
		}
		if (this.conntype == LOCAL)
			return buildLocalResultSet(rrs, command, cacheKey, handle);
		if (this.conntype == DISCONNECTED)
			return buildChunkedResultSet(rrs, cacheKey);
		log.log(Level.INFO, "Returning no resultset whatsoever.....");
		return null;
	}

	private Object handleCommand(Command comm, Handler handle) throws CannotExecuteException {
		Object xvalue;
		log.log(Level.FINER, "Running a handler");
		handle.setCommandObject(comm);
		tuning = handle.getTuningParameters();
		if (tuning != null)
			this.tuned = false;
		try {
			log.log(Level.FINEST, "Getting execution Object");
			xvalue = handle.getExecutionObject();
		} catch (org.jini.projects.athena.exception.ValidationException vex) {
			log.log(Level.INFO, "Command failed validation");
			throw new CannotExecuteException("Validation failure");
		}
		return xvalue;
	}

	private AthenaResultSet checkCache(StringBuffer cacheKey, Command comm) {
		AthenaResultSet cachedRSet = null;
		cacheKey.append(comm.getCallName());
		TreeMap map = comm.getParameters();
		Set hashSet = map.entrySet();
		Iterator iter = hashSet.iterator();
		while (iter.hasNext()) {
			Map.Entry mapent = (Map.Entry) iter.next();
			cacheKey.append(mapent.getValue());
		}
		if (this.conntype == LOCAL) {
			// If the object already exists in the cache just return it.
			log.log(Level.FINER, "Looking in cache for " + cacheKey);
			Cacheable cached = ResourceManager.getResourceManager().enquireCache("RSET", cacheKey.toString());
			if (cached != null) {
				log.log(Level.FINEST, "Item found in cache");
				cachedRSet = (AthenaResultSetImpl) cached.getObject();
			}
		}
		return cachedRSet;
	}

	private AthenaResultSet buildChunkedResultSet(RemoteResultSet rrs, StringBuffer cacheKey) throws RemoteException {
		log.fine("Building disconnected");
		int listCapacity = getListCapacity();
		ArrayList keeponserver = LocalResultSetBuilder.buildlocal(this.conn, rrs, listCapacity);
		HashMap columndetails = LocalResultSetBuilder.buildColDetails(rrs);
		if (rrs != null) {
			rrs.close();
		}
		log.fine("Checking server");
		if (keeponserver == null) {
			throw new RemoteException("REMOTE:", new EmptyResultSetException("No results available"));
		}
		log.fine("Returning Chunked resultset");
		
		ChunkedObject chunk;
		int chunksize = 100;
		if (tuned && tuning.containsKey("chunksize")) {
			chunksize = Integer.parseInt((String) tuning.get("chunksize"));
			log.finer("Chunk size set to " + chunksize);
		}
		if (cacheKey.length() == 0) {
			chunk = new ChunkedObject(keeponserver, chunksize);
		} else {
			chunk = new ChunkedObject(keeponserver, chunksize, cacheKey.toString());
		}
		chunk.chunkNow();
		onlinechunks.add(chunk);
		ChunkLoaderImpl impl = new ChunkLoaderImpl(chunk);
		ChunkLoader chunker = (ChunkLoader) DefaultExporterManager.getManager().exportProxy(impl, "ResultSet", UuidFactory.generate());
		return new ChunkedResultSetImpl(columndetails, chunker);
	}

	private AthenaResultSet buildLocalResultSet(RemoteResultSet rrs, Object command, StringBuffer cacheKey, Handler handle) throws RemoteException {
		ArrayList returntouser;
		HashMap columndetails;
		int listCapacity = getListCapacity();
		// synchronized (rrs) {
		log.log(Level.FINEST, "Building local");
		returntouser = LocalResultSetBuilder.buildlocal(conn, rrs, listCapacity);
		columndetails = LocalResultSetBuilder.buildColDetails(rrs);
		if (rrs != null) {
			rrs.close();
		}
		if (returntouser == null) {
			throw new RemoteException("REMOTE:", new EmptyResultSetException("No results available"));
		}
		// } end of synchronized(rrs)
		AthenaResultSet aset = new AthenaResultSetImpl(returntouser, columndetails);
		if (command instanceof Command) {
			if (handle.isToBeCached()) {
				FlashCachedObject fco = new FlashCachedObject(aset, cacheKey.toString(), 4, new ResultSetUpdater(null), command);
				ResourceManager.getResourceManager().addObjectToCache("RSET", fco);
			} else
				System.out.println("\t\tLocalresultset will not be cached");
		}
		return aset;
	}

	private int getListCapacity() {
		int listCapacity = 10;
		if (tuned && tuning.containsKey("listCapacity"))
			listCapacity = Integer.parseInt((String) tuning.get("listCapacity"));
		return listCapacity;
	}

	public AthenaResultSet[] executeBatchQuery(Object[] commands) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		AthenaResultSet[] batch = new AthenaResultSet[commands.length];
		log.log(Level.FINEST, "Executing a query batch");
		for (int i = 0; i < commands.length; i++) {
			batch[i] = executeQuery(commands[i]);
		}
		return batch;
	}
	
	private byte[] toBytes(long n)
	{
	byte[] b = new byte[8];
	b[7] = (byte) (n);
	n >>>= 8;
	b[6] = (byte) (n);
	n >>>= 8;
	b[5] = (byte) (n);
	n >>>= 8;
	b[4] = (byte) (n);
	n >>>= 8;
	b[3] = (byte) (n);
	n >>>= 8;
	b[2] = (byte) (n);
	n >>>= 8;
	b[1] = (byte) (n);
	n >>>= 8;
	b[0] = (byte) (n);

	return b;
	}

	/**
	 * Transaction Manager callback to prepare for commit within this
	 * transaction
	 * 
	 * @param mgr
	 *                  Transaction Manager proxy identifying instance of <CODE>
	 *                  TransactionManager</CODE> is controlling htis distributed
	 *                  transaction
	 * @param id
	 *                  Transaction Identifier
	 * @return Success value
	 * @since
	 * @throws UnknownTransactionException
	 *                   thrown if transaction manager does not hold information
	 *                   pertaining to <CODE>id</CODE>
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public int prepare(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		synchronized (this) {
			log.log(Level.FINEST, "Preparing....");
			try {
				
				if (!conn.prepare() && conn.isAutoAbortSet() == true) {
					log.log(Level.FINEST, "Connection's Auto abort flag is set!");
					rollBack();
					sp.setState(TransactionConstants.ABORTED);
					conn.resetAutoAbort();
					log.finest("Aborting");
					return TransactionConstants.ABORTED;
				}
				log.finest("Setting to VOTING");
				sp.setState(TransactionConstants.VOTING);
				log.finest("Persisting Statefile");
				AthenaLogger.persist(STATEFILE, sp);
				log.finest("Statefile persisted");
			} catch (Exception ex) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Participant cannot Prepare", ex, true, false);
				else
					lex = new LogExceptionWrapper("Participant cannot prepare", ex, false, false);
				errHandler.handleHostException(lex);
				// System.err.println(new java.util.Date() + ":
				// RemoteConnection:
				// Error:" + ex.getMessage());
				rollBack();
				log.log(Level.INFO, conn.getPersistentFileName() + ": RETURNING ABORTED");
				sp.setState(TransactionConstants.ABORTED);
				DefaultExporterManager.getManager().scheduleRelinquish("Participants", participantProxyUuid, 15000);
				return TransactionConstants.ABORTED;
			}
			try {
				log.finest("Persisting Statefile");
				AthenaLogger.persist(STATEFILE, sp);
				if (System.getProperty("org.jini.projects.athena.stall") != null) {
					System.out.println("Persisted...prepared....waiting");
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Connection state could not be persisted", e, false, true);
				else
					lex = new LogExceptionWrapper("Connection state could not be persisted", e, false, false);
				errHandler.handleHostException(lex);
			}
			log.finest("going to PREPARED state");
			log.log(Level.FINEST, conn.getPersistentFileName() + ": RETURNING PREPARED");
			sp.setState(TransactionConstants.PREPARED);
		}
		return TransactionConstants.PREPARED;
	}

	/**
	 * Transaction Manager callback to commit and rollforward this participant
	 * actions within a transaction
	 * 
	 * @param mgr
	 *                  Transaction Manager proxy identifying instance of <CODE>
	 *                  TransactionManager</CODE> is controlling this distributed
	 *                  transaction
	 * @param id
	 *                  Transaction Identifier
	 * @since
	 * @throws UnknownTransactionException
	 *                   thrown if transaction manager does not hold information
	 *                   pertaining to <CODE>id</CODE>
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public void commit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		synchronized (this) {
			TxProcessState = TRYINGTOCOMMIT;
			log.log(Level.FINER, "Commiting....");
			try {
				if (System.getProperty("org.jini.projects.athena.stall") != null) {
					System.out.println("Registering commit - Kill process now to test txn recovery");
					Thread.sleep(5000);
				}
				sp.setTx(null);
				if (!rollForward())
					throw new RemoteException("Cannot commit");
				DefaultExporterManager.getManager().scheduleRelinquish("Participants", participantProxyUuid, 15000);
			} catch (Exception e) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Participane cannot commit", e, true, false);
				else
					lex = new LogExceptionWrapper("Participant cannot commit", e, false, false);
				if(errHandler!=null)
				errHandler.handleHostException(lex);
				else
					lex.printStackTrace();
				TxProcessState = NO_STANDING_TX_PROCESSES;
				try {
					conn.clearTransactionFlag();
				} catch (Exception ex) {
				}
				sp.clearStack();
				canrelease = true;
				throw new RemoteException("Cannot commit");
			}
		}
	}

	/**
	 * Transaction Manager callback to abort and rollback this participant
	 * actions within a transaction
	 * 
	 * @param mgr
	 *                  Transaction Manager proxy identifying instance of <CODE>
	 *                  TransactionManager</CODE> is controlling htis distributed
	 *                  transaction
	 * @param id
	 *                  Transaction Identifier
	 * @since
	 * @throws UnknownTransactionException
	 *                   thrown if transaction manager does not hold information
	 *                   pertaining to <CODE>id</CODE>
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public void abort(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		synchronized (this) {
			TxProcessState = TRYINGTOABORT;
			log.log(Level.FINER, "Aborting....");
			try {
				if (System.getProperty("org.jini.projects.athena.stall") != null) {
					System.out.println(new java.util.Date() + ": RemoteConnection: Registering abort");
					// Thread.sleep(5000);
				}
				sp.setTx(null);
				rollBack();
				log.log(Level.FINER, "Successfully Aborted");
			} catch (Exception e) {
				StatisticMonitor.addFailure();
				LogExceptionWrapper lex;
				if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
					lex = new LogExceptionWrapper("Participant cannot abort", e, true, false);
				else
					lex = new LogExceptionWrapper("Participant cannot abort", e, false, false);
				errHandler.handleHostException(lex);
				TxProcessState = NO_STANDING_TX_PROCESSES;
			}
		}
	}

	/**
	 * Fires prepare and commit in one remote call.
	 * 
	 * @param mgr
	 *                  Transaction Manager proxy identifying instance of <CODE>
	 *                  TransactionManager</CODE> is controlling htis distributed
	 *                  transaction
	 * @param id
	 *                  Transaction Identifier
	 * @return Success value
	 * @since
	 * @throws UnknownTransactionException
	 *                   thrown if transaction manager does not hold information
	 *                   pertaining to <CODE>id</CODE>
	 * @throws RemoteException
	 *                   Standard network error
	 */
	public int prepareAndCommit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		synchronized (this) {
			log.log(Level.FINEST, "Preparing and commiting (one operation)");
			
			int result = prepare(mgr, id);
			if (result == TransactionConstants.PREPARED) {
				result = TransactionConstants.COMMITTED;
				try {
					commit(mgr, id);
				} catch (Exception ex) {
					LogExceptionWrapper lex;
					ex.printStackTrace();
					if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
						lex = new LogExceptionWrapper("Problem in prepareAndCommit", ex, true, false);
					else
						lex = new LogExceptionWrapper("Problem in prepareAndCommit", ex, false, false);
					errHandler.handleHostException(lex);
					result = TransactionConstants.ABORTED;
				}
			}
			return result;
		}
	}

	public AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		if (SystemManager.getSystemState() == SystemManager.SCHEDULEDDOWNTIME)
			throw new CannotExecuteException("Scheduled Downtime");
		if (SystemManager.getSystemState() == SystemManager.DBOFFLINE)
			throw new CannotExecuteException("Host Offline");
		// If a client inititates a transaction, which requires a retry on the
		// rollforward of the SystemConnection
		// a situation can occur when mahalo returns that a commit has happened
		// but transaction.commit does not
		// block until the actual rollforward of data has occured.
		// What may happen is that a client may create & commit a transaction,
		// and then issue more commands on that connection
		// before the transaction is rolled-forward properly, this may result
		// in a CannotExecute/Update Exception, or if addditional commands
		// added to the command stack used to reinitialise the rollforward.
		// The following code blocks the call until TxProcessState ==
		// NO_STANDING_TX_PROCESSES
		while (TxProcessState != NO_STANDING_TX_PROCESSES) {
			Thread.yield();
		}
		log.log(Level.FINEST, "executing a query");
		RemoteResultSet rrs = new RemoteResultSetImpl(conn, command, params);
		// Return as a local resultset
		if (this.conntype == REMOTE) {
			// System.out.println("Return rrs");
			return new AthenaResultSetImpl(rrs);
		}
		// Return as a local resultset
		int listCapacity = getListCapacity();
		if (this.conntype == LOCAL) {
			log.log(Level.FINEST, "Running local");
			ArrayList returntouser = LocalResultSetBuilder.buildlocal(conn, rrs, listCapacity);
			// System.out.println("Table size: " + returntouser.size());
			HashMap columndetails = LocalResultSetBuilder.buildColDetails(rrs);
			rrs.close();
			if (returntouser == null) {
				throw new EmptyResultSetException(new java.util.Date() + ": RemoteConnection: No results available");
			}
			return new AthenaResultSetImpl(returntouser, columndetails);
		}
		if (this.conntype == DISCONNECTED) {
			log.log(Level.FINEST, "Running disconnected");
			ArrayList keeponserver = LocalResultSetBuilder.buildlocal(conn, rrs, listCapacity);
			HashMap columndetails = LocalResultSetBuilder.buildColDetails(rrs);
			rrs.close();
			if (keeponserver == null) {
				throw new EmptyResultSetException(new java.util.Date() + ": RemoteConnection: No results available");
			}
			return new AthenaResultSetImpl(new DisconnectedResultSetImpl(keeponserver, columndetails));
		}
		return null;
	}

	public AthenaResultSet[] executeBatchQuery(Object[] commands, Object[][] params) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		AthenaResultSet[] batch = new AthenaResultSet[commands.length];
		log.log(Level.FINEST, "executing a query batch");
		for (int i = 0; i < commands.length; i++) {
			batch[i] = executeQuery(commands[i], params[i]);
		}
		return batch;
	}

	/**
	 * If debugging is set, this will output hat the object is being Garbage
	 * collected
	 * 
	 * @since
	 */
	public void finalize() {
		l.finest(new java.util.Date() + ": RemoteConnection: RemoteConnectionImpl g'ced");
	}

	/**
	 * Makes the System Connection commit changes to it's data store
	 * 
	 *  
	 */
	protected synchronized boolean rollForward() {
		try {
			log.log(Level.FINER, "Rolling forward....");
			if (conn.commit()) {
				StatisticMonitor.addCommit();
				conn.resetAutoAbort();
				conn.clearTransactionFlag();
				int stsize = sp.getStackSize();
				ResourceManager rm = ResourceManager.getResourceManager();
				for (int i = 0; i < stsize; i++) {
					Object obj = sp.getCommand(i);
					if (obj instanceof Command) {
						Command comm = (Command) obj;
						Handler handle = HandleEngine.getEngine().getHandlerFor(comm.getCallName());
						String cachesToUpdate = handle.getCachePattern();
						if (cachesToUpdate != null) {
							log.log(Level.FINEST, "Requesting updates of caches matching: " + cachesToUpdate);
							rm.updateCachesLike(cachesToUpdate, "RSET");
						}
					}
				}
			} else
				return false;
			sp.clearStack();
			sp.tx = null;
			AthenaLogger.persist(STATEFILE, sp);
		} catch (Exception ex) {
			StatisticMonitor.addFailure();
			LogExceptionWrapper lex;
			if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
				lex = new LogExceptionWrapper("Commit panic", ex, true, false);
			else
				lex = new LogExceptionWrapper("Commit panic", ex, false, false);
			if(errHandler!=null)
			errHandler.handleHostException(lex);
			
			System.err.println(new java.util.Date() + ": RemoteConnection: " + ex.getMessage() + " = COMMIT PANIC!!!!");
			ex.printStackTrace();
		}
		TxProcessState = NO_STANDING_TX_PROCESSES;
		log.log(Level.FINEST, "RemoteConnection :Setting release status on connection: " + conn.getPersistentFileName());
		canrelease = true;
		return true;
	}

	/**
	 * Nullifies any operations performed within this transaction <br>
	 * and releases state
	 * 
	 * @since
	 */
	protected synchronized void rollBack() {
		try {
			log.log(Level.FINER, "Rolling back");
			conn.rollback();
			log.finest("Rolled back Connection");
			conn.clearTransactionFlag();
			log.finest("Rest transaction flag");
			conn.resetAutoAbort();
			
			StatisticMonitor.addRollback();
			sp.clearStack();
			sp.tx = null;
			log.finest("Persisting File");
			AthenaLogger.persist(STATEFILE, sp);
		} catch (Exception ex) {
			ex.printStackTrace();
			LogExceptionWrapper lex;
			if (SystemManager.getSystemState() != SystemManager.DBOFFLINE)
				lex = new LogExceptionWrapper("Rollback panic", ex, true, false);
			else
				lex = new LogExceptionWrapper("Rollback panic", ex, false, false);
			errHandler.handleHostException(lex);
		}
		log.finest("Resetting Txn Process State");
		TxProcessState = NO_STANDING_TX_PROCESSES;
		canrelease = true;
	}

	/**
	 * Attempts to restore transaction state and ensure integrity after System
	 * crashes
	 * 
	 * @param ss
	 *                  State persistence Object
	 * @since
	 */
	public void restoreAll(StatePersistence ss) {
		try {
			System.err.println(new java.util.Date() + ": RemoteConnection: RESTORE:  initiating recovery  for this connection");
			boolean joinfailure = false;
			sp = ss;
			sp.restoreTX();
			sp.crashCount++;
			try {
				if (sp.tx != null) {
					TransactionParticipant proxy = (TransactionParticipant) DefaultExporterManager.getManager().exportProxy((TransactionParticipant) this, "Participants", UuidFactory.generate());
					sp.tx.join(proxy, sp.crashCount);
				} else {
					// sp.transactionID = 0L;
					System.err.println(new java.util.Date() + ": RemoteConnection: Error: Error while attempting to restore transaction: tx==null!");
				}
			} catch (CannotJoinException cjex) {
				System.err.println(new java.util.Date() + ": RemoteConnection: RESTORE: Cannot join will check outcome");
				log.log(Level.WARNING, "RESTORE: Cannot join txn - checking mgr", cjex);
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: " + cjex.getMessage());
				System.out.println();
				joinfailure = true;
				cjex.printStackTrace();
			} catch (Exception ccex) {
				System.err.println(new java.util.Date() + ": RemoteConnection: RESTORE: HELP!!!!");
				StatisticMonitor.addFailure();
				System.err.println(new java.util.Date() + ": RemoteConnection: Error: " + ccex.getMessage());
				String Filename = conn.getPersistentFileName() + ".backup" + System.currentTimeMillis();
				AthenaLogger.persist(Filename, sp);
				System.out.println("Bad Tx log - stored to " + Filename + ".....continuing");
				log.log(Level.SEVERE, "RESTORE: Error during join, old state stored to " + Filename, ccex);
				ccex.printStackTrace();
				sp.tx = null;
				sp.clearStack();
				AthenaLogger.persist(conn.getPersistentFileName(), sp);
				return;
			}
			if (sp.tx != null) {
				if (sp.getStackSize() != 0) {
					System.out.println(new java.util.Date() + ": RemoteConnection: RESTORE: detected PREPARED..........");
					queryTxnState(joinfailure);
				}
			}
		} catch (UnknownTransactionException utex) {			
			String Filename = conn.getPersistentFileName() + ".backup" + System.currentTimeMillis();
			try {
				AthenaLogger.persist(Filename, sp);
			} catch (IOException ex) {
				System.err.println("State could not be stored");
			}
			log.log(Level.SEVERE, "RESTORE: Transaction not known to manager: State stored to " + Filename, utex);
			utex.printStackTrace();
			StatisticMonitor.addFailure();
			return;
		} catch (Exception e) {
			System.err.println(new java.util.Date() + ": RemoteConnection: Error: " + e.getMessage());
			String Filename = conn.getPersistentFileName() + ".backup" + System.currentTimeMillis();
			try {
				AthenaLogger.persist(Filename, sp);
			} catch (IOException ex) {
				System.err.println("State could not be stored");
			}
			log.log(Level.SEVERE, "RESTORE: " + e.getMessage() + " : State stored to " + Filename, e);
			e.printStackTrace();
			StatisticMonitor.addFailure();
			return;
		}
		System.err.println(new java.util.Date() + " : recovery finished for this connection");
	}

	private void queryTxnState(boolean joinfailure) throws UnknownTransactionException, RemoteException {
		boolean done = false;
		while (!done) {
			try {
				long tState = sp.tx.getState();
				if (tState == TransactionConstants.COMMITTED) {
					log.info("RESTORE: Transaction Manager Indicator: COMMITTED....going to transactional mode");
					AthenaXid athenaXid = generateXid();
					conn.setTransactionID(athenaXid);
					try {
						for (int i = 0; i < sp.getStackSize(); i++) {
							System.out.println(new Date() + ": RemoteConnection: RESTORE: \t....Re-executing SQL statement " + i);
							conn.issueCommand(sp.getCommand(i));
						}
						rollForward();
						done = true;
						System.out.println(new Date() + ": RemoteConnection: RESTORE: caught-up COMMIT............");
					} catch (Exception ex) {
						System.err.println(new Date() + ": RemoteConnection: RESTORE: ARGGGGHHHHH! Recovery-commit panic!!!");
						String Filename = conn.getPersistentFileName() + ".backup" + System.currentTimeMillis();
						AthenaLogger.persist(Filename, sp);
						log.log(Level.SEVERE, "RESTORE: Recovery-commit panic!, state stored to " + Filename + " ...... continuing", ex);
						System.err.println(new Date() + "RESTORE: Recovery-commit panic (" + ex.getMessage() + ")!, state stored to " + Filename + " ...... continuing");
						ex.printStackTrace();
					}
					
				} else if (tState == TransactionConstants.ABORTED) {
					log.info("RESTORE: Transaction Manager Indicator: ABORTED....rolling back");
					System.out.println(new Date() + ": RemoteConnection: RESTORE: rolling back....");
					done = true;
					sp.clearStack();
					sp.tx = null;
					AthenaLogger.persist(STATEFILE, sp);
				}
				if (tState == TransactionConstants.PREPARED) {
					log.info(new Date() + ": RemoteConnection: RESTORE: tstate: PREPARED");
				}
				if (tState == TransactionConstants.ACTIVE) {
					log.info(new Date() + ": RemoteConnection: RESTORE: tstate: ACTIVE");
				}
				if (tState == TransactionConstants.NOTCHANGED) {
					log.info(new Date() + ": RemoteConnection: RESTORE: tstate: NOT CHANGED");
				}
				if (tState == TransactionConstants.VOTING) {
					if (!joinfailure) {
						log.info(new Date() + ": RemoteConnection: RESTORE: tstate: VOTING - going to prepare");
						if (prepare(sp.tx.mgr, sp.tx.id) == TransactionConstants.COMMITTED) {
							sp.tx.commit();
							log.info(new Date() + ": RemoteConnection: RESTORE: VOTING - Caught up commit");
						} else {
							log.info(new Date() + ": RemoteConnection: RESTORE: VOTING - Signalling for abort");
							sp.tx.abort();
							log.info(new Date() + ": RemoteConnection: RESTORE: VOTING - Signalled for abort");
						}
					} else
						System.out.println("There was a join failure - Not committing - cancelling transaction state");
					done = true;
				}
			} catch (UnknownTransactionException utex) {
				StatisticMonitor.addFailure();
				log.log(Level.SEVERE, "RESTORE: Transaction cannot be found in Manager", utex);
				abort(sp.tx.mgr, sp.tx.id);
				done = true;
			} catch (NoSuchObjectException ex) {
				StatisticMonitor.addFailure();
				log.log(Level.SEVERE, "RESTORE: Object does not exist", ex);
				abort(sp.tx.mgr, sp.tx.id);
				done = true;
			} catch (Exception e) {
				StatisticMonitor.addFailure();
				log.log(Level.SEVERE, "RESTORE: Genearl Failure!...... continuing", e);
				System.err.println(new Date() + ": RemoteConnection: Error: " + e.getMessage());
				e.printStackTrace();
			}

		}
	}

	private Object processDialect(org.jini.projects.athena.command.Command comm, Dialect dialect) throws WrongReturnTypeException, CannotExecuteException, RemoteException {
		Object dialectreturn;
		log.log(Level.FINEST, ": RemoteConnection: Executing a Dialect");
		try {
			if (dialect != null)
				comm.setParameter("_DIALECT", dialect);
			dialectreturn = conn.issueCommand(comm);
			log.log(Level.FINEST, "Executed:");
			if (dialectreturn == null) {
				System.out.println("RETURN IS NULL????");
			}
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			log.log(Level.SEVERE, "Dialect could not be executed for command " + comm.getCallName(), ex);
			ex.printStackTrace();
			throw new CannotExecuteException("Cannot execute: " + ex.getMessage());
		}
		if (dialectreturn instanceof DisconnectedResultSetImpl) {
			return (DisconnectedResultSetImpl) dialectreturn;
		} else {
			if (dialectreturn instanceof SystemResultSet) {
				log.log(Level.FINEST, "Got a SystemResultSet");
				try {
					RemoteResultSet rrs = new RemoteResultSetImpl((SystemResultSet) dialectreturn);
					return rrs;
				} catch (EmptyResultSetException emptyEx) {
					System.out.println("Err: " + emptyEx.getMessage());
					emptyEx.printStackTrace();
					throw new WrongReturnTypeException("Tis 'Empty");
				}
			} else {
				if (dialectreturn instanceof HashMap) {
					return dialectreturn;
				}
				try {
					System.out.println("Return class was: " + dialectreturn.getClass().getName());
				} catch (Exception e) {
				}
				throw new WrongReturnTypeException("Cannot create a table from this return value");
			}
		}
	}

	public Object executeObjectQuery(Object command) throws CannotExecuteException, EmptyResultSetException, WrongReturnTypeException, RemoteException {
		if (SystemManager.getSystemState() == SystemManager.SCHEDULEDDOWNTIME)
			throw new CannotExecuteException("Scheduled Downtime");
		if (SystemManager.getSystemState() == SystemManager.DBOFFLINE)
			throw new CannotExecuteException("Host Offline");
		return exec(command);
	}

	/*
	 * @see net.jini.security.proxytrust.ServerProxyTrust#getProxyVerifier()
	 */
	public TrustVerifier getProxyVerifier() throws RemoteException {
		// TODO Complete method stub for getProxyVerifier
		Object ob = DefaultExporterManager.getManager().getExportedProxy(this.proxyID);
		return new AthenaConnectionProxy.Verifier((RemoteConnection) ob);
	}

	/*
	 * @see net.jini.export.ProxyAccessor#getProxy()
	 */
	public Object getProxy() {
		// TODO Complete method stub for getProxy

		return DefaultExporterManager.getManager().getExportedProxy(this.proxyID);
	}

}