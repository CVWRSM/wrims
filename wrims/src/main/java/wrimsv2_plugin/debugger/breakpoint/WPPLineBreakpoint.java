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
package wrimsv2_plugin.debugger.breakpoint;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.LineBreakpoint;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.IWPPEventListener;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPThread;

/**
 * WPP line breakpoint
 */
public class WPPLineBreakpoint extends LineBreakpoint implements IWPPEventListener {
	
	// target currently installed in
	private WPPDebugTarget fTarget;
	private String fileFullPath;
	
	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public WPPLineBreakpoint() {
	}
	
	/**
	 * Constructs a line breakpoint on the given resource at the given
	 * line number. The line number is 1-based (i.e. the first line of a
	 * file is line number 1). The WPP VM uses 0-based line numbers,
	 * so this line number translation is done at breakpoint install time.
	 * 
	 * @param resource file on which to set the breakpoint
	 * @param lineNumber 1-based line number of the breakpoint
	 * @throws CoreException if unable to create the breakpoint
	 */
	public WPPLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("wpp.markerType.lineBreakpoint");
				fileFullPath=resource.getLocation().toString();
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: " + resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return DebugCorePlugin.ID_WPP_DEBUG_MODEL;
	}
	
	/**
	 * Returns whether this breakpoint is a run-to-line breakpoint
	 * 
	 * @return whether this breakpoint is a run-to-line breakpoint
	 */
	public boolean isRunToLineBreakpoint() {
		return false;
	}
    
    /**
     * Installs this breakpoint in the given interprettor.
     * Registeres this breakpoint as an event listener in the
     * given target and creates the breakpoint specific request.
     * 
     * @param target WPP interprettor
     * @throws CoreException if installation fails
     */
    public void install(WPPDebugTarget target) throws CoreException {
    	fTarget = target;
    	target.addEventListener(this);
    	createRequest(target);
    }
    
    /**
     * Create the breakpoint specific request in the target. Subclasses
     * should override.
     * 
     * @param target WPP interprettor
     * @throws CoreException if request creation fails
     */
    protected void createRequest(WPPDebugTarget target) throws CoreException {
    	String request="set:" + (getLineNumber() - 1)+":"+this.getMarker().getResource().getFullPath();
    	target.sendRequest(request);
    }
    
    /**
     * Removes this breakpoint's event request from the target. Subclasses
     * should override.
     * 
     * @param target WPP interprettor
     * @throws CoreException if clearing the request fails
     */
    protected void clearRequest(WPPDebugTarget target) throws CoreException {
    	target.sendRequest("clear:" + (getLineNumber() - 1));
    }
    
    /**
     * Removes this breakpoint from the given interprettor.
     * Removes this breakpoint as an event listener and clears
     * the request for the interprettor.
     * 
     * @param target WPP interprettor
     * @throws CoreException if removal fails
     */
    public void remove(WPPDebugTarget target) throws CoreException {
    	target.removeEventListener(this);
    	clearRequest(target);
    	fTarget = null;
    	
    }
    
    /**
     * Returns the target this breakpoint is installed in or <code>null</code>.
     * 
     * @return the target this breakpoint is installed in or <code>null</code>
     */
    protected WPPDebugTarget getDebugTarget() {
    	return fTarget;
    }
    
    /**
     * Notify's the WPP interprettor that this breakpoint has been hit.
     */
    protected void notifyThread() {
    	if (fTarget != null) {
			try {
				IThread[] threads = fTarget.getThreads();
				if (threads.length == 1) {
	    			WPPThread thread = (WPPThread)threads[0];
	    			thread.suspendedBy(this);
	    		}
			} catch (DebugException e) {
				WPPException.handleException(e);
			}    		
    	}
    }

	/* (non-Javadoc)
	 * 
	 * Subclasses should override to handle their breakpoint specific event.
	 * 
	 * @see example.debug.core.model.IWPPEventListener#handleEvent(java.lang.String)
	 */
	public void handleEvent(String event) {
		if (event.startsWith("suspended:")) {
			handleHit(event);
		}
	}
    
	/**
     * Determines if this breakpoint was hit and notifies the thread.
     * 
     * @param event breakpoint event
     */
    private void handleHit(String event) {
    	int lastSpace = event.lastIndexOf(' ');
    	if (lastSpace > 0) {
    		String line = event.substring(lastSpace + 1);
    		int lineNumber = Integer.parseInt(line);
    		// breakpoints event line numbers are 0 based, model objects are 1 based
    		lineNumber++;
    		try {
				if (getLineNumber() == lineNumber) {
					notifyThread();
				}
    		} catch (CoreException e) {
    			WPPException.handleException(e);
    		}
    	}
    }	
    
    private String getFileFullPath(){
    	return fileFullPath;
    }
}
