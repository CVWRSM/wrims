package gov.ca.dwr.hecdssvue.perspective;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSFileView;
import gov.ca.dwr.hecdssvue.views.DSSMonthlyView;
import gov.ca.dwr.hecdssvue.views.DSSOpsView;
import gov.ca.dwr.hecdssvue.views.DSSPlotView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import gov.ca.dwr.hecdssvue.views.DTSView;
import gov.ca.dwr.hecdssvue.views.DeliveryShortagesView;
import gov.ca.dwr.hecdssvue.views.SanJoaquinRiverView;
import gov.ca.dwr.hecdssvue.views.StorageFlowsView;
import gov.ca.dwr.hecdssvue.views.WaterManagementActionsView;
import gov.ca.dwr.hecdssvue.views.WaterYearView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class DSSPerspectiveFactory implements IPerspectiveFactory {

	private String projectExplorerID="org.eclipse.ui.navigator.ProjectExplorer";
	private String outlineID="org.eclipse.ui.views.ContentOutline";
		
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		IFolderLayout lf = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
		lf.addView(projectExplorerID);
		lf.addView(DebugCorePlugin.ID_WPP_FILEINCEXPLORE_VIEW);
		IFolderLayout lbf = layout.createFolder("leftbottom", IPageLayout.BOTTOM, 0.6f, "left");
		lbf.addView(outlineID);
		IFolderLayout bf = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
		bf.addView(DSSFileView.ID);
		bf.addView(DSSCatalogView.ID);
		bf.addView(StorageFlowsView.ID);
		bf.addView(SanJoaquinRiverView.ID);
		bf.addView(WaterManagementActionsView.ID);
		bf.addView(DeliveryShortagesView.ID);
		bf.addView(DTSView.ID);
		IFolderLayout rf = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, editorArea);
		rf.addView(DSSTableView.ID);
		IFolderLayout tf = layout.createFolder("top", IPageLayout.TOP, 0.7f, editorArea);
		tf.addView(DSSMonthlyView.ID);
		tf.addView(DSSPlotView.ID);
		IFolderLayout mf = layout.createFolder("middle", IPageLayout.BOTTOM, 0.7f, "top");
		mf.addView(DSSOpsView.ID);
		mf.addView(WaterYearView.ID);
	}

}
