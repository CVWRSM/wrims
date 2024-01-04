package wrims.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import vista.db.dss.DSSUtil;
import vista.set.DataSetAttr;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import wrimsv2.evaluator.TimeOperation;

public class testDSSread2 {

	public static int beginYear = 1921;
	public static int beginMonth = 10;
	public static int endYear = 1923;
	public static int endMonth = 8;
	public static String partA = "CALSIM";
	public static String partE = "1MON";
	public static String partF = "CALSIM30_10";
	public static String svDssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
	public static String dvDssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\Version134_052312_WRIMS050912DV.DSS";
	public static String excelFilePath = "OperationsControl_CS3_version134.xlsm";
	public static String readSheetName = "DSS Group";
	public static String writeSheetName = "Data";
	
	
	/// param for excel 
	// write date
	//static int timeRowIndex = 12;	
	static int dvTimeColIndex = 5;
	static int svTimeColIndex = 1;
	
	// SV input
	static int svRowIndex_partBC = 6;
	static int svColIndex_partB = 3;
	static int svColIndex_partC = 4;

	// DV input
	static int dvRowIndex_partBC = 18;
	static int dvColIndex_partB = 3;
	static int dvColIndex_partC = 4;
	
	// SV data
	//static int svDataColIndex = svTimeColIndex + 1;	
	//static int svDataRowIndex = timeRowIndex;
	
	// DV data
	//static int dvDataColIndex = dvTimeColIndex + 1;	
	//static int dvDataRowIndex = timeRowIndex;		
	
	// time window	
	private static final String _startHour = "2400";
	private static final String _endHour = "2400";
	private static String _startTime = "";
	private static String _endTime = "";	
	private static String _startDate = "";
	private static String _endDate = "";	
	private static String[] _dateList = null;

	
	
	public static void main(String[] args) throws IOException {
		
		
		writeDssToOperationsControlExcel ();
		
	}

	
	public static void writeDssToOperationsControlExcel () {
		
		
		Workbook wb = ExcelUtil.getWorkbook(excelFilePath);
		
		findTimeWindow();
		
		wb = writeDssToExcelWorkBook(wb,svDssFilePath, svColIndex_partB, svColIndex_partC, svRowIndex_partBC, svTimeColIndex);
		wb = writeDssToExcelWorkBook(wb,dvDssFilePath, dvColIndex_partB, dvColIndex_partC, dvRowIndex_partBC, dvTimeColIndex);

		
		ExcelUtil.writeWorkbookToFile(wb, excelFilePath);
	
		System.out.println("finish");
		
	}
	
	public static Workbook writeDssToExcelWorkBook(Workbook wb, String dssFilePath, int colIndex_partB, int colIndex_partC, int rowIndex_partBC, int colIndex_time){
		
		int colIndex_data_begin = colIndex_time + 1;
		
		String[] partB = ExcelUtil.readColumn(wb, readSheetName, colIndex_partB, rowIndex_partBC);
		String[] partC = ExcelUtil.readColumn(wb, readSheetName, colIndex_partC, rowIndex_partBC);
			 
		int numberOfSvVars = partB.length;

		for (int i = 0; i < partB.length; i++) {
			System.out.println(partB[i] + ":" + partC[i]);
		}
	
		Group svGroup = ReadDSS.getGroup(DSSUtil.createGroup("local", dssFilePath), _startTime, _endTime, partA, partE, partF);
	    
		RegularTimeSeries rts;
		
		for ( int i=0;i<numberOfSvVars;i++){
			
			rts = ReadDSS.readDSS(svGroup, partB[i], partC[i]);
			
			DataSetAttr dsa = rts.getAttributes();
			
			System.out.println(dsa.getYType());
			System.out.println(dsa.getYUnits());	

			String[] attr = {partA, partB[i], partC[i], "", partE, partF, _startDate, _startHour, _endDate, _endHour, dsa.getYUnits(), dsa.getYType() };
			
			wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, colIndex_data_begin + i, 0, attr);			
			
			double[] d = rts.getYArray();
			
			wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, colIndex_data_begin + i, 12, d);
		}
	
		// write date
		wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, colIndex_time, 12, _dateList);
	
		return wb;

	}


	public static Workbook sub_dv(Workbook wb){
	
		int beginYear = 1921;
		int beginMonth = 10;
		int endYear = 1923;
		int endMonth = 8;
		String partA = "CALSIM";
		String partE = "1MON";
		String partF = "CALSIM30_10";
		String dvDssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\Version134_052312_WRIMS050912DV.DSS";
		String readSheetName = "DSS Group";
		String writeSheetName = "Data";
		
		// write date
		int dvTimeColIndex = 5;
		int timeRowIndex = 12;		
		
		// DV input
		int dvRowIndex = 18;
		int dvColIndex_partB = 3;
		int dvColIndex_partC = 4;
		
		
		// SV data
		int dvDataColIndex = dvTimeColIndex + 1;	
		int dvDataRowIndex = timeRowIndex;		
		

	
		int beginDay = TimeOperation.numberOfDays(beginMonth,beginYear);
		int endDay = TimeOperation.numberOfDays(endMonth,endYear);
		
		String[] dateList = getDateList(beginYear, beginMonth, endYear, endMonth);
		
		System.out.println(Arrays.toString(dateList));
		
		
		String startTime = beginDay+toMonth(beginMonth)+ beginYear + " 2400";
		String endTime =   endDay  +toMonth(endMonth)  + endYear   + " 2400";	
		
		System.out.println(startTime);
		System.out.println(endTime);
		
	
		String[] dvPartB = ExcelUtil.readColumn(wb, readSheetName, dvColIndex_partB, dvRowIndex);
		String[] dvPartC = ExcelUtil.readColumn(wb, readSheetName, dvColIndex_partC, dvRowIndex);
		
		
		
		// dv 
		int numberOfDvVars = dvPartB.length;
		System.out.println("DV");
		for (int i = 0; i < dvPartB.length; i++) {
			System.out.println(dvPartB[i] + ":" + dvPartC[i]);
		}
	
		Group dvGroup = ReadDSS.getGroup(DSSUtil.createGroup("local", dvDssFilePath), startTime, endTime, partA, partE, partF);
	    
		RegularTimeSeries rts_dv;
		
		for ( int i=0;i<numberOfDvVars;i++){
			
			rts_dv = ReadDSS.readDSS(dvGroup, dvPartB[i], dvPartC[i]);
			double[] d = rts_dv.getYArray();
	
			wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, dvDataColIndex + i, dvDataRowIndex, d);
		}
	
		
		wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, dvTimeColIndex, timeRowIndex, dateList);	
		
		
	
		return wb;
	
	}


	public static String[] getDateList(int beginYear, int beginMonth, int endYear, int endMonth){
		
		int totalItems = ( endYear - beginYear )*12 + endMonth - beginMonth + 1;
		
		String[] out = new String[totalItems];
		
		
		int y;
		int m;
		int d;
		
		for ( int i=0; i< totalItems; i++){
			
			y = (i + beginMonth -1 )/12 + beginYear;
			m = (i + beginMonth -1 )%12 + 1;
			d = TimeOperation.numberOfDays(m,y);
			
			out[i] = m+"/"+d+"/"+y;
			
		}
		
		
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, year);
//        c.set(Calendar.MONTH, month);
//        c.set(Calendar.DAY_OF_MONTH, 15);
//        
//		System.out.println(c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		return out;
	}
	
	public static String toMonth(int iMonth){
		
		
		String[] monthList ={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
		
		
		return monthList[iMonth-1];
	}


	public static void sub_copy(){
	
		int beginYear = 1921;
		int beginMonth = 10;
		int endYear = 1923;
		int endMonth = 8;
		String partA = "CALSIM";
		String partE = "1MON";
		String partF = "CALSIM30_10";
		String svDssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
		String dvDssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\Version134_052312_WRIMS050912DV.DSS";
		String excelFilePath = "OperationsControl_CS3_version134.xlsm";
		String readSheetName = "DSS Group";
		String writeSheetName = "Data";
		
		// write date
		int svTimeColIndex = 1;
		int dvTimeColIndex = 5;
		int timeRowIndex = 12;		
	
		
		// SV input
		int svRowIndex = 6;
		int svColIndex_partB = 3;
		int svColIndex_partC = 4;
		
		// SV data
		int svDataColIndex = svTimeColIndex + 1;	
		int svDataRowIndex = timeRowIndex;
		
		
		// DV input
		int dvRowIndex = 18;
		int dvColIndex_partB = 3;
		int dvColIndex_partC = 4;
		
		
		// SV data
		int dvDataColIndex = dvTimeColIndex + 1;	
		int dvDataRowIndex = timeRowIndex;		
		
		
		
		
	
		int beginDay = TimeOperation.numberOfDays(beginMonth,beginYear);
		int endDay = TimeOperation.numberOfDays(endMonth,endYear);
		
		String[] dateList = getDateList(beginYear, beginMonth, endYear, endMonth);
		
		System.out.println(Arrays.toString(dateList));
		
		
		String startTime = beginDay+toMonth(beginMonth)+ beginYear + " 2400";
		String endTime =   endDay  +toMonth(endMonth)  + endYear   + " 2400";	
		
		System.out.println(startTime);
		System.out.println(endTime);
		
		
		Workbook wb = ExcelUtil.getWorkbook(excelFilePath);
	
		
		String[] svPartB = ExcelUtil.readColumn(wb, readSheetName, svColIndex_partB, svRowIndex);
		String[] svPartC = ExcelUtil.readColumn(wb, readSheetName, svColIndex_partC, svRowIndex);
	
		String[] dvPartB = ExcelUtil.readColumn(wb, readSheetName, dvColIndex_partB, dvRowIndex);
		String[] dvPartC = ExcelUtil.readColumn(wb, readSheetName, dvColIndex_partC, dvRowIndex);
		
		
		// sv 
		int numberOfSvVars = svPartB.length;
		System.out.println("SV");
		for (int i = 0; i < svPartB.length; i++) {
			System.out.println(svPartB[i] + ":" + svPartC[i]);
		}
	
		Group svGroup = ReadDSS.getGroup(DSSUtil.createGroup("local", svDssFilePath), startTime, endTime, partA, partE, partF);
	    
		RegularTimeSeries rts_sv;
		
		for ( int i=0;i<numberOfSvVars;i++){
			
			rts_sv = ReadDSS.readDSS(svGroup, svPartB[i], svPartC[i]);
			double[] d = rts_sv.getYArray();
	
			wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, svDataColIndex + i, svDataRowIndex, d);
		}
	
		
		wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, svTimeColIndex, timeRowIndex, dateList);
		
		
		
		// dv 
		int numberOfDvVars = dvPartB.length;
		System.out.println("DV");
		for (int i = 0; i < dvPartB.length; i++) {
			System.out.println(dvPartB[i] + ":" + dvPartC[i]);
		}
	
		Group dvGroup = ReadDSS.getGroup(DSSUtil.createGroup("local", dvDssFilePath), startTime, endTime, partA, partE, partF);
	    
		RegularTimeSeries rts_dv;
		
		for ( int i=0;i<numberOfDvVars;i++){
			
			rts_dv = ReadDSS.readDSS(dvGroup, dvPartB[i], dvPartC[i]);
			double[] d = rts_dv.getYArray();
	
			wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, dvDataColIndex + i, dvDataRowIndex, d);
		}
	
		
		wb = ExcelUtil.modifyWorkBook(wb, writeSheetName, dvTimeColIndex, timeRowIndex, dateList);	
		
		
	
		
		ExcelUtil.writeWorkbookToFile(wb, excelFilePath);
	
		
		System.out.println("finish");
	}

	public static void findTimeWindow(){
		
		int beginDay = TimeOperation.numberOfDays(beginMonth,beginYear);
		int endDay = TimeOperation.numberOfDays(endMonth,endYear);
		
		_dateList = getDateList(beginYear, beginMonth, endYear, endMonth);

		
		_startDate = beginDay+toMonth(beginMonth)+ beginYear;
		_endDate   = endDay  +toMonth(endMonth)  + endYear;  
		
		_startTime = _startDate + " " + _startHour;
		_endTime   = _endDate   + " " + _startHour;
		
	}

}
