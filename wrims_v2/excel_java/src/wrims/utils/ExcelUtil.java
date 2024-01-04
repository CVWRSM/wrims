package wrims.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtil {

	public static void main(String[] args) {

		String filePath = "OperationsControl_CS3_version134.xlsm";
		String readSheetName = "DSS Group";
		String writeSheetName = "test";

		Workbook wb = getWorkbook(filePath);

		// DV file
		String[] dvPartB = readColumn(wb, readSheetName, 3, 18);
		String[] dvPartC = readColumn(wb, readSheetName, 4, 18);

		// SV file
		int numberOfSvVars = 3;
		int svRow = 6;
		String[] svPartB = readColumn(wb, readSheetName, 3, svRow, numberOfSvVars);
		String[] svPartC = readColumn(wb, readSheetName, 4, svRow, numberOfSvVars);

		System.out.println("SV");
		for (int i = 0; i < svPartB.length; i++) {
			System.out.println(svPartB[i] + ":" + svPartC[i]);
		}
		System.out.println("DV");
		for (int i = 0; i < dvPartB.length; i++) {
			System.out.println(dvPartB[i] + ":" + dvPartC[i]);
		}

		try {
			wb.createSheet("test");
		}
		catch (Exception e) {
			// TODO: handle exception
		}

		double[] d = new double[4];

		d[0] = 11.13;
		d[1] = 22.9999;
		d[2] = 33.8;
		d[3] = 7733.8;
		int ci = 4;
		int ri = 9;

		double[][] dd = { { 1, 2, 3 }, { 4, 5, 6 } };
		wb = modifyWorkBook(wb, writeSheetName, ci, ri, dd);

		writeWorkbookToFile(wb, filePath);

	}

	public static Workbook getWorkbook(String filePath) {

		InputStream inp;
		Workbook wb = null;
		// File inp = null;

		try {
			// inp = new File(filePath);
			inp = new FileInputStream(filePath);
			wb = WorkbookFactory.create(inp);
			inp.close();

		}
		catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wb;

	}

	public static void writeWorkbookToFile(Workbook wb, String filePath) {

		FileOutputStream fileOut = null;

		try {
			fileOut = new FileOutputStream(filePath);
			wb.write(fileOut);
			fileOut.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String[] readColumn(Workbook wb, String sheetName, int colIndex, int rowIndex) {

		Sheet sh = wb.getSheet(sheetName);

		ArrayList<Cell> ca = getColumnCells(sh, colIndex, rowIndex);

		String[] s = new String[ca.size()];

		for (int i = 0; i < ca.size(); i++) {

			s[i] = ca.get(i).getStringCellValue();

		}

		return s;

	}

	public static String[] readColumn(Workbook wb, String sheetName, int colIndex, int rowIndex, int numberOfData) {

		String[] s = new String[numberOfData];

		Sheet sh = wb.getSheet(sheetName);

		Cell[] cell = getColumnCells(sh, colIndex, rowIndex, numberOfData);

		for (int i = 0; i < numberOfData; i++) {

			s[i] = cell[i].getStringCellValue();

		}

		return s;

	}

	public static Workbook modifyWorkBook(Workbook wb, String sheetName, int colIndex, int rowIndex, double[][] d) {

		int nOfRows = d[0].length;
		int nOfCols = d.length;

		Sheet sh = wb.getSheet(sheetName);

		for (int j = 0; j < nOfCols; j++) {
			for (int i = 0; i < nOfRows; i++) {

				System.out.println("j:" + j + " " + "i:" + i + " " + "d[j][i]" + d[j][i]);

				System.out.println("colIndex" + colIndex);
				Cell[] cell = getColumnCells(sh, colIndex + j, rowIndex, nOfRows);

				cell[i].setCellValue(d[j][i]);

			}
		}

		return wb;

	}

	public static Workbook modifyWorkBook(Workbook wb, String sheetName, int colIndex, int rowIndex, String[] d) {
		
		Sheet sh = wb.getSheet(sheetName);

		Cell[] cell = getColumnCells(sh, colIndex, rowIndex, d.length);

		for (int i = 0; i < d.length; i++) {

			cell[i].setCellValue(d[i]);

		}

		return wb;

	}
	
	public static Workbook modifyWorkBook(Workbook wb, String sheetName, int colIndex, int rowIndex, double[] d) {

		Sheet sh = wb.getSheet(sheetName);

		Cell[] cell = getColumnCells(sh, colIndex, rowIndex, d.length);

		for (int i = 0; i < d.length; i++) {

			cell[i].setCellValue(d[i]);

		}

		return wb;

	}

	public static ArrayList<Cell> getColumnCells(Sheet sh, int colIndex, int rowIndex) {

		Cell c;
		ArrayList<Cell> ca = new ArrayList<Cell>();
		int i = 0;

		boolean notEmpty = true;

		while (notEmpty) {

			try {

				Row r = sh.getRow(i + rowIndex);
				c = r.getCell(colIndex);

				if (c.getCellType() != Cell.CELL_TYPE_BLANK) {
					ca.add(c);
				}
				else {
					notEmpty = false;
				}

			}
			catch (Exception e) {

				notEmpty = false;

			}
			i++;
		}

		return ca;

	}

	// public static Cell[][] getColumnCells(Sheet sh, int colIndexBegin, int
	// numberOfCols, int rowIndexBegin,
	// int numberOfRows) {
	//
	// Cell[][] c = new Cell[numberOfCols][numberOfRows];
	//
	//
	// for (int j = 0; j < numberOfCols; j++) {
	//
	// for (int i = 0; i < numberOfRows; i++) {
	//
	// try {
	//
	// Row r = sh.getRow(i + rowIndexBegin);
	// c[j][i] = r.getCell(j + colIndexBegin);
	//
	// }
	// catch (Exception e) {
	//
	// Row r = sh.createRow(i + rowIndexBegin);
	// c[j][i] = r.createCell(j + colIndexBegin);
	// }
	//
	//
	// }
	//
	// }
	//
	//
	// return c;
	//
	// }

	public static Cell[] getColumnCells(Sheet sh, int colIndex, int rowIndex, int nOfRows) {

		Cell[] cell = new Cell[nOfRows];

		for (int i = 0; i < nOfRows; i++) {

			Row r = sh.getRow(i + rowIndex);

			if (r == null) {
				r = sh.createRow(i + rowIndex);
			}

			Cell c = r.getCell(colIndex);

			if (c == null) {
				c = r.createCell(colIndex);
			}

			cell[i] = c;

		}

		return cell;

	}

}
