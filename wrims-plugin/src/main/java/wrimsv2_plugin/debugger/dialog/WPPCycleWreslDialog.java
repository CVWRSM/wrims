package wrimsv2_plugin.debugger.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.debug.Compile;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.view.WPPFileIncExploreView;
import wrimsv2_plugin.debugger.view.WPPVarDetailView;
import wrimsv2_plugin.tools.FileProcess;

public class WPPCycleWreslDialog extends Dialog {
	
	public WPPCycleWreslDialog(Shell parent) {
		super(parent, SWT.MIN|SWT.RESIZE);
		setText("WRESL Files in A Cycle");
	}
	
	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(400, 150);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	 protected void createContents(final Shell shell) {
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		shell.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan=1;
		
		Label label1 = new Label(shell, SWT.NONE);
		label1.setText("Cycle:");
		
		final Combo cycleCombo = new Combo(shell, SWT.BORDER);
		for (int i=1; i<=99; i++){
			cycleCombo.add(String.valueOf(i));
		}
		cycleCombo.select(0);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int cycleIndex = Integer.parseInt(cycleCombo.getText())-1;
				procCycleWresl(shell, cycleIndex);
				shell.close();
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	 }
	
	public void procCycleWresl(Shell shell, final int index){
		WPPDebugTarget target = DebugCorePlugin.target;
		final String path = DebugCorePlugin.cycleWreslMainFilePath;
		
		if (path.equals("")){
			shell.close();
		}else{
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					final Shell shell=workbench.getActiveWorkbenchWindow().getShell();
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
					try {
						dialog.run(true,false, new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) {
								monitor.beginTask("Update explorer for included WRESL files", 100);
								StudyDataSet sds=new StudyDataSet();
								try {
									sds = Compile.checkStudy(path, true);
								} catch (IOException e) {
									WPPException.handleException(e);
								}
								monitor.worked(50);
								if (index>sds.getModelList().size()-1){
									monitor.done();
									displayOutOfRangeMessage();
									return;
								}
								ArrayList<String> fns=FileProcess.getCycleWreslFiles(path, sds, index);
								ArrayList<String> tfns=FileProcess.getTableFiles(path);
								fns.addAll(tfns);
								monitor.worked(20);
								DebugCorePlugin.fileFolderWreslInc=FileProcess.retrieveFileNames(fns);
								monitor.worked(20);
								UpdateWreslIncExplore();
								monitor.done();
							}
						});
					} catch (InvocationTargetException e) {
						WPPException.handleException(e);
					} catch (InterruptedException e) {
						WPPException.handleException(e);
					}
				}
			});
		};
	}
	
	public void UpdateWreslIncExplore(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				WPPFileIncExploreView wreslIncExplore;
				try {
					wreslIncExplore = (WPPFileIncExploreView) workBenchPage.showView(DebugCorePlugin.ID_WPP_FILEINCEXPLORE_VIEW);
					wreslIncExplore.update();
				} catch (PartInitException e) {
					WPPException.handleException(e);
				}
			}
		});
	}
	
	public void displayOutOfRangeMessage(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				final Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK );  
				messageBox.setText("Warning");
				messageBox.setMessage("Cycle index is out of the range of the model study");
				messageBox.open();
			}
		});
	}
}
