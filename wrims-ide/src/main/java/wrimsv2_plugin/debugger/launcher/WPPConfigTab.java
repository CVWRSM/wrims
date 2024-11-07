package wrimsv2_plugin.debugger.launcher;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WPPConfigTab extends AbstractLaunchConfigurationTab {
	
	private Button wpButton;
	private Button xaButton;
	private Button allowSvTsInitButton;
	private Button DSSHDF5ConversionButton;
	private ILaunchConfiguration currConfiguration;
	private Button restartFilesButton;
	private Button allRestartFilesButton;
	private Text restartFilesText;
	private Button compButton;
	private Button dssEndOutputButton;
	private Button dssSectionOutputButton;
	//private Text yearSectionText;
	private Text memSectionText;
	private Text yearSectionText;
	private Button bvHecLib6;
	private Button bvHecLib7;

	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 6;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createVerticalSpacer(comp, 3);
		
		Label wpLabel = new Label(comp, SWT.NONE);
		wpLabel.setText("&WRESL Plus:");
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		wpLabel.setLayoutData(gd);
		wpLabel.setFont(font);
		
		wpButton = new Button(comp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		wpButton.setLayoutData(gd);
		wpButton.setFont(font);
		wpButton.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();			
			}
		});
		
		Label xaLabel = new Label(comp, SWT.NONE);
		xaLabel.setText("&XA Free Limited License:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		xaLabel.setLayoutData(gd);
		xaLabel.setFont(font);
		
		xaButton = new Button(comp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		xaButton.setLayoutData(gd);
		xaButton.setFont(font);
		xaButton.setEnabled(false);
		xaButton.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();			
			}
		});
		
		Label allowSvTsInitLabel = new Label(comp, SWT.NONE);
		allowSvTsInitLabel.setText("&Allow SV File Provide Timeseries Initial Data:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		allowSvTsInitLabel.setLayoutData(gd);
		allowSvTsInitLabel.setFont(font);
		
		allowSvTsInitButton = new Button(comp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		allowSvTsInitButton.setLayoutData(gd);
		allowSvTsInitButton.setFont(font);
		allowSvTsInitButton.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();			
			}
		});
		
		DSSHDF5ConversionButton = new Button(comp, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		DSSHDF5ConversionButton.setLayoutData(gd);
		DSSHDF5ConversionButton.setText("DSS HDF5 Conversion");
		DSSHDF5ConversionButton.setFont(font);
		DSSHDF5ConversionButton.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String launchFileName=currConfiguration.getLocation().toOSString();
				try {
					String conversionFileName="DssHDF5Converter_Launch.bat";
					FileWriter conversionFile = new FileWriter(conversionFileName);
					PrintWriter out = new PrintWriter(conversionFile);
					out.println("@echo off");
					out.println();
					out.println("set path=lib;%path%");
					out.println("set temp_wrims2=jre\\bin");
					out.println("set TF_CPP_MIN_LOG_LEVEL=2");
					out.println();
					out.println("jre\\bin\\java -Xmx4096m -Xss1024K -XX:+CreateMinidumpOnCrash -Duser.timezone=Etc/GMT+8 -Djava.library.path=\"lib\" -cp \"lib\\external;lib\\*\" wrimsv2.hdf5.DSSHDF5Converter -launch="+launchFileName);
					out.close();
					Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "start", "/w", conversionFileName}, 
							null, null); 
				} catch (IOException ex) {
					WPPException.handleException(ex);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

		});
				
		Label restartFilesLabel = new Label(comp, SWT.NONE);
		restartFilesLabel.setText("&Number of Groundwater Restart Files:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		restartFilesLabel.setLayoutData(gd);
		restartFilesLabel.setFont(font);
				
		restartFilesButton = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		restartFilesButton.setLayoutData(gd);
		restartFilesButton.setFont(font);
		restartFilesButton.setEnabled(true);
		restartFilesButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				restartFilesButton.setSelection(true);
				allRestartFilesButton.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				restartFilesButton.setSelection(true);
				allRestartFilesButton.setSelection(false);
				updateLaunchConfigurationDialog();				
			}
			
		});
		
		restartFilesText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		restartFilesText.setLayoutData(gd);
		restartFilesText.setFont(font);
		restartFilesText.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});
		
		Label spaceLabel = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		spaceLabel.setLayoutData(gd);
		spaceLabel.setFont(font);
		spaceLabel.setText(" ");
		
		allRestartFilesButton = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		allRestartFilesButton.setLayoutData(gd);
		allRestartFilesButton.setFont(font);
		allRestartFilesButton.setEnabled(true);
		allRestartFilesButton.setText("All the files");
		allRestartFilesButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				allRestartFilesButton.setSelection(true);
				restartFilesButton.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				allRestartFilesButton.setSelection(true);
				restartFilesButton.setSelection(false);
				updateLaunchConfigurationDialog();
			}
			
		});
		
		Label compLabel = new Label(comp, SWT.NONE);
		compLabel.setText("Compile Only:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		compLabel.setLayoutData(gd);
		compLabel.setFont(font);
		
		compButton = new Button(comp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 4;
		compButton.setLayoutData(gd);
		compButton.setFont(font);
		compButton.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();			
			}
		});
		
		dssEndOutputButton = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		dssEndOutputButton.setLayoutData(gd);
		dssEndOutputButton.setFont(font);
		dssEndOutputButton.setEnabled(true);
		dssEndOutputButton.setText("Dss Output at the End");
		dssEndOutputButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				dssEndOutputButton.setSelection(true);
				dssSectionOutputButton.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				dssEndOutputButton.setSelection(true);
				dssSectionOutputButton.setSelection(false);
				updateLaunchConfigurationDialog();				
			}
			
		});
		
		dssSectionOutputButton = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		dssSectionOutputButton.setLayoutData(gd);
		dssSectionOutputButton.setFont(font);
		dssSectionOutputButton.setEnabled(true);
		dssSectionOutputButton.setText("Dss Output at the End of Every ");
		dssSectionOutputButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				dssSectionOutputButton.setSelection(true);
				dssEndOutputButton.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				dssEndOutputButton.setSelection(true);
				dssSectionOutputButton.setSelection(false);
				updateLaunchConfigurationDialog();				
			}
			
		});
		
		yearSectionText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		yearSectionText.setLayoutData(gd);
		yearSectionText.setFont(font);
		yearSectionText.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});
				
		Label yearSectionOutputLabel = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		yearSectionOutputLabel.setLayoutData(gd);
		yearSectionOutputLabel.setFont(font);
		yearSectionOutputLabel.setText("Years");
				
		Label memSectionLabel = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		memSectionLabel.setLayoutData(gd);
		memSectionLabel.setFont(font);
		memSectionLabel.setText("Keep Previous Additional Number of Months Data in Memory");
		
		memSectionText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		memSectionText.setLayoutData(gd);
		memSectionText.setFont(font);
		memSectionText.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
			
		});
		
		Label memMonSectionOutputLabel = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		memMonSectionOutputLabel.setLayoutData(gd);
		memMonSectionOutputLabel.setFont(font);
		memMonSectionOutputLabel.setText("Months");

		Label lvHecLib = new Label(comp, SWT.NONE);
		lvHecLib.setText("&Hec Lib version for output dss file:");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan=2;
		lvHecLib.setLayoutData(gd);
		lvHecLib.setFont(font);

		bvHecLib6 = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan =2;
		bvHecLib6.setText("6");
		bvHecLib6.setLayoutData(gd);
		bvHecLib6.setFont(font);
		bvHecLib6.setEnabled(true);
		bvHecLib6.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				bvHecLib6.setSelection(true);
				bvHecLib7.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				bvHecLib6.setSelection(true);
				bvHecLib7.setSelection(false);
				updateLaunchConfigurationDialog();
			}

		});

		bvHecLib7 = new Button(comp, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan =2;
		bvHecLib7.setText("7");
		bvHecLib7.setLayoutData(gd);
		bvHecLib7.setFont(font);
		bvHecLib7.setEnabled(true);
		bvHecLib7.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				bvHecLib7.setSelection(true);
				bvHecLib6.setSelection(false);
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				bvHecLib7.setSelection(true);
				bvHecLib6.setSelection(false);
				updateLaunchConfigurationDialog();
			}

		});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_WRESLPLUS, "no");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ALLOWSVTSINIT, "no");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ALLRESTARTFILES, "no");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_NUMBERRESTARTFILES, "12");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_COMPILEONLY, "no");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_DSSENDOUTPUT, "yes");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_YEARSECTIONOUTPUT, "10");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_MONMEMSECTION, "24");
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_VHECLIB, "6");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		currConfiguration=configuration;
		String wreslPlus = null;
		try {
			wreslPlus = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_WRESLPLUS, "no");
			if (wreslPlus.equalsIgnoreCase("yes")){
				wpButton.setSelection(true);
			}else{
				wpButton.setSelection(false);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String freeXA = null;
		try {
			freeXA = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_FREEXA, "no");
			if (freeXA.equalsIgnoreCase("yes")){
				xaButton.setSelection(true);
			}else{
				xaButton.setSelection(false);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String allowSvTsInit = null;
		try {
			allowSvTsInit = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ALLOWSVTSINIT, "no");
			if (allowSvTsInit.equalsIgnoreCase("yes")){
				allowSvTsInitButton.setSelection(true);
			}else{
				allowSvTsInitButton.setSelection(false);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String allRestartFiles = null;
		try {
			allRestartFiles = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_ALLRESTARTFILES, "no");
			if (allRestartFiles.equalsIgnoreCase("yes")){
				allRestartFilesButton.setSelection(true);
				restartFilesButton.setSelection(false);
			}else{
				allRestartFilesButton.setSelection(false);
				restartFilesButton.setSelection(true);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		try {
			String numberRestartFiles=configuration.getAttribute(DebugCorePlugin.ATTR_WPP_NUMBERRESTARTFILES, "12");
			restartFilesText.setText(numberRestartFiles);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String compileOnly = null;
		try {
			compileOnly = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_COMPILEONLY, "no");
			if (compileOnly.equalsIgnoreCase("yes")){
				compButton.setSelection(true);
			}else{
				compButton.setSelection(false);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String dssEndOutput = null;
		try {
			dssEndOutput = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DSSENDOUTPUT, "yes");
			if (dssEndOutput.equalsIgnoreCase("yes")){
				dssEndOutputButton.setSelection(true);
				dssSectionOutputButton.setSelection(false);
			}else{
				dssEndOutputButton.setSelection(false);
				dssSectionOutputButton.setSelection(true);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String yearSectionOutput = null;
		try {
			yearSectionOutput = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_YEARSECTIONOUTPUT, "10");
			yearSectionText.setText(yearSectionOutput);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
		
		String monMemSection  = null;
		try {
			monMemSection = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_MONMEMSECTION, "24");
			memSectionText.setText(monMemSection);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}

		String vHecLib  = null;
		try {
			vHecLib = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_VHECLIB, "6");
			if (vHecLib.equals("6")){
				bvHecLib6.setSelection(true);
				bvHecLib7.setSelection(false);
			}else{
				bvHecLib6.setSelection(false);
				bvHecLib7.setSelection(true);
			}
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String wreslPlus="no";
		if (wpButton.getSelection()){
			wreslPlus="yes";
		}else{
			wreslPlus="no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_WRESLPLUS, wreslPlus);
		
		String freeXA="no";
		if (xaButton.getSelection()){
			freeXA="yes";
		}else{
			freeXA="no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_FREEXA, freeXA);
		
		String allowSvTsInit="no";
		if (allowSvTsInitButton.getSelection()){
			allowSvTsInit="yes";
		}else{
			allowSvTsInit="no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ALLOWSVTSINIT, allowSvTsInit);
		
		String allRestartFiles="no";
		if (allRestartFilesButton.getSelection()){
			allRestartFiles="yes";
		}else{
			allRestartFiles="no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_ALLRESTARTFILES, allRestartFiles);
		
		String numberRestartFiles=restartFilesText.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_NUMBERRESTARTFILES, numberRestartFiles);
		
		String compileOnly="no";
		if (compButton.getSelection()){
			compileOnly="yes";
		}else{
			compileOnly="no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_COMPILEONLY, compileOnly);
		
		String dssEndOutput = "yes";
		if (dssEndOutputButton.getSelection()){
			dssEndOutput = "yes";
		}else{
			dssEndOutput = "no";
		}
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_DSSENDOUTPUT, dssEndOutput);

		
		String yearSectionOutput = yearSectionText.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_YEARSECTIONOUTPUT, yearSectionOutput);
			
		
		String monMemSection  = memSectionText.getText();
		configuration.setAttribute(DebugCorePlugin.ATTR_WPP_MONMEMSECTION, monMemSection);

		boolean isBVHecLib6Sel=bvHecLib6.getSelection();
		if (isBVHecLib6Sel){
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_VHECLIB, "6");
		}else{
			configuration.setAttribute(DebugCorePlugin.ATTR_WPP_VHECLIB, "7");
		}
	}

	@Override
	public String getName() {
		return "Configuration";
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);
		return true;
	}
}
