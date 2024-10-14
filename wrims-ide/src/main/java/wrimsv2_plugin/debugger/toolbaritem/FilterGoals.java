package wrimsv2_plugin.debugger.toolbaritem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.dialog.WPPFilterGoalsDialog;
import wrimsv2_plugin.debugger.dialog.WPPReSimDialog;
import wrimsv2_plugin.debugger.dialog.WPPSaveFileDialog;
import wrimsv2_plugin.debugger.dialog.WPPVarGoalSearchDialog;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.WPPAllGoalView;
import wrimsv2_plugin.debugger.view.WPPAllVariableView;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.tools.DataProcess;

public class FilterGoals extends ActionDelegate implements IViewActionDelegate{

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				WPPFilterGoalsDialog dialog= new WPPFilterGoalsDialog(shell);
				dialog.openDialog();
			}
		});
	}
}
