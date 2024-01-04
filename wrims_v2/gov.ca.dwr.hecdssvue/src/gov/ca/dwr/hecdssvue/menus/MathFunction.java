package gov.ca.dwr.hecdssvue.menus;

import java.util.Vector;

import javax.swing.SwingUtilities;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.CatalogListSelection;
import gov.ca.dwr.hecdssvue.components.DataOps;
import gov.ca.dwr.hecdssvue.components.DssMathFrame;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import hec.io.DataContainer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public class MathFunction implements IWorkbenchWindowActionDelegate{
	
	@Override
	public void run(IAction action) {
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
		DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DssPluginCore.ID_DSSVue_DSSCatalogView);
		
		if (dssCatalogView !=null){
			final Vector<String[]> selectedParts=dssCatalogView.getSelectedParts();			
			int size = selectedParts.size();
			Vector<DataContainer>[] dataVector = new Vector[1];
			Vector<DataContainer> dataVector_path = new Vector();
			dataVector[0]=new Vector<DataContainer>();
			for (int i=0; i<size; i++){
				String[] parts = selectedParts.get(i);
				dataVector_path = dssCatalogView.getData(DataOps.getPathname(parts), parts);
				dataVector[0].addAll(dataVector_path);
			}
			
		    CatalogListSelection ls = new CatalogListSelection();
		    final DssMathFrame mathFrame = new DssMathFrame(ls, dataVector);
		    SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
				    mathFrame.show();
				}
		    	
		    });
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}
}
