package wrimsv2_plugin.debugger.dialog;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.Workbench;

import wrimsv2_plugin.debugger.core.CBCSetting;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.core.SettingPref;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DataProcess;

public class WPPOptionDialog extends Dialog {
	
	protected Text textMemory;
	protected Combo solverCombo;
	protected Combo logCombo;
	protected Button buttonCycleDss;
	private Button buttonAllCycles;
	private Button buttonSelectedCycles;
	private Button buttonRTMessage;
	private Button buttonPrintGWFuncCalls;
	private Button buttonTrackMemoryUsage;
	private Text textSelectedCycles;
	private Text txtcbcTolerancePrimal;
	private Text txtcbcTolerancePrimalRelax;
	private Text txtcbcToleranceWarmPrimal;
	private Text txtcbcToleranceInteger;
	private Text txtcbcToleranceIntegerCheck;
	private Text txtcbcToleranceZero;
	private Text txtDevPass;
	private Text txtcbcHintRelaxPenalty;
	private Text txtcbcHintTimeMax;
	private double pvcbcTolerancePrimal;
	private double pvcbcTolerancePrimalRelax;
	private double pvcbcToleranceWarmPrimal;
	private double pvcbcToleranceInteger;
	private double pvcbcToleranceIntegerCheck;
	private double pvcbcToleranceZero;
	private double pvcbcHintRelaxPenalty;
	private double pvcbcHintTimeMax;
	private double vcbcTolerancePrimal;
	private double vcbcTolerancePrimalRelax;
	private double vcbcToleranceWarmPrimal;
	private double vcbcToleranceInteger;
	private double vcbcToleranceIntegerCheck;
	private double vcbcToleranceZero;
	private double vcbcHintRelaxPenalty;
	private double vcbcHintTimeMax;
	private int prevSel;
	private ConfigTab configTab;

	public WPPOptionDialog(Shell parent) {
		super(parent, SWT.MIN|SWT.RESIZE);
		setText("Option");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(650, 500);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}

	protected void createContents(final Shell shell) {
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=15;
		shell.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan=2;
		
		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);

	    TabItem generalTab = new TabItem(tabFolder, SWT.BORDER);
	    generalTab.setText("General");
	    createGeneralTab(tabFolder, generalTab);
	    
	    TabItem cycleTab = new TabItem(tabFolder, SWT.BORDER);
	    cycleTab.setText("Cycles");
	    createCycleTab(tabFolder, cycleTab);
	    
	    TabItem cbcTab = new TabItem(tabFolder, SWT.BORDER);
	    cbcTab.setText("CBC");
	    createCBCTab(tabFolder, cbcTab);
	  
	    TabItem ifsTab = new TabItem(tabFolder, SWT.BORDER);
	    ifsTab.setText("Infeasibility Analysis");
	    createInfeasibleTab(tabFolder, ifsTab);
	    
	    configTab = new ConfigTab(tabFolder, SWT.BORDER);
	    configTab.setText("Configurations");
	    
	    tabFolder.setSize(600, 350);
	    tabFolder.setLayoutData(gridData);
		
		gridData=new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan=1;
	    
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int iAssignCBCSetting=assignCBCSetting();
				if (iAssignCBCSetting==0){
					showDevPassFail();
					return;
				}
				assignIfsSetting();
				SettingPref.saveCBCSetting();
				DebugCorePlugin.xmx=textMemory.getText();
				DebugCorePlugin.solver=solverCombo.getText();
				DebugCorePlugin.log=logCombo.getText();
				DebugCorePlugin.outputCycleToDss=buttonCycleDss.getSelection();
				DebugCorePlugin.outputAllCycles=buttonAllCycles.getSelection();
				DebugCorePlugin.outputCycles="\'"+textSelectedCycles.getText()+"\'";
				SettingPref.save();
				DebugCorePlugin.showRunTimeMessage=buttonRTMessage.getSelection();
				DebugCorePlugin.printGWFuncCalls=buttonPrintGWFuncCalls.getSelection();
				DebugCorePlugin.trackMemoryUsage=buttonTrackMemoryUsage.getSelection();
				if (DebugCorePlugin.isDebugging){
					try {
						DebugCorePlugin.target.sendRequest("solveroption:"+DebugCorePlugin.solver+":"+DebugCorePlugin.log);
						checkCbcUsed();
						if (DebugCorePlugin.outputCycleToDss) {
							DebugCorePlugin.target.sendRequest("OutputCycleDataToDssOn");
						}else{
							DebugCorePlugin.target.sendRequest("OutputCycleDataToDssOff");
						}
						if (DebugCorePlugin.outputAllCycles) {
							DebugCorePlugin.target.sendRequest("OutputAllCyclesOn");
						}else{
							DebugCorePlugin.target.sendRequest("OutputAllCyclesOff");
						}
						DebugCorePlugin.target.sendRequest("SelectedCycleOutput:"+DebugCorePlugin.outputCycles.replace(" ", ""));
						DebugCorePlugin.target.sendRequest("ShowRunTimeMessage:"+DebugCorePlugin.showRunTimeMessage);
						DebugCorePlugin.target.sendRequest("PrintGWFuncCalls:"+DebugCorePlugin.printGWFuncCalls);
						DebugCorePlugin.target.sendRequest("TrackMemoryUsage:"+DebugCorePlugin.trackMemoryUsage);
						
						if (iAssignCBCSetting==1 || iAssignCBCSetting==3){
							if (vcbcTolerancePrimal!=pvcbcTolerancePrimal){
								String cbcSetting = "cbcTolerancePrimal:"+CBCSetting.cbcTolerancePrimal;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
							if (vcbcTolerancePrimalRelax!=pvcbcTolerancePrimalRelax){
								String cbcSetting = "cbcTolerancePrimalRelax:"+CBCSetting.cbcTolerancePrimalRelax;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
							if (vcbcToleranceWarmPrimal != pvcbcToleranceWarmPrimal){
								String cbcSetting = "cbcToleranceWarmPrimal:"+CBCSetting.cbcToleranceWarmPrimal;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
							if (vcbcToleranceInteger != pvcbcToleranceInteger){
								String cbcSetting = "cbcToleranceInteger:"+CBCSetting.cbcToleranceInteger;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
							if (vcbcToleranceIntegerCheck != pvcbcToleranceIntegerCheck){
								String cbcSetting = "cbcToleranceIntegerCheck:"+CBCSetting.cbcToleranceIntegerCheck;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
							if (vcbcToleranceZero != pvcbcToleranceZero){
								String cbcSetting = "cbcToleranceZero:"+CBCSetting.cbcToleranceZero;
								DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
							}
						}
						
						if (vcbcHintRelaxPenalty != pvcbcHintRelaxPenalty){
							String cbcSetting = "cbcHintRelaxPenalty:"+CBCSetting.cbcHintRelaxPenalty;
							DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
						}
						if (vcbcHintTimeMax != pvcbcHintTimeMax){
							String cbcSetting = "cbcHintTimeMax:"+DataProcess.doubleStringtoInt(CBCSetting.cbcHintTimeMax);
							DebugCorePlugin.target.sendRequest(cbcSetting.replace(" ", ""));
						}
					} catch (DebugException e) {
						WPPException.handleException(e);
					}
				}
				showSolverStatus();
				configTab.saveConfigPref();
				shell.close();
			}
		});
		ok.setLayoutData(gridData);
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		cancel.setLayoutData(gridData);
		
		shell.setDefaultButton(ok);
		shell.pack();
	}
	
	public void createGeneralTab(TabFolder tabFolder, TabItem generalTab){		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=15;
		composite.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan=1;
		
		Label label1 = new Label(composite, SWT.NONE);
		label1.setText("Solver:");
		
		solverCombo = new Combo(composite, SWT.BORDER);
		solverCombo.add("CBC");
		solverCombo.add("XA");
		//solverCombo.add("Gurobi");
		//solverCombo.add("CBC2.10");
		//solverCombo.add("CBC2.9.8");
		//solverCombo.add("LPSolve");
		
		Label label2 =  new Label(composite, SWT.NONE);
		label2.setText("Log:");
		
		logCombo = new Combo(composite, SWT.SINGLE|SWT.BORDER);
		logCombo.add("None");
		logCombo.add("Log");
		logCombo.add("xa_cbc");
		logCombo.add("cbc_xa");
		
		if (DebugCorePlugin.solver.equals("CBC")){
			solverCombo.select(0);
		}else if (DebugCorePlugin.solver.equals("XA")){
			solverCombo.select(1);
		}else if (DebugCorePlugin.solver.equals("Gurobi")){
			solverCombo.select(2);
		}
		prevSel=solverCombo.getSelectionIndex();
		solverCombo.setLayoutData(gridData);
		solverCombo.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				if (DebugCorePlugin.isDebugging){
					String solverName = solverCombo.getText();
					if (solverName.equalsIgnoreCase("CBC") && (DebugCorePlugin.cbc210Used || DebugCorePlugin.cbc298Used)){
						showNoSwitchWarning("CBC");
						solverCombo.select(prevSel);
					}else if (solverName.equalsIgnoreCase("CBC2.10") && (DebugCorePlugin.cbcUsed || DebugCorePlugin.cbc298Used)){
						showNoSwitchWarning("CBC2.10");
						solverCombo.select(prevSel);
					}else if (solverName.equalsIgnoreCase("CBC2.9.8") && (DebugCorePlugin.cbcUsed || DebugCorePlugin.cbc210Used)){
						showNoSwitchWarning("CBC2.9.8");
						solverCombo.select(prevSel);
					}else{
						prevSel=solverCombo.getSelectionIndex();
					}
				}
			}
			
		});
		
		if (DebugCorePlugin.target==null){
			solverCombo.setEnabled(true);
			logCombo.setEnabled(true);
		}else if (DebugCorePlugin.target.isTerminated()){
			solverCombo.setEnabled(true);
			logCombo.setEnabled(true);
		}else if (DebugCorePlugin.target.isSuspended()){
			solverCombo.setEnabled(true);
			logCombo.setEnabled(true);
		}else{
			solverCombo.setEnabled(false);
			logCombo.setEnabled(false);
		}
		
		if (DebugCorePlugin.log.equals("None")){
			logCombo.select(0);
		}else if (DebugCorePlugin.log.equals("Log")){
			logCombo.select(1);
		}else if (DebugCorePlugin.log.equals("xa_cbc")){
			logCombo.select(2);
		}else if (DebugCorePlugin.log.equals("cbc_xa")){
			logCombo.select(3);
		}
		logCombo.setLayoutData(gridData);
		
		Label label3 =  new Label(composite, SWT.NONE);
		label3.setText("Allocated Memory (mb):");
		
		textMemory =  new Text(composite, SWT.BORDER);
		textMemory.setText(DebugCorePlugin.xmx);
		textMemory.setLayoutData(gridData);
		
		Label label4 =  new Label(composite, SWT.NONE);
		label4.setText("Output Cycle Data to DSS");
		
		buttonCycleDss =  new Button(composite, SWT.CHECK);
		buttonCycleDss.setSelection(DebugCorePlugin.outputCycleToDss);
		buttonCycleDss.setLayoutData(gridData);
		
		Label label5 =  new Label(composite, SWT.NONE);
		label5.setText("Show run time message to locate blowout and freeze");
		
		buttonRTMessage =  new Button(composite, SWT.CHECK);
		buttonRTMessage.setSelection(DebugCorePlugin.showRunTimeMessage);
		buttonRTMessage.setLayoutData(gridData);
		
		Label label6 =  new Label(composite, SWT.NONE);
		label6.setText("Print groundwater function call info");
		
		buttonPrintGWFuncCalls =  new Button(composite, SWT.CHECK);
		buttonPrintGWFuncCalls.setSelection(DebugCorePlugin.printGWFuncCalls);
		buttonPrintGWFuncCalls.setLayoutData(gridData);
		
		Label label7 =  new Label(composite, SWT.NONE);
		label7.setText("Track memory usage");
		
		buttonTrackMemoryUsage =  new Button(composite, SWT.CHECK);
		buttonTrackMemoryUsage.setSelection(DebugCorePlugin.trackMemoryUsage);
		buttonTrackMemoryUsage.setLayoutData(gridData);
		
		generalTab.setControl(composite);
	}
	
	public void createCycleTab(TabFolder tabFolder, TabItem cycleTab){		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=25;
		composite.setLayout(layout);
		
		GridData gridData=new GridData(GridData.BEGINNING);
		gridData.horizontalSpan=2;
		
		buttonAllCycles =  new Button(composite, SWT.RADIO);
		buttonAllCycles.setText("All cycles");
		buttonAllCycles.setSelection(DebugCorePlugin.outputAllCycles);
		buttonAllCycles.setLayoutData(gridData);
		
		buttonSelectedCycles =  new Button(composite, SWT.RADIO);
		buttonSelectedCycles.setText("Selected cycles");
		buttonSelectedCycles.setSelection(!DebugCorePlugin.outputAllCycles);
		buttonSelectedCycles.setLayoutData(gridData);
		
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan=2;
		
		textSelectedCycles = new Text (composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		textSelectedCycles.setText(DebugCorePlugin.outputCycles.replace("\'", ""));
		textSelectedCycles.setLayoutData(gridData1);
		
		Label labelSelectedCycles = new Label(composite, SWT.NONE);
		labelSelectedCycles.setText("*Selected cycles to output, e.g. 1, 2, 4, 6, 10");
		labelSelectedCycles.setLayoutData(gridData);
		
		cycleTab.setControl(composite);
	}
	
	public void createCBCTab(TabFolder tabFolder, TabItem cbcTab){		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=15;
		composite.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan=1;
		
		Label label1 = new Label(composite, SWT.NONE);
		label1.setLayoutData(gridData);
		label1.setText("Tolerance Primal:");
		txtcbcTolerancePrimal=new Text(composite, SWT.BORDER);
		txtcbcTolerancePrimal.setLayoutData(gridData);
		txtcbcTolerancePrimal.setText(CBCSetting.cbcTolerancePrimal);
		
		Label label2 = new Label(composite, SWT.NONE);
		label2.setLayoutData(gridData);
		label2.setText("Tolerance Primal Relax:");
		txtcbcTolerancePrimalRelax=new Text(composite, SWT.BORDER);
		txtcbcTolerancePrimalRelax.setLayoutData(gridData);
		txtcbcTolerancePrimalRelax.setText(CBCSetting.cbcTolerancePrimalRelax);
		
		Label label3 = new Label(composite, SWT.NONE);
		label3.setLayoutData(gridData);
		label3.setText("Tolerance Warm Primal:");		
		txtcbcToleranceWarmPrimal=new Text(composite, SWT.BORDER);
		txtcbcToleranceWarmPrimal.setLayoutData(gridData);
		txtcbcToleranceWarmPrimal.setText(CBCSetting.cbcToleranceWarmPrimal);
		
		Label label4 = new Label(composite, SWT.NONE);
		label4.setLayoutData(gridData);
		label4.setText("Tolerance Integer:");		
		txtcbcToleranceInteger=new Text(composite, SWT.BORDER);
		txtcbcToleranceInteger.setLayoutData(gridData);
		txtcbcToleranceInteger.setText(CBCSetting.cbcToleranceInteger);
		
		Label label5 = new Label(composite, SWT.NONE);
		label5.setLayoutData(gridData);
		label5.setText("Tolerance Integer Check:");		
		txtcbcToleranceIntegerCheck=new Text(composite, SWT.BORDER);
		txtcbcToleranceIntegerCheck.setLayoutData(gridData);
		txtcbcToleranceIntegerCheck.setText(CBCSetting.cbcToleranceIntegerCheck);
		
		Label label6 = new Label(composite, SWT.NONE);
		label6.setLayoutData(gridData);
		label6.setText("Tolerance Zero:");		
		txtcbcToleranceZero=new Text(composite, SWT.BORDER);
		txtcbcToleranceZero.setLayoutData(gridData);
		txtcbcToleranceZero.setText(CBCSetting.cbcToleranceZero);

		Label label7 = new Label(composite, SWT.NONE);
		label7.setLayoutData(gridData);
		label7.setText("Developer Pass Code:");		
		txtDevPass=new Text(composite, SWT.BORDER);
		txtDevPass.setLayoutData(gridData);
		txtDevPass.setText("");
		
		Button butDefault = new Button(composite, SWT.BORDER);
		butDefault.setLayoutData(gridData);
		butDefault.setText("Default");
		butDefault.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				txtcbcTolerancePrimal.setText(CBCSetting.cbcTolerancePrimalDefault);
				txtcbcTolerancePrimalRelax.setText(CBCSetting.cbcTolerancePrimalRelaxDefault);
				txtcbcToleranceWarmPrimal.setText(CBCSetting.cbcToleranceWarmPrimalDefault);
				txtcbcToleranceInteger.setText(CBCSetting.cbcToleranceIntegerDefault);
				txtcbcToleranceIntegerCheck.setText(CBCSetting.cbcToleranceIntegerCheckDefault);
				txtcbcToleranceZero.setText(CBCSetting.cbcToleranceZeroDefault);
			}
		});
		
		Label label8 = new Label(composite, SWT.NONE);
		label8.setLayoutData(gridData);
		label8.setText("");
		
		/*
		Label label9 = new Label(composite, SWT.NONE);
		GridData gridData2=new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan=2;
		label9.setLayoutData(gridData2);
		label9.setText("*When WRIMS 2 GUI restarts, CBC tolerances return to default values.");
		*/
		
		cbcTab.setControl(composite);
	}
	
	public void createInfeasibleTab(TabFolder tabFolder, TabItem ifsTab){		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout=new GridLayout(2, false);
		layout.marginWidth=20;
		layout.marginHeight=15;
		composite.setLayout(layout);
		
		GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan=1;
		
		Label label1 = new Label(composite, SWT.NONE);
		label1.setLayoutData(gridData);
		label1.setText("Infeasibility Hint Relax Penalty:");
		txtcbcHintRelaxPenalty=new Text(composite, SWT.BORDER);
		txtcbcHintRelaxPenalty.setLayoutData(gridData);
		txtcbcHintRelaxPenalty.setText(CBCSetting.cbcHintRelaxPenalty);
		
		Label label2 = new Label(composite, SWT.NONE);
		label2.setLayoutData(gridData);
		label2.setText("Max Time for Infeasibility Hint Finding:");
		txtcbcHintTimeMax=new Text(composite, SWT.BORDER);
		txtcbcHintTimeMax.setLayoutData(gridData);
		txtcbcHintTimeMax.setText(CBCSetting.cbcHintTimeMax);
	
		Button butDefault = new Button(composite, SWT.BORDER);
		butDefault.setLayoutData(gridData);
		butDefault.setText("Default");
		butDefault.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				txtcbcHintRelaxPenalty.setText(CBCSetting.cbcHintRelaxPenaltyDefault);
				txtcbcHintTimeMax.setText(CBCSetting.cbcHintTimeMaxDefault);
			}
		});
	
		Label label3 = new Label(composite, SWT.NONE);
		label3.setLayoutData(gridData);
		label3.setText("");
		
		Label label4 = new Label(composite, SWT.NONE|SWT.WRAP);
		gridData = new GridData(GridData.FILL_HORIZONTAL, SWT.TOP, true, false);
		gridData.horizontalSpan=2;
		label4.setLayoutData(gridData);
		label4.setText("Please go to \"Infeasibility\" tab of the Launch Configuration to edit selected WRESL files or constraints for infeasibility analysis. "+
		"You can edit the list before or during the debug or run mode there.");
		
		/*
		Button butEditIfs = new Button(composite, SWT.BORDER);
		butEditIfs.setLayoutData(gridData);
		butEditIfs.setText("Edit");
		butEditIfs.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				if (DebugCorePlugin.isRunning){
					
					if (DebugCorePlugin.ifsFilePath==null){
						showNoEditIfsFiles();
					}else{
						File f = new File(DebugCorePlugin.ifsFilePath);
						if (!f.exists()){
							try {
								f.createNewFile();
							} catch (IOException e) {
								WPPException.handleException(e);
							}
						}
						try {
							Desktop.getDesktop().open(f);
						} catch (IOException e) {
							WPPException.handleException(e);
						}
					}
					
					final IWorkbench workbench=PlatformUI.getWorkbench();
					workbench.getDisplay().asyncExec(new Runnable(){
						public void run(){
							try {
								workbench.getActiveWorkbenchWindow().getActivePage().showView(DebugCorePlugin.ID_WPP_INFEASIBILITY_VIEW);
							} catch (PartInitException e) {
								WPPException.handleException(e);
							}
						}
					});
				}else{
					showNoEditIfsFile();
				}
			}
		});
		*/
		
		ifsTab.setControl(composite);
	}
	
	public void showSolverStatus(){
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String log="";
				if (!DebugCorePlugin.log.equalsIgnoreCase("NONE")){
					log=DebugCorePlugin.log;
				}
				String status=DebugCorePlugin.solver+"  "+log;
				
				IWorkbenchPage page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
				try {
					IViewPart console = page.showView( IConsoleConstants.ID_CONSOLE_VIEW );
					if (console != null){
						console.getViewSite().getActionBars().getStatusLineManager().setMessage(status);
					}
				} catch (PartInitException e) {
					WPPException.handleException(e);
				} 
			}
		});
	}
	
	public int assignCBCSetting(){
		vcbcTolerancePrimal      = Double.parseDouble(txtcbcTolerancePrimal.getText());
		vcbcTolerancePrimalRelax = Double.parseDouble(txtcbcTolerancePrimalRelax.getText());
		vcbcToleranceWarmPrimal  = Double.parseDouble(txtcbcToleranceWarmPrimal.getText());
		vcbcToleranceInteger     = Double.parseDouble(txtcbcToleranceInteger.getText());
		vcbcToleranceIntegerCheck= Double.parseDouble(txtcbcToleranceIntegerCheck.getText());
		vcbcToleranceZero        = Double.parseDouble(txtcbcToleranceZero.getText());
		
		pvcbcTolerancePrimal      = Double.parseDouble(CBCSetting.cbcTolerancePrimal);
		pvcbcTolerancePrimalRelax = Double.parseDouble(CBCSetting.cbcTolerancePrimalRelax);
		pvcbcToleranceWarmPrimal  = Double.parseDouble(CBCSetting.cbcToleranceWarmPrimal);
		pvcbcToleranceInteger     = Double.parseDouble(CBCSetting.cbcToleranceInteger);
		pvcbcToleranceIntegerCheck= Double.parseDouble(CBCSetting.cbcToleranceIntegerCheck);
		pvcbcToleranceZero        = Double.parseDouble(CBCSetting.cbcToleranceZero);
		
		if (vcbcTolerancePrimal==CBCSetting.dvcbcTolerancePrimal &&
			vcbcTolerancePrimalRelax==CBCSetting.dvcbcTolerancePrimalRelax &&	
			vcbcToleranceWarmPrimal == CBCSetting.dvcbcToleranceWarmPrimal &&
			vcbcToleranceInteger == CBCSetting.dvcbcToleranceInteger &&
			vcbcToleranceIntegerCheck == CBCSetting.dvcbcToleranceIntegerCheck &&
			vcbcToleranceZero == CBCSetting.dvcbcToleranceZero){
			
			CBCSetting.cbcTolerancePrimal=CBCSetting.cbcTolerancePrimalDefault;
			CBCSetting.cbcTolerancePrimalRelax=CBCSetting.cbcTolerancePrimalRelaxDefault;
			CBCSetting.cbcToleranceWarmPrimal=CBCSetting.cbcToleranceWarmPrimalDefault;
			CBCSetting.cbcToleranceInteger=CBCSetting.cbcToleranceIntegerDefault;
			CBCSetting.cbcToleranceIntegerCheck=CBCSetting.cbcToleranceIntegerCheckDefault;
			CBCSetting.cbcToleranceZero=CBCSetting.cbcToleranceZeroDefault;
			
			CBCSetting.changeSetting=false;
			return 1;
		}
		
		if (vcbcTolerancePrimal==pvcbcTolerancePrimal &&
			vcbcTolerancePrimalRelax==pvcbcTolerancePrimalRelax &&	
			vcbcToleranceWarmPrimal == pvcbcToleranceWarmPrimal &&
			vcbcToleranceInteger == pvcbcToleranceInteger &&
			vcbcToleranceIntegerCheck == pvcbcToleranceIntegerCheck &&
			vcbcToleranceZero == pvcbcToleranceZero){
			CBCSetting.changeSetting=true;
			return 2;
		}
		
		if (txtDevPass.getText().equals(DebugCorePlugin.devPass)){
			CBCSetting.cbcTolerancePrimal      = txtcbcTolerancePrimal.getText();
			CBCSetting.cbcTolerancePrimalRelax = txtcbcTolerancePrimalRelax.getText();
			CBCSetting.cbcToleranceWarmPrimal  = txtcbcToleranceWarmPrimal.getText();
			CBCSetting.cbcToleranceInteger     = txtcbcToleranceInteger.getText();
			CBCSetting.cbcToleranceIntegerCheck=  txtcbcToleranceIntegerCheck.getText();
			CBCSetting.cbcToleranceZero        = txtcbcToleranceZero.getText();
			CBCSetting.changeSetting=true;
			return 3;
		}else{
			return 0;
		}
	}
	
	public void assignIfsSetting(){
		pvcbcHintRelaxPenalty      = Double.parseDouble(CBCSetting.cbcHintRelaxPenalty);
		pvcbcHintTimeMax = Double.parseDouble(CBCSetting.cbcHintTimeMax);
		
		CBCSetting.cbcHintRelaxPenalty = txtcbcHintRelaxPenalty.getText();
		CBCSetting.cbcHintTimeMax = txtcbcHintTimeMax.getText();
		
		vcbcHintRelaxPenalty      = Double.parseDouble(CBCSetting.cbcHintRelaxPenalty);
		vcbcHintTimeMax = Double.parseDouble(CBCSetting.cbcHintTimeMax);
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
	
	public void showDevPassFail(){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Warning");
				messageBox.setMessage("Developer Pass Code is wrong! Please set the CBC tolenrances to the default values.");
				messageBox.open();
			}
		});
	}
	
	public void showNoSwitchWarning(final String solverName){
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				String solverNameVer;
				if (solverName.equalsIgnoreCase("CBC")){
					solverNameVer="CBC";
				}else{
					solverNameVer=solverName;
				}
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Warning");
				messageBox.setMessage("During debugging, you can only switch solvers between CBC and XA but not between different versions of CBC."
						+" You have used "+solverNameVer+". You can switch between different versions of CBC when your run is terminated.");
				messageBox.open();
			}
		});
	}
	
	public void checkCbcUsed(){
		if (DebugCorePlugin.solver.equalsIgnoreCase("CBC")){
			DebugCorePlugin.cbcUsed=true;
			DebugCorePlugin.cbc210Used=false;
			DebugCorePlugin.cbc298Used=false;
		}else if (DebugCorePlugin.solver.equalsIgnoreCase("CBC2.10")){
			DebugCorePlugin.cbcUsed=false;
			DebugCorePlugin.cbc210Used=true;
			DebugCorePlugin.cbc298Used=false;
		}else if (DebugCorePlugin.solver.equalsIgnoreCase("CBC2.9.8")){
			DebugCorePlugin.cbcUsed=false;
			DebugCorePlugin.cbc210Used=false;
			DebugCorePlugin.cbc298Used=true;
		}
	}
}
