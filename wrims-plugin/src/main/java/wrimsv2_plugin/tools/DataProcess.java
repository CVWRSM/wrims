package wrimsv2_plugin.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.model.IValue;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPDebugTarget;
import wrimsv2_plugin.debugger.model.WPPValue;

public class DataProcess {
	public static ArrayList<String[]> generateVarDetailData(String data){
		ArrayList<String[]> varDetail = new ArrayList<String[]>();
		String[] dataStrings = data.split("#");
		for (int i=0; i<dataStrings.length; i++){
			String[] entry=dataStrings[i].split(":");
			varDetail.add(entry);
		}
		return varDetail;
	}
	
	public static IValue[] generateTree(String data){
		if (data.equals("")){
			return new WPPValue[0];
		}else{
			String[] dataStrings=data.split("#");
			int size=dataStrings.length;
		
			WPPValue[] values=new WPPValue[size];  
			for (int i=0; i<size; i++){
				String[] dataSubStrings=dataStrings[i].split(":",2);
				WPPValue value=new WPPValue(DebugCorePlugin.target,dataSubStrings[1], dataSubStrings[0]); 
				values[i]=value;
			}
			return values;
		}
	}
	
	public static IValue[] retrieveAllGoal(){
		String line="";
		
		ArrayList<String> keys=new ArrayList<String>();
		Map<String, String> allGoalMap = new HashMap<String, String>();
		String allGoalFileName="allgoals.dat";
		File dataFolder = new File(DebugCorePlugin.dataDir);
		dataFolder.mkdirs();
		File allGoalFile=new File(dataFolder, allGoalFileName);
		if (allGoalFile.exists()){
			try {
				FileReader fr = new FileReader(allGoalFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					String[] part=line.split(":");
					String gn=part[0];
					keys.add(gn);
					allGoalMap.put(gn, part[1]);
				}
				fr.close();
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		
		int size=allGoalMap.size();
		
		WPPValue[] values=new WPPValue[size];  
		for (int i=0; i<size; i++){
			String gn=keys.get(i);
			WPPValue value=new WPPValue(DebugCorePlugin.target,allGoalMap.get(gn),gn); 
			values[i]=value;
		}
		return values;
	}
	
	public static IValue[] retrieveAllVariableValue(){
		String line="";
		
		ArrayList<String> keys=new ArrayList<String>();
		Map<String, String> allVariableMap = new HashMap<String, String>();
		String allVariableFileName="allvariables.dat";
		File dataFolder = new File(DebugCorePlugin.dataDir);
		dataFolder.mkdirs();
		File allVariableFile=new File(dataFolder, allVariableFileName);
		if (allVariableFile.exists()){
			try {
				FileReader fr = new FileReader(allVariableFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					String[] part=line.split(":");
					String vn=part[0];
					keys.add(vn);
					allVariableMap.put(vn, part[1]);
				}
				fr.close();
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		
		int size=allVariableMap.size();
		
		WPPValue[] values=new WPPValue[size];  
		for (int i=0; i<size; i++){
			String vn=keys.get(i);
			WPPValue value=new WPPValue(DebugCorePlugin.target,allVariableMap.get(vn),vn); 
			values[i]=value;
		}
		return values;
	}
	
	public static Map<String, String> retrieveWeightedVariables(){
		String line="";
		
		Map<String, String> weightedVariableMap = new HashMap<String, String>();
		String weightFileName="allweights.dat";
		File dataFolder = new File(DebugCorePlugin.dataDir);
		dataFolder.mkdirs();
		File weightFile=new File(dataFolder, weightFileName);
		if (weightFile.exists()){
			try {
				FileReader fr = new FileReader(weightFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					String[] part=line.split(":");
					weightedVariableMap.put(part[0], part[1]);
				}
				fr.close();
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		return weightedVariableMap;
	}
	
	public static Map<String, VariableProperty> retrieveAllVariableProperty(){
		String line="";
		
		Map<String, VariableProperty> allVariableProperty=new HashMap<String, VariableProperty>();
		String partFileName="allparts.dat";
		File dataFolder = new File(DebugCorePlugin.dataDir);
		dataFolder.mkdirs();
		File partFile=new File(dataFolder, partFileName);
		if (partFile.exists()){
			try {
				FileReader fr = new FileReader(partFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					String[] part=line.split(":");
					VariableProperty vp = new VariableProperty(part[1], part[2]);
					allVariableProperty.put(part[0], vp);
				}
				fr.close();
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		return allVariableProperty;
	}
	
	public static ArrayList<String> generateArrayList(String data){
		if (data.equals("")) return new ArrayList<String>();
		String[] dataParts=data.split(":");
		return new ArrayList<String>(Arrays.asList(dataParts));  
	}
	
	public static Map<String, VariableProperty> generateVariableProperty(String data){
		Map<String, VariableProperty> vp=new HashMap<String, VariableProperty>();
		if (!data.equals("")){
			String[] dataStrings = data.split("#");
			for (int i=0; i<dataStrings.length; i++){
				String[] dataParts = dataStrings[i].split(":");
				VariableProperty property = new VariableProperty(dataParts[1], dataParts[2]);
				vp.put(dataParts[0], property);
			}
		}
		return vp;
	}
	
	public static void generateAltStudyData(){
		Map<String, String>[] studiesData = DebugCorePlugin.studiesData;
		int suspendedYear=DebugCorePlugin.suspendedYear;
		int suspendedMonth=DebugCorePlugin.suspendedMonth;
		int suspendedDay=DebugCorePlugin.suspendedDay;
		int suspendedCycle=DebugCorePlugin.suspendedCycle;
		String sm, sd, sc;
		if (suspendedMonth<10){
			sm="0"+suspendedMonth;
		}else{
			sm=""+suspendedMonth;
		}
		if (suspendedDay<10){
			sd="0"+suspendedDay;
		}else{
			sd=""+suspendedDay;
		}
		if (suspendedCycle<10){
			sc="0"+suspendedCycle;
		}else{
			sc=""+suspendedCycle;
		}
		String line="";
		for (int i=4; i<8; i++){
			int j=i-4;
			studiesData[j]=new HashMap<String, String>();
			if (DebugCorePlugin.selectedStudies[i]){
				String dvFileName=DebugCorePlugin.studyFolderNames[j]+"\\dvar\\"+suspendedYear+"_"+sm+"_"+"c"+sc+".dvar";
				File dvFile=new File(dvFileName);
				if (dvFile.exists()){
					try {
						FileReader fr = new FileReader(dvFile);
						BufferedReader br = new BufferedReader(fr);
						while ((line = br.readLine()) != null) {
							if (line.equals("/* Weighted Dvar    */") || line.equals("/* Unweighted Dvar    */") || line.equals("")){
							}else{
								String[] part=line.split(":");
								studiesData[j].put(part[0].trim(), part[1]);
							}
						}
					} catch (Exception e) {
						WPPException.handleException(e);
					}
				}
				String svFileName=DebugCorePlugin.studyFolderNames[j]+"\\svar\\"+suspendedYear+"_"+sm+"_"+"c"+sc+".svar";
				File svFile=new File(svFileName);
				if (svFile.exists()){
					try {
						FileReader fr = new FileReader(svFile);
						BufferedReader br = new BufferedReader(fr);
						while ((line = br.readLine()) != null) {
							String[] part=line.split(":");
							studiesData[j].put(part[0].trim(), part[1]);
						}
					} catch (Exception e) {
						WPPException.handleException(e);
					}
				}
			}
		}
	}
	
	public static void initialVariableValueAlt(){
		Map<String, String>[] variableValueAlt = DebugCorePlugin.variableAltValue;
		for (int i=0; i<8; i++){
			variableValueAlt[i]=new HashMap<String, String>();
		}
	}
	
	public static int doubleStringtoInt(String ds){
		Long l=Math.round(Double.parseDouble(ds));
		return	l.intValue();
	}
}
