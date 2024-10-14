package wrimsv2_plugin.debugger.menuitem;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.dialog.WPPDSSHDF5ConversionDialog;


public class DSSHDF5ConversionMenu implements IWorkbenchWindowActionDelegate {
	public DSSHDF5ConversionMenu(){

	}

	@Override
	public void run(IAction action) {
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				WPPDSSHDF5ConversionDialog dialog= new WPPDSSHDF5ConversionDialog(shell);
				dialog.openDialog();
				
				
			}
		});
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

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
