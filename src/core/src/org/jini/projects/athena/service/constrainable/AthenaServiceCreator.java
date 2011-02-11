/*
 * neon : neon.service.constrainable
 * 
 * 
 * AthenaServiceCreator.java Created on 07-Jan-2004
 * 
 * AthenaServiceCreator
 *  
 */

package org.jini.projects.athena.service.constrainable;

import java.rmi.Remote;

import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.Uuid;

import org.jini.glyph.chalice.builder.ProxyCreator;
import org.jini.projects.athena.connection.RemoteConnection;
import org.jini.projects.athena.resultset.ChunkLoader;
import org.jini.projects.athena.resultset.RemoteResultSet;
import org.jini.projects.athena.service.AthenaAdminProxy;
import org.jini.projects.athena.service.AthenaRegistration;
import org.jini.projects.thor.service.ChangeEventListener;



/**
 * 
 * The Proxy Creator for Athena. Used by the ExportManager to create the
 * underlying smart proxies.
 * 
 * @author calum
 */
public class AthenaServiceCreator implements ProxyCreator {
	/**
	 *  
	 */
	public AthenaServiceCreator() {
		super();
		// URGENT Complete constructor stub for AthenaServiceCreator
	}

	/*
	 * @see utilities20.export.builder.ProxyCreator#create(java.rmi.Remote,
	 *           net.jini.id.Uuid)
	 */
	public Remote create(Remote in, Uuid ID) {
		if (in instanceof AthenaRegistration) {
			if (in instanceof RemoteMethodControl) {
				return new AthenaRegistrationProxy.ConstrainableAthenaRegistrationProxy((AthenaRegistration) in, ID, null);
			} else
				return new AthenaRegistrationProxy((AthenaRegistration) in, ID);
		}
		if (in instanceof AthenaAdminProxy) {
			if (in instanceof RemoteMethodControl) {
				return new AdminProxy.ConstrainableAdminProxy((AthenaAdminProxy) in, ID, null);
			} else
				return new AdminProxy((AthenaAdminProxy) in, ID);
		}
		if (in instanceof RemoteResultSet) {
			if (in instanceof RemoteMethodControl) {
				return new RemoteResultSetProxy.ConstrainableRemoteResultSetProxy((RemoteResultSet) in, ID, null);
			} else
				return new RemoteResultSetProxy((RemoteResultSet) in, ID);
		}
		if (in instanceof org.jini.projects.thor.service.ChangeEventListener) {            
			if (in instanceof RemoteMethodControl) {
				return new ChangeListenerProxy.ConstrainableChangeListenerProxy((ChangeEventListener) in, ID, null);
			} else
				return new ChangeListenerProxy((ChangeEventListener) in, ID);
		}
		if (in instanceof RemoteConnection) {
			if (in instanceof RemoteMethodControl) {
				return new AthenaConnectionProxy.ConstrainableAthenaConnectionProxy((RemoteConnection) in, ID, null);
			} else
				return new AthenaConnectionProxy((RemoteConnection) in, ID);
		}
		if (in instanceof ChunkLoader) {
			if (in instanceof RemoteMethodControl) {
				return new ChunkLoaderProxy.ConstrainableChunkLoaderProxy((ChunkLoader) in, ID, null);
			} else
				return new ChunkLoaderProxy((ChunkLoader) in, ID);
		}
		return null;
	}
}