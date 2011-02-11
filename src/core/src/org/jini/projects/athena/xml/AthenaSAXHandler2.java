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
import java.util.Vector;

import org.xml.sax.SAXParseException;

public class AthenaSAXHandler2 extends org.xml.sax.helpers.DefaultHandler {
    HashMap details = new HashMap();
    Vector vec;
    HashMap execdetails;
    HashMap record;
    String field;
    String paramName;
    boolean inrecord = false;
    boolean inheader = false;
    boolean inparam = false;

    /** Creates new AthenaSAXHandler2 */
    public AthenaSAXHandler2() {
    }

    public void startElement(java.lang.String str, java.lang.String str1, java.lang.String str2, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
        if (str2.equals("param")) {
            paramName = attributes.getValue(0);
            inparam = true;
        }
        if (str2.equals("record")) {
            if (record != null)
                vec.add(record);
            record = new HashMap();
            inrecord = true;
            return;
        }
        if (str2.equals("header")) {
            inheader = true;
        }
        if (inrecord) {
            field = str2.toLowerCase();
        }
        if (str2.equals("item") && inheader) {
            details.put(attributes.getValue("name"), new Integer(Integer.parseInt((String) attributes.getValue("index"))));
        }

    }

    public void endElement(java.lang.String str, java.lang.String str1, java.lang.String str2) {

        if (str2.equals("param")) {
            inparam = false;
        }
        if (str2.equals("record")) {
            inrecord = false;
        }
        if (str2.equals("header")) {
            inheader = false;
        }


    }

    public void characters(char[] data, int start, int length) {
        if (inrecord) {
            record.put(field, new String(data, start, length));

        }
        if (inparam) {
            String value = new String(data, start, length);
            if (!(value.trim().equals(""))) {
                execdetails.put(paramName, value);
            }
        }
    }

    public void startDocument() throws org.xml.sax.SAXException {
        vec = new Vector();
        execdetails = new HashMap();
    }

    public void endDocument() throws org.xml.sax.SAXException {
        if (record != null)
            vec.add(record);
//        System.out.println("Vector: " + vec);

    }

    public Vector getData() {
        return vec;
    }

    public void error(SAXParseException spex) {
        System.err.println("Error: " + spex.getMessage());
        System.err.println("At: " + spex.getLineNumber() + " : " + spex.getColumnNumber());
        spex.printStackTrace();
    }

    public void fatalError(SAXParseException spex) {

        System.err.println("Error: " + spex.getMessage());
        System.err.println("At: " + spex.getLineNumber() + " : " + spex.getColumnNumber());
        spex.printStackTrace();
    }

    public HashMap getExceDetails() {
        return execdetails;
    }

    public HashMap getHeader() {
        return details;
    }
}
