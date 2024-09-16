package wrimsv2_plugin.debugger.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.batchrun.LaunchConfigInfo;
import wrimsv2_plugin.debugger.core.CBCSetting;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.dialog.WPPDssToSqlDialog;
import wrimsv2_plugin.debugger.dialog.WPPReSimDialog;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.menuitem.EnableMenus;
import wrimsv2_plugin.debugger.toolbaritem.EnableButtons;
import wrimsv2_plugin.debugger.toolbaritem.HandlePauseResumeButton;
import wrimsv2_plugin.tools.DataProcess;
import wrimsv2_plugin.tools.Encryption;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class WPPInfeasibilityTab extends AbstractLaunchConfigurationTab {

	private ILaunchConfiguration launchConfig;
	private String launchPath;
	private Button noSelButt;
	private Button selButt;
	private List list;
	private Button fileButt;
	private Button folderButt;
	private Button importButt;
	private Button deleteButt;
	private Button deleteAllButt;
	private Button insertFileButt;
	private Button insertFolderButt;
	private Button constraintButt;
	private Button insertConstraintButt;
	private Button upButt;
	private Button downButt;
	private Button resimButt;
	private Button resumeButt;
	private Button pauseButt;
	
	private final String cPref="c: ";
	private final String fPref="f: ";
	
	public WPPInfeasibilityTab(){
		
	}
	
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 11;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createVerticalSpacer(comp, 3);
		
		Label Label1 = new Label(comp, SWT.NONE|SWT.WRAP);
	    Label1.setText("Select WRESL files or Constraints for Infeasibility Analysis: ");
	    //Label1.setLayout(topLayout);
	    GridData gd = new GridData(GridData.BEGINNING, SWT.TOP, true, false);
		gd.horizontalSpan = 11;
		Label1.setLayoutData(gd);
		
		Label Label2 = new Label(comp, SWT.NONE|SWT.WRAP);
	    Label2.setText("(you can edit the list before or during the debug and run mode; please click \"Apply\" button to save the changes)");
	    gd = new GridData(GridData.BEGINNING, SWT.TOP, true, false);
		gd.horizontalSpan = 11;
		Label2.setLayoutData(gd);
		
		Label Label3 = new Label(comp, SWT.NONE|SWT.WRAP);
	    Label3.setText("Relative paths relative to the launch file are used in the list.");
	    gd = new GridData(GridData.BEGINNING, SWT.TOP, true, false);
		gd.horizontalSpan = 11;
		Label3.setLayoutData(gd);
		
		/*
		Group Group1 = new Group(comp, SWT.NONE|SWT.WRAP);
	    Group1.setText("Select WRESL files or Constraints for Infeasibility Analysis: ");
	    Group1.setLayout(topLayout);
	    GridData gd = new GridData(GridData.BEGINNING, SWT.TOP, true, false);
		gd.horizontalSpan = 9;
		Group1.setLayoutData(gd);

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
		
		list = new List(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 11;
		gd.heightHint=200;
		list.setLayoutData(gd);
		//list.setSize(600, 400);

		importButt = new Button(comp, SWT.NONE);;
	    importButt.setText("Import");
		importButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		importButt.setLayoutData(gd);
		importButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String [] {"*.ifs;*.launch"});
				dialog.setFilterPath(DebugCorePlugin.prevIfsFilterPath);
				String ifsFilePath = dialog.open();
				if (ifsFilePath != null){
					importIfsFile(ifsFilePath);	
					updateLaunchConfigurationDialog();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	    fileButt = new Button(comp, SWT.NONE);
	    fileButt.setText("Add Files");
		fileButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		fileButt.setLayoutData(gd);
		fileButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String [] {"*.wresl"});
				dialog.setFilterPath(DebugCorePlugin.prevIfsFilterPath);
				dialog.open();
				String filterPath=dialog.getFilterPath();
				DebugCorePlugin.prevIfsFilterPath=filterPath;
				String[] absPaths = dialog.getFileNames();
				for (int i=0; i<absPaths.length; i++){
					String absPath=filterPath+"\\"+absPaths[i];
					String relativePath="";
					if (!absPath.equals("")){
						relativePath=makeRelativePath(launchPath, absPath);
					}
					list.add(fPref+relativePath);
				}
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		folderButt = new Button(comp, SWT.NONE);;
	    folderButt.setText("Add Folder");
		folderButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		folderButt.setLayoutData(gd);
		folderButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterPath(DebugCorePlugin.prevIfsFilterPath);
				String absPath = dialog.open();
				DebugCorePlugin.prevIfsFilterPath=dialog.getFilterPath();
				String relativePath="";
				if (absPath!=null && !absPath.equals("")){
					relativePath=makeRelativePath(launchPath, absPath);
					list.add(fPref+relativePath);	
					updateLaunchConfigurationDialog();
				}	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		constraintButt = new Button(comp, SWT.NONE);;
		constraintButt.setText("Add Constraint");
		constraintButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		constraintButt.setLayoutData(gd);
		constraintButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), "Add Constraint", "Please add constraint name:", null, null);
				id.open();
				String cn=id.getValue();
				if (cn !=null && !cn.equals("")) {
					list.add(cPref+cn);
					updateLaunchConfigurationDialog();
				}	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertFileButt = new Button(comp, SWT.NONE);
		insertFileButt.setText("Insert Files");
		insertFileButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertFileButt.setLayoutData(gd);
		insertFileButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String [] {"*.wresl"});
				dialog.setFilterPath(DebugCorePlugin.prevIfsFilterPath);
				dialog.open();
				String filterPath=dialog.getFilterPath();
				DebugCorePlugin.prevIfsFilterPath=filterPath;
				String[] absPaths = dialog.getFileNames();
				for (int i=0; i<absPaths.length; i++){
					String absPath=filterPath+"\\"+absPaths[i];
					String relativePath="";
					if (!absPath.equals("")){
						relativePath=makeRelativePath(launchPath, absPath);
					}
					int index=list.getSelectionIndex();
					if (index<0){
						index=0;
					}
					list.add(fPref+relativePath, index);
				}
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertFolderButt = new Button(comp, SWT.NONE);;
		insertFolderButt.setText("Insert Folder");
		insertFolderButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertFolderButt.setLayoutData(gd);
		insertFolderButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterPath(DebugCorePlugin.prevIfsFilterPath);
				String absPath = dialog.open();
				DebugCorePlugin.prevIfsFilterPath = dialog.getFilterPath();
				String relativePath="";
				if (absPath!=null && !absPath.equals("")){
					relativePath=makeRelativePath(launchPath, absPath);
					int index=list.getSelectionIndex();
					if (index<0){
						index=0;
					}
					list.add(fPref+relativePath, index);	
					updateLaunchConfigurationDialog();	
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		insertConstraintButt = new Button(comp, SWT.NONE);;
		insertConstraintButt.setText("Insert Constraint");
		insertConstraintButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		insertConstraintButt.setLayoutData(gd);
		insertConstraintButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), "Insert Constraint", "Please insert constraint name:", null, null);
				id.open();
				String cn=id.getValue();
				int index=list.getSelectionIndex();
				if (index<0){
					index=0;
				}
				if (cn !=null && !cn.equals("")) {
					list.add(cPref+cn, index);
					updateLaunchConfigurationDialog();
				}	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		deleteButt = new Button(comp, SWT.NONE);;
	    deleteButt.setText("Delete");
		deleteButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		deleteButt.setLayoutData(gd); 
		deleteButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] sels=list.getSelectionIndices();
				list.remove(sels);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		deleteAllButt = new Button(comp, SWT.NONE);;
	    deleteAllButt.setText("Delete All");
		deleteAllButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		deleteAllButt.setLayoutData(gd);
		deleteAllButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				list.removeAll();
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		upButt = new Button(comp, SWT.NONE);;
	    upButt.setText("Move Up");
		upButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		upButt.setLayoutData(gd);
		upButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = list.getSelectionIndex();
				if (index>0){
					String sel=list.getItem(index);
					list.setItem(index, list.getItem(index-1));
					list.setItem(index-1, sel);
					list.deselectAll();
					list.select(index-1);
					updateLaunchConfigurationDialog();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		downButt = new Button(comp, SWT.NONE);;
	    downButt.setText("Move Down");
		downButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		downButt.setLayoutData(gd);
		downButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = list.getSelectionIndex();
				if (index>=0 && index<list.getItemCount()-1){
					String sel=list.getItem(index);
					list.setItem(index, list.getItem(index+1));
					list.setItem(index+1, sel);
					list.deselectAll();
					list.select(index+1);
					updateLaunchConfigurationDialog();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		resimButt = new Button(comp, SWT.NONE);;
		resimButt.setText("Re-simulation");
		resimButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		resimButt.setLayoutData(gd);
		resimButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (HandlePauseResumeButton.status==3){
					WPPReSimDialog dialog= new WPPReSimDialog(getShell());
					dialog.openDialog();	
				}else{
					showWarning(0);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});		
		
		resumeButt = new Button(comp, SWT.NONE);;
		resumeButt.setText("Resume");
		resumeButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		resumeButt.setLayoutData(gd);
		resumeButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (HandlePauseResumeButton.status==3){
					try {
						if (DebugCorePlugin.isDebugging){
							if (DebugCorePlugin.target.isSuspended()){
								DebugCorePlugin.debugSet.updateDebugTimeSet();
								DebugCorePlugin.target.resume();
								enableRunMenu(1);
							}
						}
					} catch (DebugException ex) {
						WPPException.handleException(ex);
					}	
				}else{
					showWarning(1);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});	
		
		pauseButt = new Button(comp, SWT.NONE);;
		pauseButt.setText("Pause");
		pauseButt.setFont(font);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		pauseButt.setLayoutData(gd);
		pauseButt.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (HandlePauseResumeButton.status==1){
					try {
						if (DebugCorePlugin.isDebugging){
							if (!DebugCorePlugin.target.isSuspended()){
								DebugCorePlugin.target.pause();
								enableRunMenu(3);
							}
						}
					} catch (DebugException ex) {
						WPPException.handleException(ex);
					}
				}else{
					showWarning(2);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});	
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		launchConfig=configuration;
		IFile f = launchConfig.getFile();
		if (f != null){
			launchPath=f.getLocation().toFile().getAbsolutePath();
		
			try {
			/*
			String isSelEntries = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_IFSISSELENTRY, "no");
			if (isSelEntries.equalsIgnoreCase("no")){
				noSelButt.setSelection(true);
			}else{
				selButt.setSelection(true);
			}
			*/
			
				list.removeAll();
				int numberSelFiles=configuration.getAttribute(DebugCorePlugin.ATTR_WPP_IFSNUMBERSELENTRIES, 0);
				for (int i=0; i<numberSelFiles; i++){
					String relativePath=configuration.getAttribute(DebugCorePlugin.ATTR_WPP_IFSSELENTRYNAME+i, "");
					list.add(relativePath);
				}
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		/*
		if (noSelButt.getSelection()){
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_IFSISSELENTRY, "no");
		}else{
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_IFSISSELENTRY, "yes");
		}
		*/
		
		int numberSelFiles=list.getItemCount();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_IFSNUMBERSELENTRIES, numberSelFiles);
		
		for (int i=0; i<numberSelFiles; i++){
			String relativePath=list.getItem(i);
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_IFSSELENTRYNAME+i, relativePath);			
		}
		
		if (DebugCorePlugin.isRunning){
			generateIfsFile(configuration);
		}
	}

	@Override
	public String getName() {
		return "Infeasibility";
	}
	
	public String procRelativePath(String path){
		String absPath=launchConfig.getFile().getLocation().toFile().getParentFile().getAbsolutePath();
		absPath=absPath+"\\"+path;
		return absPath;
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
	
	public void generateIfsFile(ILaunchConfiguration configuration){
		try {
			File f = new File(DebugCorePlugin.ifsFilePath);
			f.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			int size = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_IFSNUMBERSELENTRIES, 0);
			for (int i=0; i<size; i++){
				String relativePath=configuration.getAttribute(DebugCorePlugin.ATTR_WPP_IFSSELENTRYNAME+i, "");
				out.println(relativePath);
			}
			out.close();
		} catch (CoreException e) {
			WPPException.handleException(e);
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public void showWarning(final int flag){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				String proc="run re-simulation";
				if (flag==0){
					proc="run re-simulation";
				}else if (flag==1){
					proc="rusume your simulatoin";
				}else if (flag==2){
					proc="pause your simulatoin";
				}
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Warning");
				if (flag==0 || flag==1){
					messageBox.setMessage("You need first pause your model during the debug mode before you can "+proc+".");
				}else{
					messageBox.setMessage("You need first run your model under the debug mode before you can "+proc+".");
				}
				messageBox.open();
			}
		});
	}
	
	public void enableRunMenu(int flag){
		HandlePauseResumeButton.procPauseResumeToolbarItem(flag);
		HashMap<String, Boolean> enableMenuMap=new HashMap<String, Boolean>();
		HashMap<String, Boolean> enableButtonMap=new HashMap<String, Boolean>();
		if (flag==3){
			enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		}else{
			enableMenuMap.put(DebugCorePlugin.ID_WPP_TERMINATEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_PAUSEMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SUSPENDMENU, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESUMEMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_RESIMMENU, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLE, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEP, true);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETODVFILE, false);
			enableMenuMap.put(DebugCorePlugin.ID_WPP_SAVETOSVFILE, false);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTCYCLEBUTTON, true);
			enableButtonMap.put(DebugCorePlugin.ID_WPP_NEXTTIMESTEPBUTTON, true);
		}
		new EnableMenus(enableMenuMap);
		new EnableButtons(enableButtonMap);
	}
}
