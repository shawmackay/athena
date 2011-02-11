
package org.jini.projects.athena.connection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jini.projects.athena.resources.Cacheable;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.resultset.AthenaResultSetImpl;
import org.jini.projects.athena.xml.StoredProcLoader;
import org.xml.sax.SAXException;

/**
 * Handles the loading and storage of stored procedure definitions. This is so
 * Athena can appropriately place procedure arguments and types.
 * 
 * @author calum
 */
public abstract class ProcedureHandler {
	static ResourceManager rm = ResourceManager.getResourceManager();
	AthenaResultSet rs = null;
	static Logger LOG = Logger.getLogger("org.jini.projects.athena.connection");
	Logger l = Logger.getLogger("org.jini.projects.athena.connection");
	protected static Connection conn;
	static {
		try {
			String driver = System.getProperty("org.jini.projects.athena.connect.driver");
			String url = System.getProperty("org.jini.projects.athena.connect.url");
			String user = System.getProperty("org.jini.projects.athena.connect.username");
			String passwd = System.getProperty("org.jini.projects.athena.connect.password");
			if (driver != null) {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, passwd);
				//	oconn = new OracleConnection(-2);
				rm.addCache(AthenaResultSetImpl.class, "sprocdef");
			}
		} catch (Exception ex) {
			System.err.println("Can't initiate connections");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public ProcedureHandler() {
	}

	/**
	 * Obtains the appropriate procedure handler for the SQL datasource. For
	 * example, passing "Oracle" will return an instance of
	 * <code>org.jini.projects.org.jini.projects.athena.connection.oracle.OracleProcedureHandler</code>
	 * 
	 * @param name
	 * @return a specific procedure handler.
	 */
	public static ProcedureHandler getInstance(String name) {
		try {
			LOG.info("ProcHandler Name:[" + name + "]");
			if (name != null) {
				ProcedureHandler proc = (ProcedureHandler) Class.forName("org.jini.projects.athena.connects." + name.toLowerCase() + "." + name + "ProcedureHandler").newInstance();
				return proc;
			}
		} catch (InstantiationException e) {
			System.err.println("Err: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Err: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtains the default instance, as specified by the
	 * org.jini.projects.athena.connect.type system property
	 * 
	 * @return a default Procedure handler for this connection
	 */
	public static ProcedureHandler getInstance() {
		return getInstance(System.getProperty("org.jini.projects.athena.connect.type"));
	}

	/**
	 * Gets the procedure definition for a given catalog , schema and procedure
	 * 
	 * @param catalog
	 *                   the catalog or package that the store proecure is in
	 * @param schema
	 *                   the schema or userarea that the procedure (or it's package) is
	 *                   in
	 * @param procedureName
	 *                   the procedure name
	 * @return @throws
	 *              SQLException
	 */
	protected abstract AthenaResultSet getProcedureDefinition(String catalog, String schema, String procedureName) throws SQLException;

	/**
	 * Adds a procedure definition to the cache
	 * 
	 * @param catalog
	 * @param schema
	 * @param procedureName
	 */
	protected void addProcedureDefinition(String catalog, String schema, String procedureName) {
		try {
			l.fine("Procedure Details: " + schema + ", " + catalog + "," + procedureName);
			AthenaResultSet ars = getProcedureDefinition(catalog, schema, procedureName);
			String cachekey = (catalog + "." + procedureName).toUpperCase();
			l.finer("Adding to cache as [" + cachekey + "]");
			rm.addObjectToCache("sprocdef", ars, cachekey, 0);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Obtains the procedure definition for the given name
	 * 
	 * @param procedureName
	 * @return a procedure definition for the given name
	 */
	public ProcedureDefinition getProcedure(String procedureName) {		
		l.finest("PROC:" + procedureName.toUpperCase().trim());
		Object ob = rm.enquireCache("sprocdef", procedureName.toUpperCase().trim());
		if (ob == null) {
			l.severe("Proc def not available");
		}
		rs = (AthenaResultSet) ((Cacheable) ob).getObject();
		if (rs == null) {
			l.severe("Procedure Def has not been loaded");
			return null;
		}
		return new ProcedureDefinition(rs);
	}

	/**
	 * Loads this systems procedure definition file This is stored in
	 * [athenaroot]/config/handlers/[athenaservicename]proc.xml
	 *  
	 */
	public static void loadDefs() {
		String file = System.getProperty("user.dir") + File.separator + "config";
		File f = new File(file + File.separator + "handlers" + File.separator + System.getProperty("org.jini.projects.athena.service.name") + "proc.xml");
		if (f.exists()) {
			LOG.info("Loading Definitions");
			ProcedureHandler handler = ProcedureHandler.getInstance();
			try {
                 BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream(f));
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    StoredProcLoader loader = new StoredProcLoader();
                    org.xml.sax.helpers.DefaultHandler dh = loader;
                    parser.parse(b_xmlin, dh);
                    List l = loader.getProcedures();
                    for(Iterator iter = l.iterator();iter.hasNext();){
                        String procdefs =(String) iter.next(); 
                    	buildDefinitions(procdefs, handler);
                    }
			} catch (SAXException e) {
				System.err.println("Error parsing XML: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
                System.err.println("Error loading XML: " + e.getMessage());
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
				// URGENT Handle ParserConfigurationException
				e.printStackTrace();
			}
		}
	}

	private static void buildDefinitions(String procdefs, ProcedureHandler handler) {
		        String currCat = null;
                String currSchema = null;
                String procName = null;
                LOG.fine("Procdef string: " + procdefs);
                String[] parts=procdefs.trim().split("\\.");
                currSchema=parts[0];
                currCat = parts[1];
                procName = parts[2];
                
				LOG.info("Creating Procedure Definition for: " + currCat + "." + currSchema + "." + procName);
				handler.addProcedureDefinition(currCat, currSchema, procName);
	}
}