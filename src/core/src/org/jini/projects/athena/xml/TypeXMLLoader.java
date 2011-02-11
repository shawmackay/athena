package org.jini.projects.athena.xml;

/*
 * AthenaSAXHandler2.java
 *
 * Created on 23 October 2001, 14:15
 */

/**
 *
 *
 * @version 0.9community */

import java.util.HashMap;
import java.util.StringTokenizer;

import org.jini.projects.athena.command.types.ArrayType;
import org.jini.projects.athena.command.types.ObjectType;
import org.jini.projects.athena.command.types.ScalarType;
import org.jini.projects.athena.command.validators.Validator;
import org.xml.sax.SAXParseException;

public class TypeXMLLoader extends org.xml.sax.helpers.DefaultHandler {

    HashMap loadedtypes = new HashMap();
    String typename;
    boolean inrecord = false;

    boolean inheader = false;
    boolean intype = false;
    private int typeSpec = 0;
    private final int ARRAY = 1;
    private final int OBJECT = 2;
    private final int EXPRESSION = 3;
    private final int TRANSFORM = 4;
    private ArrayType arrtype;
    private ObjectType obtype;
    private ScalarType simtype;
    private String header;
    private String repeater;
    private String separator;
    private String tail;
    private String fieldname;
    private String fieldtype; //Not used to any great extent
    private String transform;
    private String expression;

    /** Creates new AthenaSAXHandler2 */
    public TypeXMLLoader() {
    }

    public void startElement(String str, String str1, String str2, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
        if (str2.equals("type")) {
            typename = attributes.getValue(0);
            intype = true;
        }
        if (str2.equals("array")) {
            typeSpec = ARRAY;
            arrtype = new ArrayType();
            return;
        }
        if (str2.equals("object")) {
            typeSpec = OBJECT;
            obtype = new ObjectType();

            return;
        }
        if (str2.equals("expression")) {
            simtype = new ScalarType();
            simtype.setExpression(attributes.getValue(0));
            typeSpec = EXPRESSION;
            return;
        }
        if (str2.equals("transform")) {
            simtype = new ScalarType();

            simtype.setTransformType_Class(attributes.getValue(0));
            typeSpec = TRANSFORM;
            return;
        }
        // Begin Array Element Handling
        if (str2.equals("header")) {
            arrtype.setBase(attributes.getValue(0));
        }
        if (str2.equals("repeater")) {
            arrtype.setRepeater(attributes.getValue(0));
        }
        if (str2.equals("separator")) {
            arrtype.setSeparator(attributes.getValue(0));
        }
        if (str2.equals("tail")) {
            arrtype.setTail(attributes.getValue(0));
        }
        // End Array Element Handling
        //Begin Object Element Handling
        if (str2.equals("field")) {
            String name = attributes.getValue("name");
            String type = attributes.getValue("type");

            if (type == null || type.equals(""))
                type = "string";
            String validRule = attributes.getValue("validation");
            if (validRule != null) {
                StringTokenizer token = new StringTokenizer(validRule, "|");
                int rule = 0;
                while (token.hasMoreTokens()) {
                    String singlerule = token.nextToken();
                    if (singlerule.equals("NOTNULL"))
                        rule = rule | Validator.NOTNULL;
                    if (singlerule.equals("REQUIRED"))
                        rule = rule | Validator.REQUIRED;
                    if (singlerule.equals("OPTIONAL"))
                        rule = rule | Validator.OPTIONAL;
                }
                obtype.addRule(name, rule);
            }
            obtype.addField(name, type);
        }
        if (str2.equals("java"))
            simtype.addAllowableTransform(attributes.getValue(0));
    }

    public void endElement(String str, String str1, String str2) {

        if (str2.equals("array")) {
            typeSpec = 0;
            loadedtypes.put(typename, arrtype);
        }
        if (str2.equals("object")) {
            typeSpec = 0;
            loadedtypes.put(typename, obtype);
        }
        if (str2.equals("expression")) {
            typeSpec = 0;
            loadedtypes.put(typename, simtype);
        }
        if (str2.equals("transform")) {
            typeSpec = 0;
            loadedtypes.put(typename, simtype);
        }
        if (str2.equals("type")) ;
        //System.out.print(".");
    }

    public void characters(char[] data, int start, int length) {
    }

    public void startDocument() throws org.xml.sax.SAXException {
        //System.out.print("Starting type loading");
    }

    public void endDocument() throws org.xml.sax.SAXException {
        
    }

    public HashMap getLoadedTypes() {
        return loadedtypes;
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
