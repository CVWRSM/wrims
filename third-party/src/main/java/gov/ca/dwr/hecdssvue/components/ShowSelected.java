package gov.ca.dwr.hecdssvue.components;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSMonthlyView;
import gov.ca.dwr.hecdssvue.views.DSSPlotView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import hec.io.DataContainer;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.exception.WPPException;

public class ShowSelected {

	public static void showSelected(){
	
		if (DssPluginCore.selectedCheckBox.size()==0){
			try {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						try {
							DSSCatalogView catalogView = (DSSCatalogView) workbench.getActiveWorkbenchWindow()
									.getActivePage().findView(DSSCatalogView.ID);
							Iterator iterator = ((IStructuredSelection) catalogView.getViewer().getSelection())
									.iterator();
							Vector<DataContainer> dataVector = new Vector();
							Vector<DataContainer> dataVector_path = new Vector();
							while(iterator.hasNext()){
								String[] parts = (String[]) iterator.next();
								// 	read 1 file
								//	DataContainer data = catalogView.getData(catalogView.getPathname(parts));
								//	if (data == null) {
								//		continue;
								//	}
								//	dataVector.add(data);
								// read multiple files
								dataVector_path = catalogView.getData(DataOps.getPathname(parts), parts);
								if (dataVector_path == null) {
									continue;
								}
								dataVector.addAll(dataVector_path);
							}
					
					
							DSSPlotView dpv = (DSSPlotView) workbench.getActiveWorkbenchWindow()
									.getActivePage().findView(DSSPlotView.ID);
							dpv.showSelected(dataVector);
					
							DSSTableView dtv = (DSSTableView) workbench.getActiveWorkbenchWindow()
									.getActivePage().findView(DSSTableView.ID);
							dtv.showSelected(dataVector);
			
							DSSMonthlyView mv = (DSSMonthlyView) workbench.getActiveWorkbenchWindow()
									.getActivePage().findView(DSSMonthlyView.ID);
							mv.showSelected(dataVector);
						} catch (Exception e) {
							WPPException.handleException(e);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			new RetrieveCheckBoxTsData();
		}
	}
}
