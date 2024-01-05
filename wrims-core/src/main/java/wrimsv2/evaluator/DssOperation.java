package wrimsv2.evaluator;

import wrimsv2.commondata.wresldata.Alias;
import wrimsv2.commondata.wresldata.Dvar;
import wrimsv2.commondata.wresldata.StudyDataSet;
import wrimsv2.commondata.wresldata.Svar;
import wrimsv2.commondata.wresldata.Timeseries;
import wrimsv2.components.ControlData;
import wrimsv2.components.Error;
import wrimsv2.hdf5.HDF5Reader;
import wrimsv2.parallel.ParallelVars;
import wrimsv2.tools.General;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;
import hec.heclib.util.doubleArrayContainer;
import hec.io.TimeSeriesContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryo.util.IdentityMap.Values;
import com.google.common.primitives.Doubles;

public class DssOperation {
	
	private static int savedStartMonthlyTimestep;
	private static int savedEndMonthlyTimestep;
	private static int totalSavedMonthlyTimestep;
	private static int savedStartDailyTimestep;
	private static int savedEndDailyTimestep;
	private static int totalSavedDailyTimestep;
	
	public static boolean getSVTimeseries(String name, String file, String timeStep, int svFileIndex){
		ControlData.timeStep=timeStep;
		ControlData.partE=timeStep;
		Timeseries ts=ControlData.allTsMap.get(name);
		String partC=ts.kind;
		HecTimeSeries hts=getDataForSvar(ControlData.partA.toUpperCase(),ts.dssBPart.toUpperCase(),partC.toUpperCase(),"",timeStep.toUpperCase(), ControlData.svDvPartF.toUpperCase(), svFileIndex, file);
		
		if (hts==null){
			return false;
		}
		if (!hts.units().toUpperCase().equals(ts.units.toUpperCase())){
			return false;
		}
		/*
		if (!(hts instanceof RegularTimeSeries)){
			return false;
		}
		*/
		//RegularTimeSeries rts=(RegularTimeSeries)ds;
		
		DssDataSet dds= new DssDataSet();
		ArrayList<Double> dataArray= new ArrayList<Double>();
		HecTime startDate=hts.startTime();
		int year=startDate.year();
		int month=startDate.month();
		int day = startDate.day();
		doubleArrayContainer values=new doubleArrayContainer();
		hts.getData(values);
		if (ts.units.equals("taf") && ts.convertToUnits.equals("cfs")){
			int i=0;
			for (double dataEntry :  values.array){
				if (dataEntry==-901.0){
					dataArray.add(-901.0);
				}else if (dataEntry==-902.0){
					dataArray.add(-902.0);
				}else{
					ParallelVars prvs = TimeOperation.findTime(i, year, month, day);
					double dataEntryValue=dataEntry*Evaluation.tafcfs("taf_cfs", prvs);
					dataArray.add(dataEntryValue);
				}
				i=i+1;
			}
		}else if (ts.units.equals("cfs") && ts.convertToUnits.equals("taf")){
			int i=0;
			for (double dataEntry :  values.array){
				if (dataEntry==-901.0){
					dataArray.add(-901.0);
				}else if (dataEntry==-902.0){
					dataArray.add(-902.0);
				}else{
					ParallelVars prvs = TimeOperation.findTime(i, year, month, day);
					double dataEntryValue=dataEntry*Evaluation.tafcfs("cfs_taf", prvs);
					dataArray.add(dataEntryValue);
				}
				i=i+1;
			}
		}else{
			for (double dataEntry :  values.array){
				dataArray.add(dataEntry);
			}
		}
		dds.setUnits(ts.units);
		dds.setConvertToUnits(ts.convertToUnits);
		dds.setKind(partC);
        dds.setData(dataArray);
        dds.setTimeStep(timeStep);
        Date startDateJ=new Date(year-1900, month-1, day);
        dds.setStartTime(startDateJ);
        dds.setFromDssFile(true);
        dds.generateStudyStartIndex();
        String entryNameTS=entryNameTS(name, timeStep);
        DataTimeSeries.svTS.put(entryNameTS, dds);
		return true;
	}
	
	public static boolean getSVInitTimeseries(String name){
		
		Timeseries ts=ControlData.currTsMap.get(name);
		if (ts==null){
			return false;
		}
		String partC=ts.kind;
		DataSet ds=getDataForInitial(regularExp(ControlData.partA),regularExp(ts.dssBPart),regularExp(partC),"",regularExp(ControlData.partE), regularExp(ControlData.initPartF));
		
		if (ds==null){
			return false;
		}
		if (!ds.getAttributes().getYUnits().toUpperCase().equals(ts.units.toUpperCase())){
			return false;
		}
		if (!(ds instanceof RegularTimeSeries)){
			return false;
		}
		RegularTimeSeries rts=(RegularTimeSeries)ds;
		DssDataSet dds= new DssDataSet();
		ArrayList<Double> dataArray= new ArrayList<Double>();
		Date startDate=rts.getStartTime().getDate();
		if (ts.units.equals("taf") && ts.convertToUnits.equals("cfs")){
			ParallelVars prvs = new ParallelVars(); 
			prvs.dataYear=startDate.getYear()+1900;
			prvs.dataMonth=startDate.getMonth()+1;
			prvs.dataDay=startDate.getDate();
			int i=0;
			for (double dataEntry :  rts.getYArray()){
				if (dataEntry==-901.0){
					dataArray.add(-901.0);
				}else if (dataEntry==-902.0){
					dataArray.add(-902.0);
				}else{
					TimeOperation.findTime(i);
					dataArray.add(dataEntry*Evaluation.tafcfs("taf_cfs", prvs));
				}
				i=i+1;
			}
		}else if (ts.units.equals("cfs") && ts.convertToUnits.equals("taf")){
			ParallelVars prvs = new ParallelVars();
			prvs.dataYear=startDate.getYear()+1900;
			prvs.dataMonth=startDate.getMonth()+1;
			prvs.dataDay=startDate.getDate();
			int i=0;
			for (double dataEntry :  rts.getYArray()){
				if (dataEntry==-901.0){
					dataArray.add(-901.0);
				}else if (dataEntry==-902.0){
					dataArray.add(-902.0);
				}else{
					TimeOperation.findTime(i);
					dataArray.add(dataEntry*Evaluation.tafcfs("cfs_taf", prvs));
				}
				i=i+1;
			}
		}else{
			for (double dataEntry :  rts.getYArray()){
				dataArray.add(dataEntry);
			}
		}
		dds.setUnits(ts.units);
		dds.setConvertToUnits(ts.convertToUnits);
		dds.setKind(ts.kind);
        dds.setData(dataArray);
        dds.setTimeStep(rts.getTimeInterval().toString());
        dds.setStartTime(startDate);
        String entryNameTS=entryNameTS(name, ControlData.timeStep);
        DataTimeSeries.svInit.put(entryNameTS, dds);
		return true;
	}
	
	public static String regularExp(String part){
		return "^"+part+"$";
	}
	
	public static boolean getDVAliasInitTimeseries(String name){		
		String units;
		String partC;
		if (ControlData.currDvMap.containsKey(name)){
			Dvar dvar=ControlData.currDvMap.get(name);
			partC=dvar.kind;
			units=dvar.units;
		}else{
			Alias alias=ControlData.currAliasMap.get(name);
			partC=alias.kind;
			units=alias.units;
		}
		
		DataSet ds=getDataForInitial(regularExp(ControlData.partA),regularExp(name),regularExp(partC),"",regularExp(ControlData.partE), regularExp(ControlData.initPartF));
		if (ds==null){
			Error.addEvaluationError("Intial data of "+name+" in dss file doesn't exist." );
			return false;
		}
		if (!units.toUpperCase().equals(ds.getAttributes().getYUnits().toUpperCase())){
			return false;
		}
		if (!(ds instanceof RegularTimeSeries)){
			Error.addEvaluationError("Intial data of "+name+" in dss file is not a regular timeseries." );
			return false;
		}
		RegularTimeSeries rts=(RegularTimeSeries)ds;
		DssDataSet dds= new DssDataSet();
		ArrayList<Double> dataArray= new ArrayList<Double>();
		for (double dataEntry :  rts.getYArray()){
			dataArray.add(dataEntry);
		}
        dds.setData(dataArray);
        dds.setUnits(units);
        dds.setKind(partC);
        String timeStep=rts.getTimeInterval().toString();
        dds.setTimeStep(timeStep);
        dds.setStartTime(rts.getStartTime().getDate());
        String entryNameTS=entryNameTS(name, timeStep);
        DataTimeSeries.dvAliasInit.put(entryNameTS, dds);
		return true;
	}
	
    public static DataSet getDataFor(String file, String apart, String bpart, String cpart, String dpart, String epart, String fpart){
        dpart = "01JAN1920"; //To Do: this method may be removed
        DataReference ref = DSSUtil.createDataReference("local",file,Pathname.createPathname(new String[]{apart, bpart, cpart, dpart, epart, fpart}));
        return ref.getData();
    }
    
    public static synchronized HecTimeSeries getDataForSvar(String apart, String bpart, String cpart, String dpart, String epart, String fpart, int svFileIndex, String filename){
    	//DataReference[] refs;
    	String path="/"+apart+"/"+bpart+"/"+cpart+"/"+dpart+"/"+epart+"/"+fpart+"/";
    	HecTimeSeries ts = new HecTimeSeries();
    	if (svFileIndex==1){
    		//refs = ControlData.groupSvar.find(new String[]{apart, bpart, cpart, dpart, epart, fpart});
    		DSSPathname dssPathname = ControlData.cacheSvar.getNominalPathname(path);
            TimeSeriesContainer tsc = new TimeSeriesContainer();
            tsc.fileName = filename;
            tsc.fullName = dssPathname.pathname();
            boolean removeMissing = false;
            ts.read(tsc, removeMissing);
            ts.setUnits(tsc.units);
    	}else{
    		//refs = ControlData.groupSvar2.find(new String[]{apart, bpart, cpart, dpart, epart, fpart});
    		DSSPathname dssPathname = ControlData.cacheSvar2.getNominalPathname(path);
            TimeSeriesContainer tsc = new TimeSeriesContainer();
            tsc.fileName = filename;
            tsc.fullName = dssPathname.pathname();
            boolean removeMissing = false;
            ts.read(tsc, removeMissing);
            ts.setUnits(tsc.units);
    	}
    	/*
        if (refs.length==0){
              return null;
        } else {
              return refs[0].getData();
        }
        */
    	
    	return ts;
    }
    
    public static DataSet getDataForInitial(String apart, String bpart, String cpart, String dpart, String epart, String fpart){
    	DataReference[] refs = ControlData.groupInit.find(new String[]{apart, bpart, cpart, dpart, epart, fpart});
        if (refs.length==0){
              return null;
        } else {
              return refs[0].getData();
        }
    }
	
	public static void writeInitDvarAliasToDSS() {	
		System.out.println("writing initial data for dvar and alias to dv dss");
		Set initSet=DataTimeSeries.dvAliasInit.keySet();
		Iterator iterator = initSet.iterator();
		while(iterator.hasNext()){
			String initName=(String)iterator.next();
			DssDataSet dds=DataTimeSeries.dvAliasInit.get(initName);
			if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(initName), dds.getKind()))){
				ArrayList<Double> data=dds.getData();
				int size=data.size();
				double[] values=new double[size];
				for (int i=0; i<size; i++){
					values[i]=data.get(i);
				}
				String timeStep=dds.getTimeStep();
				DSSData ds = new DSSData();
				Date startDate=dds.getStartTime();
				long startJulmin = TimeFactory.getInstance().createTime(startDate).getTimeInMinutes();
				Date modelStartDate=new Date(ControlData.startYear-1900, ControlData.startMonth, ControlData.startDay);
				ds._dataType=DSSUtil.REGULAR_TIME_SERIES;
				ds._yType="PER-AVER";
				ds._numberRead=TimeOperation.getNumberOfTimestep(startDate, modelStartDate, timeStep);
				ds._yUnits=dds.getUnits().toUpperCase();
				ds._yValues = values;
				boolean storeFlags = false;
				String pathName="/"+ControlData.partA+"/"+getTSName(initName)+"/"+dds.getKind()+"//"+timeStep+"/"+ControlData.svDvPartF+"/";
				ControlData.writer.storeTimeSeriesData(pathName, startJulmin, ds,
						storeFlags);
			}
		}
		System.out.println("initial data for dvar and alias written to dv dss");
	}
	
	public static void writeDVAliasToDSS() {
		long t1 = Calendar.getInstance().getTimeInMillis();
		System.out.println("writing dvar and alias to dv dss");
		Set dvAliasSet=DataTimeSeries.dvAliasTS.keySet();
		Iterator iterator = dvAliasSet.iterator();
		while(iterator.hasNext()){
			String dvAliasName=(String)iterator.next();
			DssDataSetFixLength ddsfl=DataTimeSeries.dvAliasTS.get(dvAliasName);
			if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(dvAliasName), ddsfl.getKind()))){
				double[] values=ddsfl.getData();
				String timestep=ddsfl.getTimeStep();
				int size = values.length;
				int nTimestep = TimeOperation.getNumberOfTimestep(ControlData.memStartDate, ControlData.prevOutputDate, timestep);
				if (nTimestep<0) nTimestep=0;
				double[] values1=new double[size-nTimestep];
				int size1=values1.length;
				for (int i=0; i<size1; i++){
					values1[i]=values[i+nTimestep];
				}
				Date startDate;
				if (timestep.equals("1MON")){
					startDate=TimeOperation.addOneMonth(ControlData.prevOutputDate);
				}else{
					startDate=TimeOperation.addOneDay(ControlData.prevOutputDate);
				}
				DSSData dd = new DSSData();
				dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
				dd._yType="PER-AVER";
				dd._numberRead=size1;
				dd._yUnits=ddsfl.getUnits().toUpperCase();
				dd._yValues = values1;
				boolean storeFlags = false;
				String pathName="/"+ControlData.partA+"/"+getTSName(dvAliasName)+"/"+ddsfl.getKind()+"//"+ddsfl.getTimeStep()+"/"+ControlData.svDvPartF+"/";
				String startDateStr=TimeOperation.dssTimeEndDay(startDate.getYear()+1900, startDate.getMonth()+1, startDate.getDate());
				long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
				ControlData.writer.storeTimeSeriesData(pathName, startJulmin, dd,
						storeFlags);
				dd._yValues=null;
				dd=null;
				values1=null;
				values=null;
			}
		}
		System.gc();
		
		if (ControlData.isOutputCycle) writeDVAliasCycleDataToDSS();
		System.out.println("dvar and alias written to dv dss");
		long t2 = Calendar.getInstance().getTimeInMillis();
		ControlData.t_writeDss=ControlData.t_writeDss+(int) (t2-t1);
	}
	
	public static void writeDVAliasCycleDataToDSS() {
		int totalCycleNumber=ControlData.currStudyDataSet.getModelList().size();
		
		for (int i=0; i<totalCycleNumber; i++){
			int cycleI=i+1;
			String strCycleI=cycleI+"";
			if (General.isSelectedCycleOutput(strCycleI)){
				HashMap<String, DssDataSetFixLength> dvAliasTSCycle = DataTimeSeries.dvAliasTSCycles.get(i);
				Set dvAliasSet=dvAliasTSCycle.keySet();
				Iterator iterator = dvAliasSet.iterator();
				while(iterator.hasNext()){
					String dvAliasName=(String)iterator.next();
					DssDataSetFixLength ddsfl=dvAliasTSCycle.get(dvAliasName);
					if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(dvAliasName), ddsfl.getKind()))){
						double[] values=ddsfl.getData();
						String timestep=ddsfl.getTimeStep();
						int size = values.length;
						int nTimestep = TimeOperation.getNumberOfTimestep(ControlData.memStartDate, ControlData.prevOutputDate, timestep);
						if (nTimestep<0) nTimestep=0;
						double[] values1=new double[size-nTimestep];
						int size1=values1.length;
						for (int j=0; j<size1; j++){
							values1[j]=values[j+nTimestep];
						}
						Date startDate;
						if (timestep.equals("1MON")){
							startDate=TimeOperation.addOneMonth(ControlData.prevOutputDate);
						}else{
							startDate=TimeOperation.addOneDay(ControlData.prevOutputDate);
						}
						DSSData dd = new DSSData();
						dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
						dd._yType="PER-AVER";
						dd._numberRead=size1;
						dd._yUnits=ddsfl.getUnits().toUpperCase();
						dd._yValues = values1;
						boolean storeFlags = false;
						String pathName="/"+ControlData.partA+"_Cycle"+cycleI+"/"+getTSName(dvAliasName)+"/"+ddsfl.getKind()+"//"+ddsfl.getTimeStep()+"/"+ControlData.svDvPartF+"/";
						String startDateStr=TimeOperation.dssTimeEndDay(startDate.getYear()+1900, startDate.getMonth()+1, startDate.getDate());
						long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
						ControlData.writer.storeTimeSeriesData(pathName, startJulmin, dd,
							storeFlags);
						dd._yValues=null;
						dd=null;
						values1=null;
						values=null;
					}
				}
			}
		}
		System.gc();
	}
	
	public static String entryNameTS(String name, String timeStep){
		return name+"@"+timeStep;
	}
	
	public static String getTSName(String entryNameTS){
		String[] entry=entryNameTS.split("@");
		return entry[0];
	}
	
	public static void saveInitialData(DSSDataWriter writer, String fileName){
		System.out.println("write initial data for dvar and alias to "+fileName);
		Set initSet=DataTimeSeries.dvAliasInit.keySet();
		Iterator iterator = initSet.iterator();
		while(iterator.hasNext()){
			String initName=(String)iterator.next();
			DssDataSet dds=DataTimeSeries.dvAliasInit.get(initName);
			ArrayList<Double> data=dds.getData();
			int size=data.size();
			double[] values=new double[size];
			for (int i=0; i<size; i++){
				values[i]=data.get(i);
			}
			String timeStep=dds.getTimeStep();
			DSSData ds = new DSSData();
			Date startDate=dds.getStartTime();
			long startJulmin = TimeFactory.getInstance().createTime(startDate).getTimeInMinutes();
			Date modelStartDate=new Date(ControlData.startYear-1900, ControlData.startMonth, ControlData.startDay);
			ds._dataType=DSSUtil.REGULAR_TIME_SERIES;
			ds._yType="PER-AVER";
			ds._numberRead=TimeOperation.getNumberOfTimestep(startDate, modelStartDate, timeStep);
			ds._yUnits=dds.getUnits().toUpperCase();
			ds._yValues = values;
			boolean storeFlags = false;
			String pathName="/"+ControlData.partA+"/"+getTSName(initName)+"/"+dds.getKind()+"//"+timeStep+"/"+ControlData.svDvPartF+"/";
			writer.storeTimeSeriesData(pathName, startJulmin, ds,
				storeFlags);
		}
	}
	
	public static void saveDVInitialData(DSSDataWriter writer, String fileName){
		System.out.println("write initial data for dvar and alias to "+fileName);
		Set initSet=DataTimeSeries.dvAliasInit.keySet();
		Iterator iterator = initSet.iterator();
		while(iterator.hasNext()){
			String initName=(String)iterator.next();
			DssDataSet dds=DataTimeSeries.dvAliasInit.get(initName);
			if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(initName), dds.getKind()))){
				ArrayList<Double> data=dds.getData();
				int size=data.size();
				double[] values=new double[size];
				for (int i=0; i<size; i++){
					values[i]=data.get(i);
				}
				String timeStep=dds.getTimeStep();
				DSSData ds = new DSSData();
				Date startDate=dds.getStartTime();
				long startJulmin = TimeFactory.getInstance().createTime(startDate).getTimeInMinutes();
				Date modelStartDate=new Date(ControlData.startYear-1900, ControlData.startMonth, ControlData.startDay);
				ds._dataType=DSSUtil.REGULAR_TIME_SERIES;
				ds._yType="PER-AVER";
				ds._numberRead=TimeOperation.getNumberOfTimestep(startDate, modelStartDate, timeStep);
				ds._yUnits=dds.getUnits().toUpperCase();
				ds._yValues = values;
				boolean storeFlags = false;
				String pathName="/"+ControlData.partA+"/"+getTSName(initName)+"/"+dds.getKind()+"//"+timeStep+"/"+ControlData.initPartF+"/";
				writer.storeTimeSeriesData(pathName, startJulmin, ds,
						storeFlags);
			}
		}
		System.out.println("Initial file saved.");
	}
	
	public static void saveSVInitialData(DSSDataWriter writer, String fileName){
		System.out.println("write initial data for svar to "+fileName);
		Set initSet=DataTimeSeries.svInit.keySet();
		Iterator iterator = initSet.iterator();
		Map<String, Timeseries> allTsMap = ControlData.allTsMap;
		while(iterator.hasNext()){
			String initName=(String)iterator.next();
			DssDataSet dds=DataTimeSeries.svInit.get(initName);
			ArrayList<Double> data=dds.getData();
			String ctu = "none";
			String units="none";
			if (allTsMap.containsKey(initName)){
				Timeseries ts=allTsMap.get(initName);
				units = ts.units;
				ctu=ts.convertToUnits;
			}
			ArrayList<Double> values=dds.getData();
			DSSData dd = new DSSData();
			dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
			dd._yType="PER-AVER";
			int size=values.size();
			dd._numberRead=size;
			dd._yUnits=dds.getUnits().toUpperCase();
			dd._yValues=new double[size];
			Date startDate=dds.getStartTime();
			startDate.setTime(startDate.getTime()-1*24*60*60);
			int year=startDate.getYear()+1900;
			int month=startDate.getMonth()+1;
			int day=startDate.getDate();
			String startDateStr=TimeOperation.dssTimeEndDay(year, month, day);
			long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
			if (units.equals("taf") && ctu.equals("cfs")){			
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					if (value == null){
						dd._yValues[i]=-901.0;
					}else{
						if (value == -901.0 || value == -902.0){
							dd._yValues[i]=value;
						}else{
							ParallelVars prvs=TimeOperation.findTime(i, year, month, day);
							dd._yValues[i]=value/Evaluation.tafcfs("taf_cfs", prvs);
						}
					}
				}
			}else if (units.equals("cfs") && ctu.equals("taf")){			
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					if (value == null){
						dd._yValues[i]=-901.0;
					}else{
						if (value == -901.0 || value == -902.0){
							dd._yValues[i]=value;
						}else{
							ParallelVars prvs=TimeOperation.findTime(i, year, month, day);
							dd._yValues[i]=value/Evaluation.tafcfs("cfs_taf", prvs);
						}
					}
				}
			}else{
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					dd._yValues[i]=value;
				}
			}
			boolean storeFlags = false;
			String pathName="/"+ControlData.partA+"/"+initName+"/"+dds.getKind()+"//"+dds.getTimeStep()+"/"+ControlData.initPartF+"/";
			writer.storeTimeSeriesData(pathName, startJulmin, dd, storeFlags);
		}
		System.out.println("Initial file saved.");
	}
	
	public static void saveDvarData(DSSDataWriter writer, String fileName){
		System.out.println("write dvar and alias to "+fileName);
		
		savedEndMonthlyTimestep=savedTimeStep("1MON", ControlData.currYear, ControlData.currMonth, ControlData.currDay);
		savedEndDailyTimestep=savedTimeStep("1DAY", ControlData.currYear, ControlData.currMonth, ControlData.currDay);
		
		Set dvAliasSet=DataTimeSeries.dvAliasTS.keySet();
		Iterator iterator = dvAliasSet.iterator();
		while(iterator.hasNext()){
			String dvAliasName=(String)iterator.next();
			DssDataSetFixLength ddsfl=DataTimeSeries.dvAliasTS.get(dvAliasName);
			if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(dvAliasName), ddsfl.getKind()))){
				String timestep = ddsfl.getTimeStep();
				double[] values=ddsfl.getData();
				double[] values1;
				int size = values.length;
				int nTimestep = TimeOperation.getNumberOfTimestep(ControlData.memStartDate, ControlData.prevOutputDate, timestep);
				if (nTimestep<0) nTimestep=0;
				if (timestep.equals("1MON")){
					int size1 = savedEndMonthlyTimestep+1-nTimestep;
					values1=new double[size1];
					for (int i=0; i<size1; i++){
						values1[i]=values[i+nTimestep];
					}
				}else{
					int size1 = savedEndDailyTimestep+1-nTimestep;
					values1=new double[size1];
					for (int i=0; i<size1; i++){
						values1[i]=values[i+nTimestep];
					}
				}
				Date startDate;
				if (timestep.equals("1MON")){
					startDate=TimeOperation.addOneMonth(ControlData.prevOutputDate);
				}else{
					startDate=TimeOperation.addOneDay(ControlData.prevOutputDate);
				}
				DSSData dd = new DSSData();
				dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
				dd._yType="PER-AVER";
				dd._numberRead= values1.length;
				dd._yUnits=ddsfl.getUnits().toUpperCase();
				dd._yValues = values1;
				boolean storeFlags = false;
				String pathName="/"+ControlData.partA+"/"+getTSName(dvAliasName)+"/"+ddsfl.getKind()+"//"+timestep+"/"+ControlData.svDvPartF+"/";
				String startDateStr=TimeOperation.dssTimeEndDay(startDate.getYear()+1900, startDate.getMonth()+1, startDate.getDate());
				long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
				writer.storeTimeSeriesData(pathName, startJulmin, dd,
						storeFlags);
				dd._yValues=null;
				values=null;
			}
		}
		System.gc();
		
		if (ControlData.isOutputCycle) saveDvarCycleData(writer, fileName);
		
		System.out.println("Dvar file saved.");
	}
	
	public static void saveDvarCycleData(DSSDataWriter writer, String fileName){
		
		savedStartMonthlyTimestep=savedTimeStep("1MON", ControlData.cycleDataStartYear, ControlData.cycleDataStartMonth, ControlData.cycleDataStartDay);
		savedStartDailyTimestep=savedTimeStep("1DAY", ControlData.cycleDataStartYear, ControlData.cycleDataStartMonth, ControlData.cycleDataStartDay);
		totalSavedMonthlyTimestep=savedEndMonthlyTimestep-savedStartMonthlyTimestep+1;
		totalSavedDailyTimestep=savedEndDailyTimestep-savedStartDailyTimestep+1;
		
		int totalCycleNumber=ControlData.currStudyDataSet.getModelList().size();
		
		for (int i=0; i<totalCycleNumber; i++){
			int cycleI=i+1;
			String strCycleI=cycleI+"";
			if (General.isSelectedCycleOutput(strCycleI)){
				HashMap<String, DssDataSetFixLength> dvAliasTSCycle = DataTimeSeries.dvAliasTSCycles.get(i);
				Set dvAliasSet=dvAliasTSCycle.keySet();
				Iterator iterator = dvAliasSet.iterator();
				while(iterator.hasNext()){
					String dvAliasName=(String)iterator.next();
					DssDataSetFixLength ddsfl=dvAliasTSCycle.get(dvAliasName);
					if (ControlData.outputType !=0 || (ControlData.outputType ==0 && ControlData.ovOption ==0) || (ControlData.ovOption !=0 && isToWrite(getTSName(dvAliasName), ddsfl.getKind()))){
						String timestep=ddsfl.getTimeStep();
						double[] values=ddsfl.getData();
						double[] modValues;
						if (timestep.equals("1MON")){
							modValues=new double[totalSavedMonthlyTimestep];
							for (int j=savedStartMonthlyTimestep; j<=savedEndMonthlyTimestep; j++){
								modValues[j-savedStartMonthlyTimestep]=values[j];
							}
						}else{
							modValues=new double[totalSavedDailyTimestep];
							for (int j=savedStartDailyTimestep; j<=savedEndDailyTimestep; j++){
								modValues[j-savedStartDailyTimestep]=values[j];
							}
						}
						DSSData dd = new DSSData();
						dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
						dd._yType="PER-AVER";
						dd._numberRead=modValues.length;
						dd._yUnits=ddsfl.getUnits().toUpperCase();
						dd._yValues = modValues;
						boolean storeFlags = false;
						String pathName="/"+ControlData.partA+"_Cycle"+cycleI+"/"+getTSName(dvAliasName)+"/"+ddsfl.getKind()+"//"+timestep+"/"+ControlData.svDvPartF+"/";
						//Date startDate=ddsfl.getStartTime();
						Date startDate=new Date(ControlData.memStartYear-1900, ControlData.memStartMonth-1, ControlData.memStartDay);
						String startDateStr=TimeOperation.dssTimeEndDay(startDate.getYear()+1900, startDate.getMonth()+1, startDate.getDate());
						long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
						writer.storeTimeSeriesData(pathName, startJulmin, dd,
								storeFlags);
						dd._yValues=null;
						values=null;
					}
				}
			}
		}
		System.gc();
	}
	
	public static void saveSvarTSData(DSSDataWriter writer, String fileName){
		System.out.println("write svar timeseries to "+fileName);
		Set svTsSet=DataTimeSeries.svTS.keySet();
		Iterator iterator = svTsSet.iterator();
		Map<String, Timeseries> allTsMap = ControlData.allTsMap;
		while(iterator.hasNext()){
			String svTsName=(String)iterator.next();
			String svName=getTSName(svTsName);
			String ctu = "none";
			String units="none";
			if (allTsMap.containsKey(svName)){
				Timeseries ts=allTsMap.get(svName);
				units = ts.units;
				ctu=ts.convertToUnits;
			}
			DssDataSet dds=DataTimeSeries.svTS.get(svTsName);
			ArrayList<Double> values=dds.getData();
			DSSData dd = new DSSData();
			dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
			dd._yType="PER-AVER";
			int size=values.size();
			dd._numberRead=size;
			dd._yUnits=dds.getUnits().toUpperCase();
			dd._yValues=new double[size];
			Date startDate=dds.getStartTime();
			startDate.setTime(startDate.getTime()-1*24*60*60);
			int year=startDate.getYear()+1900;
			int month=startDate.getMonth()+1;
			int day=startDate.getDate();
			String startDateStr=TimeOperation.dssTimeEndDay(year, month, day);
			long startJulmin = TimeFactory.getInstance().createTime(startDateStr).getTimeInMinutes();
			if (units.equals("taf") && ctu.equals("cfs")){			
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					if (value == null){
						dd._yValues[i]=-901.0;
					}else{
						if (value == -901.0 || value == -902.0){
							dd._yValues[i]=value;
						}else{
							ParallelVars prvs=TimeOperation.findTime(i, year, month, day);
							dd._yValues[i]=value/Evaluation.tafcfs("taf_cfs", prvs);
						}
					}
				}
			}else if (units.equals("cfs") && ctu.equals("taf")){			
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					if (value == null){
						dd._yValues[i]=-901.0;
					}else{
						if (value == -901.0 || value == -902.0){
							dd._yValues[i]=value;
						}else{
							ParallelVars prvs=TimeOperation.findTime(i, year, month, day);
							dd._yValues[i]=value/Evaluation.tafcfs("cfs_taf", prvs);
						}
					}
				}
			}else{
				for (int i=0; i<size; i++){
					Double value=values.get(i);
					dd._yValues[i]=value;
				}
			}
			boolean storeFlags = false;
			String pathName="/"+ControlData.partA+"/"+svName+"/"+dds.getKind()+"//"+dds.getTimeStep()+"/"+ControlData.svDvPartF+"/";
			writer.storeTimeSeriesData(pathName, startJulmin, dd, storeFlags);
		}
		System.out.println("Svar file saved.");
	}
	
	public static int savedTimeStep(String timeStep, int year, int month, int day){
		if (timeStep.equals("1MON")){
			return (year-ControlData.startYear)*12+(month-ControlData.startMonth);
		}else{
			Date startDate = new Date (ControlData.startYear-1900, ControlData.startMonth-1, ControlData.startDay);
			Date endDate=new Date (year-1900, month-1, day);
			//long startTime=startDate.getTime();
			//long endTime=endDate.getTime();
			//double timestep=(endTime-startTime)/(24*60*60*1000l);
			Calendar c1=Calendar.getInstance();
			c1.setTime(startDate);
			Calendar c2=Calendar.getInstance();
			c2.setTime(endDate);
			long timestep = Duration.between(c1.toInstant(), c2.toInstant()).toDays();
			return (int)timestep;
		}
	}
	
	public static void shiftData(){
		Date outputDate=new Date(ControlData.outputYear-1900, ControlData.outputMonth-1, ControlData.outputDay);
		shiftDvAliasData(ControlData.prevMemDate, ControlData.memStartDate, outputDate);
		shiftDvAliasCycleData(ControlData.prevMemDate, ControlData.memStartDate, outputDate);
	}
	
	public static void shiftDvAliasData(Date prevMemDate, Date memStartDate, Date outputDate){
		Set dvAliasSet=DataTimeSeries.dvAliasTS.keySet();
		Iterator iterator = dvAliasSet.iterator();
		while(iterator.hasNext()){
			String dvAliasName=(String)iterator.next();
			DssDataSetFixLength ddsfl=DataTimeSeries.dvAliasTS.get(dvAliasName);
			double[] values=ddsfl.getData();
			String timestep=ddsfl.getTimeStep();
			int nTimeStep1 = TimeOperation.getNumberOfTimestep(prevMemDate, memStartDate, timestep)-1;
			int nTimeStep2 = TimeOperation.getNumberOfTimestep(prevMemDate, outputDate, timestep)-1;
			int size = nTimeStep2-nTimeStep1+1;
			//double[] values1=new double[values.length];
			for (int i=0; i<size; i++){
				//values1[i]=values[i+nTimeStep1];
				values[i]=values[i+nTimeStep1];
				values[i+nTimeStep1]=-901.0;
			}
			//ddsfl.data=null;
			//values=null;
			//ddsfl.setData(values1);
			ddsfl.setStartTime(memStartDate);
		}
		System.gc();
	}
	
	public static void shiftDvAliasCycleData(Date prevMemDate, Date memStartDate, Date outputDate){
		int totalCycleNumber=ControlData.currStudyDataSet.getModelList().size();
		
		for (int i=0; i<totalCycleNumber; i++){
			int cycleI=i+1;
			String strCycleI=cycleI+"";
			if (General.isSelectedCycleOutput(strCycleI)){
				HashMap<String, DssDataSetFixLength> dvAliasTSCycle = DataTimeSeries.dvAliasTSCycles.get(i);
				Set dvAliasSet=dvAliasTSCycle.keySet();
				Iterator iterator = dvAliasSet.iterator();
				while(iterator.hasNext()){
					String dvAliasName=(String)iterator.next();
					DssDataSetFixLength ddsfl=dvAliasTSCycle.get(dvAliasName);
					double[] values=ddsfl.getData();
					String timestep=ddsfl.getTimeStep();
					int nTimeStep1 = TimeOperation.getNumberOfTimestep(prevMemDate, memStartDate, timestep)-1;
					int nTimeStep2 = TimeOperation.getNumberOfTimestep(prevMemDate, outputDate, timestep)-1;
					int size = nTimeStep2-nTimeStep1+1;
					//double[] values1=new double[values.length];
					for (int j=0; j<size; j++){
						//values1[j]=values[j+nTimeStep1];
						values[j]=values[j+nTimeStep1];
					}
					//ddsfl.data=null;
					//values=null;
					//ddsfl.setData(values1);
					ddsfl.setStartTime(memStartDate);
				}
			}
		}
		System.gc();
	}
	
	public static boolean isToWrite(String name, String kind){
		boolean isToWrite=false;
		String nameUp=name.toUpperCase();
		if (ControlData.ovPartBC.containsKey(nameUp)){
			if (ControlData.ovPartBC.get(nameUp).equals(kind.toUpperCase())){
				isToWrite = true;
			}
		}
		return isToWrite;
	}
}
