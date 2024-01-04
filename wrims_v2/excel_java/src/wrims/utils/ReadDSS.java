package wrims.utils;

import java.io.IOException;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Workbook;

import vista.db.dss.DSSUtil;
import vista.set.DataReference;
import vista.set.DataSet;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import vista.time.Time;
import vista.time.TimeFactory;
import vista.time.TimeWindow;

public class ReadDSS {

	public static void main(String[] args) throws IOException {

		String partA = "CALSIM";
		String svPartB = "S_SHSTALEVEL5";
		String svPartC = "STORAGE-LEVEL";
		String partE = "1MON";
		String partF = "CALSIM30_10";
		String dssFilePath = "D:\\cvwrsm\\trunk\\excel_java\\CalSim30_10_SV.dss";
		String startTime = "31JAN1982 2400";
		String endTime = "31JUL1982 2400";

		// Group g1 = DSSUtil.createGroup("local", dssFilePath);
		Group g = getGroup(DSSUtil.createGroup("local", dssFilePath), startTime, endTime, partA, partE, partF);

		RegularTimeSeries r = readDSS(g, svPartB, svPartC);
		double[] d = r.getYArray();

		String filePath = "workbook.xlsm";
		String sheetName = "Control";
		int ci = 4;
		int ri = 9;
		Workbook wb = ExcelUtil.getWorkbook(filePath);
		wb = ExcelUtil.modifyWorkBook(wb, sheetName, ci, ri, d);
		ExcelUtil.writeWorkbookToFile(wb, filePath);

	}

	public static Group getGroup(Group group, String startTime, String endTime, String partA, String partE, String partF) {

		partA = regularExp(partA);
		partE = regularExp(partE);
		partF = regularExp(partF);

		Time st = TimeFactory.getInstance().createTime(startTime);
		Time et = TimeFactory.getInstance().createTime(endTime);
		TimeWindow tw = TimeFactory.getInstance().createTimeWindow(st, et);

		DataReference[] refs = group.find(new String[] { partA, "", "", "", partE, partF });
		DataReference[] refs_tw = new DataReference[refs.length];

		for (int i = 0; i < refs.length; i++) {

			refs_tw[i] = DataReference.create(refs[i], tw);

		}

		Group out = Group.createGroup(group.getName(), refs_tw);

		return out;
	}
	
	public static double[][] readDSS(Group group, String[] partB, String[] partC) {

		int numberOfVars = partB.length;
		
		for (int i=0; i< numberOfVars; i++) {
		
			partB[i] = regularExp(partB[i]);
			partC[i] = regularExp(partC[i]);
			
	    }

		DataReference[] refs = new DataReference[numberOfVars];
		
		for (int i=0; i< numberOfVars; i++) {
			
			refs[i] = group.find(new String[] { "", partB[i], partC[i], "", "", "" })[0];
			
		}
		
		RegularTimeSeries[] rts = new RegularTimeSeries[numberOfVars];

		for (int i=0; i< numberOfVars; i++) {

			//DataSet ds = refs[0].getData();
			rts[i] = (RegularTimeSeries) refs[i].getData();
			
		}
		
		int dataLength = rts[0].getYArray().length;
		
		double out[][] = new double[numberOfVars][dataLength];
	
		
		for (int i=0; i< numberOfVars; i++) {

				out[i] = rts[i].getYArray();
		}
		
//		System.out.println(size);
//		System.out.println(numberOfVars);
//		//System.out.println(Arrays.deepToString(out));
		return out;
	}
	
	public static RegularTimeSeries readDSS(Group group, String partB, String partC) {

		partB = regularExp(partB);
		partC = regularExp(partC);

		DataReference[] refs = group.find(new String[] { "", partB, partC, "", "", "" });

		System.out.println(partB+":"+partC);
		System.out.println("refs.length:"+refs.length);
		DataSet ds = refs[0].getData();
		RegularTimeSeries rts = (RegularTimeSeries) ds;

		return rts;
	}
	
//	public static double[] readDSS(Group group, String partB, String partC) {
//
//		partB = regularExp(partB);
//		partC = regularExp(partC);
//
//		DataReference[] refs = group.find(new String[] { "", partB, partC, "", "", "" });
//
//		DataSet ds = refs[0].getData();
//		RegularTimeSeries rts = (RegularTimeSeries) ds;
//
//		return rts.getYArray();
//	}

	public static double[] readDSS(Group group, String startTime, String endTime, String partA, String partB,
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
