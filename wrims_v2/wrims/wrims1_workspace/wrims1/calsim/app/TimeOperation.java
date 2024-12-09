package calsim.app;

import java.util.Date;

public class TimeOperation {
	public static String dssTimeFrame(int year1, int month1, int day1, int year2, int month2, int day2){
		return dayName(day1)+monthName(month1)+year1+" 0100 - "+dayName(day2)+monthName(month2)+year2+" 0000";
	}
	
	public static String dssTime(int year, int month, int day){
		return dayName(day)+monthName(month)+year+" 0000"; 
	}
	
	public static String dssTimeEndDay(int year, int month, int day){
		return dayName(day)+monthName(month)+year+" 2400"; 
	}
	
	public static String dayName(int day){
		if (day<10){
			return "0"+day;
		}else{
			return ""+day;
		}
	}
	
	public static int monthValue(String month){
		if (month.equals("jan")){
			return 1;
		}else if (month.equals("feb")){
			return 2;
		}else if (month.equals("mar")){
			return 3;
		}else if (month.equals("apr")){
			return 4;
		}else if (month.equals("may")){
			return 5;
		}else if (month.equals("jun")){
			return 6;
		}else if (month.equals("jul")){
			return 7;
		}else if (month.equals("aug")){
			return 8;
		}else if (month.equals("sep")){
			return 9;
		}else if (month.equals("oct")){
			return 10;
		}else if (month.equals("nov")){
			return 11;
		}else{
			return 12;
		}
	}
	
	public static int waterMonthValue(String month){
		if (month.equals("jan")){
			return 4;
		}else if (month.equals("feb")){
			return 5;
		}else if (month.equals("mar")){
			return 6;
		}else if (month.equals("apr")){
			return 7;
		}else if (month.equals("may")){
			return 8;
		}else if (month.equals("jun")){
			return 9;
		}else if (month.equals("jul")){
			return 10;
		}else if (month.equals("aug")){
			return 11;
		}else if (month.equals("sep")){
			return 12;
		}else if (month.equals("oct")){
			return 1;
		}else if (month.equals("nov")){
			return 2;
		}else{
			return 3;
		}
	}
	
	public static int waterMonthValue(int month){
		if (month>=10){
			return month-9;
		}else{
			return month+3;
		}
	}
	
	public static String monthName(int month){
		if (month == 1){
			return "JAN";
		}else if (month == 2){
			return "FEB";
		}else if (month == 3){
			return "MAR";
		}else if (month == 4){
			return "APR";
		}else if (month == 5){
			return "MAY";
		}else if (month == 6){
			return "JUN";
		}else if (month == 7){
			return "JUL";
		}else if (month == 8){
			return "AUG";
		}else if (month == 9){
			return "SEP";
		}else if (month == 10){
			return "OCT";
		}else if (month == 11){
			return "NOV";
		}else{
			return "DEC";
		}
	}
	
	public static String monthNameNumeric(int month){
		if (month == 1){
			return "01";
		}else if (month == 2){
			return "02";
		}else if (month == 3){
			return "03";
		}else if (month == 4){
			return "04";
		}else if (month == 5){
			return "05";
		}else if (month == 6){
			return "06";
		}else if (month == 7){
			return "07";
		}else if (month == 8){
			return "08";
		}else if (month == 9){
			return "09";
		}else if (month == 10){
			return "10";
		}else if (month == 11){
			return "11";
		}else{
			return "12";
		}
	}
	
	public static boolean isLeapYear(int year){
		if (year % 4 == 0) {
		    if (year % 100 != 0) {
		    	return true;
		    }else if (year % 400 == 0) {
		    	return true;
		    }else {
		    	return false;
		    }
		}else{
			return false;
		}
	}
	
	public static int numberOfDays(int month, int year){
		int days;
		if (month==1 || month==3 || month==5 || month==7 
				||month==8 || month==10 ||month==12){
			days=31;
		}else if (month==4|| month==6 || month==9 || month==11){ 
			days=30;
		}else {
			if (TimeOperation.isLeapYear(year)){
				days=29;
			}else{
				days=28;
			}
		}
		return days;
	}
	
	public static int getNumberOfTimestep(Date dateA, Date dateB, String timeStep){
		if (timeStep.equals("1MON")){
			int monthA=dateA.getMonth();
			int yearA=dateA.getYear();
			int monthB=dateB.getMonth();
			int yearB=dateB.getYear();
			int diff=(yearB-yearA)*12+(monthB-monthA)+1;
			if (diff<=0) diff=0;
			return diff;
		}else{
			long timeA=dateA.getTime();
			long timeB=dateB.getTime();
			int diff=Math.round((timeB-timeA)/(1000*60*60*24))+1;
			if (diff<=0) diff=0;
			return diff;
		}
	}
	
	public static Date addOneMonth(Date date){
		int month=date.getMonth()+1;
		int year=date.getYear();
		if (month>11){
			month=month-12;
			year=year+1;
		}
		int day=TimeOperation.numberOfDays(month+1, year+1900);
		Date newDate = new Date(year, month, day);
		return newDate;
	}
	
	public static Date addOneDay(Date date){
		long newTime=date.getTime()+1 * 24 * 60 * 60 * 1000l;
		Date newDate = new Date (newTime);
		return newDate;
	}
}
