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
package wrimsv2_plugin.debugger.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

/**
 * Tab group for a WRESL/WRIMS application
 */
public class WPPTabGroup extends AbstractLaunchConfigurationTabGroup {
	
	private WPPMainTab mainTab;
	private WPPConfigTab configTab;
	private WPPWsiDiTab wsidiTab;
	private WPPPATab paTab;
	private WPPMSTab msTab;
	private WPPInfeasibilityTab infeasibilityCheckTab;
	private WPPSQLTab sqlTab;
	private WPPSensitivityTab sensitivityTab;
	private SourceLookupTab sourceLookupTab;
	private CommonTab commonTab;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		mainTab=new WPPMainTab();
		configTab=new WPPConfigTab();
		wsidiTab=new WPPWsiDiTab(mainTab);
		paTab=new WPPPATab(mainTab);
		msTab=new WPPMSTab();
		infeasibilityCheckTab=new WPPInfeasibilityTab();
		sqlTab=new WPPSQLTab();
		sensitivityTab = new WPPSensitivityTab();
		sourceLookupTab=new SourceLookupTab();
		commonTab=new CommonTab();
		
		setTabs(new ILaunchConfigurationTab[] {
				mainTab,
				configTab,
				wsidiTab,
				paTab,
				msTab,
				infeasibilityCheckTab,
				sqlTab,
				sensitivityTab,
				sourceLookupTab,
				commonTab
		});
	}
	
	public WPPMainTab getMainTab(){
		return mainTab;
	}
	
	public WPPConfigTab getConfigTab(){
		return configTab;
	}
	
	public WPPWsiDiTab getWsiDiTab(){
		return wsidiTab;
	}
	
	public WPPPATab getPATab(){
		return paTab;
	}
	
	public WPPMSTab getMSTab(){
		return msTab;
	}
	
	public WPPSQLTab getSQLTab(){
		return sqlTab;
	}
	
	public WPPSensitivityTab getSensitivityTab(){
		return sensitivityTab;
	}
	
	public SourceLookupTab getSourceLookupTab(){
		return sourceLookupTab;
	}
	
	public CommonTab getCommonTab(){
		return commonTab;
	}
}
