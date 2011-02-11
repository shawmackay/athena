/*
 * athena.jini.org : org.jini.projects.athena.command.types.transforms
 * 
 * 
 * ReverseTransform.java
 * Created on 03-Nov-2004
 * 
 * ReverseTransform
 *
 */
package org.jini.projects.athena.command.types.transforms;

import org.jini.projects.athena.command.types.Transformer;
import org.jini.projects.athena.exception.ValidationException;

/**
 * @author calum
 */
public class ReverseTransform implements Transformer{
	String data;
	public Object get() throws ValidationException {
		// TODO Complete method stub for get
		char[] rev = new char[data.length()];
		for (int i=0;i<rev.length;i++)
			rev[data.length()-i]=data.charAt(i);
		return new String(rev);
	}
	public void set(Object obj) throws ClassCastException {
		// TODO Complete method stub for set
		data = (String) obj;
	}
}
