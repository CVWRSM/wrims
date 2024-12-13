/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
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


