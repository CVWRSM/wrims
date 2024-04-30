package wrimsv2_plugin.debugger.msr;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.batchrun.BatchRunProcess;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class MSRProcRunBR {
	
	private int preMSStartYear=1921;
	private int preMSStartMonth=10;
	private int preMSStartDay=1;
	
	public boolean continueRun(BatchRunProcess brp){
		int diff=TimeOperation.diffInDay(brp.msStartYear, brp.msStartMonth, brp.msStartDay, brp.msEndYear, brp.msEndMonth, brp.msEndDay);
		if (diff<0){
			return false;
		}else{
			return true;
		}
	}
	
	public void initialMSTime(BatchRunProcess brp){	
		brp.msDurationIndex=0;
		brp.msStartYear=brp.startYear;
		brp.msStartMonth=brp.startMonth;
		brp.msStartDay=brp.startDay;
		preMSStartYear=brp.msStartYear;
		preMSStartMonth=brp.msStartMonth;
		preMSStartDay=brp.msStartDay;
		getMSEndTime(brp);	
	}
	
	public void updateMSTime(BatchRunProcess brp){
		getMSStartTime(brp);
		if (brp.msDurationIndex<brp.msDuration.length-1){
			brp.msDurationIndex++;
		}
		getMSEndTime(brp);
	}
	
	public void getMSEndTime(BatchRunProcess brp){
		int month=brp.msStartMonth+brp.msDuration[brp.msDurationIndex];
		int year=(month-1)/12;
		brp.msEndYear=brp.msStartYear+year;
		brp.msEndMonth=month-year*12;

		int daysIn=TimeOperation.numberOfDays(brp.msEndMonth, brp.msEndYear);
		brp.msEndDay=brp.msStartDay-1;	
		if (brp.msEndDay<=0){
			brp.msEndMonth=brp.msEndMonth-1;
			if (brp.msEndMonth<=0){
				brp.msEndMonth=12;
				brp.msEndYear=brp.msEndYear-1;		
			}
			brp.msEndDay=TimeOperation.numberOfDays(brp.msEndMonth, brp.msEndYear);			
		}else if (brp.msEndDay>daysIn){
			brp.msEndDay=daysIn;
		}
		
		int diff=TimeOperation.diffInDay(brp.msEndYear, brp.msEndMonth, brp.msEndDay, brp.endYear, brp.endMonth, brp.endDay);
		if (diff<0){
			brp.msEndYear=brp.endYear;
			brp.msEndMonth=brp.endMonth;
			brp.msEndDay=brp.endDay;
		}
	}
	
	public void getMSStartTime(BatchRunProcess brp){
		preMSStartYear=brp.msStartYear;
		preMSStartMonth=brp.msStartMonth;
		preMSStartDay=brp.msStartDay;
		
		int month=brp.msStartMonth+brp.msDuration[brp.msDurationIndex];
		int year=(month-1)/12;
		brp.msStartYear=brp.msStartYear+year;
		brp.msStartMonth=month-year*12;
		brp.msStartDay=brp.startDay;
		int daysIn=TimeOperation.numberOfDays(brp.msStartMonth, brp.msStartYear);
		if (brp.msStartDay>daysIn){
			brp.msStartDay=daysIn;
		}
	}
	
	public int getPreMSStartYear() {
		return preMSStartYear;
	}
	
	public int getPreMSStartMonth() {
		return preMSStartMonth;
	}
	
	public int getPreMSStartDay() {
		return preMSStartDay;
	}
}
