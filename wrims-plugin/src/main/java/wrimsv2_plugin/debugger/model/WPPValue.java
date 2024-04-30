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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Value of a WPP variable.
 */
public class WPPValue extends WPPDebugElement implements IValue {
	
	private String fValue;
	private String fVariable;
	private WPPDebugTarget fTarget;
	private IValue parentValue=null;
	
	public WPPValue(WPPDebugTarget target, String value) {
		super(target);
		fValue = value;
		fTarget=target;
		parentValue=null;
	}
	
	public WPPValue(WPPDebugTarget target, String value, String variable) {
		super(target);
		fValue = value;
		fVariable=variable;
		fTarget=target;
		parentValue=null;
	}
	
	public WPPValue(WPPDebugTarget target, IValue parentValue, String value, String variable) {
		super(target);
		fValue = value;
		fVariable=variable;
		fTarget=target;
		this.parentValue=parentValue;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		try {
			Integer.parseInt(fValue);
		} catch (NumberFormatException e) {
			return "text";
		}
		return "integer";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		return fValue;
	}
	
	public String getVariableString() {
		return fVariable;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0];
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		if (fValue.contains(":")){ 
			return true;
		}else{
			return false;
		}
	}
	
	public IValue[] getValues(){
		String[] dataStrings=fValue.split(":");
		int size=dataStrings.length;
		WPPValue[] values=new WPPValue[size];  
		for (int i=0; i<size; i++){
			String[] dataSubStrings=dataStrings[i].split("%",2);
			WPPValue value=new WPPValue(fTarget,this, dataSubStrings[1], dataSubStrings[0]); 
			values[i]=value;
		}
		return values;
	}
	
	public boolean hasParentValue(){
		if (parentValue==null){
			return false;
		}else{
			return true;
		}
	}
	
	public IValue getParentValue(){
		return parentValue;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
	public boolean equals(Object obj) {
        return obj instanceof WPPValue && ((WPPValue)obj).fValue.equals(fValue);
    }
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return fValue.hashCode();
    }
}
