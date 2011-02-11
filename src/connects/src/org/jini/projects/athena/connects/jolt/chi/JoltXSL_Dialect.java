/*
 *  TESTP11_Dialect.java
 *
 *  Created on 11 September 2001, 10:04
 */
package org.jini.projects.athena.connects.jolt.chi;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.command.dialect.DialectEngine;
import org.jini.projects.athena.command.types.TypeEngine;
import org.jini.projects.athena.exception.ValidationException;
import org.jini.projects.athena.xml.AthenaSAXHandler2;
import bea.jolt.JoltRemoteService;

/**
 * The default dialect used for BEA Jolt and Tuxedo.
 * This should suffice for most cases.<br/>
 * Restrictions:<br>
 * This uses one string parameter, that is both input and output parameter, named INOUT<br>
 * Encoding and decoding will be required at both ends, hence the use of XSLT
 * All other parameters will not be recognised, set or queried.
 */

public class JoltXSL_Dialect implements Dialect, java.io.Serializable {
    transient Vector returndata;
    private java.util.TreeMap params_in;
    private java.util.HashMap params_out = new HashMap();
    private java.util.HashMap header;
    private StringBuffer command = new StringBuffer();
    private transient JoltRemoteService joltService;
    private String CommandName;


    public JoltXSL_Dialect() {
    }

    /**
     * Create a new JoltXSL_dialect with the given name
     * @param name Name of the command to fire to Jolt
     */
    public JoltXSL_Dialect(String name) {
        this.CommandName = name;
    }


    public void setCallReturn(Object obj) {
    }


    public Object getCallOutput() {
        if (returndata.size() > 0) {

            return returndata;
        } else {

            return params_out;
        }
    }


    public Object getCallInput() {
        return command.toString();
    }


    public java.util.HashMap getOutputHeader() {
        return header;
    }

    /**
     * Always zero - as we do not know in advance if the program changes data
     */
    public int getExecutionType() {
        return 0;
    }


    public void processOutput() {
        try {
            String callret = joltService.getStringDef("INOUT", "");
            String wrapper = "<INPUT>" + callret + "</INPUT>";
            System.out.println("CommandName is: " + this.CommandName);
            System.out.println("Call return is: " + callret);
            Transformer processor =
                    DialectEngine.getEngine().getOutputTransform(this.CommandName);
            StringBuffer outputBuffer = new StringBuffer();
            StreamSource input = new StreamSource(new StringReader(wrapper));
            StringWriter outdest = new StringWriter();
            StreamResult output = new StreamResult(outdest);

            processor.transform(input, output);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            AthenaSAXHandler2 athenahandler = new AthenaSAXHandler2();
            org.xml.sax.helpers.DefaultHandler dh = athenahandler;

            ByteArrayInputStream bis = new ByteArrayInputStream(outdest.getBuffer().toString().getBytes());
            parser.parse(bis, dh);

            returndata = athenahandler.getData();
            params_out = athenahandler.getExceDetails();
            System.out.println("Output Parameters: " + params_out);
            header = athenahandler.getHeader();
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void processInput() {
        joltService.clear();

        Transformer processor =
                DialectEngine.getEngine().getInputTransform(this.CommandName);
        Set paramsSet = params_in.entrySet();
        Iterator iter = paramsSet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            processor.setParameter((String) entry.getKey(), entry.getValue());
        }
        StringBuffer outputBuffer = new StringBuffer();
        String wrapper = "<INPUT></INPUT>";
        StreamSource input = new StreamSource(new StringReader(wrapper));
        StringWriter outdest = new StringWriter();
        StreamResult output = new StreamResult(outdest);

        try {
            processor.transform(input, output);
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println(outdest.getBuffer().toString().trim());
        String preprocess = outdest.getBuffer().toString().trim();
        TypeEngine engine = TypeEngine.getTypeEngine();
        System.out.println("Preprocessed: " + preprocess);
        try {
            joltService.addString("INOUT", engine.process(preprocess, params_in));
        } catch (ValidationException e) {
            System.out.println();
        }
        TypeEngine.returnEngine(engine);
    }

    public void init(Object[] initials) {
        joltService = (JoltRemoteService) initials[0];
        params_in = (TreeMap) initials[1];
    }

    public void setCallName(String callName) {
        System.out.println("JoltXSL_Dialect: Setting callName to  " + callName);
        this.CommandName = callName;
    }

    /**
     *
     * This is not required as Jolt performs this mapping for us
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallAlias(java.lang.String)
     */
    public void setCallAlias(String callAlias) {
    }
}
