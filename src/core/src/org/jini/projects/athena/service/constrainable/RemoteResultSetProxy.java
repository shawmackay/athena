/*
 * org.jini.projects.athena : org.jini.projects.org.jini.projects.athena.service.constrainable
 *
 *
 * RemoteResultSetProxy.java
 * Created on 08-Mar-2004
 *
 * RemoteResultSetProxy
 *
 */

package org.jini.projects.athena.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

import org.jini.projects.athena.resultset.RemoteResultSet;

/**
 * Jini 2.0 Smart proxy for RemoteResultSet
 * 
 * @author calum
 */
public class RemoteResultSetProxy implements RemoteResultSet, Serializable, ReferentUuid {
	transient Logger l = Logger.getLogger("org.jini.projects.athena.service.proxies");
	RemoteResultSet backend;
	Uuid id;

	RemoteResultSetProxy(RemoteResultSet backend, Uuid proxyID) {
		super();
		this.backend = backend;
		this.id = proxyID;
		// URGENT Complete constructor stub for AthenaRegistrationProxy
	};

	final static class ConstrainableRemoteResultSetProxy extends RemoteResultSetProxy implements RemoteMethodControl {

		public ConstrainableRemoteResultSetProxy(RemoteResultSet server, Uuid id, MethodConstraints methodConstraints) {
			super(constrainServer(server, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new RemoteResultSetProxy.ConstrainableRemoteResultSetProxy(backend, id, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) backend).getConstraints();
		}

	}

	private static RemoteResultSet constrainServer(RemoteResultSet server, MethodConstraints methodConstraints) {
		return (RemoteResultSet) ((RemoteMethodControl) server).setConstraints(methodConstraints);
	}

	public void close() throws RemoteException {
		backend.close();
	}

	public int findColumn(String colname) throws RemoteException {
		return backend.findColumn(colname);
	}

	public boolean first() throws RemoteException {
		return backend.first();
	}

	public int getColumnCount() throws RemoteException {
		return backend.getColumnCount();
	}

	public Integer getConcurrency() throws RemoteException {
		return backend.getConcurrency();
	}

	/**
	 * @return @throws
	 *              RemoteException
	 */
	public Integer getCursorType() throws RemoteException {
		return backend.getCursorType();
	}

	public Object getField(int columnIndex) throws RemoteException {
		return backend.getField(columnIndex);
	}

	public Object getField(String name) throws RemoteException {
		return backend.getField(name);
	}

	public String getFieldName(int field) throws RemoteException {
		return backend.getFieldName(field);
	}


	public long getRowCount() throws RemoteException {
		return backend.getRowCount();
	}

	public boolean last() throws RemoteException {
		return backend.last();
	}

	public Integer moveAbsolute(int pos) throws RemoteException {
		return backend.moveAbsolute(pos);
	}

	public boolean next() throws RemoteException {
		return backend.next();
	}


	public boolean previous() throws RemoteException {
		return backend.previous();
	}


	public void refreshRow() throws RemoteException {
		backend.refreshRow();
	}


	public String toString() {
		return backend.toString();
	}


	public void updateObject(int columnindex, Object obj) throws RemoteException {
		backend.updateObject(columnindex, obj);
	}


	public void updateRow() throws RemoteException {
		backend.updateRow();
	}

	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return id;
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