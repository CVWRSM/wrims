package gov.ca.dwr.hecdssvue.menus;

import java.util.Vector;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.DataOps;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import hec.dataTable.HecDataTable;
import hec.dssgui.NewPartsDialog;
import hec.heclib.dss.HecDss;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.view.WPPVarMonitorView;

public class DuplicateRecords implements IWorkbenchWindowActionDelegate{

	private Vector<String> oldPathnameVector=new Vector<String>();
	private Vector<String> newPathnameVector=new Vector<String>();		
	
	@Override
	public void run(IAction action) {
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
		final DSSCatalogView dssCatalogView=(DSSCatalogView) workBenchPage.findView(DSSCatalogView.ID);

		if (dssCatalogView !=null){
			final Vector<String[]> selectedParts=dssCatalogView.getSelectedParts();
			final Vector<String> selectedPathnames=new Vector<String>();
			for (int l=0; l<selectedParts.size(); l++){
				selectedPathnames.add(DataOps.getPathname(selectedParts.get(l)));
			}
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
			       	NewPartsDialog newParts = new NewPartsDialog("New pathname parts for duplicate records:", selectedPathnames);
			       	newParts.show();
			       	Vector newPathnames = newParts.getNewPathnames();
			       	if (newPathnames == null) {
			       		return;
			       	}
			       	boolean[] foundInDv={false, false, false, false};
			       	for (int i=0; i<3; i++){
						if (DebugCorePlugin.selectedStudies[i]){
							HecDss dvFile = DebugCorePlugin.dvDss[i];
							if (dvFile !=null){
								Vector dvPathNameList = dvFile.getPathnameList();
								Vector<String> selectedPathnames = new Vector<String>();
								for (int j=0; j<selectedParts.size(); j++){
									String[] parts=selectedParts.get(j);
									oldPathnameVector=new Vector<String>();
									newPathnameVector=new Vector<String>();	
									for (int k=0; k<dvPathNameList.size(); k++){
										String pathname=(String)dvPathNameList.get(k);
										if (containParts(pathname, parts, (String)newPathnames.get(j))){
											foundInDv[i]=true;
										}
									}
									dvFile.duplicateRecords(oldPathnameVector, newPathnameVector);
								}
							}
							HecDss svFile = DebugCorePlugin.svDss[i];
							if (!foundInDv[i] && svFile !=null){
								Vector svPathNameList = svFile.getPathnameList();
								Vector<String> selectedPathnames = new Vector<String>();
								for (int j=0; j<selectedParts.size(); j++){
									String[] parts=selectedParts.get(j);
									oldPathnameVector=new Vector<String>();
									newPathnameVector=new Vector<String>();	
									for (int k=0; k<svPathNameList.size(); k++){
										String pathname=(String)svPathNameList.get(k);
										containParts(pathname, parts, (String)newPathnames.get(j));
									}
									svFile.duplicateRecords(oldPathnameVector, newPathnameVector);
								}
							}
						}
					}
			       	final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							TableViewer viewer = dssCatalogView.getViewer();
							viewer.setInput(viewer.getInput());
						}
					});
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

	public boolean containParts(String pathName, String[] selectedParts, String newPathname){
		String start="/";
		String end="/";
		for (int i=1; i<4; i++){
			start=start+selectedParts[i]+"/";
		}
		for (int i=5; i<7; i++){
			end=end+selectedParts[i]+"/";
		}
		if (pathName.startsWith(start) && pathName.endsWith(end)){
			oldPathnameVector.add(pathName);
			int oldPart4Start=nthOccurrence(pathName, "/", 3);
			int oldPart4End=nthOccurrence(pathName, "/", 4);
			String part4=pathName.substring(oldPart4Start, oldPart4End);
			int newPart4Start=nthOccurrence(newPathname, "/", 3);
			int newPart4End=nthOccurrence(newPathname, "/", 4);
			String modNewPathname=newPathname.substring(0, newPart4Start)+part4+newPathname.substring(newPart4End);
			newPathnameVector.add(modNewPathname);
			return true;
		}else{
			return false;
		}
	}
	
	public static int nthOccurrence(String str, String c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (n-- > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
}
