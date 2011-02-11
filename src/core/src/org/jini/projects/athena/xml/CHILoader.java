package org.jini.projects.athena.xml;

/*
 * AthenaSAXHandler2.java
 *
 * Created on 23 October 2001, 14:15
 */

/**
 *
 * ~~author  calum
 * @version 0.9community */

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jini.projects.athena.command.HandledCommand;
import org.jini.projects.athena.command.Handler;
import org.jini.projects.athena.command.PreparedStatementParameter;
import org.jini.projects.athena.command.validators.ParameterValidator;
import org.jini.projects.athena.command.validators.Validator;
import org.xml.sax.SAXParseException;

public class CHILoader extends org.xml.sax.helpers.DefaultHandler {
    Vector commands = new Vector();
    HandledCommand hcomm;
    StringBuffer strb = new StringBuffer();
    //StringBuffer command;
    String handlerName;
    String contextKey;
    //String type;
    String tuneEntryType;
    HashMap tuningParms;
    String cachepattern;
    List prepare_params = new ArrayList();
    boolean updateOrRemove;
    ParameterValidator validator;
    boolean inName = false;
    boolean inContext = false;
    boolean inCommand = false;
    boolean inTuning = false;
    boolean inTuningEntry = false;
    static HashMap chiValidators = new HashMap();

    public CHILoader() {
    }

    public void startElement(java.lang.String str, java.lang.String str1, java.lang.String str2, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
        //System.out.println("Starting Element " + str2) ;
        if (str2.equals("name")) {
            inName = true;
        }
        if (str2.equals("context")) {
            inContext = true;

            contextKey = attributes.getValue("key");
        }
        if (str2.equals("params")) {
            validator = new ParameterValidator();
        }
        if (str2.equals("cache-update")) {
            cachepattern = attributes.getValue("pattern");
            updateOrRemove = true;
        }
        if (str2.equals("cache-remove")) {
            cachepattern = attributes.getValue("pattern");
            updateOrRemove = false;
        }
        if (str2.equals("param")) {
            String validatingParam = attributes.getValue("name");
            String validRule = attributes.getValue("validation");
            String datatype = attributes.getValue("type");
           
            
            String datatypeformat = attributes.getValue("typeformat");
            if (validRule == null)
                validRule = "OPTIONAL";
            int rule = 0;
            // System.out.println("PARAM: " + validatingParam + " RULE: " + validRule);
            StringTokenizer token = new StringTokenizer(validRule, "|");
            while (token.hasMoreTokens()) {
                String singlerule = token.nextToken();
                if (singlerule.equals("NOTNULL"))
                    rule = rule | Validator.NOTNULL;
                if (singlerule.equals("REQUIRED"))
                    rule = rule | Validator.REQUIRED;
                if (singlerule.equals("OPTIONAL"))
                    rule = rule | Validator.OPTIONAL;
            }
            validator.setRule(validatingParam, rule);
            if (datatype != null) {
                if (datatypeformat != null)
                    validator.setDataType(validatingParam, datatype + ":" + datatypeformat);
                else
                    validator.setDataType(validatingParam, datatype);
            }
        }
        if (str2.equals("command")) {
            hcomm = new HandledCommand();
            hcomm.setType(attributes.getValue("type"));
            hcomm.setAlias(attributes.getValue("alias"));
            hcomm.setReturnInType(attributes.getValue("return_as"));
            hcomm.setOptype(attributes.getValue("optype"));
            hcomm.setUseCache(false);
            if (attributes.getValue("useCache") != null)
                if (attributes.getValue("useCache").equals("true"))
                    hcomm.setUseCache(true);

            strb = new StringBuffer();
            inCommand = true;
        }
        if(str2.equals("command-text"))
        	inCommand = true;
        if(str2.equals("prepared-param")){
        	String type = attributes.getValue("type").toLowerCase();
        	int index = Integer.parseInt(attributes.getValue("index"));
        	String columnName = attributes.getValue("comm-param");
        	PreparedStatementParameter param = new PreparedStatementParameter(index,columnName,type);
        	prepare_params.add(param);
        }
        if (str2.equals("tuning")) {
            tuningParms = new HashMap();
        }
        if (str2.equals("entry")) {
            tuneEntryType = attributes.getValue("name");
            inTuningEntry = true;
        }
    }

    public void endElement(java.lang.String str, java.lang.String str1, java.lang.String str2) {

        if (str2.equals("command")) {
            inCommand = false;
            hcomm.setPrepared_params(prepare_params);
            this.commands.add(hcomm);
        }
        if (str2.equals("name")) {
            inName = false;
        }
        if (str2.equals("context")) {
            inContext = false;
        }
        if (str2.equals("tuning")) {
            inTuning = false;
        }
        if (str2.equals("entry")) {
            inTuningEntry = false;
        }
    }

    public void characters(char[] data, int start, int length) {
        //System.out.println("Got characters");
        if (inName) {
            handlerName = new String(data, start, length).trim();
        }
        if (inCommand) {
            strb.append(data, start, length);
            hcomm.setCommand(strb);
        }
        if (inTuningEntry) {
            //System.out.println("Setting Tuning Parameter: "  + tuneEntryType + " => " + new String(data,start,length));
            tuningParms.put(tuneEntryType, new String(data, start, length));
        }


    }

    public void startDocument() throws org.xml.sax.SAXException {
        //System.out.println("Starting Document");
    }

    public void endDocument() throws org.xml.sax.SAXException {
        //System.out.println("Ending Document");

    }

    public Handler getHandlerInstance() {
        Handler handle = new Handler(this.handlerName);
        handle.setValidator(validator);
        handle.setHandlerCommands(this.commands);
        handle.setTuningParameters(tuningParms);
        handle.setCacheManipulation(updateOrRemove, cachepattern);
    
        return handle;
    }

    public void error(SAXParseException spex) {
        System.out.println("Error: " + spex.getMessage());
        System.out.println("At: " + spex.getLineNumber() + " : " + spex.getColumnNumber());
        spex.printStackTrace();
    }

    public void fatalError(SAXParseException spex) {

        System.out.println("Error: " + spex.getMessage());
        System.out.println("At: " + spex.getLineNumber() + " : " + spex.getColumnNumber());
        spex.printStackTrace();
    }


}
