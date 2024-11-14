
package gov.ca.dwr.wresl.xtext.editor;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class WreslEditorStandaloneSetup extends WreslEditorStandaloneSetupGenerated{

	public static void doSetup() {
		new WreslEditorStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

