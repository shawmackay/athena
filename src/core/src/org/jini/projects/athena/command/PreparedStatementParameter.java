/*
 * athena.support.jini.org : org.jini.projects.athena.connects.sql.chi
 * 
 * 
 * PreparedStatementParameter.java
 * Created on 28-Oct-2004
 * 
 * PreparedStatementParameter
 *
 */
package org.jini.projects.athena.command;

import java.io.Serializable;

/**
 * @author calum
 */
public class PreparedStatementParameter implements Serializable{
	int paramNumber;
	String columnName;
	String type;
	
	
	/**
	 * Mapping between Athena Parameter & [position, SQL Type].
	 * Generates a mapping between an Athena parameter, 
	 * and the SQL type and position the parameter should be place in, when using a
	 * Prepared Statement call 
	 * @param paramNumber
	 * @param columnName
	 * @param type
	 */
	public PreparedStatementParameter(int paramNumber, String columnName, String type) {
		super();
		this.paramNumber = paramNumber;
		this.columnName = columnName;
		this.type = type;
	}
	/**
	 * @return Returns the columnName.
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @return Returns the parameter position..
	 */
	public int getParamNumber() {
		return paramNumber;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
}
