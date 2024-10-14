package wrimsv2_plugin.batchrun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.List;

import wrimsv2_plugin.debugger.core.SettingPref;
import wrimsv2_plugin.debugger.exception.WPPException;

public class WsiDiBatchRunCmd {

	private ArrayList<String> launchPathList=new ArrayList<String>();
	private ArrayList<String> launchNameList=new ArrayList<String>();
	private Map<String, LaunchConfigInfo> configMap = new HashMap<String, LaunchConfigInfo>();
	private Map<String, BatchRunProcess> brpMap = new HashMap<String, BatchRunProcess>();
	
	private boolean isSequential=true;
	private String fn="";
	
	private String dvNamesLine="";
	private String lookupNamesLine="";
	private String engineNamesLine="";
	private String launchNamesLine="";
	private String offsetsLine="";
	
	public WsiDiBatchRunCmd(String[] args){
		processArgs(args);
		SettingPref.load();
		procBatchRunFileNames();
		startAllBatchRun();
	}

	public void procBatchRunFileNames(){
		
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
		    	}
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void startAllBatchRun(){
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
	
	public void processArgs(String[] args){
		if (args.length==1){
			fn=args[0];
		}else{
			if(args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase(("-parallel"))) {
				isSequential=false;
			}
			fn=args[1];
		}
	}
	
	public static void main(String[] args){
		new WsiDiBatchRunCmd(args);
	}
}

