package wrimsv2_plugin.debugger.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
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
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.view.UpdateView;
import wrimsv2_plugin.debugger.view.WPPAllGoalView;
import wrimsv2_plugin.debugger.view.WPPAllVariableView;
import wrimsv2_plugin.debugger.view.WPPGoalView;
import wrimsv2_plugin.debugger.view.WPPVarDetailView;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.debugger.view.WPPWatchView;
import wrimsv2_plugin.tools.ProcWatchItem;
import wrimsv2_plugin.tools.SearchTable;
import wrimsv2_plugin.tools.ShowDuplicatedWatch;

public class WPPAddWatchDialog extends Dialog {
	
	private IViewPart view;
	
	public WPPAddWatchDialog(Shell parentShell, IViewPart view) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		this.view=view;
		setText("Add Watched Variables");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(400, 170);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	 protected void createContents(final Shell shell) {
		FillLayout layout=new FillLayout(SWT.VERTICAL);
		layout.marginHeight=10;
		layout.marginWidth=20;
		shell.setLayout(layout);
		
		Label label1=new Label(shell, SWT.NONE);
		label1.setText("Watch:");
		
		final Text text1=new Text(shell, SWT.BORDER);
						
		RowLayout layout1=new RowLayout(SWT.HORIZONTAL);
		layout1.fill=true;
		//layout.pack=true;
		Composite line3=new Composite(shell, SWT.NONE);
		line3.setLayout(layout1);
		Button ok = new Button(line3, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				ArrayList<String> watchItems=DebugCorePlugin.watchItems;
				final String varGoalName=text1.getText();
				String varGoalNameLowerCase=varGoalName.toLowerCase();
				if (watchItems.contains(varGoalNameLowerCase)) {
					new ShowDuplicatedWatch(varGoalName);
					text1.setText("");
				}else{
					watchItems.add(varGoalNameLowerCase);
					ProcWatchItem.saveWatchItems(watchItems);
					if (DebugCorePlugin.target!=null && DebugCorePlugin.target.isSuspended()){
						UpdateView.updateWatchView(DebugCorePlugin.target);
					}else{
						final IWorkbench workbench=PlatformUI.getWorkbench();
						workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							WPPWatchView watchView = (WPPWatchView) workbench.getActiveWorkbenchWindow().getActivePage().findView(DebugCorePlugin.ID_WPP_WATCH_VIEW);
							watchView.addWatched(varGoalName);
						}
					});
					}
					shell.close();
				}
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
}
