package wrimsv2_plugin.debugger.pa;

import hec.heclib.dss.HecDss;
import hec.heclib.util.Heclib;
import hec.hecmath.HecMath;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import wrimsv2_plugin.batchrun.BatchRunProcess;
import wrimsv2_plugin.batchrun.LaunchConfigInfo;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcInitBR {
	
	private String initFile="";
	
	public PAProcInitBR(LaunchConfigInfo configuration, int sid){
		String suffix="";
		if (sid>1) suffix="_MS"+sid;
		initFile = configuration.getStringAttribute(DebugCorePlugin.ATTR_WPP_INITFILE+suffix, (String)null);
	}
	
	public void createPAInit(String launchFilePath, BatchRunProcess brp){
		File initDSSFile=new File(initFile);
		if (initFile !=null){
			if (!initDSSFile.isAbsolute()){
				initFile=FileProcess.procRelativePath(initFile, launchFilePath);
				initDSSFile=new File(initFile);
			}
			brp.paInitFile=createPAInitFileName(initFile);
			if (initDSSFile !=null){
				File newInitDSSFile = new File(brp.paInitFile);
				try {
					FileUtils.copyFile(initDSSFile, newInitDSSFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String createPAInitFileName(String initFile){
		File file = new File(initFile);
		String fn = file.getName();
		String fp = file.getParent();
		String prefix="\\pa_";
		File newFile=new File(fp+prefix+fn);
		while (newFile.exists()){
			prefix=prefix+"_";
			newFile=new File(fp+prefix+fn);
		}
		return fp+prefix+fn;
	}
	
	public void deletePAInit(BatchRunProcess brp){
		if (brp.deletePAInit){
			final File paInit=new File(brp.paInitFile);
			paInit.delete();
		}
	}
	
	public void createInitData(PAProcRunBR procRun, BatchRunProcess brp){
		DssOperations.waitForDSSOp();
		HecDss paInitDss;
		try {
			paInitDss=HecDss.open(brp.paInitFile);
		} catch (Exception e) {
			e.printStackTrace();
			DebugCorePlugin.isDssInOp=false;
			return;
		}
		Vector<String> paPathList = paInitDss.getPathnameList();
		Collections.sort(paPathList, Collections.reverseOrder());
		int size = paPathList.size();
		/*
		int sYear=brp.startYear;
		for (int i=0; i<size; i++){
			String path = paPathList.get(i);
			try {
				TimeSeriesContainer firstDc = (TimeSeriesContainer)paInitDss.get(path);
				long st=firstDc.startTime;
				long stms=st*60000;
				long l1970=1440l * 25568l * 60000l;
				long ststd=stms-l1970;
				int sYear1=new Date(ststd).getYear()+1900;
				if (sYear1<sYear){
					sYear=sYear1;
				}
			} catch (Exception e1) {
				WPPException.handleException(e1);
			}
		}
		int dsy = brp.paStartYear-(brp.startYear-sYear);
		String startTime=TimeOperation.createStartTime(dsy, 1, 1, "1DAY");
		String endTime=TimeOperation.createEndTime1(brp.paStartYear, brp.paStartMonth,brp.paStartDay, "1DAY");
		paInitDss.setTimeWindow(startTime, endTime);
		*/
		for (int i=0; i<size; i++){
			String path = paPathList.get(i);
			try {
				TimeSeriesContainer dc = (TimeSeriesContainer)paInitDss.get(path);
				TimeSeriesMath tm = new TimeSeriesMath(dc);
				//TimeSeriesMath tm1 = (TimeSeriesMath) tm.extractTimeSeriesDataForTimeSpecification("YEAR", dsy+"-"+brp.paStartYear, true, 0, false);
				HecMath newTm = tm.shiftInTime(brp.paStartInterval+"MON");
				paInitDss.write(newTm);
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		paInitDss.close();
		DebugCorePlugin.isDssInOp=false;
	}

}
