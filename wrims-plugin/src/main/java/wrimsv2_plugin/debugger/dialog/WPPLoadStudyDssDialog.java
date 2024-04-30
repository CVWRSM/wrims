package wrimsv2_plugin.debugger.dialog;

import hec.heclib.dss.HecDss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.view.WPPVariableView;
import wrimsv2_plugin.debugger.view.WPPWatchView;
import wrimsv2_plugin.tools.DataProcess;

public class WPPLoadStudyDssDialog extends Dialog {
	private Button[] checkBox=new Button[8];
	private Text[] dvFileText=new Text[4];
	private Button[] dvBrowserButton=new Button[4];
	private Text[] svFileText=new Text[4];
	private Button[] svBrowserButton=new Button[4];
	private Text[] studyFolderText=new Text[4];
	private Button[] studyBrowserButton=new Button[4];
	private String unavailableFiles="";
	private String unavailableFolders="";
	private String errorFiles="";
	private DropTarget[] dvFileDt = new DropTarget[4];
	private DropTarget[] svFileDt = new DropTarget[4];
	private DropTarget[] studyFolderDt = new DropTarget[4];
	
	public WPPLoadStudyDssDialog(Shell parentShell) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		setText("Load Alt DV/SV or Study");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(900, 550);
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
		label1.setText("Please select DV and SV files or study folders for alternatives");
		
		GridLayout layout = new GridLayout(37, true);
		
		Composite[] fileSelection = new Composite[8];
		
		for (int i=0; i<4; i++){
			final int j=i;
			fileSelection[i] = new Composite(shell, SWT.NONE);
			fileSelection[i].setLayout(layout);
			
			checkBox[i]=new Button(fileSelection[i], SWT.CHECK);
			checkBox[i].setText("Alt "+(i+1)+":");
			checkBox[i].setSelection(DebugCorePlugin.selectedStudies[i]);
			GridData gd0 = new GridData(GridData.FILL_HORIZONTAL);
			gd0.horizontalSpan = 3;
			checkBox[i].setLayoutData(gd0);
			/*
			checkBox[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (int j=4; j<8; j++){
						checkBox[j].setSelection(false);
						DebugCorePlugin.selectedStudies[j]=false;
					}
				}
			});
			*/
			
			dvFileText[i] = new Text(fileSelection[i], SWT.SINGLE | SWT.BORDER);
			GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			gd1.horizontalSpan = 13;
			dvFileText[i].setLayoutData(gd1);
			dvFileText[i].setText(DebugCorePlugin.studyDvFileNames[i]);
			
			dvFileDt[i] = new DropTarget(dvFileText[i], DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
			dvFileDt[i].setTransfer(new Transfer[] { FileTransfer.getInstance()});
			dvFileDt[i].addDropListener(new DropTargetAdapter() {
	            public void drop(DropTargetEvent event) {
	                String fileList[] = null;
	                FileTransfer ft = FileTransfer.getInstance();
	                if (ft.isSupportedType(event.currentDataType)) {
	                    fileList = (String[]) event.data;
	                    dvFileText[j].setText(fileList[0]);
	                }
	            }
	        });
		
			dvBrowserButton[i] = new Button(fileSelection[i], SWT.PUSH);
			dvBrowserButton[i].setText("Browser DV");
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 4;
			dvBrowserButton[i].setLayoutData(gd2);
			dvBrowserButton[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							Shell shell=workbench.getActiveWorkbenchWindow().getShell();
							FileDialog dlg=new FileDialog(shell, SWT.OPEN);
							dlg.setFilterNames(new String[]{"DSS Files (*.dss)", "All Files (*.*)"});
							dlg.setFilterExtensions(new String[]{"*.dss", "*.*"});
							dlg.setFileName(dvFileText[j].getText());
							String file=dlg.open();
							if (file !=null){
								dvFileText[j].setText(file);
							}
						}
					});
				}
			});
			
			svFileText[i] = new Text(fileSelection[i], SWT.SINGLE | SWT.BORDER);
			gd1 = new GridData(GridData.FILL_HORIZONTAL);
			gd1.horizontalSpan = 13;
			svFileText[i].setLayoutData(gd1);
			svFileText[i].setText(DebugCorePlugin.studySvFileNames[i]);
			
			svFileDt[i] = new DropTarget(svFileText[i], DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
			svFileDt[i].setTransfer(new Transfer[] { FileTransfer.getInstance()});
			svFileDt[i].addDropListener(new DropTargetAdapter() {
	            public void drop(DropTargetEvent event) {
	                String fileList[] = null;
	                FileTransfer ft = FileTransfer.getInstance();
	                if (ft.isSupportedType(event.currentDataType)) {
	                    fileList = (String[]) event.data;
	                    svFileText[j].setText(fileList[0]);
	                }
	            }
	        });
		
			svBrowserButton[i] = new Button(fileSelection[i], SWT.PUSH);
			svBrowserButton[i].setText("Browser SV");
			gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 4;
			svBrowserButton[i].setLayoutData(gd2);
			svBrowserButton[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							Shell shell=workbench.getActiveWorkbenchWindow().getShell();
							FileDialog dlg=new FileDialog(shell, SWT.OPEN);
							dlg.setFilterNames(new String[]{"DSS Files (*.dss)", "All Files (*.*)"});
							dlg.setFilterExtensions(new String[]{"*.dss", "*.*"});
							dlg.setFileName(svFileText[j].getText());
							String file=dlg.open();
							if (file !=null){
								svFileText[j].setText(file);
							}
						}
					});
				}
			});
		}
		
		for (int i=4; i<8; i++){
			final int j=i-4;
			fileSelection[i] = new Composite(shell, SWT.NONE);
			fileSelection[i].setLayout(layout);
			
			checkBox[i]=new Button(fileSelection[i], SWT.CHECK);
			checkBox[i].setText("Alt "+(i+1)+":");
			checkBox[i].setSelection(DebugCorePlugin.selectedStudies[i]);
			GridData gd0 = new GridData(GridData.FILL_HORIZONTAL);
			gd0.horizontalSpan = 3;
			checkBox[i].setLayoutData(gd0);
			/*
			checkBox[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (int j=0; j<4; j++){
						checkBox[j].setSelection(false);
						DebugCorePlugin.selectedStudies[j]=false;
					}
				}
			});
			*/
			
			studyFolderText[j] = new Text(fileSelection[i], SWT.SINGLE | SWT.BORDER);
			GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			gd1.horizontalSpan = 27;
			studyFolderText[j].setLayoutData(gd1);
			studyFolderText[j].setText(DebugCorePlugin.studyFolderNames[j]);
			
			studyFolderDt[j] = new DropTarget(studyFolderText[j], DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
			studyFolderDt[j].setTransfer(new Transfer[] { FileTransfer.getInstance()});
			studyFolderDt[j].addDropListener(new DropTargetAdapter() {
	            public void drop(DropTargetEvent event) {
	                String fileList[] = null;
	                FileTransfer ft = FileTransfer.getInstance();
	                if (ft.isSupportedType(event.currentDataType)) {
	                    fileList = (String[]) event.data;
	                    studyFolderText[j].setText(fileList[0]);
	                }
	            }
	        });
		
			studyBrowserButton[j] = new Button(fileSelection[i], SWT.PUSH);
			studyBrowserButton[j].setText("Browser Study");
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 7;
			studyBrowserButton[j].setLayoutData(gd2);
			studyBrowserButton[j].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							Shell shell=workbench.getActiveWorkbenchWindow().getShell();
							DirectoryDialog dlg=new DirectoryDialog(shell, SWT.OPEN);
							String file=dlg.open();
							if (file !=null){
								studyFolderText[j].setText(file);
							}
						}
					});
				}
			});
		}
		
		Composite okCancel=new Composite(shell, SWT.NONE);
		okCancel.setLayout(layout);
		Button ok = new Button(okCancel, SWT.PUSH);
		ok.setText("OK");
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 5;
		ok.setLayoutData(gd3);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				okPressed(shell);
			}
		});
		
		Button cancel = new Button(okCancel, SWT.PUSH);
		cancel.setText("Cancel");
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		gd4.horizontalSpan = 5;
		cancel.setLayoutData(gd4);
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
	}
	
	public void okPressed(Shell shell){
		selectFiles();
		if (DebugCorePlugin.selectedStudies[0] || DebugCorePlugin.selectedStudies[1] || DebugCorePlugin.selectedStudies[2] || DebugCorePlugin.selectedStudies[3]){
			if (checkFilesExist()){
				if (openDssFiles()){
					shell.close();
					processViews();
				}else{
					showDssFileErrorDialog(1);
				}
			}else{
				showDssFileErrorDialog(0);
			}
		}else{
			if (checkFoldersExist()){
				shell.close();
				processViews();
			}else{
				showDssFileErrorDialog(2);
			}
		}
	}
	
	public void selectFiles(){
		for (int i=0; i<4; i++){
			if (checkBox[i].getSelection()){
				DebugCorePlugin.selectedStudies[i]=true;
			}else{
				DebugCorePlugin.selectedStudies[i]=false;
			}
			DebugCorePlugin.studyDvFileNames[i]=dvFileText[i].getText();
			DebugCorePlugin.studySvFileNames[i]=svFileText[i].getText();
		}
		for (int i=4; i<8; i++){
			int j=i-4;
			if (checkBox[i].getSelection()){
				DebugCorePlugin.selectedStudies[i]=true;
			}else{
				DebugCorePlugin.selectedStudies[i]=false;
			}
			DebugCorePlugin.studyFolderNames[j]=studyFolderText[j].getText();
		}
	}
	
	public void showDssFileErrorDialog(final int flag){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
				messageBox.setText("Error:");
				switch (flag){
				case 0:
					messageBox.setMessage("Files "+unavailableFiles+" do not exist.");
					break;
				case 1:
					messageBox.setMessage("Dss files "+errorFiles+" could not be opened.");
					break;
				case 2:
					messageBox.setMessage("Study folders "+unavailableFolders+" do not exist.");
					break;
				}
				messageBox.open();
			}
		});
	}
	
	public boolean checkFilesExist(){
		unavailableFiles="";
		boolean allFileFound=true;
		for (int i=0; i<4; i++){
			if (DebugCorePlugin.selectedStudies[i]){
				File dvFile = new File(DebugCorePlugin.studyDvFileNames[i]);
				if (!dvFile.exists()){
					unavailableFiles=unavailableFiles+DebugCorePlugin.studyDvFileNames[i]+",";
					allFileFound=false;
				}
				File svFile = new File(DebugCorePlugin.studySvFileNames[i]);
				if (!svFile.exists()){
					unavailableFiles=unavailableFiles+DebugCorePlugin.studySvFileNames[i]+",";
					allFileFound=false;
				}
			}		
		}
		if (unavailableFiles.endsWith(",")){
			unavailableFiles=unavailableFiles.substring(0, unavailableFiles.length()-1);
		}
		return allFileFound;
	}
	
	public boolean checkFoldersExist(){
		unavailableFolders="";
		boolean allFolderFound=true;
		for (int i=4; i<8; i++){
			int j=i-4;
			if (DebugCorePlugin.selectedStudies[i]){
				File folder = new File(DebugCorePlugin.studyFolderNames[j]);
				if (!folder.exists()){
					unavailableFolders=unavailableFolders+DebugCorePlugin.studyFolderNames[j]+",";
					allFolderFound=false;
				}
			}		
		}
		if (unavailableFolders.endsWith(",")){
			unavailableFolders=unavailableFolders.substring(0, unavailableFolders.length()-1);
		}
		return allFolderFound;
	}
	
	public boolean openDssFiles(){
		errorFiles="";
		boolean success=true;
		for (int i=0; i<4; i++){
			if (DebugCorePlugin.selectedStudies[i]){
				try {
					DebugCorePlugin.dvDss[i]=HecDss.open(DebugCorePlugin.studyDvFileNames[i]);
					DebugCorePlugin.dvVector[i]=DebugCorePlugin.dvDss[i].getCondensedCatalog();
					DebugCorePlugin.dvDss[i].close();
				} catch (Exception e) {
					WPPException.handleException(e);
					errorFiles=errorFiles+DebugCorePlugin.studyDvFileNames[i]+",";
					success=false;
				}
				try {
					DebugCorePlugin.svDss[i]=HecDss.open(DebugCorePlugin.studySvFileNames[i]);
					DebugCorePlugin.svVector[i]=DebugCorePlugin.svDss[i].getCondensedCatalog();
					DebugCorePlugin.svDss[i].close();
				} catch (Exception e) {
					WPPException.handleException(e);
					errorFiles=errorFiles+DebugCorePlugin.studySvFileNames[i]+",";
					success=false;
				}
			}
		}
		if (errorFiles.endsWith(",")){
			errorFiles=errorFiles.substring(0, errorFiles.length()-1);
		}
		return success;
	}
	
	public void processViews(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){

			@Override
			public void run() {
				IWorkbenchPage workBenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
				
				WPPVariableView variableView = (WPPVariableView) workBenchPage.findView(DebugCorePlugin.ID_WPP_VARIABLE_VIEW);
				WPPWatchView watchView = (WPPWatchView) workBenchPage.findView(DebugCorePlugin.ID_WPP_WATCH_VIEW);
				
				if (DebugCorePlugin.target!=null && DebugCorePlugin.target.isSuspended()){
					DataProcess.generateAltStudyData();
					variableView.updateView();
					if (DebugCorePlugin.target.allVGLoadedViewNames.contains(DebugCorePlugin.TITLE_WATCH_VIEW)) watchView.updateView();
				}else{
					variableView.adjustAltColumnNames();
					watchView.adjustAltColumnNames();
				}
			}
		});
	}
}
