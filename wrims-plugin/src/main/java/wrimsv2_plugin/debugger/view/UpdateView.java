package wrimsv2_plugin.debugger.view;

import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.tools.DataProcess;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.VariableProperty;

public class UpdateView {
		
	public static void updateVarMonitor(String event){
		final String dataString=event.replaceFirst("updateVarMonitor!","");
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				WPPVarMonitorView varMonitorView=(WPPVarMonitorView) workBenchPage.findView(DebugCorePlugin.ID_WPP_VARIABLEMONITOR_VIEW);
				varMonitorView.updatePlot(dataString);
			}
		});
	}
	
	public static void processVariableGoalView(final WPPDebugTarget target){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				String filePath=findFilePathActiveEditor(workBenchPage);
				if (FileProcess.isWreslFile(filePath)) {
					updateVariableView(target, filePath);
					updateGoalView(target, filePath);
				}
			}
		});
	}

	public static void processView(String viewName, IProgressMonitor monitor, ProgressMonitorDialog dialog, String filePath, WPPDebugTarget target){
		if (viewName.equals(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW)){
			monitor.subTask("Update all variables");
			updateAllVariableView(target);
		}else if (viewName.equals(DebugCorePlugin.TITLE_VARIABLES_VIEW)){
			monitor.subTask("Update variables in current file");
			if (FileProcess.isWreslFile(filePath)) updateVariableView(target, filePath);
		}else if (viewName.equals(DebugCorePlugin.TITLE_ALLGOALS_VIEW)){
			monitor.subTask("Update all goals");
			updateAllGoalView(target);
		}else if (viewName.equals(DebugCorePlugin.TITLE_GOALS_VIEW)){
			monitor.subTask("Update goals in current file");
			if (FileProcess.isWreslFile(filePath)) updateGoalView(target, filePath);
		}else if (viewName.equals(DebugCorePlugin.TITLE_WATCH_VIEW)){
			monitor.subTask("Update watched data");
			updateWatchView(target);
		}
	}
	
	public static void processView(String viewName, String filePath, WPPDebugTarget target){
		if (viewName.equals(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW)){
			updateAllVariableViewWithMonitor(target);
		}else if (viewName.equals(DebugCorePlugin.TITLE_VARIABLES_VIEW)){
			if (FileProcess.isWreslFile(filePath)) updateVariableView(target, filePath);
		}else if (viewName.equals(DebugCorePlugin.TITLE_ALLGOALS_VIEW)){
			updateAllGoalViewWithMonitor(target);
		}else if (viewName.equals(DebugCorePlugin.TITLE_GOALS_VIEW)){
			if (FileProcess.isWreslFile(filePath)) updateGoalView(target, filePath);
		}else if (viewName.equals(DebugCorePlugin.TITLE_WATCH_VIEW)){
			updateWatchView(target);
		}
	}
	
	public static void updateVariableView(WPPDebugTarget target, String filePath){
		
		final String data;
		try {
			data=target.sendRequest("data:"+filePath);
		
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				
					DataProcess.initialVariableValueAlt();
					//data="i:4456#a(-1):123.0#reservoir:reservorlevel1%56:reservorlevel2%1234";
					String[] dataParts = data.split("!");
					DebugCorePlugin.variableStack=DataProcess.generateTree(dataParts[0]);
					DebugCorePlugin.variableProperty=DataProcess.generateVariableProperty(dataParts[1]);
				
					WPPVariableView variableView = (WPPVariableView) workBenchPage.findView(DebugCorePlugin.ID_WPP_VARIABLE_VIEW);
					variableView.updateView();
				}
			});
		
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
	
	public static void updateAllVariableView(WPPDebugTarget target){
		String data="";
		try{
			data=target.sendRequest("alldata");
		}catch (DebugException e) {
			WPPException.handleException(e);
		}
		
		/*
		String[] dataStrings = data.split("&");
		if (dataStrings.length==3){
			DebugCorePlugin.weightedVariables=dataStrings[2];
			DebugCorePlugin.allVarProperties=dataStrings[1];
		}else if (dataStrings.length==2){
			DebugCorePlugin.allVarProperties=dataStrings[1];
		}
		DebugCorePlugin.allVariableStack=DataProcess.generateTree(dataStrings[0]);
		*/
		
		if (data.equals("alldataretrieved")){
			DebugCorePlugin.allVariableStack=DataProcess.retrieveAllVariableValue();
		}else{
			DebugCorePlugin.allVariableStack=new WPPValue[0];
			DebugCorePlugin.allVariableProperty=new HashMap<String, VariableProperty>();
			WPPException.handleException(new Exception("Failed in retrieve all variable data"));
		}
		
		final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				WPPAllVariableView allVariableView = (WPPAllVariableView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_ALLVARIABLE_VIEW);
				allVariableView.updateView();
			}
		});
	}
	
	public static void updateAllVariableViewWithMonitor(final WPPDebugTarget target){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
				try {
					dialog.run(true,false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							String data="";
							monitor.beginTask("Update all variables", 100);
							try{
								data=target.sendRequest("alldata");
							}catch (DebugException e) {
								WPPException.handleException(e);
							}
							monitor.worked(33);
							
							/*
							String[] dataStrings = data.split("&");
							if (dataStrings.length==3){
								DebugCorePlugin.weightedVariables=dataStrings[2];
								DebugCorePlugin.allVarProperties=dataStrings[1];
							}else if (dataStrings.length==2){
								DebugCorePlugin.allVarProperties=dataStrings[1];
							}
							
							DebugCorePlugin.allVariableStack=DataProcess.generateTree(dataStrings[0]);
							*/
							
							if (data.equals("alldataretrieved")){
								DebugCorePlugin.allVariableStack=DataProcess.retrieveAllVariableValue();
							}else{
								DebugCorePlugin.allVariableStack=new WPPValue[0];
								DebugCorePlugin.allVariableProperty=new HashMap<String, VariableProperty>();
								WPPException.handleException(new Exception("Failed in retrieve all variable data"));
							}
							
							monitor.worked(33);
							
							final IWorkbench workbench=PlatformUI.getWorkbench();
							workbench.getDisplay().asyncExec(new Runnable(){
								public void run(){
									WPPAllVariableView allVariableView = (WPPAllVariableView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_ALLVARIABLE_VIEW);
									allVariableView.updateView();	
								}
							});
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void updateAllGoalView(WPPDebugTarget target){
		String goal="";
		try{
			goal=target.sendRequest("allgoals");
		}catch (DebugException e) {
			WPPException.handleException(e);
		}
		
		if (goal.equals("allgoalretrieved")){
			DebugCorePlugin.allGoalStack=DataProcess.retrieveAllGoal();
		}else{
			DebugCorePlugin.allGoalStack=new WPPValue[0];
			WPPException.handleException(new Exception("Failed in retrieve all goals"));
		}
							
		DebugCorePlugin.allControlGoals=new ArrayList<String>();
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				WPPAllGoalView allGoalView = (WPPAllGoalView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_ALLGOAL_VIEW);
				allGoalView.updateView();
			}
		});
	}
	
	public static void updateAllGoalViewWithMonitor(final WPPDebugTarget target){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
				try {
					dialog.run(true,false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							String goal="";
							monitor.beginTask("Update all goals", 100);
							try{
								goal=target.sendRequest("allgoals");
							}catch (DebugException e) {
								WPPException.handleException(e);
							}
							monitor.worked(33);
							
							if (goal.equals("allgoalretrieved")){
								DebugCorePlugin.allGoalStack=DataProcess.retrieveAllGoal();
							}else{
								DebugCorePlugin.allGoalStack=new WPPValue[0];
								WPPException.handleException(new Exception("Failed in retrieve all goals"));
							}
							monitor.worked(33);
							
							DebugCorePlugin.allControlGoals=new ArrayList<String>();
							final IWorkbench workbench=PlatformUI.getWorkbench();
							workbench.getDisplay().asyncExec(new Runnable(){
								public void run(){
									WPPAllGoalView allGoalView = (WPPAllGoalView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_ALLGOAL_VIEW);
									allGoalView.updateView();
								}
							});
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void updateGoalView(WPPDebugTarget target, String filePath){
		final String data;
		try {
			data=target.sendRequest("goal:"+filePath);
		
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				
					String[] dataParts=data.split("!");
				
					if (dataParts.length==2){
						DebugCorePlugin.goalStack=DataProcess.generateTree(dataParts[0]);
						DebugCorePlugin.fileControlGoals=DataProcess.generateArrayList(dataParts[1]);
					}else if (dataParts.length==1){
						DebugCorePlugin.goalStack=DataProcess.generateTree(dataParts[0]);
						DebugCorePlugin.fileControlGoals=new ArrayList<String>();
					}else{
						DebugCorePlugin.goalStack=new WPPValue[0];
				    	DebugCorePlugin.fileControlGoals=new ArrayList<String>();
					}
				
					WPPGoalView goalView = (WPPGoalView) workBenchPage.findView(DebugCorePlugin.ID_WPP_GOAL_VIEW);
					goalView.updateView();
				}
			});
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
	
	public static void updateVarDetailView(final ArrayList<String> variableNames){
		if (variableNames.size()>0){
			try{
				String data="";
				String linkedVarNames="";
				for (String varName:variableNames){
					linkedVarNames=linkedVarNames+varName+"#";
				}
				if (linkedVarNames.endsWith("#")) linkedVarNames=linkedVarNames.substring(0, linkedVarNames.length()-1);
				data= DebugCorePlugin.target.sendRequest("tsdetail:"+linkedVarNames);
				DebugCorePlugin.varDetailTimeseries=DataProcess.generateVarDetailData(data);
				data= DebugCorePlugin.target.sendRequest("futdetail:"+variableNames.get(0));
				DebugCorePlugin.varDetailFuture=DataProcess.generateVarDetailData(data);
				data= DebugCorePlugin.target.sendRequest("cycledetail:"+variableNames.get(0));
				DebugCorePlugin.varDetailCycle=DataProcess.generateVarDetailData(data);
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						WPPVarDetailView varDetailView = (WPPVarDetailView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_VARIABLEDETAIL_VIEW);
						varDetailView.updateDetail(variableNames);
					}
				});
			} catch (DebugException e) {
				WPPException.handleException(e);
			}
		}
	}
	
	public static void updateWatchView(WPPDebugTarget target){
		String data="";
		try{
			String watchItemsString=formWatchItemsString(DebugCorePlugin.watchItems);
			data=target.sendRequest("watch:"+watchItemsString);
		}catch (DebugException e) {
			WPPException.handleException(e);
		}
		
		String[] dataStrings=data.split("&");
		if (dataStrings.length>1) DebugCorePlugin.watchProperty=DataProcess.generateVariableProperty(dataStrings[1]);
		String[] dataParts=dataStrings[0].split("!");
		if (dataParts.length==2){
			DebugCorePlugin.watchStack=DataProcess.generateTree(dataParts[0]);
			DebugCorePlugin.watchControlGoals=DataProcess.generateArrayList(dataParts[1]);
		}else if (dataParts.length==1){
			DebugCorePlugin.watchStack=DataProcess.generateTree(dataParts[0]);
			DebugCorePlugin.watchControlGoals=new ArrayList<String>();
		}else{
			DebugCorePlugin.watchStack=new WPPValue[0];
	    	DebugCorePlugin.watchControlGoals=new ArrayList<String>();
		}
									
		final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				WPPWatchView watchView;
				try {
					watchView = (WPPWatchView) workbench.getActiveWorkbenchWindow().getActivePage().showView(DebugCorePlugin.ID_WPP_WATCH_VIEW);
					watchView.updateView();
				} catch (PartInitException e) {
					WPPException.handleException(e);
				}
			}
		});
	}
	
	public static String findFilePathActiveEditor(IWorkbenchPage workBenchPage){
		String path="";
		IEditorPart editorPart=workBenchPage.getActiveEditor() ;
		if (editorPart != null){
			IEditorInput input = editorPart.getEditorInput();
			IFile file = ResourceUtil.getFile(input);
			if (file != null) path = file.getRawLocation().toString();
		}
		return path;
	}
	
	public static String findFilePathEditorPart(IWorkbenchPart part){
		String path="";
		if (part instanceof IEditorPart){
			IEditorPart editorPart=(IEditorPart)part ;
			if (editorPart != null){
				IEditorInput input = editorPart.getEditorInput();
				IFile file = ResourceUtil.getFile(input);
				path = file.getRawLocation().toString();
			}
		}
		return path;
	}
	
	public static String formWatchItemsString(ArrayList<String> watchItems){
		String watchItemsString="";
		int size = watchItems.size();
		for (int i=0; i<size; i++){
			watchItemsString=watchItemsString+watchItems.get(i)+"#";
		}
		if (watchItemsString.endsWith("#")) watchItemsString.substring(0, watchItemsString.length()-1);
		return watchItemsString;
	}
	
	public static void addNameSortListener(final Table table, final AbstractDebugView view){
	    Listener nameSortListener = new Listener() {  
            
			@Override
			public void handleEvent(Event e) {
				 if (view instanceof WPPAllGoalView){
					 ((WPPAllGoalView)view).isAscending=!((WPPAllGoalView)view).isAscending;
					 ((WPPAllGoalView)view).updateView();
				 }else if (view instanceof WPPAllVariableView){
					 ((WPPAllVariableView)view).isAscending=!((WPPAllVariableView)view).isAscending;
					 ((WPPAllVariableView)view).updateView();
				 }else if (view instanceof WPPGoalView){
					 ((WPPGoalView)view).isAscending=!((WPPGoalView)view).isAscending;
					 ((WPPGoalView)view).updateView();
				 }else if (view instanceof WPPVariableView){
					 ((WPPVariableView)view).isAscending=!((WPPVariableView)view).isAscending;
					 ((WPPVariableView)view).updateView();
				 }else if (view instanceof WPPWatchView){
					 ((WPPWatchView)view).isAscending=!((WPPWatchView)view).isAscending;
					 ((WPPWatchView)view).updateView();
				 }
			}
	    };  
	    
	    table.getColumn(0).addListener(SWT.Selection, nameSortListener);
	}
}
