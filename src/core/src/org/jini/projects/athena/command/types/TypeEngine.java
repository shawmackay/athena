/*
 * Created by IntelliJ IDEA. User: calum Date: 27-Aug-2002 Time: 10:07:13 To
 * change template for new class use Code Style | Class Templates options (Tools |
 * IDE Options).
 */

package org.jini.projects.athena.command.types;

//import
// org.jini.projects.org.jini.projects.athena.resources.InstanceLifeCyclePool;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.athena.exception.ValidationException;
import org.jini.projects.athena.resources.Pool;
import org.jini.projects.athena.resources.ResourceManager;
import org.jini.projects.athena.resources.SimplePool;
import org.jini.projects.athena.xml.TypeXMLLoader;

/**
 * Provides a way to validate, expand and manipulate objects passed as
 * <code>Command</code> parameters, or specified in ReplacedCommand buffers,
 * or Dialects.  
 * 
 * @see org.jini.projects.athena.command.Command 
 * @author calum
 *  
 */
public class TypeEngine {
	//public static TypeEngine typeEngine = new TypeEngine();
	private HashMap types = new HashMap();
	private static Pool enginePool;
	private HashMap symbolTable;
	private static Logger statlog = Logger.getLogger("org.jini.projects.athena.command.types");
	private Logger log = TypeEngine.statlog;
	private static Configuration CONFIG;

	/**
	 * Initialises the TypeEngine Pool
	 * 
	 * @param config
	 */
	public static void initialise(Configuration config) {
		CONFIG = config;
		//System.out.print("TypeEngines:");
		System.out.flush();
		Integer engNumber = new Integer(10);
		try {
			engNumber = (Integer) config.getEntry("org.jini.projects.athena", "typeEngines", Integer.class, new Integer(10));
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		TypeEngine[] engines = new TypeEngine[engNumber.intValue()];
		for (int i = 0; i < engNumber.intValue(); i++) {
			engines[i] = new TypeEngine();
		}
		SimplePool spool = new SimplePool(engines);
		enginePool = spool;
		ResourceManager.getResourceManager().addPool(TypeEngine.class, spool);
	}

	private TypeEngine() {
		try {
			loadTypes();
		} catch (Exception ex) {
			System.out.println("Err: Type Engine Failure! " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Obtains a Type Engine from the pool available
	 * 
	 * @return an instance from the pool
	 */
	public static TypeEngine getTypeEngine() {
		statlog.log(Level.FINEST, "Checking out a type Engine");
		return (TypeEngine) enginePool.checkOut();
		//return typeEngine;
	}

	/**
	 * Confirms whether or not a particular type name is registered with the
	 * type engine
	 * 
	 * @param typeName
	 *                  the type to check
	 * @return whether the type exists or not
	 */

	public boolean containsTypeDef(String typeName) {
		boolean intypes = types.containsKey(typeName.trim());
		return intypes;
	}

	/**
	 * Obtains a type declaration for a given name
	 * 
	 * @param typeName
	 * @return Type object that represents this type definition
	 */
	public Type getTypeDef(String typeName) {
		return (Type) types.get(typeName);
	}

	/**
	 * Sets the set of names that will be checked
	 * 
	 * @param symbolTable
	 */
	public void setSymbolTable(HashMap symbolTable) {
		log.log(Level.FINEST, "Setting Symbol Table");
		this.symbolTable = symbolTable;
	}

	/**
	 * Returns a previously checked-out Type Engine back to the pool
	 * 
	 * @param engine
	 */
	public static void returnEngine(TypeEngine engine) {
		statlog.log(Level.FINEST, "Returning engine");
		enginePool.checkIn(engine);
	}

	private boolean loadTypes() throws FileNotFoundException {
		//System.getProperties().put("org.jini.projects.athena.service.name",
		// "SALESUPR");
		String athenaName = "";
		if (CONFIG == null)
			System.out.println("CONFIG is null");
		try {
			athenaName = (String) CONFIG.getEntry("org.jini.projects.athena", "athenaName", String.class);

		} catch (ConfigurationException e) {
			// URGENT Handle ConfigurationException
			e.printStackTrace();
		}
		File f = new File(System.getProperty("user.dir") + "/config/handlers/" + athenaName + ".types.xml");
		if (f.exists()) {
			BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "/config/handlers/" + athenaName + ".types.xml"));
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				TypeXMLLoader loader = new TypeXMLLoader();
				org.xml.sax.helpers.DefaultHandler dh = loader;
				parser.parse(b_xmlin, dh);
				types = loader.getLoadedTypes();
				/*
				 * Iterator iter = types.entrySet().iterator();
				 * while(iter.hasNext()){ Map.Entry entr = (Map.Entry)
				 * iter.next(); System.out.println(entr.getKey());
				 */
			} catch (Exception ex) {
				System.out.println("Err: " + ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		} else
			types = new HashMap();
		return true;
		/*
		 * HashMap fields = new HashMap(); fields.put("value", "int");
		 * fields.put("date", "date"); ObjectType obtype = new
		 * ObjectType("object", fields); types.put("object", obtype);
		 * 
		 * HashMap dummyFields = new HashMap(); dummyFields.put("name",
		 * "string"); ObjectType dummy = new ObjectType("dummy", dummyFields);
		 * types.put("dummy", dummy);
		 * 
		 * ArrayType arr = new ArrayType("ts_arr(",
		 * "types_struct(${object.value},${object.date},
		 * '${object.dummy.name}')", ",", ")"); types.put("arr", arr);
		 */
	}

	/**
	 * Given an encoded string, process and expand with the given parameters
	 * @param toProcess the endcoded string to process
	 * @param parameters a map of parameters and values to be used in the processing
	 * @return the expanded, un encoded string
	 * @throws ValidationException
	 */
	public String process(String toProcess, Map parameters) throws ValidationException {
		StringBuffer expandedCommand = new StringBuffer(toProcess.toString());
		int position = 0;
		int endpos = 0;
		log.log(Level.FINEST, "Processing..............." + expandedCommand);
		while (expandedCommand.indexOf("${", endpos) != -1) {
			position = expandedCommand.indexOf("${", endpos);
			endpos = expandedCommand.indexOf("}", position);
			String var = expandedCommand.substring(position + 2, endpos);
			log.log(Level.FINEST, "hit variable: " + var);
			log.log(Level.FINEST, "Buffer: " + expandedCommand);
			expandedCommand.replace(position, endpos + 1, handleTypes(parameters, var));
			endpos = 1;
		}
		return expandedCommand.toString();
	}
	 /**
	  * Returns the expanded value of a variable 
	  * Given a map of parameters, a variable Name,  
	  * @param parameters list of current parameters and values
	  * @param varName the variable to expand
	  * @return expanded version of parameter
	  * @throws ValidationException
	  */
	public String handleTypes(Map parameters, String varName) throws ValidationException {
		// todo Remove reliance of types on $ % and @ - these are for reading
		// purposes only
		log.log(Level.FINEST, "Var:" + varName);
		log.log(Level.FINEST, "Parameters:  " + parameters);
		Object x;
		String var = null;
		String type = null;
		boolean inTable = false;
		var = splitVar(varName);
		type = splitType(varName);
		if (varName.indexOf(':') == -1)
			if (symbolTable.containsKey(var)) {
				inTable = true;
			}
		x = parameters.get(var);
		if (x != null) {
			try {
				if (x instanceof Map) {
					String obName = type.substring(0, type.indexOf('.'));
					ObjectType obType;
					if (inTable)
						obType = (ObjectType) symbolTable.get(var);
					else
						obType = (ObjectType) types.get(obName);
					return handleObject((Map) parameters.get(var), obType, varName.substring(varName.indexOf('.') + 1));
				}
				if (x instanceof ArrayList) {
					//todo Handle ArrayType Here
					String arrName;
					if (varName.indexOf(":") != -1) {
						arrName = varName.substring(varName.indexOf(':') + 1);
					} else
						arrName = varName;
					ArrayType arrType;
					if (inTable)
						arrType = (ArrayType) symbolTable.get(var);
					else
						arrType = (ArrayType) types.get(arrName);
					return handleArray((ArrayList) parameters.get(var), arrType);
				}
				if (inTable)
					return handleSimpleType(parameters.get(var), symbolTable.get(type));
				else
					return handleSimpleType(parameters.get(var), types.get(type));
			} catch (ValidationException ex) {
				System.out.println("error: " + ex.getMessage());
				ex.printStackTrace();
				throw ex;
			}
		} else
			return "";
	}
	/**
	 * Perform regex validation or transformation on the given object
	 * @param obj object to validate or transform
	 * @param type type to use in processing
	 * @return
	 * @throws ValidationException
	 */
	private String handleSimpleType(Object obj, Object type) throws ValidationException {
		if (type != null) {
			if (type instanceof ScalarType) {
				ScalarType simple = (ScalarType) type;
				if (simple.isExpression()) {
					log.log(Level.FINEST, "Is an expression type");
					if (simple.parse(obj))

						return obj.toString();
				} else if (simple.allowableTransformFrom(obj))
					return (String) simple.transform(obj);
				else
					throw new ValidationException("Transformation cannot be done on this datatype");
			}
		}
		return obj.toString();
	}
	/**
	 * Expand an array of items with a given type definition
	 * @param items list of objects in the array
	 * @param arrType the array definiution to use when expanding
	 * @return string representation of the array
	 * @throws ValidationException
	 */
	private String handleArray(List items, ArrayType arrType) throws ValidationException {
		//Expand for each one and concat
		StringBuffer ret = new StringBuffer(50);
		ret.append(arrType.getBase());
		int loop = items.size();
		for (int i = 0; i < loop; i++) {
			StringBuffer currIdx = new StringBuffer(arrType.getRepeater());
			while (currIdx.indexOf("${") != -1) {
				String varName = currIdx.substring(currIdx.indexOf("${") + 2, currIdx.indexOf("}"));
				if (varName.trim().equals("[]"))
					currIdx.replace(currIdx.indexOf("${"), currIdx.indexOf("}") + 1, (String) items.get(i));
				else {
					Object x = items.get(i);
					//String var = splitVar(varName);
					String type = splitType(varName);
					if (x instanceof Map) {
						String obName = type.substring(0, type.indexOf('.'));
						if (obName.endsWith("."))
							obName = obName.substring(0, obName.length() - 1);
						ObjectType obType = (ObjectType) types.get(obName);
						currIdx.replace(currIdx.indexOf("${"), currIdx.indexOf("}") + 1, handleObject((Map) x, obType, varName.substring(varName.indexOf('.') + 1)));
					} else
						currIdx.replace(currIdx.indexOf("${"), currIdx.indexOf("}") + 1, handleSimpleType(x, types.get(type)));
				}
			}
			ret.append(currIdx);
			if (i < loop - 1)
				ret.append(arrType.getSeparator());
			if (i == 0) {
				//System.out.println("Resizing");
				currIdx.setLength(currIdx.length() * (loop - 2));
			}
		}
		ret.append(arrType.getTail());
		return ret.toString();
	}
	/**
	 * Expand a compound type -this mayt have recursive transformation or expansions (i.e. arrays or compound types nested in this object
	 * @param parameters
	 * @param obj
	 * @param varName
	 * @return
	 * @throws ValidationException
	 */
	private String handleObject(Map parameters, ObjectType obj, String varName) throws ValidationException {
		String var;
		String type;
		var = splitVar(varName);
		type = splitType(varName);
		if (varName.indexOf('.') != -1) {
			String obName = type.substring(0, type.indexOf('.'));
			ObjectType obType = (ObjectType) types.get(obName);
			if (!var.equals(varName)) {
				return handleObject((Map) parameters.get(var), obType, type.substring(type.indexOf('.') + 1));
			} else
				return handleObject((Map) parameters.get(obName), obType, type.substring(type.indexOf('.') + 1));
		}
		String subVar = splitVar(type);
		String subType = splitType(type);
		log.log(Level.FINEST, "Type: " + type);
		log.log(Level.FINEST, "Var: " + var);
		log.log(Level.FINEST, "SubVar: " + subVar);
		log.log(Level.FINEST, "SubType: " + subType);
		if (obj.getField(var) != null) {
			return handleSimpleType(parameters.get(var), types.get(type));
		} else {
			System.out.println("No item named: " + var);
		}
		return "";
	}

	private String splitType(String varName) {
		String type = "";
		String interested;
		if (varName.indexOf(".") != -1) {
			interested = varName.substring(0, varName.indexOf("."));
		} else
			interested = varName;
		if (interested.indexOf(":") != -1) {
			type = varName.substring(interested.indexOf(":") + 1);
		} else {
			type = varName;
		}
		return type;
	}

	private String splitVar(String varName) {
		String var;
		String interested;
		if (varName.indexOf(".") != -1) {
			interested = varName.substring(0, varName.indexOf("."));
		} else
			interested = varName;
		if (interested.indexOf(":") != -1) {
			var = interested.substring(0, interested.indexOf(":"));
		} else {
			var = interested;
		}
		return var;
	}

	/**
	 * Get a list of types available for this engine
	 * @return Map of type definitions
	 */
	public HashMap getTypes() {
		log.log(Level.FINEST, "Getting Type Definitions");
		return this.types;
	}
}