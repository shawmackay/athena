package org.jini.projects.athena.command.types;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jini.projects.athena.exception.ValidationException;

/**
 * 
 * System representation of a scalar(singular) type, which requires special processing. Either a regex for the
 * data to be validated against or a transformation type for 
 * transforming the data supplied by the user into a different type.
 * @author Calum
 *
 */
public class ScalarType implements java.io.Serializable, Type {
    static final long serialVersionUID = -1437537176137009227L;
    String expression;
    String transformType_Class;
    /**
     * Set up a type. For expressions, the first parameter must be non-null,
     * for transforms, the second and third parameters must not be null.
     * @param expression
     * @param transformType_Class
     * @param allowableTransforms
     */
    public ScalarType(String expression, String transformType_Class, ArrayList allowableTransforms) {
        this.expression = expression;
        this.transformType_Class = transformType_Class;
        this.allowableTransforms = allowableTransforms;
    }

    ArrayList allowableTransforms;

    public ScalarType() {
    }
    /**
     * Set up a type. For expressions, the first parameter must be non-null,
     * for transforms, the second and third parameters must not be null.
     * @param expression
     * @param transformType_Class
     */
    
    public ScalarType(String expression, String transformType_Class) {
        this.expression = expression;
        this.transformType_Class = transformType_Class;
        allowableTransforms = new ArrayList();
    }
    /**
     * return the expression set in the system
     * @return Regeular expression used when validating command parameters
     */
    public String getExpression() {
        return expression;
    }
    /**
     * Set the expression object
     * @param expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Return an object that wil be used to transform the field
     * @return Transformer instance to use to transform a parameter
     */
    public Object getTransformType_Class() {
        try {
            return Class.forName(transformType_Class).newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("Class " + transformType_Class + " could not be found");
        } catch (Exception e) {
            System.out.println("Err: " + e.getStackTrace());
        }
        return null;
    }

    public String getTransformType_Name() {
        return transformType_Class;
    }

    public void setTransformType_Class(String transformType_Class) {
        this.transformType_Class = transformType_Class;
        allowableTransforms = new ArrayList();
    }

    public boolean isExpression() {
        return (expression != null);
    }


    public boolean allowableTransformFrom(Object obj) {
        for (int i = 0; i < allowableTransforms.size(); i++) {
            if (((String) allowableTransforms.get(i)).equals(obj.getClass().getName()))
                return true;

        } 
        return false;
    }

    public Object transform(Object in) throws ValidationException {
        Transformer type = (Transformer) getTransformType_Class();        
        type.set(in);
        return type.get();
    }

    public Object[] getAllowableTransforms() {
        return allowableTransforms.toArray();
    }

    public boolean parse(Object in) throws ValidationException {
        String data = (String) in;
        if (expression != null && data != null) {
            if (Pattern.matches(expression, data)) {                
                return true;
            } else
                throw new org.jini.projects.athena.exception.ValidationException("Data does not match type definition");
        }
        if (expression == null) throw new org.jini.projects.athena.exception.ValidationException("Need a reqular expression for parsing");
        if (data == null) throw new org.jini.projects.athena.exception.ValidationException("Data is required to attempt a match");
        return false;
    }

    public static void main(String[] args) {
        ArrayList types = new ArrayList();
        types.add("java.util.Date");
        ScalarType st = new ScalarType("\\d{6}[A-Z]", "MFrameDate_type", types);
        if (st.allowableTransformFrom(new java.util.Date())) {
            System.out.println("Can be transformed!");
            try {
                System.out.println(st.transform(new java.util.Date()));
                System.out.println("Parsing: " + st.parse("604905P"));
            } catch (ValidationException e) {
                System.out.println("Object could not be validated");
            }
        } else
            System.out.println("Cannot be transformed!");


    }

    public void addAllowableTransform(String classname) {
        allowableTransforms.add(classname);
    }
}
