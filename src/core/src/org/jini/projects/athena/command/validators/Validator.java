/*
 *  Validator.java
 *
 *  Created on 29 January 2002, 10:59
 */
package org.jini.projects.athena.command.validators;

import org.jini.projects.athena.exception.ValidationException;

/**
 * Defines a way to add Validation rules and check if those rules apply to a certain Command
 *@author     calum
 */
public interface Validator {
    /**
     *  The field is required to be specified    
     */
    public static int REQUIRED = 1;
    /**
     * The field may be optional
     */
    public static int OPTIONAL = 2;
    /**
     *  The field, if specified in the command, must not be null     
     */
    public static int NOTNULL = 4;


    /**
     *  Sets the command attribute of the Validator object
     *
     *@param  command  The new command value
     *@since
     */
    public void setCommand(org.jini.projects.athena.command.Command command);


    /**
     * Validates the command 
     * @throws ValidationException if the command is invalid
     */
    public void isValid() throws ValidationException;


	/**
	 * Sets the rule for a particular parameter name, rules are based on a combined value 
	 * 
	 * @param parameter The  parameter the rule applies to
	 * @param validRule A Value representing the new Rule
	 */
    public void setRule(String parameter, int validRule);


    /**
	 * Adds a multiple rules to the current rule set
	 */
    public void addRules(java.util.HashMap rules);
	/**
	 * Provides the link between a particular parameter name and the type it represents
	 */

    public void setDataType(String parameter, String typename);
}

