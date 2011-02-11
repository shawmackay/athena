/*
 * athena.jini.org : org.jini.projects.athena.xml
 * 
 * 
 * HarnessLoader.java
 * Created on 30-Apr-2004
 * 
 * HarnessLoader
 *
 */
package org.jini.projects.athena.xml;

import java.util.ArrayList;
import java.util.List;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.AthenaConnection;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author calum
 */
public class HarnessLoader extends DefaultHandler {
    private AthenaConnection conn;
    
    private List commands = new ArrayList();
    public HarnessLoader(AthenaConnection conn){
    	   this.conn = conn;
    }
    
    private Command currentCommand;
    
    public List getCommands(){
        return commands;
    }
    

    
	/* @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		// TODO Complete method stub for endDocument
		System.out.println("Document completed");
	}
	/* @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Complete method stub for endElement
		super.endElement(uri, localName, qName);
        if(qName.equals("command")){
        	System.out.println("Command [" + currentCommand.getCallName() + "] added to stack");
            commands.add(currentCommand);
        }
	}
	/* @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		// TODO Complete method stub for error
		super.error(e);
	}
	/* @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		// TODO Complete method stub for fatalError
		super.fatalError(e);
	}
	/* @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		// TODO Complete method stub for startDocument
		super.startDocument();
        System.out.println("Starting document read");
	}
	/* @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Complete method stub for startElement
		super.startElement(uri, localName, qName, attributes);
        if(qName.equals("command")){
         try {
			currentCommand = conn.getCommand();
		} catch (Exception e) {
			// URGENT Handle Exception
			e.printStackTrace();
		}
         currentCommand.setCallName( attributes.getValue("callname"));         
         System.out.println("created new Command: " + currentCommand.getCallName());
        }
        if(qName.equals("param")){
        	currentCommand.setParameter(attributes.getValue("name"), attributes.getValue("value"));
            System.out.println("Added parameter: " + attributes.getValue("name") + " to Command");
        }
	}
	/* @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	public void warning(SAXParseException e) throws SAXException {
		// TODO Complete method stub for warning
		super.warning(e);
	}
}
