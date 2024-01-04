package gov.ca.dwr.wrims.calsimshp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;

public class CalSimShpFeature extends SimpleFeatureImpl {

	private DSSResolver dssResolver = null;
	
	public CalSimShpFeature(Object[] values, SimpleFeatureType featureType, FeatureId id, DSSResolver dssResolver) {
		super(values, featureType, id, false);
		this.dssResolver = dssResolver;
	}

    public Object getAttribute(int index) throws IndexOutOfBoundsException {
    	Object baseValue="0";
   		dssResolver.resolve(values, index, baseValue);
   		if(index >= values.length) {
   			System.out.println("foo");
   		}
        return values[ index ];
    }
    
    public List<Object> getAttributes() {
   		dssResolver.resolveAll(values);
        return new ArrayList<Object>(Arrays.asList( values ));
    }

	
}
