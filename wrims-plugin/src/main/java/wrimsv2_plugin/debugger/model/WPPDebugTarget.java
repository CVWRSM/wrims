/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bjorn Freeman-Benson - initial API and implementation
 *******************************************************************************/
package wrimsv2_plugin.debugger.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import wrimsv2_plugin.debugger.breakpoint.WPPLineBreakpoint;
import wrimsv2_plugin.debugger.breakpoint.WPPRunToLineBreakpoint;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.toolbaritem.EnableButtons;
import wrimsv2_plugin.debugger.toolbaritem.HandlePauseResumeButton;
import wrimsv2_plugin.debugger.view.UpdateView;
import wrimsv2_plugin.debugger.view.WPPAllGoalView;
import wrimsv2_plugin.debugger.view.WPPAllVariableView;
import wrimsv2_plugin.debugger.view.WPPExceptionView;
import wrimsv2_plugin.debugger.view.WPPGoalView;
import wrimsv2_plugin.debugger.view.WPPVarDetailView;
import wrimsv2_plugin.debugger.view.WPPVarMonitorView;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.debugger.view.WPPWatchView;
import wrimsv2_plugin.tools.DataProcess;

/**
 * WPP Debug Target
 */
public class WPPDebugTarget extends WPPDebugElement implements IDebugTarget, IBreakpointManagerListener, IWPPEventListener {
	
	// associated system process (VM)
	private IProcess fProcess;
	
	// containing launch object
	private ILaunch fLaunch;
	
	// sockets to communicate with VM
	private Socket fRequestSocket;
	private PrintWriter fRequestWriter;
	private BufferedReader fRequestReader;
	private Socket fEventSocket;
	private BufferedReader fEventReader;
	
	// terminated state
	private boolean fTerminated = false;
	
	// threads
	private IThread[] fThreads;
	private WPPThread fThread;

	private String fCurrFileName;
	private ISourceLookupResult result;
	private int currLine;
	private IEditorPart fTextEditor;
	private IWorkbenchPart fPart;
	private IPartListener fPartListener;
	private IPartListener2 fPartListener2;
	public ArrayList<String> allVGLoadedViewNames=new ArrayList<String>(); 
	private ArrayList<String> dataAvaialbeViewNames = new ArrayList<String>();
	
	// event dispatch job
	private EventDispatchJob fEventDispatch;
	// event listeners
	private Vector fEventListeners = new Vector();
	
	/**
	 * Listens to events from the WPP VM and fires corresponding 
	 * debug events.
	 */
	class EventDispatchJob extends Job {
		
		public EventDispatchJob() {
			super("WPP Event Dispatch");
			setSystem(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String event = "";
			while (!isTerminated() && event != null) {
				try {
					event = fEventReader.readLine();
					if (event != null) {
						Object[] listeners = fEventListeners.toArray();
						for (int i = 0; i < listeners.length; i++) {
							((IWPPEventListener)listeners[i]).handleEvent(event);	
						}
					}
				} catch (IOException e) {
					terminated();
				}
			}
			return Status.OK_STATUS;
		}
		
	}
	
	/**
	 * Registers the given event listener. The listener will be notified of
	 * events in the program being interpretted. Has no effect if the listener
	 * is already registered.
	 *  
	 * @param listener event listener
	 */
	public void addEventListener(IWPPEventListener listener) {
		if (!fEventListeners.contains(listener)) {
			fEventListeners.add(listener);
		}
	}
	
	/**
	 * Deregisters the given event listener. Has no effect if the listener is
	 * not currently registered.
	 *  
	 * @param listener event listener
	 */
	public void removeEventListener(IWPPEventListener listener) {
		fEventListeners.remove(listener);
	}
	
	/**
	 * Constructs a new debug target in the given launch for the 
	 * associated WPP VM process.
	 * 
	 * @param launch containing launch
	 * @param process WPP VM
	 * @param requestPort port to send requests to the VM
	 * @param eventPort port to read events from
	 * @exception CoreException if unable to connect to host
	 */
	public WPPDebugTarget(ILaunch launch, IProcess process, int requestPort, int eventPort) throws CoreException {
		super(null);
		DebugCorePlugin.target=this;
		fLaunch = launch;
		fProcess = process;
		addEventListener(this);
		try {
			// give interpreter a chance to start
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			fRequestSocket = new Socket("localhost", requestPort);
			fEventSocket = new Socket("localhost", eventPort);
			fRequestWriter = new PrintWriter(fRequestSocket.getOutputStream());
			fRequestReader = new BufferedReader(new InputStreamReader(fRequestSocket.getInputStream()));
			fEventReader = new BufferedReader(new InputStreamReader(fEventSocket.getInputStream()));
		} catch (UnknownHostException e) {
			requestFailed("Unable to connect to WPP VM", e);
		} catch (IOException e) {
			requestFailed("Unable to connect to WPP VM", e);
		}
		fThread = new WPPThread(this);
		fThreads = new IThread[] {fThread};
		fEventDispatch = new EventDispatchJob();
		fEventDispatch.schedule();
		
		IBreakpointManager breakpointManager = getBreakpointManager();
        breakpointManager.addBreakpointListener(this);
		breakpointManager.addBreakpointManagerListener(this);
		
		//installDeferredBreakpoints();
		
		installPartListener();
		
		getStart();
		//System.out.println(data);
		//data=sendRequest("variables:s_shsta#s_folsm");
		//System.out.println(data);
		
		
		/*
		if (fTextEditor!=null) ((ITextEditor)fTextEditor).resetHighlightRange();
		data=sendRequest("resume");
		System.out.println(data);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			WPPException.handleException(e);
		}
		
		data=sendRequest("step");
		System.out.println(data);
		
		if (fTextEditor!=null) ((ITextEditor)fTextEditor).resetHighlightRange();
		data=sendRequest("resume");
		System.out.println(data);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			WPPException.handleException(e);
		}
		
		data=sendRequest("year:10001");
		System.out.println(data);
		
		if (fTextEditor!=null) ((ITextEditor)fTextEditor).resetHighlightRange();
		data=sendRequest("resume");
		System.out.println(data);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			WPPException.handleException(e);
		}
		
		
		fProcess.terminate();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchWindow window=workbench.getActiveWorkbenchWindow();
				if (window !=null){
					window.getActivePage().removePartListener(fPartListener);
				}
			}
		});
		*/
	}
	
	public void getStart() {
		String data;
		try {
			data=sendRequest("start");
			DebugCorePlugin.isDebugging=true;
			
			data=sendRequest("time:"+DebugCorePlugin.debugYear+"/"+DebugCorePlugin.debugMonth+"/"+DebugCorePlugin.debugDay+"/"+DebugCorePlugin.debugCycle);
			data=sendRequest("conditional_breakpoint:"+DebugCorePlugin.conditionalBreakpoint);
			enableRunMenuWithStart();
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
	
	public void processViews(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				
				dataAvaialbeViewNames=new ArrayList<String>();
				allVGLoadedViewNames=new ArrayList<String>();
				
				WPPAllVariableView allVariableView = (WPPAllVariableView) workBenchPage.findView(DebugCorePlugin.ID_WPP_ALLVARIABLE_VIEW);
				if (workBenchPage.isPartVisible(allVariableView)){
					if (!dataAvaialbeViewNames.contains(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW)){
						dataAvaialbeViewNames.add(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW);
					}
					if (!allVGLoadedViewNames.contains(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW)){
						allVGLoadedViewNames.add(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW);
					}
				}
				WPPVariableView variableView = (WPPVariableView) workBenchPage.findView(DebugCorePlugin.ID_WPP_VARIABLE_VIEW);
				if (!dataAvaialbeViewNames.contains(DebugCorePlugin.TITLE_VARIABLES_VIEW)){
					dataAvaialbeViewNames.add(DebugCorePlugin.TITLE_VARIABLES_VIEW);
				}
				WPPAllGoalView allGoalView = (WPPAllGoalView) workBenchPage.findView(DebugCorePlugin.ID_WPP_ALLGOAL_VIEW);
				if (workBenchPage.isPartVisible(allGoalView)){
					if (!dataAvaialbeViewNames.contains(DebugCorePlugin.TITLE_ALLGOALS_VIEW)){
						dataAvaialbeViewNames.add(DebugCorePlugin.TITLE_ALLGOALS_VIEW);
					}
					if (!allVGLoadedViewNames.contains(DebugCorePlugin.TITLE_ALLGOALS_VIEW)){
						allVGLoadedViewNames.add(DebugCorePlugin.TITLE_ALLGOALS_VIEW);
					}
				}
				WPPGoalView goalView = (WPPGoalView) workBenchPage.findView(DebugCorePlugin.ID_WPP_GOAL_VIEW);
				if (!dataAvaialbeViewNames.contains(DebugCorePlugin.TITLE_GOALS_VIEW)){
					dataAvaialbeViewNames.add(DebugCorePlugin.TITLE_GOALS_VIEW);
				}
				WPPWatchView watchView = (WPPWatchView) workBenchPage.findView(DebugCorePlugin.ID_WPP_WATCH_VIEW);
				if (workBenchPage.isPartVisible(watchView)){
					if (!dataAvaialbeViewNames.contains(DebugCorePlugin.TITLE_WATCH_VIEW)){
						dataAvaialbeViewNames.add(DebugCorePlugin.TITLE_WATCH_VIEW);
					}
					if (!allVGLoadedViewNames.contains(DebugCorePlugin.TITLE_WATCH_VIEW)){
						allVGLoadedViewNames.add(DebugCorePlugin.TITLE_WATCH_VIEW);
					}
				}
				final IWorkbench workbench=PlatformUI.getWorkbench();
				final String filePath=UpdateView.findFilePathActiveEditor(workBenchPage);
				
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
						try {
							dialog.run(true,false, new IRunnableWithProgress() {
								@Override
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
									monitor.beginTask("Update views", 100);
									for (String viewName: dataAvaialbeViewNames){
										UpdateView.processView(viewName, monitor, dialog, filePath, DebugCorePlugin.target);
										monitor.worked(25);
									}
									monitor.done();	
									UpdateView.updateVarDetailView(DebugCorePlugin.selectedVariableNames);
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
		});
	}
	
	public void installPartListener(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchWindow window=workbench.getActiveWorkbenchWindow();
				final IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				if (window !=null){				
					fPartListener2=new IPartListener2(){

						@Override
						public void partActivated(
								IWorkbenchPartReference partRef) {
							IWorkbenchPart part = partRef.getPart(false);
							String filePath=UpdateView.findFilePathActiveEditor(workBenchPage);
							if (part instanceof AbstractDebugView){
								String viewName=part.getTitle();
								if (viewName.equals(DebugCorePlugin.TITLE_ALLVARIABLES_VIEW) || viewName.equals(DebugCorePlugin.TITLE_ALLGOALS_VIEW) || viewName.equals(DebugCorePlugin.TITLE_WATCH_VIEW)){
									if (!allVGLoadedViewNames.contains(viewName)){
										allVGLoadedViewNames.add(viewName);
										if (isSuspended()){
											UpdateView.processView(viewName, filePath, DebugCorePlugin.target);
										}
									}
								}
								/*else{
									if (isSuspended()){
										UpdateView.processView(viewName, filePath, DebugCorePlugin.target);
									}
								}*/
							}
						}

						@Override
						public void partBroughtToTop(
								IWorkbenchPartReference partRef) {
							IWorkbenchPart part = partRef.getPart(false);
							if ((part instanceof ITextEditor) && (!part.equals(fPart))){
								fPart=part;
								String filePath=UpdateView.findFilePathEditorPart(fPart);						
								if (isSuspended() && filePath.endsWith(".wresl")){
									UpdateView.processVariableGoalView(DebugCorePlugin.target);
								}
							}
						}

						@Override
						public void partClosed(IWorkbenchPartReference partRef) {
							IWorkbenchPart part = partRef.getPart(false);
							if (part instanceof AbstractDebugView){
								String viewName=part.getTitle();
								if (allVGLoadedViewNames.contains(viewName)){
									allVGLoadedViewNames.remove(viewName);
								}
							}
						}

						@Override
						public void partDeactivated(
								IWorkbenchPartReference partRef) {
						}

						@Override
						public void partOpened(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partHidden(IWorkbenchPartReference partRef) {
						}

						@Override
						public void partVisible(IWorkbenchPartReference partRef) {					
						}

						@Override
						public void partInputChanged(
								IWorkbenchPartReference partRef) {						
						}
						
					};
					window.getActivePage().addPartListener(fPartListener2);
				}
			}
		});
	}
	
	public void setSourceName(String fileName){
		fCurrFileName=fileName;
	}
	
	public void setCurrLine(int line){
		currLine=line;
	}
	
	public String getSourceName(){
		return fCurrFileName;
	}

    /* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	@Override
	public IProcess getProcess() {
		return fProcess;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	@Override
	public IThread[] getThreads() throws DebugException {
		return fThreads;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	@Override
	public boolean hasThreads() throws DebugException {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	@Override
	public String getName() throws DebugException {
		return "WPP";
	}
	
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (!isTerminated() && breakpoint.getModelIdentifier().equals(getModelIdentifier())) {
			try {
				String program = getLaunch().getLaunchConfiguration().getAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM, (String)null);
				if (program.toLowerCase().endsWith(".wresl") || program.toLowerCase().endsWith(".wpp")) {
					return true;
				}
			} catch (CoreException e) {
				WPPException.handleException(e);
			}			
		}
		return false;
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	@Override
	public ILaunch getLaunch() {
		return fLaunch;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		return getProcess().canTerminate();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return fTerminated || getProcess().isTerminated();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		WPPThread thread = getThread();
		if (thread != null) thread.terminate();
		enableRunMenuWithTerminate();
		DebugCorePlugin.isDebugging=false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return !isTerminated() && getThread().isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException {
		getThread().resume();
	}	
	
	public void resimCycle(String parameters) throws DebugException{
		getThread().resimCycle(parameters);
	}
	
	public void resimDate(String parameters) throws DebugException{
		getThread().resimDate(parameters);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
		getThread().suspend();
	}
	
	public void pause() throws DebugException {
		getThread().pause();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if ((breakpoint.isEnabled() && getBreakpointManager().isEnabled()) || !breakpoint.isRegistered()) {
					WPPLineBreakpoint wppBreakpoint = (WPPLineBreakpoint)breakpoint;
				    wppBreakpoint.install(this);
				}
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
			    WPPLineBreakpoint wppBreakpoint = (WPPLineBreakpoint)breakpoint;
				wppBreakpoint.remove(this);
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled() && getBreakpointManager().isEnabled()) {
					breakpointAdded(breakpoint);
				} else {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	@Override
	public boolean canDisconnect() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	@Override
	public void disconnect() throws DebugException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	@Override
	public boolean isDisconnected() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	/**
	 * Notification we have connected to the VM and it has started.
	 * Resume the VM.
	 */
	private void started() {
		fireCreationEvent();
		installDeferredBreakpoints();
		try {
			resume();
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
	
	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}
	
	/**
	 * Called when this debug target terminates.
	 */
	private synchronized void terminated() {
		fTerminated = true;
		fThread = null;
		fThreads = new IThread[0];
		IBreakpointManager breakpointManager = getBreakpointManager();
        breakpointManager.removeBreakpointListener(this);
		breakpointManager.removeBreakpointManagerListener(this);
		fireTerminateEvent();
		removeEventListener(this);
	}
	
	/* (non-Javadoc)
	 * @see example.debug.core.model.WPPDebugElement#sendRequest(java.lang.String)
	 */
	@Override
	public String sendRequest(String request) throws DebugException {
		synchronized (fRequestSocket) {
			fRequestWriter.println(request);
			fRequestWriter.flush();
			try {
				// wait for reply
				return fRequestReader.readLine();
			} catch (IOException e) {
				requestFailed("Request failed: " + request, e);
			}
		}
		return null;
	}  
	
	/**
	 * When the breakpoint manager disables, remove all registered breakpoints
	 * requests from the VM. When it enables, reinstall them.
	 */
	public void breakpointManagerEnablementChanged(boolean enabled) {
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(getModelIdentifier());
		for (int i = 0; i < breakpoints.length; i++) {
			if (enabled) {
				breakpointAdded(breakpoints[i]);
			} else {
				breakpointRemoved(breakpoints[i], null);
			}
        }
	}	
	
	/**
	 * Returns whether popping the data stack is currently permitted
	 *  
	 * @return whether popping the data stack is currently permitted
	 */
	public boolean canPop() {
	    return !isTerminated() && isSuspended() && DebugCorePlugin.variableStack.length > 0;
	}
	
	/**
	 * Pops and returns the top of the data stack
	 * 
	 * @return the top value on the stack 
	 * @throws DebugException if the stack is empty or the request fails
	 */
	public IValue pop() throws DebugException {
	    IValue[] dataStack = DebugCorePlugin.variableStack;
	    if (dataStack.length > 0) {
	        sendRequest("popdata");
	        return dataStack[0];
	    }
	    requestFailed("Empty stack", null);
	    return null;
	}
	
	/**
	 * Returns whether pushing a value is currently supported.
	 * 
	 * @return whether pushing a value is currently supported
	 */
	public boolean canPush() {
	    return !isTerminated() && isSuspended();
	}
	
	/**
	 * Pushes a value onto the stack.
	 * 
	 * @param value value to push
	 * @throws DebugException on failure
	 */
	public void push(String value) throws DebugException {
	    sendRequest("pushdata " + value);
	}

	/* (non-Javadoc)
	 * @see example.debug.core.model.IWPPEventListener#handleEvent(java.lang.String)
	 */
	@Override
	public void handleEvent(String event) {
		if (event.startsWith("suspended")) {		
			handleSuspended(event);
		}else if (event.startsWith("updateVarMonitor!")){
			UpdateView.updateVarMonitor(event);
		}else if(event.startsWith("totalcycle#")){
			DebugCorePlugin.totalNoOfCycle=Integer.parseInt(event.replace("totalcycle#", ""));
		}else if (event.equals("terminate")){
			try {
				terminate();
			} catch (DebugException e) {
				WPPException.handleException(e);
			}
		}
	}
	
	private void handleSuspended(final String event){		
		
		if (event.contains("!")){
			final String[] dateStrings=event.replaceFirst("suspended!", "").split("#");
			DebugCorePlugin.suspendedYear=Integer.parseInt(dateStrings[0]);
			DebugCorePlugin.suspendedMonth=Integer.parseInt(dateStrings[1]);
			DebugCorePlugin.suspendedDay=Integer.parseInt(dateStrings[2]);
			DebugCorePlugin.suspendedCycle=Integer.parseInt(dateStrings[3]);
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					DebugCorePlugin.debugSet.setDebugDate(dateStrings[0],dateStrings[1], dateStrings[2]);
					DebugCorePlugin.debugSet.getComboCycle().setText(dateStrings[3]);
				}
			});
		}
		enableRunMenuWithSuspended();
		DataProcess.generateAltStudyData();
		processViews();
		
		if (event.contains(":")) {
			String[] eventPart=event.split(":");
			setCurrLine(Integer.parseInt(eventPart[1]));
			setSourceName(eventPart[2]);
			openSourceHighlight();
		}
	}
	
	public void openSourceHighlight(){
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() { 
			public void run() { 
				IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IPath path = new Path(getSourceName());
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					FileEditorInput fileEditorInput=new FileEditorInput(file);
					fTextEditor=page.openEditor(fileEditorInput, DebugCorePlugin.ID_WPP_EDITOR);
					IDocument document = ((ITextEditor)fTextEditor).getDocumentProvider().getDocument(fTextEditor.getEditorInput());
					IRegion lineInfo;
					lineInfo = document.getLineInformation(currLine);
					((ITextEditor)fTextEditor).setHighlightRange(lineInfo.getOffset(), lineInfo.getLength(), true);
				} catch (Exception e) {
					WPPException.handleException(e);
				}
			} 
		}); 
	}
	
	protected synchronized WPPThread getThread() {
		return fThread;
	}
	
	public void enableRunMenuWithStart(){
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
		new EnableMenus(enableMenuMap);
		HandlePauseResumeButton.procPauseResumeToolbarItem(1);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		new EnableButtons(enableButtonMap);
	}
	
	public void enableRunMenuWithSuspended(){
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, true);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, true);
		new EnableMenus(enableMenuMap);
		HandlePauseResumeButton.procPauseResumeToolbarItem(3);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		new EnableButtons(enableButtonMap);
	}
	
	public void enableRunMenuWithTerminate(){
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, false);
		enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, false);
		new EnableMenus(enableMenuMap);
		HandlePauseResumeButton.procPauseResumeToolbarItem(0);
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, false);
		enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, false);
		new EnableButtons(enableButtonMap);
	}
}
