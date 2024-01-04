package gov.ca.dwr.wrims.calsimshp;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.store.ContentState;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class CalSimShpFeatureReader implements
		FeatureReader<SimpleFeatureType, SimpleFeature> {
	
    /** State used when reading file */
    protected ContentState state;
	private Iterator<CalSimShpFeature> iterator;

    public CalSimShpFeatureReader(ContentState contentState, Query query) throws IOException {
    	this.state = contentState;
    	CalSimShpDataStore store = (CalSimShpDataStore) state.getEntry().getDataStore();
        iterator = store.memory.values().iterator();
    }
    
	@Override
	public SimpleFeatureType getFeatureType() {
		return state.getFeatureType();
	}

	@Override
	public SimpleFeature next() throws IOException, NoSuchElementException {
		if (iterator == null) {
			throw new IOException("Feature Reader has been closed");
		}
		return iterator.next();
	}

	@Override
	public boolean hasNext() throws IOException {
		return (iterator != null) && iterator.hasNext();	}

	@Override
	public void close() throws IOException {
		if (iterator != null) {
			iterator = null;
		}
	}

}
