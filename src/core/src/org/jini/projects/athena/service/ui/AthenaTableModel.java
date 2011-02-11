package org.jini.projects.athena.service.ui;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jini.projects.athena.resultset.AthenaResultSet;



/**
 * @author calum
 *
 * CallistoExamples
 */
public class AthenaTableModel implements TableModel {
	AthenaResultSet rs;
	ArrayList modelListeners = new ArrayList();
	public AthenaTableModel(AthenaResultSet rs) {
		try {
			this.rs = rs;

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was a problem creating the table view", "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @see javax.swing.table.TableModel#addTableModelListener(TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l) {
		modelListeners.add(l);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		return Object.class;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		try {
			return rs.getColumnCount() + 1;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		try {
			if (columnIndex == 0)
				return "ROW";
			else
				return rs.getFieldName(columnIndex - 1);
		} catch (Exception e) {
			return "UNKNOWN";
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		try {
			return (int) rs.getRowCount();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if (columnIndex == 0)
				return new Integer(rowIndex);
			else {
				rs.moveAbsolute(rowIndex);
				
				return rs.getField(columnIndex - 1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 * @see javax.swing.table.TableModel#removeTableModelListener(TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l) {
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

}