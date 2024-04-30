package wrimsv2_plugin.debugger.pa;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import wrimsv2_plugin.batchrun.BatchRunProcess;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class PAProcRunBR {
	
	private int prePAStartYear=1921;
	private int prePAStartMonth=10;
	private int prePAStartDay=1;
	
	public PAProcRunBR(){
	}
	
	public boolean continueRun(BatchRunProcess brp){
		int diff=TimeOperation.diffInDay(brp.paStartYear, brp.paStartMonth, brp.paStartDay, brp.paEndYear, brp.paEndMonth, brp.paEndDay);
		if (diff<0){
			return false;
		}else{
			return true;
		}
	}
	
	public void initialPATime(BatchRunProcess brp){	
		brp.paStartYear=brp.startYear;
		brp.paStartMonth=brp.startMonth;
		brp.paStartDay=brp.startDay;
		prePAStartYear=brp.paStartYear;
		prePAStartMonth=brp.paStartMonth;
		prePAStartDay=brp.paStartDay;
		getPAEndTime(brp);
		
	}
	
	public void updatePATime(BatchRunProcess brp){
		getPAStartTime(brp);
		getPAEndTime(brp);
	}
	
	public void getPAEndTime(BatchRunProcess brp){
		int month=brp.paStartMonth+brp.paDuration;
		int year=(month-1)/12;
		brp.paEndYear=brp.paStartYear+year;
		brp.paEndMonth=month-year*12;

		int daysIn=TimeOperation.numberOfDays(brp.paEndMonth, brp.paEndYear);
		brp.paEndDay=brp.paStartDay-1;	
		if (brp.paEndDay<=0){
			brp.paEndMonth=brp.paEndMonth-1;
			if (brp.paEndMonth<=0){
				brp.paEndMonth=12;
				brp.paEndYear=brp.paEndYear-1;		
			}
			brp.paEndDay=TimeOperation.numberOfDays(brp.paEndMonth, brp.paEndYear);			
		}else if (brp.paEndDay>daysIn){
			brp.paEndDay=daysIn;
		}
		
		int diff=TimeOperation.diffInDay(brp.paEndYear, brp.paEndMonth, brp.paEndDay, brp.endYear, brp.endMonth, brp.endDay);
		if (diff<0){
			brp.paEndYear=brp.endYear;
			brp.paEndMonth=brp.endMonth;
			brp.paEndDay=brp.endDay;
		}
	}
	
	public void getPAStartTime(BatchRunProcess brp){
		prePAStartYear=brp.paStartYear;
		prePAStartMonth=brp.paStartMonth;
		prePAStartDay=brp.paStartDay;
		
		int month=brp.paStartMonth+brp.paStartInterval;
		int year=(month-1)/12;
		brp.paStartYear=brp.paStartYear+year;
		brp.paStartMonth=month-year*12;
		brp.paStartDay=brp.startDay;
		int daysIn=TimeOperation.numberOfDays(brp.paStartMonth, brp.paStartYear);
		if (brp.paStartDay>daysIn){
			brp.paStartDay=daysIn;
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
