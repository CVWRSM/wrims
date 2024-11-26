package wrimsv2_plugin.debugger.view;

import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;

public class WPPExceptionView extends ViewPart implements ISelectionListener{
	private List list;
	
	public WPPExceptionView(){
		super();
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createPartControl(Composite parent) {
		list=new List(parent,1);
	}
	
	public void addException(Exception e){
		String message = e.getMessage();
		if(message!=null)
		{
			list.add(e.getMessage());
		}
		else
		{
			list.add("Unknown Exception");
		}
	}

	@Override
	public void setFocus() {
		// TODO: DWR Review Change
		//This was causing runtime exceptions on load.
		//Tracking under github issue: https://github.com/CentralValleyModeling/wrims/issues/154
		//getSite().getPart().setFocus();
		try
		{
			list.setFocus();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
