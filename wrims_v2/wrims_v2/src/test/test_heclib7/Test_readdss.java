package test.test_heclib7;

import wrimsv2.components.ControlData;
import wrimsv2.components.FilePaths;
import wrimsv2.evaluator.CondensedReferenceCacheAndRead;
import wrimsv2.evaluator.CondensedReferenceCacheAndRead.CondensedReferenceCache;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDss;
import hec.heclib.dss.HecDssCatalog;
import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;
import hec.heclib.util.doubleArrayContainer;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;
import junit.framework.TestCase;

public class Test_readdss extends TestCase {
	
	public void test_readdss(){
		String dvPath="C:\\testHecLib7\\CS3L2020SV_SJWadj_LYRA.dss";
		String path="/CALSIM/YUBA_TRANS/FLOW-INFLOW//1MON/L2020A/";
		try{
	        CondensedReferenceCache cacheSvar = CondensedReferenceCacheAndRead.createCondensedCache(dvPath, "*");
			TimeSeriesContainer tsc = cacheSvar.readFullRecord(path);
	    	if (tsc==null){
	    		System.err.println("no data exisits");
	    	}else{
	            HecTime startTime=tsc.getStartTime();
	    		int year=startTime.year();
	    		int month=startTime.month();
	    		int day = startTime.day();
	    		System.out.println("YUBA_TRANS start date: " + year+"/"+month+"/"+day);
	    		System.out.println("YUBA_TRANS number of values: " + tsc.getNumberValues());
	    		System.out.println("YUBA_TRANS number of values: " + tsc.getValues().length);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void test_readdss1(){
		String dvPath="C:\\testHecLib7\\CS3L2020SV_SJWadj_LYRA.dss";
		String path="/CALSIM/YUBA_TRANS/FLOW-INFLOW//1MON/L2020A/";
		HecDss dvDss;
		TimeSeriesContainer tsc;
		try{
			dvDss = HecDss.open(dvPath);
			tsc = (TimeSeriesContainer)dvDss.get(path, true);
			System.out.println("YUBA_TRANS number of values from method 2 reading: " + tsc.numberValues);
			System.out.println("YUBA_TRANS number of values from method 2 reading: " + tsc.values.length);
			dvDss.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
