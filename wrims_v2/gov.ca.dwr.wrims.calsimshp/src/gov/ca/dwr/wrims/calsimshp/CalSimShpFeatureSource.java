package gov.ca.dwr.wrims.calsimshp;

import java.io.IOException;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

@SuppressWarnings("unchecked")
public class CalSimShpFeatureSource extends ContentFeatureSource {

	public CalSimShpFeatureSource(ContentEntry entry, Query query) {
		super(entry, query);
	}

    public CalSimShpDataStore getDataStore() {
        return (CalSimShpDataStore) super.getDataStore();
    }
    
	@Override
	protected ReferencedEnvelope getBoundsInternal(Query query)
			throws IOException {
		return null;
	}

	@Override
	protected int getCountInternal(Query query) throws IOException {
		if(query.getFilter() == Filter.INCLUDE) {
			// TODO return the count of all features
			return -1;
		}
		return -1;
	}

	@Override
	protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(
			Query query) throws IOException {
		return new CalSimShpFeatureReader(getState(), query);
	}

	@Override
	protected SimpleFeatureType buildFeatureType() throws IOException {
		return getDataStore().schema;
	}

}
