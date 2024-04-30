package wrimsv2_plugin.debugger.dialog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2.components.IncFileCollector;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.UpdateView;
import wrimsv2_plugin.debugger.view.WPPWatchView;
import wrimsv2_plugin.tools.ProcWatchItem;
import wrimsv2_plugin.tools.ShowDuplicatedWatch;

public class WPPExportStudyDialog extends Dialog {
	private String selFilePath;
	private Text targetText;
	private String targetFolder;
	private IFile selFile;
	
	public WPPExportStudyDialog(Shell parentShell, String selFilePath, IFile ifile) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		this.selFilePath=selFilePath;
		this.selFile=ifile;
		setText("Export Study");
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
		label1.setText("Please select a folder to export the study to:");
				
		Composite fileSelection = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(15, true);
		fileSelection.setLayout(layout);
		targetText = new Text(fileSelection, SWT.SINGLE | SWT.BORDER);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 12;
		targetText.setLayoutData(gd1);
		targetText.setText("");
		
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
						DirectoryDialog dlg =new DirectoryDialog(shell,SWT.OPEN);
						targetText.setText(dlg.open());
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
		targetFolder=targetText.getText();
		if (selFilePath.toLowerCase().endsWith(".wresl")){
			procMainFile();
		}else if (selFilePath.toLowerCase().endsWith(".launch")){
			procLaunchFile();
		}
		shell.close();
	}
	
	protected void procMainFile(){
		new IncFileCollector(selFilePath, targetFolder);
		collectExternalFiles(selFilePath, targetFolder);
	}
	
	protected void procLaunchFile(){

        // check for an existing launch config for the WPP file
        String path = selFile.getFullPath().toString(); 
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        final ILaunchConfiguration configuration;
		try {
			configuration = launchManager.getLaunchConfiguration(selFile);
			
			String msr = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_MULTISTUDY, "1");
			final int msv = Integer.parseInt(msr);
			
			collectLaunchFile(selFilePath, targetFolder);
			
			final IWorkbench workbench=PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable(){
				public void run(){
					Shell shell=workbench.getActiveWorkbenchWindow().getShell();
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
					try {
						dialog.run(true,false, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								try{
								
									monitor.beginTask("Export Studies", 100);
								
									String sid="";
									for (int i=1; i<=msv; i++){
										if (i>1){
											sid="_MS"+String.valueOf(i);
										}
											
										collectModelFiles(configuration, sid);
										collectDSSFiles(configuration, sid, 0);
										collectDSSFiles(configuration, sid, 1);
										collectDSSFiles(configuration, sid, 2);	
									
										int progress=Math.round(100.0f/msv);
										monitor.worked(progress);
									}
								}
								finally{
									monitor.done();
								}
							}
						});
					} catch (InvocationTargetException e) {
						WPPException.handleException(e);
					} catch (InterruptedException e) {
						WPPException.handleException(e);
					}
				}
			});
			
		} catch (Exception e) {
			WPPException.handleException(e);
		}
		
	}
	
	protected void collectDSSFiles(ILaunchConfiguration configuration, String sid, int flag) {
		String dssFile=null;
		try{
			if (flag==0){
				dssFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVARFILE+sid, (String)null);
			}else if (flag==1){
				dssFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_INITFILE+sid, (String)null);
			}else{
				dssFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE+sid, (String)null);
			}
		}catch (CoreException e){
		}
		
		if (dssFile !=null){
			if (!new File(dssFile).isAbsolute()){
				dssFile = procRelativePath(dssFile, selFile);
			}
			int index = dssFile.indexOf(":");
			String dssFilePathWithoutDrive = dssFile.substring(index+1);
			
			File targetPath = new File(targetFolder,dssFilePathWithoutDrive);
			try {
				FileUtils.copyFile(new File(dssFile), targetPath);
			} catch (IOException e) {
			}
		}
	}	
	
	protected void collectModelFiles(ILaunchConfiguration configuration, String sid){
		String mainFile=null;
		try {
			mainFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_PROGRAM+sid, (String)null);
		} catch (CoreException e) {
		}
		if (mainFile !=null){
			if (!new File(mainFile).isAbsolute()){
				mainFile = procRelativePath(mainFile, selFile);
			}
			new IncFileCollector(mainFile, targetFolder);
			collectExternalFiles(mainFile, targetFolder);
		}
	}
	
	public void collectExternalFiles(String mainFile, String targetFolder) {
		int index1 = mainFile.lastIndexOf("\\");
		String mainDirectory = mainFile.substring(0, index1 + 1);
		String externalDirectory=mainDirectory+"external";
		int index2 = externalDirectory.indexOf(":");
		String externalDirectoryWithoutDrive = externalDirectory.substring(index2+1);
		try {
			FileUtils.copyDirectory(new File(externalDirectory), new File(targetFolder, externalDirectoryWithoutDrive));
		} catch (IOException e) {
		}
	}
	
	public void collectLaunchFile(String launchFile, String targetFolder){
		int index = launchFile.indexOf(":");
		String launchFilePathWithoutDrive = launchFile.substring(index+1);
		File targetPath = new File(targetFolder,launchFilePathWithoutDrive);
		try {
			FileUtils.copyFile(new File(launchFile), targetPath);
		} catch (IOException e) {
		}
	}
	
	public String procRelativePath(String path, IFile file){
		String absPath=file.getLocation().toFile().getParentFile().getAbsolutePath();
		absPath=absPath+"\\"+path;
		return absPath;
	}
}
