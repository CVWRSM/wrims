package wrimsv2_plugin.debugger.commanditem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class DSSHDF5Conversion extends AbstractHandler {

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
		    	if (path.endsWith(".launch")){
					convertLaunch(path);
				}else if (path.endsWith(".config")){
					convertConfig(path);
				}
		    }
		}
		return null;
	}
	
	public void convertLaunch(String fn){
		try {
			String conversionFileName="DssHDF5Converter_Launch.bat";
			FileWriter conversionFile = new FileWriter(conversionFileName);
			PrintWriter out = new PrintWriter(conversionFile);
			out.println("@echo off");
			out.println();
			out.println("set path=lib;%path%");
			out.println("set temp_wrims2=jre\\bin");
			out.println();
			out.println("jre\\bin\\java -Xmx4096m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"lib\" -cp \"lib\\external;lib\\*\" wrimsv2.hdf5.DSSHDF5Converter -launch="+fn);
			out.close();
			Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "start", "/w", conversionFileName}, 
					null, null); 
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public void convertConfig(String fn){
		try {
			String conversionFileName="DssHDF5Converter_Config.bat";
			FileWriter conversionFile = new FileWriter(conversionFileName);
			PrintWriter out = new PrintWriter(conversionFile);
			out.println("@echo off");
			out.println();
			out.println("set path=lib;%path%");
			out.println("set temp_wrims2=jre\\bin");
			out.println();
			out.println("jre\\bin\\java -Xmx4096m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"lib\" -cp \"lib\\external;lib\\*\" wrimsv2.hdf5.DSSHDF5Converter -config="+fn);
			out.close();
			Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "start", "/w", conversionFileName}, 
					null, null); 
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
}
