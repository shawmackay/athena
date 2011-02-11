/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 29-Aug-2002
 * Time: 13:33:35
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command;

import java.util.HashMap;
/**
 * Simple Compound Type implementation based on an underlying HashMap
 * @author calum
 */
public class StdCompoundType extends HashMap implements CompoundType {

	/**
     * Creates an empty Compound Type
	 */
    public StdCompoundType() {
    }

    /**
     * Sets the field <i>name</i> to the given value
     * @param name
     * @param value
     */
    public void setField(String name, Object value) {
        this.put(name.toLowerCase(), value);
    }


    public Object getField(String name) {
        return this.get(name.toLowerCase());

    }

    /**
     * Returns the current number of defined fields in this CompoundType
     * @return number of fields
     */
    public int getNumFields() {
        return this.size();

    }

    /**
     * Returns whether there is a value assigned to the given name
     * @param fieldName field to Check
     * @return indicator of assignment.
     */
    public boolean isAssigned(String fieldName) {
        return (this.containsKey(fieldName.toLowerCase()));
    }

    /**
     * Unassigns all fields
     */
    public void clearFields() {
        this.clear();
    }

    /**
     * Creates a compund type bound to the given field name, i.e. nested types
     *
     * @param name
     * @return CompundType instance bound to name
     */
    public CompoundType createCompundType(String name) {
        StdCompoundType comptype = new StdCompoundType();
        this.put(name.toLowerCase(), comptype);
        return comptype;
    }

    /**
     *  Creates an Array type bound to the given field name
     * @param name
     * @return Array instance bound to name
     */
    public Array createArray(String name) {
        StdArray arrtype = new StdArray();
        this.put(name.toLowerCase(), arrtype);
        return arrtype;
    }

    
    public String[] getFieldNames() {
        return (String[]) this.keySet().toArray(new String[]{});
    }

}
