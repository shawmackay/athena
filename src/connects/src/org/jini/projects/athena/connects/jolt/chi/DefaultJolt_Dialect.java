/*
 *  TESTP11_Dialect.java
 *
 *  Created on 10 September 2001, 13:06
 */
package org.jini.projects.athena.connects.jolt.chi;

import bea.jolt.JoltDefinition;
import bea.jolt.JoltParam;
import bea.jolt.JoltRemoteService;
import bea.jolt.SBuffer;

import java.util.Enumeration;
import java.util.HashMap;

import org.jini.projects.athena.command.dialect.Dialect;

/**
 * This handles parameterised data from the JoltDefinition
 * This is not used, instead, where possible use JoltXSL_Dialect instead
 */
public class DefaultJolt_Dialect implements Dialect {
    HashMap header = new HashMap();
    JoltRemoteService joltService;
    JoltDefinition joltDefinition;
    HashMap params_in;
    HashMap params_out = new HashMap();


    /**
     *  Creates a new DefaultJolt_Dialect. This handles most instances where in
     *  Jolt, you have defined the JoltDefinition, using something like FML.<BR>
     *  You may have a COBOL copybook that mirrors this definition, where you have
     *  a one-to-one relationship in the arguments.<BR>
     *  In cases where you have combined arguments, for instance a single String
     *  representation built up from oth erarguments that need combining and
     *  splitting before and after the call create a specific Dialect to handle
     *  that
     *
     *@since
     */
    public DefaultJolt_Dialect() {
    }


    /**
     *  Sets the callReturn attribute of the DefaultJolt_Dialect object
     *
     *@param  obj  The new callReturn value
     *@since
     */
    public void setCallReturn(Object obj) {
    }


    /**
     *  Gets the callOutput attribute of the DefaultJolt_Dialect object
     *
     *@return    The callOutput value
     *@since
     */
    public Object getCallOutput() {
        Enumeration enum = joltDefinition.getParams();
        int i = 0;
        while (enum.hasMoreElements()) {
            JoltParam jParam = (JoltParam) enum.nextElement();
            String pName = jParam.getName();
            header.put(pName, new Integer(i++));
            switch (jParam.getType()) {
                case SBuffer.SSTRING:
                    params_out.put(pName, joltService.getStringDef(pName, null));
                    break;
                case SBuffer.SINT:
                    params_out.put(pName, new Integer(joltService.getIntDef(pName, 0)));
                    break;
            }
        }
        return params_out;
    }


    /**
     *  Gets the callInput attribute of the DefaultJolt_Dialect object
     *
     *@return    The callInput value
     *@since
     */
    public Object getCallInput() {
        return params_in;
    }


    /**
     *  Gets the outputHeader attribute of the DefaultJolt_Dialect object
     *
     *@return    The outputHeader value
     *@since
     */
    public java.util.HashMap getOutputHeader() {
        return header;
    }


    /**
     *  Gets the executionType attribute of the DefaultJolt_Dialect object
     *
     *@return    The executionType value
     *@since
     */
    public int getExecutionType() {
        return 0;
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void processOutput() {
    }


    /**
     *  Description of the Method
     *
     *@since
     */
    public void processInput() {
        Enumeration enum = joltDefinition.getParams();
        while (enum.hasMoreElements()) {
            JoltParam jParam = (JoltParam) enum.nextElement();
            String pName = jParam.getName();
            switch (jParam.getType()) {
                case SBuffer.SSTRING:
                    if (params_in.get(pName) instanceof String) {
                        joltService.addString(pName, (String) params_in.get(pName));
                    }
                    break;
                case SBuffer.SINT:
                    if (params_in.get(pName) instanceof Integer) {
                        joltService.addInt(pName, ((Integer) params_in.get(pName)).intValue());
                    }
                    break;
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  initials  Description of Parameter
     *@since
     */
    public void init(Object[] initials) {
        joltService = (JoltRemoteService) initials[0];
        joltDefinition = joltService.getDefinition();
        params_in = (HashMap) initials[1];
    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallName(java.lang.String)
     */
    public void setCallName(String callName) {
    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallAlias(java.lang.String)
     */
    public void setCallAlias(String callAlias) {
    }

}

