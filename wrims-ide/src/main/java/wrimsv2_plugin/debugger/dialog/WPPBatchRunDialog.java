package wrimsv2_plugin.debugger.dialog;

import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.hecmath.HecMath;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import vista.db.dss.DSSUtil;
import wrimsv2_plugin.batchrun.BatchRunProcess;
import wrimsv2_plugin.batchrun.LaunchConfigInfo;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;


public class WPPBatchRunDialog extends Dialog {
	private Text fileText;
	private	String fileName="";
	private	String lfgName="";
	private String dvDssName="";
	private Button addButton;
	private Button deleteButton;
	private Button startAllButton;
	private Button stopAllButton;
	private Button stopButton;
	private Button seqButton;
	private Button wsidiButton;
	private Button openButton;
	private Button saveButton;
	private Button combineButton;
	private ProgressBar combineDvPB;
	private List brl;
	
	private boolean isSequential=false;
	private boolean isWsidi=false;
	private boolean isDvCombining=false;
	
	private ArrayList<String> launchPathList=new ArrayList<String>();
	private ArrayList<String> launchNameList=new ArrayList<String>();
	private Map<String, LaunchConfigInfo> configMap = new HashMap<String, LaunchConfigInfo>();
	private Map<String, BatchRunProcess> brpMap = new HashMap<String, BatchRunProcess>();
	private Map<String, Boolean> brpIsToRunMap = new HashMap<String, Boolean>();
	private String dvNamesLine="";
	private String lookupNamesLine="";
	private String engineNamesLine="";
	private String launchNamesLine="";
	private String offsetsLine="";
	private String[] dvDssList;
	private int dssCombineEndYear=1921;
	private int dssCombineEndMonth=10;
	private int dssCombineEndDay=31;
	private ArrayList<String> dvPathList=new ArrayList<String>();
	private Map<String, TimeSeriesContainer> dcMap=new HashMap<String, TimeSeriesContainer>();
	private Map<String, Integer> startTimeMap=new HashMap<String, Integer>();
	
	public WPPBatchRunDialog(Shell parentShell) {
		super(parentShell, SWT.MIN|SWT.RESIZE);
		setText("Batch Run");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setLocation(450, 300);
		shell.pack();
		shell.open();
	}

	protected void createContents(final Shell shell) {
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 8;
		gl.makeColumnsEqualWidth = true;
		gl.marginWidth=10;
		gl.marginHeight=15;
		shell.setLayout(gl);
		
		brl=new List(shell, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		GridData gd0 = new GridData(GridData.FILL_HORIZONTAL);
		gd0.horizontalSpan = 8;
		gd0.heightHint=brl.getItemHeight()*14;
		brl.setLayoutData(gd0);
		
		fileText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 5;
		fileText.setLayoutData(gd1);
		fileText.setText(fileName);
		
		Button browseButton = new Button(shell, SWT.PUSH);
		browseButton.setText("Browse");
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 1;
		browseButton.setLayoutData(gd2);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.OPEN);
						dlg.setFilterNames(new String[]{"Launch File (*.launch)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.launch", "*.*"});
						dlg.setFileName(fileText.getText());
						String file=dlg.open();
						if (file !=null){
							fileText.setText(file);
						}
					}
				});
			}
		});
		
		addButton = new Button(shell, SWT.PUSH);
		addButton.setText("Add");
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 1;
		addButton.setLayoutData(gd3);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						String lfp = fileText.getText().toLowerCase();
						if (!lfp.equals("") && !launchPathList.contains(lfp)){
							LaunchConfigInfo config = new LaunchConfigInfo(lfp);
							BatchRunProcess brp=new BatchRunProcess();
							int index1=lfp.lastIndexOf("\\");
							int index2=lfp.lastIndexOf(".");
							String lfn=lfp.substring(index1+1, index2);
							launchPathList.add(lfp);
							launchNameList.add(lfn);
							configMap.put(lfp, config);
							brpMap.put(lfp, brp);
							brl.add(lfn);
						}
					}
				});
			}
		});
		
		deleteButton = new Button(shell, SWT.PUSH);
		deleteButton.setText("Delete");
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		gd4.horizontalSpan = 1;
		deleteButton.setLayoutData(gd4);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						int[] indices = brl.getSelectionIndices();
						for (int i=indices.length-1; i>=0; i--){
							String lfp=launchPathList.get(indices[i]);
							configMap.remove(lfp);
							brpMap.remove(lfp);
							launchNameList.remove(indices[i]);
							launchPathList.remove(indices[i]);
						}
						brl.remove(indices);	
					}
				});
			}
		});
		
		startAllButton = new Button(shell, SWT.PUSH);
		startAllButton.setText("Start All");
		GridData gd5 = new GridData(GridData.FILL_HORIZONTAL);
		gd5.horizontalSpan = 1;
		startAllButton.setLayoutData(gd5);
		startAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						if (!isWsidi){
							stopAllButton.setEnabled(true);
							stopButton.setEnabled(true);
							startAllButton.setEnabled(false);
							addButton.setEnabled(false);
							deleteButton.setEnabled(false);
							seqButton.setEnabled(false);
							openButton.setEnabled(false);
						}
						startAllBatchRun();
					}
				});
			}
		});
		
		stopAllButton = new Button(shell, SWT.PUSH);
		stopAllButton.setText("Stop All");
		GridData gd6 = new GridData(GridData.FILL_HORIZONTAL);
		gd6.horizontalSpan = 1;
		stopAllButton.setLayoutData(gd6);
		stopAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						stopAllBatchRun();
						stopAllButton.setEnabled(false);
						stopButton.setEnabled(false);
						startAllButton.setEnabled(true);
						addButton.setEnabled(true);
						deleteButton.setEnabled(true);
						seqButton.setEnabled(true);
						openButton.setEnabled(true);
					}
				});
			}
		});
		stopAllButton.setEnabled(false);
		
		stopButton = new Button(shell, SWT.PUSH);
		stopButton.setText("Stop");
		GridData gd7 = new GridData(GridData.FILL_HORIZONTAL);
		gd7.horizontalSpan = 1;
		stopButton.setLayoutData(gd7);
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						stopBatchRuns();
					}
				});
			}
		});
		stopButton.setEnabled(false);
		
		seqButton = new Button(shell, SWT.CHECK);
		seqButton.setText("Sequential");
		GridData gd8 = new GridData(GridData.FILL_HORIZONTAL);
		gd8.horizontalSpan = 1;
		seqButton.setLayoutData(gd8);
		seqButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						isSequential=!isSequential;
					}
				});
			}
		});

		wsidiButton = new Button(shell,SWT.CHECK);
		wsidiButton.setText("WsiDi");
		GridData gd9 = new GridData(GridData.FILL_HORIZONTAL);
		gd9.horizontalSpan = 1;
		wsidiButton.setLayoutData(gd9);
		wsidiButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						isWsidi=!isWsidi;
					}
				});
			}
		});
		
		openButton = new Button(shell, SWT.PUSH);
		openButton.setText("Open");
		GridData gd10 = new GridData(GridData.FILL_HORIZONTAL);
		gd10.horizontalSpan = 1;
		openButton.setLayoutData(gd10);
		openButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.OPEN);
						dlg.setFilterNames(new String[]{"Launch File Group (*.lfg)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.lfg", "*.*"});
						dlg.setFileName(lfgName);
						String file=dlg.open();
						if (file !=null){
							stopAllBatchRun();
							stopAllButton.setEnabled(false);
							stopButton.setEnabled(false);
							startAllButton.setEnabled(true);
							addButton.setEnabled(true);
							deleteButton.setEnabled(true);
							seqButton.setEnabled(true);
							openButton.setEnabled(true);
														
							lfgName=file;
							brl.removeAll();
							launchPathList=new ArrayList<String>();
				    		launchNameList=new ArrayList<String>();
				    		configMap=new HashMap<String, LaunchConfigInfo>();
				    		brpMap=new HashMap<String, BatchRunProcess>();
							procBatchRunFileNames(lfgName);
						}
					}
				});
			}
		});
		
		saveButton = new Button(shell, SWT.PUSH);
		saveButton.setText("Save");
		GridData gd11 = new GridData(GridData.FILL_HORIZONTAL);
		gd11.horizontalSpan = 1;
		saveButton.setLayoutData(gd11);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						FileDialog dlg=new FileDialog(shell, SWT.SAVE);
						dlg.setFilterNames(new String[]{"Launch File Group (*.lfg)", "All Files (*.*)"});
						dlg.setFilterExtensions(new String[]{"*.lfg", "*.*"});
						dlg.setFileName(lfgName);
						String file=dlg.open();
						if (file !=null){
							lfgName=file;
							saveLfgFile(lfgName);
						}
					}
				});
			}
		});
		
		isDvCombining=false;
		combineButton = new Button(shell, SWT.PUSH);
		combineButton.setText("Combine DV DSS");
		GridData gd12 = new GridData(GridData.FILL_HORIZONTAL);
		gd12.horizontalSpan = 1;
		combineButton.setLayoutData(gd12);
		combineButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){

					public void run(){
						isDvCombining=!isDvCombining;
						if (isDvCombining){
							Shell shell=workbench.getActiveWorkbenchWindow().getShell();
							FileDialog dlg=new FileDialog(shell, SWT.SAVE);
							dlg.setText("Combine DV Dss files from PA runs to");
							dlg.setFilterNames(new String[]{"DV Dss File (*.dss)", "All Files (*.*)"});
							dlg.setFilterExtensions(new String[]{"*.dss", "*.*"});
							dlg.setFileName(dvDssName);
							String file=dlg.open();
							if (file !=null){
								dvDssName=file;
								Thread thread=new Thread(){
									public void run(){
										procLaunchConfigs();
										procDvDssFiles();
										combineDvDss(dvDssName);
										final IWorkbench workbench=PlatformUI.getWorkbench();
										workbench.getDisplay().asyncExec(new Runnable(){
											public void run(){
												combineButton.setText("Combine DV DSS");
												isDvCombining=false;
												resetCombineDvPB();
											}
										});
									}
								};
								thread.start();
							}
							combineButton.setText("Stop Combining");
						}
					}
				});
			}
		});
		
		Label placeHolder=new Label(shell, SWT.NONE);
		gd1 = new GridData(GridData.BEGINNING);
		gd1.horizontalSpan =5;
		placeHolder.setLayoutData(gd1);
		placeHolder.setVisible(true);
		
		combineDvPB=new ProgressBar(shell, SWT.NONE);
		gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan =3;
		combineDvPB.setLayoutData(gd1);
		resetCombineDvPB();;
	}
	
	public void resetCombineDvPB(){
		combineDvPB.setMinimum(0);
		combineDvPB.setMaximum(10);
		combineDvPB.setSelection(0);
		combineDvPB.setVisible(true);
	}
	
	public void setCombineDvPB(final int index){
		Display.getDefault().asyncExec(new Runnable(){
		
			public void run(){
				combineDvPB.setSelection(index);
				combineDvPB.setVisible(true);
			}
		
		});
	}
	
	protected void startAllBatchRun(){
		
		if (isWsidi){
			if (isSequential){
				prepareWsiDiSequential();
				try {
					Runtime.getRuntime().exec("cmd /c start " + "WSIDIGenerator\\sequential_wsidi_generator.bat");
				} catch (IOException e) {
					WPPException.handleException(e);
				}
			}else{
				prepareWsiDiParallel();
				try {
					Runtime.getRuntime().exec("cmd /c start " + "WSIDIGenerator\\parallel_wsidi_generator.bat");
				} catch (IOException e) {
					WPPException.handleException(e);
				}
			}
		}else{
			emptyProgressFiles();
			clearBatchRunList();
		
			for (int i=0; i<launchPathList.size(); i++){
				String lfp=launchPathList.get(i);
				brpIsToRunMap.put(lfp,  true);							
			}
		
			checkProgress();
			generateSVCatalog();
			if (isSequential){
				sequentialRun();
			}else{
				parallelRun();
			}
		}
	}
	
	protected void sequentialRun(){
		
		Runnable runnable = new Runnable(){
			
			public void run(){
				try {
					for (int i=0; i<launchPathList.size(); i++){
						String lfp=launchPathList.get(i);
						if (configMap.containsKey(lfp) && brpMap.containsKey(lfp) && brpIsToRunMap.containsKey(lfp)){
							LaunchConfigInfo config = configMap.get(lfp);
							BatchRunProcess brp = brpMap.get(lfp);
							if (brpIsToRunMap.get(lfp)) {
								brpIsToRunMap.put(lfp, false);
								brp.launch(config, lfp);									
							}
						}
					}
				} catch (CoreException e) {
					WPPException.handleException(e);
				}
			}
		};
		
		new Thread(runnable).start();
	}
	
	protected void parallelRun(){
		for (int i=0; i<launchPathList.size(); i++){
			final String lfp=launchPathList.get(i);
			if (configMap.containsKey(lfp) && brpMap.containsKey(lfp) && brpIsToRunMap.containsKey(lfp)){
				final LaunchConfigInfo config = configMap.get(lfp);
				final BatchRunProcess brp = brpMap.get(lfp);
				
				Runnable runnable = new Runnable(){
					
					public void run(){
						try {
							if (brpIsToRunMap.get(lfp)) {
								brpIsToRunMap.put(lfp, false);
								brp.launch(config, lfp);
							}
						} catch (CoreException e) {
							WPPException.handleException(e);
						}
					}
				};
				
				new Thread(runnable).start();
			}
		}
	}
	
	protected void stopAllBatchRun(){
		for (int i=0; i<launchPathList.size(); i++){
			String lfp=launchPathList.get(i);
			if (brpMap.containsKey(lfp)){
				BatchRunProcess brp = brpMap.get(lfp);
				brp.terminate(lfp+".bat");									
			}
			brpIsToRunMap.put(lfp, false);
		}
	}
	
	protected void stopBatchRuns(){
		int[] indices = brl.getSelectionIndices();
		for (int i=0; i<indices.length; i++){
			String lfp=launchPathList.get(indices[i]);
			if (brpMap.containsKey(lfp)){
				BatchRunProcess brp = brpMap.get(lfp);
				brp.terminate(lfp+".bat");									
			}
			brpIsToRunMap.put(lfp, false);
		}
	}
	
	public void checkProgress(){
		
		Runnable runnable = new Runnable(){
			
			public void run(){
				
				boolean allComplete;
				
				do{
					allComplete=true;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						WPPException.handleException(e);
					}
					for (int i=0; i<launchPathList.size(); i++){
						String lfp=launchPathList.get(i);
						if (brpIsToRunMap.get(lfp)){
							allComplete=false;
						}else{
							BatchRunProcess brp = brpMap.get(lfp);
							if (brp.isRunning){
								allComplete=false;
							}
							File pf = new File(lfp+".config.prgss");
							try {
								BufferedReader br = new BufferedReader(new FileReader(pf));
								String line = br.readLine();
								if (line==null){
								}else if (line.startsWith("Run to ")){
									int index=line.indexOf("/");
									String sYear = line.substring(7, index);
									int percent = Math.round(100.0f*(Integer.parseInt(sYear)-brp.startYear)/(brp.endYear-brp.startYear));
									final String item=launchNameList.get(i)+" "+line+" "+percent + "% Done.";
									final int pi=i;
									IWorkbench workbench=PlatformUI.getWorkbench();
									workbench.getDisplay().asyncExec(new Runnable(){
										public void run(){
											brl.setItem(pi, item);
										}
									});
								}else{
									final String item=launchNameList.get(i)+" "+line;
									final int pi=i;
									IWorkbench workbench=PlatformUI.getWorkbench();
									workbench.getDisplay().asyncExec(new Runnable(){
										public void run(){
											brl.setItem(pi, item);
										}
									});
								}
							} catch (FileNotFoundException e) {
								WPPException.handleException(e);
							} catch (IOException e) {
								WPPException.handleException(e);
							}
						}
					}
				}while (!allComplete);
				
				IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						stopAllButton.setEnabled(false);
						stopButton.setEnabled(false);
						startAllButton.setEnabled(true);
						addButton.setEnabled(true);
						deleteButton.setEnabled(true);
						seqButton.setEnabled(true);
						DebugCorePlugin.isDssInOp=false;
					}
				});
			}
		};
		
		new Thread(runnable).start();
		
	}
	
	public void emptyProgressFiles(){
		for (int i=0; i<launchPathList.size(); i++){
			FileWriter progressFile;
			try {
				progressFile = new FileWriter((launchPathList.get(i)+".config.prgss"));
				PrintWriter pw = new PrintWriter(progressFile);
				pw.println();
				pw.close();
			} catch (IOException e) {
				WPPException.handleException(e);
			}
		}
	}
	
	public void clearBatchRunList(){
		IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				for (int i=0; i<launchNameList.size(); i++){
					String lfn=launchNameList.get(i);
					brl.setItem(i, lfn);
				}
			}
		});
	}
	
	public void prepareWsiDiParallel(){
		setupWsidiBrp();
		
		String wsidiMainTemplate = ".\\WSIDIGenerator\\ParallelMain_template.py";
		String wsidiMainFile = ".\\WSIDIGenerator\\ParallelMain.py";
		try {
	         FileInputStream fs= new FileInputStream(wsidiMainTemplate);
	         BufferedReader br = new BufferedReader(new InputStreamReader(fs));
	         FileWriter writer = new FileWriter(wsidiMainFile);
	         String line;
	         int count =0;
	         while((line = br.readLine())!=null){
	              count++;
	              if(count==29){
	            	  writer.write(dvNamesLine);
	              }else if (count==30){
	            	  writer.write(lookupNamesLine);
	              }else if (count==31){
	            	  writer.write(engineNamesLine);
	              }else if (count==32){
	            	  writer.write(launchNamesLine);
	              }else if (count==33){
	            	  writer.write(offsetsLine);
	              }else{
	                  writer.append(line+"\n");
	              }
	         }
	         writer.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
		
		for (int i=0; i<launchPathList.size(); i++){
			String lfp=launchPathList.get(i);
			if (configMap.containsKey(lfp) && brpMap.containsKey(lfp)){
				LaunchConfigInfo config = configMap.get(lfp);
				BatchRunProcess brp = brpMap.get(lfp);
				brp.getStartEndDate(config);
				brp.createBatch(config, lfp, true);
			}
		}
	}
	
	public void prepareWsiDiSequential(){
		setupWsidiBrp();
		
		String wsidiMainTemplate = ".\\WSIDIGenerator\\SequentialMain_template.py";
		String wsidiMainFile = ".\\WSIDIGenerator\\SequentialMain.py";
		try {
	         FileInputStream fs= new FileInputStream(wsidiMainTemplate);
	         BufferedReader br = new BufferedReader(new InputStreamReader(fs));
	         FileWriter writer = new FileWriter(wsidiMainFile);
	         String line;
	         int count =0;
	         while((line = br.readLine())!=null){
	              count++;
	              if(count==29){
	            	  writer.write(dvNamesLine);
	              }else if (count==30){
	            	  writer.write(lookupNamesLine);
	              }else if (count==31){
	            	  writer.write(engineNamesLine);
	              }else if (count==32){
	            	  writer.write(launchNamesLine);
	              }else if (count==33){
	            	  writer.write(offsetsLine);
	              }else{
	                  writer.append(line+"\n");
	              }
	         }
	         writer.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
		
		for (int i=0; i<launchPathList.size(); i++){
			String lfp=launchPathList.get(i);
			if (configMap.containsKey(lfp) && brpMap.containsKey(lfp)){
				LaunchConfigInfo config = configMap.get(lfp);
				BatchRunProcess brp = brpMap.get(lfp);
				brp.getStartEndDate(config);
				brp.createBatch(config, lfp, true);
			}
		}
	}
	
	public void setupWsidiBrp(){
		dvNamesLine =   "        studyDvNames=[";
		lookupNamesLine="        lookupNames=[";
		engineNamesLine="        engineNames=[";
		launchNamesLine="        launchNames=[";
		offsetsLine =   "        offsets=[";
		
		for (int i=0; i<launchPathList.size(); i++){
			String lfp=launchPathList.get(i);
			if (configMap.containsKey(lfp) && brpMap.containsKey(lfp)){
				LaunchConfigInfo config = configMap.get(lfp);
				BatchRunProcess brp = brpMap.get(lfp);
				brp.createBatch(config, lfp, true);
				if (i==0){
					dvNamesLine=dvNamesLine+"r\""+brp.dvFileFullPath+"\"";
					lookupNamesLine=lookupNamesLine+"r\""+brp.lookupFullPath+"\"";
					engineNamesLine=engineNamesLine+"r\""+brp.engineFileFullPath+"\"";
					launchNamesLine=launchNamesLine+"r\""+lfp+"\""; 
					offsetsLine=offsetsLine+brp.wsidiOffset;
				}else{
					dvNamesLine=dvNamesLine+",r\""+brp.dvFileFullPath+"\"";
					lookupNamesLine=lookupNamesLine+",r\""+brp.lookupFullPath+"\"";
					engineNamesLine=engineNamesLine+",r\""+brp.engineFileFullPath+"\"";
					launchNamesLine=launchNamesLine+",r\""+lfp+"\"";
					offsetsLine=offsetsLine+","+brp.wsidiOffset;
				}
			}
		}
		dvNamesLine=dvNamesLine+"]\n";
		lookupNamesLine=lookupNamesLine+"]\n";
		engineNamesLine=engineNamesLine+"]\n";
		launchNamesLine=launchNamesLine+"]\n";
		offsetsLine=offsetsLine+"]\n";
	}
	
	public void procBatchRunFileNames(String fn){
		
		File file = new File(fn);
		FileInputStream fs;
		try {
			fs = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		    LineNumberReader reader = new LineNumberReader(br);
		    String lfp;
		    while((lfp = br.readLine())!=null){
		    	if (!lfp.equals("") && !launchPathList.contains(lfp)){
		    		LaunchConfigInfo config = new LaunchConfigInfo(lfp);
		    		BatchRunProcess brp=new BatchRunProcess();
		    		int index1=lfp.lastIndexOf("\\");
		    		int index2=lfp.lastIndexOf(".");
		    		String lfn=lfp.substring(index1+1, index2);
		    		launchPathList.add(lfp);
		    		launchNameList.add(lfn);
		    		configMap.put(lfp, config);
		    		brpMap.put(lfp, brp);
		    		brl.add(lfn);
		    	}
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void saveLfgFile(String fn){
		try {
			File file = new File(fn);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			for (int i=0; i<launchPathList.size(); i++){
				out.println(launchPathList.get(i));
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateSVCatalog(){
		for (int i=0; i<launchPathList.size(); i++){
			String lfp=launchPathList.get(i);
			if (configMap.containsKey(lfp) && brpMap.containsKey(lfp)){
				LaunchConfigInfo config = configMap.get(lfp);
				String svPath=config.getStringAttribute(DebugCorePlugin.ATTR_WPP_SVARFILE, (String)null);
				if (!new File(svPath).isAbsolute()){
					svPath=FileProcess.procRelativePath(svPath, lfp);
				}
				int ms=Integer.parseInt(config.getStringAttribute(DebugCorePlugin.ATTR_WPP_MULTISTUDY, "1"));
				String lt=config.getStringAttribute(DebugCorePlugin.ATTR_WPP_LAUNCHTYPE, "0");
				int launchType=Integer.parseInt(lt);
				if (ms==1 && launchType==1) DSSUtil.generateCatalog(svPath);
			}
		}
	}
	
	public void procLaunchConfigs(){
		final int size=launchPathList.size();
		Display.getDefault().asyncExec(new Runnable(){
			
			public void run(){
				combineDvPB.setMaximum(size+1);
				combineDvPB.setSelection(0);
				combineDvPB.setVisible(true);
			}
			
		});
		dvDssList=new String[size];
		if (size>0){
			for (int i=0; i<size; i++){
				String lfp=launchPathList.get(i);
				LaunchConfigInfo config = configMap.get(lfp);				
				String dvPath = config.getStringAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE, (String)null);
				if (!new File(dvPath).isAbsolute()){
					dvPath=FileProcess.procRelativePath(dvPath, lfp);
				}
				dvDssList[i] = dvPath;
			}
		}
	}
	
	public void procDvDssFiles(){
		dvPathList=new ArrayList<String>();
		dcMap=new HashMap<String, TimeSeriesContainer>();
		HecDss dvDss;
		int lSize = dvDssList.length;
		for (int j=0; j<lSize; j++){
			String dvPath=dvDssList[j];
			Vector paPathList=new Vector();
			try {
				dvDss=HecDss.open(dvPath);
				paPathList = dvDss.getCatalogedPathnames();
			} catch (Exception e) {
				WPPException.handleException(e);
				dvDss=null;
			}
				
			int size=0;
			if (paPathList!=null){
				size = paPathList.size();
			}
			
			for (int i=0; i<size; i++){
				try {
					String path = (String) paPathList.get(i);
					String[] parts=path.split("/");
					if (!dvPathList.contains(path)){
						dvPathList.add(path);
						TimeSeriesContainer dc = (TimeSeriesContainer)dvDss.get(path);
						TimeSeriesContainer dc1 = new TimeSeriesContainer();
						dc1.fullName=dc.fullName;
						dc1.interval=dc.interval;
						dc1.units=dc.units;
						dc1.type=dc.type;
						dc1.startTime=dc.startTime;
						dc1.numberValues=dc.numberValues;
						dc1.times=dc.times;
						dc1.values=dc.values;
						dcMap.put(path, dc1);				
					}else{
						TimeSeriesContainer dc = (TimeSeriesContainer)dvDss.get(path);	
						TimeSeriesContainer dc1=dcMap.get(path);
						int nv = dc.numberValues;
						int nv1 = dc1.numberValues;
						if(dc.startTime>=dc1.startTime && dc.times[nv-1]>=dc1.times[nv1-1]){
							long dc1StartTime=dc1.startTime*60l*1000l-2208988800000l;
							Date dateA=new Date(dc1StartTime);
							long dcStartTime=dc.startTime*60l*1000l-2208988800000l;
							Date dateB=new Date(dcStartTime);
							TimeSeriesContainer dc2 = new TimeSeriesContainer();
							dc2.fullName=dc1.fullName;
							dc2.interval=dc1.interval;
							dc2.units=dc1.units;
							dc2.type=dc1.type;
							dc2.startTime=dc1.startTime;
							int offset;
							if (parts[5].equalsIgnoreCase("1MON")){
								offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1MON")-1;							
							}else{
								offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1DAY")-1;
							}
							dc2.numberValues=nv+offset;
							dc2.times=new int[dc2.numberValues];
							dc2.values=new double[dc2.numberValues];
							for (int k=0; k<dc1.numberValues; k++){
								dc2.times[k]=dc1.times[0]+k*dc2.interval;
								dc2.values[k]=-901.0;
							}
							for (int k=0; k<nv1; k++){
								dc2.times[k]=dc1.times[k];
								dc2.values[k]=dc1.values[k];
							}
							for (int k=0; k<nv; k++){
								dc2.times[k+offset]=dc.times[k];
								dc2.values[k+offset]=dc.values[k];
							}
							dcMap.put(path, dc2);
						}else if (dc.startTime<=dc1.startTime && dc.times[nv-1]>=dc1.times[nv1-1]){
							dcMap.put(path, dc);
						}else if(dc.startTime>dc1.startTime && dc.times[nv-1]<dc1.times[nv1-1]){
							long dc1StartTime=dc1.startTime*60l*1000l-2208988800000l;
							Date dateA=new Date(dc1StartTime);
							long dcStartTime=dc.startTime*60l*1000l-2208988800000l;
							Date dateB=new Date(dcStartTime);
							if (parts[5].equalsIgnoreCase("1MON")){
								int offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1MON")-1;
								for (int k=0; k<nv; k++){
									dc1.times[offset+k]=dc.times[k];
									dc1.values[offset+k]=dc.values[k];
								}
							}else{
								int offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1DAY")-1;
								for (int k=0; k<nv; k++){
									dc1.times[offset+k]=dc.times[k];
									dc1.values[offset+k]=dc.values[k];
								}
							}
							dcMap.put(path, dc1);
						}else{
							long dc1StartTime=dc1.startTime*60l*1000l-2208988800000l;
							Date dateB=new Date(dc1StartTime);
							long dcStartTime=dc.startTime*60l*1000l-2208988800000l;
							Date dateA=new Date(dcStartTime);
							TimeSeriesContainer dc2 = new TimeSeriesContainer();
							dc2.fullName=dc.fullName;
							dc2.interval=dc.interval;
							dc2.units=dc.units;
							dc2.type=dc.type;
							dc2.startTime=dc.startTime;
							int offset;
							if (parts[5].equalsIgnoreCase("1MON")){
								offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1MON")-1;	
								
							}else{
								offset = TimeOperation.getNumberOfTimestep(dateA, dateB, "1DAY")-1;
							}
							dc2.numberValues=nv1+offset;
							dc2.times=new int[dc2.numberValues];
							dc2.values=new double[dc2.numberValues];
							for (int k=0; k<dc2.numberValues; k++){
								dc2.times[k]=dc.times[0]+k*dc2.interval;
								dc2.values[k]=-901.0;
							}
							for (int k=0; k<nv; k++){
								dc2.times[k]=dc.times[k];
								dc2.values[k]=dc.values[k];
							}
							for (int k=0; k<nv1; k++){
								dc2.times[k+offset]=dc1.times[k];
								dc2.values[k+offset]=dc1.values[k];
							}
							dcMap.put(path, dc2);
						}
					}		
					if (!isDvCombining) {
						dvDss.close();
						return;
					}
				}catch (Exception e){
					WPPException.handleException(e);
				}
				dvDss.close();
			}
			setCombineDvPB(j+1);
		}
	}
	
	public void combineDvDss(String fn){
		HecDss combinedDss;
		try {
			combinedDss=HecDss.open(fn);
		} catch (Exception e) {
			WPPException.handleException(e);
			return;
		}
		int size = dvPathList.size();
		for (int i=0; i<size; i++){
			String path = dvPathList.get(i);
			try {
				TimeSeriesContainer dc = dcMap.get(path);
				combinedDss.put(dc);
				if (!isDvCombining) {
					combinedDss.close();
					return;
				}
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		setCombineDvPB(size+1);
		combinedDss.close();
	}
}
