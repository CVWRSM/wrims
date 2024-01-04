package gov.ca.dwr.wrims.geoschematic;


import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.jdiagram.Activator;
import gov.ca.dwr.jdiagram.SchematicPluginCore;

import java.util.Collections;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.geotools.referencing.CRS;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.tool.display.ToolManager;
import org.locationtech.udig.project.ui.internal.tool.display.ToolProxy;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.project.ui.viewers.MapViewer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A map view.
 *
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc.)
 */
public class MapView extends ViewPart implements MapPart, IAdaptable {

    public static final String ID = "gov.ca.dwr.wrims.geoschematic.MapView"; //$NON-NLS-1$
    private static final String MAP_NAME = "Map Viewer";

	public static CoordinateReferenceSystem DEFAULT_CRS;
	static{
		try {
			DEFAULT_CRS = CRS.decode("EPSG:4326", true); //$NON-NLS-1$
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	} 

    private MapViewer mapviewer;
    private Map map;
	private DateSelector dateSelector;
	private Action forwardAction;
	private Action backwardAction;
    private String lastToolId;

    IPartListener2 partlistener = new IPartListener2(){
        public void partActivated( IWorkbenchPartReference partRef ) {
            if (partRef.getPart(false) == MapView.this){
                IToolManager tools = ApplicationGIS.getToolManager();
                tools.setCurrentEditor(MapView.this );
                selectLastTool();
            }
        }

        public void partBroughtToTop( IWorkbenchPartReference partRef ) {
        }

        public void partClosed( IWorkbenchPartReference partRef ) {
        }

        public void partDeactivated( IWorkbenchPartReference partRef ) {
        }

        public void partOpened( IWorkbenchPartReference partRef ) {
        }

        public void partHidden( IWorkbenchPartReference partRef ) {
        }

        public void partVisible( IWorkbenchPartReference partRef ) {
        }

        public void partInputChanged( IWorkbenchPartReference partRef ) {
        }

    };

    public MapView() {
        super();
    }

    @Override
    public void createPartControl( Composite parent ) {
        GridLayout layout = new GridLayout(1,false);
        layout.marginBottom=0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 2;
        layout.horizontalSpacing = 0;
        parent.setLayout(layout);

        // mapviewer = new MapViewer(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.MULTI);
        mapviewer = new MapViewer(parent, SWT.SINGLE | SWT.DOUBLE_BUFFERED);
        mapviewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create a new empty map
        // if you are going to add layers do so now
        // prior to adding to the mapviewer
        //
        Project p = ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject();
        map = (Map) ProjectFactory.eINSTANCE.createMap(p, "Default Map", Collections.EMPTY_LIST);
        map.setName(MAP_NAME);
        mapviewer.setMap(map);

        //set default crs
        map.getViewportModelInternal().setCRS(ViewportModel.BAD_DEFAULT);
        map.getViewportModelInternal().setCRS(DEFAULT_CRS);

        new MapInfoAreaComposite(parent, SWT.NONE, mapviewer);

        getSite().getWorkbenchWindow().getPartService().addPartListener(partlistener);
        setTool("org.locationtech.udig.tool.category.zoom");
		makeActions();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		dateSelector = new DateSelector(this);
		manager.add(dateSelector);
		manager.add(backwardAction);
		manager.add(forwardAction);
		//searchText=new SearchText(this);
		//manager.add(searchText);
	}

	//@SuppressWarnings("deprecation")
	private void makeActions() {
		forwardAction = new Action("Forward", Activator.getImageDescriptor("forward.png")){
			
			public void run(){
				int twSize=DssPluginCore._schematicTwSelections.size();
				Combo dateList = dateSelector.getDateList();
				int size=dateList.getItemCount();
				if (SchematicPluginCore.selIndex<twSize){
					if (twSize<size){
						SchematicPluginCore.selIndex=twSize;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}else{
					if (SchematicPluginCore.selIndex+1<size){
						SchematicPluginCore.selIndex = SchematicPluginCore.selIndex+1;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}
			}
			
		};
		
		backwardAction = new Action("Backward", Activator.getImageDescriptor("backward.png")){
			
			public void run(){
				int twSize=DssPluginCore._schematicTwSelections.size();
				Combo dateList = dateSelector.getDateList();
				int size=dateList.getItemCount();
				if (SchematicPluginCore.selIndex<=twSize){
					if (twSize<size){
						SchematicPluginCore.selIndex=twSize;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}else{
					if (SchematicPluginCore.selIndex>=size){
						SchematicPluginCore.selIndex = size-1;
						dateList.select(SchematicPluginCore.selIndex);
					}else{
						SchematicPluginCore.selIndex = SchematicPluginCore.selIndex-1;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}
			}
			
		};
	}

    public void setTool( String toolId ) {
        ToolProxy mi =((ToolManager)ApplicationGIS.getToolManager()).findToolProxy(toolId);
        if (mi != null){
            ApplicationGIS.getToolManager().getToolAction(mi.getId(), mi.getCategoryId()).run();
            if (mi.getType() == 1){
                //MODAL Tool
                this.lastToolId = mi.getId();
            }
        }
    }

    private void selectLastTool(){
        if (this.lastToolId != null){
            setTool(lastToolId);
        }
    }

    @Override
    public void setFocus() {
        mapviewer.getViewport().getControl().setFocus();
    }

    public Map getMap() {
        return mapviewer.getMap();
    }

    @Override
    public void dispose() {
        super.dispose();

        getSite().getWorkbenchWindow().getPartService().removePartListener(partlistener);

        if (mapviewer != null && mapviewer.getViewport() != null
                && getMap() != null) {
            mapviewer.getViewport().removePaneListener(
                    getMap().getViewportModelInternal());
        }
        if (mapviewer != null) {
            mapviewer.dispose();
        }
    }

    public void openContextMenu() {
        mapviewer.openContextMenu();
    }

    public void setFont( Control control ) {
        mapviewer.setFont(control);
    }

    public void setSelectionProvider( IMapEditorSelectionProvider selectionProvider ) {
        mapviewer.setSelectionProvider(selectionProvider);
    }
    
    public void refresh() {
    	mapviewer.getRenderManager().refresh(null);
    }

    @Override
    public IStatusLineManager getStatusLineManager() {
        return getViewSite().getActionBars().getStatusLineManager();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getAdapter(Class adaptee) {
        if (adaptee.isAssignableFrom(Map.class)) {
            return getMap();
        }
        return super.getAdapter(adaptee);
    }
}