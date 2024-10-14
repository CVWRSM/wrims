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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import wrimsv2_plugin.debugger.exception.WPPException;

/**
 * A WPP thread. A WPP VM is single threaded.
 */
public class WPPThread extends WPPDebugElement implements IThread, IWPPEventListener {
	
	/**
	 * Breakpoint this thread is suspended at or <code>null</code>
	 * if none.
	 */
	private IBreakpoint fBreakpoint;
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;
	
	/**
	 * Wether this thread is suspended
	 */
	private boolean fSuspended = false;

	/**
	 * Most recent error event or <code>null</code>
	 */
	private String fErrorEvent;
	
	/**
	 * Table mapping stack frames to current variables
	 */
	private Map fVariables = new HashMap();
	
	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param target VM
	 */
	public WPPThread(WPPDebugTarget target) {
		super(target);
		getWPPDebugTarget().addEventListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			String framesData = sendRequest("stack");
			if (framesData != null) {
				String[] frames = framesData.split("#");
				IStackFrame[] theFrames = new IStackFrame[frames.length];
				for (int i = 0; i < frames.length; i++) {
					String data = frames[i];
					theFrames[frames.length - i - 1] = new WPPStackFrame(this, data, i);
				}
				return theFrames;
			}
		}
		return new IStackFrame[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	@Override
	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	@Override
	public int getPriority() throws DebugException {
		return 0;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0) {
			return frames[0];
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	@Override
	public String getName() {
		return "Main thread";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	@Override
	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoint == null) {
			return new IBreakpoint[0];
		}
		return new IBreakpoint[]{fBreakpoint};
	}
	
	/**
	 * Notifies this thread it has been suspended by the given breakpoint.
	 * 
	 * @param breakpoint breakpoint
	 */
	public void suspendedBy(IBreakpoint breakpoint) {
		fBreakpoint = breakpoint;
		suspended(DebugEvent.BREAKPOINT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume() {
		return isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend() {
		return !isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return fSuspended && !isTerminated();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException {
		setSuspended(false);
		sendRequest("resume");
	}
	
	public void resimCycle(String parameters) throws DebugException {
		setSuspended(false);
		sendRequest("resim_cycle:"+parameters);
	}
	
	public void resimDate(String parameters) throws DebugException {
		setSuspended(false);
		sendRequest("resim_date:"+parameters);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException {
	    sendRequest("suspend");
	}
	
	public void pause() throws DebugException {
	    sendRequest("pause");
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	@Override
	public boolean canStepInto() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	@Override
	public boolean canStepOver() {
		return isSuspended();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping() {
		return fStepping;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException {
		sendRequest("step");
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException {
		sendRequest("terminate");
	}
	
	/**
	 * Sets whether this thread is stepping
	 * 
	 * @param stepping whether stepping
	 */
	private void setStepping(boolean stepping) {
		fStepping = stepping;
	}
	
	/**
	 * Sets whether this thread is suspended
	 * 
	 * @param suspended whether suspended
	 */
	private void setSuspended(boolean suspended) {
		fSuspended = suspended;
	}

	/**
	 * Sets the most recent error event encountered, or <code>null</code>
	 * to clear the most recent error
	 * 
	 * @param event one of 'unimpinstr' or 'nosuchlabel' or <code>null</code>
	 */
	private void setError(String event) {
		fErrorEvent = event;
	}

	/**
	 * Returns the most revent error event encountered since the last
	 * suspend, or <code>null</code> if none.
	 * 
	 * @return the most revent error event encountered since the last
	 * suspend, or <code>null</code> if none
	 */
	public Object getError() {
		return fErrorEvent;
	}

	/* (non-Javadoc)
	 * @see example.debug.core.model.IWPPEventListener#handleEvent(java.lang.String)
	 */
	@Override
	public void handleEvent(String event) {
		// clear previous state
		fBreakpoint = null;
		setStepping(false);
		
		// handle events
		if (event.startsWith("resumed")) {
			setSuspended(false);
			if (event.endsWith("step")) {
				setStepping(true);
				resumed(DebugEvent.STEP_OVER);
			} else if (event.endsWith("client")) {
				resumed(DebugEvent.CLIENT_REQUEST);
			}
			else if (event.endsWith("drop")) {
				resumed(DebugEvent.STEP_RETURN);
			}
		} else if (event.startsWith("suspended")) {
			setSuspended(true);
			if (event.endsWith("client")) {
				suspended(DebugEvent.CLIENT_REQUEST);
			} else if (event.endsWith("step")) {
				suspended(DebugEvent.STEP_END);
			} else if (event.startsWith("suspended event") && getError() != null) {
				exceptionHit();
			} 
			else if (event.endsWith("drop")) {
				suspended(DebugEvent.STEP_END);
			}
		} else if (event.equals("started")) {
			fireCreationEvent();
		} else {
			setError(event);
		}
		
	}
	
	/**
	 * Notification the target has resumed for the given reason.
	 * Clears any error condition that was last encountered and
	 * fires a resume event, and clears all cached variables
	 * for stack frames.
	 * 
	 * @param detail reason for the resume
	 */
	private void resumed(int detail) {
		setError(null);
		synchronized (fVariables) {
			fVariables.clear();
		}
		fireResumeEvent(detail);
	}
	
	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail reason for the suspend
	 */
	private void suspended(int detail) {
		fireSuspendEvent(detail);
	}

	/**
     * Notification an error was encountered. Fires a breakpoint
     * suspend event.
     */
    private void exceptionHit() {
    	suspended(DebugEvent.BREAKPOINT);
    }  
	
	/**
	 * Sets the current variables for the given stack frame. Called
	 * by WPP stack frame when it is created.
	 * 
	 * @param frame
	 * @param variables
	 */
	protected void setVariables(IStackFrame frame, IVariable[] variables) {
		synchronized (fVariables) {
			fVariables.put(frame, variables);
		}
	}
	
	/**
	 * Returns the current variables for the given stack frame, or
	 * <code>null</code> if none.
	 * 
	 * @param frame stack frame
	 * @return variables or <code>null</code>
	 */
	protected IVariable[] getVariables(IStackFrame frame) {
		synchronized (fVariables) {
			return (IVariable[]) fVariables.get(frame);
		}
	}
	
	/**
	 * Pops the top frame off the callstack.
	 *
	 * @throws DebugException
	 */
	public void pop() throws DebugException {
		sendRequest("drop");
	}
	
	/**
	 * Returns whether this thread can pop the top stack frame.
	 *
	 * @return whether this thread can pop the top stack frame
	 */
	public boolean canPop() {
		try {
			return getStackFrames().length > 1;
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
		return false;
	}
}
