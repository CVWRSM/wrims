package gov.ca.dwr.wrims.geoschematic;

import java.util.Collections;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.ModeledPageLayout;
import org.locationtech.udig.catalog.internal.ui.CatalogView;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectFactory;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.LayersView;
import org.locationtech.udig.project.ui.internal.MapEditor;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;
import org.locationtech.udig.tool.info.internal.InfoView2;

@SuppressWarnings("restriction")
public class GeoschematicPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
        layout.setEditorAreaVisible(true);
		String editorArea = layout.getEditorArea();
		ModeledPageLayout mpl = (ModeledPageLayout) layout;
		mpl.stackView(MapView.ID, IPageLayout.ID_EDITOR_AREA, true);
		Project p = ProjectPlugin.getPlugin().getProjectRegistry().getDefaultProject();
        IMap map = (Map) ProjectFactory.eINSTANCE.createMap(p, "Default Map", Collections.EMPTY_LIST);
		try {
			IEditorInput editorInput = ApplicationGIS.getInput(map);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, MapEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		} 
		
        IFolderLayout leftFolder = layout.createFolder("gov.ca.dwr.wrims.geoschematic.GeoschematicPerspective.leftFolder", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$
        leftFolder.addView(ProjectExplorer.ID); 
        leftFolder.addView(LayersView.ID); 

        IFolderLayout bottomFolder = layout.createFolder("gov.ca.dwr.wrims.geoschematic.GeoschematicPerspective.bottomFolder", IPageLayout.BOTTOM, 0.7f, editorArea); //$NON-NLS-1$
        bottomFolder.addView(InfoView2.VIEW_ID);
        bottomFolder.addPlaceholder(CatalogView.VIEW_ID);

        //layout.getViewLayout(MapView.ID).setCloseable(false);
        
	}
	
}
