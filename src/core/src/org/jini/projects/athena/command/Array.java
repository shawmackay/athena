/*
 * User: calum
 * Date: 29-Aug-2002
 * Time: 13:29:55
 */

package org.jini.projects.athena.command;

/**
 * Represents an array type, similar to a SQL STRUCT definition.
 * 
 * @author calum
 *  
 */

public interface Array {
	/**
	 * Obtains an object array containing all the items in the object
	 * 
	 * @return a standard java Object array
	 */
	public Object[] getItems();

	/**
	 * Returns the element at the given position
	 * 
	 * @param index
	 *                  the position of the item
	 * @return the item at the given position
	 */
	public Object getItem(int index);

	/**
	 * Removes the element at the given position
	 * 
	 * @param index
	 *                  the position of the item
	 */

	public Object remove(int index);

	/**
	 * Inserts the data at the given position
	 * 
	 * @param index
	 *                  the position of the item
	 * @return the item at the given position
	 */

	public void insert(int index, Object item);

	 /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of element to replace.
     * @param item element to be stored at the specified position.
     * @return the element previously at the specified position.
     */
	public Object set(int index, Object item);

	/**
	 * Adds an item to the end of the array
	 * 
	 * @param item
	 * @return success of insertion
	 */
	public boolean add(Object item);

	/**
	 * Clears the contents of the Array
	 */
	public void clear();

	/**
	 * Gets the length of the array
	 * 
	 * @return the number of elements in the Array
	 */
	public int length();

	/**
	 * Returns a system-specific instance of a compound type, but does <i>
	 * <b>not </b </i> add it into the array.
	 * 
	 * @return a newly defined CompoundType object
	 */
	public CompoundType createCompundType();

	/**
	 * Returns a system-specific instance of a compound type, <i><b>and </b
	 * </i> adds it to the end of the array.
	 * 
	 * @return a newly defined compoundType after inserting it at the given index
	 */

	public CompoundType createCompundType(int index);

	/**
	 * Returns a system-specific instance of an Athena Array type, but does <i>
	 * <b>not </b </i> add it into the array.
	 * 
	 * @return a newly defined Array Definition
	 */

	public Array createArray();

	/**
	 * Returns a system-specific instance of a Athena Array type, <i><b>and </b
	 * </i> adds it to the end of the array.
	 * 
	 * @return a newly defined Array Definition after inserting the array reference at the
	 * given index
	 */
	public Array createArray(int index);
}