package test.test_heclib7;

import hec.heclib.util.Heclib;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.antlr.runtime.RecognitionException;

import wrimsv2.evaluator.DataTimeSeries;
import wrimsv2.evaluator.DssDataSetFixLength;

public class Test_timestamp extends TestCase {
	
	public void testTimeStamp(){
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
		dds.setStartTime(startDate);
		dds.setUnits("Test");
		dds.setKind("None");
		DataTimeSeries.dvAliasTS.put("Test",dds);
		
		Calendar stCal=Calendar.getInstance();
		stCal.setTime(startDate);
		int sYear = stCal.get(Calendar.YEAR);
		int sMonth = stCal.get(Calendar.MONTH)+1;
		int sDay = stCal.get(Calendar.DAY_OF_MONTH);
		
		System.out.println(sYear+"/"+sMonth+"/"+sDay);
	}
}
