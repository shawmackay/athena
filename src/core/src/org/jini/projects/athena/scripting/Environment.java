/*
 * Created on 23-Nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jini.projects.athena.scripting;

import java.io.File;

/**
 * @author Calum
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Environment {
    public void initEnvironment();
    public void cleanEnvironment();
    public void setScript(File scriptFile) throws Exception;
    public void setScript(String script) throws Exception;
    public void setVariable(String name, Object obj);
    public Object getVariable(String name);
    public void execute() throws Exception;
}
