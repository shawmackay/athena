package org.jini.projects.athena.command.types;

/**
 * Represents the data needed to expand a <code>org.jini.projects.org.jini.projects.athena.command.Array</code> instance, into a
 * valid String.
 * @author calum
 *
 */
public class ArrayType implements java.io.Serializable, Type {
    static final long serialVersionUID = -1153028906496685827L;

    /**
     * Sets the base, or the leading data in SQL. i.e. 'my_array('
     * @param base
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Sets the repeater string. i.e. $object
     * @param repeater
     */
    public void setRepeater(String repeater) {
        this.repeater = repeater;
    }

    /**
     * Sets the separating string used to differentiate from one index to the next. i.e.','
     * @param separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Sets the tail string used to mark the end of the array definition. i.e. ')'
     * @param tail
     */
    public void setTail(String tail) {
        this.tail = tail;
    }

    /**
     * Returns the base string for this array type
     * @return the base string
     */
    public String getBase() {
        return base;
    }

    /**
     * Returns the repeating string for this array type
     * @return the repeater string
     */
    public String getRepeater() {
        return repeater;
    }

    /**
     * Returns the separating string for this array type
     * @return the base string
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Returns the end string for this array type
     * @return the end string for the array
     */
    public String getTail() {
        return tail;
    }

    private String base;
    private String repeater;
    private String separator;
    private String tail;

    public ArrayType() {
    }

    /**
     * Creates an ArrayType, initialising it with the supplied parameters
     * @param base
     * @param repeater
     * @param separator
     * @param tail
     */
    public ArrayType(String base, String repeater, String separator, String tail) {
        this.base = base;
        this.repeater = repeater;
        this.separator = separator;
        this.tail = tail;
    }
}
