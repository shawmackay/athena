/*
 *  HandleEngine.java
 *
 *  Created on 29 January 2002, 13:05
 */

package org.jini.projects.athena.command;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.athena.xml.CHILoader;

/**
 * Singleton class from which a handler can be requested
 *
 * @author calum
 *
 */
public class HandleEngine {
    private static HandleEngine engine;
    
    private Logger log = Logger.getLogger("org.jini.projects.athena.command");
    java.util.HashMap handlers = new java.util.HashMap();
    int numhandlers = 0;
    private String athenaName;
    /**
     * Creates a new instance of HandleEngine
     *
     * @since
     */
    private HandleEngine(Configuration config) {
        try {
			athenaName = (String) config.getEntry("org.jini.projects.athena", "athenaName", String.class);
		} catch (ConfigurationException e) {
			// URGENT Handle ConfigurationException
			e.printStackTrace();
		}
        File handledir = new File(System.getProperty("user.dir") + "/config/handlers/" + athenaName);
        if (handledir.isDirectory()) {
            File[] handledescriptions = handledir.listFiles();
            for (int i = 0; i < handledescriptions.length; i++) {
                if (handledescriptions[i].getAbsolutePath().endsWith(".xml")) {
                    loadHandler(handledescriptions[i].getAbsolutePath());
                    numhandlers++;
                }
            }
        } else
            log.log(Level.INFO, "Handler directory incorrect: " + System.getProperty("user.dir") + "/config/handlers/" + athenaName);
        log.fine("Loaded " + numhandlers + " handlers");
    }

    /**
     * Returns the reference to the singleton Engine
     *
     * @return The engine value
     * @since
     */
    public static HandleEngine getEngine() {
        return engine;
    }
    /**
     * Gets a list of handler definitions. Used in the ServiceUI
     * @return Map of loaded handler definitions
     */
    public Map getHandlerDefinitions() {
        File handledir = new File(System.getProperty("user.dir") + "/config/handlers/" + System.getProperty("org.jini.projects.athena.service.name"));
        HashMap map = new HashMap();
        if (handledir.isDirectory()) {
            File[] handledescriptions = handledir.listFiles();
            for (int i = 0; i < handledescriptions.length; i++) {
                if (handledescriptions[i].getAbsolutePath().endsWith(".xml")) {
                    loadRawXML(handledescriptions[i].getAbsolutePath(), map);
                }
            }
        } else
            log.log(Level.FINE, "Handler directory incorrect: " + System.getProperty("user.dir") + "/config/handlers/" + System.getProperty("org.jini.projects.athena.service.name"));
        log.finest("Loaded " + numhandlers + " handlers");
        return map;
    }


    private void loadRawXML(String fileName, java.util.Map dataObject) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            char[] buffer = new char[(int) f.length()];
            fr.read(buffer);
            String s = new String(buffer);
            Pattern p = Pattern.compile("<name>.*</name>");
            Matcher m = p.matcher(s);

            if (m.find()) {
                String namegroup = m.group();
                namegroup = m.group();
                
                int open_element_end = namegroup.indexOf('>');
                int close_element_start = namegroup.indexOf('<', open_element_end);
                String n = namegroup.substring(open_element_end + 1, close_element_start);
                
                dataObject.put(n, s);
            }

        } catch (FileNotFoundException e) {
            // URGENT Handle FileNotFoundException
            e.printStackTrace();
        } catch (IOException e) {
            // URGENT Handle IOException
            e.printStackTrace();
        }

    }

    /**
     * Obtains a handler for a given name - usually the callname of a Command
     * object
     *
     * @see org.jini.projects.athena.command.Command
     * @param name
     *                   Description of Parameter
     * @return A handler for the givn argument
     */
    public Handler getHandlerFor(String name) {
       // if (System.getProperty("org.jini.projects.athena.debug") != null)
         //   if (DEBUG)
        log.log(Level.FINE, "CHI: Handler: " + name);
        Handler h = (Handler) handlers.get(name.toLowerCase());
        if (h == null) {
            System.out.println("Arghgh No handler exists");
        }
        Handler handle = new Handler(h);
        
        return handle;
    }

    /**
     * Loads an xml file and translates this into a Handler Defnition in memory
     *
     * @param xmlfile
     *                   Description of Parameter
     * @since
     */
    public void loadHandler(String xmlfile) {
        try {
            BufferedInputStream b_xmlin = new BufferedInputStream(new FileInputStream(xmlfile));
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            CHILoader loader = new CHILoader();
            org.xml.sax.helpers.DefaultHandler dh = loader;
            parser.parse(b_xmlin, dh);
            Handler returndata = loader.getHandlerInstance();
            handlers.put(returndata.getHandlerName().toLowerCase(), returndata);
        } catch (Exception ex) {
            System.err.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    /**
     * Initialises the engine, using the supplied Jini cponfiguration
     * @param config the configuration to use
     */
   public static void initialise(Configuration config){
        if (engine == null) {
            engine = new HandleEngine(config);
        }
    }
}
