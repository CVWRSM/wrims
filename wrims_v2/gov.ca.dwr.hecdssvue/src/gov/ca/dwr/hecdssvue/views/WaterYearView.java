package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.ShowSelected;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
//import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.DssOperations;

public class WaterYearView extends ViewPart {

	private HashMap<Integer, ArrayList<Integer>> sacIndex= new HashMap<Integer, ArrayList<Integer>>();
	private HashMap<Integer, ArrayList<Integer>> sjrIndex= new HashMap<Integer, ArrayList<Integer>>();
	private HashMap<Integer, ArrayList<Integer>> shastaIndex= new HashMap<Integer, ArrayList<Integer>>();
	private HashMap<Integer, ArrayList<Integer>> featherIndex= new HashMap<Integer, ArrayList<Integer>>();
	
	private Button[][] index= new Button[4][5];
	
	private Button fromFirstDss;
	private Button defaultTable;
	
	private Composite area;
	
	public static final String ID = "gov.ca.dwr.hecdssvue.views.WaterYearView";
	
	private boolean fromDSS=true;
	
	@Override
	public void createPartControl(Composite parent) {
		initialWaterYearMap();
		DssPluginCore.initWYTDss=false;
		area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(40, true));
		
		Label sac=new Label(area, SWT.NONE);
		sac.setText("SacR Index (40-30-30)");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 10;//TODO
		sac.setLayoutData(gd);
		
		Label sjr=new Label(area, SWT.NONE);
		sjr.setText("SJR Index (60-20-20)");
		sjr.setLayoutData(gd);
		
		Label shasta=new Label(area, SWT.NONE);
		shasta.setText("Shasta Index");
		shasta.setLayoutData(gd);
		
		Label feather=new Label(area, SWT.NONE);
		feather.setText("Feather Index");
		feather.setLayoutData(gd);
		
		index[0][0] = new Button(area, SWT.CHECK);
		index[0][0].setText("Wet (W 1)");
		index[0][0].setLayoutData(gd);
		
		index[1][0] = new Button(area, SWT.CHECK);
		index[1][0].setText("Wet (W 1)");
		index[1][0].setLayoutData(gd);
		
		index[2][0] = new Button(area, SWT.CHECK);
		index[2][0].setText("Normal (1)");
		index[2][0].setLayoutData(gd);
		
		index[3][0] = new Button(area, SWT.CHECK);
		index[3][0].setText("0");
		index[3][0].setLayoutData(gd);
		
		index[0][1] = new Button(area, SWT.CHECK);
		index[0][1].setText("Above Normal (AN 2)");
		index[0][1].setLayoutData(gd);
		
		index[1][1] = new Button(area, SWT.CHECK);
		index[1][1].setText("Above Normal (AN 2)");
		index[1][1].setLayoutData(gd);
		
		index[2][1] = new Button(area, SWT.CHECK);
		index[2][1].setText("Below Normal (2)");
		index[2][1].setLayoutData(gd);
		
		index[3][1] = new Button(area, SWT.CHECK);
		index[3][1].setText("1");
		index[3][1].setLayoutData(gd);
		
		index[0][2] = new Button(area, SWT.CHECK);
		index[0][2].setText("Below Normal (BN 3)");
		index[0][2].setLayoutData(gd);
		
		index[1][2] = new Button(area, SWT.CHECK);
		index[1][2].setText("Below Normal (BN 3)");
		index[1][2].setLayoutData(gd);
		
		index[2][2] = new Button(area, SWT.CHECK);
		index[2][2].setText("Dry (3)");
		index[2][2].setLayoutData(gd);
		
		fromFirstDss=new Button(area, SWT.RADIO);
		fromFirstDss.setText("Upon 1st Selected DSS");
		fromFirstDss.setLayoutData(gd);
		fromFirstDss.setSelection(true);
		fromFirstDss.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				fromFirstDss.setSelection(true);
				defaultTable.setSelection(false);
				initialWaterYearMapFromDSS();
				DssPluginCore.initWYTDss=false;
				wateryearFilter();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		index[0][3] = new Button(area, SWT.CHECK);
		index[0][3].setText("Dry (D 4)");
		index[0][3].setLayoutData(gd);
		
		index[1][3] = new Button(area, SWT.CHECK);
		index[1][3].setText("Dry (D 4)");
		index[1][3].setLayoutData(gd);
		
		index[2][3] = new Button(area, SWT.CHECK);
		index[2][3].setText("Critial (4)");
		index[2][3].setLayoutData(gd);
		
		defaultTable=new Button(area, SWT.RADIO);
		defaultTable.setText("Upon Default Table");
		defaultTable.setLayoutData(gd);
		defaultTable.setSelection(false);
		defaultTable.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				defaultTable.setSelection(true);
				fromFirstDss.setSelection(false);
				initialWaterYearMapFromTable();
				wateryearFilter();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		index[0][4] = new Button(area, SWT.CHECK);
		index[0][4].setText("Critical (C 5)");
		index[0][4].setLayoutData(gd);
		
		index[1][4] = new Button(area, SWT.CHECK);
		index[1][4].setText("Critical (C 5)");
		index[1][4].setLayoutData(gd);
		
		Label blank1=new Label(area, SWT.NONE);
		blank1.setText("");
		blank1.setLayoutData(gd);
		
		Button clearAll=new Button(area, SWT.PUSH);
		clearAll.setText("Clear All");
		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 4;//TODO
		clearAll.setLayoutData(gd1);
		clearAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (int i=0; i<5; i++){
					index[0][i].setSelection(false);
				}
				for (int i=0; i<5; i++){
					index[1][i].setSelection(false);
				}
				for (int i=0; i<4; i++){
					index[2][i].setSelection(false);
				}
				for (int i=0; i<2; i++){
					index[3][i].setSelection(false);
				}
				DssPluginCore.isAllWaterYear=true;
			}
		});
		
		for (int i=0; i<5; i++){
			index[0][i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					wateryearFilter();
				}
			});
			
			index[1][i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					wateryearFilter();
				}
			});
		}
		
		for (int i=0; i<4; i++){
			index[2][i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					wateryearFilter();
				}
			});
		}
		
		for (int i=0; i<2; i++){
			index[3][i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					wateryearFilter();
				}
			});
		}
	}
	
	public void wateryearFilter(){
		boolean isFirstSet=true;
		DssPluginCore.isAllWaterYear=true;
		ArrayList<Integer> selectedWaterYear=new ArrayList<Integer>();
		for (int i=0; i<5; i++){
			if (index[0][i].getSelection()){
				if (sacIndex.size()>0 && sacIndex.containsKey(i+1)){
					DssPluginCore.isAllWaterYear=false;
					selectedWaterYear.addAll(sacIndex.get(i+1));
					isFirstSet=false;
				}
			}
		}
		ArrayList<Integer> selectedSJRWaterYear=new ArrayList<Integer>();
		for (int i=0; i<5; i++){
			if (index[1][i].getSelection()){
				if (sjrIndex.size()>0 && sjrIndex.containsKey(i+1)){
					DssPluginCore.isAllWaterYear=false;
					selectedSJRWaterYear.addAll(sjrIndex.get(i+1));
				}
			}
		}
		if (selectedSJRWaterYear.size()==0){
		}else if (isFirstSet){
			selectedWaterYear.addAll(selectedSJRWaterYear);
			isFirstSet=false;
		}else{
			selectedWaterYear.retainAll(selectedSJRWaterYear);
		}
		ArrayList<Integer> selectedShastaWaterYear=new ArrayList<Integer>();
		for (int i=0; i<4; i++){
			if (index[2][i].getSelection()){
				if (shastaIndex.size()>0 && shastaIndex.containsKey(i+1)){
					DssPluginCore.isAllWaterYear=false;
					selectedShastaWaterYear.addAll(shastaIndex.get(i+1));
				}
			}
		}
		if (selectedShastaWaterYear.size()==0){
		}else if (isFirstSet){
			selectedWaterYear.addAll(selectedShastaWaterYear);
			isFirstSet=false;
		}else{
			selectedWaterYear.retainAll(selectedShastaWaterYear);
		}
		ArrayList<Integer> selectedFeatherWaterYear=new ArrayList<Integer>();
		for (int i=0; i<2; i++){
			if (index[3][i].getSelection()){
				if (featherIndex.size()>0 && featherIndex.containsKey(i)){
					DssPluginCore.isAllWaterYear=false;
					selectedFeatherWaterYear.addAll(featherIndex.get(i));
				}
			}
		}
		if (selectedFeatherWaterYear.size()==0){
		}else if (isFirstSet){
			selectedWaterYear.addAll(selectedFeatherWaterYear);
			isFirstSet=false;
		}else{
			selectedWaterYear.retainAll(selectedFeatherWaterYear);
		}
		
		DssPluginCore.filterWaterYear=new HashSet<Integer>(selectedWaterYear);
		
		ShowSelected.showSelected();
	}
	
	public void initialWaterYearMap(){
		if (fromDSS){
			initialWaterYearMapFromDSS();
		}else{
			initialWaterYearMapFromTable();
		}
	}
	
	public void initialWaterYearMapFromTable(){
		sacIndex= new HashMap<Integer, ArrayList<Integer>>();
		sjrIndex= new HashMap<Integer, ArrayList<Integer>>();
		shastaIndex= new HashMap<Integer, ArrayList<Integer>>();
		featherIndex= new HashMap<Integer, ArrayList<Integer>>();
		int[][] lookups=readInLookups("wytypes.table");
		for (int i=0; i<lookups.length; i++){
			int wateryear=lookups[i][0];
			
			int saci=lookups[i][1];
			if (sacIndex.containsKey(saci)){
				sacIndex.get(saci).add(wateryear);
			}else{
				ArrayList<Integer> sacArray=new ArrayList<Integer>();
				sacArray.add(wateryear);
				sacIndex.put(saci, sacArray);
			}
			
			int sjri=lookups[i][2];
			if (sjrIndex.containsKey(sjri)){
				sjrIndex.get(sjri).add(wateryear);
			}else{
				ArrayList<Integer> sjrArray=new ArrayList<Integer>();
				sjrArray.add(wateryear);
				sjrIndex.put(sjri, sjrArray);
			}
			
			int shastai=lookups[i][3];
			if (shastaIndex.containsKey(shastai)){
				shastaIndex.get(shastai).add(wateryear);
			}else{
				ArrayList<Integer> shastaArray=new ArrayList<Integer>();
				shastaArray.add(wateryear);
				shastaIndex.put(shastai, shastaArray);
			}
			
			int featheri=lookups[i][5];
			if (featherIndex.containsKey(featheri)){
				featherIndex.get(featheri).add(wateryear);
			}else{
				ArrayList<Integer> featherArray=new ArrayList<Integer>();
				featherArray.add(wateryear);
				featherIndex.put(featheri, featherArray);
			}
		}
		
	}
	
	private int[][] readInLookups(String fn) {

		// Open input file
		int lookups[][]=new int[0][0];
		
		Scanner input;
		try {
			input = new Scanner(new FileReader(DebugCorePlugin.dataDir+"\\"+fn));
		} catch (FileNotFoundException e) {
			WPPException.handleException(e);
			return lookups;
		}

		Vector<String> allLookups = new Vector<String>();

		int lineCount = 0;
		input.nextLine(); // Skip first two line
		input.nextLine(); // Skip first two line
		while (input.hasNextLine()) {
			String line = input.nextLine();
			allLookups.add(line);
			lineCount++;
		}
		input.close();
		lookups = new int[lineCount][6];
		for (int i = 0; i < lineCount; i++) {
			String[] parts = allLookups.get(i).split("[\t]+");
			for (int j = 0; j < 6; j++) {
				if (parts[j].equals("null"))
					parts[j] = "";
				lookups[i][j] = Integer.parseInt(parts[j]);
			}
		}

		return lookups;
	}

	public void initialWaterYearMapFromDSS(){
		if (DssPluginCore.dssArray.size()==0) return;
		HecDss dss = DssPluginCore.dssArray.get(0);
		if (dss == null) return;
		Vector<CondensedReference> dvVector = dss.getCondensedCatalog();
		String sacn=DssOperations.matchPathName(dvVector, "WYT_SAC_", "WATERYEARTYPE", "1MON");
		if (sacn!=null){
			try {
				DataContainer dc = dss.get(sacn);
				sacIndex=procWaterYearTypeFromDSS(dc);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			sacn=DssOperations.matchPathName(dvVector, "WYT_SAC_DV", "INDEX", "1MON");
			if (sacn!=null){
				try {
					DataContainer dc = dss.get(sacn);
					sacIndex=procWaterYearTypeFromDSS(dc);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		String sjrn=DssOperations.matchPathName(dvVector, "WYT_SJR_", "WATERYEARTYPE", "1MON");
		if (sjrn!=null){
			try {
				DataContainer dc = dss.get(sjrn);
				sjrIndex=procWaterYearTypeFromDSS(dc);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			sjrn=DssOperations.matchPathName(dvVector, "WYT_SJR_DV", "INDEX", "1MON");
			if (sjrn!=null){
				try {
					DataContainer dc = dss.get(sjrn);
					sjrIndex=procWaterYearTypeFromDSS(dc);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		String shstan=DssOperations.matchPathName(dvVector, "WYT_SHASTA_CVP_", "WATERYEARTYPE", "1MON");
		if (shstan!=null){
			try {
				DataContainer dc = dss.get(shstan);
				shastaIndex=procWaterYearTypeFromDSS(dc);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			shstan=DssOperations.matchPathName(dvVector, "WYT_SHAS_DV", "INDEX", "1MON");
			if (shstan!=null){
				try {
					DataContainer dc = dss.get(shstan);
					shastaIndex=procWaterYearTypeFromDSS(dc);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		String feathern=DssOperations.matchPathName(dvVector, "WYT_FEATHER_", "WATERYEARTYPE", "1MON");
		if (feathern!=null){
			try {
				DataContainer dc = dss.get(feathern);
				featherIndex=procWaterYearTypeFromDSS(dc);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			feathern=DssOperations.matchPathName(dvVector, "WYT_FEATHER_DV", "INDEX", "1MON");
			if (feathern!=null){
				try {
					DataContainer dc = dss.get(feathern);
					featherIndex=procWaterYearTypeFromDSS(dc);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public HashMap<Integer, ArrayList<Integer>> procWaterYearTypeFromDSS(DataContainer dc){
		HashMap<Integer, ArrayList<Integer>>wyt= new HashMap<Integer, ArrayList<Integer>>();
		double[] values = ((TimeSeriesContainer)dc).values;
		int startTime = ((TimeSeriesContainer)dc).startTime;
		HecTime hecTime = new HecTime();
		hecTime.set(startTime);
		int month=hecTime.month();
		int year=hecTime.year();
		for (int i=0; i<values.length; i++){
			if (month==5){
				Long wytl=new Long(Math.round(values[i]));
				int wyti=wytl.intValue();
				if (wyt.containsKey(wyti)){
					wyt.get(wyti).add(year);
				}else{
					ArrayList<Integer> wytArray=new ArrayList<Integer>();
					wytArray.add(year);
					wyt.put(wyti, wytArray);
				}
			}
			month++;
			if (month>=13){
				year++;
				month=1;
			}
		}
		return wyt;
	}
	
	
	@Override
	public void setFocus() {
		area.setFocus();
		if (DssPluginCore.initWYTDss && fromDSS){
			initialWaterYearMapFromDSS();
			DssPluginCore.initWYTDss=false;
		}
	}

}
