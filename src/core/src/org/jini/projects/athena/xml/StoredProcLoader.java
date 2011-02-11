/*
 * athena.jini.org : org.jini.projects.athena.xml
 * 
 * 
 * StoredProcLoader.java Created on 13-Apr-2004
 * 
 * StoredProcLoader
 *  
 */

package org.jini.projects.athena.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author calum
 */
public class StoredProcLoader extends DefaultHandler {
	ArrayList procedures = new ArrayList();

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String procedurename = attributes.getValue("name");
		if (procedurename != null)
			procedures.add(procedurename);
	}

	public static void main(String args[]) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			StoredProcLoader loader = new StoredProcLoader();
			FileInputStream fis = new FileInputStream("config/handlers/SALESUPTproc2.xml");
			parser.parse(fis, loader, "config/handlers/SALESUPTproc2.xml");
			System.out.println(loader.getProcedures());
		} catch (FileNotFoundException e) {
			// URGENT Handle FileNotFoundException
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// URGENT Handle FactoryConfigurationError
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// URGENT Handle ParserConfigurationException
			e.printStackTrace();
		} catch (SAXException e) {
			// URGENT Handle SAXException
			e.printStackTrace();
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	public List getProcedures() {
		return procedures;
	}
}