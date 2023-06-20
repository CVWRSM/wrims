package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.Activator;
import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.DataOps;
import hec.heclib.dss.HecDss;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class DSSFileView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.hecdssvue.views.DSSFileView";
	
	private Button[] checkBox=new Button[4];
	private Text[] dvFileText=new Text[4];
	private Button[] dvBrowserButton=new Button[4];
	private Text[] svFileText=new Text[4];
	private Button[] svBrowserButton=new Button[4];
	private DropTarget[] dvDt=new DropTarget[4];
	private DropTarget[] svDt=new DropTarget[4];
	private Button okButton;
	private Button openButton;
	private Button saveButton;
	private String unavailableFiles="";
	private String unavailableFolders="";
	private String errorFiles="";
	private Combo[] studyType= new Combo[4];
	
	String lines[] = { "# comment",
			"# variable and value are separated by whitespace",
			"# value obviously cannot contain whitespace", 
			"",
			"# Specify dss files to operate on",
			"# DV Variables: AltDV1, AltDV2, AltDV3, AltDV4",
			"# SV Variables: AltSV1, AltSV2, AltSV3, AltSV4",
			"" };

	private Label fileLabel;

	private Composite dialogArea;
	
	/**
	 * The constructor.
	 */
	public DSSFileView() {
	}


	public void createPartControl(Composite parent) {

		dialogArea = new Composite(parent, SWT.NONE);
		FillLayout fl = new FillLayout(SWT.VERTICAL);
		dialogArea.setLayout(fl);
		fl.marginWidth=10;
		fl.marginHeight=15;
		
		Composite title = new Composite(dialogArea, SWT.NONE);
		title.setLayout(new GridLayout(39, true));
		Label labelTitle = new Label(title, SWT.NONE);
		labelTitle.setText("Please select DV and SV files for alternatives");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 9;
		labelTitle.setLayoutData(gd);
		
		okButton = new Button(title, SWT.PUSH);
		okButton.setText("OK");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		okButton.setLayoutData(gd);
		okButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	selectFiles();
		    }
		});
		
		openButton = new Button(title, SWT.PUSH);
		openButton.setText("Open Project");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		openButton.setLayoutData(gd);
		openButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.OPEN);
						dlg.setFilterNames(new String[]{"DSS Project (*.dsv)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.dsv", "*.*"});
						String file=dlg.open();
						fileLabel.setText(file);
						openProject(file);
					}
				});
		    }
		});
		
		saveButton = new Button(title, SWT.PUSH);
		saveButton.setText("Save Project");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		saveButton.setLayoutData(gd);
		saveButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.SAVE);
						dlg.setFilterNames(new String[]{"DSS Project (*.dsv)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.dsv", "*.*"});
						String file=dlg.open();
						saveProject(file);
					}
				});
		    }
		});
		
		fileLabel = new Label(title, SWT.NONE);
		fileLabel.setText("");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 12;
		fileLabel.setLayoutData(gd);
		
		Composite[] fileSelection = new Composite[4];
		for (int i=0; i<4; i++){
			final int j=i;
			fileSelection[i] = new Composite(dialogArea, SWT.NONE);
			GridLayout layout = new GridLayout(39, true);
			fileSelection[i].setLayout(layout);
			
			checkBox[i]=new Button(fileSelection[i], SWT.CHECK);
			checkBox[i].setText("Alt "+(i+1));
			checkBox[i].setSelection(DebugCorePlugin.selectedStudies[i]);
			GridData gd0 = new GridData(GridData.FILL_HORIZONTAL);
			gd0.horizontalSpan = 3;//TODO
			checkBox[i].setLayoutData(gd0);
//			/*
//			checkBox[i].addSelectionListener(new SelectionAdapter() {
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					for (int j=4; j<8; j++){//TODO
////					for (int j=0; j<4; j++){
//						checkBox[j].setSelection(false);
//						DebugCorePlugin.selectedStudies[j]=false;
//					}
//				}
//			});
//			*/
			
			dvFileText[i] = new Text(fileSelection[i], SWT.SINGLE | SWT.BORDER);
			GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			gd1.horizontalSpan = 14;
			dvFileText[i].setLayoutData(gd1);
			dvFileText[i].setText(DebugCorePlugin.studyDvFileNames[i]);
			
			dvDt[i] = new DropTarget(dvFileText[i], DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
	        dvDt[i].setTransfer(new Transfer[] { FileTransfer.getInstance()});
	        dvDt[i].addDropListener(new DropTargetAdapter() {
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
			dvBrowserButton[i].setText("DV");
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 2;
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
			gd1.horizontalSpan = 14;
			svFileText[i].setLayoutData(gd1);
			svFileText[i].setText(DebugCorePlugin.studySvFileNames[i]);

			svDt[i] = new DropTarget(svFileText[i], DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
	        svDt[i].setTransfer(new Transfer[] { FileTransfer.getInstance()});
	        svDt[i].addDropListener(new DropTargetAdapter() {
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
			svBrowserButton[i].setText("SV");
			gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 2;
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
			
			studyType[i] = new Combo(fileSelection[i], SWT.SINGLE | SWT.BORDER);
			GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
			gd3.horizontalSpan = 2;
			studyType[i].setLayoutData(gd3);
			studyType[i].add("CL");
			studyType[i].add("CS3");
			studyType[i].add("CS2");
			studyType[i].select(0);
			DebugCorePlugin.studyTypes[i]=0;
			studyType[i].addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					DebugCorePlugin.studyTypes[j]=((Combo)e.getSource()).getSelectionIndex();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
				
			});
		}
		


		
	}
	
	
	public void selectFileNames(){
		for (int i=0; i<4; i++){
			if (checkBox[i].getSelection()){
				DebugCorePlugin.selectedStudies[i]=true;
			}else{
				DebugCorePlugin.selectedStudies[i]=false;
			}
			DebugCorePlugin.studyDvFileNames[i]=dvFileText[i].getText();
			DebugCorePlugin.studySvFileNames[i]=svFileText[i].getText();
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
//				case 2:
//					messageBox.setMessage("Study folders "+unavailableFolders+" do not exist.");
//					break;
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
//					HecDss dss =HecDss.open(DebugCorePlugin.studyDvFileNames[i]);
//					DebugCorePlugin.dvDss[i] = dss;
					DebugCorePlugin.dvDss[i]=HecDss.open(DebugCorePlugin.studyDvFileNames[i]);
					DebugCorePlugin.dvVector[i]=DebugCorePlugin.dvDss[i].getCondensedCatalog();
				} catch (Exception e) {
					WPPException.handleException(e);
					errorFiles=errorFiles+DebugCorePlugin.studyDvFileNames[i]+",";
					success=false;
				}
				try {
					DebugCorePlugin.svDss[i]=HecDss.open(DebugCorePlugin.studySvFileNames[i]);
					DebugCorePlugin.svVector[i]=DebugCorePlugin.svDss[i].getCondensedCatalog();
				} catch (Exception e) {
					WPPException.handleException(e);
					errorFiles=errorFiles+DebugCorePlugin.studySvFileNames[i]+",";
					success=false;
				}
			}
			DssPluginCore.initWYTDss=true;
		}
		if (errorFiles.endsWith(",")){
			errorFiles=errorFiles.substring(0, errorFiles.length()-1);
		}
		return success;
	}
	
	public void openProject(String fn){
		
		for (int i=0; i<checkBox.length; i++){
			checkBox[i].setSelection(false);
			dvFileText[i].setText("");
			svFileText[i].setText("");
			studyType[i].select(0);
		}
		
		try {
			File file = new File(fn);
			if (!file.exists()){
				file.createNewFile();
			}
			FileInputStream fs = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		    LineNumberReader reader = new LineNumberReader(br);
		    String line;
		    while((line = br.readLine())!=null){
		    	if (line.startsWith("#"))
					continue;

				String[] temp = line.toLowerCase().split("\t+");

				if (temp.length != 2)
					continue;
				
				int index;
				if (temp[0].length()==6){
					if (temp[0].startsWith("altdv")){
						index=Integer.parseInt(temp[0].substring(5))-1;
						dvFileText[index].setText(temp[1]);
						checkBox[index].setSelection(true);
					}else if (temp[0].startsWith("altsv")){
						index=Integer.parseInt(temp[0].substring(5))-1;
						svFileText[index].setText(temp[1]);
						checkBox[index].setSelection(true);
					}else if (temp[0].startsWith("stdtp")){
						index=Integer.parseInt(temp[0].substring(5))-1;
						int type=Integer.parseInt(temp[1]);
						studyType[index].select(type);
					}else{
						continue;
					}
				}else{
					continue;
				}
		    }
		    
		    selectFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveProject(String fn){
		try {
			File file = new File(fn);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			for (int i = 0; i < lines.length; i++)
				out.write(lines[i] + "\n");
			for (int i=0; i<dvFileText.length; i++){
				int j=i+1;
				out.println("AltDv" + j + "\t" + dvFileText[i].getText());
			}
			for (int i=0; i<svFileText.length; i++){
				int j=i+1;
				out.println("AltSv" + j + "\t" + svFileText[i].getText());
			}
			for (int i=0; i<studyType.length; i++){
				int j=i+1;
				out.println("StdTp" + j +"\t"+studyType[i].getSelectionIndex());
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void selectFiles(){
		selectFileNames();
		//DataOps.setProject();
		if (DebugCorePlugin.selectedStudies[0] || DebugCorePlugin.selectedStudies[1] || DebugCorePlugin.selectedStudies[2] || DebugCorePlugin.selectedStudies[3]){
			if (checkFilesExist()){
				boolean success=openDssFiles();
				if (success){
////					close();
////					processViews();
				}else{
					showDssFileErrorDialog(1);
				}
				DssPluginCore.dssArray = new ArrayList<HecDss> ();
//				dssArray.add(DebugCorePlugin.dvDss[0]);
//				dssArray.add(DebugCorePlugin.svDss[0]);
//				for (int i = 0; i <DebugCorePlugin.dvDss.length; i++){
//					dssArray.add(DebugCorePlugin.dvDss[i]);
//					dssArray.add(DebugCorePlugin.svDss[i]);
//				}
				for (int i = 0; i <DebugCorePlugin.selectedStudies.length/2;i++){
					if (DebugCorePlugin.selectedStudies[i]==true){
						DssPluginCore.dssArray.add(DebugCorePlugin.dvDss[i]);
						DssPluginCore.dssArray.add(DebugCorePlugin.svDss[i]);
					}
				}
				try {
					DSSCatalogView dcv = (DSSCatalogView) getSite().getWorkbenchWindow()
                                        .getActivePage().showView(DSSCatalogView.ID);
					try {
						dcv.getViewer().setInput(DssPluginCore.dssArray);
						DataOps.clearGeoSchematicVariableData();
						DataOps.loadAllSchematicVariableData();
//						dcv.setInput(dssArray);
//						dcv.updateData();
					} catch (Exception ex) {
						Status status = new Status(IStatus.ERROR,
						                Activator.PLUGIN_ID,
						                "Error opening dss file: ", ex);
						StatusManager.getManager().handle(status,
								StatusManager.LOG);
					}
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
				// put in Catalog View
			}else{
				showDssFileErrorDialog(0);
			}
		}else{
//			if (checkFoldersExist()){
////				close();
//				processViews();
//			}else{
//				showDssFileErrorDialog(2);
//			}
		}
	}


	@Override
	public void setFocus() {
		dialogArea.setFocus();
	}
}
