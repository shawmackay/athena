/*
 * Created on 17-Mar-2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.jini.projects.athena.util.builders;

/**
 * @author calum
 */
public class LogExceptionWrapper extends Exception {

    private boolean severe = false;
    private boolean warning = false;

    /**
     *
     */
    public LogExceptionWrapper() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public LogExceptionWrapper(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public LogExceptionWrapper(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public LogExceptionWrapper(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public LogExceptionWrapper(String message, Throwable cause, boolean severe, boolean warning) {
        super(message, cause);
        // TODO Auto-generated constructor stub
        this.severe = severe;
        this.warning = warning;
    }

    /**
     * @param cause
     */
    public LogExceptionWrapper(Throwable cause, boolean severe, boolean warning) {
        super(cause);
        this.severe = severe;
        this.warning = warning;
        // TODO Auto-generated constructor stub
    }

    public boolean isWarning() {
        return warning;
    }

    public boolean isSevere() {
        return severe;
    }

    /**
     * Sets the severe.
     * @param severe The severe to set
     */
    public void setSevere(boolean severe) {
        this.severe = severe;
    }

    /**
     * Sets the warning.
     * @param warning The warning to set
     */
    public void setWarning(boolean warning) {
        this.warning = warning;
    }

}
