package gov.ca.dwr.wrims.calsimshp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.jdiagram.SchematicPluginCore;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.TimeSeriesContainer;

import org.opengis.filter.identity.FeatureId;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

/**
 * This class is intended to contain all of the logic required to retrieve the DSS values
 * associated with a given feature.
 * 
 * @author chodgson@refractions.net
 *
 */
public class DSSResolver {
	
	public static final int NUM_DSS = 4;
	
	private final int firstDssIndex;
	
	private final String name;
	
	private static double FACTOR = 1000. * 43560 / (24 * 60 * 60);
	
	private int precision = 0; 

	/**
	 * Create a new DSSResolver based key points of the featureType on which the Resolver is
	 * intended to operate. 
	 * @param firstDssIndex the index of the first attribute intended to contain DSS values;
	 * 						the NUM_DSS DSS attributes are contiguous from this index
	 * @param idIndex the index of the attribute which contains the identifier of the feature
	 */
	public DSSResolver(int firstDssIndex, String name) {
		this.firstDssIndex = firstDssIndex;
		this.name=name;
	}

	/**
	 * If the specified index represents a DSS attribute, update the values array to contain the
	 * current value of said attribute.
	 * 
	 * @param values an array of the feature's attributes, to be updated with the new DSS value
	 * @param index the index of the attribute to be updated, if it is a DSS attribute
	 */
	public void resolve(Object[] values, int index, Object baseValue) {
		
		if(index >= firstDssIndex && index < firstDssIndex + NUM_DSS) {
			if (!DssPluginCore.geoSchematicVariableNames.contains(name)){
				DssPluginCore.geoSchematicVariableNames.add(name);
				loadGeoSchematicVariableData();
			}
			
			// from 0 to (NUM_DSS - 1), this specifies which dataset to retrieve data from
			int dssIndex = index - firstDssIndex; 
			if (DebugCorePlugin.selectedStudies[dssIndex]){
				values[index] = truncateAfterDecimal(retrieveUndebug(dssIndex, baseValue), precision,
						true);
			}else{
				values[index] = "";
			}
		}
	}

	/**
	 * Update all DSS attributes in the values array of this feature.
	 * 
	 * @param values an array of the feature's attributes, to be updated with the new DSS value(s)
	 */
	public void resolveAll(Object[] values) {
		// this approach simply reuses the single-value lookup from above
		// if there is a more efficient way to do this, please go ahead
		
		Object baseValue="0";
		boolean updateBaseValue=true;
		for(int index = firstDssIndex; index < firstDssIndex + NUM_DSS; index++) {
			resolve(values, index, baseValue);
			if (updateBaseValue){
				baseValue=values[index];
				updateBaseValue=false;
			}
		}
	}

	public void loadGeoSchematicVariableData(){
		if (DssPluginCore.allPathName.containsKey(name)){
			String pathName = DssPluginCore.allPathName.get(name);
			for (int i=0; i<4; i++){
				if (DebugCorePlugin.selectedStudies[i]){
					HecMath dataSet=null;
					HecDss dvFile = DebugCorePlugin.dvDss[i];
					HecDss svFile = DebugCorePlugin.svDss[i];
					if (dvFile != null){
						try {
							dataSet= dvFile.read(pathName);
							if (dataSet ==null){
								readFromSV(svFile, pathName, i);
								continue;
							}else{
								DssPluginCore.geoSchematicVariableData[i].put(name, dataSet);
								DssPluginCore.geoSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
								DssPluginCore.geoSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
								continue;
							}
						} catch (Exception e) {
							readFromSV(svFile, pathName, i);
						}
					}else{
						readFromSV(svFile, pathName, i);
					}
				}
			}
		}else{
			for (int i=0; i<4; i++){
				if (DebugCorePlugin.selectedStudies[i]){
					HecMath dataSet=null;
					HecDss dvFile = DebugCorePlugin.dvDss[i];
					HecDss svFile = DebugCorePlugin.svDss[i];
				
					String pathName=DssPluginCore.dvPathnameMap[i].get(name);
					if (pathName !=null){
						try {
							dataSet= dvFile.read(pathName);
							DssPluginCore.geoSchematicVariableData[i].put(name, dataSet);
							DssPluginCore.geoSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
							DssPluginCore.geoSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
						}catch (Exception e) {
						}
					}
					pathName=DssPluginCore.svPathnameMap[i].get(name);
					if (pathName !=null){
						try {
							dataSet= svFile.read(pathName);
							DssPluginCore.geoSchematicVariableData[i].put(name, dataSet);
							DssPluginCore.geoSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
							DssPluginCore.geoSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
						}catch (Exception e) {
						}
					}
				}
			}
		}
	}
	
	public void readFromSV(HecDss svFile, String pathName, int i){
		try {
			HecMath dataSet = svFile.read(pathName);
			if (dataSet !=null){
				DssPluginCore.geoSchematicVariableData[i].put(name, dataSet);
				DssPluginCore.geoSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
				DssPluginCore.geoSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
			}
		} catch (Exception e) {
		}
	}
	
	public String retrieveUndebug(int index, Object baseValue){
		ArrayList<String> tws = DssPluginCore._schematicTwSelections;
		String date=SchematicPluginCore.selDate;
		for (int i=0; i<tws.size()-1; i++){
			if (date.equals(tws.get(i+1))){
				if (DssPluginCore.units.equals(DssPluginCore.cfs)){
					if (DssPluginCore.months.size()<12){
						return retrieveLongTermAverageSelectedMonths(date, index, true, baseValue);
					}else{
						return retrieveLongTermAverage(i, date, index, true, baseValue);
					}
				}else{
					if (DssPluginCore.months.size()<12){
						return retrieveLongTermAverageSelectedMonths(date, index, false, baseValue);
					}else{
						return retrieveLongTermAverage(i, date, index, false, baseValue);
					}
				}
			}
		}
		
		String result="";

		ArrayList<String[]> pathParts = new ArrayList<String[]>();
		CondensedReference found = null;
		boolean isStorage=false;
		String value = "";			
		if (DssPluginCore.geoSchematicVariableData[index].containsKey(name)){
			try {
				HecMath dataSet = DssPluginCore.geoSchematicVariableData[index].get(name);
				if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
				TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
				if (tsc !=null) {
					boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
							: false);
					tsc = unitsConversion(isStorage, tsc, isTAFSelected, index);
					HecTime hecStartTime = new HecTime();
					hecStartTime.set(tsc.startTime);
					HecTime ht = new HecTime();
							
					ht.setDate(date);
					int valueIndex = (ht.year() - hecStartTime.year())
								* 12 + (ht.month() - hecStartTime.month());
					if (valueIndex < 0 || valueIndex > tsc.values.length - 1) {
						value = "N/A";
					} else {
						Double value_temp=tsc.values[valueIndex];
						if (Math.abs(value_temp)>100000000) {//empty value
							value="N/A";
						} else {
							value = tsc.values[valueIndex] + " "
									+ tsc.units;
						}
					}
														
					if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
						String[] altFields = value.split("\\s");
						try {
							double baseVal = Double.parseDouble((String)baseValue);
							double altVal = Double.parseDouble(altFields[0]);
							result=(altVal - baseVal) + " " + altFields[1];
						} catch (NumberFormatException nfe) {
							result=value;
						}
					} else {
						result=value;
					}
				}else{
					result=value;
				}
			} catch (Exception e) {
				//WPPException.handleException(e);
				result=value;
			}
		}		
		
		return result;
	}
	
public Double calculateLongTermAverage(String date, int di, boolean isCFS, HashMap<String, Double> altAverage){
		
		Double result=null;
				
		int index = 0;
		int month = -1;
		int year = -1;

		HecTime startDate = new HecTime();
		HecTime endDate = new HecTime();
		
		String[] split = date.split(" - ");
		startDate.setDate(split[0]); 
		startDate.setTime("0100");

		endDate.setDate(split[1]); 
		endDate.setTime("2400");
		month = endDate.month() + 1;
		year = endDate.year();
		if (month == 13) {
			year = startDate.year() + 1;
		}
		String monthName = TimeOperation.getMonthText(month);
		endDate.setDate(monthName + year);
		endDate.add(-1440);
		
		HecTime ht = new HecTime();
		HecTime hecStartTime = new HecTime();
		HecTime hecEndTime = new HecTime();
		
		HashMap<String, HecMath> altGeoSchematicVariableData = DssPluginCore.geoSchematicVariableData[di];
		if (altGeoSchematicVariableData.containsKey(name)){
			HecMath dataSet=altGeoSchematicVariableData.get(name);
			boolean isStorage=false;
			try {
				TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
				if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
				boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
						: false);
				tsc=unitsConversion(isStorage, tsc, isTAFSelected, di);
				ht.set(startDate);
				hecStartTime.set(tsc.startTime);
				hecEndTime.set(tsc.endTime);
				int count=0;
				double sum=0.0;
				boolean inRange=false;
				while (ht.compareTimes(hecEndTime)<=0 && ht.compareTimes(endDate)<=0){
					if (ht.compareTimes(hecStartTime) >= 0){
						int valueIndex = (ht.year() - hecStartTime
								.year())
								* 12
								+ (ht.month() - hecStartTime
										.month());
						sum += tsc.values[valueIndex];
						count++;
						inRange=true;
					}
					month = ht.month() + 1;
					if (month == 13) {
						month = 1;
						year = ht.year() + 1;
					} else {
						year = ht.year();
					}
					ht.setDate(TimeOperation.getMonthText(month)+
							+ year);
				}
				if (inRange) {
					result=sum/count;
				}
			} catch (HecMathException e) {
				e.printStackTrace();
			}
			altAverage.put(name, result);
		}
		
		return result;
	}	
	
	public Double calculateLongTermAverageSelectedMonths(String date, int di, boolean isCFS){
		
		Double result=null;
				
		int index = 0;
		int month = -1;
		int year = -1;

		HecTime startDate = new HecTime();
		HecTime endDate = new HecTime();
		
		String[] split = date.split(" - ");
		startDate.setDate(split[0]); 
		startDate.setTime("0100");

		endDate.setDate(split[1]); 
		endDate.setTime("2400");
		month = endDate.month() + 1;
		year = endDate.year();
		if (month == 13) {
			year = startDate.year() + 1;
		}
		String monthName = TimeOperation.getMonthText(month);
		endDate.setDate(monthName + year);
		endDate.add(-1440);
		
		HecTime ht = new HecTime();
		HecTime hecStartTime = new HecTime();
		HecTime hecEndTime = new HecTime();
		
		HashMap<String, HecMath> altGeoSchematicVariableData = DssPluginCore.geoSchematicVariableData[di];
		if (altGeoSchematicVariableData.containsKey(name)){
			HecMath dataSet=altGeoSchematicVariableData.get(name);
			boolean isStorage=false;
			try {
				TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
				if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
				boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
						: false);
				tsc=unitsConversion(isStorage, tsc, isTAFSelected, di);
				ht.set(startDate);
				hecStartTime.set(tsc.startTime);
				hecEndTime.set(tsc.endTime);
				int count=0;
				double sum=0.0;
				boolean inRange=false;
				while (ht.compareTimes(hecEndTime)<=0 && ht.compareTimes(endDate)<=0){
					if (ht.compareTimes(hecStartTime) >= 0 && DssPluginCore.months.contains(ht.month())){
						int valueIndex = (ht.year() - hecStartTime
								.year())
								* 12
								+ (ht.month() - hecStartTime
										.month());
						sum += tsc.values[valueIndex];
						count++;
						inRange=true;
					}
					month = ht.month() + 1;
					if (month == 13) {
						month = 1;
						year = ht.year() + 1;
					} else {
						year = ht.year();
					}
					ht.setDate(TimeOperation.getMonthText(month)+
							+ year);
				}
				if (inRange) {
					result=sum/count;
				}
			} catch (HecMathException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}	
	
	public String retrieveLongTermAverage(int pi, String date, int di, boolean isCFS, Object baseValue){
		
		String result = "";
		
		ArrayList<HashMap<String, Double>> termAverage;
		if (isCFS){
			termAverage=DssPluginCore.geoLongTermAverageDataCFS.get(pi);
		}else{
			termAverage=DssPluginCore.geoLongTermAverageDataTAF.get(pi);
		}
		
		ArrayList<String[]> pathParts = new ArrayList<String[]>();
		CondensedReference found = null;
		boolean isStorage=false;
		if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
		String value = "";			
		HashMap<String, Double> altAverage = termAverage.get(di);
		if (altAverage.containsKey(name)){
			double data = altAverage.get(name);
			boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
					: false);
			String units;
			if (isCFS){
				units=DssPluginCore.geoSchematicVariableUnitsCFS[di].get(name);
			}else{
				units=DssPluginCore.geoSchematicVariableUnitsTAF[di].get(name);
			}
			if (data>100000000) {
				value="N/A";
			} else {
				value = data + " " + units;
			}
			if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
				String[] altFields = value.split("\\s");
				try {
					double baseVal = Double.parseDouble((String)baseValue);
					double altVal = Double.parseDouble(altFields[0]);
					result = (altVal - baseVal) + " " + altFields[1];
				} catch (NumberFormatException nfe) {
					result=value;
				}
			}else {
				result=value;
			}
		}else {
			boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
					: false);
			String units;
			if (isCFS){
				units=DssPluginCore.geoSchematicVariableUnitsCFS[di].get(name);
			}else{
				units=DssPluginCore.geoSchematicVariableUnitsTAF[di].get(name);
			}
			Double data=calculateLongTermAverage(date, di, isCFS, altAverage);
			if (data==null){
				result = "";
			}else{
				result=String.valueOf(data)+ " " + units;
			}
		}

		return result;
	}
	
	public String retrieveLongTermAverageSelectedMonths(String date, int di, boolean isCFS, Object baseValue){
				
		String result="";
		
		Double data = calculateLongTermAverageSelectedMonths(date, di, isCFS);
		if (data==null){
			return result;
		}
		
		boolean isStorage=false;
		if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
		String value = "";			
		boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
					: false);
		String units;
		if (isCFS){
			units=DssPluginCore.geoSchematicVariableUnitsCFS[di].get(name);
		}else{
			units=DssPluginCore.geoSchematicVariableUnitsTAF[di].get(name);
		}
		if (data>100000000) {
			value="N/A";
		} else {
			value = data + " " + units;
		}
		if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
			String[] altFields = value.split("\\s");
			try {
				double baseVal = Double.parseDouble((String)baseValue);
				double altVal = Double.parseDouble(altFields[0]);
				result=(altVal - baseVal) + " " + altFields[1];
			} catch (NumberFormatException nfe) {
				result=value;
			}
		}
		return result;
	}
	
	public TimeSeriesContainer unitsConversion(boolean isStorage, TimeSeriesContainer dataSet, boolean force, int index) {

		if ((DssPluginCore.units.equals("TAF") || force || isStorage)
				&& dataSet.units.equals("CFS")) {
			// CB dataSet = adjustMonthlyData(dataSet, 1.9834631 / 1000.);
			dataSet = adjustMonthlyData(dataSet, true); // CB added constant IV FACTOR
			// so do not need to pass a
			// factor
			dataSet.units="TAF";
			DssPluginCore.geoSchematicVariableUnitsTAF[index].put(name, "TAF");

		} else if (DssPluginCore.units.equals("CFS") && dataSet.units.equals("TAF")
				&& !force && !isStorage) { // CB added section
			dataSet = adjustMonthlyData(dataSet, false); // CB added constant IV
			// FACTOR so do not
			// need to pass a
			// factor
			dataSet.units="CFS";
			DssPluginCore.geoSchematicVariableUnitsCFS[index].put(name, "CFS");
		}
		return dataSet;
	}
	
	public TimeSeriesContainer adjustMonthlyData(TimeSeriesContainer tsc, boolean isCFStoTAF) { // CB added
		// the
		// boolean
		// and
		// made
		// a
		// constant
		// IV
		// factor
		HecTime ht = null;
		try {
			double[] nvalues = new double[tsc.values.length];
			int[] times = tsc.times;
			int ndays = 0;
			ht = new HecTime();
			for (int i = 0; i < times.length; i++) {
				ht.set(times[i]);
				// FIX: subtract 1 min
				ht.add(-1);
				ndays = ht.day();
				/*
				 * HecTime BUG: sometimes ht.day() returns a day later than *
				 * should???? I wasted a lot of time on this!!
				 * 
				 * returns ht.day() == 1, ht.hour() == 0 AS OPPOSED TO ht.day()
				 * == 28,30,31 (depending on month), ht.hour() == 24
				 */

				// if (DEBUG) System.out.println(ht.year()+" "+ht.month()+
				// " "+ht.day()+ht.hour()+" "+ht.minute()+" "+ht.second()+" "+times[i]+"
				// "+values[i]);
				// CB nvalues[i] = tsc.values[i] * ndays * factor;
				if (isCFStoTAF) {
					nvalues[i] = tsc.values[i] * ndays / FACTOR;
				} else {
					nvalues[i] = tsc.values[i] / ndays * FACTOR;
				}
			}
			tsc.values = nvalues;
		} catch (Exception e) {
			System.out.println("Exception adjustMonthlyData: " + e);
		}
		return tsc;
	}
	
	private String truncateAfterDecimal(String value, int i,
			boolean displayUnits) {
		if (value == null) {
			return null;
		}
		String[] fields = value.split("\\s");
		if (fields.length < 2) {
			return value;
		}
		return String.format("%." + i + "f %s", Double.parseDouble(fields[0]),
				displayUnits ? fields[1] : "");
	}
}
