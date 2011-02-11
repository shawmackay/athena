
package org.jini.projects.athena.service.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jini.projects.athena.command.Array;
import org.jini.projects.athena.command.CompoundType;

/**
 * @author calum
 * 
 * CallistoExamples
 */
public class AthenaCellRenderer extends DefaultTableCellRenderer {
	public AthenaCellRenderer() {
	}

	/**
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable,
	 *           Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp;      
		if (value instanceof String) {
			String displayData = (String) value;
			if (displayData.indexOf('\n') != -1) {
				JTextPane area = new JTextPane();
                System.out.println("Creating textPane");
				if (isSelected) {
					if (hasFocus) {
						area.setBackground(table.getSelectionBackground().brighter());
						area.setForeground(table.getSelectionForeground());
					} else {
						area.setBackground(table.getSelectionBackground());
						area.setForeground(table.getSelectionForeground());
					}
				} else {
					area.setBackground(table.getBackground());
					area.setForeground(table.getForeground());
				}
                System.out.println("Set backgrounds");
				area.setText(displayData);
				int initialWidth = 0;
				int tHeight = area.getFontMetrics(area.getFont()).getHeight();
				int numlines = 1;
				int cpos = 0;
                System.out.println("Seeting displayData");
				String subString = displayData.substring(cpos, displayData.indexOf('\n'));
				while (displayData.indexOf("\n", cpos) != -1) {
					if (displayData.indexOf("\n", cpos) != -1 && cpos != -1)
						subString = displayData.substring(cpos, displayData.indexOf('\n', cpos));
					cpos = displayData.indexOf("\n", cpos) + 1;
					Rectangle2D rect = area.getFontMetrics(area.getFont()).getStringBounds(subString, this.getGraphics());
					if (rect.getWidth() > initialWidth)
						initialWidth = (int) rect.getWidth();
					numlines++;
				}
                System.out.println("Modifying width");
				//				System.out.println("Long Text Width=" + initialWidth);
				area.setSize(initialWidth + 32, tHeight * numlines + 8);
				TableColumnModel mod = table.getColumnModel();
				TableColumn column2 = mod.getColumn(column);
				if (column2.getWidth() < (initialWidth + 32)) {
					column2.setWidth(initialWidth + 32);
					column2.setMaxWidth(initialWidth + 32);
					column2.setPreferredWidth(initialWidth + 32);
				}
				//area.setWrapStyleWord(true);
                System.out.println("Modifying height");
				table.setRowHeight(row, area.getHeight());
				area.setOpaque(true);
				comp = area;
			} else {
                if(value!=null)
                	comp = buildComponent(displayData, isSelected, table, hasFocus);
                else
                    comp = new JLabel("");
			}
		} else
            if(value!=null)
            	comp = buildComponent(value, isSelected, table, hasFocus);
            else
                comp = new JLabel("");
		Dimension d = comp.getSize();
		if (d.getHeight() >= 1 && table.getRowHeight(row) < d.getHeight())
			table.setRowHeight(row, (int) d.getHeight());
		int initialWidth = 0;
		if (d.getWidth() > initialWidth)
			initialWidth = (int) d.getWidth();
		TableColumnModel mod = table.getColumnModel();
		TableColumn column2 = mod.getColumn(column);
		if (column2.getWidth() < (initialWidth + 32)) {
			column2.setWidth(initialWidth + 32);
			column2.setMaxWidth(initialWidth + 32);
			column2.setPreferredWidth(initialWidth + 32);
		}
		return comp;
	}

	private Component buildComponent(Object value, boolean selected, JTable table, boolean hasFocus) {
        
		if (value instanceof CompoundType) {
			
			JPanel list = new JPanel();
			list.setBorder(new MatteBorder(new Insets(2, 2, 2, 2), Color.BLUE));
			list.setLayout(new GridLayout(0, 1));
			CompoundType type = (CompoundType) value;
			String[] names = type.getFieldNames();
			int initialWidth = 0;
			for (int i = 0; i < names.length; i++) {
				Component comp = buildComponent(names[i] + ": " + type.getField(names[i]), selected, table, hasFocus);
				list.add(comp);
				list.setMinimumSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				list.setPreferredSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				list.setSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				comp.validate();
				//Rectangle2D rect =
				// this.getFontMetrics(this.getFont()).getStringBounds(arr.getItem(i).toString(),
				// this.getGraphics());
				if (comp.getWidth() > initialWidth) {
					initialWidth = comp.getWidth();
					//					list.setSize(initialWidth + 16, list.getHeight());
				}
			}
			list.setSize(new Dimension(initialWidth + 16, list.getHeight()));
			return list;
		}
		if (value instanceof Array) {
			
			//DefaultListModel dlm = new DefaultListModel();
			JPanel list = new JPanel();
			list.setBorder(new MatteBorder(new Insets(2, 2, 2, 2), Color.RED));
			list.setLayout(new GridLayout(0, 1));
			Array arr = (Array) value;
			int initialWidth = 0;
			for (int i = 0; i < arr.length(); i++) {
				Component comp = buildComponent(arr.getItem(i), selected, table, hasFocus);
				list.add(comp);
				list.setMinimumSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				list.setPreferredSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				list.setSize(new Dimension(list.getWidth(), list.getHeight() + comp.getHeight() + 12));
				if (comp.getWidth() > initialWidth) {
					initialWidth = comp.getWidth();
				}
			}
			list.setSize(new Dimension(initialWidth + 16, list.getHeight()));
			return list;
		}
            
		JLabel lab = new JLabel(value.toString());
		lab.setBorder(new EtchedBorder());
		Rectangle2D rect = this.getFontMetrics(this.getFont()).getStringBounds(value.toString(), this.getGraphics());
		int initialWidth = (int) rect.getWidth();
		lab.setSize(initialWidth + 32, 28);
		lab.setOpaque(true);
		if (selected) {
			if (hasFocus) {
				lab.setBackground(table.getSelectionBackground().brighter());
				lab.setForeground(table.getSelectionForeground());
			} else {
				lab.setBackground(table.getSelectionBackground());
				lab.setForeground(table.getSelectionForeground());
			}
		} else {
			lab.setBackground(table.getBackground());
			lab.setForeground(table.getForeground());
		}
		return lab;
	}
}