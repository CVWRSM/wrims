package wrimsv2_plugin.debugger.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.framework.BundleContext;

import wrimsv2_plugin.debugger.exception.WPPException;


public class DebuggerPreferenceInitializer extends
		AbstractPreferenceInitializer {

	//unused class. To use need add extension and turn on initializer in DebugCorePlugin.start(BundleContext context)
	public DebuggerPreferenceInitializer() {
        super();
	}

	@Override
	public void initializeDefaultPreferences() {
		File file=new File (DebugCorePlugin.preferenceFile);
		try {
			Platform.getPreferencesService().importPreferences(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			WPPException.handleException(e);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}

}
