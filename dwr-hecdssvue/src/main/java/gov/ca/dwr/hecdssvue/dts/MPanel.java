/*
 * Copyright (c) 2024
 * United States Army Corps of Engineers - Hydrologic Engineering Center (USACE/HEC)
 * All Rights Reserved.  USACE PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval from HEC
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
