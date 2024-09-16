package wrimsv2_plugin.debugger.dialog;

import java.io.File;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WPPSaveFileDialog extends Dialog {
	private int flag=1;
	private Text fileText;
	private	String fileName="";
	
	public WPPSaveFileDialog(Shell parentShell, int flag) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		this.flag=flag;
		setText("Save To DSS");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(600, 200);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	protected void createContents(final Shell shell) {
		FillLayout fl = new FillLayout(SWT.VERTICAL);
		shell.setLayout(fl);
		fl.marginWidth=10;
		fl.marginHeight=15;
		
		Label label1=new Label(shell, SWT.NONE);
		if (flag==0){
			label1.setText("Please select an initial file to save the data in memory:");
			fileName=DebugCorePlugin.savedInitFileName;
		}
		else if (flag==1){
			label1.setText("Please select a SV file to save the data in memory:");
			fileName=DebugCorePlugin.savedSvFileName;
		}else{
			label1.setText("Please select a DV file to save the data in memory:");
			fileName=DebugCorePlugin.savedDvFileName;
		}
		
		Composite fileSelection = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(15, true);
		fileSelection.setLayout(layout);
		fileText = new Text(fileSelection, SWT.SINGLE | SWT.BORDER);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 12;
		fileText.setLayoutData(gd1);
		fileText.setText(fileName);
		
		Button browserButton = new Button(fileSelection, SWT.PUSH);
		browserButton.setText("Browser");
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 3;
		browserButton.setLayoutData(gd2);
		browserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.SAVE);
						dlg.setFilterNames(new String[]{"DSS Files (*.dss)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.dss", "*.*"});
						dlg.setFileName(fileText.getText());
						String file=dlg.open();
						if (file !=null){
							fileText.setText(file);
						}
					}
				});
			}
		});
		
		Composite okCancel=new Composite(shell, SWT.NONE);
		okCancel.setLayout(layout);
		Button ok = new Button(okCancel, SWT.PUSH);
		ok.setText("OK");
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 2;
		ok.setLayoutData(gd3);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				okPressed(shell);
			}
		});
		
		Button cancel = new Button(okCancel, SWT.PUSH);
		cancel.setText("Cancel");
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		gd4.horizontalSpan = 2;
		cancel.setLayoutData(gd4);
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	}
	
	public void okPressed(Shell shell){
		fileName=fileText.getText();
		boolean exists = (new File(fileName)).exists();
		if (exists) {
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					Shell shell=workbench.getActiveWorkbenchWindow().getShell();
					boolean overwrite = MessageDialog.openConfirm(shell, "Overwrite Confirm", "Do you want to overwrite the file of "+fileName);
					if (overwrite){
						saveDssFile();
					}
				}
			}); 
		} else {
			saveDssFile();
		}
		shell.close();
	}
	
	public void saveDssFile(){
		String request;
		if (flag==0){
			request="saveinitdss:"+fileName;
		}else if (flag==1){
			request="savesvdss:"+fileName;
		}else{
			request="savedvdss:"+fileName;
		}
		try {
			if (!fileName.equals("")){
				String status=DebugCorePlugin.target.sendRequest(request);
				if (status.equals("dsssavefailed")){
					System.out.println("Failed in saving dss data to "+fileName);
				}
			}
		} catch (DebugException e) {
			WPPException.handleException(e);
		}
	}
}
