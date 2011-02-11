package org.jini.projects.athena.connection;

import javax.transaction.xa.Xid;

public class AthenaXid implements javax.transaction.xa.Xid {

	private int formatID;
	private byte[] globalTxnId;
	private byte[] branchQualifier;

	private transient String _string = null;

	protected final static char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public AthenaXid(int formatID, byte[] globalTxnId, byte[] branchQualifier) {
		this.formatID = formatID;
		if ((globalTxnId == null) || (globalTxnId.length == 0)) {
			this.globalTxnId = new byte[0];
		} else {
			this.globalTxnId = new byte[globalTxnId.length];
			System.arraycopy(globalTxnId, 0, this.globalTxnId, 0, globalTxnId.length);
		}

		if ((branchQualifier == null) || (branchQualifier.length == 0)) {
			this.branchQualifier = new byte[0];
		} else {
			this.branchQualifier = new byte[branchQualifier.length];
			System.arraycopy(branchQualifier, 0, this.branchQualifier, 0, branchQualifier.length);
		}

	}

	public byte[] getBranchQualifier() {
		// TODO Auto-generated method stub
		return branchQualifier;
	}

	public int getFormatId() {
		// TODO Auto-generated method stub
		return formatID;
	}

	public byte[] getGlobalTransactionId() {
		// TODO Auto-generated method stub
		return globalTxnId;
	}

	public String toString() {

		if (_string == null) {
			StringBuffer buffer = new StringBuffer("xid://");
			buffer.append(HEX_DIGITS[(int) ((formatID >> 28) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 24) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 20) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 16) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 12) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 8) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) ((formatID >> 4) & 0x0F)]);
			buffer.append(HEX_DIGITS[(int) (formatID & 0x0F)]);
			buffer.append('-');
			if (globalTxnId != null && globalTxnId.length > 0) {
				for (int i = globalTxnId.length; i-- > 0;) {
					buffer.append(HEX_DIGITS[(globalTxnId[i] & 0xF0) >> 4]);
					buffer.append(HEX_DIGITS[(globalTxnId[i] & 0x0F)]);
				}
			}
			buffer.append('-');
			if (branchQualifier != null && branchQualifier.length > 0) {
				for (int i = branchQualifier.length; i-- > 0;) {
					buffer.append(HEX_DIGITS[(branchQualifier[i] & 0xF0) >> 4]);
					buffer.append(HEX_DIGITS[(branchQualifier[i] & 0x0F)]);
				}
			}
			_string = buffer.toString();
		}

		return _string;
	}

	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (obj instanceof AthenaXid) {

			Xid xid = (Xid) obj;
			// compare format ids
			if (xid.getFormatId() != formatID) {
				return false;
			}

			// compare global transaction id
			byte[] global = xid.getGlobalTransactionId();
			if ((global == null) || (global.length == 0)) {
				if ((globalTxnId != null) || (globalTxnId.length != 0)) {
					return false;
				}
			} else {
				if (global.length != globalTxnId.length) {
					return false;
				}

				for (int index = 0; index < global.length; index++) {
					if (global[index] != globalTxnId[index]) {
						return false;
					}
				}
			}

			// compare branch qualifier
			byte[] branch = xid.getBranchQualifier();
			if ((branch == null) || (branch.length == 0)) {
				if ((branchQualifier != null) || (branchQualifier.length != 0)) {
					return false;
				}
			} else {
				if (branch.length != branchQualifier.length) {
					return false;
				}

				for (int index = 0; index < branch.length; index++) {
					if (branch[index] != branchQualifier[index]) {
						return false;
					}
				}
			}
			return true;
		}

		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

}
