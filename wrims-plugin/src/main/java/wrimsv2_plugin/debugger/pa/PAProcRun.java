package wrimsv2_plugin.debugger.pa;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcRun {
	
	private int prePAStartYear=1921;
	private int prePAStartMonth=10;
	private int prePAStartDay=1;
	
	public PAProcRun(){
	}
	
	public boolean continueRun(){
		int diff=TimeOperation.diffInDay(DebugCorePlugin.paStartYear, DebugCorePlugin.paStartMonth, DebugCorePlugin.paStartDay, DebugCorePlugin.paEndYear, DebugCorePlugin.paEndMonth, DebugCorePlugin.paEndDay);
		if (diff<0){
			return false;
		}else{
			return true;
		}
	}
	
	public void initialPATime(){	
		DebugCorePlugin.paStartYear=DebugCorePlugin.startYear;
		DebugCorePlugin.paStartMonth=DebugCorePlugin.startMonth;
		DebugCorePlugin.paStartDay=DebugCorePlugin.startDay;
		prePAStartYear=DebugCorePlugin.paStartYear;
		prePAStartMonth=DebugCorePlugin.paStartMonth;
		prePAStartDay=DebugCorePlugin.paStartDay;
		getPAEndTime();
		
	}
	
	public void updatePATime(){
		getPAStartTime();
		getPAEndTime();
	}
	
	public void getPAEndTime(){
		int month=DebugCorePlugin.paStartMonth+DebugCorePlugin.paDuration;
		int year=(month-1)/12;
		DebugCorePlugin.paEndYear=DebugCorePlugin.paStartYear+year;
		DebugCorePlugin.paEndMonth=month-year*12;

		int daysIn=TimeOperation.numberOfDays(DebugCorePlugin.paEndMonth, DebugCorePlugin.paEndYear);
		DebugCorePlugin.paEndDay=DebugCorePlugin.paStartDay-1;	
		if (DebugCorePlugin.paEndDay<=0){
			DebugCorePlugin.paEndMonth=DebugCorePlugin.paEndMonth-1;
			if (DebugCorePlugin.paEndMonth<=0){
				DebugCorePlugin.paEndMonth=12;
				DebugCorePlugin.paEndYear=DebugCorePlugin.paEndYear-1;		
			}
			DebugCorePlugin.paEndDay=TimeOperation.numberOfDays(DebugCorePlugin.paEndMonth, DebugCorePlugin.paEndYear);			
		}else if (DebugCorePlugin.paEndDay>daysIn){
			DebugCorePlugin.paEndDay=daysIn;
		}
		
		int diff=TimeOperation.diffInDay(DebugCorePlugin.paEndYear, DebugCorePlugin.paEndMonth, DebugCorePlugin.paEndDay, DebugCorePlugin.endYear, DebugCorePlugin.endMonth, DebugCorePlugin.endDay);
		if (diff<0){
			DebugCorePlugin.paEndYear=DebugCorePlugin.endYear;
			DebugCorePlugin.paEndMonth=DebugCorePlugin.endMonth;
			DebugCorePlugin.paEndDay=DebugCorePlugin.endDay;
		}
	}
	
	public void getPAStartTime(){
		prePAStartYear=DebugCorePlugin.paStartYear;
		prePAStartMonth=DebugCorePlugin.paStartMonth;
		prePAStartDay=DebugCorePlugin.paStartDay;
		
		int month=DebugCorePlugin.paStartMonth+DebugCorePlugin.paStartInterval;
		int year=(month-1)/12;
		DebugCorePlugin.paStartYear=DebugCorePlugin.paStartYear+year;
		DebugCorePlugin.paStartMonth=month-year*12;
		DebugCorePlugin.paStartDay=DebugCorePlugin.startDay;
		int daysIn=TimeOperation.numberOfDays(DebugCorePlugin.paStartMonth, DebugCorePlugin.paStartYear);
		if (DebugCorePlugin.paStartDay>daysIn){
			DebugCorePlugin.paStartDay=daysIn;
		}
	}

	public int getPrePAStartYear() {
		return prePAStartYear;
	}
	
	public int getPrePAStartMonth() {
		return prePAStartMonth;
	}
	
	public int getPrePAStartDay() {
		return prePAStartDay;
	}
}
