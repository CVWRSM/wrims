package wrimsv2_plugin.debugger.pa;

import hec.heclib.dss.HecDSSDataAttributes;
import hec.heclib.dss.HecDss;
import hec.hecmath.HecMath;
import hec.hecmath.TimeSeriesMath;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcInit {
	
	private String initFile="";
	
	public PAProcInit(ILaunchConfiguration configuration, int sid){
		String suffix="";
		if (sid>1) suffix="_MS"+sid;
		try {
			initFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_INITFILE+suffix, (String)null);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}
	
	public void createPAInit(ILaunchConfiguration configuration){
		File initDSSFile=new File(initFile);
		if (initFile !=null){
			if (!initDSSFile.isAbsolute()){
				initFile=FileProcess.procRelativePath(initFile, configuration);
				initDSSFile=new File(initFile);
			}
			DebugCorePlugin.paInitFile=createPAInitFileName(initFile);
			if (initDSSFile !=null){
				File newInitDSSFile = new File(DebugCorePlugin.paInitFile);
				try {
					FileUtils.copyFile(initDSSFile, newInitDSSFile);
				} catch (IOException e) {
					WPPException.handleException(e);
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
	
	public void deletePAInit(){
		if (DebugCorePlugin.deletePAInit){
			final File paInit=new File(DebugCorePlugin.paInitFile);
			paInit.delete();
		}
	}
	
	public void createInitData(PAProcRun procRun){
		DssOperations.waitForDSSOp();
		HecDss paInitDss;
		try {
			paInitDss=HecDss.open(DebugCorePlugin.paInitFile);
		} catch (Exception e) {
			WPPException.handleException(e);
			DebugCorePlugin.isDssInOp=false;
			return;
		}
		Vector<String> paPathList = paInitDss.getPathnameList();
		Collections.sort(paPathList, Collections.reverseOrder());
		int size = paPathList.size();
		/*
		int sYear=DebugCorePlugin.startYear;
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
		int dsy = DebugCorePlugin.paStartYear-(DebugCorePlugin.startYear-sYear);
		String startTime=TimeOperation.createStartTime(dsy, 1, 1, "1DAY");
		String endTime=TimeOperation.createEndTime1(DebugCorePlugin.paStartYear, DebugCorePlugin.paStartMonth, DebugCorePlugin.paStartDay, "1DAY");
		paInitDss.setTimeWindow(startTime, endTime);
		*/
		for (int i=0; i<size; i++){
			String path = paPathList.get(i);
			try {
				TimeSeriesContainer dc = (TimeSeriesContainer)paInitDss.get(path);
				TimeSeriesMath tm = new TimeSeriesMath(dc);
				//TimeSeriesMath tm1 = (TimeSeriesMath) tm.extractTimeSeriesDataForTimeSpecification("YEAR", dsy+"-"+DebugCorePlugin.paStartYear, true, 0, false);
				HecMath newTm = tm.shiftInTime(DebugCorePlugin.paStartInterval+"MON");
				paInitDss.write(newTm);
			} catch (Exception e) {
				WPPException.handleException(e);
			}
		}
		paInitDss.close();
		DebugCorePlugin.isDssInOp=false;
	}

}
