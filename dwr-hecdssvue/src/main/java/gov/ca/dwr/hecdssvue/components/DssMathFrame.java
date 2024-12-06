package gov.ca.dwr.hecdssvue.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import rma.swing.RmaImage;
import wrimsv2_plugin.debugger.exception.WPPException;
import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import hec.dssgui.ListSelection;
import hec.dssgui.MathFrame2;
import hec.dssgui.MathPanel;
import hec.heclib.dss.HecDss;
import hec.hecmath.DSS;
import hec.hecmath.DSSFile;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;

public class DssMathFrame extends MathFrame2 {

	public DssMathFrame(ListSelection parent, Vector[] dataSets) {
		super(parent, dataSets);
		rebuild();
	}
	
	private class SymWindow extends java.awt.event.WindowAdapter
	{
		/**
		 *  Method Description
		 *
		 *@param  event  Description
		 */
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == DssMathFrame.this)
			{
				exitAction();
			}
		}
	}
	
	public void rebuild(){
		ActionListener[] als = super._computeButton.getActionListeners();
		for (int i=0; i<als.length; i++){
			super._computeButton.removeActionListener(als[i]);
		}
		super._computeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dssCompute();				
			}
			
		});
		
		super._closeAction=	new AbstractAction("Close"){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exitAction();			
			}
		};
		
		super._saveAction =
				new AbstractAction("Save", RmaImage.getImageIcon("Images/Save.gif"))
				{
					public void actionPerformed(ActionEvent e)
					{
						saveAction();
					}
				};
	
		super._saveAction.setEnabled(false);
		super._saveAsAction =
				new AbstractAction("Save As", RmaImage.getImageIcon("Images/saveAs.gif"))
				{
					public void actionPerformed(ActionEvent e)
					{
						saveAsAction();
					}
				};
		super._saveAsAction.setEnabled(false);
	}
	
	public void exitAction()
	{
		dispose();
	}
	
	protected void dssCompute(){
		super.compute();
		if ( isModified() )
		{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					dssSave();
				}
				
			});
			
		}
	}
	
	protected void dssSave(){
		int opt = JOptionPane.showConfirmDialog(this, "Data has changed. Save Changes?",
				"Save Changes", JOptionPane.YES_NO_OPTION);
		if ( opt == JOptionPane.YES_OPTION ) {
			dssSaveAction();
			if (_dssFileModified || _forceCatalogUpdate) {
				_dssFileModified = false;
				/*
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					WPPException.handleException(e);
				}
				*/
				updateCatalog();
			}
		}
		JOptionPane.getRootFrame().dispose();
		if (opt == JOptionPane.NO_OPTION){
			dispose();
		}
	}
	/**
	 *  saves the dataSet's copies.
	 */
	protected void dssSaveAction()
	{
		dssSaveAction(_dataSetVector);
	}
	/**
	 *
	 */
	protected void dssSaveAction(Vector dataSets)
	{
		dssSave(dataSets, false);
	}

	/**
	 *  perform a saveas on the dataset's copies
	 */
	protected void dssSaveAsAction(){
		dssSave(_dataSetVector, true);
	}

	protected void dssSave(Vector dataSets, boolean saveAs)
	{
		DataContainer dc = null;
		boolean success;
		int size = dataSets.size();
		if (size > 0)
		{
			for (int i = 0; i < size; i++ )
			{
				dc = (DataContainer)dataSets.get(i);
				if ( dc.modified )
				{
					String fileName=dc.fileName;
					try {
						HecDss dssFile= HecDss.open(fileName);			
						Vector<String> selectedPathnames = new Vector<String>();
						Vector pathNameList = dssFile.getPathnameList();
						String[] parts=dc.fullName.split("/");
						for (int k=0; k<pathNameList.size(); k++){
							String pathname=(String)pathNameList.get(k);
							if (DataOps.containParts(pathname, parts)){
								selectedPathnames.add(pathname);
							}
						}
						dssFile.delete(selectedPathnames);
						dssFile.write(dc);
						_dssFileModified=true;
					} catch (Exception e) {
						WPPException.handleException(e);
					}
				}
			}
		}else{
			setModified(false);
		}
	}
	
	public void updateCatalog() {
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable(){
			public void run(){
				DSSCatalogView dssCatalogView = (DSSCatalogView)workbench.getActiveWorkbenchWindow().getActivePage().findView(DSSCatalogView.ID);
				TableViewer viewer = dssCatalogView.getViewer();
				viewer.setInput(viewer.getInput());
			}
		});
	}
}
