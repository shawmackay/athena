/*
 *  Dialect.java
 *
 *  Created on 10 September 2001, 13:20
 */

package org.jini.projects.athena.command.dialect;

/**
 * Represents the basic commands and process required for translating data
 * between Athena and a <CODE>SystemConnection</CODE> call.
 * 
 * @author calum
 *  
 */
public interface Dialect {

	/**
	 * Informs that this dialect will perform non-updating operations on the
	 * datasource. <em>Note: currently not used</em>
	 *  
	 */
	public final static int READ = 1;
	/**
	 * Informs that this dialect will performing updating operations on the
	 * datasource. <em>Note: currently not used</em>
	 *  
	 */
	public final static int WRITE = 2;
	/**
	 * Informs that this dialect will performing operations where the effect
	 * scope is not known at runtime <em>Note: currently not used</em>
	 */
	public final static int PROGRAM = 4;

	/**
	 * Initialises the Dialect object with the data required to build the data
	 * for the call
	 * 
	 * @param initialParams
	 *                  Parameters required for translation to occur
	 */
	public void init(Object[] initialParams);

	/**
	 * Gets the OutputHeader representing the field definitions after a call has
	 * been executed, used to build <CODE>ResultSet</CODE>s, etc.
	 * 
	 * @return Map of field Definitions
	 */
	public java.util.HashMap getOutputHeader();

	/**
	 * Performs the initial translation from the <CODE>Command</CODE>
	 * parameters to those needed by the Call prior to execution
	 * 
	 */
	public void processInput();

	/**
	 * Performs the second translation from the Call after execution to an
	 * Object that Athena can handle
	 */
	public void processOutput();

	/**
	 * Gets the raw input prior to execution but after <CODE>processInput()
	 * </CODE>.<BR>
	 * This is the actual input that will be fed as arguments to the call. <BR>
	 * This is useful for debugging
	 * 
	 * @return The Raw pre-Execution data that will be sent to the host
	 */
	public Object getCallInput();

	/**
	 * Gets the output from the execution after <CODE>processOutput()</CODE>
	 * has been called. <BR>
	 * This will be in a format that Athena can place either as a return code to
	 * the client or place into a <CODE>SystemResultSet</CODE> Object.
	 * 
	 * @return The CallOutput value

	 */
	public Object getCallOutput();

	/**
	 * Gets the type of execution that this call will make, such WRITE or READ
	 * 
	 * @return The executionType value

	 */
	public int getExecutionType();

	/**
	 * Sets the callReturn attribute of the Dialect object
	 * 
	 * @param obj
	 *                  The new callReturn value

	 */
	public void setCallReturn(Object obj);

	/**
	 * For generic dialects, the call name represents the processing
	 * specialisation as done pre and post call to the host, for instance
	 * parsing commareas
	 * 
	 * @param callName
	 *                  The new callReturn value

	 */

	public void setCallName(String callName);

	/**
	 * For generic dialect, the call alias represents the actual program/process
	 * called at the host, for instance a CICS transaction or program name.
	 * 
	 * @param callAlias
	 */
	public void setCallAlias(String callAlias);
}

