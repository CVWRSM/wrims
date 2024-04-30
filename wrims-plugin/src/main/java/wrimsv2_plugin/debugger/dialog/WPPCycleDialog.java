package wrimsv2_plugin.debugger.dialog;

import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.WPPVarDetailView;

public class WPPCycleDialog extends Dialog {

	private int row;
	private int col;
	private TableItem item;
	private Table table;
	private String varName;
	private String input;
	
	public WPPCycleDialog(Shell parent, int row, int col, TableItem item, Table table, String varName) {
		super(parent, SWT.MIN|SWT.RESIZE);
		this.row=row;
		this.col=col;
		this.item=item;
		this.table=table;
		this.varName=varName;
		setText("Modify Value");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(300, 200);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}
	
	public String getInput(){
		return input;
	}
	
	public void setInput(String input){
		this.input=input;
	}

	 protected void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));
		
		GridData data=new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan=1;
		
		Label label0_0 = new Label(shell, SWT.NONE);
		label0_0.setText("Variable:");
		label0_0.setLayoutData(data);
		
		Label label0_1 = new Label(shell, SWT.NONE);
		label0_1.setText(varName);
		label0_1.setLayoutData(data);
		
		Label label1_0 = new Label(shell, SWT.NONE);
		label1_0.setText("Cycle Index:");
		label1_0.setLayoutData(data);
		
		Label label1_1 = new Label(shell, SWT.NONE);
		final String itemText0=item.getText(0);
		label1_1.setText(itemText0);
		label1_1.setLayoutData(data);
		
		Label label2_0 = new Label(shell, SWT.NONE);
		label2_0.setText("Cycle Name:");
		label2_0.setLayoutData(data);
		
		Label label2_1 = new Label(shell, SWT.NONE);
		final String itemText1=item.getText(1);
		label2_1.setText(itemText1);
		label2_1.setLayoutData(data);
		
		Label label3 = new Label(shell, SWT.NONE);
		label3.setText("Value:");
		label3.setLayoutData(data);
		
		final Text text=new Text(shell, SWT.BORDER);
		text.setText(item.getText(2));
		text.setLayoutData(data);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				input=text.getText();
				ArrayList<String[]> cycle=DebugCorePlugin.varDetailCycle;
				String[] rowItem=cycle.get(row);
				try {
					String data=DebugCorePlugin.target.sendRequest("modify_cycle:"+varName+"#"+rowItem[1]+"#"+input);
					if (data.equals("modified")){
						rowItem[col]=input;
						final IWorkbench workbench=PlatformUI.getWorkbench();
						workbench.getDisplay().asyncExec(new Runnable(){
							public void run(){
								WPPVarDetailView varDetailView = (WPPVarDetailView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_VARIABLEDETAIL_VIEW);
								Table table=varDetailView.getTable();
								int topIndex=table.getTopIndex();
								varDetailView.displayCycleValues();
								table.setSelection(row);
								table.setTopIndex(topIndex);
							}
						});
					}
				} catch (DebugException e) {
					WPPException.handleException(e);
				}
				shell.close();
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	 }
}
