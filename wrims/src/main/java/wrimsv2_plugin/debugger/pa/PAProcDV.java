package wrimsv2_plugin.debugger.pa;

import hec.heclib.dss.HecDss;
import hec.hecmath.HecMath;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcDV {

	private String dvFile="";
	
	public PAProcDV(ILaunchConfiguration configuration){
		try {
			dvFile = configuration.getAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE, (String)null);
		} catch (CoreException e) {
			WPPException.handleException(e);
		}
	}
	
	public void deleteDVFile(ILaunchConfiguration configuration){
		try {
			File dvDSSFile=new File(dvFile);
			if (dvFile !=null){
				if (!dvDSSFile.isAbsolute()){
					dvFile=FileProcess.procRelativePath(dvFile, configuration);
					dvDSSFile=new File(dvFile);
				}
				if (dvDSSFile.exists() && dvDSSFile !=null){
					dvDSSFile.delete();
				}
			}
		} catch (Exception e) {
			WPPException.handleException(e);
		}
	}
	
	public void resetDVStartDate(){
		
		if (!DebugCorePlugin.resetOutputStart) return;
		
		DssOperations.waitForDSSOp();
		
		HecDss dvDss;
		try {
			dvDss=HecDss.open(dvFile);
		} catch (Exception e) {
			WPPException.handleException(e);
			DebugCorePlugin.isDssInOp=false;
			return;
		}
		String shiftInDay=TimeOperation.diffInDay(DebugCorePlugin.paStartYear, DebugCorePlugin.paStartMonth, DebugCorePlugin.paStartDay, DebugCorePlugin.paDVStartYear, DebugCorePlugin.paDVStartMonth, DebugCorePlugin.paDVStartDay)+"DAYS";
		String startTime=TimeOperation.createStartTime(DebugCorePlugin.paStartYear, DebugCorePlugin.paStartMonth, DebugCorePlugin.paStartDay, "1DAY");
		String endTime=TimeOperation.createEndTime(DebugCorePlugin.paEndYear, DebugCorePlugin.paEndMonth, DebugCorePlugin.paEndDay, "1DAY");
		dvDss.setTimeWindow(startTime, endTime);
		Vector<String> pathList = dvDss.getPathnameList();
		Collections.sort(pathList, Collections.reverseOrder());
		for (int i=0; i<pathList.size(); i++){
			String path = pathList.get(i);
			String[] parts = path.split("/");
			if (parts[parts.length-1].equals(DebugCorePlugin.svFPart)){
				try {
					TimeSeriesContainer dc = (TimeSeriesContainer)dvDss.get(path);
					TimeSeriesMath tm = new TimeSeriesMath(dc);
					TimeSeriesMath newTm = (TimeSeriesMath) tm.shiftInTime(shiftInDay);
					String newPath=regeneratePath(tm.getPath());
					newTm.setPathname(newPath);
					dvDss.write(newTm);
				} catch (Exception e) {
				}
			}
		}
		dvDss.close();
		
		DebugCorePlugin.isDssInOp=false;
	}
	
	public String regeneratePath(String path){
		String[] parts = path.split("/");
		int size = parts.length;
		parts[size-1]=parts[size-1]+"_"+DebugCorePlugin.paStartYear+TimeOperation.getMonthText(DebugCorePlugin.paStartMonth)+DebugCorePlugin.paStartDay;
		String newPath="";
		for (int i=0; i<size; i++){
			newPath=newPath+parts[i]+"/";
		}
		return newPath;
	}
}
