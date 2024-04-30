package wrimsv2_plugin.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class IDEPerspectiveFactory implements IPerspectiveFactory {

	private String projectExplorerID="org.eclipse.ui.navigator.ProjectExplorer";
	private String consoleViewID="org.eclipse.ui.console.ConsoleView";
	private String outlineID="org.eclipse.ui.views.ContentOutline";
		
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		IFolderLayout lf = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
		lf.addView(projectExplorerID);
		lf.addView(DebugCorePlugin.ID_WPP_FILEINCEXPLORE_VIEW);
		IFolderLayout lbf = layout.createFolder("leftbottom", IPageLayout.BOTTOM, 0.6f, "left");
		lbf.addView(outlineID);
		IFolderLayout bf = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
		bf.addView(consoleViewID);
		bf.addView(DebugCorePlugin.ID_WPP_VARIABLEDETAIL_VIEW);
		bf.addView(DebugCorePlugin.ID_WPP_VARIABLEMONITOR_VIEW);
		bf.addView(DebugCorePlugin.ID_WPP_EXCEPTION_VIEW);
		bf.addView(DebugCorePlugin.ID_WPP_CALSIMHYDRO_VIEW);
		//bf.addView(DebugCorePlugin.ID_WPP_INFEASIBILITY_VIEW);
		IFolderLayout rf = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, editorArea);
		rf.addView(DebugCorePlugin.ID_WPP_VARIABLE_VIEW); 
		rf.addView(DebugCorePlugin.ID_WPP_GOAL_VIEW);
		rf.addView(DebugCorePlugin.ID_WPP_ALLVARIABLE_VIEW);
		rf.addView(DebugCorePlugin.ID_WPP_ALLGOAL_VIEW);
		rf.addView(DebugCorePlugin.ID_WPP_WATCH_VIEW);
	}

}
