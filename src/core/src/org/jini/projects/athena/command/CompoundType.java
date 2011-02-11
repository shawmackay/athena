/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 29-Aug-2002
 * Time: 13:26:41
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command;

/**
 * Represents a compound data type, such as a DB Object or SQL Structs
 *
 * @author calum
 *
 */
public interface CompoundType {
    /**
     * Sets the field <i>name</i> to the given value
     * @param name Name of field
     * @param value value to set
     */
    public void setField(String name, Object value);

    /**
     * Return the list of all field names set in this object
     * @return field name list
     */

    public String[] getFieldNames();

    /**
     * Returns the value of the field named <i>name</i>
     * @param name
     * @return current value of field with given name, or null 
     */

    public Object getField(String name);

    /**
     * Returns the current number of defined fields in this CompoundType
     * @return count of defined fields
     */
    public int getNumFields();

    /**
   * Returns whether there is a value assigned to the given name
     * @param fieldName field to Check
     * @return indicator of assignment.
     */
    public boolean isAssigned(String fieldName);

    /**
     * Unassigns all fields
     */
    public void clearFields();

    /**
     * Creates a compund type bound to the given field name, i.e. nested types
     *
     * @param name
     * @return a newly defined, bound Compound Type
     */
    public CompoundType createCompundType(String name);

    /**
     *  Creates an Array type bound to the given field name
     * @param name
     * @return a newly defined, bound Compound Type
     */
    public Array createArray(String name);
}
