/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package wrimsv2_plugin.debugger.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;

import java.net.URL;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugUIPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static DebugUIPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	private final static String ICONS_PATH = "icons/full/";//$NON-NLS-1$
	private final static String PATH_OBJECT = ICONS_PATH + "obj16/"; //Model object icons //$NON-NLS-1$
    private final static String PATH_ELOCALTOOL = ICONS_PATH + "elcl16/"; //Enabled local toolbar icons //$NON-NLS-1$
    private final static String PATH_DLOCALTOOL = ICONS_PATH + "dlcl16/"; //Disabled local toolbar icons //$NON-NLS-1$
    
    /**
     *  Toolbar action to pop data stack
     */
    public final static String IMG_ELCL_POP = "IMG_ELCL_POP";
    public final static String IMG_DLCL_POP = "IMG_DLCL_POP";
    
    /**
     * Toolbar action to push onto data stack
     */
    public final static String IMG_ELCL_PUSH = "IMG_ELCL_PUSH";
    public final static String IMG_DLCL_PUSH = "IMG_DLCL_PUSH";
    
    /**
     * WPP program image
     */
    public final static String IMG_OBJ_WPP = "IMB_OBJ_WPP";
    	
	/**
	 * The constructor.
	 */
	public DebugUIPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebugUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebugUIPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("example.debug.ui.DebugUIPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		declareImage(IMG_OBJ_WPP, PATH_OBJECT + "wpp.gif");
	    declareImage(IMG_ELCL_POP, PATH_ELOCALTOOL + "pop.gif");
	    declareImage(IMG_DLCL_POP, PATH_DLOCALTOOL + "pop.gif");
	    declareImage(IMG_ELCL_PUSH, PATH_ELOCALTOOL + "push.gif");
	    declareImage(IMG_DLCL_PUSH, PATH_DLOCALTOOL + "push.gif");
	}
	
    /**
     * Declares a workbench image given the path of the image file (relative to
     * the workbench plug-in). This is a helper method that creates the image
     * descriptor and passes it to the main <code>declareImage</code> method.
     * 
     * @param symbolicName the symbolic name of the image
     * @param path the path of the image file relative to the base of the workbench
     * plug-ins install directory
     * <code>false</code> if this is not a shared image
     */
    private void declareImage(String key, String path) {
        URL url = BundleUtility.find("example.debug.ui", path);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        getImageRegistry().put(key, desc);
    }
    
 }
