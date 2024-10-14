package wrimsv2_plugin.debugger.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import wrimsv2_plugin.debugger.exception.WPPException;

public class SettingPref {
	private static String settingPrefFile="setting.prf";
	private static String cbcSettingPrefFile="CBCSetting.prf";
	private static String cbcSettingDefaultFile="CBCSettingDefault.prf";
	
	public static void load(){
		try {
			File file = new File(DebugCorePlugin.dataDir, settingPrefFile);
			if (!file.exists()){
				file.createNewFile();
				DebugCorePlugin.solver="CBC";
				DebugCorePlugin.xmx="4096";
				DebugCorePlugin.outputCycleToDss=false;
				DebugCorePlugin.outputAllCycles=true;
				DebugCorePlugin.outputCycles="\'\'";
				save();
				return;
			}
			FileInputStream fs = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		    LineNumberReader reader = new LineNumberReader(br);
		    DebugCorePlugin.solver=br.readLine();
		    DebugCorePlugin.xmx=br.readLine();
		    String strOutputCycleToDss = br.readLine();
		    if (strOutputCycleToDss.toLowerCase().equals("true")){
		    	DebugCorePlugin.outputCycleToDss=true;
		    }else{
		    	DebugCorePlugin.outputCycleToDss=false;
		    }
		    String strOutputAllCycles = br.readLine();
		    if (strOutputAllCycles.toLowerCase().equals("true")){
		    	DebugCorePlugin.outputAllCycles=true;
		    }else{
		    	DebugCorePlugin.outputAllCycles=false;
		    }
		    DebugCorePlugin.outputCycles=br.readLine();
		} catch (Exception e) {
			DebugCorePlugin.solver="CBC";
			DebugCorePlugin.xmx="4096";
			DebugCorePlugin.outputCycleToDss=false;
			DebugCorePlugin.outputAllCycles=true;
			DebugCorePlugin.outputCycles="\'\'";
			e.printStackTrace();
		}
		return;
	}
	
	public static void save(){
		try {
			File file = new File(DebugCorePlugin.dataDir, settingPrefFile);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			out.println(DebugCorePlugin.solver);
			out.println(DebugCorePlugin.xmx);
			out.println(DebugCorePlugin.outputCycleToDss);
			out.println(DebugCorePlugin.outputAllCycles);
			out.println(DebugCorePlugin.outputCycles);
			out.close();
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
	
	public static void loadCBCDefault(){
		try {
			File file = new File(DebugCorePlugin.dataDir, cbcSettingDefaultFile);
			if (file.exists()){
				FileInputStream fs = new FileInputStream(file.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			    String line = br.readLine();
			    while (line !=null){
			    	line.replace(" ", "");
			    	String[] parts = line.split(":");
			    	if (parts.length>=2){
			    		String pn=parts[0].trim();
			    		String pv=parts[1].trim();
			    		if (pn.equalsIgnoreCase("cbcTolerancePrimal")){
			    			CBCSetting.cbcTolerancePrimalDefault      = pv;
			    		}else if (pn.equalsIgnoreCase("cbcTolerancePrimalRelax")){			
			    			CBCSetting.cbcTolerancePrimalRelaxDefault = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceWarmPrimal")){
			    			CBCSetting.cbcToleranceWarmPrimalDefault  = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceInteger")){	
			    			CBCSetting.cbcToleranceIntegerDefault     = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceIntegerCheck")){
			    			CBCSetting.cbcToleranceIntegerCheckDefault= pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceZero")){
			    			CBCSetting.cbcToleranceZeroDefault        = pv;
			    		}else if (pn.equalsIgnoreCase("cbcHintRelaxPenalty")){
			    			CBCSetting.cbcHintRelaxPenaltyDefault     = pv;
			    		}else if (pn.equalsIgnoreCase("cbcHintTimeMax")){
			    			CBCSetting.cbcHintTimeMaxDefault          = pv;
			    		}
			    	}
			    	line=br.readLine();
			    }
				br.close();
				CBCSetting.dvcbcTolerancePrimal      = Double.parseDouble(CBCSetting.cbcTolerancePrimalDefault);
				CBCSetting.dvcbcTolerancePrimalRelax = Double.parseDouble(CBCSetting.cbcTolerancePrimalRelaxDefault);
				CBCSetting.dvcbcToleranceWarmPrimal  = Double.parseDouble(CBCSetting.cbcToleranceWarmPrimalDefault);
				CBCSetting.dvcbcToleranceInteger     = Double.parseDouble(CBCSetting.cbcToleranceIntegerDefault);
				CBCSetting.dvcbcToleranceIntegerCheck= Double.parseDouble(CBCSetting.cbcToleranceIntegerCheckDefault);
				CBCSetting.dvcbcToleranceZero        = Double.parseDouble(CBCSetting.cbcToleranceZeroDefault);
				CBCSetting.dvcbcHintRelaxPenalty     = Double.parseDouble(CBCSetting.cbcHintRelaxPenaltyDefault);
				CBCSetting.dvcbcHintTimeMax          = Double.parseDouble(CBCSetting.cbcHintTimeMaxDefault);
			}
		}catch(Exception e){		
		}
	}
	
	public static void loadCBCSetting(){
		try {
			File file = new File(DebugCorePlugin.dataDir, cbcSettingPrefFile);
			if (file.exists()){
				FileInputStream fs = new FileInputStream(file.getAbsolutePath());
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			    String line = br.readLine();
			    while (line !=null){
			    	line.replace(" ", "");
			    	String[] parts = line.split(":");
			    	if (parts.length>=2){
			    		String pn=parts[0].trim();
			    		String pv=parts[1].trim();
			    		if (pn.equalsIgnoreCase("cbcTolerancePrimal")){
			    			CBCSetting.cbcTolerancePrimal      = pv;
			    		}else if (pn.equalsIgnoreCase("cbcTolerancePrimalRelax")){			
			    			CBCSetting.cbcTolerancePrimalRelax = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceWarmPrimal")){
			    			CBCSetting.cbcToleranceWarmPrimal  = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceInteger")){	
			    			CBCSetting.cbcToleranceInteger     = pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceIntegerCheck")){
			    			CBCSetting.cbcToleranceIntegerCheck= pv;
			    		}else if (pn.equalsIgnoreCase("cbcToleranceZero")){
			    			CBCSetting.cbcToleranceZero        = pv;
			    		}else if (pn.equalsIgnoreCase("cbcHintRelaxPenalty")){
			    			CBCSetting.cbcHintRelaxPenalty     = pv;
			    		}else if (pn.equalsIgnoreCase("cbcHintTimeMax")){
			    			CBCSetting.cbcHintTimeMax          = pv;
			    		}
			    	}
			    	line=br.readLine();
			    }
				br.close();
			}
		}catch(Exception e){		
		}
		
		double vcbcTolerancePrimal = Double.parseDouble(CBCSetting.cbcTolerancePrimal);
		double vcbcTolerancePrimalRelax = Double.parseDouble(CBCSetting.cbcTolerancePrimalRelax);	
		double vcbcToleranceWarmPrimal=Double.parseDouble(CBCSetting.cbcToleranceWarmPrimal);
		double vcbcToleranceInteger=Double.parseDouble(CBCSetting.cbcToleranceInteger);
		double vcbcToleranceIntegerCheck=Double.parseDouble(CBCSetting.cbcToleranceIntegerCheck);
		double vcbcToleranceZero=Double.parseDouble(CBCSetting.cbcToleranceZero);
		
		if (vcbcTolerancePrimal==CBCSetting.dvcbcTolerancePrimal &&
			vcbcTolerancePrimalRelax==CBCSetting.dvcbcTolerancePrimalRelax &&	
			vcbcToleranceWarmPrimal == CBCSetting.dvcbcToleranceWarmPrimal &&
			vcbcToleranceInteger == CBCSetting.dvcbcToleranceInteger &&
			vcbcToleranceIntegerCheck == CBCSetting.dvcbcToleranceIntegerCheck &&
			vcbcToleranceZero == CBCSetting.dvcbcToleranceZero){
				CBCSetting.changeSetting=false;
		}else{
				CBCSetting.changeSetting=true;
		}
	}
	
	public static void saveCBCSetting(){
		try {
			File file = new File(DebugCorePlugin.dataDir, cbcSettingPrefFile);
			if (!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(fw);
			out.println("cbcTolerancePrimal: "+CBCSetting.cbcTolerancePrimal);
			out.println("cbcTolerancePrimalRelax: "+CBCSetting.cbcTolerancePrimalRelax);
			out.println("cbcToleranceWarmPrimal: "+CBCSetting.cbcToleranceWarmPrimal);
			out.println("cbcToleranceInteger: "+CBCSetting.cbcToleranceInteger);
			out.println("cbcToleranceIntegerCheck: "+CBCSetting.cbcToleranceIntegerCheck);
			out.println("cbcToleranceZero: "+CBCSetting.cbcToleranceZero);
			out.println("cbcHintRelaxPenalty: "+CBCSetting.cbcHintRelaxPenalty);
			out.println("cbcHintTimeMax: "+CBCSetting.cbcHintTimeMax);
			out.close();
			fw.close();
		} catch (IOException e) {
			WPPException.handleException(e);
		}
	}
}
