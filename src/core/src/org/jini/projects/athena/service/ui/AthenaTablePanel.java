/*
 * athena.jini.org : org.jini.projects.athena.service.ui
 * 
 * 
 * AthenaTablePanel.java
 * Created on 04-May-2004
 * 
 * AthenaTablePanel
 *
 */
package org.jini.projects.athena.service.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.exception.AthenaException;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.resultset.AthenaResultSetImpl;
import org.jini.projects.athena.service.AthenaRegistration;


/**
 * @author calum
 */
public class AthenaTablePanel extends JPanel{
    
    public AthenaTablePanel(AthenaRegistration reg){
    	currentReg = reg;
          jTable1= new JTable();
        init();
    }
    
    public AthenaTablePanel(){
          jTable1= new JTable();
    	init();
        ArrayList arr = new ArrayList();
        ArrayList data = new ArrayList();
        data.add("b");
        data.add("d");
        arr.add(data);
        HashMap header = new HashMap();
        header.put("a", new Integer(0));
        header.put("c", new Integer(1));
        AthenaResultSetImpl testrs = new AthenaResultSetImpl(arr, header);
        jTable1.setModel(new AthenaTableModel(testrs));
    }
    
    
    private JPanel jPanel1 = new JPanel();
    private JTextField jTextField1 = new JTextField();
    private JTable jTable1;
    private JButton jButton1;
    private AthenaRegistration currentReg;
    public void init(){
        setLayout(new BorderLayout());
        jPanel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,6,0);
        jPanel1.add(new JLabel("Query:"), gbc);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,6,6,6);
        gbc.weightx =1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jPanel1.add(jTextField1,gbc);
      
        jTable1.setDefaultRenderer(String.class, null);
        jTable1.setDefaultRenderer(String.class, new AthenaCellRenderer());
        jTable1.setDefaultRenderer(Object.class, new AthenaCellRenderer());
        jButton1 = new JButton();
        jButton1.setMnemonic('e');
        jButton1.setText("Execute");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,0,6,12);
        jPanel1.add(jButton1, gbc);
        JScrollPane tableScroll = new JScrollPane();
        tableScroll.setViewportView(jTable1);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //        jPanel2.add(jScrollPane2);
        add(tableScroll, BorderLayout.CENTER);
        add(jPanel1, BorderLayout.NORTH);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });   
    }
    
	public static void main(String[] args) {
        JFrame jfr = new JFrame("AthenaTableTest");
        jfr.getContentPane().add(new AthenaTablePanel());
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfr.setSize(800,600);
        jfr.show();
	}
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        // Add your handling code here:
        AthenaConnection conn = null;
        AthenaResultSet rs = null;
        try {
            if (currentReg != null) {

                conn = currentReg.getConnection("ServiceUI", 20000);
                conn.setConnectionType(AthenaConnection.LOCAL);
                rs = conn.executeQuery(jTextField1.getText());
                jTable1.setModel(new AthenaTableModel(rs));
                /*              while(rs.next()) {
                                    System.out.println("Data: " + rs.getField(0));
                                }*/

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (AthenaException e) {
                    System.out.println("Could not close resultset");
                }
            if (conn != null) {
                try {
                    conn.canRelease(true);
                    conn.release();
                } catch (Exception e) {
                    System.out.println("Could not close Connection");
                }
            }
        }
    }
    
    
}
