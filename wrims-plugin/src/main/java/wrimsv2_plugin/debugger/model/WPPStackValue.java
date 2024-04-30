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

/**
 * A value on the data stack
 */
public class WPPStackValue extends WPPValue {
    
    private int fIndex;
    
    /**
     * Constructs a value that appears on the data stack
     * 
     * @param target debug target
     * @param value value on the stack
     * @param index index on the stack
     */
	public WPPStackValue(WPPDebugTarget target, String value, int index) {
		super(target, value);
		fIndex = index;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
	public boolean equals(Object obj) {
        return super.equals(obj) && ((WPPStackValue)obj).fIndex == fIndex;
    }
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return super.hashCode() + fIndex;
    }
}
