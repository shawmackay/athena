/*
 * DefaultValidator.java
 * 
 * Created on 29 January 2002, 11:00
 */

package org.jini.projects.athena.command.validators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.exception.ValidationException;

/**
 * A Simple validator that just handles existence checking for types 
 * Now superceded by ParameterValidator
 * @see org.jini.projects.athena.command.validators.ParameterValidator
 */
public class DefaultValidator implements Validator {
	Command command;
	HashMap rules = new HashMap();
	HashMap datatype = new HashMap();
	
    private Logger log = Logger.getLogger("org.jini.projects.athena.command.validators");
	/**
	 * Creates a new instance of DefaultValidator

	 */
	public DefaultValidator() {
	}

	/**
	 * Sets the command that we want to validate
	 * @param command  The new command value
	 */
	public void setCommand(org.jini.projects.athena.command.Command command) {
		this.command = command;
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
	 *Runs the validation against the command to check if all defined variables match their given rules
	 * @exception ValidationException Thrown if the system find the command to be invalid.

	 */
	public void isValid() throws ValidationException {
		TreeMap params = command.getParameters();
		Iterator iter = params.entrySet().iterator();
		log.log(Level.FINEST, new java.util.Date() + ": CHI: Validating");
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
				log.log(Level.FINEST, "Checking:  " + name + "{" + name.toLowerCase() + "}");
				int validRule = ((Integer) rules.get(name.toLowerCase())).intValue();
				if ((validRule & Validator.REQUIRED) == Validator.REQUIRED) {
					if (!params.containsKey(name.toLowerCase()) && !params.containsKey(name.toUpperCase())) {
						throw new ValidationException("Validation Error: " + name + " is required");
					}
				}
			}
		}
		
	}

	/**
	 * Adds a multiple rules to the current rule set
	 */
	public void addRules(java.util.HashMap rules) {
		this.rules.putAll(rules);
	}
	/**
	 * Provides the link between a particular parameter name and the type it represents
	 */
	public void setDataType(String parameter, String typename) {
		datatype.put(parameter, typename);
	}
}