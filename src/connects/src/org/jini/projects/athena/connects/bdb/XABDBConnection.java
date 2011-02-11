package org.jini.projects.athena.connects.bdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.StdCommand;
import org.jini.projects.athena.connection.HostErrorHandler;
import org.jini.projects.athena.connection.SystemConnection;
import org.jini.projects.athena.exception.AthenaException;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.XAEnvironment;

public class XABDBConnection implements SystemConnection {

	private XAEnvironment env;
	private Database theDB;
	private EnvironmentConfig envConfig;
	private DatabaseConfig dbConfig;

	private boolean allocated = false;
	private boolean connected = false;
	private boolean canBeFreed = false;
	private Object txnID;
	public String PersistentFile = null;

	private Properties bindings;

	private int ref = 0;
	private boolean autoAbort = false;
	private Connection conn = null;
	private int numalloc = 0;
	private Database myClassDb;
	private StoredClassCatalog classCatalog;
	private DatabaseConfig classDbConfig;

	public XABDBConnection() {
		try {
			connectTo(System.getProperties());
		} catch (AthenaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public XABDBConnection(boolean withSystemProps) {
		if (withSystemProps)
			try {
				connectTo(System.getProperties());
			} catch (AthenaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public synchronized void allocate() {

		allocated = true;
		numalloc++;
		if (numalloc > 1) {

		}
	}

	public boolean canFree() {
		// TODO Auto-generated method stub
		return canBeFreed;
	}

	public void close() throws AthenaException {
		// TODO Auto-generated method stub
		try {
			if (theDB != null) {
				theDB.close();
			}
			if (myClassDb != null) {
				myClassDb.close();
			}
			if (env != null) {
				env.close();
			}

		} catch (DatabaseException dbe) {
			// Exception handling goes here
		}
	}

	public boolean commit() throws Exception {
		// TODO Auto-generated method stub
		try {
			env.commit((Xid) txnID, false);
			System.out.println("Commited XA Transaction");
			return true;
		} catch (XAException dbe) {
			return false;
		}
	}

	public void connectTo(Properties connectionprops) throws AthenaException {
		// TODO Auto-generated method stub
		try {
			envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(true);
			dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setSortedDuplicates(true);
			dbConfig.setTransactional(true);
			classDbConfig = new DatabaseConfig();
			classDbConfig.setAllowCreate(true);
			classDbConfig.setTransactional(true);
			env = new XAEnvironment(new File(connectionprops.getProperty("org.jini.projects.athena.envName")), envConfig);
			theDB = env.openDatabase(null, connectionprops.getProperty("org.jini.projects.athena.dbName"), dbConfig);

			myClassDb = env.openDatabase(null, "classDb", classDbConfig);
			classCatalog = new StoredClassCatalog(myClassDb);
			String bindingsFile = System.getProperty("bindingProps");
			bindings = new Properties();
			if (bindingsFile != null) {
				try {
					bindings.load(new FileInputStream(new File(bindingsFile)));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (DatabaseException dbe) {
			// Exception handling goes here
			dbe.printStackTrace();
		}

	}

	public HostErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReference(int ref) {
		PersistentFile = System.getProperty("org.jini.projects.athena.service.name") + "CONN" + ref + ".ser";
	}

	public String getPersistentFileName() {
		return this.PersistentFile;
	}

	public Command getSystemCommand() throws AthenaException {
		// TODO Auto-generated method stub
		return new org.jini.projects.athena.command.StdCommand();
	}

	public Object handleType(Object in) throws AthenaException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean inTransaction() throws AthenaException {
		return txnID != null;
	}

	public boolean isAllocated() {
		return allocated;
	}

	public boolean isAutoAbortSet() throws AthenaException {
		return autoAbort;
	}

	public boolean isConnected() {
		return connected;
	}

	public Object issueCommand(Object command) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Issuing command");
		if (command instanceof Command) {
			BDBCommand bcmd = convertCommand((Command) command);

			if (bcmd.getType() == BDBCommand.WRITE) {
				doWrite(bcmd);
			}
			if (bcmd.getType() == BDBCommand.READ) {
				Object o = doRead(bcmd);
				return o;

			}
		}
		return null;
	}

	private Object convertResult(DatabaseEntry data, String className) {
		try {
			System.out.println("Finding class: " + className);
			Class cl = Class.forName(className);
			if (isBDBPrimitive(cl)) {
				EntryBinding binding = getBDBPrimitiveBinding(cl);
				return binding.entryToObject(data);
			} else if (bindings.containsKey(data.getClass().getName())) {
				try {
					Class bindingClass = cl;
					Object bindingInstance = bindingClass.newInstance();
					if (bindingInstance instanceof TupleBinding) {
						TupleBinding theBinding = (TupleBinding) bindingInstance;
						return theBinding.entryToObject(data);
					}

				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				EntryBinding dataBinding = new SerialBinding(classCatalog, data.getClass());
				return dataBinding.entryToObject(data);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private BDBCommand convertCommand(Command command) {
		// TODO Auto-generated method stub
		String type = (String) command.getParameter("__TYPE__");
		String returnType = (String) command.getParameter("__RETURNTYPE__");
		System.out.println("Return type is " + returnType);
		Object data = command.getParameter("data");
		Object key = command.getParameter("key");
		DatabaseEntry theData = new DatabaseEntry();
		DatabaseEntry theKey = new DatabaseEntry();
		int optype = 0;
		if (type.equalsIgnoreCase("read"))
			optype = BDBCommand.READ;
		if (type.equalsIgnoreCase("write"))
			optype = BDBCommand.WRITE;
		if (optype == BDBCommand.WRITE) {
			if (isBDBPrimitive(data.getClass())) {
				EntryBinding binding = getBDBPrimitiveBinding(data);
				binding.objectToEntry(data, theData);
			} else if (bindings.containsKey(data.getClass().getName())) {
				try {
					Class bindingClass = Class.forName(bindings.getProperty(data.getClass().getName()));
					Object bindingInstance = bindingClass.newInstance();
					if (bindingInstance instanceof TupleBinding) {
						TupleBinding theBinding = (TupleBinding) bindingInstance;
						theBinding.objectToEntry(data, theData);
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				EntryBinding dataBinding = new SerialBinding(classCatalog, data.getClass());
				dataBinding.objectToEntry(data, theData);
			}
		}
		System.out.println("Converting Key: " + key.toString());
		if (isBDBPrimitive(key.getClass())) {
			EntryBinding binding = getBDBPrimitiveBinding(key);
			binding.objectToEntry(key, theKey);
		} else if (bindings.containsKey(key.getClass().getName())) {
			try {
				Class bindingClass = Class.forName(bindings.getProperty(key.getClass().getName()));
				Object bindingInstance = bindingClass.newInstance();
				if (bindingInstance instanceof TupleBinding) {
					TupleBinding theBinding = (TupleBinding) bindingInstance;
					theBinding.objectToEntry(key, theKey);
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			EntryBinding dataBinding = new SerialBinding(classCatalog, key.getClass());
			dataBinding.objectToEntry(key, theKey);
		}

		BDBCommand bcmd = new SimpleBDBCommand(optype, theKey, theData, returnType);
		return bcmd;
	}

	private boolean isBDBPrimitive(Class dataClass) {
		if (dataClass.equals(Long.class))
			return true;
		if (dataClass.equals(String.class))
			return true;
		if (dataClass.equals(Character.class))
			return true;
		if (dataClass.equals(Boolean.class))
			return true;
		if (dataClass.equals(Short.class))
			return true;
		if (dataClass.equals(Integer.class))
			return true;
		if (dataClass.equals(Float.class))
			return true;
		if (dataClass.equals(Double.class))
			return true;
		if (dataClass.equals(Byte.class))
			return true;
		return false;
	}

	private boolean checkBDBPrimitiveBinding(Class dataClass) {
		if (dataClass.equals(Long.class))
			return true;
		if (dataClass.equals(String.class))
			return true;
		if (dataClass.equals(Character.class))
			return true;
		if (dataClass.equals(Boolean.class))
			return true;
		if (dataClass.equals(Short.class))
			return true;
		if (dataClass.equals(Integer.class))
			return true;
		if (dataClass.equals(Float.class))
			return true;
		if (dataClass.equals(Double.class))
			return true;
		if (dataClass.equals(Byte.class))
			return true;
		return false;
	}

	private EntryBinding getBDBPrimitiveBinding(Class dataClass) {
		if (checkBDBPrimitiveBinding(dataClass))
			return TupleBinding.getPrimitiveBinding(dataClass);
		else
			return null;
	}

	private EntryBinding getBDBPrimitiveBinding(Object data) {
		if (checkBDBPrimitiveBinding(data.getClass()))
			return TupleBinding.getPrimitiveBinding(data.getClass());
		else
			return null;

	}

	private void doWrite(BDBCommand bcmd) throws Exception {
		Cursor cursor = null;
		try {
			
			if (txnID != null){
				System.out.println("Specified TRansction");
				cursor = theDB.openCursor(env.getXATransaction((Xid) txnID), null);
			
			}else
				cursor = theDB.openCursor(null, null);
			OperationStatus retVal = null;
			retVal = cursor.put(bcmd.getKey(), bcmd.getData());
			System.out.println("Written data:");
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if(cursor !=null){
				cursor.close();
			}
		}

	}

	private Object doRead(BDBCommand bcmd) throws Exception {
		Cursor cursor = null;
		ArrayList arr = new ArrayList();
		try {
			DatabaseEntry theKey = bcmd.getKey();
			DatabaseEntry theData = new DatabaseEntry();
			System.out.println("BCMD Return Type: " + bcmd.getReturnType());

			// Perform the get.

			if (txnID != null)
				cursor = theDB.openCursor(env.getXATransaction((Xid) txnID), null);
			else
				cursor = theDB.openCursor(null, null);
			int recordsread = 0;

			OperationStatus retVal = cursor.getSearchKeyRange(theKey, theData, LockMode.DEFAULT);
			if (retVal == OperationStatus.NOTFOUND) {
				System.out.println("Key " + " not matched in database " + theDB.getDatabaseName());
			} else {
				if (cursor.count() >= 1) {
					System.out.println("Got a cursor lock");
					while (retVal == OperationStatus.SUCCESS) {

						Object o = convertResult(theData, bcmd.getReturnType());
						System.out.println("Object returned from query is: " + o.toString());
						arr.add(o);
						recordsread++;
						retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
					}
				}
			}
			if (recordsread == 0) {
				System.out.println("No record found for key '" + bcmd.getKey().toString() + "'.");
			}
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return arr;
	}

	public Object issueCommand(Object command, Object[] params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void reConnect() throws AthenaException {
		// TODO Auto-generated method stub

	}

	public void release() throws AthenaException {
		allocated = false;
		numalloc--;

	}

	public void resetAutoAbort() throws AthenaException {
		this.autoAbort = false;
	}

	public boolean rollback() throws Exception {
		// TODO Auto-generated method stub
		try {
			env.rollback((Xid) txnID);
			return true;
		} catch (XAException dbe) {
			return false;
		}
	}

	public void setAutoAbort() {
		this.autoAbort = true;

	}

	public void setTransactionID(Object ID) throws AthenaException {
		// TODO Auto-generated method stub

		this.txnID = ID;
		try {
			if (txnID instanceof Xid) {

				System.out.println("Starting Transaction: " + txnID);
				env.start((Xid) txnID, 0);
			}
		} catch (XAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean prepare() throws Exception {
		try {
			System.out.println("Preparing XA TRansaction: " + txnID);
			// env.end((Xid) txnID,XAResource.TMSUCCESS);
			env.prepare((Xid) txnID);
			System.out.println("XA TRansaction prepared");
		} catch (XAException e) {
			// TODO Auto-generated catch block
			System.out.println("XA Exception: " + e.getMessage());

			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void clearTransactionFlag() throws AthenaException {
		// TODO Auto-generated method stub
		txnID = null;
	}

}
