package gov.ca.dwr.wrims.calsimshp;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.store.ContentDataStore;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

public class CalSimShpDataStore extends ContentDataStore {
	
	public enum CalSimType {
		ARC, NODE;

		public static CalSimType fromString(String str) {
			return valueOf(str.toUpperCase());
		}
	}

	protected CalSimType type;
	
    /** Memory holds Map of Feature by fid. */
    protected Map<String,CalSimShpFeature> memory;

    /** Schema holds FeatureType by typeName */
    protected SimpleFeatureType schema;

	private URL url;

	public CalSimShpDataStore(URL url, CalSimType type, String idFieldName, String typeFieldName, String subTypeFieldName) throws IOException {
		super();
		this.url = url;
		this.type = type;
		
		ShapefileDataStore shpds = null;
		FeatureReader<SimpleFeatureType, SimpleFeature> reader = null;
		try {
	    	shpds = new ShapefileDataStore(url);
	    	reader = shpds.getFeatureReader();
	    	
	    	SimpleFeatureType shpType = shpds.getSchema();
	    	SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
	    	
	    	typeBuilder.setName(shpType.getTypeName() + "_dss");
	    	typeBuilder.setCRS(shpType.getGeometryDescriptor().getCoordinateReferenceSystem());
	    	for(AttributeDescriptor attribute : shpType.getAttributeDescriptors()) {
	    		typeBuilder.add(attribute);
	    	}
	    	typeBuilder.nillable(true).add("dss1", Double.class);
	    	typeBuilder.nillable(true).add("dss2", Double.class);
	    	typeBuilder.nillable(true).add("dss3", Double.class);
	    	typeBuilder.nillable(true).add("dss4", Double.class);
	    	schema = typeBuilder.buildFeatureType();
	    	
	    	memory = new HashMap<String,CalSimShpFeature>();
	    	while(reader.hasNext()) {
	    		SimpleFeature baseFeature = reader.next();
	    		Object[] values = new Object[baseFeature.getAttributeCount() + 4];
	    		for(int i = 0; i < baseFeature.getAttributeCount(); i++) {
	    			values[i] = baseFeature.getAttribute(i);
	    		}
	    		DSSResolver dssResolver = new DSSResolver(shpType.getAttributeCount(), (String)baseFeature.getAttribute(idFieldName));
	    		CalSimShpFeature f = new CalSimShpFeature(values, schema, baseFeature.getIdentifier(), dssResolver);
	    		memory.put(f.getID(), f);
	    	}
		} finally {
			if(reader != null) {
				reader.close();
			}
			if(shpds != null) {
				shpds.dispose();
			}
		}
	}
	
	@Override
	protected List<Name> createTypeNames() throws IOException {
		String name = url.getPath();
		int pos = name.lastIndexOf('/');
		if(pos >=0 ) {
			name = name.substring(pos+1);
		}
		pos = name.lastIndexOf('.');
		if(pos >= 0) {
			name = name.substring(0, name.lastIndexOf('.'));
		}

        Name typeName = new NameImpl( name );
        return Collections.singletonList(typeName);
	}

	@Override
	protected ContentFeatureSource createFeatureSource(ContentEntry entry)
			throws IOException {
		return new CalSimShpFeatureSource(entry, null);
	}

}
