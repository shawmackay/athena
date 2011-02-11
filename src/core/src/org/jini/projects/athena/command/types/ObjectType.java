/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 27-Aug-2002
 * Time: 11:35:58
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *System representation of an object with fields, used in validation.etc
 *
 * @author calum
 *
 */
public class ObjectType implements java.io.Serializable, Type {
    static final long serialVersionUID = -2988910753623909168L;
    private String name;
    private HashMap fields;
    private HashMap rules;

    public ObjectType() {
        fields = new HashMap();
        rules = new HashMap();
    }

    public ObjectType(String name, HashMap fields, HashMap rules) {
        this.name = name;
        this.fields = fields;
        this.rules = rules;
    }

    /**
     * returns the named value
     * @param name
     * @return value held in the given field, or null
     */
    public Object getField(String name) {
        return fields.get(name);
    }

    /**
     * Adds a field with a named type
     * @param name
     * @param type
     */
    public void addField(String name, String type) {
        fields.put(name, type);
    }
    /**
     * Gets all the fields that comprise this Object
     * @return All fields within this type
     */
    public Map getFields() {
        return fields;
    }
    
    /**
     * Gets all the validation rules for the fields
     * @return Map of parameters and rules
     */
    public Map getRules() {
        return this.rules;
    }

    /**
     * Adds a validation rule into the system
     * @param fieldName
     * @param Rule
     */
    public void addRule(String fieldName, int Rule) {
        rules.put(fieldName, new Integer(Rule));
    }

    public Map getTypes() {
        HashMap ret = new HashMap();
        Iterator iter = fields.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry) iter.next();
            if (!(ent.getValue().equals("string")))
                ret.put(ent.getKey(), ent.getValue());
        }
        return ret;
    }
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
}
