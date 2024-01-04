package gov.ca.dwr.hecdssvue.menus;

import gov.ca.dwr.hecdssvue.components.DataOps;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class DeleteRecords implements IWorkbenchWindowActionDelegate{

	@Override
	public void run(IAction action) {
		
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				boolean delete = MessageDialog.openConfirm(shell, "Delete Confirmation", "Do you want to delete selected timeseries?");
				if (delete){
					DataOps.deleteSelected();
				}
			}
		}); 
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
