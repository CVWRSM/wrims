package wrims.misc;

import java.awt.Component;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import vista.db.dss.DSSUtil;
import vista.set.DataSetAttr;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import wrimsv2.evaluator.TimeOperation;

public class WriteDssToExcel {

	public WriteDssToExcel(){
		
	}
	
	// from GUI input
	//======================================
	public static int beginYear = 1921;
	public static int beginMonth = 10;
	public static int endYear = 2003;
	public static int endMonth = 9;
	public static String dssPartA = "";
	public static String dssPartF = "";
	public static String svDssFilePath = "";
	public static String dvDssFilePath = "";
	public static String excelFilePath = "";
	//======================================	

	
	// timestep
	private static String dssPartE = "1MON";

	
	/// param for excel 
	private static String readSheetName = "DSS Group";
	private static String writeSheetName = "Data";
	// write time
	public static int dvTimeColIndex = 5;
	public static int svTimeColIndex = 1;
	// SV input
	public static int svRowIndex_partBC = 6;
	public static int svColIndex_partB = 3;
	public static int svColIndex_partC = 4;
	// DV input
	public static int dvRowIndex_partBC = 18;
	public static int dvColIndex_partB = 3;
	public static int dvColIndex_partC = 4;
	
	// time window	
	private static final String _startHour = "2400";
	private static final String _endHour = "2400";
	private static String _startTime = "";
	private static String _endTime = "";	
	private static String _startDate = "";
	private static String _endDate = "";	
	private static String[] _dateList = null;

	
	
	public static void main(String[] args) throws IOException, InvalidFormatException, DssDataNotFoundException {
		
		
		writeDssToConstraintReport ();
		
	}

	
	public static void writeDssToConstraintReport () throws InvalidFormatException, IOException, DssDataNotFoundException {

		
		Workbook wb = ExcelTool.getWorkbook(excelFilePath);
		
		findTimeWindow();
		
		wb = writeDssToExcelWorkBook(wb,svDssFilePath, svColIndex_partB, svColIndex_partC, svRowIndex_partBC, svTimeColIndex);
		
		wb = writeDssToExcelWorkBook(wb,dvDssFilePath, dvColIndex_partB, dvColIndex_partC, dvRowIndex_partBC, dvTimeColIndex);

		
		ExcelTool.writeWorkbookToFile(wb, excelFilePath);
	
		System.out.println("finish");
		
	}
	
	public static Workbook writeDssToExcelWorkBook(Workbook wb, String dssFilePath, int colIndex_partB, int colIndex_partC, int rowIndex_partBC, int colIndex_time) throws DssDataNotFoundException{

		
		int colIndex_data_begin = colIndex_time + 1;
		
		String[] partB = ExcelTool.readColumn(wb, readSheetName, colIndex_partB, rowIndex_partBC);
		String[] partC = ExcelTool.readColumn(wb, readSheetName, colIndex_partC, rowIndex_partBC);
			 
		int numberOfVars = partB.length;

		for (int i = 0; i < partB.length; i++) {
			System.out.println(partB[i] + ":" + partC[i]);
		}
	
		Group svGroup = DssTool.getGroup(DSSUtil.createGroup("local", dssFilePath), _startTime, _endTime, dssPartA, dssPartE, dssPartF);
	    
		//DssUtil.checkDSSArray(svGroup, partB, partC);

		RegularTimeSeries[] rts_array = new RegularTimeSeries[numberOfVars];
		
		rts_array = DssTool.readDSS(svGroup, partB, partC);
		
		for ( int i=0;i<numberOfVars;i++){
						
			DataSetAttr dsa = rts_array[i].getAttributes();

			String[] attr = {dssPartA, partB[i], partC[i], "", dssPartE, dssPartF, _startDate, _startHour, _endDate, _endHour, dsa.getYUnits(), dsa.getYType() };
			
			wb = ExcelTool.modifyWorkBook(wb, writeSheetName, colIndex_data_begin + i, 0, attr);			
			
			double[] d = rts_array[i].getYArray();
			
			wb = ExcelTool.modifyWorkBook(wb, writeSheetName, colIndex_data_begin + i, 12, d);

		}
		
	
		// write date
		wb = ExcelTool.modifyWorkBook(wb, writeSheetName, colIndex_time, 12, _dateList);
		
	
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


	public static void findTimeWindow(){
		
		int beginDay = TimeOperation.numberOfDays(beginMonth,beginYear);
		int endDay = TimeOperation.numberOfDays(endMonth,endYear);
		
		_dateList = getDateList(beginYear, beginMonth, endYear, endMonth);

		
		_startDate = beginDay+toMonth(beginMonth)+ beginYear;
		_endDate   = endDay  +toMonth(endMonth)  + endYear;  
		
		_startTime = _startDate + " " + _startHour;
		_endTime   = _endDate   + " " + _startHour;
		
	}
	
	public class Task extends SwingWorker<Void, Void> {

		
		//Component com;
		JButton button;
		//String excelFilePath_default;
		
		public void setComponents(JButton button){
			
			//this.com = com;
			this.button= button;
			//this.excelFilePath_default = excelFilePath_default;
		}
		
		
		@Override
		public Void doInBackground() {


			setProgress(0);
			this.button.setEnabled(false);
			
			try {
				
				JOptionPane.showMessageDialog(null, "Writing DSS data to Excel file...");	

				setProgress(5);
				
				Workbook wb = ExcelTool.getWorkbook(excelFilePath);

				setProgress(40);
				
				findTimeWindow();
				
				wb = writeDssToExcelWorkBook(wb,svDssFilePath, svColIndex_partB, svColIndex_partC, svRowIndex_partBC, svTimeColIndex);

				setProgress(60);
				
				wb = writeDssToExcelWorkBook(wb,dvDssFilePath, dvColIndex_partB, dvColIndex_partC, dvRowIndex_partBC, dvTimeColIndex);

				setProgress(90);
				
				ExcelTool.writeWorkbookToFile(wb, excelFilePath);
				
				setProgress(99);
			
				System.out.println("finish");
				
				Process p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", "excel", excelFilePath });

				setProgress(0);
			}
			catch (Exception e) {
				
				String msg = "CalSim3 version might not match the Excel file version.\n";
				
				JTextArea text = new JTextArea(msg+e.getMessage());
				JOptionPane.showMessageDialog(null,text);
				
				//JOptionPane.showMessageDialog(this.com, e.getMessage());
				
				setProgress(0);
				this.button.setEnabled(true);
				// StringWriter errors = new StringWriter();
				// e.printStackTrace(new PrintWriter(errors));
				// JOptionPane.showMessageDialog(this,errors.toString());
				// e.printStackTrace();

			}		
			
			return null;
		}

		@Override
		public void done() {
			
			this.button.setEnabled(true);
			//setProgress(0);
			// Toolkit.getDefaultToolkit().beep();
			// startButton.setEnabled(true);
			// setCursor(null); //turn off the wait cursor
			// taskOutput.append("Done!\n");
		}
	}


}

