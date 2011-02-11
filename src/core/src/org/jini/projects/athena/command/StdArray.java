/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 29-Aug-2002
 * Time: 13:41:28
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command;

import java.util.ArrayList;

/**
 * Simple Array implementation based on an underlying ArrayList
 * @author calum
 */

public class StdArray extends ArrayList implements Array {


    

	public StdArray() {
    }

    public Object[] getItems() {
        return this.toArray();

    }

    public Object getItem(int index) {
        return this.get(index);
    }

    public Object remove(int index) {
        return super.remove(index);
    }

    public Object set(int index, Object item) {
        return super.set(index, item);
    }

    public boolean add(Object item) {
        return super.add(item);
    }

    public void insert(int index, Object item) {
        super.add(index, item);
    }


    public void clear() {
        this.clear();
    }

    public int length() {
        return this.size();
    }

    public CompoundType createCompundType() {
        StdCompoundType comptype = new StdCompoundType();
        this.add(comptype);
        return comptype;
    }

    public CompoundType createCompundType(int index) {
        StdCompoundType comptype = new StdCompoundType();
        this.add(index, comptype);
        return comptype;
    }

    public Array createArray() {
        StdArray arrtype = new StdArray();
        this.add(arrtype);
        return arrtype;
    }

    public Array createArray(int index) {
        StdArray arrtype = new StdArray();
        this.add(index, arrtype);
        return arrtype;
    }
}
