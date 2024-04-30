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
package wrimsv2_plugin.presentation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.breakpoint.WPPLineBreakpoint;
import wrimsv2_plugin.debugger.breakpoint.WPPWatchpoint;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPStackFrame;
import wrimsv2_plugin.debugger.model.WPPThread;

/**
 * Renders PDA debug elements
 */
public class WPPModelPresentation extends LabelProvider implements IDebugModelPresentation {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attribute, Object value) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof WPPDebugTarget) {
			return getTargetText((WPPDebugTarget)element);
		} else if (element instanceof WPPThread) {
	        return getThreadText((WPPThread)element);
	    } else if (element instanceof WPPStackFrame) {
	        return getStackFrameText((WPPStackFrame)element);
	    } else if (element instanceof WPPWatchpoint) {
	        return getWatchpointText((WPPWatchpoint)element);
	    }
		return null;
	}
	
	/**
	 * Returns a label for the given watchpoint.
	 * 
     * @param watchpoint
     * @return a label for the given watchpoint
     */
    private String getWatchpointText(WPPWatchpoint watchpoint) {
        try {
	        String label = watchpoint.getVariableName() + " (" + watchpoint.getFunctionName() + ")";
	        if (watchpoint.isAccess()) {
	            label += " [read]";
	        }
	        if (watchpoint.isModification()) {
	            label += " [write]";
	        }
	        return label;
        } catch (CoreException e) {
            return null;
        } 
    }
    /**
	 * Returns a label for the given debug target
	 * 
	 * @param target debug target
	 * @return a label for the given debug target
	 */
	private String getTargetText(WPPDebugTarget target) {
		try {
			String pgmPath = target.getLaunch().getLaunchConfiguration().getAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM, (String)null);
			if (pgmPath != null) {
			    IPath path = new Path(pgmPath);
			    String label = "";
			    if (target.isTerminated()) {
			    	label = "<terminated>";
			    }
			    return label + "WPP [" + path.lastSegment() + "]";
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		return "WPP";
		
	}
	
	/**
	 * Returns a label for the given stack frame
	 * 
	 * @param frame a stack frame
	 * @return a label for the given stack frame 
	 */
	private String getStackFrameText(WPPStackFrame frame) {
	    try {
	       return frame.getName() + " (line: " + frame.getLineNumber() + ")"; 
	    } catch (DebugException e) {
	    	WPPException.handleException(e);
	    }
	    return null;

	}
	
	/**
	 * Returns a label for the given thread
	 * 
	 * @param thread a thread
	 * @return a label for the given thread
	 */
	private String getThreadText(WPPThread thread) {
	    String label = thread.getName();
	    if (thread.isStepping()) {
	        label += " (stepping)";
	    } else if (thread.isSuspended()) {
	        IBreakpoint[] breakpoints = thread.getBreakpoints();
	        if (breakpoints.length == 0) {
	        	if (thread.getError() == null) {
	        		label += " (suspended)";
	        	} else {
	        		label += " (" + thread.getError() + ")";
	        	}
	        } else {
	            IBreakpoint breakpoint = breakpoints[0]; // there can only be one in WPP
	            if (breakpoint instanceof WPPLineBreakpoint) {
	            	WPPLineBreakpoint wppBreakpoint = (WPPLineBreakpoint) breakpoint;
	            	if (wppBreakpoint instanceof WPPWatchpoint) {
	            	    try {
		            	    WPPWatchpoint watchpoint = (WPPWatchpoint)wppBreakpoint;
		            	    label += " (watchpoint: " + watchpoint.getSuspendType() + " " + watchpoint.getVariableName() + ")";
	            	    } catch (CoreException e) {
	            	    	WPPException.handleException(e);
	            	    }
	            	} else if (wppBreakpoint.isRunToLineBreakpoint()) {
	            		label += " (run to line)";
	            	} else {
	            		label += " (suspended at line breakpoint)";
	            	}
	            }
	        }
	    } else if (thread.isTerminated()) {
	        label = "<terminated> " + label;
	    }
	    return label;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
		listener.detailComputed(value, detail);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return DebugCorePlugin.ID_WPP_EDITOR;
		}
		return null;
	}
}
