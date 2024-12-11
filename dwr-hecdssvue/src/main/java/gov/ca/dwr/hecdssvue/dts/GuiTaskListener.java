/*
 * Copyright (c) 2024
 * United States Army Corps of Engineers - Hydrologic Engineering Center (USACE/HEC)
 * All Rights Reserved.  USACE PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval from HEC
 */

package gov.ca.dwr.hecdssvue.dts;

import vista.gui.CursorChangeListener;

/**
 * Task listener for the Gui package which determines cursor changes and status messages
 *
 * @author Nicky Sandhu ,Armin Munevar
 * @version $Id: GuiTaskListener.java,v 1.1.2.8 2001/07/12 01:59:38 amunevar Exp $
 */
public abstract class GuiTaskListener extends CursorChangeListener {

    /**
     *
     */
    public void doPreWork() {
        super.doPreWork();
    }

    /**
     *
     */
    public void doPostWork() {
        super.doPostWork();
    }
}


