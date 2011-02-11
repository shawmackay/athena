/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 09-Jul-2002
 * Time: 14:27:31
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.harness;

import org.jini.projects.athena.command.Command;

public interface CommandHarness {
    public void populateCommand(Command comm);
}
