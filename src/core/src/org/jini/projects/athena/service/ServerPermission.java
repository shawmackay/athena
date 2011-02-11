/*
 * thor.jini.org : org.jini.projects.thor.service
 * 
 * 
 * ServerPermission.java
 * Created on 07-Jun-2004
 * 
 * ServerPermission
 *
 */
package org.jini.projects.athena.service;

import net.jini.security.AccessPermission;

/**
 * @author calum
 */
public class ServerPermission extends AccessPermission{
	public ServerPermission(String name){
		super(name);
	}
	
}
