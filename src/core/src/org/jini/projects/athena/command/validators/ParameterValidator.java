package org.jini.projects.athena.command.validators;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jini.projects.athena.command.types.ObjectType;
import org.jini.projects.athena.command.types.ScalarType;
import org.jini.projects.athena.command.types.Type;
import org.jini.projects.athena.command.types.TypeEngine;
import org.jini.projects.athena.exception.ValidationException;
import org.jini.projects.athena.xml.TypeXMLLoader;

/**
 * Validate existence checks, that any types defined in the handler are available, and
 * checks regular expressions are valid for any parameters
 * @author calum
 */
public class ParameterValidator implements org.jini.projects.athena.command.validators.Validator {
	private HashMap types;
	private HashMap datatype = new HashMap();
	private HashMap rules = new HashMap();
	private HashMap symbolTable = new HashMap();
	private org.jini.projects.athena.command.Command command;
	private Logger log = Logger.getLogger("org.jini.projects.athena.command.validators");

	//TypeEngine eng;
	public ParameterValidator() {
	}

	/**
	 * Creates a new instance of DefaultValidator
	 * 
	 * @since
	 */
	public ParameterValidator(TypeEngine typer) {
		//eng = typer;
	}
	/**
	 * Sets the command that we want to validate
	 * @param comm  The command thet needs validating
	 */
	public void setCommand(org.jini.projects.athena.command.Command comm) {
		command = comm;
	}

	private boolean loadTypes() throws FileNotFoundException {
		//System.getProperties().put("org.jini.projects.athena.service.name",
		// "SALESUPR");
		BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "/config/handlers/" + System.getProperty("org.jini.projects.athena.service.name") + ".types.xml"));
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			TypeXMLLoader loader = new TypeXMLLoader();
			org.xml.sax.helpers.DefaultHandler dh = loader;
			parser.parse(b_xmlin, dh);
			types = loader.getLoadedTypes();
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
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
		 * '${object.dummy.name}')", ",", ")"); types.put("arr", arr); return
		 * true;
		 */
	}
	/**
	 *Runs the validation against the command to check if all defined variables match their given rules
	 * @exception ValidationException Thrown if the system find the command to be invalid.

	 */
	public void isValid() throws ValidationException {
		TypeEngine eng = TypeEngine.getTypeEngine();
		checkLevel(command.getParameters(), this.datatype, "", this.rules, eng);
		TypeEngine.returnEngine(eng);
	}

	public void checkLevel(Map params, Map types, String objectname, Map rules, TypeEngine eng) throws ValidationException {
		boolean typehandled = false;
		log.log(Level.FINE, "Calling is valid");
		Iterator iter = params.entrySet().iterator();
		log.log(Level.FINE, "CHI: Validating");
		boolean validationResult = true;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			if (rules.containsKey(name.toLowerCase())) {
				int validRule = ((Integer) rules.get(name.toLowerCase())).intValue();
				if ((validRule & Validator.NOTNULL) == Validator.NOTNULL) {
					if (entry.getValue() == null) {
						throw new ValidationException("Validation Error: " + name + " cannot be null");
					}
				}
			}
		}
		if (validationResult == true) {
			//Check against required parameters
			iter = rules.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String name = (String) entry.getKey();
				log.log(Level.FINE, "Checking:  " + name + "{" + name.toLowerCase() + "}");
				int validRule = ((Integer) rules.get(name.toLowerCase())).intValue();
				if ((validRule & Validator.REQUIRED) == Validator.REQUIRED) {
					if (!params.containsKey(name.toLowerCase()) && !params.containsKey(name.toUpperCase())) {
						throw new ValidationException("Validation Error: " + name + " is required");
					}
				}
			}
		}
		//ExistenceChecking is complete, check datatypes are valid
		//
		//NOTE: Any transformation types, must be placed in either the replaced command or the dialect pre-processing.
		//For replaced commands somthing similar to
		// INSERT INTO mytable value(${id},${fname},${lname:quoted_string}) 
		// should be used
		
		
		iter = types.entrySet().iterator();
		log.log(Level.FINEST, "Datatype checking on datatype set " + datatype);
		while (iter.hasNext()) {
			log.log(Level.FINEST, "Got next Item");
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			//if (DEBUG)
			log.log(Level.INFO, "Checking datatype of " + name + "[" + entry.getValue() + "]");
			if (eng == null)
				log.log(Level.INFO, "Your engine is null");
			if (eng.containsTypeDef((String) entry.getValue())) {
				Type type = eng.getTypeDef((String) entry.getValue());
				log.log(Level.INFO, "TypeDef Class : " + type.getClass().getName());
				if (type instanceof ScalarType) {
					ScalarType stype = (ScalarType) type;
					if (!stype.isExpression()) {
						log.log(Level.INFO, "Transformer Class:" + entry.toString());
						initiateTransform(params, entry, stype.getTransformType_Name());
						typehandled = true;
					} else {
						log.log(Level.INFO, "Expression Class:");
						stype.parse(params.get(name));
						typehandled = true;
					}
				}
				if (type instanceof ObjectType) {
					ObjectType objType = (ObjectType) type;
					log.log(Level.INFO, "Object Class:" + entry.toString());
					System.out.println("Checking Object...." + name);
					checkLevel((Map) params.get(name), objType.getTypes(), "", objType.getRules(), eng);
				}
				if (!typehandled)
					this.symbolTable.put(name, type);
			} else {
				log.log(Level.WARNING, "Type Engine does not understand " + name);
			}
		}
	}

	public void initiateTransform(Map params, Map.Entry entry, String name) throws ValidationException {
		log.warning("Transformation not allowed during validation - modify the replaced command to ${var:transform_type} or handle this thorugh the dialect");
		return;
//		try {
//			log.log(Level.INFO, "SYSTEM Is accessing transforms during validation!");
//			String dtypeEntry = (String) entry.getValue();
//			String dtypeFormat = null;
//			System.out.println(entry.getKey().toString() + ":" + dtypeEntry);
//			if (dtypeEntry.indexOf(":") != -1) {
//				dtypeFormat = dtypeEntry.substring(dtypeEntry.indexOf(":") + 1);
//				dtypeEntry = dtypeEntry.substring(0, dtypeEntry.indexOf(":"));
//			}
//			Transformer dtype = null;
//			Class x = null;
//			try {
//				x = Class.forName(name.trim());
//			} catch (ClassNotFoundException ex) {
//				log.log(Level.INFO, "FQ Class not found");
//			}
//			try {
//				if (x != null)
//					dtype = (Transformer) x.newInstance();
//				else {
//					log.log(Level.INFO, "Falling back to org.jini.projects.org.jini.projects.athena.command.types.transforms." + name.trim() + "_type");
//					dtype = (Transformer) Class.forName("org.jini.projects.athena.command.types.transforms." + name.trim() + "_type").newInstance();
//				}
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//				throw new ValidationException("Validation Error: " + name + " Datatype could not be loaded");
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//				throw new ValidationException("Validation Error: " + name + " Datatype could not be loaded");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				throw new ValidationException("Validation Error: " + name + " Datatype could not be loaded");
//			}
//			dtype.set(params.get((String) entry.getKey()));
//			if (dtypeFormat != null) {
//				dtype.setFormat(dtypeFormat);
//			}
//			log.log(Level.INFO, "Setting parameter of " + entry.getKey().toString() + " from " + params.get(entry.getKey().toString()) + " to " + dtype.get());
//			params.put(entry.getKey().toString(), dtype.get());
//		} catch (Exception e) {
//			System.out.println("Err: " + e.getMessage());
//			e.printStackTrace();
//			if (e instanceof ValidationException)
//				throw (ValidationException) e;
//			else {
//				e.printStackTrace();
//				throw new ValidationException("Command failed validation: " + e.getMessage());
//			}
//		}
	}

	public HashMap getSymbolTable() {
		return symbolTable;
	}
	
	/**
	 * Adds a multiple rules to the current rule set
	 */
	public void addRules(java.util.HashMap rules) {
		this.rules.putAll(rules);
	}

	/**
	 * Sets the rule for a particular parameter name, rules are based on a combined value 
	 * 
	 * @param parameter The  parameter the rule applies to
	 * @param validRule A Value representing the new Rule
	 */
	public void setRule(String parameter, int validRule) {
		rules.put(parameter, new Integer(validRule));
	}
	
	/**
	 * Provides the link between a particular parameter name and the type it represents
	 */

	public void setDataType(String parameter, String typename) {
		datatype.put(parameter, typename);
	}
}