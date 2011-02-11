/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 11-Jun-02
 * Time: 15:43:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.ui;

import javax.swing.JLabel;



public class ConnLabel extends JLabel {
    private SimpleConnectionListRenderer renderer;

    public ConnLabel(SimpleConnectionListRenderer renderer) {
        this.renderer = renderer;
    }


}
