package org.jini.projects.athena.command.dialect;

import java.io.Serializable;

/**
 * Helper class for holding dialect transform information.
 * XSLT Transform information is held in pairs under a single TransformName.<br/>
 * This is so that if you transform from Athena to Host via XSL, you should really use XSL
 * to transfrom the data from the host into Athena. The output XSL file
 * could be the same for multiple transforms, say for instance, error.xsl, where if the first
 * five characters are "ERROR" specify an error, otherwise just pass it through.
 * @author calum
 *
 */
public class TransformInfo implements Serializable{
    private String transformName;
    private String inputTransForm;
    private String outputTransform;
/**
 * Construct a new TransformInfo object
 * @param transformName the name of the transform
 * @param inputTransForm name of the XSL file to use in input transformation
 * @param outputTransform name of the XSL file to use in output transformation
 */
    public TransformInfo(String transformName, String inputTransForm, String outputTransform) {
        this.transformName = transformName;
        this.inputTransForm = inputTransForm;
        this.outputTransform = outputTransform;
    }

    /**
     * Get the transform name - this is the name that will have to be in your handler
     * @return name of the transform i.e the call name
     */
    public String getTransformName() {
        return transformName;
    }

    /**
     * Get the name of the Input Transform. As defined in<br/>
     * [athenaroot]/config/dialects/[athenaname]/in 
     * @return the name of the Input transform
     */
     
    public String getInputTransForm() {
        return inputTransForm;
    }
    /**
     * Get the name of the Output Transform. As defined in<br/>
     * [athenaroot]/config/dialects/[athenaname]/out
     * @return the name of the Output transform
     */
    public String getOutputTransform() {
        return outputTransform;
    }
    
    public String toString() {
        return transformName + ":" + inputTransForm + "," + outputTransform;
    }
}
