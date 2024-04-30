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

import wrimsv2_plugin.debugger.dialog.WPPBatchRunDialog;

public class BatchRunMenu implements IWorkbenchWindowActionDelegate {
	public BatchRunMenu(){

	}

	@Override
	public void run(IAction action) {
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				WPPBatchRunDialog dialog= new WPPBatchRunDialog(shell);
				dialog.openDialog();
				
				
			}
		});
		
		/*
		Runnable runnable = new Runnable(){
			
			public void run(){
				final String launchFilePath1="D:\\CAM_Plus\\PA_OCAPBIOP_CAM_DFT_WRIMS2_NewCAMPlus_09182014\\CAM_DFT_PA_W2.launch";
				final String launchFilePath2="D:\\MultiStudyRuns\\MultiStudy.launch";
		
				final LaunchConfigInfo configuration1 = new LaunchConfigInfo(launchFilePath1);
				final LaunchConfigInfo configuration2 = new LaunchConfigInfo(launchFilePath2);
				
				BatchRunProcess brp1=new BatchRunProcess();
				BatchRunProcess brp2=new BatchRunProcess();
				try {
					brp1.launch(configuration1, launchFilePath1);
					brp2.launch(configuration2, launchFilePath2);
				} catch (CoreException e) {
					WPPException.handleException(e);
				}
			}
		};
		
		new Thread(runnable).start();
		*/
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
