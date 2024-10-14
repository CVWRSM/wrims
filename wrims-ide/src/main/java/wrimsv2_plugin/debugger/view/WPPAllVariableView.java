package wrimsv2_plugin.debugger.view;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.listener.TableCopyListener;
import wrimsv2_plugin.debugger.model.IWPPEventListener;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.debugger.view.WPPAllGoalView.ViewLabelProvider;
import wrimsv2_plugin.debugger.view.WPPAllGoalView.WPPComparator;
import wrimsv2_plugin.tools.SearchTable;
import wrimsv2_plugin.tools.SetSelectionInTable;

public class WPPAllVariableView extends AbstractDebugView implements ISelectionListener { 
	private IValue[] dataStack=null;
	private Table table;
	private ViewLabelProvider vlp;
	static public boolean isAscending=true;
	
	public class ViewLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object element, int index) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getColumnText(Object element, int index) {
			if (element instanceof WPPDebugTarget){
				return null;
			}else if (element instanceof WPPValue){
				try {
					if (((WPPValue) element).hasVariables()){
						if (index==0){
							return ((WPPValue) element).getVariableString();
						}else{
							return ((WPPValue) element).getValueString();
						}
					}else{
						if (index==0){
							return ((WPPValue) element).getVariableString();
						}else{	
							return ((WPPValue) element).getValueString();
						}
					}
				} catch (DebugException e) {
					WPPException.handleException(e);
				}
			}
			return null;
		}
		
	}
	
	class ViewContentProvider implements ITreeContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof WPPDebugTarget) {
				return DebugCorePlugin.allVariableStack;
			} else if (parentElement instanceof WPPValue){
				try{
					if (((WPPValue)parentElement).hasVariables()){
						return ((WPPValue)parentElement).getValues();
					}
				}
				catch (DebugException e) {
					WPPException.handleException(e);
				}
			}
			return new Object[0];
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof IDebugTarget) {
				return null;
			} else if (((WPPValue)element).hasParentValue()) {
				return (((WPPValue)element).getParentValue());
			} else {
				return ((IDebugElement)element).getDebugTarget();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			if (element instanceof IDebugElement) {
				return getChildren(element).length > 0;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	
	class WPPComparator extends ViewerComparator{
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			String s1 = vlp.getColumnText(e1, 0);
			String s2 = vlp.getColumnText(e2, 0);
			int c =s1.compareTo(s2);
			if (isAscending){
				return c;
			}else{
				return -c;
			}
		}
	}
	
	@Override
	protected Viewer createViewer(Composite parent) {
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				WPPVarDetailView varDetailView = (WPPVarDetailView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_VARIABLEDETAIL_VIEW);
			}
		});
		
		TableViewer viewer = new TableViewer(parent);
		vlp=new ViewLabelProvider();
		viewer.setLabelProvider(vlp);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setComparator(new WPPComparator());
		getSite().setSelectionProvider(viewer);
		table = viewer.getTable();
	    new TableColumn(table, SWT.LEFT).setText("Variable");
	    new TableColumn(table, SWT.LEFT).setText("Value");
	    
	    // Pack the columns
	    for (int i = 0, n = table.getColumnCount(); i < n; i++) {
	    	table.getColumn(i).pack();
	    }

	    // Turn on the header and the lines
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    TableCopyListener tcl=new TableCopyListener(table);
	    table.addKeyListener(tcl);
	    
	    UpdateView.addNameSortListener(table, this);
	    
	    // Pack the window
	    parent.pack();
	    
		return viewer;
	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getHelpContextId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void fillContextMenu(IMenuManager menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureToolBar(IToolBarManager tbm) {
		// TODO Auto-generated method stub
		
	}
	
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, this);
		super.dispose();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}
	
	public void updateView(){
		DebugCorePlugin.updateSelectedVariable=false;
		dataStack=DebugCorePlugin.allVariableStack;
		TableViewer viewer=(TableViewer) getViewer();
		IStructuredSelection oldSelection = ((IStructuredSelection)viewer.getSelection());
		viewer.setInput(DebugCorePlugin.target);
		table=viewer.getTable();
		ProcessAltColumn.removeAltColumns(table);
	    for (int i = 0, n = table.getColumnCount(); i < n; i++) {
	    	table.getColumn(i).pack();
	    }
		viewer.refresh();
		if (dataStack.length>0) new SetSelectionInTable(oldSelection, viewer, table);
		DebugCorePlugin.updateSelectedVariable=true;
	}
	
	public Table getTable(){
		return table;
	}
	
	public void showDssAlt(){
		TableViewer viewer=(TableViewer) getViewer();
		ProcessAltColumn.AdjustAltColumnNames(viewer, 2);
		fillDssAltData();
	}
	
	public void fillDssAltData(){
		int size=table.getItemCount();
		int numCol = table.getColumnCount();
		for (int index=2; index<numCol; index++){
			for (int i=0; i<size; i++){
				TableItem ti = table.getItem(i);
				String vn = ti.getText(0);
				ti.setText(index, ProcessAltColumn.addAltColumnData(vn, 2, index));
			}
		}
		table.redraw();
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		getViewer().getControl().setFocus();
	}
}
