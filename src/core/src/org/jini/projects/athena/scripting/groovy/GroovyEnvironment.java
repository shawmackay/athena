/*
 * Created on 23-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jini.projects.athena.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.FileInputStream;

import org.jini.projects.athena.scripting.Environment;

/**
 * @author Calum
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GroovyEnvironment implements Environment{
    Binding currentBinding= null;
    GroovyShell currentShell = null;
    File scriptFile =null;
    String script = null;
    boolean useFile = false;
    public GroovyEnvironment(){
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#cleanEnvironment()
     */
    public void cleanEnvironment() {
        // TODO Auto-generated method stub
        currentBinding = new Binding();
        currentShell= new GroovyShell(currentBinding);
    }
    
    
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#getVariable(java.lang.String)
     */
    public Object getVariable(String name) {
        // TODO Auto-generated method stub
        return currentBinding.getVariable(name);
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#initEnvironment()
     */
    public void initEnvironment() {
        // TODO Auto-generated method stub
        System.out.println("Initialising Script Shell");
        currentBinding = new Binding();
        currentShell= new GroovyShell(currentBinding);
        System.out.println("Initialised Script Shell");
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#setVariable(java.lang.String, java.lang.Object)
     */
    public void setVariable(String name, Object obj) {
        // TODO Auto-generated method stub
        System.out.println("Setting variable: " + name + " to instance of " +obj.getClass().getName());
        currentBinding.setVariable(name, obj);
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#execute()
     */
    public void execute() throws Exception {
        // TODO Auto-generated method stub
        if(useFile){
            StringBuffer buffer = new StringBuffer();
            System.out.println("Opening script");
            byte[] array = new byte[(int)scriptFile.length()];
            FileInputStream fis = new FileInputStream(scriptFile);
            fis.read(array);
            fis.close();
            System.out.println("Script file closed");
            currentShell.evaluate(new String(array));           
        } else{
            currentShell.evaluate(script);
        }
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#setScript(java.io.File)
     */
    public void setScript(File scriptFile) throws Exception {
        // TODO Auto-generated method stub
        this.useFile = true;
        this.scriptFile = scriptFile;
        
    }
    /* (non-Javadoc)
     * @see org.jini.projects.athena.scripting.Environment#setScript(java.lang.String)
     */
    public void setScript(String script) throws Exception {
        this.useFile = false;
        this.script = script;
        

    }
}
