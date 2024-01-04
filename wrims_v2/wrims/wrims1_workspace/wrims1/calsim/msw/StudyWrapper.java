package calsim.msw;

import calsim.app.*;
import calsim.wreslcoder.*;
import calsim.wreslcoder.wresl.WreslParser;
import vista.time.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class StudyWrapper {

//CB	private final String calsimLibDir = System.getProperty("calsim.home")
//CB	private final String calsimLibDir = System.getenv("calsim.home") + File.separator + "lib" + File.separator + "lib"; 	//CB replaced cause Eclipse can set environment variables
	private String calsimLibDir = null;  //CB set dynamically depending on whether started from batch or Eclipse

	private int initIndex;

	private Study sty;

	private WreslMaker wm;

	private MSWGui _gui;

	private File _errorFile; // for catching FORTRAN infeasibilities and other errors

	private String ts;

	String[] _positionDVs;

	String _init = " ";

	Hashtable _positionInterfaces = new Hashtable();

	boolean _wrapperDebug = true; //CB - to isolate Leaf's GUI crash

	boolean position;

	int posperiods;

	int numyears;

	int ndv;
	
	public StudyWrapper(String studyFile, String iI, MSWGui gui, boolean debug) {
		setLibraryDirectory(); //CB added
		initIndex = new Integer(iI).intValue();
		_gui = gui;
		sty = new Study();
		try {
			sty.load(studyFile);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
		String errorFileString = (new File(sty.getWreslFile()).getParent()
				+ File.separator + "error.log");
		// Delete the FORTRAN generated message file if it exists
		_errorFile = new File(errorFileString);
		_errorFile.delete();

		setInitFile(sty.getInitFile()); // store initial file name which is in "studyFile"
		if (_wrapperDebug) System.out.println("Initial Init file: " + sty.getInitFile());
		sty.setPosAnalysisOption(new Boolean(true));
		sty.setDialogWindowOption(new Boolean(false));

		ts = sty.getTimeStep();
		position = MSWUtil.position;
		posperiods = MSWUtil.nperiods;
		numyears = posperiods / 12;
		ndv = numyears;

		if (_wrapperDebug) System.out.println("First Pass: " + MSWUtil.firstPass);
		if (position && MSWUtil.firstPass) {
			if (ndv == 0 || posperiods - 12 * numyears > 0 && numyears > 0) ndv++;
			if (_wrapperDebug) System.out.println("Number of DV files: " + ndv);
			String[] dvnames = new String[ndv];
			if (ndv > 1) {
				String dvname = sty.getDvFile();
				int dir = dvname.lastIndexOf("\\");
				String dirname = dvname.substring(0, dir + 1);
				for (int i = 0; i < ndv; i++) {
					dvnames[i] = new String(dirname + "POSITION" + new Integer(i + 1).toString()
						+ "DV.DSS");
					if (_wrapperDebug) System.out.println("Newly Created DV name: " + dvnames[i]);
				}
				setPositionDV(dvnames);
			} else {
				dvnames = new String[1];
				dvnames[0] = sty.getDvFile();
				if (_wrapperDebug) System.out.println("Using current sty dv: " + dvnames[0]);
				setPositionDV(dvnames);
			}
		}

		// Generate WreslMaker object.
		String wf = sty.getWreslFile();
		File wreslFile = new File(wf);
		String studyDir = wreslFile.getParent().toUpperCase();
		String commonDir = sty.getCommonPath().toUpperCase();
		wm = new WreslMaker(studyDir, commonDir, wf, _gui.getOutputWriter(), _gui.getBuildDebugOption(), 
			_gui._hideWarnings, _gui._hideProgressDetails, _gui._parseFileDelay);

		if (!getDVFile().equalsIgnoreCase(sty.getInitFile())) {
			if (!_gui.isDvFileAsInitFile() || (_gui.isDvFileAsInitFile() && _gui.isDVDelete())) {
				File initFile = new File(sty.getInitFile());
				try {
					FileInputStream initFIS = new FileInputStream(initFile);
					byte[] b = new byte[initFIS.available()];
					initFIS.read(b);
					initFIS.close();
					File dvFile = new File(getDVFile());
					/*
					 * Without the following line of code, modellers should beware
					 * that previous data will only be cleared one month at a time
					 * and that old data with same bpart and different cpart might
					 * interfere. Best to start study with fresh dv file.
					 */
					// if (dvFile.exists()) dvFile.delete();
					if (_gui.isDVDelete()) dvFile.delete();
					FileOutputStream dvFOS = new FileOutputStream(dvFile);
					dvFOS.write(b);
					dvFOS.close();

				} catch (IOException ioe) {
					throw new RuntimeException(ioe.getMessage());
				}
				if (!MSWUtil.position) sty.setInitFile(getDVFile());  //TODO: weird ??
				if (_wrapperDebug) System.out.println("Second set init: " + sty.getInitFile());
			}
		} //else if (_gui.isDvFileAsInitFile()) { // cannot do this here as it depends on the index of the last study
//			if (!MSWUtil.position) sty.setInitFile(getDVFile());
//			if (DEBUG) System.out.println("Second set init: " + sty.getInitFile());
//		}
	}

    /**
     * CB added because batch file sets calsim.home Property, but in Eclipse it is in environment variables.
     */
    private void setLibraryDirectory() {
		if (System.getenv("calsim.home") != null) {
//			System.out.println("getenv did NOT return null");
			calsimLibDir = System.getenv("calsim.home") + File.separator+ "lib"; //Eclipse project settings sets
//			System.out.println("calsimLibDir = " + calsimLibDir);
		} else {
//			System.out.println("getenv DID return null");
			calsimLibDir = System.getProperty("calsim.home") + File.separator+ "lib"; //batch file sets
//			System.out.println("calsimLibDir = " + calsimLibDir);
		}
	}

	protected String getDVFile() {
		return sty.getDvFile();
	}

	protected String getTimeStep() {
		return sty.getTimeStep();
	}

	void setPositionDV(String[] posDV) {
		_positionDVs = posDV;
	}

	public String getPositionDV(int index) {
		return _positionDVs[index];
	}

	public String getStudyDVFilename() {
		return sty.getDvFile();
	}

	void setInitFile(String init) {
		_init = init;
	}

	public String getInitFile() {
		return _init;
	}

	void setStudyInitFile(String initFile) {
		sty.setInitFile(initFile);
	}

	void setIsStudyPositionStartYear(Boolean isStartYear) {
		sty.setPositionStartYear(isStartYear);
	}

	public void setPositionInterface(int index, StudyInterface sI) {
		_positionInterfaces.put(new Integer(index), sI);
	}

	public StudyInterface getPositionInterface(int index) {
		return (StudyInterface) _positionInterfaces.get(new Integer(index));
	}

	MYDate setStopDate(MYDate d) {
		sty.setStopMonth(d.getMonth());
		sty.setStopYear(d.getYear());
		sty.setStopDay(getDaysInMonth(d));
		return d;
	}

	MYDate setStartDate(MYDate d) {
		sty.setStartMonth(d.getMonth());
		sty.setStartYear(d.getYear());
		if (ts.equalsIgnoreCase("1MON"))
			sty.setStartDay(getDaysInMonth(d));
		else
			sty.setStartDay("1");
		return d;
	}

	protected String getDaysInMonth(MYDate d) {
		int month = d.getMonthIndex();
		int year = d.getYearIndex();
		return new Integer(getDaysInMonth(month, year)).toString();
	}

	protected int getDaysInMonth(int month, int year) {
		int daysInMonth;
		int daysArray[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		daysInMonth = daysArray[month - 1];
		if (month == 2) if (leapYear(year)) daysInMonth = 29;
		return daysInMonth;
	}

	private boolean leapYear(int year) {
		if (Math.IEEEremainder(year, 4) == 0 && (Math.IEEEremainder(year, 100) != 0
				|| Math.IEEEremainder(year, 400) == 0)) {
			return true;
		} else return false;
	}

	void setNumberSteps(int number) {
		sty.setNumberSteps(String.valueOf(number));
	}

	void setNumberSteps(String number) {
		sty.setNumberSteps(number);
	}

	/**
	 * Write study.sty file to current run directory for Fortran
	 */
	public void saveStudyFile() {
		Object obj;
		Vector v = sty.getAllProperties();
		try {
			File wreslFile = new File(sty.getWreslFile());
			String studyDir = wreslFile.getParent();
			PrintWriter studyPrintWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(studyDir + File.separator + "study.sty")));
			studyPrintWriter.println("Study File: Generated by WRIMS. Do Not Edit!");
			for (Enumeration e = v.elements(); e.hasMoreElements();) {
				obj = e.nextElement();
				if (obj != null) studyPrintWriter.println(obj.toString().toUpperCase());
				else studyPrintWriter.println("");
			}
			studyPrintWriter.println("MULTI"); // indicates to Fortran that multistudy is running
			studyPrintWriter.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Check the required study properties
	 */
	boolean checkStudy() {
		if (sty.getName().length() == 0
				|| sty.getHydrologyVersion().length() == 0
				|| sty.getWreslFile().length() == 0
				|| sty.getSvFile().length() == 0
				|| sty.getDvFile().length() == 0) {
			String msg = "Name, Hydrology Version, Wresl File, and DSS Files are Required!";
			_gui.println(msg);
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} else
			return true;
	}

	/**
	 * Scans Wresl file dates and compares to executable
	 */
	public boolean newerFiles() {
		try {
			WreslIncScanner scanner = new WreslIncScanner(sty.getWreslFile());
			if (scanner.getNewerList(WreslMaker.makeExeFile(sty.getWreslFile())).size() > 0)
				return true;
		} catch (FileNotFoundException e) {
			String msg = "File Not Found! " + e.getMessage();
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (calsim.wreslcoder.wresl.TokenMgrError e) {
			_gui.println(e.getMessage());
			String msg = "Wresl Token Error! ";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			System.gc();
		}
		return false;
	}

	/**
	 * Parses the Wresl files
	 */
	public boolean parseFiles() {
		try {
			if (sty.getWreslFile().indexOf("-") != -1) {
				String msg = "Illegal Directory Name! No '-' characters are allowed: "
					+ sty.getWreslFile();
				JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			wm.parse();
			Integer numberCycles = sty.getNumberSequences();
			Integer numberWreslCycles = new Integer(wm.getNumberOfWreslCycles());
			if (numberCycles.intValue() != numberWreslCycles.intValue()) {
				String msg = "Number of sequences must be same as specified in Wresl!";
				JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (calsim.wreslcoder.wresl.TokenMgrError e) {
			if (_gui._hideProgressDetails) // need to print the problem file when the files parsed are hidden
				_gui.println(WreslParser._lastFileParsed.getAbsolutePath());
			_gui.println(e.getMessage());
			String msg = "Wresl Token Error! ";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			WreslParser.closeAllFiles();
			return false;
		} catch (FileNotFoundException e) {
			if (_gui._hideProgressDetails) // need to print the problem file when the files parsed are hidden
				_gui.println(WreslParser._lastFileParsed.getAbsolutePath());
			String msg = "File Not Found! " + e.getMessage();
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			WreslParser.closeAllFiles();
			return false;
		} catch (calsim.wreslcoder.wresl.ParseException e) {
			if (_gui._hideProgressDetails) // need to print the problem file when the files parsed are hidden
				_gui.println(WreslParser._lastFileParsed.getAbsolutePath());
			_gui.println(e.getMessage());
			String msg = "Wresl Parse Error! ";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			WreslParser.closeAllFiles();
			return false;
		}
		return true;
	}

	/**
	 * Compiles the F90 files
	 */
	public boolean compile() {
		for (int i = 0; i <= sty.getNumberSequences().intValue(); i++) {
			if (!wm.compile(calsimLibDir, java.lang.String.valueOf(i))) {
				String msg = "Compilation Error!";
				JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if (!wm.compileGlobal(calsimLibDir)) {
			String msg = "Compilation Error!";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Links the F90 object code
	 */
	public boolean link() {
		if (!wm.link(calsimLibDir, sty.getNumberSequences())) {
			String msg = "Link Error!";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Runs the F90 executable program containing the study.
	 */
	public boolean execute() {
		if (wm.runModel() > 0) {
			String msg = "Execution Error!";
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	// The following method returns a TimeWindow object which is a necessary
	// argument for
	// the StudyInterface.activate method.
	TimeWindow createTimeWindow(MYDate sD, MYDate eD) {
		return MSWUtil.createTimeWindow(new String(sty.getStartDay() + sD.toString() + " 2400"),
			new String(sty.getStopDay() + eD.toString() + " 2400"));
	}



	protected String getDVFile(int i) {
		return sty.getDvFile();
	}

	/**
	 * Method trimOutput added to prevent OutOfMemoryError when JTextArea text
	 * gets huge (CDB)
	 */
	private void trimOutput() {
		_gui.trimOutput();
	}

	/**
	 *
	 */
	boolean prepareForRun() throws Exception {
		saveStudyFile();
		// Check to see if all necessary info is contained in the study.
		return checkStudy();
	}

	/**
	 *
	 * @param DVFilename
	 */
	void setStudyDVFilename(String DVFilename) {
		sty.setDvFile(DVFilename);
	}

	public boolean doesErrorFileExist() {
		return _errorFile.exists();
	}

	String getErrorFileAbsolutePath() {
		if (doesErrorFileExist()) return _errorFile.getAbsolutePath();
		else return "";
	}

	public int getInitIndex() {
		return initIndex;
	}

	public String getStudyName() {
		return sty.getFileName().substring(sty.getFileName().lastIndexOf("\\") + 1);
	}
}
