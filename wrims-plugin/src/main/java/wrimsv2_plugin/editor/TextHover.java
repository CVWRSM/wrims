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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPStackFrame;
import wrimsv2_plugin.debugger.model.WPPThread;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.debugger.model.WPPVariable;

/**
 * Produces debug hover for the WPP debugger.
 */
public class TextHover implements ITextHover {

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        String varName = null;
        try {
            varName = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
        } catch (BadLocationException e) {
           return null;
        }
   
        WPPValue[] dataStack=(WPPValue[]) DebugCorePlugin.variableStack;
        if (dataStack==null) return null;
        
        for (int i = 0; i < dataStack.length; i++) {
            if (varName.equals(dataStack[i].getVariableString())){
				String hoverInfo=varName+"=";
            	try {
					if (dataStack[i].hasVariables()){
						WPPValue[] subValues=(WPPValue[]) dataStack[i].getValues();
						for (int j=0; j<subValues.length; j++){
							hoverInfo=hoverInfo+subValues[j].getVariableString()+": "+subValues[j].getValueString()+"; ";
						}
						return hoverInfo;
					}else{
						hoverInfo=varName+"="+dataStack[i].getValueString();
						return hoverInfo;
					}
				} catch (DebugException e) {
					WPPException.handleException(e);
				}
            }
        }

        return null;
    }
    	
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
     */
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return WordFinder.findWord(textViewer.getDocument(), offset);
    }

}
