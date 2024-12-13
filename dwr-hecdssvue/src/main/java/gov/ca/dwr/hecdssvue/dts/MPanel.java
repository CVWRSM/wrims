/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * This panel has a menu bar associated with it.
 * A frame adding this panel can query it for its
 * menu bar and add it to itself when showing this panel.
 *
 * @author Nicky Sandhu
 * @version $Id: MPanel.java,v 1.1.4.4 2000/12/20 20:07:17 amunevar Exp $
 */
public abstract class MPanel extends JPanel {
    /**
     * returns a menu bar associated with this panels components
     */
    public abstract JMenuBar getJMenuBar();

    /**
     * returns the title of the frame containing this panel. This could
     * be used to identify this panel by name as well.
     */
    public abstract String getFrameTitle();
}
