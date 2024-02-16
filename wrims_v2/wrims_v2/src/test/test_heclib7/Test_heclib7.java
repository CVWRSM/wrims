package test.test_heclib7;

import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.heclib.util.Heclib;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.testng.annotations.Test;

import wrimsv2.components.ControlData;
import wrimsv2.evaluator.DataTimeSeries;
import wrimsv2.evaluator.DssDataSetFixLength;
import wrimsv2.evaluator.DssOperation;
import junit.framework.TestCase;

public class Test_heclib7 extends TestCase {
	
	public void testHecLib7Output6Format() throws RecognitionException, IOException, Exception{
		initializeData();
		Heclib.zset("ALLV", "", 6);
		Date now = new Date();
		String dvPath6= "build\\test_data\\test_6_" + String.valueOf(now.getTime()) + ".dss";
		try{
			String outPath = writeData(dvPath6);
			DataContainer dc = readData(dvPath6, outPath);
			assertTrue (dc instanceof TimeSeriesContainer) ;
			TimeSeriesContainer tsc = (TimeSeriesContainer) dc;
			assertEquals(12, tsc.numberValues);
			assertEquals(11484000, tsc.startTime);//31 Oct 1921 2400
			assertEquals(11484000, tsc.getMinutes()[0]);//31 Oct 1921 2400
			assertEquals(11527200, tsc.getMinutes()[1]);//30 Nov 1921 2400
			assertEquals(11656800, tsc.getMinutes()[4]);//28 Feb 1922 2400
		} finally {
			File dvFile = new File(dvPath6); 
			dvFile.delete();
		}
	}
	
	public void testHecLib7Output7Format() throws RecognitionException, IOException, Exception{
		initializeData();
		Heclib.zset("ALLV", "", 7);
		Date now = new Date();
		String dvPath7= "build\\test_data\\test_7_" + String.valueOf(now.getTime()) + ".dss";
		try{
			String outPath = writeData(dvPath7);
			DataContainer dc  = readData(dvPath7, outPath);
			assertTrue (dc instanceof TimeSeriesContainer) ;
			TimeSeriesContainer tsc = (TimeSeriesContainer) dc;
			assertEquals(12, tsc.numberValues);
			assertEquals(11484000, tsc.startTime);//31 Oct 1921 2400
			assertEquals(11484000, tsc.getMinutes()[0]);//31 Oct 1921 2400
			assertEquals(11527200, tsc.getMinutes()[1]);//30 Nov 1921 2400
			assertEquals(11656800, tsc.getMinutes()[4]);//28 Feb 1922 2400
		}finally {
			File dvFile = new File(dvPath7); 
			dvFile.delete();
		}
	}	
	private void initializeData(){
		DssDataSetFixLength dds=new DssDataSetFixLength();
		double[] data=new double[12];
		data[0]=1.0;
		data[1]=2.0;
		data[2]=3.0;
		data[3]=4.0;
		data[4]=5.0;
		data[5]=6.0;
		data[6]=7.0;
		data[7]=8.0;
		data[8]=9.0;
		data[9]=10.0;
		data[10]=11.0;
		data[11]=12.0;
		dds.setData(data);
		dds.setTimeStep("1MON");
		Calendar calendar = Calendar.getInstance();
		calendar.set(1921, 10-1, 31, 24, 0);
		Date startDate = calendar.getTime();
//		Date startDate = new Date(1921-1900, 10-1, 31);
		dds.setStartTime(startDate);
		dds.setUnits("Test");
		dds.setKind("None");
		DataTimeSeries.dvAliasTS.put("Test",dds);
	}
	
	public String writeData(String dvPath) throws Exception{
		String outPath = "";
		HecDss dvDss = null;
		try {
			dvDss = HecDss.open(dvPath);
		
			Set dvAliasSet=DataTimeSeries.dvAliasTS.keySet();
			Iterator iterator = dvAliasSet.iterator();
			while(iterator.hasNext()){
				String dvAliasName=(String)iterator.next();
				DssDataSetFixLength ddsfl=DataTimeSeries.dvAliasTS.get(dvAliasName);
				double[] values=ddsfl.getData();
				String timestep=ddsfl.getTimeStep();
				Date startDate;
				TimeSeriesContainer dc = new TimeSeriesContainer();
				//dd._dataType=DSSUtil.REGULAR_TIME_SERIES;
				dc.type="PER-AVER";
				dc.numberValues=values.length;
				dc.units=ddsfl.getUnits().toUpperCase();
				dc.values = values;
				//boolean storeFlags = false;
				outPath = "/test/"+DssOperation.getTSName(dvAliasName)+
						"/"+ddsfl.getKind()+"//"+ddsfl.getTimeStep()+"/test/";
				dc.setName(outPath);
				Calendar startCalendar=Calendar.getInstance();
				startCalendar.setTime(ddsfl.getStartTime());
				HecTime startHecTime = new HecTime(startCalendar);
				dc.setStartTime(startHecTime);
				dc.setStoreAsDoubles(true);
				dvDss.put(dc);
				dc.values=null;
				dc=null;
				values=null;
			}					
		} finally{
				dvDss.close();
	}
		return outPath;
	}
	
	public DataContainer readData(String dvPath, String fullName) throws Exception{
		HecDss dvDss = null;
		DataContainer tsc;
		try{
			dvDss = HecDss.open(dvPath);
			boolean retrieve_all_values = true;
			tsc = dvDss.get(fullName, retrieve_all_values);
			return (TimeSeriesContainer)tsc;
		}finally{
			dvDss.close();
		}
	}
	
}
