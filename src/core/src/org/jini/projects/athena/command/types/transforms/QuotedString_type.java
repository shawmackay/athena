/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 25-Jun-02
 * Time: 11:48:46
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command.types.transforms;


import org.jini.projects.athena.command.types.Transformer;


/**
 * Translation type for handling single quotes when building a SQL string
 * i.e. 'O'Connor'. Tested on both Oracle and HSQLDB
 */
public class QuotedString_type implements Transformer{
    String inString;

    
    public QuotedString_type(String inString) {
        this.inString = inString;
    }

    public QuotedString_type() {
    }

    /**
     * Sets the source string
     * @throws java.lang.ClassCastException in the event of wrong data types being passed
     */
    public void set(Object obj) throws ClassCastException {
        inString = (String) obj;
    }

    /**
     * Translates the source string, escaping all single quotes so it won't effect the SQL
     *      */
    public Object get() throws org.jini.projects.athena.exception.ValidationException {
        

        StringBuffer newS = new StringBuffer();
        newS.append(inString);
        String replaced = inString.replaceAll("\\'", "''");
        return replaced;
    }


    
}
