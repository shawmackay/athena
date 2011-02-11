/*
 * DialectEngine.java
 * 
 * Created on 10 September 2001, 13:26
 */

package org.jini.projects.athena.command.dialect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.athena.xml.XSLTransformsLoader;

/**
 * Stores the list of available dialects for retrieval at runtime. Also stores
 * the list of input and output XSL transforms, for those systems that may need
 * it
 * 
 * @version 0.9community
 */
public class DialectEngine {
	private static DialectEngine engine;
	HashMap transforms;
	private Map In_transformPool = new HashMap();
	private Map Out_transformPool = new HashMap();
	private final String IN_XSLDIR; 
	
	private final String OUT_XSLDIR;
	static Logger LOG = Logger.getLogger("org.jini.projects.athena.command.dialect");
	private Logger log = LOG;
	private String athenaName;
	private Configuration config;

	
	private DialectEngine(Configuration config) {
		String in_xsldir = "";
		String out_xsldir = "";
		try {
			athenaName = (String) config.getEntry("org.jini.projects.athena", "athenaName", String.class);
			in_xsldir = System.getProperty("user.dir") + "/config/dialects/" + athenaName + "/in";
			out_xsldir = System.getProperty("user.dir") + "/config/dialects/" + athenaName + "/out";
		} catch (ConfigurationException e) {
			// URGENT Handle ConfigurationException
			e.printStackTrace();
		}
		IN_XSLDIR = in_xsldir;
		OUT_XSLDIR = out_xsldir;
		this.config = config;
		loadPools();
		loadConfig();
	}

	/**
	 * Gets the engine attribute of the DialectEngine class
	 * 
	 * @return The engine value
	 * @since
	 */
	public static DialectEngine getEngine() {
		return engine;
	}

	/**
	 * Gets the Dialect object that matches the <CODE>Command</CODE> you are
	 * trying to run
	 * 
	 * @param name
	 *                   Name of the dialect to find usually obtained from <CODE>
	 *                   Command.getCallName();</CODE>
	 * @return A new instance of the matching Dialect class
	 * @exception Exception
	 *                        Thrown if any error occurs such as ClassNotFoundException,
	 *                        InstantiationException, etc
	 * @since
	 */
	public static Dialect getDialect(String name) throws Exception {
		try {
			LOG.finest("Getting Dialect: " + name);
			Dialect retval = (Dialect) Class.forName("org.jini.projects.athena.command.dialect." + name + "_Dialect").newInstance();
			return retval;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the inputTransform attribute of the DialectEngine object
	 * 
	 * @param callName
	 *                   Description of Parameter
	 * @return The inputTransform value
	 * @since
	 */
	public Transformer getInputTransform(String callName) {
		log.info("Getting Transform for " + callName);
		TransformInfo ti = (TransformInfo) transforms.get(callName.toLowerCase());
		Templates template = (Templates) In_transformPool.get(ti.getInputTransForm());
		Transformer retval = null;
		try {
			retval = template.newTransformer();
		} catch (TransformerConfigurationException tcex) {
			log.log(Level.SEVERE, tcex.getMessage(), tcex);
		}
		return retval;
	}

	/**
	 * Gets the outputTransform attribute of the DialectEngine object
	 * 
	 * @param callName
	 *                   Description of Parameter
	 * @return The outputTransform value
	 * @since
	 */
	public Transformer getOutputTransform(String callName) {
		TransformInfo ti = (TransformInfo) transforms.get(callName.toLowerCase());
		Templates template = (Templates) Out_transformPool.get(ti.getOutputTransform());
		Transformer retval = null;
		try {
			retval = template.newTransformer();
		} catch (TransformerConfigurationException tcex) {
			log.log(Level.SEVERE, tcex.getMessage(), tcex);
		}
		return retval;
	}

	
	private void loadConfig() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XSLTransformsLoader athenahandler = new XSLTransformsLoader();
			org.xml.sax.helpers.DefaultHandler dh = athenahandler;
			File f = new File("config/dialects/" + athenaName + "/XSLTransforms.xml");
			if (f.exists()) {
				
				parser.parse(f, dh);
				transforms = athenahandler.getTransforms();
			} else
				transforms = new HashMap();
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	
	private void loadPools() {
		try {
			System.getProperties().put("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentFactoryImpl");
			TransformerFactory tfact = TransformerFactory.newInstance();
			File poolDir = new File(IN_XSLDIR);
			File[] infiles = poolDir.listFiles();
			if (infiles != null) {
				log.info("CHI: Input Transforms ");
				for (int i = 0; i < infiles.length; i++) {
					if (infiles[i].getName().endsWith(".xsl")) {
						java.io.BufferedReader reader = new BufferedReader(new FileReader(infiles[i]));
						StreamSource xsl = new StreamSource(reader);
						Templates template = tfact.newTemplates(xsl);
						In_transformPool.put(infiles[i].getName(), template);
					}
				}
			} else
				log.info("No Input transforms for " + athenaName);
			poolDir = new File(OUT_XSLDIR);
			File[] outfiles = poolDir.listFiles();
			if (outfiles != null) {
				log.log(Level.INFO, "CHI: Output Transforms ");
				for (int i = 0; i < outfiles.length; i++) {
					if (!outfiles[i].isDirectory())
						if (outfiles[i].getName().endsWith(".xsl")) {
							java.io.BufferedReader reader = new BufferedReader(new FileReader(outfiles[i]));
							StreamSource xsl = new StreamSource(reader);
							Templates template = tfact.newTemplates(xsl);
							Out_transformPool.put(outfiles[i].getName(), template);
						}
				}
			} else
				log.info("No Output transforms for " + athenaName);
		} catch (Exception ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	/**
     * Initialises the Dialect engine with appropriate configuration information
	 * @param config
	 */
	public static void initialise(Configuration config) {
		if (engine == null) {
			engine = new DialectEngine(config);
		}
	}
	/**
     * Interrogate the current set of dialects and transforms. Used in serviceUI
	 * @return Map of loaded dialect definitions
	 */
	public Map getDialectDefinitions() {
		try {
			System.out.println("Getting dialect definitions");
			Map returnMap = new HashMap();
			Map in_map = new HashMap();
			Map out_map = new HashMap();
			Map item_map = new HashMap();
			returnMap.put("in", in_map);
			returnMap.put("out", out_map);
			returnMap.put("transforms", item_map);
			Iterator iter = transforms.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				TransformInfo ti = (TransformInfo) entry.getValue();
				item_map.put(ti.getTransformName(), ti);
				if (!in_map.containsKey(ti.getInputTransForm())) {
					System.out.println("Putting " + ti.getInputTransForm() + " into in list");
					File f = new File(IN_XSLDIR + "/" + ti.getInputTransForm());
					byte[] filedata = new byte[(int) f.length()];
					try {
						FileInputStream fis = new FileInputStream(f);
						fis.read(filedata);
						in_map.put(ti.getInputTransForm(), new String(filedata));
					} catch (FileNotFoundException e) {
						// URGENT Handle FileNotFoundException
						e.printStackTrace();
					} catch (IOException e) {
						// URGENT Handle IOException
						e.printStackTrace();
					}
				}
				if (!out_map.containsKey(ti.getOutputTransform())) {
					System.out.println("Putting " + ti.getOutputTransform() + " into out list");
					File f = new File(OUT_XSLDIR + "/" + ti.getOutputTransform());
					byte[] filedata = new byte[(int) f.length()];
					try {
						FileInputStream fis = new FileInputStream(f);
						fis.read(filedata);
						out_map.put(ti.getOutputTransform(), new String(filedata));
					} catch (FileNotFoundException e) {
						// URGENT Handle FileNotFoundException
						e.printStackTrace();
					} catch (IOException e) {
						// URGENT Handle IOException
						e.printStackTrace();
					}
				}
			}
			return returnMap;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}