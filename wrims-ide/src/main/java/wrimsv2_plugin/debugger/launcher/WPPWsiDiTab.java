package wrimsv2_plugin.debugger.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.CBCSetting;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.dialog.ConfigTab;
import wrimsv2_plugin.debugger.dialog.WPPDssToSqlDialog;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DataProcess;
import wrimsv2_plugin.tools.Encryption;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class WPPWsiDiTab extends AbstractLaunchConfigurationTab {

	private Text offsetText;
	private ILaunchConfiguration launchConfig;
	private Button wsidiGenBut;
	private Button outputWsidiDvOnlyBut;
	private WPPMainTab mainTab;
	private String externalPath="";
	
	public WPPWsiDiTab(WPPMainTab mainTab){
		this.mainTab=mainTab;
	}
	
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 7;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createVerticalSpacer(comp, 3);
		
		Label offsetLabel = new Label(comp, SWT.NONE);
		offsetLabel.setText("&Offset:");
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		offsetLabel.setLayoutData(gd);
		offsetLabel.setFont(font);
		
		offsetText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		offsetText.setLayoutData(gd);
		offsetText.setFont(font);
		offsetText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		outputWsidiDvOnlyBut = new Button(comp, SWT.CHECK);
		outputWsidiDvOnlyBut.setText("&Output WsiDi related dvar timeseries only");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=7;
		outputWsidiDvOnlyBut.setLayoutData(gd);
		outputWsidiDvOnlyBut.setFont(font);
		outputWsidiDvOnlyBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		outputWsidiDvOnlyBut.setSelection(true);
		
		wsidiGenBut = new Button(comp, SWT.NONE);
		wsidiGenBut.setText("&Wsi-Di Generator");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		wsidiGenBut.setLayoutData(gd);
		wsidiGenBut.setFont(font);
		wsidiGenBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						performApply((ILaunchConfigurationWorkingCopy) launchConfig);
						try {
							((ILaunchConfigurationWorkingCopy)launchConfig).doSave();
						} catch (CoreException e) {
							WPPException.handleException(e);
						}
						wsidigenerator();
					}
				});
			}
		});
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_WSIDIOFFSET, "1.2");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String offset = null;
		try {
			offset = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_WSIDIOFFSET, "1.2");
			offsetText.setText(offset);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		try {
			String isOutputWsiDiDvOnly = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_OUTPUTWSIDIDVONLY, "yes");
			if (isOutputWsiDiDvOnly.equalsIgnoreCase("yes")) {
				
			}else {
				
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		launchConfig=configuration;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String offset=offsetText.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_WSIDIOFFSET, offset);
		
		boolean isOutputWsiDiDvOnly=outputWsidiDvOnlyBut.getSelection();
		if (isOutputWsiDiDvOnly) {
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_OUTPUTWSIDIDVONLY, "yes");
		}else {
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_OUTPUTWSIDIDVONLY, "no");
		}
	}

	@Override
	public String getName() {
		return "Wsi-Di";
	}
	
	public void wsidigenerator(){
		String engineFileFullPath = "WRIMSv2_Engine.bat";
		try {
			String configFilePath = generateWsiDiConfigFile();
			FileWriter engineFile = new FileWriter(engineFileFullPath);
			PrintWriter out = new PrintWriter(engineFile);
			generateBatch(out, configFilePath);
			Process process = Runtime.getRuntime().exec("cmd /c start " + "WSIDIGenerator\\wsidi_generator.bat");
		}catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public String generateWsiDiConfigFile(){
		
		String configFilePath="";
		String mainFile=mainTab.fMainFileText.getText();
		try {				
			String mainFileAbsPath;
			if (new File(mainFile).isAbsolute()){
				mainFileAbsPath = mainFile;
			}else{
				mainFileAbsPath = procRelativePath(mainFile);
			}
			
			int index = mainFileAbsPath.lastIndexOf(File.separator);
			String mainDirectory = mainFileAbsPath.substring(0, index + 1);
			externalPath = mainDirectory + "External";
			
			String studyDir = new File(mainFileAbsPath).getParentFile().getParentFile().getAbsolutePath();
			String configName = "__study.config";
			File f = new File(studyDir, configName);
			File dir = new File(f.getParent());
			dir.mkdirs();
			f.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			 
			out.println("##################################################################################");
			out.println("# Command line Example:");
			out.println("# C:\\wrimsv2_SG\\bin\\runConfig_limitedXA.bat D:\\example\\EXISTING_BO.config");
			out.println("# ");	
			out.println("# Note:");			
			out.println("# 1. This config file and the RUN directory must be placed in the same directory.");
			out.println("# 2. Use relative path to increase the portability.");
			out.println("#    For example, use RUN\\main.wresl for MainFile and DSS\\INIT.dss for InitFile");
			out.println("##################################################################################");	
			out.println("");
			out.println("");
			
			out.println("MainFile           "+mainFileAbsPath.toLowerCase());
			if (DebugCorePlugin.solver.equalsIgnoreCase("CBC")){
				out.println("Solver            cbc");
				//out.println("cbclibname        jCbc_2.9.8.1");
			}else if (DebugCorePlugin.solver.equalsIgnoreCase("CBC2.10")){
				out.println("Solver            cbc");
				out.println("cbclibname        jCbc_v2.10");
			}else if (DebugCorePlugin.solver.equalsIgnoreCase("CBC2.9.8")){
				out.println("Solver            cbc");
				out.println("cbclibname        jCbc");
			}else{
				out.println("Solver             "+DebugCorePlugin.solver.toLowerCase());
			}
			String dvarFile = mainTab.fDvarFileText.getText();
			if (new File(dvarFile).isAbsolute()){
				//String wsidiDvarPath=getWsiDiDvarFilePath(dvarFile);
				String wsidiDvarPath=dvarFile.toLowerCase();
				wsidiDvarPath=wsidiDvarPath.substring(0, wsidiDvarPath.lastIndexOf(".dss"))+".csv";
				String lookupPath=getLookupFolderPath(mainFileAbsPath);
				out.println("DvarFile           "+wsidiDvarPath);
				createWsiDiMain(wsidiDvarPath, lookupPath);
			}else{
				String procDvarPath=procRelativePath(dvarFile);
				//String wsidiDvarFile=getWsiDiDvarFilePath(procDvarFile);
				String wsidiDvarPath=procDvarPath.toLowerCase();
				wsidiDvarPath=wsidiDvarPath.substring(0, wsidiDvarPath.lastIndexOf(".dss"))+".csv";
				String lookupFolder=getLookupFolderPath(mainFileAbsPath);
				out.println("DvarFile           " + wsidiDvarPath.toLowerCase());
				createWsiDiMain(wsidiDvarPath, lookupFolder);
			}
			String svarFile = mainTab.fSvarFileText.getText();
			if (new File(svarFile).isAbsolute()){
				out.println("SvarFile           "+svarFile.toLowerCase());
			}else{
				String procSvarFile=procRelativePath(svarFile);
				out.println("SvarFile           "+procSvarFile.toLowerCase());
			}
			String gwDataFolder = mainTab.groundWaterFolderText.getText();
			if (new File(gwDataFolder).isAbsolute()){
				out.println("GroundwaterDir     "+gwDataFolder.toLowerCase());
			}else{
				out.println("GroundwaterDir     "+procRelativePath(gwDataFolder).toLowerCase());
			}
			out.println("SvarAPart          " + mainTab.aPartText.getText().toLowerCase());
			out.println("SvarFPart          " + mainTab.svFPartText.getText().toLowerCase());
			String initFile = mainTab.fInitFileText.getText();
			if (new File(initFile).isAbsolute()){
				out.println("InitFile           "+initFile.toLowerCase());
			}else{
				out.println("InitFile           "+procRelativePath(initFile).toLowerCase());
			}
			out.println("InitFPart          "+mainTab.initFPartText.getText().toLowerCase());
			out.println("TimeStep           "+mainTab.timeStepCombo.getText().toLowerCase());
			out.println("StartYear          "+mainTab.startYearCombo.getText().toLowerCase());
			out.println("StartMonth         "+TimeOperation.monthValue(mainTab.startMonthCombo.getText().toLowerCase()));
			out.println("StartDay           "+mainTab.startDayCombo.getText().toLowerCase());
			out.println("StopYear           "+mainTab.endYearCombo.getText().toLowerCase());
			out.println("StopMonth          "+TimeOperation.monthValue(mainTab.endMonthCombo.getText().toLowerCase()));
			out.println("StopDay            "+mainTab.endDayCombo.getText().toLowerCase());
			out.println("IlpLog             "+"no");
			out.println("IlpLogFormat       "+"none");
			out.println("IlpLogVarValue     "+"no");
			out.println("IlpLogUsageMemory  "+"no");
			out.println("WreslPlus          "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_WRESLPLUS, "no"));
			out.println("AllowSvTsInit      "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_ALLOWSVTSINIT, "no"));
			out.println("AllRestartFiles    "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_ALLRESTARTFILES, "no"));
			out.println("NumberRestartFiles "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_NUMBERRESTARTFILES, "12"));
			out.println("VersionHecDssOutput "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_VHECLIB, "6"));
			out.println("DatabaseURL        "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_DATABASEURL, "none"));
			out.println("SQLGroup           "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_SQLGROUP, "calsim"));
			/*
			String ovOption=launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_OVOPTION, "0");
			String ovFile=launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_OVFILE, "");
			if (ovFile.trim().equals("")){
				out.println("OVOption           0");
				out.println("OVFile             .");
			}else if (new File(ovFile).isAbsolute()){
				out.println("OVOption           "+ovOption);
				out.println("OVFile             "+ovFile);
			}else{
				out.println("OVOption           "+ovOption);
				out.println("OVFile             "+FileProcess.procRelativePath(ovFile, launchConfig));
			}
			*/
			
			File wsidifvFile = new File(DebugCorePlugin.dataDir, "wsidi.fv");
			String isOutputWsiDiDvOnly=launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_OUTPUTWSIDIDVONLY, "yes");
			if (isOutputWsiDiDvOnly.equalsIgnoreCase("yes")) {
				out.println("OVOption           1");
				out.println("OVFile             "+wsidifvFile.getAbsolutePath());
			}else {
				out.println("OVOption           0");
				out.println("OVFile             .");
			}
			out.println("OutputCycleDatatoDss no");
						
			if (DebugCorePlugin.outputAllCycles){
				out.println("outputallcycledata yes");
			}else{
				out.println("outputallcycledata no");
			}
			
			out.println("selectedcycleoutput "+DebugCorePlugin.outputCycles.replace(" ", ""));
				
			if (DebugCorePlugin.showRunTimeMessage){
				out.println("showruntimemessage yes");
			}else{
				out.println("showruntimemessage no");
			}
			
			if (DebugCorePlugin.printGWFuncCalls){
				out.println("printgwfunccalls yes");
			}else{
				out.println("printgwfunccalls no");
			}
			
			String dssEndOutput = launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_DSSENDOUTPUT, "yes");
			if (!dssEndOutput.equalsIgnoreCase("yes")){
				out.println("YearOutputSection  "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_YEARSECTIONOUTPUT, "10"));
				out.println("MonthMemorySection "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_MONMEMSECTION, "24"));
			}
			
			//PlaceHolder
			//if (DebugCorePlugin.lauchType==1) out.println("unchangeGWRestart "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_UNCHANGEGWRESTART, "yes");
			
			//if (DebugCorePlugin.solver.equalsIgnoreCase("LpSolve")) {
			//	out.println("LpSolveConfigFile         callite.lpsolve");
			//	out.println("LpSolveNumberOfRetries    2");				
			//}	
			
			CBCSetting.changeSetting = true;
			if (CBCSetting.changeSetting){
				out.println("cbcTolerancePrimal        "+CBCSetting.cbcTolerancePrimal);
				out.println("cbcTolerancePrimalRelax   "+CBCSetting.cbcTolerancePrimalRelax);
				out.println("cbcToleranceWarmPrimal    "+CBCSetting.cbcToleranceWarmPrimal);
				out.println("cbcToleranceInteger       "+CBCSetting.cbcToleranceInteger);
				out.println("cbcToleranceIntegerCheck  "+CBCSetting.cbcToleranceIntegerCheck);
				out.println("cbcToleranceZero          "+CBCSetting.cbcToleranceZero);
			}
			out.println("cbcHintRelaxPenalty       "+CBCSetting.cbcHintRelaxPenalty);
			out.println("cbcHintTimeMax            "+DataProcess.doubleStringtoInt(CBCSetting.cbcHintTimeMax));
			
			//out.println("IfsIsSelFile              "+launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_IFSISSELENTRY, "yes"));
			
			ConfigTab.writeConfigSetting(out);
			
			out.close();
			configFilePath= new File(studyDir, configName).getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		generateIfsFile(configFilePath);
		
		return configFilePath;
	}
	
	public void generateIfsFile(String configFilePath){
		try {
			String ifsFilePath = configFilePath+".ifs";
			File f = new File(ifsFilePath);
			f.createNewFile();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			int size = launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_IFSNUMBERSELENTRIES, 0);
			for (int i=0; i<size; i++){
				String relativePath=launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_IFSSELENTRYNAME+i, "");
				out.println(relativePath);
			}
			out.close();
		} catch (CoreException e) {
			WPPException.handleException(e);
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public void generateBatch(PrintWriter out, String configFilePath){
		String freeXA;
		//freeXA = launchConfig.getAttribute(DebugCorePlugin.ATTR_WPP_FREEXA, "no");
		freeXA="no";
		String jarXA;
		if (freeXA.equalsIgnoreCase("yes")){
			jarXA="CalLiteV16.jar";
		}else{
			jarXA="XAOptimizer.jar";
		}
		out.println("@echo off");
		out.println();
		out.println("set path=" + externalPath + ";"+"lib;%path%");
		out.println("set temp_wrims2=jre\\bin");
		out.println("set TF_CPP_MIN_LOG_LEVEL=2");
		out.println();
		/*
		String xmx="1280m";
		if (System.getProperty("os.arch").equalsIgnoreCase("amd64")){
			xmx="4096m";
		}
		*/
		out.println("jre\\bin\\java -Xmx"+DebugCorePlugin.xmx+"m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"" + externalPath + ";lib\" -cp \""+externalPath+";"+"lib\\external;lib\\*\" wrimsv2.components.ControllerBatch "+"-config="+configFilePath);
		out.close();
	}
	
	public void createWsiDiMain(String dvarPath, String lookupPath){
		String wsidiMainTemplate = ".\\WSIDIGenerator\\Main_template.py";
		String wsidiMainFile = ".\\WSIDIGenerator\\Main.py";
		try {
	         FileInputStream fs= new FileInputStream(wsidiMainTemplate);
	         BufferedReader br = new BufferedReader(new InputStreamReader(fs));
	         LineNumberReader reader = new LineNumberReader(br);
	         FileWriter writer = new FileWriter(wsidiMainFile);
	         String line;
	         int count =0;
	         while((line = br.readLine())!=null){
	              count++;
	              if(count==28){
	            	  writer.write("        studyDvName=r\""+dvarPath+"\"\n");
	              }else if (count==29){
	            	  writer.write("        lookupName=r\""+lookupPath+"\"\n");
	              }else if (count==30){
	            	  writer.write("        launchName=r\""+launchConfig.getFile().getLocation().toFile().getAbsolutePath()+"\"\n");
	         	  }else if (count==31){
	         		  writer.write("        offset="+offsetText.getText()+"\n");
	         	  }else{
	                  writer.append(line+"\n");
	              }
	         }
	         writer.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public String getWsiDiDvarFilePath(String dvFileName){
		File dvFile=new File(dvFileName);
		return dvFile.getParentFile().getAbsolutePath()+"\\genwsidi_dv.dss";
	}
	
	public String getLookupFolderPath(String mainFilePath){
		int index = mainFilePath.lastIndexOf(File.separator);
		String mainDirectory = mainFilePath.substring(0, index + 1);
		String lookupPath = mainDirectory + "lookup";
		return lookupPath;
	}
	
	public String procRelativePath(String path){
		String absPath=launchConfig.getFile().getLocation().toFile().getParentFile().getAbsolutePath();
		absPath=absPath+"\\"+path;
		return absPath;
	}
}
