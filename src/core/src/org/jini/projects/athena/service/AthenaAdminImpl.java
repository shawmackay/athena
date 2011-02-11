package org.jini.projects.athena.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.JoinManager;

import org.jini.projects.athena.command.HandleEngine;
import org.jini.projects.athena.command.dialect.DialectEngine;
import org.jini.projects.athena.command.types.TypeEngine;
/**
 * Implementation for Athena specific administration interface, JoinAdministration and DestroyAdmin
 * 
 * @author calum
 *
 */

public class AthenaAdminImpl implements AthenaAdminProxy {
    LookupDiscoveryManager ldm;
    JoinManager jm;
    Logger l = Logger.getLogger("org.jini.projects.athena.service");
    termthread termthr = new termthread();
    private boolean term = false;

    public AthenaAdminImpl(JoinManager jm, LookupDiscoveryManager ldm) throws RemoteException {
        this.jm = jm;
        this.ldm = ldm;
        termthr.start();
    }

    public void destroy() throws RemoteException {
        l.info("Remote Termination");
        //this.terminate();
        term = true;
        try {
            Thread.sleep(1);
        } catch (Exception ex) {
        }


    }

    private class termthread extends Thread {
        /**
         *  Main processing method for the termthread object
         *
         *@since
         */
        public void run() {
            while (term == false) {
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                }
            }

            jm.terminate();
            ldm.terminate();
            System.exit(0);
            // the termination will be fired in the Shutdown hooks


        }
    }


    public void addLookupAttributes(Entry[] entries) throws RemoteException {
        jm.addAttributes(entries, true);
    }

    public void addLookupGroups(String[] strings) throws RemoteException {
        try {
            ldm.addGroups(strings);
        } catch (IOException e) {
        }
    }

    public void addLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.addLocators(lookupLocators);
    }

    public Entry[] getLookupAttributes() throws RemoteException {
        return jm.getAttributes();

    }

    public String[] getLookupGroups() throws RemoteException {
        return ldm.getGroups();
    }

    public LookupLocator[] getLookupLocators() throws RemoteException {
        return ldm.getLocators();
    }

    public void modifyLookupAttributes(Entry[] entries, Entry[] entries1) throws RemoteException {
        jm.modifyAttributes(entries, entries1, true);
    }

    public void removeLookupGroups(String[] strings) throws RemoteException {
        ldm.removeGroups(strings);
    }

    public void removeLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.removeLocators(lookupLocators);
    }

    public void setLookupGroups(String[] strings) throws RemoteException {
        try {
            ldm.setGroups(strings);
        } catch (IOException e) {
        }
    }

    public void setLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.setLocators(lookupLocators);
    }

    public void hibernate() throws RemoteException {
        SystemManager.inform(HostEvents.HIBERNATE);
    }

    public void wake() throws RemoteException {
        SystemManager.inform(HostEvents.WAKE);
    }

    public Map getTypeInformation() throws RemoteException {
        TypeEngine eng = TypeEngine.getTypeEngine();
        Map m = eng.getTypes();
        TypeEngine.returnEngine(eng);
        return m;
    }

    /* @see org.jini.projects.org.jini.projects.athena.service.AthenaAdmin#getHandlerDetails()
     */
    public Map getHandlerDetails() throws RemoteException {
        // TODO Complete method stub for getHandlerDetails
        return HandleEngine.getEngine().getHandlerDefinitions();
    }
	/* @see org.jini.projects.athena.service.AthenaAdmin#getDialectDetails()
	 */
	public Map getDialectDetails() throws RemoteException {
		// TODO Complete method stub for getDialectDetails
		return DialectEngine.getEngine().getDialectDefinitions();
	}
}
