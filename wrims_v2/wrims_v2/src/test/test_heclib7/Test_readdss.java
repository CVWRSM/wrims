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
		HecDss dvDss;
		try{
			dvDss = HecDss.open(dvPath);
			HecDssCatalog catalog = new HecDssCatalog(dvPath);
	        CondensedReferenceCache cacheSvar = CondensedReferenceCacheAndRead.createCondensedCache(dvPath, "*");
			HecTimeSeries ts = new HecTimeSeries();
	    	DSSPathname dssPathname;
	    	dssPathname = cacheSvar.getNominalPathname(path);
	    	if (dssPathname==null){
	    		System.err.println("no data exisits");
	    	}else{
	    		TimeSeriesContainer tsc = new TimeSeriesContainer();
	            tsc.fileName = dvPath;
	            tsc.fullName = dssPathname.pathname();
	            boolean removeMissing = false;
	            ts.read(tsc, removeMissing);
	            ts.setUnits(tsc.units);
	            HecTime startTime=ts.startTime();
	    		int year=startTime.year();
	    		int month=startTime.month();
	    		int day = startTime.day();
	    		System.out.println("YUBA_TRANS start date: " + year+"/"+month+"/"+day);
	    		doubleArrayContainer values=new doubleArrayContainer();
	    		ts.getData(values);
	    		System.out.println("YUBA_TRANS number of values: " + ts.numberValues());
	    		System.out.println("YUBA_TRANS number of values: " + values.length);
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
			dvDss.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
