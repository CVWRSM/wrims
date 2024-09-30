package gov.ca.dwr.hecdssvue.menus;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.CatalogListSelection;
import gov.ca.dwr.hecdssvue.components.DssMathFrame;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import hec.dataTable.HecDataTable;
import hec.dssgui.ListSelection;
import hec.dssgui.MathFrame2;
import hec.dssgui.NewPartsDialog;
import hec.heclib.dss.HecDss;
import hec.io.DataContainer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.WPPVarMonitorView;

public class CopyTo implements IWorkbenchWindowActionDelegate{		
	
	@Override
	public void run(IAction action) {
		final CatalogListSelection ls = new CatalogListSelection();
		ls.setDirectory(DssPluginCore.lastCopiedDssFolder);
		ls.copyRecords(true);
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
