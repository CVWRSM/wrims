
package gov.ca.dwr.wrims.calsimshp;

import gov.ca.dwr.wrims.tokenreplacer.MapTokenResolver;
import gov.ca.dwr.wrims.tokenreplacer.TokenReplacingReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.sld.v1_1.SLDConfiguration;
import org.geotools.styling.NamedStyle;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.Parser;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Connect to a shapefile.
 * 
 * @author Chris Hodgson, Refractions Research
 */
public class CalSimShpGeoResourceImpl extends IGeoResource {
    CalSimShpServiceImpl parent;
    String typename = null;
    private URL identifier;
    private ID id;

    /**
     * Construct <code>ShpGeoResourceImpl</code>.
     *
     * @param parent
     * @param typename
     */
    public CalSimShpGeoResourceImpl( CalSimShpServiceImpl parent, String typename ) {
        this.service = parent;
        this.parent = parent;
        this.typename = typename;
        try {
            identifier = new URL(parent.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
            id = new ID(parent.getID(), typename);
        } catch (MalformedURLException e) {
            identifier = parent.getIdentifier();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }
    public ID getID() {
        return id;
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        return parent.getStatus();
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        return parent.getMessage();
    }

    /*
     * Required adaptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     * @see org.locationtech.udig.catalog.IResolve#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) {
            return null;
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = featureSource(monitor);
            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = featureSource(monitor);
            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            return adaptee.cast(featureSource(monitor));
        }
//        if (adaptee.isAssignableFrom(IndexedShapefileDataStore.class)) {
//            return adaptee.cast(parent.getDS(monitor));
//        }
        if (adaptee.isAssignableFrom(Style.class)) {
            Style style = style(monitor);
            if (style != null) {
                return adaptee.cast(style(monitor));
            }
            // proceed to ask the super class, someone may
            // of written an IResolveAdapaterFactory providing us
            // with a style ...
        }
        return super.resolve(adaptee, monitor);
    }

    private SimpleFeatureSource featureSource( IProgressMonitor monitor ) throws IOException {
        return parent.getDS(monitor).getFeatureSource(parent.getDS(monitor).getTypeNames()[0]);
    }
    
    public Style style( IProgressMonitor monitor ) throws IOException {
        SimpleFeatureSource source  = featureSource(null);

        SimpleFeatureType featureType = source.getSchema();
        Map<String, Serializable> params = parent.getConnectionParams();
        String type = (String)params.get(CalSimShpServiceExtension.TYPE_KEY);
        URL url = new URL("platform:/plugin/" + CalSimShpPlugin.ID + "/resources/calsim_" + type.toLowerCase() + ".sld");
        StyledLayerDescriptor sld = parseSLD(url);
        if( sld == null ){
            return null; // well that is unexpected since f.exists()
        }
        
        Style[] styles = SLDs.styles( sld );
        
        // put the first one on
        if (styles != null && styles.length > 0) {
            Style style = SLDs.matchingStyle(styles, featureType);
            if (style == null) {
                style = styles[0];
            }
            return style;
        }
        return null; // well nothing worked out; make your own style
    }

    public StyledLayerDescriptor parseSLD(URL url) throws IOException {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
     // try SLD 1.1 first
        SLDConfiguration config = new SLDConfiguration();
        Reader reader = null;
        try {
            Parser parser = new Parser( config );
            reader = SldUrlToInputStream(url);
            Object object = parser.parse( reader );
            if( object instanceof StyledLayerDescriptor){
                StyledLayerDescriptor sld = (StyledLayerDescriptor) object;
                return sld;
            }
            else if ( object instanceof NamedStyle ){
                NamedStyle style = (NamedStyle) object;
                StyledLayerDescriptor sld = SLDs.createDefaultSLD( style );
                return sld;
            }
        }
        catch(Exception ignore){
            // we are ignoring this error and will try the more forgiving option below
            UiPlugin.trace(SLDs.class,"SLD 1.1 configuration failed to parse "+url, ignore);
        }
        finally {
            if( reader != null){
                reader.close();
            }
        }
        // parse it up
        SLDParser parser = new SLDParser(styleFactory);
        try {
            reader = SldUrlToInputStream(url);
            parser.setInput(reader);
            StyledLayerDescriptor sld = parser.parseSLD();
            return sld;
        } catch (FileNotFoundException e) {
            return null; // well that is unexpected since f.exists()
        }
    }
    

    private Reader SldUrlToInputStream(URL url) throws IOException {
    	InputStream inputStream = url.openConnection().getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        
        Map<String, Serializable> params = parent.getConnectionParams();
		Map<String,String> tokenMap = new HashMap<String,String>();
		tokenMap.put("idField", (String)params.get(CalSimShpServiceExtension.ID_FIELD_NAME_KEY));
		tokenMap.put("typeField", (String)params.get(CalSimShpServiceExtension.TYPE_FIELD_NAME_KEY));
		tokenMap.put("subTypeField", (String)params.get(CalSimShpServiceExtension.SUB_TYPE_FIELD_NAME_KEY));
		MapTokenResolver tokenResolver = new MapTokenResolver(tokenMap);
		TokenReplacingReader trr = new TokenReplacingReader(isr, tokenResolver);
		return trr;
    }
    
    /**
     * Helper method performing the same function as service( monitor ) without the
     * monitor or chance of IOException. 
     * <p>
     * @return ShpServiceImpl responsible for this ShpGeoResourceImpl
     */
    public CalSimShpServiceImpl service() {
        return parent;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class) || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(FeatureSource.class) 
                || adaptee.isAssignableFrom(SimpleFeatureSource.class) 
                || adaptee.isAssignableFrom(IService.class) 
                || adaptee.isAssignableFrom(Style.class)
                ) 
                || super.canResolve(adaptee);
    }
    @Override
    public CalSimShpGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (CalSimShpGeoResourceInfo) super.getInfo(monitor);
    }
    protected CalSimShpGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return new CalSimShpGeoResourceInfo(this);
    }
}
