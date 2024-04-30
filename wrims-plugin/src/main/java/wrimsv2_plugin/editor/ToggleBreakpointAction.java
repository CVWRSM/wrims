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
package wrimsv2_plugin.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import wrimsv2_plugin.debugger.breakpoint.WPPBreakpointAdapter;
import wrimsv2_plugin.debugger.exception.WPPException;

/**
 * Action to toggle a breakpoint
 */
public class ToggleBreakpointAction extends Action {
	
	private ITextEditor fEditor;
	private IVerticalRulerInfo fRulerInfo;

	/**
	 * Constructs a new action to toggle a WPP breakpoint
	 * 
	 * @param editor the editor in which to toggle the breakpoint
	 * @param rulerInfo specifies breakpoint location 
	 */
	public ToggleBreakpointAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		super("Toggle Line Breakpoint");
		fEditor = editor;
		fRulerInfo = rulerInfo;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		WPPBreakpointAdapter adapter = new WPPBreakpointAdapter();
		int line = fRulerInfo.getLineOfLastMouseButtonActivity();
		IDocumentProvider provider = fEditor.getDocumentProvider();
		ITextSelection selection = null;
		try {
			provider.connect(this);
			IDocument document = provider.getDocument(fEditor.getEditorInput());
			IRegion region = document.getLineInformation(line);
			selection = new TextSelection(document, region.getOffset(), region.getLength());
		} catch (CoreException e1) {
			WPPException.handleException(e1);
		} catch (BadLocationException e) {
			WPPException.handleException(e);
		} finally {
			provider.disconnect(this);
		}
		if (selection != null) {
			try {
				if (adapter.canToggleWatchpoints(fEditor, selection)) {
					adapter.toggleWatchpoints(fEditor, selection);
				} else if (adapter.canToggleLineBreakpoints(fEditor, selection)) {
					adapter.toggleLineBreakpoints(fEditor, selection);
				}
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
	}
}
