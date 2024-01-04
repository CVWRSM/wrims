package gov.ca.dwr.wrims.calsimshp;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;

/**
 * This class appears to only be required in order to reference the WizardPage in the plugin.xml
 *  
 * @author chodgson
 *
 */
public class CalSimShpConnectionFactory extends UDIGConnectionFactory {

	@Override
	public Map<String, Serializable> createConnectionParameters(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL createConnectionURL(Object context) {
		// TODO Auto-generated method stub
		return null;
	}

}
