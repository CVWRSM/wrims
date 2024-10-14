package wrimsv2_plugin.debugger.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.CBCSetting;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;


public class ConfigTab extends TabItem {

	private static String configPrefFile="config.prf";
	private Table configTable;
	private ArrayList<String> configList=new ArrayList<String>();
	private Map<String, String[]> configMap=new HashMap<String, String[]>();
	private Combo valueCombo;
	private Combo configCombo;
	
	public ConfigTab(TabFolder parent, int style) {
		super(parent, style);
		
		prepComboData();
		
		createConfigTab(parent, this);
	}
	
	@Override
	protected void checkSubclass() {
	    //  allow subclass
	}
	
	public void createConfigTab(TabFolder tabFolder, TabItem configTab){
		
		Composite composite= new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout=new GridLayout(4, false);
		layout.marginWidth=20;
		layout.marginHeight=15;
		composite.setLayout(layout);
		
		GridData gridData=new GridData(GridData.BEGINNING);
		gridData.horizontalSpan=4;
		
		Label label1 = new Label(composite, SWT.NONE);
		label1.setLayoutData(gridData);
		label1.setText("Specify configurations:");
        
		/*
        List list = new List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        list.setLayoutData(gridData);

        list.add("config1: value1");
        */
		
		configTable = new Table(composite, SWT.BORDER|SWT.MULTI|SWT.FULL_SELECTION|SWT.V_SCROLL|SWT.H_SCROLL);

	    TableColumn tc1 = new TableColumn(configTable, SWT.BEGINNING);
	    TableColumn tc2 = new TableColumn(configTable, SWT.BEGINNING);
	    tc1.setText("Configuration");
	    tc2.setText("Value");
	    tc1.setWidth(250);
	    tc2.setWidth(250);
	    configTable.setHeaderVisible(true);
	    configTable.setLinesVisible(true);
	    
	    GridData gridData1=new GridData(GridData.FILL_BOTH);
		gridData1.horizontalSpan=4;
		gridData1.heightHint=200;
	    configTable.setLayoutData(gridData1);
	    configTable.setSize(500, 400);
	    
	    initTable();
		
		GridData gridData3=new GridData(GridData.FILL_HORIZONTAL);
		gridData3.horizontalSpan=2;
				
		configCombo=new Combo(composite, SWT.BORDER);
		configCombo.setLayoutData(gridData3);
		for (int i=0; i<configList.size(); i++){
			configCombo.add(configList.get(i));
		}
		configCombo.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				valueCombo.removeAll();
				String config=configCombo.getText();
				if (configMap.containsKey(config)){
					String[] values=configMap.get(config);
					for (int j=0; j<values.length; j++){
						valueCombo.add(values[j]);
					}
				}else{
					valueCombo.add("");
				}
				if (config.equalsIgnoreCase("watch")){
					String value="";
					for (int i=0; i<DebugCorePlugin.watchItems.size(); i++){
						String item=DebugCorePlugin.watchItems.get(i);
						if (i==0){
							value=value+item;
						}else{
							value=value+","+item;
						}
					}
					valueCombo.removeAll();
					valueCombo.add(value);
				}
				valueCombo.select(0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		configCombo.select(0);
		
		valueCombo=new Combo(composite, SWT.BORDER);
		valueCombo.setLayoutData(gridData3);
		String[] values=configMap.get(configCombo.getText());
		for (int i=0; i<values.length; i++){
			valueCombo.add(values[i]);
		}
		valueCombo.select(0);
		
		GridData gridData2=new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan=1;
		    
		Button add = new Button(composite, SWT.PUSH);
		add.setText("Add");
		add.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				String config = configCombo.getText(); 
				String value = valueCombo.getText();
				TableItem[] tis = configTable.getItems();
				int length = tis.length;
				if (length==0){
					TableItem ti = new TableItem(configTable, SWT.NONE);
				    ti.setText(new String[] {config, value});
				}else{
					for (int i=0; i<length; i++){
						TableItem ti = tis[i];
						String configTi = ti.getText(0);
						int comp = config.toLowerCase().compareTo(configTi.toLowerCase());
						if (comp==0){
							ti.setText(new String[]{config, value});
							return;
						}else if (comp<0){
							TableItem newTi = new TableItem(configTable, SWT.NONE, i);
							newTi.setText(new String[] {config, value});
							return;
						}
					}
					TableItem newTi = new TableItem(configTable, SWT.NONE);
				    newTi.setText(new String[] {config, value});
				}
			}
		});
		add.setLayoutData(gridData2);
		
		Button delete = new Button(composite, SWT.PUSH);
		delete.setText("Delete");
		delete.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				int[] indices=configTable.getSelectionIndices();
				configTable.remove(indices);
			}
		});
		delete.setLayoutData(gridData2);
			
		Button deleteAll = new Button(composite, SWT.PUSH);
		deleteAll.setText("Delete All");
		deleteAll.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
					configTable.removeAll();
				}
			});
		deleteAll.setLayoutData(gridData2);
			
		Button import1 = new Button(composite, SWT.PUSH);
		import1.setText("Import");
		import1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						WPPImportConfigFileDialog  dialog= new WPPImportConfigFileDialog(shell);
						dialog.openDialog(configTable, configList);
					}
				});
			}
		});
		import1.setLayoutData(gridData2);
			
	    composite.pack();
		configTab.setControl(composite);
	}
	
	public void initTable(){
		ArrayList<String> configs=new ArrayList<String>();
		Map<String, String> configMap=new HashMap<String, String>();
		
		try {
			File file = new File(DebugCorePlugin.dataDir, configPrefFile);
			if (!file.exists()){
				file.createNewFile();
				return;
			}
			FileInputStream fs = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		    LineNumberReader reader = new LineNumberReader(br);
		    String line = reader.readLine();
		    while (line !=null){
		    	String modLine = line.replace(" ", "");
		    	if (modLine.contains("\t")){
		    		String[] parts=modLine.split("\t");
		    		configs.add(parts[0]);
		    		configMap.put(parts[0], parts[1]);
		    	}
		    	line = reader.readLine();
		    }
		    Collections.sort(configs);
		    
		    for (int i=0; i<configs.size(); i++){
		    	TableItem ti = new TableItem(configTable, SWT.NONE);
		    	String config=configs.get(i);
			    ti.setText(new String[] {config, configMap.get(config)});
		    }
		} catch (Exception e) {
			WPPException.handleException(e);
		}
	}
	
	public void saveConfigPref(){
		try {
			File file = new File(DebugCorePlugin.dataDir, configPrefFile);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			TableItem[] tis = configTable.getItems();
			for (int i=0; i<tis.length; i++){
				TableItem ti = tis[i];
				out.println(ti.getText(0)+"\t"+ti.getText(1));
			}
			out.close();
			fw.close();
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public Table getTable(){
		return configTable;
	}
	
	public ArrayList<String> getConfigList(){
		return configList;
	}
	
	public static void writeConfigSetting(PrintWriter out){
		try {
			File file = new File(DebugCorePlugin.dataDir, configPrefFile);
			if (!file.exists()){
				file.createNewFile();
				return;
			}
			FileInputStream fs = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		    LineNumberReader reader = new LineNumberReader(br);
		    String line = reader.readLine();
		    while (line !=null){
		    	if (line.contains("\t")){
		    		String modLine = line.replace("\t", "           ");
		    		out.println(modLine);
		    	}
		    	line = reader.readLine();
		    }
		} catch (Exception e) {
			WPPException.handleException(e);
		}
	}
	
	public void prepComboData(){
		configList=new ArrayList<String>();
		configMap=new HashMap<String, String[]>();
		
		configList.add("CbcDebugDeviation");
		configMap.put("CbcDebugDeviation", new String[]{"false", "true"});
		
		configList.add("CbcDebugDeviationMin");
		configMap.put("CbcDebugDeviationMin", new String[]{"200"});
		
		configList.add("CbcDebugDeviationWeightMin");
		configMap.put("CbcDebugDeviationWeightMin", new String[]{"5E5"});
		
		configList.add("CbcDebugDeviationWeightMultiply");
		configMap.put("CbcDebugDeviationWeightMultiply", new String[]{"100"});
		
		configList.add("CbcDebugDeviationFindMissing");
		configMap.put("CbcDebugDeviationFindMissing", new String[]{"false", "true"});
		
		configList.add("cbcdebugobjdiff");
		configMap.put("cbcdebugobjdiff", new String[]{"false", "true"});
		
		configList.add("cbcobjlog");
		configMap.put("cbcobjlog", new String[]{"true", "false"});
		
		configList.add("cbclogstartdate");
		configMap.put("cbclogstartdate", new String[]{""});
		
		configList.add("cbclogstopdate");
		configMap.put("cbclogstopdate", new String[]{""});
		
		configList.add("watch");
		configMap.put("watch", new String[]{""});
		
		configList.add("NameSorting");
		configMap.put("NameSorting", new String[]{"false", "true"});
		
		configList.add("CbcWhsScaling");
		configMap.put("CbcWhsScaling", new String[]{"true", "false"});
		
		configList.add("CbcWhsSafe");
		configMap.put("CbcWhsSafe", new String[]{"false", "true"});
		
		Collections.sort(configList, String.CASE_INSENSITIVE_ORDER);
	}
}
