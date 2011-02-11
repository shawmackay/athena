/*
 * r TESTP11_Dialect.java
 *
 * Created on 10 September 2001, 13:06
 */
package org.jini.projects.athena.connects.ctg.chi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jini.projects.athena.command.dialect.Dialect;
import org.jini.projects.athena.command.dialect.DialectEngine;
import org.jini.projects.athena.command.types.TypeEngine;
import org.jini.projects.athena.exception.ValidationException;
import org.jini.projects.athena.xml.AthenaSAXHandler2;

import com.ibm.ctg.client.ECIRequest;
import com.ibm.ctg.client.JavaGateway;

/**
 * The default dialect used for CICS Transaction Gateway.
 * This should suffice for most cases.
 */
public class DefaultCTG_Dialect implements Dialect, Serializable {

    //JoltRemoteService joltService;
    //JoltDefinition joltDefinition;
    transient Vector returndata;
    private java.util.TreeMap params_in;
    private java.util.HashMap params_out = new HashMap();
    private java.util.HashMap header;
    private StringBuffer command = new StringBuffer();
    private String CommandName;
    transient ECIRequest req;
    static final boolean DEBUG = (System.getProperty("org.jini.projects.athena.debug") != null ? true : false);

    /**
     * Creates a new DefaultJolt_Dialect. This handles most instances where in
     * Jolt, you have defined the JoltDefinition, using something like FML.
     * <BR>You may have a COBOL copybook that mirrors this definition, where
     * you have a one-to-one relationship in the arguments. <BR>In cases where
     * you have combined arguments, for instance a single String representation
     * built up from oth erarguments that need combining and splitting before
     * and after the call create a specific Dialect to handle that
     *
     * @since
     */
    public DefaultCTG_Dialect() {
    }

    public DefaultCTG_Dialect(String CommandName) {
        this.CommandName = CommandName;
    }

    /**
     * Sets the callReturn attribute of the DefaultJolt_Dialect object
     *
     * @param obj
     *                   The new callReturn value
     * @since
     */
    public void setCallReturn(Object obj) {
    }

    /**
     * Gets the raw call output that was sent back before it is post-processed
     *
     * @return The callOutput value
     * @since
     */
    public Object getCallOutput() {
        if (returndata.size() > 0) {
            return returndata;
        } else {

            return params_out;
        }
    }

    /**
     * Gets the raw data sent in the call, this is useful for debugging commareas
     *
     * @return The callInput value
     * @since
     */
    public Object getCallInput() {
        return command.toString();
    }

    /**
     *Obtains the header for the ouput that can be used to build a table
     * @return The outputHeader value
     * @since
     */
    public java.util.HashMap getOutputHeader() {
        return header;
    }

    /**
     * Gets the executionType attribute of the DefaultCTG_Dialect object
     *
     * @return The executionType value
     * @since
     */
    public int getExecutionType() {
        return 0;
    }

    /**
     * Description of the Method
     *
     * @since
     */
    public void processOutput() {
        try {
            if (DEBUG)
                System.out.println("Commarea String: " + new String(req.Commarea));
            String callret = new String(req.Commarea);
            String wrapper = "<INPUT>" + callret + "</INPUT>";
            if (DEBUG) {
                System.out.println("CommandName is: " + this.CommandName);
                System.out.println("Call return is: " + callret);
            }
            Transformer processor = DialectEngine.getEngine().getOutputTransform(this.CommandName);
            StringBuffer outputBuffer = new StringBuffer();
            StreamSource input = new StreamSource(new StringReader(wrapper));
            StringWriter outdest = new StringWriter();
            StreamResult output = new StreamResult(outdest);

            processor.transform(input, output);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            AthenaSAXHandler2 athenahandler = new AthenaSAXHandler2();
            org.xml.sax.helpers.DefaultHandler dh = athenahandler;
            ByteArrayInputStream bis = new ByteArrayInputStream(outdest.getBuffer().toString().getBytes());

            parser.parse(bis, dh);

            returndata = athenahandler.getData();
            params_out = athenahandler.getExceDetails();
            if (DEBUG)
                System.out.println("Output Parameters: " + params_out);
            header = athenahandler.getHeader();
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Description of the Method
     *
     * @since
     */
    public void processInput() {
        //joltService.clear();

        Transformer processor = DialectEngine.getEngine().getInputTransform(this.CommandName);
        Set paramsSet = params_in.entrySet();
        Iterator iter = paramsSet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            processor.setParameter((String) entry.getKey(), entry.getValue());
        }
        StringBuffer outputBuffer = new StringBuffer();
        String wrapper = "<INPUT></INPUT>";
        StreamSource input = new StreamSource(new StringReader(wrapper));
        StringWriter outdest = new StringWriter();
        StreamResult output = new StreamResult(outdest);

        try {
            processor.transform(input, output);
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        if (DEBUG)
            System.out.println(outdest.getBuffer().toString());
        String preprocess = outdest.getBuffer().toString();
        TypeEngine engine = TypeEngine.getTypeEngine();
        if (DEBUG)
            System.out.println("Preprocessed: [" + preprocess + "]");
        try {
            //joltService.addString("INOUT", engine.process(preprocess,
            // params_in));
            String comm = engine.process(preprocess, params_in) + "    ";
            if (DEBUG)
                System.out.println("Post Engine process: [" + comm + "]");

            req.Commarea = comm.getBytes();
            req.Commarea_Length = comm.length();
            command.append(comm);
        } catch (ValidationException e) {
            System.out.println();
        }
        TypeEngine.returnEngine(engine);
    }

    /**
     * Description of the Method
     *
     * @param initials
     *                   Description of Parameter
     * @since
     */
    public void init(Object[] initials) {
        req = (ECIRequest) initials[0];
        params_in = (TreeMap) initials[1];
    }

    /*
	 * Change this to use ECIRequests
	 *
	 */
    public static void main(String[] args) {

        //sattr.setString(JoltSessionAttributes.APPADDRESS,
        // "//middleware2:2000");

        /*
		 * switch (sattr.checkAuthenticationLevel()) { case
		 * JoltSessionAttributes.NOAUTH : System.out.println("Connection :
		 * NOAUTH\n"); break;
		 */
        System.setProperty("org.jini.projects.athena.service.name", "MCIX");
        try {
            Class.forName("org.jini.projects.athena.command.dialect.DialectEngine");
            System.out.println("DialectEngine initialised @ " + new java.util.Date());
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        //sattr.setInt(JoltSessionAttributes.IDLETIMEOUT, 300);
        //System.out.println("Getting JoltSession @ " + new java.util.Date());
        //JoltSession session = new JoltSession(sattr, null, null, null,
        // null);
        //JoltRemoteService jrs = new JoltRemoteService("EXCHDTRIG", session);
        JavaGateway jgate = null;
        try {
            jgate = new JavaGateway("middleware2.countrywide-assured.co.uk", 2012, null, null);
            System.out.println("Building Parameters @ " + new java.util.Date());
            TreeMap params = new TreeMap();
            params.put("NAADRef", "000008X");
            params.put("exchange_date", "2020411");
            DefaultCTG_Dialect app = new DefaultCTG_Dialect("JCAD");
            ECIRequest req = new ECIRequest();
            req.Server = "VHAMCIX";
            req.Call_Type = ECIRequest.ECI_SYNC;
            req.Extend_Mode = ECIRequest.ECI_NO_EXTEND;

            req.Program = "TESTP12";
            req.Userid = null;
            req.Password = null;
            req.Luw_Token = ECIRequest.ECI_LUW_NEW;
            req = new ECIRequest(ECIRequest.ECI_SYNC, //ECI
                    // call
                    // type
                    "VHAMCIX", //CICS server
                    null, //CICS userid
                    null, //CICS password
                    "TESTP12", //CICS program to be run
                    null, //CICS transid to be run
                    null, //Byte array containing the
                    // COMMAREA
                    0, //COMMAREA length
                    ECIRequest.ECI_NO_EXTEND, //ECI extend mode
                    ECIRequest.ECI_LUW_NEW);
            Object[] parms = {req, params};
            app.init(parms);

            System.out.println("Processing input @ " + new java.util.Date());
            app.processInput();
            displayR(req);
            System.out.println("Sending: " + new String(req.Commarea));
            jgate.flow(req);
            System.out.println("Received:" + new String(req.Commarea));
            displayR(req);
            app.processOutput();
            System.out.println(app.getCallInput());
            System.out.println(app.getCallOutput());
            System.out.println(req.Commarea == null);
            System.out.println(req.Commarea_Length);
        } catch (IOException eClose) {
            System.out.println("Exception during dialect : " + eClose);
        }
        try {
            if (jgate != null) {
                jgate.close();
                System.out.println("Successfully closed JavaGateway");
            }
        } catch (IOException eClose) {
            System.out.println("Exception during close : " + eClose);
        }
        System.exit(0);
    }

    void displayMsg(String message) {
        System.out.println(message);
    }

    void displayRc(ECIRequest eciRequest) {
        displayMsg("Return code   : " + eciRequest.getCicsRcString() + "(" + eciRequest.getCicsRc() + ")");
        displayMsg("Abend code    : " + eciRequest.Abend_Code);
    }

    static void displayMs(String message) {
        System.out.println(message);
    }

    static void displayR(ECIRequest eciRequest) {
        displayMs("Return code   : " + eciRequest.getCicsRcString() + "(" + eciRequest.getCicsRc() + ")");
        displayMs("Abend code    : " + eciRequest.Abend_Code);
    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallName(java.lang.String)
     */
    public void setCallName(String callName) {
        if (DEBUG)
            System.out.println("DefaultCTG_Dialect: Setting callName to  " + callName);
        this.CommandName = callName;
    }

    /**
     * @see org.jini.projects.athena.command.dialect.Dialect#setCallAlias(java.lang.String)
     */
    public void setCallAlias(String callAlias) {
    }
}
