package gov.ca.dwr.hecdssvue.views;

import java.util.Vector;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.actions.ReportButtonAction;
import gov.ca.dwr.hecdssvue.actions.ReportCheckBoxAction;
import gov.ca.dwr.hecdssvue.components.DataOps;
import hec.io.DataContainer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import calsim.app.AppUtils;
import calsim.app.Project;
import calsim.gui.CalLiteGUIMainPanel;
import calsim.gui.DTSTable;
import calsim.gui.DtsTreeModel;
import calsim.gui.DtsTreePanel;
import calsim.gui.GuiTaskListener;
import calsim.gui.GuiUtils;

public class DTSView extends AbstractCalSimView{

	public static String ID="gov.ca.dwr.hecdssvue.views.DTSView";
	
	public void createPartControl(Composite parent){
		super.createPartControl(parent);
		
		DssPluginCore.dtp = new DtsTreePanel();
		DtsTreeModel dtm = DssPluginCore.dtp.getCurrentModel();
		dtm.clearVectors();
		dtm.createTreeFromPrj(null, null, "");
				
		DssPluginCore.dtp.repaint();
		contentPane.add(new JScrollPane(DssPluginCore.dtp));
		
		DataOps.setProject();
		
		final DTSTable table = DssPluginCore.dtp.getTable();
		JButton opencurrent = table.opencurrent;
		opencurrent.addActionListener(new GuiTaskListener("Retrieving...") {
			public void doWork(){
				Vector<DataContainer> v = table.retrieveData();
				showSelected(v);
			}
		});
		
		JMenuItem open = dtm.open;
		open.addActionListener(new GuiTaskListener("Retrieving...") {
			public void doWork(){
				Vector<DataContainer> v = table.retrieveData();
				showSelected(v);
			}
		});
	}
	
	protected void showSelected(final Vector<DataContainer> dataVector) {
		try {
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
					DSSTableView dssTableView = (DSSTableView) workBenchPage.findView(DSSTableView.ID);
					dssTableView.showSelected(dataVector);
					DSSMonthlyView dssMonthlyView = (DSSMonthlyView) workBenchPage.findView(DSSMonthlyView.ID);
					dssMonthlyView.showSelected(dataVector);
					DSSPlotView dssPlotView = (DSSPlotView) workBenchPage.findView(DSSPlotView.ID);
					dssPlotView.showSelected(dataVector);
					
				}
			});
		} catch (Exception e) {
			WPPException.handleException(e);
		}	
	}
}
