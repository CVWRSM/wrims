/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package gov.ca.dwr.wrims.calsimshp;

import gov.ca.dwr.wrims.calsimshp.CalSimShpDataStore.CalSimType;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.memory.MemoryDataStore;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

/**
 * Connect to a shapefile, load it into a MemoryDatastore, add some columns for DSS data.
 * 
 * @author Chris Hodgson, Refractions Research
 */
@SuppressWarnings("restriction")
public class CalSimShpServiceImpl extends IService {
    //private URL url;
    private ID id;
    private Map<String, Serializable> params = null;

    private Throwable msg = null;
    /**
     * Volatile cache of dataStore if created.
     */
    volatile CalSimShpDataStore ds = null;
    protected final Lock rLock = new UDIGDisplaySafeLock();
    private final static Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    /**
     * Construct <code>ShpServiceImpl</code>.
     * 
     * @param arg1
     * @param arg2
     */
    public CalSimShpServiceImpl(URL url, Map<String, Serializable> params) {
        //this.url = url;
        if( url == null ){
            throw new NullPointerException("CalSimShpService requres a URL");
        }
        try {
            id = new ID(url);
        } catch (Throwable t) {
            throw new IllegalArgumentException("Unable to create ID from:"+url, t);
        }
        this.params = params;
    }

    /*
     * Required adaptions: <ul> <li>IServiceInfo.class <li>List.class <IGeoResource> </ul>
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(MemoryDataStore.class)) {
            return adaptee.cast(getDS(monitor));
    	}
        return super.resolve(adaptee, monitor);
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return adaptee.isAssignableFrom(CalSimShpDataStore.class) 
        		|| super.canResolve(adaptee);
    }

    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if (members != null){
            members = null;
        }
        if( ds != null ){
            ds.dispose();
            ds = null;
        }
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<CalSimShpGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            getDS(monitor); // slap it to load datastore
            rLock.lock();
            try {
                if (members == null) {
                    members = new LinkedList<CalSimShpGeoResourceImpl>();
                    String[] typenames = ds.getTypeNames();
                    if (typenames != null)
                        for( int i = 0; i < typenames.length; i++ ) {
                            CalSimShpGeoResourceImpl shpGeoResource = new CalSimShpGeoResourceImpl(this, typenames[i]);
                            members.add(shpGeoResource);
                        }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }

    private volatile List<CalSimShpGeoResourceImpl> members = null;

    @Override
    public CalSimShpServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (CalSimShpServiceInfo) super.getInfo(monitor);
    }

    /*
     * @see org.locationtech.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
       CalSimShpDataStore dataStore = getDS(monitor); // load ds
        if (dataStore == null) {
            return null; // could not connect
        }
        rLock.lock();
        try {
            return new CalSimShpServiceInfo(this);
        } finally {
            rLock.unlock();
        }
    }

    /*
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    CalSimShpDataStore getDS( IProgressMonitor monitor ) throws IOException {
        if (ds == null) {
            dsInstantiationLock.lock();
            try {
                if (ds == null) {
                    try {
                    	ds = new CalSimShpDataStore((URL)params.get(CalSimShpServiceExtension.FILE_KEY),
                    			CalSimType.fromString((String)params.get(CalSimShpServiceExtension.TYPE_KEY)),
                    			(String)params.get(CalSimShpServiceExtension.ID_FIELD_NAME_KEY),
                    			(String)params.get(CalSimShpServiceExtension.TYPE_FIELD_NAME_KEY),
                    			(String)params.get(CalSimShpServiceExtension.SUB_TYPE_FIELD_NAME_KEY));
                    } catch (IOException e) {
                    	e.printStackTrace();
                        throw new IOException(e);
                    }
                }
            } finally {
                dsInstantiationLock.unlock();
            }
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ResolveChangeEvent event = new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta);
            fire(event);
        }
        return ds;
    }

    private void fire( ResolveChangeEvent event ) {
        ICatalog catalog = parent(new NullProgressMonitor());
        if (catalog instanceof CatalogImpl) {
            ((CatalogImpl) catalog).fire(event);
        }
    }

//    private void openIndexGenerationDialog( final ShapefileDataStore ds ) {
//        rLock.lock();
//        try {
//            if (ds instanceof IndexedShapefileDataStore) {
//                IndexedShapefileDataStore ids = (IndexedShapefileDataStore) ds;
//                if (ids.isIndexed())
//                    return;
//                String name = getIdentifier().getFile();
//                int lastIndexOf = name.lastIndexOf(File.separator);
//                if (lastIndexOf > 0)
//                    name = name.substring(lastIndexOf + 1);
//                final String finalName = name;
//                IRunnableWithProgress runnable = new IRunnableWithProgress(){
//
//                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
//                        monitor.beginTask(Messages.ShpPreferencePage_createindex + " " + finalName, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
//                        index(ds, ds.getTypeNames()[0]);
//                        monitor.done();
//                    }
//
//                };
//                if (PlatformUI.getWorkbench().isClosing() && false) {
//                    try {
//                        runnable.run(new NullProgressMonitor());
//                    } catch (InvocationTargetException e) {
//                        ShpPlugin.log("", e); //$NON-NLS-1$
//                    } catch (InterruptedException e) {
//                        ShpPlugin.log("", e); //$NON-NLS-1$
//                    }
//                } else {
//                    PlatformGIS.runInProgressDialog(Messages.ShpServiceImpl_indexing + " " + finalName, true, runnable, false); //$NON-NLS-1$
//                }
//            }
//        } finally {
//            rLock.unlock();
//        }
//
//    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if( ds == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return id.toURL();
    }
    
    @Override
    public ID getID() {
        return id;
    }
    
    /**
     * The File as indicated in the connection parameters, may be null if we are representing a web
     * resource.
     * 
     * @return file as indicated in the connection parameters, may be null if we are reprsenting a
     *         web resource
     */
//    public File toFile() {
//        Map<String, Serializable> parametersMap = getConnectionParams();
//        URL url = (URL) parametersMap.get(ShapefileDataStoreFactory.URLP.key);
//        return URLUtils.urlToFile(url);
//    }
}
