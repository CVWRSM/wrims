package gov.ca.dwr.hecdssvue.actions;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.ClearAllCheckBox;
import gov.ca.dwr.hecdssvue.views.DSSMonthlyView;
import gov.ca.dwr.hecdssvue.views.DSSPlotView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import gov.ca.dwr.hecdssvue.views.DeliveryShortagesView;
import gov.ca.dwr.hecdssvue.views.SanJoaquinRiverView;
import gov.ca.dwr.hecdssvue.views.StorageFlowsView;
import gov.ca.dwr.hecdssvue.views.WaterManagementActionsView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.exception.WPPException;



public class ReportButtonAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b=(JButton)e.getSource();
		String bn=b.getName();
		
		if (bn.equalsIgnoreCase("btnpClear") || bn.equalsIgnoreCase("btnpClear_SJR") || bn.equalsIgnoreCase("btnpClear_shortage")){
			DssPluginCore.selectedCheckBox=new ArrayList<String>();
			new ClearAllCheckBox();
		}
	}

}
