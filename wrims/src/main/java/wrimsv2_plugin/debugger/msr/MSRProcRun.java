package wrimsv2_plugin.debugger.msr;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class MSRProcRun {
	
	private int preMSStartYear=1921;
	private int preMSStartMonth=10;
	private int preMSStartDay=1;
	
	public boolean continueRun(){
		int diff=TimeOperation.diffInDay(DebugCorePlugin.msStartYear, DebugCorePlugin.msStartMonth, DebugCorePlugin.msStartDay, DebugCorePlugin.msEndYear, DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndDay);
		if (diff<0){
			return false;
		}else{
			return true;
		}
	}
	
	public void initialMSTime(){	
		DebugCorePlugin.msDurationIndex=0;
		DebugCorePlugin.msStartYear=DebugCorePlugin.startYear;
		DebugCorePlugin.msStartMonth=DebugCorePlugin.startMonth;
		DebugCorePlugin.msStartDay=DebugCorePlugin.startDay;
		preMSStartYear=DebugCorePlugin.msStartYear;
		preMSStartMonth=DebugCorePlugin.msStartMonth;
		preMSStartDay=DebugCorePlugin.msStartDay;
		getMSEndTime();
		
	}
	
	public void updateMSTime(){
		getMSStartTime();
		if (DebugCorePlugin.msDurationIndex<DebugCorePlugin.msDuration.length-1){
			DebugCorePlugin.msDurationIndex++;
		}
		getMSEndTime();
	}
	
	public void getMSEndTime(){
		int month=DebugCorePlugin.msStartMonth+DebugCorePlugin.msDuration[DebugCorePlugin.msDurationIndex];
		int year=(month-1)/12;
		DebugCorePlugin.msEndYear=DebugCorePlugin.msStartYear+year;
		DebugCorePlugin.msEndMonth=month-year*12;

		int daysIn=TimeOperation.numberOfDays(DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndYear);
		DebugCorePlugin.msEndDay=DebugCorePlugin.msStartDay-1;	
		if (DebugCorePlugin.msEndDay<=0){
			DebugCorePlugin.msEndMonth=DebugCorePlugin.msEndMonth-1;
			if (DebugCorePlugin.msEndMonth<=0){
				DebugCorePlugin.msEndMonth=12;
				DebugCorePlugin.msEndYear=DebugCorePlugin.msEndYear-1;		
			}
			DebugCorePlugin.msEndDay=TimeOperation.numberOfDays(DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndYear);			
		}else if (DebugCorePlugin.msEndDay>daysIn){
			DebugCorePlugin.msEndDay=daysIn;
		}
		
		int diff=TimeOperation.diffInDay(DebugCorePlugin.msEndYear, DebugCorePlugin.msEndMonth, DebugCorePlugin.msEndDay, DebugCorePlugin.endYear, DebugCorePlugin.endMonth, DebugCorePlugin.endDay);
		if (diff<0){
			DebugCorePlugin.msEndYear=DebugCorePlugin.endYear;
			DebugCorePlugin.msEndMonth=DebugCorePlugin.endMonth;
			DebugCorePlugin.msEndDay=DebugCorePlugin.endDay;
		}
	}
	
	public void getMSStartTime(){
		preMSStartYear=DebugCorePlugin.msStartYear;
		preMSStartMonth=DebugCorePlugin.msStartMonth;
		preMSStartDay=DebugCorePlugin.msStartDay;
		
		int month=DebugCorePlugin.msStartMonth+DebugCorePlugin.msDuration[DebugCorePlugin.msDurationIndex];;
		int year=(month-1)/12;
		DebugCorePlugin.msStartYear=DebugCorePlugin.msStartYear+year;
		DebugCorePlugin.msStartMonth=month-year*12;
		DebugCorePlugin.msStartDay=DebugCorePlugin.startDay;
		int daysIn=TimeOperation.numberOfDays(DebugCorePlugin.msStartMonth, DebugCorePlugin.msStartYear);
		if (DebugCorePlugin.msStartDay>daysIn){
			DebugCorePlugin.msStartDay=daysIn;
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
