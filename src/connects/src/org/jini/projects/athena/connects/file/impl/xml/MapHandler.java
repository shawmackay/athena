/*
 * athena.support.jini.org : org.jini.projects.athena.connects.file.impl.xml
 * 
 * 
 * MapHandler.java
 * Created on 03-Nov-2004
 * 
 * MapHandler
 *
 */
package org.jini.projects.athena.connects.file.impl.xml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jini.projects.athena.command.Handler;
import org.jini.projects.athena.connects.file.impl.FileMapper;
import org.jini.projects.athena.xml.CHILoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * @author calum
 */
public class MapHandler extends DefaultHandler {
	FileMapper mapper;
	FileMapper.FileMap map;
	FileMapper.RecordMap rec;
	String filename;
	boolean fixedwidthFields=false;
	public MapHandler(FileMapper mapper){
		this.mapper = mapper;
	}
	
	public void endDocument() throws SAXException {
		// TODO Complete method stub for endDocument
		super.endDocument();		
	}
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Complete method stub for endElement
		super.endElement(uri, localName, qName);
		if (qName.equals("record")){		
			map.setRecordMap(rec);
		}
		if(qName.equals("map")){
			mapper.addMap(filename, map);
		
		}
	}
	
	public FileMapper getMapper(){
		return mapper;
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Complete method stub for startElement
		super.startElement(uri, localName, qName, attributes);
		
		if (qName.equals("map")){			
			map = mapper.new FileMap();
			filename = attributes.getValue("file");
		}
		if(qName.equals("ignore-line")){			
			if(attributes.getValue("start")!=null)
				map.addIgnored(mapper.new IgnoreMarker(attributes.getValue("start"), "start"));
			else
				map.addIgnored(mapper.new IgnoreMarker(attributes.getValue("after"), "after"));
		}
		if(qName.equals("record")){
			rec = mapper.new RecordMap();
			String separate = attributes.getValue("fields-separated-by");
			if(separate.equals("fixed")){
				rec.setType(2);
				fixedwidthFields = true;
			}
			else{
				rec.setType(1);
				rec.setSeparator(separate);
				fixedwidthFields = false;
			}
		}
		if(qName.equals("field")){
			String fieldName= attributes.getValue("name");
			String fieldType= attributes.getValue("type");
			if(fixedwidthFields){
				int length = Integer.parseInt(attributes.getValue("length"));
				rec.addFixedWidthField(mapper.new FixedFieldType(fieldName, fieldType, length));
			} else
				rec.addSeparatedField(mapper.new FieldType(fieldName, fieldType));			
		}
	}
	
	public static void main(String[] args){
	     try {
			BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream("hostsmap.xml"));
			 SAXParserFactory factory = SAXParserFactory.newInstance();
			 SAXParser parser = factory.newSAXParser();
			 HashMap hsh = new HashMap();
			 FileMapper fileMapSet = new FileMapper(hsh);
			 MapHandler loader = new MapHandler(fileMapSet);
			 org.xml.sax.helpers.DefaultHandler dh = loader;
			 parser.parse(b_xmlin, dh);
			 System.out.println(hsh);
			 Object o =fileMapSet.getFileMap("/etc/hosts"); 
			 if(o!=null){
			 	System.out.println("FileMap blah is loaded");
			 	FileMapper.FileMap f = (FileMapper.FileMap) o;
			 	FileMapper.RecordMap recMap = f.getRecordMap();
			 	List l = recMap.getFields();
			 	Iterator iter = l.iterator();
			 	while(iter.hasNext()){
			 		FileMapper.FieldType field = (FileMapper.FieldType) iter.next();
			 		System.out.println("\tField: " + field.getName() + ", " + field.getType());
			 	}
			 }
		} catch (FileNotFoundException e) {
			// TODO Handle FileNotFoundException
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Handle FactoryConfigurationError
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Handle ParserConfigurationException
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Handle SAXException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}
         
	}
}
