/*
 *  ServiceTableModel.java
 *
 *  Created on 06 August 2001, 15:19
 */
package org.jini.projects.athena.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *  @author calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class ServiceTableRenderer extends javax.swing.table.DefaultTableCellRenderer {
    private static ImageIcon connon = null;
    private static ImageIcon connoff = null;
    private static ImageIcon allocon = null;
    private static ImageIcon allocoff = null;
    private static ImageIcon txnon = null;
    private static ImageIcon txnoff = null;
    private final int CONNECTION = 3;
    private final int CONNECTED = 0;
    private final int ALLOCATED = 1;
    private final int INTXN = 2;


    /**
     *  Constructor for the ServiceTableRenderer object
     *
     *@since
     */
    public ServiceTableRenderer() {
        if (connon == null) {
            System.out.println("Getting icons");
            connon = new ImageIcon(getClass().getResource("images/Develsm.gif"));
            connoff = new ImageIcon(getClass().getResource("images/Devoffsm.gif"));
            allocon = new ImageIcon(getClass().getResource("images/Useraccsm.gif"));
            allocoff = new ImageIcon(getClass().getResource("images/Useroffsm.gif"));
            txnon = new ImageIcon(getClass().getResource("images/Productsm.gif"));
            txnoff = new ImageIcon(getClass().getResource("images/Prodoffsm.gif"));
        }
        setFont(new Font("Dialog",0,10));
       
        
    }


    /**
     *  Gets the TableCellRendererComponent attribute of the ServiceTableRenderer
     *  object
     *
     *@param  table       Description of Parameter
     *@param  value       Description of Parameter
     *@param  isSelected  Description of Parameter
     *@param  hasFocus    Description of Parameter
     *@param  row         Description of Parameter
     *@param  column      Description of Parameter
     *@return             The TableCellRendererComponent value
     *@since
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setIcon(null);
        this.setText((String) value);
        setOpaque(true);
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        
        if (column == CONNECTED) {
            if (((String) value).equals("C")) {
                this.setIcon(ServiceTableRenderer.connon);
            } else {
                this.setIcon(ServiceTableRenderer.connoff);
            }
            this.setText("");
        }
        if (column == ALLOCATED) {
            if (((String) value).equals("A")) {
                this.setIcon(ServiceTableRenderer.allocon);
            } else {
                this.setIcon(ServiceTableRenderer.allocoff);
            }
            this.setText("");
        }
        if (column == INTXN) {
            if (((String) value).equals("T")) {
                this.setIcon(ServiceTableRenderer.txnon);
            } else {
                this.setIcon(ServiceTableRenderer.txnoff);
            }
            this.setText("");
        }
        return this;
    }
}

