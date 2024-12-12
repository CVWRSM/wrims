/*
 * Copyright (c) 2024
 * United States Army Corps of Engineers - Hydrologic Engineering Center (USACE/HEC)
 * All Rights Reserved.  USACE PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval from HEC
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * A default frame understands MPanel's. It queries
 * the panel for its menu bar and adds it to itself.
 *
 * @author Nicky Sandhu
 * @version $Id: DefaultFrame.java,v 1.1.4.5 2000/12/20 20:07:05 amunevar Exp $
 */
public class DefaultFrame extends JFrame {
    /**
     * creates a frame with the given MPanel and sets
     * its menu bar accordingly
     *
     * @see MPanel
     */
    public DefaultFrame(MPanel panel) {
        getContentPane().setLayout(new BorderLayout());
        setMPanel(panel);
    }

    /**
     * sets the main panel to this MPanel
     *
     * @see MPanel
     */
    public void setMPanel(MPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        setJMenuBar(panel.getJMenuBar());
        setTitle(panel.getFrameTitle());
    }
}
