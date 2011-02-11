/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 05-Aug-2002
 * Time: 15:23:48
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.xml;

import java.util.HashMap;

import org.jini.projects.athena.command.dialect.TransformInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

 
public class XSLTransformsLoader extends org.xml.sax.helpers.DefaultHandler {

    private long start;
    private HashMap transforms = new HashMap();
    private boolean inInput = false;
    private boolean inOutput = false;
    private String transformName;
    private String inputName;
    private String outputName;

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("transform")) {
            transformName = attributes.getValue(0);
        }
        if (qName.equals("input")) {
            inInput = true;
        }
        if (qName.equals("output")) {
            inOutput = true;
        }

    }

    public void endDocument()
            throws SAXException {
        //SystemManager.LOG.info("Transforms Loaded");
    }

    public void startDocument()
            throws SAXException {

        start = System.currentTimeMillis();
        //SystemManager.LOG.info("Loading Transforms");
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        if (inInput)
            inputName = String.copyValueOf(ch, start, length);
        if (inOutput)
            outputName = String.copyValueOf(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("input"))
            inInput = false;
        if (qName.equals("output"))
            inOutput = false;
        if (qName.equals("transform")) {
            TransformInfo ti = new TransformInfo(transformName, inputName, outputName);
            transforms.put(transformName.toLowerCase(), ti);
            transformName = null;
            inputName = null;
            outputName = null;
        }
    }

    public void error(SAXParseException e)
            throws SAXException {
        super.error(e);
    }

    public HashMap getTransforms() {
        return transforms;
    }


}
