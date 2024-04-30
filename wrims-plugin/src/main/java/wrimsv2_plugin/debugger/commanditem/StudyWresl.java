package wrimsv2_plugin.debugger.commanditem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;

import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.debug.Compile;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.view.WPPFileIncExploreView;
import wrimsv2_plugin.debugger.view.WPPVarMonitorView;
import wrimsv2_plugin.tools.FileProcess;

public class StudyWresl extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WPPDebugTarget target = DebugCorePlugin.target;
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
		    Object firstEle = ssel.getFirstElement();
		    IFile ifile = (IFile) Platform.getAdapterManager().getAdapter(firstEle,
		    		IFile.class);
		    if (ifile != null) {
		    	final String path = ifile.getRawLocation().toOSString();
		        	
		        final IWorkbench workbench=PlatformUI.getWorkbench();
		    	workbench.getDisplay().asyncExec(new Runnable(){
		    		public void run(){
		    			Shell shell=workbench.getActiveWorkbenchWindow().getShell();
		    			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
		    			try {
							dialog.run(true,false, new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor) {
									monitor.beginTask("Update explorer for included WRESL files", 100);
									StudyDataSet sds=new StudyDataSet();
									try {
										sds = Compile.checkStudy(path, true);
									} catch (IOException e) {
										WPPException.handleException(e);
									}
									monitor.worked(50);
									ArrayList<String> fns=FileProcess.getStudyWreslFiles(path, sds);
									ArrayList<String> tfns=FileProcess.getTableFiles(path);
									fns.addAll(tfns);
									monitor.worked(20);
									DebugCorePlugin.fileFolderWreslInc=FileProcess.retrieveFileNames(fns);
									monitor.worked(20);
									UpdateWreslIncExplore();
									monitor.done();
									
								}
							});
						} catch (InvocationTargetException e) {
							WPPException.handleException(e);
						} catch (InterruptedException e) {
							WPPException.handleException(e);
						}
		    		}
		    	});
		    }
		}
		return null;
	}
	
	public void UpdateWreslIncExplore(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				WPPFileIncExploreView wreslIncExplore;
				try {
					wreslIncExplore = (WPPFileIncExploreView) workBenchPage.showView(DebugCorePlugin.ID_WPP_FILEINCEXPLORE_VIEW);
					wreslIncExplore.update();
				} catch (PartInitException e) {
					WPPException.handleException(e);
				}
			}
		});
	}
}
