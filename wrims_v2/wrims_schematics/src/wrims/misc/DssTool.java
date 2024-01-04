package wrims.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import vista.db.dss.DSSUtil;
import vista.set.DataReference;
import vista.set.DataSet;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import vista.time.Time;
import vista.time.TimeFactory;
import vista.time.TimeWindow;

public class DssTool {

	public static void main(String[] args) throws IOException, InvalidFormatException, DssDataNotFoundException {

		String partA = "CALSIM";
		String svPartB = "S_SHSTALEVEL5";
		String svPartC = "STORAGE-LEVEL";
		String partE = "1MON";
		String partF = "CALSIM30_10";
		String dssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
		String startTime = "31JAN1932 2400";
		String endTime = "31JUL1962 2400";

		// Group g1 = DSSUtil.createGroup("local", dssFilePath);
		Group g = getGroup(DSSUtil.createGroup("local", dssFilePath), startTime, endTime, partA, partE, partF);

		RegularTimeSeries r = readDSS(g, svPartB, svPartC);
		
		String[] arrayPartB = {"S_SHSTALEVEL5_","S_SHSTALEVEL5__"};
		String[] arrayPartC = {"STORAGE-LEVEL","STORAGE-LEVEL"};		
		
		RegularTimeSeries[] ra = readDSS(g, arrayPartB, arrayPartC);		
		
		double[] d = r.getYArray();

		String filePath = "workbook.xlsm";
		String sheetName = "Control";
		int ci = 4;
		int ri = 9;
		Workbook wb = ExcelTool.getWorkbook(filePath);
		wb = ExcelTool.modifyWorkBook(wb, sheetName, ci, ri, d);
		ExcelTool.writeWorkbookToFile(wb, filePath);

	}

	public static Group getGroup(Group group, String startTime, String endTime, String partA, String partE, String partF) throws DssDataNotFoundException {

		String partA_regexp = regularExp(partA);
		String partE_regexp = regularExp(partE);
		String partF_regexp = regularExp(partF);

		Time st = TimeFactory.getInstance().createTime(startTime);
		Time et = TimeFactory.getInstance().createTime(endTime);
		TimeWindow tw = TimeFactory.getInstance().createTimeWindow(st, et);

		DataReference[] refs = group.find(new String[] { partA_regexp, "", "", "", partE_regexp, partF_regexp });
		
		if (refs.length<1) throw new DssDataNotFoundException("DSS data not found:  ", partA + "/*/*/*/"+ partE + "/"+partF);

		
		
		DataReference[] refs_tw = new DataReference[refs.length];

		System.out.println("refs.length"+refs.length);
		
		for (int i = 0; i < refs.length; i++) {

			refs_tw[i] = DataReference.create(refs[i], tw);
			if (refs_tw[i]==null) throw new DssDataNotFoundException("DSS data not found:  ", partA + "/*/*/"+ tw.toString() +"/"+ partE + "/"+partF);

		}

		Group out = Group.createGroup(group.getName(), refs_tw);

		return out;
	}
	
	public static RegularTimeSeries readDSS(Group group, String partB, String partC) throws DssDataNotFoundException {
	
		String partB_regexp = regularExp(partB);
		String partC_regexp = regularExp(partC);
	
		DataReference[] refs = group.find(new String[] { "", partB_regexp, partC_regexp, "", "", "" });
	
		System.out.println(partB_regexp+":"+partC_regexp);
		
		System.out.println("refs.length:"+refs.length);
		
		if (refs.length<1) throw new DssDataNotFoundException("DSS data not found:  ","*/"+partB+"/"+partC+"/*/*/*");
		
		DataSet ds = refs[0].getData();
		RegularTimeSeries rts = (RegularTimeSeries) ds;
	
		return rts;
	}

	public static int checkDSSArray(Group group, String[] partB, String[] partC) throws DssDataNotFoundException {
		
		int errCount = 0;
		
		int size = partB.length;
		
		ArrayList<String> errMsg = new ArrayList<String>();
		
		for (int i=0; i<size; i++){
			
			try {
				readDSS(group, partB[i], partC[i]);
				
			} catch (DssDataNotFoundException e) {
				
				errMsg.add(e.getErrMsg());
				
			}
			
		}
		
		//System.out.println("errMsg.size()"+errMsg.size());
		
		errCount = errMsg.size();
		
		if (errCount>0) {
			String []strArray = new String[errMsg.size()];
			throw new DssDataNotFoundException(errMsg.toArray(strArray));
		}
	
		return errCount;
	}	
	
	public static RegularTimeSeries[] readDSS(Group group, String[] partB, String[] partC) throws DssDataNotFoundException {
		
		int size = partB.length;
		RegularTimeSeries[] rts = new RegularTimeSeries[size];
		
		ArrayList<String> errMsg = new ArrayList<String>();
		
		for (int i=0; i<size; i++){
			
			try {
				rts[i] = readDSS(group, partB[i], partC[i]);
				
			} catch (DssDataNotFoundException e) {
				
				errMsg.add(e.getErrMsg());
			}
			
		}
		
		//System.out.println("errMsg.size()"+errMsg.size());
		
		if (errMsg.size()>0) {
			String []strArray = new String[errMsg.size()];
			throw new DssDataNotFoundException(errMsg.toArray(strArray));
		}
	
		return rts;
	}

//	private static double[][] readDSS(Group group, String[] partB, String[] partC) {
//
//		int numberOfVars = partB.length;
//		
//		for (int i=0; i< numberOfVars; i++) {
//		
//			partB[i] = regularExp(partB[i]);
//			partC[i] = regularExp(partC[i]);
//			
//	    }
//
//		DataReference[] refs = new DataReference[numberOfVars];
//		
//		for (int i=0; i< numberOfVars; i++) {
//			
//			refs[i] = group.find(new String[] { "", partB[i], partC[i], "", "", "" })[0];
//			
//		}
//		
//		RegularTimeSeries[] rts = new RegularTimeSeries[numberOfVars];
//
//		for (int i=0; i< numberOfVars; i++) {
//
//			//DataSet ds = refs[0].getData();
//			rts[i] = (RegularTimeSeries) refs[i].getData();
//			
//		}
//		
//		int dataLength = rts[0].getYArray().length;
//		
//		double out[][] = new double[numberOfVars][dataLength];
//	
//		
//		for (int i=0; i< numberOfVars; i++) {
//
//				out[i] = rts[i].getYArray();
//		}
//		
////		System.out.println(size);
////		System.out.println(numberOfVars);
////		//System.out.println(Arrays.deepToString(out));
//		return out;
//	}
	
	private static double[] readDSS(Group group, String startTime, String endTime, String partA, String partB,
			String partC, String partE, String partF) {

		partA = regularExp(partA);
		partB = regularExp(partB);
		partC = regularExp(partC);
		partE = regularExp(partE);
		partF = regularExp(partF);

		DataReference[] refs = group.find(new String[] { partA, partB, partC, "", partE, partF });

		Time st = TimeFactory.getInstance().createTime(startTime);
		Time et = TimeFactory.getInstance().createTime(endTime);
		TimeWindow tw = TimeFactory.getInstance().createTimeWindow(st, et);

		DataReference r = DataReference.create(refs[0], tw);

		DataSet ds = r.getData();
		RegularTimeSeries rts = (RegularTimeSeries) ds;

		// for (double d : rts.getYArray()){
		// System.out.println(d);
		// }

		return rts.getYArray();
	}

	public static String regularExp(String part) {
		return "^" + part + "$";
	}
}
