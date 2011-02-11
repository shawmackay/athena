/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 25-Jun-02
 * Time: 15:07:36
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command.types;
/**
 * Enables an object to be transformed from one type to another. This could
 * be classbased i.e. Date to String or data-based (one string format to another)
 * Data is placed into the transformer in the set() operation, and transformation should actually be initiated
 * in the get() operation.
 * @author calum
 *
 */
public interface Transformer {
    /**
     * Sets the source Data, ensuring that it is of
     * an allowable type, before continuing.
     * @param obj Instance to transform from
     */

    public void set(Object obj) throws ClassCastException;

    /**
     * Obtain the data after it has been modified and/or validated     
     */
    public Object get() throws org.jini.projects.athena.exception.ValidationException;

    
}
