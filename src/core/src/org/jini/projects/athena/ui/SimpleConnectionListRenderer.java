/*
 *  SimpleConnectionListRenderer.java
 *
 *  Created on 09 August 2001, 10:44
 */
package org.jini.projects.athena.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 *  @author calum
 *
 *@author     calum
 *     09 October 2001
 *@version 0.9community */
public class SimpleConnectionListRenderer extends JLabel implements javax.swing.ListCellRenderer {
    int idx = 0;
    boolean connected = false;
    boolean allocated = false;
    boolean inTxn = false;
    String username = "none";

    /**
     *  Creates new SimpleConnectionListRenderer
     *
     *@since
     */
    public SimpleConnectionListRenderer() {
        setOpaque(true);
        setMinimumSize(new Dimension(250, 20));
        setPreferredSize(new Dimension(250, 20));
        setSize(new Dimension(250, 20));
    }


    /**
     *  Gets the ListCellRendererComponent attribute of the
     *  SimpleConnectionListRenderer object
     *
     *@param  jlist         Description of Parameter
     *@param  value         Description of Parameter
     *@param  index         Description of Parameter
     *@param  isSelcted     Description of Parameter
     *@param  cellHasFocus  Description of Parameter
     *@return               The ListCellRendererComponent value
     *@since
     */
    public Component getListCellRendererComponent(javax.swing.JList jlist, Object value, int index, boolean isSelcted, boolean cellHasFocus) {

        ConnectionStatus cstat = (ConnectionStatus) value;
        this.idx = cstat.index;
        this.connected = cstat.connected;
        this.allocated = cstat.allocated;
        this.inTxn = cstat.inTxn;
        this.username = cstat.Username;
        doPanel();
        return this;
    }

    public void doPanel() {
        this.setFont(new Font("Dialog", 1, 10));
        //setText("hello");
        this.removeAll();
        this.setLayout(new BorderLayout());
        add(new ConnLabel(this), BorderLayout.CENTER);

    }

    public void paint(Graphics g) {

        g.setColor(Color.gray);
        g.setFont(new Font("Dialog", java.awt.Font.PLAIN, 10));
        g.fillRect(0, 0, getWidth(), getHeight());


        String draw = null;
        if (this.username != null)
            draw = this.username + " (" + this.idx + ")";
        else
            draw = "Connection:  " + this.idx;
        g.setColor(Color.black);
        g.drawString(draw, 98, 18);
        g.setColor(Color.white);
        g.drawString(draw, 96, 16);
        int strwid = 0;
        int pos = strwid + 4;

        if (this.connected == true) {
            drawBevlRect(g, pos, 4, 16, 12, new Color(102, 102, 255));
        } else {
            drawBevlRect(g, pos, 4, 16, 12, new Color(0, 0, 102));
        }
        pos = pos + 24;

        if (this.allocated == true) {
            drawBevlRect(g, pos, 4, 16, 12, new Color(255, 64, 64));
        } else {
            drawBevlRect(g, pos, 4, 16, 12, new Color(102, 0, 0));
        }

        pos = pos + 24;

        if (this.inTxn == true) {
            drawBevlRect(g, pos, 4, 16, 12, new Color(64, 255, 64));
        } else {
            drawBevlRect(g, pos, 4, 16, 12, new Color(0, 102, 0));
        }
    }


    /**
     *  Description of the Method
     *
     *@param  g       Description of Parameter
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  width   Description of Parameter
     *@param  height  Description of Parameter
     *@param  color   Description of Parameter
     *@since
     */
    public void drawBevlRect(Graphics g, int x, int y, int width, int height, Color color) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.white);
        g.drawLine(x + 1, y + 2, x + 1, y + 1);
        g.drawLine(x + 1, y + 1, x + 2, y + 1);
        g.setColor(color.brighter());
        g.drawLine(x, y + height, x, y);
        g.drawLine(x, y, x + width, y);
        g.setColor(Color.black);
        g.drawLine(x, y + height, x + width, y + height);
        g.drawLine(x + width, y + height, x + width, y);
    }

    class InfoPanel extends JPanel {

        private SimpleConnectionListRenderer renderer;

        public InfoPanel() {

        }

        public InfoPanel(SimpleConnectionListRenderer renderer) {
            this.renderer = renderer;
        }


    }
}

