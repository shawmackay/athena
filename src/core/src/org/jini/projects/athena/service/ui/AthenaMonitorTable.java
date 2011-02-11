/*
 * AthenaMonitorTable.java
 *
 * Created on 15 January 2002, 10:33
 */

package org.jini.projects.athena.service.ui;

/**
 *
 * ~~author  calum
 * @version 0.9community */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class AthenaMonitorTable extends JPanel implements org.jini.projects.athena.monitors.Monitor {
    String[] titles;
    JTable table;
    AthenaMonitorTableModel tabModel;
    JScrollPane scroller;

    /** Creates new AthenaMonitorTable */
    public AthenaMonitorTable() {
        this.setSize(200, 200);
        tabModel = new AthenaMonitorTableModel();

        Vector tdata = buildranddata();
        tabModel.setData(tdata);
        titles = tabModel.getTitles();
        Vector vTitles = new Vector();
        for (int i = 0; i < titles.length; i++)
            vTitles.addElement(titles[i]);


        table = new JTable(tdata, vTitles);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setModel(tabModel);
        table.setAutoCreateColumnsFromModel(true);
        scroller = new JScrollPane(table);
        scroller.setPreferredSize(new Dimension(400, 100));
        scroller.setMaximumSize(new Dimension(400, 100));
        JTableHeader tableHeader = table.getTableHeader();

        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setLayout(new BorderLayout());
        this.add(tableHeader, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
    }

    public AthenaMonitorTable(String[] titles) {
        this.setSize(200, 200);
        tabModel = new AthenaMonitorTableModel();

        Vector tdata = buildranddata();
        tabModel.setData(tdata);
        tabModel.setTitles(titles);
        Vector vTitles = new Vector();
        for (int i = 0; i < titles.length; i++)
            vTitles.addElement(titles[i]);

        table = new JTable(tabModel);
        //table=new JTable(tdata,vTitles);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.setModel(tabModel);
        table.setAutoCreateColumnsFromModel(true);
        scroller = new JScrollPane(table);
        scroller.setPreferredSize(new Dimension(400, 100));
        scroller.setMaximumSize(new Dimension(400, 100));
        JTableHeader tableHeader = table.getTableHeader();

        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setLayout(new BorderLayout());
        this.add(tableHeader, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(600, 600);
        AthenaMonitorTable table = new AthenaMonitorTable();
        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.show();
    }


    protected Vector buildranddata() {
        Vector details;
        Random rnd = new Random();
        details = new Vector();

        for (int i = 0; i < 24; i++) {

            HashMap record = new HashMap();
            record.put("conn", new Long(rnd.nextInt(10)));
            record.put("alloc", new Long(rnd.nextInt(10)));
            record.put("txn", new Long(rnd.nextInt(10)));
            record.put("fail", new Long(rnd.nextInt(10)));
            record.put("ops", new Long(rnd.nextInt(10)));
            record.put("rollbacks", new Long(rnd.nextInt(10)));
            record.put("commits", new Long(rnd.nextInt(10)));
            details.add(record);
        }

        return details;
    }


    public void setStatistics(Object stats) {
        if (tabModel != null) {
            tabModel.setData((Vector) stats);
            table.createDefaultColumnsFromModel();
        }
    }
}

class AthenaMonitorTableModel extends javax.swing.table.AbstractTableModel {
    Vector data;
    String[] titles;
    boolean overrideTitleSearch = false;

    public AthenaMonitorTableModel() {


    }

    public void setTitles(String[] columnTitles) {
        titles = new String[columnTitles.length + 1];

        titles[0] = "Index";
        for (int i = 0; i < columnTitles.length; i++) {
            this.titles[i + 1] = columnTitles[i];
            overrideTitleSearch = true;
        }
    }

    public void setData(Vector data) {
        this.data = data;
        if (!overrideTitleSearch) {
            //Attempt to titles from the Mappings
            HashMap firstrec = (HashMap) data.get(0);
            Set keys = firstrec.keySet();
            Iterator iter = keys.iterator();
            titles = new String[keys.size() + 1];
            int i = 1;
            titles[0] = "Index";
            while (iter.hasNext()) {
                titles[i] = (String) iter.next();
                i++;
            }
        }
    }

    public String[] getTitles() {
        return titles;
    }

    public AthenaMonitorTableModel(Vector data) {
        //Obtain the first HashMap and get the titles
        setData(data);
    }

    public String getColumnName(int columnIndex) {

        return titles[columnIndex];
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return String.valueOf(row + 1);
        }
        HashMap record = (HashMap) data.get(data.size() - 1 - row);
        Object x = record.get(titles[column]);
        String val;
        if (x != null)
            val = x.toString();
        else
            val = "0";
        return val;
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return titles.length;
    }

}

