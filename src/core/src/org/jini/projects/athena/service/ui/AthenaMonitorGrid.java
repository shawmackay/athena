/*
 *  AthenaMonitorGrid.java
 *
 *  Created on 11 October 2001, 14:09
 */
package org.jini.projects.athena.service.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jini.projects.athena.monitors.HashedStatisticRow;
import org.jini.projects.athena.monitors.StatisticRow;

/**
 *  @author calum
 *
 *@author     calum
 *     05 March 2002
 *@version 0.9community */
public class AthenaMonitorGrid extends JComponent implements org.jini.projects.athena.monitors.Monitor {


    /**
     *  Description of the Class
     *
     *@author     calum
     *     05 March 2002
     */
    public class randthread extends Thread {
        /**
         *
         *  processing method for the randthread object
         *
         *            */
        public void run() {
            for (; ;) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
                if (randOn) {
                }
                setStatistics(buildranddata());
                repaint();
            }
        }
    }


    /**
     *  The main program for the AthenaMonitorGrid class
     *
     *@param  args  The command line arguments
     *        */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(600, 600);
        AthenaMonitorGrid grid = new AthenaMonitorGrid();
        grid.setGap(3);
        grid.setTitles(new String[]{"conn", "alloc", "txn", "fail", "ops", "rollbacks", "commits"});
        grid.setTotals(true);
        grid.setRandomOn();
        //grid.setSeamless(true);
        frame.getContentPane().add(grid, BorderLayout.CENTER);
        frame.show();
    }

    Color[] colmap = {new Color(0, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0), new Color(255, 255, 0), new Color(0, 255, 255), new Color(128, 0, 255), new Color(220, 220, 220)};
    StatisticRow[] hashes = {};
    Image image;
    boolean randOn = false;

    private int rowGap = 0;
    boolean seamless = false;

    Vector stats = null;
    int stepsize = 10;
    randthread thr = new randthread();
    String[] titles;
    boolean totals = false;
    float transparency = 1.0f;


    /**
     *  Creates new AthenaMonitorGrid
     *
     *        */

    public AthenaMonitorGrid() {
        this.setMinimumSize(new Dimension(200, 200));

    }


    protected Vector buildranddata() {
        Vector details;
        Random rnd = new Random();
        if (stats == null) {
            details = new Vector();
            for (int i = 0; i < 24; i++) {
                HashedStatisticRow record = new HashedStatisticRow();
                record.put("conn", new Integer(rnd.nextInt(10)));
                record.put("alloc", new Integer(rnd.nextInt(10)));
                record.put("txn", new Integer(rnd.nextInt(10)));
                record.put("fail", new Integer(rnd.nextInt(10)));
                record.put("ops", new Integer(rnd.nextInt(10)));
                record.put("rollbacks", new Integer(rnd.nextInt(10)));
                record.put("commits", new Integer(rnd.nextInt(10)));
                details.add(record);
            }
        } else {
            details = stats;
            details.remove(0);
            HashedStatisticRow record = new HashedStatisticRow();
            record.put("conn", new Integer(rnd.nextInt(10)));
            record.put("alloc", new Integer(rnd.nextInt(10)));
            record.put("txn", new Integer(rnd.nextInt(10)));
            record.put("fail", new Integer(rnd.nextInt(10)));
            record.put("ops", new Integer(rnd.nextInt(10)));
            record.put("rollbacks", new Integer(rnd.nextInt(10)));
            record.put("commits", new Integer(rnd.nextInt(10)));
            details.add(record);
        }
        return details;
    }


    /**
     *  Description of the Method
     *
     *@param  g             Description of Parameter
     *@param  xpos          Description of Parameter
     *@param  ypos          Description of Parameter
     *@param  xinc          Description of Parameter
     *@param  yinc          Description of Parameter
     *@param  value         Description of Parameter
     *@param  col           Description of Parameter
     *@param  transparency  Description of Parameter
     *        */
    public void draw3DBar(Graphics2D g, int xpos, int ypos, int xinc, int yinc, int value, Color col, float transparency) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g.setColor(col.brighter());
        if (value >= 0) {

            Polygon poly = new Polygon();
            poly.addPoint(xpos, ypos);
            poly.addPoint(xpos + xinc, ypos + yinc);
            poly.addPoint(xpos + xinc + xinc, ypos);
            poly.addPoint(xpos + xinc, ypos - yinc);
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));

        Polygon poly = new Polygon();
        poly.addPoint(xpos, ypos);
        poly.addPoint(xpos + xinc, ypos - yinc);
        poly.addPoint(xpos + xinc, ypos - yinc - ( value * stepsize));
        poly.addPoint(xpos, ypos - (value * stepsize));
        g.fillPolygon(poly);

        poly = new Polygon();
        poly.addPoint(xpos + xinc, ypos - yinc);
        poly.addPoint(xpos + xinc + xinc, ypos);
        poly.addPoint(xpos + xinc + xinc, ypos - (value * stepsize));
        poly.addPoint(xpos + xinc, ypos - yinc - ( value * stepsize));
        g.setColor(col.darker().darker());
        g.fillPolygon(poly);

        poly = new Polygon();
        g.setColor(col);
        poly.addPoint(xpos, ypos);
        poly.addPoint(xpos + xinc, ypos + yinc);
        poly.addPoint(xpos + xinc, ypos + yinc - ((int) value * stepsize));
        poly.addPoint(xpos, ypos - ((int) value * stepsize));
        g.fillPolygon(poly);


        poly = new Polygon();
        poly.addPoint(xpos + xinc, ypos + yinc);
        poly.addPoint(xpos + xinc + xinc, ypos);
        poly.addPoint(xpos + xinc + xinc, ypos - (value * stepsize));
        poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
        g.setColor(col.darker());
        g.fillPolygon(poly);

        if (value >= 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            poly = new Polygon();
            poly.addPoint(xpos, ypos - (value * stepsize));
            poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
            poly.addPoint(xpos + xinc + xinc, ypos - (value * stepsize));
            poly.addPoint(xpos + xinc, ypos - yinc - (value * stepsize));
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        } else {
            poly = new Polygon();
            poly.addPoint(xpos, ypos);
            poly.addPoint(xpos + xinc, ypos + yinc);
            poly.addPoint(xpos + xinc + xinc, ypos);
            poly.addPoint(xpos + xinc, ypos - yinc);
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }

    }


    public void draw3DBarFrom(Graphics2D g, int xpos, int ypos, int xinc, int yinc, int value, int fromValue, Color col, float transparency) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g.setColor(col.brighter());
        if (value >= 0) {

            Polygon poly = new Polygon();
            poly.addPoint(xpos, ypos);
            poly.addPoint(xpos + xinc, ypos + yinc);
            poly.addPoint(xpos + xinc + xinc, ypos);
            poly.addPoint(xpos + xinc, ypos - yinc);
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }


        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));

        Polygon poly = new Polygon();


        poly.addPoint(xpos, ypos);
        poly.addPoint(xpos + xinc, ypos - yinc);
        poly.addPoint(xpos + xinc, ypos - yinc - (fromValue * stepsize));
        poly.addPoint(xpos, ypos - (value * stepsize));
        g.fillPolygon(poly);

        if (true == true) {//fromValue < value && value >= 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            poly = new Polygon();
            poly.addPoint(xpos, ypos - (value * stepsize));
            poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
            poly.addPoint(xpos + xinc + xinc, ypos - (fromValue * stepsize));
            poly.addPoint(xpos + xinc, ypos - yinc - (fromValue * stepsize));
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }



        //BACK
        poly = new Polygon();
        poly.addPoint(xpos + xinc, ypos - yinc);
        poly.addPoint(xpos + xinc + xinc, ypos);
        poly.addPoint(xpos + xinc + xinc, ypos - (fromValue * stepsize));
        poly.addPoint(xpos + xinc, ypos - yinc - (fromValue * stepsize));
        g.setColor(col.darker().darker());
        g.fillPolygon(poly);

        //FRONT
        poly = new Polygon();
        g.setColor(col);
        poly.addPoint(xpos, ypos);
        poly.addPoint(xpos + xinc, ypos + yinc);
        poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
        poly.addPoint(xpos, ypos - (value * stepsize));
        g.fillPolygon(poly);


        poly = new Polygon();
        poly.addPoint(xpos + xinc, ypos + yinc);
        poly.addPoint(xpos + xinc + xinc, ypos);
        poly.addPoint(xpos + xinc + xinc, ypos - (fromValue * stepsize));
        poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
        g.setColor(col.darker());
        g.fillPolygon(poly);


        //TOP
        if (true == true) {//fromValue >= value) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            poly = new Polygon();
            poly.addPoint(xpos, ypos - (value * stepsize));
            poly.addPoint(xpos + xinc, ypos + yinc - (value * stepsize));
            poly.addPoint(xpos + xinc + xinc, ypos - (fromValue * stepsize));
            poly.addPoint(xpos + xinc, ypos - yinc - (fromValue * stepsize));
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }
        if (value < 0 && fromValue < 0) {

            poly = new Polygon();
            poly.addPoint(xpos, ypos);
            poly.addPoint(xpos + xinc, ypos + yinc);
            poly.addPoint(xpos + xinc + xinc, ypos);
            poly.addPoint(xpos + xinc, ypos - yinc);
            g.setColor(col.brighter());
            g.fillPolygon(poly);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  g       Description of Parameter
     *@param  xstart  Description of Parameter
     *@param  ystart  Description of Parameter
     *        */
    private void drawTotals(Graphics g, int xstart, int ystart) {
        int h = getHeight();
        int pos = (h - ystart) / (titles.length + 2);
        for (int i = 0; i < titles.length; i++) {
            g.fillRect(xstart, ystart + pos * (i + 1) - 8, 16, 16);
            g.setColor(this.colmap[i]);
            g.fill3DRect(xstart - 1, ystart + pos * (i + 1) - 9, 16, 16, true);
            g.setColor(Color.black);
            Object x = hashes[hashes.length - 1].getItem(titles[i]);
            if (x == null) {
                g.drawString(titles[i] + " " + "0", xstart + 22, ystart + pos * (i + 1));
            } else {
                g.drawString(titles[i] + " " + x.toString(), xstart + 22, ystart + pos * (i + 1));
            }
            g.setColor(Color.white);
            g.drawString(titles[i], xstart + 21, ystart + pos * (i + 1) - 1);
            g.setColor(Color.black);
        }
    }

    /**
     *  Gets the actualColor attribute of the AthenaMonitorGrid object
     *
     *@param  original  Description of Parameter
     *@param  step      Description of Parameter
     *@param  total     Description of Parameter
     *@return           The actualColor value
     *        */
    public Color getActualColor(Color original, int step, int total) {
        float fRed = 0.0f;
        float fBlue = 0.0f;
        float fGreen = 0.0f;
        float biaspc = 40.0f;

        fRed = original.getRed() > 0 ? original.getRed() / 100.0f * biaspc + (original.getRed() - (original.getRed() / 100.0f * biaspc)) / total * (step) : 0.0f;
        fGreen = original.getGreen() > 0 ? original.getGreen() / 100.0f * biaspc + (original.getGreen() - (original.getGreen() / 100.0f * biaspc)) / total * (step) : 0.0f;
        fBlue = original.getBlue() > 0 ? original.getBlue() / 100.0f * biaspc + (original.getBlue() - (original.getBlue() / 100.0f * biaspc)) / total * (step) : 0.0f;
        return new Color((int) fRed, (int) fGreen, (int) fBlue);
    }


    /**
     *  Gets the stepSize attribute of the AthenaMonitorGrid object
     *
     *@return    The stepSize value
     *        */
    public int getStepSize() {
        return this.stepsize;
    }

    /**
     *  Gets the titles attribute of the AthenaMonitorGrid object
     *
     *@return    The titles value
     *        */
    public String[] getTitles() {
        return titles;
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of Parameter
     *        */
    public void paint(Graphics g) {
        if (this.getHeight() < 200 || this.getWidth() < 200) {
            this.setSize(new Dimension(200, 200));
        }
        /*
		 *  if (image!=null){
		 *  *int imheight = image.getHeight((java.awt.image.ImageObserver) this);
		 *  int imwidth = image.getHeight((java.awt.image.ImageObserver) this);
		 *  double aspectratio =(double)imheight / (double) imwidth;
		 *  g.drawImage(image.getScaledInstance(getWidth(), (int) ((double)getWidth()*aspectratio),image.SCALE_FAST),0,0,(java.awt.image.ImageObserver) this);
		 *
		 *  g.drawImage(image,0,0,(java.awt.image.ImageObserver) this);
		 *  }
		 */

        if (hashes.length > 0) {
            int topvalue = 0;
            Graphics2D g2 = (Graphics2D) g;
            int xdiv = hashes.length + titles.length + 10;
            for (int a = 0; a < hashes.length; a++) {
                for (int b = 0; b < titles.length; b++) {
                    Object x = hashes[a].getItem(titles[b]);
                    if (x != null) {
                        long value = ((Integer) x).longValue();
                        if (value > topvalue) {
                            topvalue = (int) value;
                        }
                    }
                }
            }

            int ydiv = topvalue;
            int xinc = (getWidth() - 50) / xdiv;

            int yinc = xinc / 2;
            int xstart = (xinc * hashes.length) + (xinc * titles.length);

            int ystart = getHeight() - (100) - (hashes.length * yinc) - 50;


            int xpos = xstart;
            int ypos = ystart;
            Color drawCol;
            Color actualCol;
            xpos = xstart + xinc;
            ypos = ystart - yinc;

            g2.setColor(this.getBackground());
            g2.drawRect(0, 0, this.getWidth(), this.getHeight());
            g2.setColor(Color.black);
            //  g2.drawLine(xpos, ypos, xpos+(xinc*hashes.length), ypos+(yinc*titles.length));
            // g2.drawLine(xpos, ypos, xpos-(xinc*5), ypos+(yinc*titles.));
            // g2.drawLine(xpos-(xinc*5), ypos+(yinc*5), xstart+xinc,ypos+(yinc*10));
            //g2.drawLine(xstart+xinc,ypos+(yinc*10), xpos+(xinc*5), ypos+(yinc*5));

            int i = 0;
            for (i = 0; i < hashes.length; i++) {
                xpos = xstart - (xinc * (i));
                ypos = ystart + (yinc * (i));
                for (int j = 0; j < titles.length; j++) {
                    xpos += (rowGap);
                    ypos += (rowGap);
                    Object x = hashes[i].getItem(titles[j]);
                    Object fromx;
                    if (i != 0)
                        fromx = hashes[i - 1].getItem(titles[j]);
                    else
                        fromx = new Integer(0);
                    long value;
                    long fromValue = 0;
                    if (fromx != null) {
                        fromValue = ((Integer) fromx).longValue();
                    } else {
                        fromValue = 0;
                    }

                    if (x != null) {
                        value = ((Integer) x).longValue();
                    } else {
                        value = 0;
                    }
                    if (value > topvalue) {
                        topvalue = (int) value;
                    }
                    drawCol = colmap[j];
                    if (seamless)
                        draw3DBarFrom(g2, xpos, ypos, xinc, yinc, (int) value, (int) fromValue, getActualColor(drawCol, i, hashes.length), transparency);
                    else
                        draw3DBar(g2, xpos, ypos, xinc, yinc, (int) value, getActualColor(drawCol, i, hashes.length), transparency);
                    if (i == hashes.length - 1) {
                        g2.rotate(Math.toRadians(90), xpos, ypos + 10);
                        //g2.shear(0.2,0.2);
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
                        g2.setColor(Color.black);
                        if (!totals) {
                            g2.drawString(titles[j] + " : " + value, xpos, ypos + 10);
                        } else {
                            g2.drawString(titles[j], xpos, ypos + 10);
                        }
                        //g2.shear(-0.2,-0.2);
                        g2.rotate(Math.toRadians(-90), xpos, ypos + 10);

                    }
                    g2.setColor(drawCol);
                    xpos = xpos + xinc;
                    ypos = ypos + yinc;
                }
            }
            xpos = xstart - (xinc * (i - 1)) - 10;
            ypos = ystart + (yinc * (i - 1));
            g2.setColor(Color.black);

            g2.drawLine(xpos, ypos, xpos, ypos - (topvalue * stepsize));
            g2.drawLine(xpos, ypos, xpos - 5, ypos);
            g2.drawString("0", xpos - 16, ypos + 4);
            g2.drawLine(xpos, ypos - (topvalue * stepsize), xpos - 5, ypos - (topvalue * stepsize));
            String valstr = String.valueOf(topvalue);
            g2.drawString(valstr, xpos - valstr.length() * 8 - 8, ypos - (topvalue * stepsize) + 4);
            xpos = (xstart - (xinc * (i - 1)) + (xinc * titles.length) + xinc) + (rowGap * titles.length);
            ypos = (ystart + (yinc * (i - 1)) + (yinc * titles.length) + yinc) + (rowGap * titles.length);
            int xpos2 = (xstart + (xinc * (titles.length + 1)) + xinc) + (rowGap * titles.length);
            ;
            int ypos2 = (ystart + (yinc * (titles.length))) + (rowGap * titles.length);
            g2.drawLine(xpos, ypos, xpos2, ypos2);
            g2.drawLine(xpos, ypos, xpos + xinc / 2, ypos + yinc / 2);
            g2.drawLine(xpos2, ypos2, xpos2 + xinc / 2, ypos2 + yinc / 2);
            g2.drawString("0", xpos + xinc / 2 + 5, ypos + yinc / 2 + 10);

            //These arerelative from the far end, i.e. 3/4 of f the way back from 100 is 1/4 or 25
            g2.drawString(String.valueOf(hashes.length / 2), (xpos2) - (xinc * hashes.length / 2) + xinc / 2 + 5, (ypos2 + (yinc * hashes.length / 2)) + yinc / 2 + 10);
            g2.drawString(String.valueOf((hashes.length / 4) * 3), (xpos2) - (xinc * hashes.length / 4) + xinc / 2 + 5, (ypos2 + (yinc * hashes.length / 4)) + yinc / 2 + 10);
            g2.drawString(String.valueOf((hashes.length / 4)), (xpos2) - (xinc * ((hashes.length / 4) * 3)) + xinc / 2 + 5, (ypos2 + (yinc * ((hashes.length / 4) * 3))) + yinc / 2 + 10);

            g2.drawString(String.valueOf(hashes.length), xpos2 + xinc / 2 + 5, ypos2 + yinc / 2 + 10);
            if (totals) {
                drawTotals(g, xstart + (titles.length * xinc), ystart + (titles.length * yinc));
            }
        }
    }

    public void setGap(int n) {
        rowGap = n;
    }


    /**
     *  Sets the image attribute of the AthenaMonitorGrid object
     *
     *@param  newImage  The new image value
     *        */
    public void setImage(Image newImage) {
        image = newImage;
    }


    /**
     *  Sets the randomOn attribute of the AthenaMonitorGrid object
     *
     *        */
    public void setRandomOn() {
        randOn = true;
        setStatistics(buildranddata());
        thr.start();
    }

    public void setSeamless(boolean onoff) {
        seamless = onoff;
    }


    /**
     *  Sets the statistics attribute of the AthenaMonitorGrid object
     *
     *@param  tstats  The new statistics value
     *        */
    public void setStatistics(Object tstats) {
        this.stats = (Vector) tstats;
        hashes = new StatisticRow[stats.size()];
        for (int i = 0; i < stats.size(); i++) {
            hashes[i] = (StatisticRow) stats.get(i);
        }
        repaint();
    }

    /**
     *  Sets the stepSize attribute of the AthenaMonitorGrid object
     *
     *@param  stepsize  The new stepSize value
     *        */
    public void setStepSize(int stepsize) {
        this.stepsize = stepsize;
    }

    /**
     *  Sets the titles attribute of the AthenaMonitorGrid object
     *
     *@param  titles  The new titles value
     *        */
    public void setTitles(String[] titles) {
        this.titles = titles;
    }


    /**
     *  Sets the totals attribute of the AthenaMonitorGrid object
     *
     *@param  onoff  The new totals value
     *        */
    public void setTotals(boolean onoff) {
        totals = onoff;
    }


    /**
     *  Sets the transparency attribute of the AthenaMonitorGrid object
     *
     *@param  trans  The new transparency value
     *        */
    public void setTransparency(float trans) {
        transparency = trans;
    }


    public void setWindowSize(int windowSize) {

    }
}

