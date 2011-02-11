/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 09-Jul-2002
 * Time: 14:28:59
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.harness.commands;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.command.CompoundType;
import org.jini.projects.athena.harness.CommandHarness;


public class storedproc_harness implements CommandHarness {
    public void populateCommand(Command comm) {

        comm.clear();
        comm.setCallName("insertts");
        CompoundType comp = comm.createCompoundType("ob");
        comp.setField("val", new Integer(101));
        comp.setField("date", "12-DEC-2002");
    }
}
