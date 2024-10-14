package wrimsv2_plugin.debugger.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.batchrun.LaunchConfigInfo;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WPPInfeasibilityView extends ViewPart {

	private Composite area;
	
	private List list;
	private Button fileButt;
	private Button folderButt;
	private Button importButt;
	private Button deleteButt;
	private Button deleteAllButt;
	private Button insertFileButt;
	private Button insertFolderButt;
	private String prevFilterPath="";
	private Button constraintButt;
	private Button insertConstraintButt;
	
	private final String cPref="c: ";
	private final String fPref="f: ";
	
	@Override
	public void createPartControl(Composite parent) {
		area = new Composite(parent, SWT.NONE);
		Font font = parent.getFont();
		
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 9;
		area.setLayout(topLayout);
		area.setFont(font);
		
		Group group1 = new Group(area, SWT.SHADOW_IN);
	    group1.setText("&Select WRESL files or Constraints for Infeasibility Analysis:");
	    group1.setLayout(topLayout);
	    GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 9;
		group1.setLayoutData(gd);
		
		/*
		noSelButt = new Button(group1, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		noSelButt.setText("No");
		noSelButt.setLayoutData(gd);
		noSelButt.setFont(font);
		noSelButt.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		selButt = new Button(group1, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		selButt.setText("Yes");
		selButt.setLayoutData(gd);
		selButt.setFont(font);
		selButt.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
		list = new List(area, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 9;
		gd.heightHint=200;
		list.setLayoutData(gd);
		//list.setSize(600, 400);

		importButt = new Button(area, SWT.NONE);;
	    importButt.setText("Import");
		importButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		importButt.setLayoutData(gd);
		importButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String [] {"*.ifs;*.launch"});
				dialog.setFilterPath(prevFilterPath);
				String ifsFilePath = dialog.open();
				if (ifsFilePath != null){
					importIfsFile(ifsFilePath);	
					saveListData();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	    fileButt = new Button(area, SWT.NONE);
	    fileButt.setText("Add Files");
		fileButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		fileButt.setLayoutData(gd);
		fileButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String [] {"*.wresl"});
				dialog.setFilterPath(prevFilterPath);
				dialog.open();
				String filterPath=dialog.getFilterPath();
				prevFilterPath=filterPath;
				String[] absPaths = dialog.getFileNames();
				for (int i=0; i<absPaths.length; i++){
					String absPath=filterPath+"\\"+absPaths[i];
					String relativePath="";
					if (!absPath.equals("")){
						String launchPath = DebugCorePlugin.launchConfig.getFile().getLocation().toFile().getAbsolutePath();
						relativePath=makeRelativePath(launchPath, absPath);
					}
					list.add(fPref+relativePath);
				}
				saveListData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		folderButt = new Button(area, SWT.NONE);;
	    folderButt.setText("Add Folder");
		folderButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		folderButt.setLayoutData(gd);
		folderButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterPath(prevFilterPath);
				String absPath = dialog.open();
				prevFilterPath=dialog.getFilterPath();
				String relativePath="";
				if (absPath!=null && !absPath.equals("")){
					String launchPath = DebugCorePlugin.launchConfig.getFile().getLocation().toFile().getAbsolutePath();
					relativePath=makeRelativePath(launchPath, absPath);
				}
				list.add(fPref+relativePath);	
				saveListData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		constraintButt = new Button(area, SWT.NONE);;
		constraintButt.setText("Add Constraint");
		constraintButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		constraintButt.setLayoutData(gd);
		constraintButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				InputDialog id = new InputDialog(getSite().getShell(), "Add Constraint", "Please add constraint name:", null, null);
				id.open();
				String cn=id.getValue();
				if (cn !=null && !cn.equals("")) list.add(cPref+cn);	
				saveListData();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertFileButt = new Button(area, SWT.NONE);
		insertFileButt.setText("Insert Files");
		insertFileButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertFileButt.setLayoutData(gd);
		insertFileButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String [] {"*.wresl"});
				dialog.setFilterPath(prevFilterPath);
				dialog.open();
				String filterPath=dialog.getFilterPath();
				prevFilterPath=filterPath;
				String[] absPaths = dialog.getFileNames();
				for (int i=0; i<absPaths.length; i++){
					String absPath=filterPath+"\\"+absPaths[i];
					String relativePath="";
					if (!absPath.equals("")){
						String launchPath = DebugCorePlugin.launchConfig.getFile().getLocation().toFile().getAbsolutePath();
						relativePath=makeRelativePath(launchPath, absPath);
					}
					int index=list.getSelectionIndex();
					if (index<0){
						index=0;
					}
					list.add(fPref+relativePath, index);
				}
				saveListData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertFolderButt = new Button(area, SWT.NONE);;
		insertFolderButt.setText("Insert Folder");
		insertFolderButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertFolderButt.setLayoutData(gd);
		insertFolderButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				DirectoryDialog dialog = new DirectoryDialog(getSite().getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterPath(prevFilterPath);
				String absPath = dialog.open();
				prevFilterPath = dialog.getFilterPath();
				String relativePath="";
				if (absPath!=null && !absPath.equals("")){
					String launchPath = DebugCorePlugin.launchConfig.getFile().getLocation().toFile().getAbsolutePath();
					relativePath=makeRelativePath(launchPath, absPath);
				}
				int index=list.getSelectionIndex();
				if (index<0){
					index=0;
				}
				list.add(fPref+relativePath, index);	
				saveListData();;	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertConstraintButt = new Button(area, SWT.NONE);;
		insertConstraintButt.setText("Insert Constraint");
		insertConstraintButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertConstraintButt.setLayoutData(gd);
		insertConstraintButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				InputDialog id = new InputDialog(getSite().getShell(), "Insert Constraint", "Please insert constraint name:", null, null);
				id.open();
				String cn=id.getValue();
				int index=list.getSelectionIndex();
				if (index<0){
					index=0;
				}
				if (cn !=null && !cn.equals("")) list.add(cPref+cn, index);	
				saveListData();;	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		deleteButt = new Button(area, SWT.NONE);;
	    deleteButt.setText("Delete");
		deleteButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		deleteButt.setLayoutData(gd); 
		deleteButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				int[] sels=list.getSelectionIndices();
				list.remove(sels);
				saveListData();;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		deleteAllButt = new Button(area, SWT.NONE);;
	    deleteAllButt.setText("Delete All");
		deleteAllButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		deleteAllButt.setLayoutData(gd);
		deleteAllButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isRunTime()) return;
				list.removeAll();
				saveListData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public void setFocus() {
		area.setFocus();
	}

	public void importIfsFile(String filePath){
		filePath=filePath.toLowerCase();
		if (filePath.endsWith(".ifs")){
			list.removeAll();
			File file = new File(filePath);
			if (file.exists()){
				FileInputStream fs;
				try {
					fs = new FileInputStream(filePath);
					BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				    String line = br.readLine();
				    while (line !=null){
				    	list.add(line);
				    	line = br.readLine();
				    }
				    br.close();
				    fs.close();
				} catch (FileNotFoundException e) {
					WPPException.handleException(e);
				} catch (IOException e) {
					WPPException.handleException(e);
				}
			}
		}else if (filePath.endsWith(".launch")){
			list.removeAll();
			LaunchConfigInfo config = new LaunchConfigInfo(filePath);
			int size=config.getIntAttribute(DebugCorePlugin.ATTR_WPP_IFSNUMBERSELENTRIES,0);
			for (int i=0; i<size; i++){
				list.add(config.getStringAttribute(DebugCorePlugin.ATTR_WPP_IFSSELENTRYNAME+i, ""));
			}
		}
	}
	
	public String makeRelativePath(String absolutePath, String relativeTo){

        String[] absoluteDirectories = absolutePath.split("\\\\");
        String[] relativeDirectories = relativeTo.split("\\\\");

        //Get the shortest of the two paths
        int length = absoluteDirectories.length < relativeDirectories.length ? absoluteDirectories.length : relativeDirectories.length;

        //Use to determine where in the loop we exited
        int lastCommonRoot = -1;
        int index;

        //Find common root
        for (index = 0; index < length; index++){
        	if (absoluteDirectories[index].equalsIgnoreCase(relativeDirectories[index])){
                lastCommonRoot = index;
        	}else{
                break;
        	}
		}

        //If we didn't find a common prefix then throw
        if (lastCommonRoot == -1){
        	return relativeTo;
        }

        //Build up the relative path
        StringBuilder relativePath = new StringBuilder();

        //Add on the ..
       for (index = lastCommonRoot + 1; index < absoluteDirectories.length-1; index++){
            if (absoluteDirectories[index].length() > 0){
            	relativePath.append("..\\");
            }
       }
       
       //Add on the folders
       for (index = lastCommonRoot + 1; index < relativeDirectories.length - 1; index++){
    	   	relativePath.append(relativeDirectories[index] + "\\");
       }
       relativePath.append(relativeDirectories[relativeDirectories.length - 1]);

       return relativePath.toString();
	}
	
	public void saveListData(){
		
	}

	public boolean isRunTime(){
		boolean isRunTime=DebugCorePlugin.isRunning;
		if (isRunTime){
			showNoEditIfsFile();
		}
		return isRunTime;
	}
	
	public void showNoEditIfsFile(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Warning");
				messageBox.setMessage("You can edit this list under debug mode or run mode here. Before or after the run time, "+
				"please edit in the \"Infeasibility\" tab of the Launch Configuration.");
				messageBox.open();
			}
		});
	}
}
