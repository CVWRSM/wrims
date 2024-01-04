/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package gov.ca.dwr.wrims.calsimshp;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;

/**
 * Service Extension implementation for Shapefiles.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class CalSimShpServiceExtension implements ServiceExtension {

    public static final String FILE_KEY = "gov.ca.dwr.wrims.calsimshp.file_key";
    public static final String ID_FIELD_NAME_KEY = "gov.ca.dwr.wrims.calsimshp.id_field_name_key";
    public static final String TYPE_FIELD_NAME_KEY = "gov.ca.dwr.wrims.calsimshp.type_field_name_key";
    public static final String SUB_TYPE_FIELD_NAME_KEY = "gov.ca.dwr.wrims.calsimshp.sub_type_field_name_key";
    public static final String TYPE_KEY = "gov.ca.dwr.wrims.calsimshp.type_key";

    public IService createService(URL id, Map<String,Serializable> params) {
        if(!validateParams(params)) {
        	return null;
        }
        
        if(id == null) {
        	return new CalSimShpServiceImpl(createUrl(params), params);
        } else {
        	return new CalSimShpServiceImpl(id, params);
        }
    }
    
    private boolean validateParams(Map<String,Serializable> params) {
        URL fileUrl = (URL)params.get(FILE_KEY);
        if(fileUrl == null || !fileUrl.toString().toLowerCase().endsWith(".shp")) {
           	return false;
        }
        String type = (String)params.get(TYPE_KEY);
        if(type == null || type.isEmpty()) {
        	return false;
        }
    	return true;
    }
    
    /**
     * Params must already be validated using validateParams.
     * @param params
     * @return
     */
    public static URL createUrl(Map<String,Serializable> params) {
		return (URL) params.get(FILE_KEY);
    }

    public Map<String,Serializable> createParams(URL url) {
    	Map<String,Serializable> params = new HashMap<String,Serializable>();
    	params.put(FILE_KEY, url);
    	if(validateParams(params)) {
    		return params;
    	}
    	return null;
    }

}
