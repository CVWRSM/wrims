package wrimsv2_plugin.debugger.msr;

import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDss;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class MSRDataTransfer {
	
	private String[] dvFNs=new String[8];
	private String[] svFNs=new String[8];
	private String[] initFNs=new String[8];
	
	private Map<String, String>[] dvToInitPN=new HashMap[8];
	private Map<String, String>[] dvToSvPN=new HashMap[8];
	
	private String timestep="1MON";
	
	public void procDataTxfrFile (ILaunchConfiguration configuration, int ms, boolean isSensitivity, int sri){
		int size=ms-1;
		dvFNs=new String[size];
		svFNs=new String[size];
		initFNs=new String[size];
		dvToInitPN=new HashMap[size];
		dvToSvPN=new HashMap[size];
		
		for (int sid=2; sid<=ms; sid++){
			String suffix="_MS"+sid;
			String suffixPre="";
			if (sid>2){
				int j=sid-1;
				suffixPre="_MS"+j;
			}
			
			try {
				String dataTxfrFN = null;
				dataTxfrFN = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DATATRANSFER+suffix, (String)null);				
				if (!new File(dataTxfrFN).isAbsolute()){
					dataTxfrFN=FileProcess.procRelativePath(dataTxfrFN, configuration);
				}
				procDataTxfrPN(dataTxfrFN, configuration, sid);
				
				String dvFN = null;
				dvFN = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE+suffixPre, (String)null);				
				if (!new File(dvFN).isAbsolute()){
					dvFN=FileProcess.procRelativePath(dvFN, configuration);
				}
				if (isSensitivity) dvFN=FileProcess.createSensitivityFilePath(dvFN, sri);
				
				String initFN = null;
				initFN = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE+suffix, (String)null);				
				if (!new File(initFN).isAbsolute()){
					initFN=FileProcess.procRelativePath(initFN, configuration);
				}
				
				String svFN = null;
				svFN = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVARFILE+suffix, (String)null);				
				if (!new File(svFN).isAbsolute()){
					svFN=FileProcess.procRelativePath(svFN, configuration);
				}
				
				int index=sid-2;
				dvFNs[index]=dvFN;
				svFNs[index]=svFN;
				initFNs[index]=initFN;
				
			} catch (CoreException e) {
				WPPException.handleException(e);
			}
		}
		
	}
	
	public void procDataTxfrPN(String dataTxfrFN, ILaunchConfiguration configuration, int sid){
		
		Map<String, String> dvToInitPNMap=new HashMap<String, String>();
		dvToInitPN[sid-2]=dvToInitPNMap;
		Map<String, String> dvToSvPNMap=new HashMap<String, String>();
		dvToSvPN[sid-2]=dvToSvPNMap;
		
		File dataTxfrFile=new File(dataTxfrFN);
		if (dataTxfrFile.exists()){
			String suffix="_MS"+sid;
			String suffixPre="";
			if (sid>2){
				int j=sid-1;
				suffixPre="_MS"+j;
			}
		
			try {
				String timeStep = null;
				timeStep = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_TIMESTEP+suffix, (String)null);
				
				String timeStepPre = null;
				timeStepPre = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_TIMESTEP+suffixPre, (String)null);
				
				String aPart = null;
				aPart = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_APART+suffix, (String)null);
				
				String aPartPre = null;
				aPartPre = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_APART+suffixPre, (String)null);
				
				String fPart = null;
				fPart = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVFPART+suffix, (String)null);
				
				String fPartPre = null;
				fPartPre = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_SVFPART+suffixPre, (String)null);
								
				FileReader fr = new FileReader(dataTxfrFile);
				BufferedReader br = new BufferedReader(fr);
				boolean isDvToSv=false;
				String line = br.readLine();
				if (line != null && line.startsWith("*")){
					while ((line = br.readLine()) != null && !line.startsWith("*")) {
						line=line.trim().toUpperCase();
						String[] pnParts=line.split("/");
						String[] bPartParts = pnParts[0].split(",");
						String bPartPre=bPartParts[0];
						String bPart=bPartParts[1];
						int length=pnParts.length;
						if (length==3){
							String pnPre="/"+aPartPre+"/"+bPartPre+"/"+pnParts[1]+"//"+pnParts[2]+"/"+fPartPre+"/";
							String pn="/"+aPart+"/"+bPart+"/"+pnParts[1]+"//"+pnParts[2]+"/"+fPart+"/";
							dvToInitPNMap.put(pnPre, pn);
						}else if (length==2){
							String pnPre="/"+aPartPre+"/"+bPartPre+"/"+pnParts[1]+"//"+timeStepPre+"/"+fPartPre+"/";
							String pn="/"+aPart+"/"+bPart+"/"+pnParts[1]+"//"+timeStep+"/"+fPart+"/";
							dvToInitPNMap.put(pnPre, pn);
						}
					}
					while ((line = br.readLine()) != null && !line.startsWith("*")) {
						line=line.trim().toUpperCase();
						String[] pnParts=line.split("/");
						String[] bPartParts = pnParts[0].split(",");
						String bPartPre=bPartParts[0];
						String bPart=bPartParts[1];
						int length=pnParts.length;
						if (length==3){
							String pnPre="/"+aPartPre+"/"+bPartPre+"/"+pnParts[1]+"//"+pnParts[2]+"/"+fPartPre+"/";
							String pn="/"+aPart+"/"+bPart+"/"+pnParts[1]+"//"+pnParts[2]+"/"+fPart+"/";
							dvToSvPNMap.put(pnPre, pn);
						}else if (length==2){
							String pnPre="/"+aPartPre+"/"+bPartPre+"/"+pnParts[1]+"//"+timeStepPre+"/"+fPartPre+"/";
							String pn="/"+aPart+"/"+bPart+"/"+pnParts[1]+"//"+timeStep+"/"+fPart+"/";
							dvToSvPNMap.put(pnPre, pn);
						}
					}
				}
				fr.close();
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
	}
	
	public void dataTxfr(int sid){
		DssOperations.waitForDSSOp();
		
		int i=sid-1;
		HecDss dvDss, svDss, initDss;
		try {
			dvDss=HecDss.open(dvFNs[i]);
			svDss=HecDss.open(svFNs[i]);
			initDss=HecDss.open(initFNs[i]);
		} catch (Exception e) {
			WPPException.handleException(e);
			DebugCorePlugin.isDssInOp=false;
			return;
		}
		
		Vector v = dvDss.getCatalogedPathnames();
		
		Map<String, String> dvToInitPNMap=dvToInitPN[i];
		Set<String> keys = dvToInitPNMap.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String dvPN=it.next();
			String initPN = dvToInitPNMap.get(dvPN);
			String dvPNFull=DssOperations.matchPathName(v, new DSSPathname(dvPN));
			if (dvPNFull !=null){
				String initPNFull=addPathD(dvPNFull, initPN);
				try {
					String startTime=TimeOperation.createStartTime(DebugCorePlugin.msStartYear, DebugCorePlugin.msStartMonth, DebugCorePlugin.msStartDay, timestep);
					String endTime=TimeOperation.createEndTime(DebugCorePlugin.msEndYear, DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndDay, timestep);
					TimeSeriesContainer tsc = (TimeSeriesContainer)dvDss.get(dvPNFull, startTime, endTime);
					TimeSeriesMath tsm = new TimeSeriesMath(tsc);
					tsm.setPathname(initPNFull);
					initDss.write(tsm);
				} catch (Exception e) {
					WPPException.handleException(e);
				}
			}
		}
		
		Map<String, String> dvToSvPNMap=dvToSvPN[i];
		keys = dvToSvPNMap.keySet();
		it = keys.iterator();
		while(it.hasNext()){
			String dvPN=it.next();
			String svPN = dvToSvPNMap.get(dvPN);
			String dvPNFull=DssOperations.matchPathName(v, new DSSPathname(dvPN));
			if (dvPNFull !=null){
				String svPNFull=addPathD(dvPNFull, svPN);
				try {
					String startTime=TimeOperation.createStartTime(DebugCorePlugin.msStartYear, DebugCorePlugin.msStartMonth, DebugCorePlugin.msStartDay, timestep);
					String endTime=TimeOperation.createEndTime(DebugCorePlugin.msEndYear, DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndDay, timestep);
					TimeSeriesContainer tsc = (TimeSeriesContainer)dvDss.get(dvPNFull, startTime, endTime);
					TimeSeriesMath tsm = new TimeSeriesMath(tsc);
					tsm.setPathname(svPNFull);
					svDss.write(tsm);
				} catch (Exception e) {
					WPPException.handleException(e);
				}
			}
		}
		
		dvDss.close();
		svDss.close();
		initDss.close();
		
		DebugCorePlugin.isDssInOp=false;
	}
	
	public String addPathD(String dvPNFull, String pn){
		String[] parts1 = dvPNFull.split("/");
		String[] parts2 = pn.split("/");
		timestep=parts1[5];
		
		return "/"+parts2[1]+"/"+parts2[2]+"/"+parts2[3]+"/"+parts1[4]+"/"+parts2[5]+"/"+parts2[6]+"/";
	}
}
