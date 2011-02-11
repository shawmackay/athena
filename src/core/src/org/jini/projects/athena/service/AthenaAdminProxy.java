/*
 *  AthenaAdminProxy.java
 *
 *  Created on 05 October 2001, 11:51
 */
package org.jini.projects.athena.service;

import net.jini.admin.JoinAdmin;

import com.sun.jini.admin.DestroyAdmin;

/**
 * Combiner interface
 * @author calum
 */
public interface AthenaAdminProxy extends DestroyAdmin, JoinAdmin, AthenaAdmin {


}

