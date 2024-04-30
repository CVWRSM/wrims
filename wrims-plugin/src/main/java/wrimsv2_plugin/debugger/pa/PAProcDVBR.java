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

import wrimsv2_plugin.batchrun.BatchRunProcess;
import wrimsv2_plugin.batchrun.LaunchConfigInfo;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.FileProcess;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcDVBR {

	private String dvFile="";
	
	public PAProcDVBR(LaunchConfigInfo configuration){
		dvFile = configuration.getStringAttribute(DebugCorePlugin.ATTR_WPP_DVARFILE, (String)null);
	}
	
	public void deleteDVFile(String launchFilePath){
		try {
			File dvDSSFile=new File(dvFile);
			if (dvFile !=null){
				if (!dvDSSFile.isAbsolute()){
					dvFile=FileProcess.procRelativePath(dvFile, launchFilePath);
					dvDSSFile=new File(dvFile);
				}
				if (dvDSSFile.exists() && dvDSSFile !=null){
					dvDSSFile.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void resetDVStartDate(BatchRunProcess brp){
		
		if (!brp.resetOutputStart) return;

		DssOperations.waitForDSSOp();
		
		HecDss dvDss;
		try {
			dvDss=HecDss.open(dvFile);
		} catch (Exception e) {
			e.printStackTrace();
			DebugCorePlugin.isDssInOp=false;
			return;
		}
		String shiftInDay=TimeOperation.diffInDay(brp.paStartYear, brp.paStartMonth, brp.paStartDay, brp.paDVStartYear, brp.paDVStartMonth, brp.paDVStartDay)+"DAYS";
		String startTime=TimeOperation.createStartTime(brp.paStartYear, brp.paStartMonth, brp.paStartDay, "1DAY");
		String endTime=TimeOperation.createEndTime(brp.paEndYear, brp.paEndMonth, brp.paEndDay, "1DAY");
		dvDss.setTimeWindow(startTime, endTime);
		Vector<String> pathList = dvDss.getPathnameList();
		Collections.sort(pathList, Collections.reverseOrder());
		for (int i=0; i<pathList.size(); i++){
			String path = pathList.get(i);
			String[] parts = path.split("/");
			if (parts[parts.length-1].equals(brp.svFPart)){
				try {
					TimeSeriesContainer dc = (TimeSeriesContainer)dvDss.get(path);
					TimeSeriesMath tm = new TimeSeriesMath(dc);
					TimeSeriesMath newTm = (TimeSeriesMath) tm.shiftInTime(shiftInDay);
					String newPath=regeneratePath(tm.getPath(), brp);
					newTm.setPathname(newPath);
					dvDss.write(newTm);
				} catch (Exception e) {
				}
			}
		}
		dvDss.close();
		
		DebugCorePlugin.isDssInOp=false;
	}
	
	public String regeneratePath(String path, BatchRunProcess brp){
		String[] parts = path.split("/");
		int size = parts.length;
		parts[size-1]=parts[size-1]+"_"+brp.paStartYear+TimeOperation.getMonthText(brp.paStartMonth)+brp.paStartDay;
		String newPath="";
		for (int i=0; i<size; i++){
			newPath=newPath+parts[i]+"/";
		}
		return newPath;
	}
}
