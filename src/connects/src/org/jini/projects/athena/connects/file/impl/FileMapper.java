/*
 * athena.support.jini.org : org.jini.projects.athena.connects.file
 * 
 * 
 * FileMapper.java Created on 02-Nov-2004
 * 
 * FileMapper
 *  
 */

package org.jini.projects.athena.connects.file.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Constructs the mapping between a command alias and a file
 * 
 * @author calum
 */
public class FileMapper {
	private Map fileMap;

	public FileMapper(Map map) {
		this.fileMap = map;
	}
	
	public void addMap(String name, FileMap map){
		fileMap.put(name, map);
	}

	public FileMap getFileMap(String callName){
		return (FileMap)fileMap.get(callName);
	}
	
	public class FileMap{
		String fileName;
		RecordMap recordMap;
		List ignore = new ArrayList();
		public FileMap(String fileName, RecordMap recMap, List ignore){
			this.fileName = fileName;
			this.recordMap = recMap;
			this.ignore = ignore;
		}
		public FileMap(){
			
		}
		
		public List getIgnored() {
			return ignore;
		}
		public void setIgnored(List ignore) {
			this.ignore = ignore;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public void setRecordMap(RecordMap recordMap) {
			this.recordMap = recordMap;
		}
		public String getFileName() {
			return fileName;
		}
		public RecordMap getRecordMap() {
			return recordMap;
		}
		
		public void addIgnored(IgnoreMarker marker){
			ignore.add(marker);
		}
	}
	
	public class IgnoreMarker{
		private String regexMatcher;
		private String type;
		
		/**
		 * @param regexMatcher
		 * @param type
		 */
		public IgnoreMarker(String regexMatcher, String type) {
			super();
			this.regexMatcher = regexMatcher;
			this.type = type;
		}
		public String getRegexMatcher() {
			return regexMatcher;
		}
		public String getType() {
			return type;
		}
	}
	
	public class RecordMap {
		private int type = 0;
		List fields = new ArrayList();
		private String separator;
		public RecordMap(){
			
		}
	
		public String getSeparator() {
			return separator;
		}
		public void setSeparator(String separator) {
			this.separator = separator;
		}
		public void setType(int type) {
			this.type = type;
		}
		public RecordMap(List fields, String separator) {
			type = 1; // SequenceSeparated file
			this.fields = fields;
			this.separator = separator;
		}

		public RecordMap(List fixedWidthFields) {
			type =2; //Fixed Length Field Map;
			this.fields = fixedWidthFields;
		}
		
		public boolean isSeqSeperated(){
			return type==1;			
		}
		public boolean isFixedWidth(){
			return type==2;			
		}
		
		public void addSeparatedField(FieldType field){
			fields.add(field);
		}
		
		public void addFixedWidthField(FixedFieldType field){
			fields.add(field);
		}
		
		public List getFields() {
			return fields;
		}
	}

	public class FieldType {
		private String fieldname;
		private String fieldtype;

		public FieldType(String name, String type) {
			this.fieldname = name;
			this.fieldtype = type;
		}

		public String getName() {
			return fieldname;
		}

		public String getType() {
			return fieldtype;
		}
	}

	public class FixedFieldType extends FieldType {

		private int length = 0;

		public FixedFieldType(String name, String type, int length) {
			super(name, type);
			this.length = length;
		}

		public int getLength() {
			return length;
		}
	}
}
