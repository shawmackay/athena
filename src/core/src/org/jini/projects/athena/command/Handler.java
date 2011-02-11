/*
 * Handler.java
 *
 * Created on 29 January 2002, 12:06
 */
package org.jini.projects.athena.command;

//import org.jini.projects.org.jini.projects.athena.command.validators.DefaultValidator;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.athena.command.types.TypeEngine;
import org.jini.projects.athena.command.validators.ParameterValidator;
import org.jini.projects.athena.command.validators.Validator;
import org.jini.projects.athena.exception.ValidationException;
import org.jini.projects.athena.scripting.Environment;

/**
 * Intercepts <code>Commands</code>, validating them and informing the
 * Runtime how it should forward them on to the host.
 *
 * @author calum
 *
 */
public class Handler extends Object implements Cloneable {
    private int currentCommIdx = -1;
    
    private Logger log = Logger.getLogger("org.jini.projects.athena.command");
    Validator validation;
    Vector commands;
    HandledCommand hcomm;
    Command comm;
    String handler_name;
    String cachepattern;
    boolean cacheupdate;

    boolean nocache;

    HashMap tuning;
    HashMap symbolTable = new HashMap();

    /**
     * Creates a new instance of a Handler
     *
     * @since
     */
    public Handler() {
        //Default Constructor
    }

    /**
     * Creates a handler with a given name 
     *
     * @param name
     *                   Name of handler used when calling.
     * @since
     */
    public Handler(String name) {
        handler_name = name;
    }

    /**
     * Copy Constructor for the Handler object
     *
     * @param handle
     *                   Existing Handler instance to copy
     * @since
     */
    public Handler(Handler handle) {
        this.validation = handle.validation;
        this.commands = handle.commands;
        this.comm = handle.comm;
        this.handler_name = handle.handler_name;
        this.tuning = handle.tuning;
        this.cachepattern = handle.cachepattern;
        this.cacheupdate = handle.cacheupdate;

    }


    /**
     * Sets the validator of the handler. This will validate all the arguments
     * of the passed Command
     *
     * @param valid
     *                   The new validator to use
     * @since
     */
    public void setValidator(Validator valid) {
        this.validation = valid;
    }

    /**
     * Sets the name of the stage2 execution object
     *
     * @param commands
     *                   The new handlerCommand value
     * @since
     */
    public void setHandlerCommands(Vector commands) {
        this.commands = commands;

    }

    /**
     * Sets up any tuning parameters that thr runtime coan utilize during the
     * exceution of the command
     */
    public void setTuningParameters(java.util.HashMap params) {
        this.tuning = params;
    }

    /**
     * Sets the commandType indicating how the runtime will forward commands
     * matching this handler. This can currently be:
     * <ul>
     * <li><b>replace</b>- The system looks for parameters embedded in the
     * command string. <br>These parameters take the form of unix environment
     * variables i.e. <br>
     *
     * <pre>
     *  select * from orders where ord_num=${order_no}</pre>- where order_no is specified in the org.jini.projects.org.jini.projects.athena.command.Command
     * parameters as 50 <br>Athena will replace/expand this to <br>
     *
     * <pre>
     *  select * from orders where ordnum=50
     *       *          </pre>
     *
     * This type is especially useful for SQL Strings</li>
     * <li><b>dialect</b>- The system instantiates a given dialect and
     * returns it to the caller</li>
     * </ul>
     *
     *
     *  * Sets the <code>Command</code> object that will be validated against
     * when the handler is executed
     *
     * @param comm
     *                   The new commandObject value
     * @since
     */
    public void setCommandObject(Command comm) {
        this.comm = comm;
        if (validation == null) {
            log.log(Level.FINE, "Creating an empty validator");
            //validation = new ParameterValidator(org.jini.projects.athena.command.types.TypeEngine.getTypeEngine());
            validation = new ParameterValidator();
        }
        validation.setCommand(comm);
    }

    /**
     * Configures how this item will handle communication to Dynamic caches
     * matching a certain pattern. <br>This patterm is a regular expression.
     * When a query command executes successfully, it can store it's data in a
     * cache. When an update occurs that could affect the data in the cache,
     * the update has to be able to know which items could be affected.
     * Unfortunately, due to the sheer number of combinations of data that you
     * can get a single cache would be too unwieldy and inefficient, so the
     * cache invlaidation procedure may match several caches simultaneously. To
     * do this all queries store their caches in certain names in the format
     * <br>
     *
     * <pre>
     *  &lt;CALLNAME&gt;&lt;ARGUMENT1&gt;&lt;ARGUMENT2&gt;&lt;ARGUMENT3&gt;....&lt;ARGUMENTN&gt;
     *       *</pre>
     *
     * where arguments are in alphabetical order of their names rather than
     * their values
     *
     * for instance the command <b>TEST</b> with arguments <b>blocks,
     * command, and order_num</b> will have the pattern <b>
     *
     * </pre>
     *
     * TEST${blocks}${command}${order_num}
     *
     * </pre>
     *
     * </b> in this case a pre-processor will expand this, in the event of the
     * arguments being 50,read,J0556 to <br>TEST50readJ0556 .....and <i>then
     * </i> it will do a regexp match - so in this case this will be exact
     * matches so TEST49readJ0556 will not match, however the patterm <br>
     * TEST\d\d${command}${order_num} <br>will match any blocks with this
     * order_num run with this command and ask them to be invalidated <br><br>
     * Invalidation can happen two ways, In-thread (cache-update) or on-demand
     * (cache-remove). <br>In the first instance all marked invalid caches
     * will be re-executed before the commit is finalised - which will slow
     * down the updating client slightly. However you may opt for on-demand
     * updates, which simply removes the item from the available cahe items,
     * which can then be re-loaded by the next client who requests that object.
     * <br><br>The cache update occurs as part of transaction finalisation to
     * ensure that only updates that can be seen to have completed will
     * re-communicate with the host.
     *
     * @see org.jini.projects.athena.resources.FlashableCacheManager
     */
    public void setCacheManipulation(boolean updateOrRemove, String pattern) {
        cacheupdate = updateOrRemove;
        cachepattern = pattern;
    }

    /**
     * Indicates whether an update will request an update to a cache or whether
     * it will remove it from the cache completely
     *
     * @return boolean
     */

    public boolean updateCacheorRemove() {
        return cacheupdate;
    }

    /**
     * Holds the reqular expression that will be used to match items in the
     * RSET cache
     *
     * @return String
     */
    public String getCachePattern() {
        return cachepattern;
    }

    /**
     * Executes the handler.It also merges the validation and cache rules to
     * the user-supplied Command object Returning an object which indicates how
     * org.jini.projects.athena should progress with the execution of the client request
     *
     * @return The executionObject value
     * @exception ValidationException
     *                        the user-supplied command is invali, in line with the
     *                        validation rules of the handler
     * @since
     */
    public Object getExecutionObject() throws ValidationException {
        try {
            log.log(Level.FINEST,"VALIDATING on " + validation.getClass().getName());
            validation.isValid();
            log.log(Level.FINEST,"VALIDATION COMPLETED");
        } catch (ValidationException vex) {
            System.out.println("Validation Msg: " + vex.getMessage());
            throw vex;
        }
        HandledCommand hcomm = (HandledCommand) commands.get(currentCommIdx);
        nocache = hcomm.isUseCache();
        String type = hcomm.getType();
        StringBuffer command = hcomm.getCommand();
        if (type.equals("dialect")) {
            try {                
                    log.log(Level.INFO, "Getting command: " + command.toString().trim());
                org.jini.projects.athena.command.dialect.Dialect dialect = org.jini.projects.athena.command.dialect.DialectEngine.getDialect(command.toString().trim());
                if (dialect == null) {
                    
                        log.log(Level.FINE, new java.util.Date() + ": CHI: Your dialect is null");
                    if (command.toString().indexOf(':') != -1) {
                        String dname = command.substring(1, command.indexOf(":"));
                        String cname = command.substring(command.indexOf(":") + 1);
                      
                            log.log(Level.FINE, "Dname: " + dname.trim());
                            log.log(Level.FINE, "Cname: " + cname);
                     

                        comm.setCallName(cname.trim());
                        dialect = org.jini.projects.athena.command.dialect.DialectEngine.getDialect(dname.trim());
                        dialect.setCallName(cname.trim());
                    }
                }
                if (hcomm.getAlias() != null) {
                    comm.setParameter("_ALIAS", hcomm.getAlias());
                    dialect.setCallAlias(hcomm.getAlias());
                }
                return dialect;
            } catch (Exception ex) {
                System.out.println("Err: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        if (type.equals("proc")) {
            //System.out.println("Running procedure");
            if (hcomm.getAlias() != null) {
                comm.setParameter("_ALIAS", hcomm.getAlias());
                comm.setParameter("_EXECTYPE","proc");
                //System.out.println("Set Alias to: " + hcomm.getAlias());
            }
            comm.setParameter("_BASESQL", hcomm.getCommand());
            return comm;
        }        
        if(type.equals("prepare")){
        	comm.setParameter("_BASESQL", hcomm.getCommand());
        	comm.setParameter("_PREPARE_PARAM_NAMES", hcomm.getPrepared_params());
        	comm.setParameter("_EXECTYPE","prepare");
        	return comm;
        }
        if (type.equals("replace")) {
            return replacedCommand(command);
        }
        if(type.equals("passthrough")){
        	comm.setParameter("__TYPE__", hcomm.getOptype());
        	comm.setParameter("__RETURNTYPE__", hcomm.getReturnInType());
        	return comm;
        }
        if (type.equals("groovy")) {
            try {
                Class cl = Class.forName("org.jini.projects.athena.scripting.groovy.GroovyEnvironment");
                Environment env = (Environment) cl.newInstance();
                env.initEnvironment();
                System.out.println("Setting script to: " + hcomm.getAlias());
                env.setScript(new File(hcomm.getAlias()));
                System.out.println("EnvironmentClass: " + env.getClass().getName());
                return env;
            } catch (Exception ex) {
                System.err.println(new java.util.Date() + ": CHI: Class Instantiation Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Tells the handler to move to the next command in the handler set
     *
     * @return boolean true if another command is available to run
     */

    public boolean nextCommand() {
        currentCommIdx++;
        if (currentCommIdx >= commands.size())
            return false;
        else
            return true;
    }

    /**
     * Gets the known name of the handler
     *
     * @return the handler name
     * @since
     */
    public String getHandlerName() {
        return this.handler_name;
    }

    /**
     * Obtain the runtime tuning settings Returns the set of parameters that
     * the runtime should apply in order to optimixe the execution of this
     * handler.
     */
    public HashMap getTuningParameters() {
        return this.tuning;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Handles the 'replace' command type. Delegates to the <code>TypeEngine<code> class to process the string.
     *       *@return    Description of the Returned Value
     *@see TypeEngine
     *@since
     */
    private String replacedCommand(StringBuffer command) throws ValidationException {                
        TypeEngine eng = TypeEngine.getTypeEngine();
        ParameterValidator parm = (ParameterValidator) this.validation;
        eng.setSymbolTable(parm.getSymbolTable());        
        String ret = eng.process(command.toString().toLowerCase(), comm.getParameters());        
        TypeEngine.returnEngine(eng);
        return ret.trim();
    }
   
    /**
     * Whether the results of this handler should be cached
     * @return caching
     */
    public boolean isToBeCached() {
        return nocache;
    }

    /**
     * @param b
     */
    public void setCache(boolean b) {
        nocache = b;
    }

}
