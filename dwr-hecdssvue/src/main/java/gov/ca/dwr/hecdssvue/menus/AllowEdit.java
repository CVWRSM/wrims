package gov.ca.dwr.hecdssvue.menus;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import hec.dataTable.HecDataTable;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.view.WPPVarMonitorView;

public class AllowEdit implements IWorkbenchWindowActionDelegate{

	@Override
	public void run(IAction action) {
		DssPluginCore.dssEditable=!DssPluginCore.dssEditable;
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
		DSSTableView dssTableView=(DSSTableView) workBenchPage.findView(DSSTableView.ID);
		
		if (dssTableView !=null){
			HecDataTable table = dssTableView.getTable();
			if (DssPluginCore.dssEditable){
				if (table != null) table.setEditable(true);
				action.setText("Disallow Editing");
			}else{
				if (table !=null) table.setEditable(false);
				action.setText("Allow Editing");
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

}
