/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package gov.ca.dwr.wrims.calsimshp;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CalSimShpPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CalSimShpPlugin plugin;

    public static final String ID = "gov.ca.dwr.wrims.calsimshp"; //$NON-NLS-1$

    /**
	 * The constructor.
	 */
	public CalSimShpPlugin() {
		super();
		plugin = this;
	}

	/**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
    }

	/**
     * This method is called when the plug-in is stopped
     */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return x
	 */
	public static CalSimShpPlugin getDefault() {
		return plugin;
	}

	public static void log(String msg) {
	      log(msg, null);
	}
	
	public static void log(String msg, Exception e) {
		plugin.getLog().log(new Status(Status.INFO, ID, Status.OK, msg, e));
	}
	
}
