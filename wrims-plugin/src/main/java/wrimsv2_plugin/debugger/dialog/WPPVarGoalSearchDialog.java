package wrimsv2_plugin.debugger.dialog;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.view.WPPAllGoalView;
import wrimsv2_plugin.debugger.view.WPPAllVariableView;
import wrimsv2_plugin.debugger.view.WPPGoalView;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.tools.SearchTable;

public class WPPVarGoalSearchDialog extends Dialog {
	
	private IViewPart view;
	
	public WPPVarGoalSearchDialog(Shell parent, IViewPart view) {
		super(parent, SWT.MIN|SWT.RESIZE);
		this.view=view;
		setText("Search");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(250, 170);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	protected void createContents(final Shell shell) {
		FillLayout layout=new FillLayout(SWT.VERTICAL);
		layout.marginHeight=15;
		layout.marginWidth=20;
		shell.setLayout(layout);
		
		Label label1=new Label(shell, SWT.NONE);
		label1.setText("Search:");
		
		final Text text1=new Text(shell, SWT.BORDER);
		text1.setText(DebugCorePlugin.textVarGoalSearch);
				
		RowLayout layout1=new RowLayout(SWT.HORIZONTAL);
		layout1.fill=true;
		//layout.pack=true;
		Composite line3=new Composite(shell, SWT.NONE);
		line3.setLayout(layout1);
		Button ok = new Button(line3, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String textString=text1.getText();
				DebugCorePlugin.textVarGoalSearch=textString;
				if (view instanceof WPPVariableView){
					search(textString);
				}else if (view instanceof WPPAllVariableView){
					search(textString);
				}else if (view instanceof WPPGoalView){
					search(textString);
				}else if (view instanceof WPPAllGoalView){
					search(textString);
				}
				shell.close();
			}
		});
		
		Button cancel = new Button(line3, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	 }
	
	public void search(String text){
		TableViewer viewer;
	
		if (view instanceof WPPVariableView){
			viewer=(TableViewer) ((WPPVariableView)view).getViewer();
		}else if (view instanceof WPPAllVariableView){
			viewer=(TableViewer) ((WPPAllVariableView)view).getViewer();
		}else if (view instanceof WPPGoalView){
			viewer=(TableViewer) ((WPPGoalView)view).getViewer();
		}else{ 
			viewer=(TableViewer) ((WPPAllGoalView)view).getViewer();
		}
	
		Table table = viewer.getTable();
		TableItem[] tableItems = table.getItems();
		int length=table.getItemCount();
		if (length>0){
			int currIndex;
			TableItem[] selections = table.getSelection();
			if (selections.length>0){			
				currIndex=SearchTable.search(tableItems, 0, length, selections[0].getText(), true, false);
			}else{
				currIndex=length;
			}
			int foundIndex=SearchTable.search(tableItems, currIndex+1, length, text, false, true);
			if (foundIndex==-1){
				foundIndex=SearchTable.search(tableItems, 0, currIndex, text, false, true);
				if (foundIndex==-1){
					showNotFound(text);
				}else{
					TableItem[] items=new TableItem[1];
					items[0]=tableItems[foundIndex];
					table.setSelection(items);
				}
			}else{
				TableItem[] items=new TableItem[1];
				items[0]=tableItems[foundIndex];
				table.setSelection(items);
			}
		}else{
			showNotFound(text);
		}
	}
	
	public void showNotFound(final String text){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Warning");
				messageBox.setMessage("\""+text+"\""+" is not found in variable/goal view");
				messageBox.open();
			}
		});
	}
}
