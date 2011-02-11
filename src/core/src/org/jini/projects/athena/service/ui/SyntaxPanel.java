/*
 * athena.jini.org : org.jini.projects.athena.ui
 * 
 * 
 * SyntaxPanel.java Created on 28-Apr-2004
 * 
 * SyntaxPanel
 *  
 */

package org.jini.projects.athena.service.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author calum
 */
public class SyntaxPanel extends JPanel {
	StyledDocument doc;
	MutableAttributeSet keywords = new SimpleAttributeSet();
	MutableAttributeSet tags = new SimpleAttributeSet();
	MutableAttributeSet attributes = new SimpleAttributeSet();
	MutableAttributeSet strings = new SimpleAttributeSet();
	MutableAttributeSet comments = new SimpleAttributeSet();
	MutableAttributeSet normal = new SimpleAttributeSet();

	public SyntaxPanel() {
		StyleConstants.setFontFamily(keywords, "Arial");
		StyleConstants.setBold(keywords, true);
		StyleConstants.setForeground(keywords, Color.BLUE.darker());
		StyleConstants.setFontFamily(tags, "Arial");
		StyleConstants.setBold(tags, true);
		StyleConstants.setForeground(tags, new Color(100, 100, 255));
		StyleConstants.setFontFamily(attributes, "Arial");
		StyleConstants.setBold(attributes, false);
		StyleConstants.setForeground(attributes, Color.GREEN.darker().darker());
		StyleConstants.setFontFamily(strings, "Arial");
		StyleConstants.setBold(strings, false);
		StyleConstants.setForeground(strings, Color.MAGENTA.darker());
		StyleConstants.setFontFamily(comments, "Courier");
		StyleConstants.setItalic(comments, true);
		StyleConstants.setForeground(comments, Color.ORANGE.darker());
		StyleConstants.setFontFamily(normal, "Arial");
        StyleConstants.setBold(normal, false);
		StyleConstants.setForeground(normal, Color.BLACK);
		setLayout(new BorderLayout());
		JTextPane tp = new JTextPane();
		doc = tp.getStyledDocument();
		tp.setDocument(doc);
        JScrollPane pane = new JScrollPane(tp);
		add(pane, BorderLayout.CENTER);
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.getContentPane().add(new SyntaxPanel());
		frame.setVisible(true);
	}

	public void insertDoc(String XML) throws BadLocationException {
		//int pos =0;
        doc.remove(0, doc.getLength());
		int lastpos = 0;
		int length = 0;
		int startpos = 0;
		StringBuffer buff = new StringBuffer(XML);
		boolean inComment = false;
		boolean inElement = false;
		boolean inData = false;
		// Pattern comment= Pattern.compile("<!--.*-->");
		//Pattern element= Pattern.compile("<[\\w\\s=*/?>");
		for (int pos = 0; pos < XML.length(); pos++) {
			if (XML.charAt(pos) == '<') {
				startpos = pos;
				pos++;
				if (XML.charAt(pos) == '!') {
					//inComment
					int endpos = XML.indexOf(">", pos);
					doc.insertString(doc.getLength(), XML.substring(startpos, endpos + 1), comments);
					pos = endpos;
				} else if(XML.charAt(pos) == '?') {
                    //inComment
                    int endpos = XML.indexOf(">", pos);
                    doc.insertString(doc.getLength(), XML.substring(startpos, endpos + 1), tags);
                    pos = endpos;
                }else {
					//inElement
					int endpos = XML.indexOf(">", pos);
					String the_element = XML.substring(startpos, endpos + 1);
					scanElement(the_element);
					pos = endpos;
				}
			} else
				doc.insertString(doc.getLength(), XML.substring(pos, pos + 1), normal);
		}
	}

	private void scanElement(String el) throws BadLocationException {
		boolean inelementdef = true;
		doc.insertString(doc.getLength(), el.substring(0, 1), tags);
		boolean elementNamed = false;
		for (int i = 1; i < el.length(); i++) {
			while (el.charAt(i) == ' ')
				i++;
			//System.out.println("i: " + i);
			int elementNameEnd = el.indexOf(' ', i);
            if (elementNameEnd==-1)
                elementNameEnd = el.indexOf(">")-1;
			if (elementNameEnd != -1 && !elementNamed) {
				doc.insertString(doc.getLength(), el.substring(1, elementNameEnd + 1), keywords);
				elementNamed = true;
              //  System.out.println("Element is named: " + el.substring(1, elementNameEnd));
				i = elementNameEnd;
			} else {                
                //We have an attribute
				elementNameEnd = el.indexOf('>', i);
                String s = el.substring(i, elementNameEnd);
                int attrpos = 0;
              //  System.out.println("Attribute: " + s);
                while(s.indexOf("=", attrpos)>-1){
                    String attributename=s.substring(attrpos,s.indexOf("=", attrpos));
                 //   System.out.println(attributename);
                    doc.insertString(doc.getLength(), attributename, attributes);
                    doc.insertString(doc.getLength(), "=", tags);
                    attrpos = s.indexOf("=",attrpos);
                    int attributeStart = s.indexOf("\"", attrpos);
                    int attributeEnd = s.indexOf("\"", attributeStart+1);
                    String attributeValue = s.substring(attributeStart,attributeEnd+1);
                //    System.out.println(attributeValue);
                    doc.insertString(doc.getLength(), attributeValue, strings);
                    attrpos = attributeEnd+1;
                }
                i +=s.length();
			}
			if (el.charAt(i) == '>')
				doc.insertString(doc.getLength(), ">", tags);
		}
	}
}