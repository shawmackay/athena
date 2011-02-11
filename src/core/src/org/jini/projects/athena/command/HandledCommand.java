/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 02-Aug-2002
 * Time: 14:33:11
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command;

import java.util.List;

/**
 * Stores information about an idividual command, described in a Handler.
 * @see org.jini.projects.athena.xml.CHILoader
 * @author calum
 *
 */
public class HandledCommand {
    public HandledCommand() {
        //Default Constructor
    }


    private StringBuffer command;
    private List prepared_params;
    private String type;
    private String alias;
    private String returnInType;
    private String optype;
    private boolean useCache;
    
    public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	/**
     * Get the command invoker string 
     * @return the command
     */
    public StringBuffer getCommand() {
        return command;
    }
    /**
     * Set the command invoker string 
     * @param command the command to set
     */
    public void setCommand(StringBuffer command) {
        this.command = command;
    }
    /**
     * Get the command type 
     * @return the command
     */
    public String getType() {
        return type;
    }
    /**
     * Set the command type 
     * @param type the command
     */
    public void setType(String type) {
        this.type = type;
    }


    public HandledCommand(String type, StringBuffer command) {
        this.type = type;
        this.command = command;
    }

    /**
     * Returns the alias, if any.
     * @return String
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     * @param alias The alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Return whether the results of this command should be cached internally
     * @return boolean
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * Sets the useCache flag - if set, athena will cache the results of this invocation
     * @param useCache Cache flag
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

	/**
	 * @return Returns the prepared_params.
	 */
	public List getPrepared_params() {
		return prepared_params;
	}
	/**
	 * @param prepared_params The prepared_params to set.
	 */
	public void setPrepared_params(List prepared_params) {
		this.prepared_params = prepared_params;
	}
	public String getReturnInType() {
		return returnInType;
	}
	public void setReturnInType(String returnInType) {
		this.returnInType = returnInType;
	}
}
