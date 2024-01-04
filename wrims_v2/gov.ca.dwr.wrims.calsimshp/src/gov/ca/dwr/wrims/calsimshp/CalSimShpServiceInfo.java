/**
 * 
 */
package gov.ca.dwr.wrims.calsimshp;

import java.net.URI;
import java.net.URISyntaxException;

import org.locationtech.udig.catalog.IServiceInfo;

class CalSimShpServiceInfo extends IServiceInfo {

	private final CalSimShpServiceImpl service;

	CalSimShpServiceInfo(CalSimShpServiceImpl serviceImpl) {
		super();
		service = serviceImpl;
        keywords = new String[] { ".shp", "Shapefile", "CalSim" }; //$NON-NLS-1$ //$NON-NLS-2$                                             
		try {
			schema = new URI("shp://www.opengis.net/gml"); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			CalSimShpPlugin.log(null, e);
			schema = null;
		}
		title = service.getID().toString();
		title = title.replace("%20"," ");        		  //$NON-NLS-1$//$NON-NLS-2$
	}

	public String getDescription() {
		return service.getIdentifier().toString();
	}

	public String getTitle() {
	    return title;
	}
}
