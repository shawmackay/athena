/*
 * athena.jini.org : org.jini.projects.athena.service.constrainable
 * 
 * 
 * AthenaTransactionParticipantProxy.java
 * Created on 06-Apr-2004
 * 
 * AthenaTransactionParticipantProxy
 *
 */
package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

/**
 * @author calum
 */
public class AthenaTransactionParticipantProxy implements TransactionParticipant, Serializable, ReferentUuid {

	final static class ConstrainableAthenaTransactionParticipantProxy extends AthenaTransactionParticipantProxy implements RemoteMethodControl {

		public ConstrainableAthenaTransactionParticipantProxy(TransactionParticipant participant, Uuid id, MethodConstraints methodConstraints) {
			super(constrainServer(participant, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new AthenaTransactionParticipantProxy.ConstrainableAthenaTransactionParticipantProxy(backend, proxyID, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) backend).getConstraints();
		}

	}

	private static final long serialVersionUID = 267682616263L;
	transient Logger l = Logger.getLogger("org.jini.projects.athena.service.proxies");

	final TransactionParticipant backend;
	final Uuid proxyID;

	/**
	 * 
	 */
	public AthenaTransactionParticipantProxy(TransactionParticipant backend, Uuid proxyID) {
		super();
		this.backend = backend;
		this.proxyID = proxyID;
		// URGENT Complete constructor stub for AthenaRegistrationProxy
	}

	private static TransactionParticipant constrainServer(TransactionParticipant server, MethodConstraints methodConstraints) {
		return (TransactionParticipant) ((RemoteMethodControl) server).setConstraints(methodConstraints);
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public void abort(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		try {

			System.out.println("Athena Participant: Aborting");
			this.backend.abort(mgr, id);

		} catch (UnknownTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}catch (RuntimeException e){
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param mgr
	 * @param id
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public void commit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		try {
			System.out.println("Athena Participant: Committing");
			this.backend.commit(mgr, id);

		} catch (UnknownTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}catch (RuntimeException e){
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @return either PREPARE or ABORT
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public int prepare(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		try {
			System.out.println("Athena Participant: Prepare");
			return this.backend.prepare(mgr, id);
		} catch (UnknownTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}catch (RuntimeException e){
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @return either COMMIT or ABORT
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public int prepareAndCommit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
		try {
			System.out.println("Athena Participant: Prepare/Commit");
			return this.backend.prepareAndCommit(mgr, id);
		} catch (UnknownTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e){
			e.printStackTrace();
			throw e;
		}
		
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Athena Transaction Participant";
	}

	/*
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return proxyID;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return ReferentUuids.compare(this, obj);
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return backend.hashCode();
	}
}
