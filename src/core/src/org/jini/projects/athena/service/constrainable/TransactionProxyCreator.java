/*
 * athena.jini.org : org.jini.projects.athena.service.constrainable
 * 
 * 
 * TransactionProxyCreator.java Created on 06-Apr-2004
 * 
 * TransactionProxyCreator
 *  
 */

package org.jini.projects.athena.service.constrainable;

import java.rmi.Remote;

import org.jini.glyph.chalice.builder.ProxyCreator;

import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.id.Uuid;


/**
 * @author calum
 */
public class TransactionProxyCreator implements ProxyCreator {
	/*
	 * @see utilities20.export.builder.ProxyCreator#create(java.rmi.Remote,
	 *           net.jini.id.Uuid)
	 */
	public Remote create(Remote in, Uuid ID) {
		// TODO Complete method stub for create
		if (in instanceof TransactionParticipant) {
			if (in instanceof RemoteMethodControl) {
				return new AthenaTransactionParticipantProxy.ConstrainableAthenaTransactionParticipantProxy((TransactionParticipant) in, ID, null);
			} else
				return new AthenaTransactionParticipantProxy((TransactionParticipant) in, ID);
		}
		return null;
	}
}