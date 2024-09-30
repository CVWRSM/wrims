package gov.ca.dwr.hecdssvue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;
import java.util.prefs.Preferences;

import gov.ca.dwr.hecdssvue.components.NumericTextField;
import hec.heclib.dss.HecDSSFileAccess;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.swixml.SwingEngine;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.ca.dwr.hecdssvue"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		DssPluginCore._preferences = Preferences.userNodeForPackage(getClass());
		HecDSSFileAccess.setMessageLevel(0);
		DssPluginCore.swix = new SwingEngine(this);
		DssPluginCore.swix.getTaglib().registerTag("numtextfield", NumericTextField.class);
		DssPluginCore.swix.render(new File(DebugCorePlugin.dataDir, "GUI.xml"));
		DssPluginCore.CalLiteLookups=readInLookups("GUI_Links3_CalLite.table");
		DssPluginCore.CalSim2Lookups=readInLookups("GUI_Links3_CalSim2.table");
		DssPluginCore.CalSim3Lookups=readInLookups("GUI_Links3_CalSim3.table");
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private String[][] readInLookups(String fn) {

		// Open input file
		String lookups[][]=new String[0][0];
		
		Scanner input;
		try {
			input = new Scanner(new FileReader(DebugCorePlugin.dataDir+"\\"+fn));
		} catch (FileNotFoundException e) {
			WPPException.handleException(e);
			return lookups;
		}

		Vector<String> allLookups = new Vector<String>();

		int lineCount = 0;
		input.nextLine(); // Skip header line
		while (input.hasNextLine()) {
			String line = input.nextLine();
			allLookups.add(line);
			lineCount++;
		}
		input.close();
		lookups = new String[lineCount][6];
		for (int i = 0; i < lineCount; i++) {
			String[] parts = allLookups.get(i).split("[\t]+");
			for (int j = 0; j < 6; j++) {
				if (parts[j].equals("null"))
					parts[j] = "";
				lookups[i][j] = parts[j];
			}
		}

		return lookups;
	}
}
