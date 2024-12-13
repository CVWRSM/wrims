package wrimsv2_plugin.reporttool;

import java.util.Set;
import wrimsv2_plugin.reporttool.Report.PathnameMap;
import wrimsv2_plugin.tools.DssOperations;
import wrimsv2_plugin.tools.TimeOperation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecTimeSeries;
import hec.io.TimeSeriesContainer;
import hec.heclib.util.HecTime;
import hec.heclib.util.Heclib;
import hec.hecmath.HecMathException;
import hec.hecmath.TimeSeriesMath;
import hec.hecmath.functions.*;
import hec.hecmath.computation.*;
import hec.data.TimeWindow;

public class Utils {
	static StringBuffer messages = new StringBuffer();

	public static void clearMessages() {
		messages.setLength(0);
	}

	public static void addMessage(String msg) {
		messages.append(msg).append("\n");
	}

	public static String getMessages() {
		return messages.toString();
	}

	public static PathnameMap getPathMapForVarName(String varname,
			ArrayList<PathnameMap> pathname_maps) {
		for (PathnameMap x : pathname_maps) {
			if (x.var_name.equals(varname)) {
				return x;
			}
		}
		return null;
	}
	
	public static TimeWindow getTimeWindow(String s){
		HecTime hTime = new HecTime();
		if (hTime.set(s.split("-")[0].trim()) != 0) return null;
		Date startTime = hTime.getJavaDate(TimeZone.getDefault().getRawOffset()/60000);
		if (hTime.set(s.split("-")[1].trim()) != 0) return null;
		Date endTime = hTime.getJavaDate(TimeZone.getDefault().getRawOffset()/60000);;
		return new TimeWindow(startTime, true, endTime, true);
	}

	public static TimeSeriesContainer getTSContainer(HecTimeSeries hts, String path_pattern, boolean calculate_dts)
	{
		if (calculate_dts) {
			try {
				// FIXME: add expression parser to enable any expression
				String bpart = path_pattern.split("/")[2];
				String[] vars = bpart.split("\\+");
				TimeSeriesContainer ref = null;
				TimeSeriesMath mref = new TimeSeriesMath();
				TimeSeriesMath mxref = new TimeSeriesMath();
				for (String varname : vars) {
					TimeSeriesContainer xref = null;
					String varPath = createPathFromVarname(path_pattern, varname);
					xref = getTSContainer(hts, varPath, false);
					if (xref == null) {
						throw new RuntimeException("Aborting calculation of "
								+ path_pattern + " due to previous path missing");
					}
					if (ref == null) {
						ref = xref;
					} else {
						mref.setData(ref);
						mxref.setData(xref);
						mref = (TimeSeriesMath) mref.add(mxref);
						ref = (TimeSeriesContainer) mref.getData();
					}
				}
				ref.fullName = createPathFromVarname(ref.fullName, bpart);
				return ref;
			} catch (Exception ex) {
				Utils.addMessage(ex.getMessage());
				return null;
			}
		} else {
			try {
				TimeSeriesContainer tsc = new TimeSeriesContainer();
				DSSPathname searchpath = new DSSPathname(path_pattern);
				// searchpath.setDPart("");
				// searchpath.formPathname();
				if (hts.recordExists(searchpath.toString())){
					tsc.fullName = path_pattern;
				    hts.read(tsc, false);
				    return tsc;
				}
				searchpath.setPathname(path_pattern);
				searchpath.setAPart("*");
				searchpath.setDPart("*");
				searchpath.setFPart("*");
				String[] paths = hts.getCatalog(false, searchpath.toString());
				if(paths == null || paths.length == 0) {
					Set<String> equivalentDssIntervals =
						DssOperations.findEquivalentDssIntervals(searchpath.getEPart());
					for(String ePart : equivalentDssIntervals) {
						searchpath.setEPart(ePart);
						paths = hts.getCatalog(false, searchpath.toString());
						if(paths != null && paths.length > 0) {
							break;
						}
					}
				}
				if(paths == null || paths.length == 0) {
					return null;
				}
				searchpath.setPathname(paths[0]);
				searchpath.setDPart("");
				tsc.fullName = searchpath.toString();
				hts.read(tsc, false);
				return tsc;
			} catch (Exception ex) {
				String msg = "Exception while trying to retrieve " + path_pattern
						+ " from " + hts.DSSFileName();
				System.err.println(msg);
				addMessage(msg);
				return null;
			}
		}
	}

	private static String createPathFromVarname(String path, String varname){
		return substitutePartIntoPath(path, varname, 2);
	} 
	public static String substitutePartIntoPath(String path, String part, int targetIndex) {
		String[] parts = path.split("/");
		if (parts.length > targetIndex) {
			parts[targetIndex] = part;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			builder.append(parts[i]).append("/");
		}
		for (int count = parts.length; count <= targetIndex; count++){
			if (count == targetIndex)builder.append(part);
			builder.append("/");
		}
		return builder.toString();
	}

	public static String getUnitsForReference(TimeSeriesContainer tsc) {
		if (tsc != null) {
			return tsc.units;
		}
		return "";
	}

	public static String getUnits(TimeSeriesContainer tsc1, TimeSeriesContainer tsc2) {
		if (tsc1 == null) {
			if (tsc2 == null) {
				return "";
			} else {
				return getUnitsForReference(tsc2);
			}
		} else {
			return getUnitsForReference(tsc1);
		}
	}

	public static String getTypeOfReference(TimeSeriesContainer tsc) {
		if (tsc != null) {
			return tsc.parameter;
		}
		return "";
	}

	public static String getType(TimeSeriesContainer tsc1, TimeSeriesContainer tsc2) {
		if (tsc1 == null) {
			if (tsc2 == null) {
				return "";
			} else {
				return getTypeOfReference(tsc2);
			}
		} else {
			return getTypeOfReference(tsc1);
		}
	}

	public static String getExceedancePlotTitle(PathnameMap path_map) {
		String title = "Exceedance " + path_map.var_name.replace("\"", "");
		if (path_map.var_category.equalsIgnoreCase("S_Jan")) {
			title = title + " (Jan)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Feb")) {
			title = title + " (Feb)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Mar")) {
			title = title + " (Mar)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Apr")) {
			title = title + " (Apr)";
		}else if (path_map.var_category.equalsIgnoreCase("S_May")) {
			title = title + " (May)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Jun")) {
			title = title + " (Jun)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Jul")) {
			title = title + " (Jul)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Aug")) {
			title = title + " (Aug)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Sep")) {
			title = title + " (Sep)";
		}else if (path_map.var_category.equalsIgnoreCase("S_SEPT")) {
			title = title + " (Sept)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Oct")) {
			title = title + " (Oct)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Nov")) {
			title = title + " (Nov)";
		}else if (path_map.var_category.equalsIgnoreCase("S_Dec")) {
			title = title + " (Dec)";
		}
		return title;
	}

	public static Date convertToDate(HecTime time_val) {
		return time_val.getJavaDate(TimeZone.getDefault().getRawOffset()/60000);
	}

	public static String extractNameFromReference(TimeSeriesContainer tsc) {
		return tsc.parameter + " @ " + tsc.location;
	}

	public static ArrayList<double[]> buildDataArray(TimeSeriesContainer ref1,
			TimeSeriesContainer ref2, TimeWindow tw) {
		ArrayList<double[]> dlist = new ArrayList<double[]>();
		if (ref1 == null && ref2 == null) {
			return dlist;
		}
		HecTime indexTime = new HecTime();
		indexTime.set(tw.getStartDate(), TimeZone.getDefault().getRawOffset()/60000);
		int[] times1 = ref1.times;
		double[] values1 = ref1.values;
		int[] times2 = ref2.times;
		double[] values2 = ref2.values;
		int i1 = 0; int i2 = 0;
		if (tw != null) {
			while (i1 < times1.length && times1[i1] < indexTime.value()) i1++;
			while (i2 < times2.length && times1[i2] < indexTime.value()) i2++;
		}
		while (i1 < times1.length && i2 < times2.length) {
			if (times1[i1] == times2[i2]){
				indexTime.set(times1[i1]);
				Date date = indexTime.getJavaDate(TimeZone.getDefault().getRawOffset()/60000);
				dlist.add(new double[] { date.getTime(), values1[i1], values2[i2]});
				i1++; i2++;}
			else return dlist;
		}
		return dlist;
	}

	public static ArrayList<Double> sort(TimeSeriesContainer tsc, int end, TimeWindow tw) {
		int i = 0;
		HecTime indexTime = new HecTime(tw.getStartDate(),TimeZone.getDefault().getRawOffset()/60000);
		HecTime endTime = new HecTime(tw.getEndDate(),TimeZone.getDefault().getRawOffset()/60000);
		if (tw != null) {
			while(tsc.times[i] < indexTime.value()) i++;
		}
		ArrayList<Double> dx = new ArrayList<Double>();
		while (i < tsc.numberValues && tsc.times[i] <= endTime.value()) {
			indexTime.set(tsc.times[i]);
			if (end==1) {
				if (indexTime.month() == 1 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==2) {
				if (indexTime.month() == 2 && (indexTime.day() == 28 || indexTime.day() == 29)) {
					dx.add(tsc.values[i]);
				}
			}else if (end==3) {
				if (indexTime.month() == 3 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==4) {
				if (indexTime.month() == 4 && indexTime.day() == 30) {
					dx.add(tsc.values[i]);
				}
			}else if (end==5) {
				if (indexTime.month() == 5 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==6) {
				if (indexTime.month() == 6 && indexTime.day() == 30) {
					dx.add(tsc.values[i]);
				}
			}else if (end==7) {
				if (indexTime.month() == 7 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==8) {
				if (indexTime.month() == 8 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==9) {
				if (indexTime.month() == 9 && indexTime.day() == 30) {
					dx.add(tsc.values[i]);
				}
			}else if (end==10) {
				if (indexTime.month() == 10 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			}else if (end==11) {
				if (indexTime.month() == 11 && indexTime.day() == 30) {
					dx.add(tsc.values[i]);
				}
			}else if (end==12) {
				if (indexTime.month() == 12 && indexTime.day() == 31) {
					dx.add(tsc.values[i]);
				}
			} else {
				dx.add(tsc.values[i]);
			}
			i++;
		}
		Collections.sort(dx);
		return dx;
	}

	public static ArrayList<double[]> buildExceedanceArray(TimeSeriesContainer ref1,
			TimeSeriesContainer ref2, int end_of_month, TimeWindow tw) {
		ArrayList<Double> x1 = sort(ref1, end_of_month, tw);
		ArrayList<Double> x2 = sort(ref2, end_of_month, tw);
		ArrayList<double[]> darray = new ArrayList<double[]>();
		int i = 0;
		int n = (int) Math.round(Math.min(x1.size(), x2.size()));
		while (i < n) {
			darray.add(new double[] { 100.0 - 100.0 * i / (n + 1), x1.get(i),
					x2.get(i) });
			i = i + 1;
		}
		return darray;
	}

	public static TimeSeriesContainer cfs2taf(TimeSeriesContainer data) {
		try{
			TimeSeriesContainer tsc_af = (TimeSeriesContainer) data.clone();
			TimeSeriesFunctions.transformTimeSeries(data, tsc_af, TimeSeriesMath.VOLUME, false, 0.0d);
			TimeSeriesMath tsm_af = (new TimeSeriesMath(tsc_af));
			TimeSeriesMath tsm_taf = (TimeSeriesMath) tsm_af.divide(1000.0);
			tsm_taf.setUnits("TAF");
			return (TimeSeriesContainer) tsm_taf.getData();
		}
		catch (ComputationException e){return null;}
		catch (HecMathException e) {return null;}
	}

	public static TimeSeriesContainer taf2cfs(TimeSeriesContainer data) {
		HecTime indexTime = new HecTime();
		TimeSeriesContainer tsc_cfs = (TimeSeriesContainer) data.clone();
		for (int i = 0; i < data.numberValues; i++) {
			indexTime.set(data.times[i]);
			tsc_cfs.values[i] = data.values[i] * (504.1666667 / indexTime.day());
		}
		DSSPathname path_cfs = new DSSPathname(data.fullName);
		path_cfs.setCPart("FLOW");
		tsc_cfs.fullName = path_cfs.pathname();
		tsc_cfs.setUnits("CFS");
		tsc_cfs.parameter = "FLOW";
		return tsc_cfs;
	}

	public static double avg(TimeSeriesContainer tsc, TimeWindow tw) {
		try {
			int i = 0;
			int valueCount = 0;
			int missingCount = 0;
			int lastTimeValue = tsc.endHecTime.value();
			double valueSum = 0.0d;
			
			HecTime indexTime = new HecTime();
			HecTime endTime = new HecTime();
			if (tw != null) {
				indexTime.set(tw.getStartDate(),TimeZone.getDefault().getRawOffset()/60000);
				endTime.set(tw.getEndDate(),TimeZone.getDefault().getRawOffset()/60000);
				lastTimeValue = endTime.value();
				while(tsc.times[i] < indexTime.value()) i++;
			}
			while (i < tsc.numberValues){
				if (tsc.times[i] > lastTimeValue)break;
				if (tsc.values[i] == Heclib.UNDEFINED_DOUBLE){
					missingCount += 1;
					continue;
				}
				valueSum += tsc.values[i];
				valueCount += 1;
				i++;
			}
			if (valueCount > 0)	return (valueSum/valueCount)*12;
			else return Double.NaN;
		} catch (Exception ex) {
			return Double.NaN;
		}
	}

	public static String formatTimeWindowAsWaterYear(TimeWindow tw) {
		// SubTimeFormat year_format = new SubTimeFormat("yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(tw.getStartDate());
		cal.add(Calendar.MONTH, 3);
		String rv = String.valueOf(cal.get(Calendar.YEAR)) + "-";
		cal.setTime(tw.getEndDate());
		//cal.add(Calendar.MONTH, 3);
		rv = rv + String.valueOf(cal.get(Calendar.YEAR));
		return rv;
	}

	public static String formatTimeAsYearMonthDay(Date d) {
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(d);
		return gmtCal.get(Calendar.YEAR) + "," + gmtCal.get(Calendar.MONTH)
				+ "," + gmtCal.get(Calendar.DATE);
	}

	public static String formatAsOptionValue(TimeWindow tw) {
		return formatTimeAsYearMonthDay(tw.getStartDate()) + "-"
				+ formatTimeAsYearMonthDay(tw.getEndDate());
	}

	public static int monthToInt(String month) {

		HashMap<String, Integer> monthMap = new HashMap<String, Integer>();

		monthMap.put("jan", 1);
		monthMap.put("feb", 2);
		monthMap.put("mar", 3);
		monthMap.put("apr", 4);
		monthMap.put("may", 5);
		monthMap.put("jun", 6);
		monthMap.put("jul", 7);
		monthMap.put("aug", 8);
		monthMap.put("sep", 9);
		monthMap.put("oct", 10);
		monthMap.put("nov", 11);
		monthMap.put("dec", 12);

		month = month.toLowerCase();
		Integer monthCode = null;

		try {

			monthCode = monthMap.get(month);

		}

		catch (Exception e) {

			//log.debug(e.getMessage());

		}

		if (monthCode == null) {

			//log.debug("Invalid Key at UnitsUtils.monthToInt");
			return -1;

		}

		return monthCode.intValue();

	}

	public static ArrayList<double[]> buildMonthlyAverages(TimeSeriesContainer tsc1, TimeSeriesContainer tsc2, TimeWindow tw) {
		TimeSeriesContainer tscc1 = (TimeSeriesContainer) tsc1.clone();
		TimeSeriesContainer tscc2 = (TimeSeriesContainer) tsc2.clone();
		ArrayList<double[]> dlist = new ArrayList<double[]>();
		if ((tsc1 == null) && (tsc2 == null)) {
			return dlist;
		}
		if (tw != null) {
			Date sd = tw.getStartDate();
			HecTime sdht=new HecTime();
			Calendar sdc = Calendar.getInstance();
			sdc.setTime(sd);
			sdht.set(sdc);
			Date ed = tw.getEndDate();
			HecTime edht=new HecTime();
			Calendar edc = Calendar.getInstance();
			edc.setTime(ed);
			edht.set(edc);  
			tscc1.trimToTime(sdht, edht);
			tscc2.trimToTime(sdht, edht);
		}	
		int[] size1=new int[12];
		int[] size2=new int[12];
		double[] avg1=new double[12];
		double[] avg2=new double[12];
		for (int i=0; i<12; i++){
			size1[i]=0;
			size2[i]=0;
			avg1[i]=0.0;
			avg2[i]=0.0;
		}
		
		HecTime st1 = tscc1.startHecTime;
		int month1 = st1.month();
		double[] values1 = tscc1.values;
		for (int i=0; i<values1.length; i++){
			int index= month1-1;
			size1[index]=size1[index]+1;
			avg1[index]=avg1[index]+values1[i];
			month1=month1+1;
			if (month1>12) month1=1;
		}
		
		HecTime st2 = tscc2.startHecTime;
		int month2 = st2.month();
		double[] values2 = tscc2.values;
		for (int i=0; i<values2.length; i++){
			int index= month2-1;
			size2[index]=size2[index]+1;
			avg2[index]=avg2[index]+values2[i];
			month2=month2+1;
			if (month2>12) month2=1;
		}
		
		for (int i=9; i<12; i++){
			Date dataDate=new Date();
			int year=st1.year()-1;
			dataDate.setYear(year-1900);
			dataDate.setMonth(i);
			dataDate.setDate(TimeOperation.numberOfDays(i+1, 1900+year));
			if (size1[i]==0){
				avg1[i]=0.0;
			}else{
				avg1[i]=avg1[i]/size1[i];
			}
			if (size2[i]==0){
				avg2[i]=0.0;
			}else{
				avg2[i]=avg2[i]/size2[i];
			}
			dlist.add(new double[] { dataDate.getTime(), avg1[i], avg2[i] });
		}
		
		for (int i=0; i<9; i++){
			Date dataDate=new Date();
			int year=st1.year();
			dataDate.setYear(year-1900);
			dataDate.setMonth(i);
			dataDate.setDate(TimeOperation.numberOfDays(i+1, 1900+year));
			if (size1[i]==0){
				avg1[i]=0.0;
			}else{
				avg1[i]=avg1[i]/size1[i];
			}
			if (size2[i]==0){
				avg2[i]=0.0;
			}else{
				avg2[i]=avg2[i]/size2[i];
			}
			dlist.add(new double[] { dataDate.getTime(), avg1[i], avg2[i] });
		}
		return dlist;
	}
}
