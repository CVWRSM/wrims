package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.dts.DTSTable;
import gov.ca.dwr.hecdssvue.dts.DtsTreeModel;
import gov.ca.dwr.hecdssvue.dts.DtsTreePanel;
import gov.ca.dwr.hecdssvue.dts.GuiTaskListener;
import gov.ca.dwr.hecdssvue.components.DataOps;
import hec.io.DataContainer;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import wrimsv2_plugin.debugger.exception.WPPException;

public class DTSView extends AbstractCalSimView{

	public static String ID="gov.ca.dwr.hecdssvue.views.DTSView";

	@Override
	public void createPartControl(Composite parent){
		super.createPartControl(parent);

		DtsTreePanel dtp = new DtsTreePanel();
		DtsTreeModel dtm = dtp.getCurrentModel();
		dtm.clearVectors();
		dtm.createTreeFromPrj(null, null);
				
		dtp.repaint();
		contentPane.add(new JScrollPane(dtp));
		
		DataOps.setProject();
		
		final DTSTable table = dtp.getTable();
		JButton opencurrent = table.opencurrent;
		opencurrent.addActionListener(new GuiTaskListener() {
			public void doWork(){
				Vector<DataContainer> v = table.retrieveData();
				showSelected(v);
			}
		});
		
		JMenuItem open = dtm.open;
		open.addActionListener(new GuiTaskListener() {
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
