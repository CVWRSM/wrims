package calsim.msw;

import calsim.app.AppUtils;

import java.io.*;
import java.util.*;
import java.awt.Cursor;
import javax.swing.*;

import vista.time.TimeWindow;

/**
 * MultiStudyWrapper (MSW) enables enables the user to run multiple studies in a
 * user-defined series. The user can also specify pass and operate data between
 * studies.
 * @author Clay Booher
 */
public class MultiStudyWrapper extends Thread {

	private StudyWrapper[] sW;

	private StudyInterface[] iSI, fSI;

	private MYDate _runStartDate;

	private MYDate _runStopDate;

	private int lastIndex;

	private MSWGui _gui;

	private String iSIInfo[], fSIInfo[];

//	private boolean DEBUG = true; //CB - to isolate Leaf's GUI crash

	private boolean _wrapperDebug;

	private boolean _transferDebug;

	private boolean _position;

	private int _termNumber = 0;

	private int _numMonths;

	private int _posperiods;

	private int _numyears;

	private int _numDVFiles;

	private int _yr0periods = 12;

	private int _afteryr0periods = 12;

	public MultiStudyWrapper(String multiStudyFile, MSWGui gui) throws Exception {
		_gui = gui;
		_wrapperDebug = _gui.getWrapperDebugOption();
		// Define and instantiate vectors for loading and locating MSW text file input.
		Vector mswDetails = new Vector();
		Vector loc = new Vector();
		// Parse text file input into the mswDetails vector.
		// if ((new File(multiStudyFile)).exists()) {
		MSWUtil.parseFile(multiStudyFile, mswDetails, loc);
		// Determine vector index at which the start and end dates are set.
		Integer datesLoc = (Integer) loc.elementAt(0);
		// Place the start and end dates in a String array.
		String[] fields = (String[]) mswDetails.elementAt(datesLoc.intValue());
		_position = MSWUtil.position;
		// Create MYDate objects for the start and end dates.
		String startMonth = fields[1].substring(0, 3);
		Integer startYear = new Integer(fields[1].substring(3, 7));
		_runStartDate = new MYDate(startMonth, startYear);
		MSWUtil.positionStart = _runStartDate;
		String stopMonth = fields[2].substring(0, 3);
		Integer stopYear = new Integer(fields[2].substring(3, 7));
		_runStopDate = new MYDate(stopMonth, stopYear);
		if (MSWUtil.position) {
			setPositionStartMonths(startMonth, stopMonth, MSWUtil.nperiods);
			MSWUtil.posStartYear = startYear.intValue();
		}
		// Determine the number of studies involved from the block of text
		// input.
		int asize = mswDetails.size() - datesLoc.intValue() - 1;
		lastIndex = asize - 1;

		// Instantiate the StudyWrapper and StudyInterface arrays.
		sW = new StudyWrapper[asize];
		iSI = new StudyInterface[asize];
		fSI = new StudyInterface[asize];
		iSIInfo = new String[asize];
		fSIInfo = new String[asize];

		try {

			// Loop to instantiate StudyWrappers.
			for (int i = 0; i < asize; i++) {
				// Check to see if stop button pushed.
				if (_gui.wasStopPushed()) break;
				// Place details in fields String array.
				fields = (String[]) mswDetails.elementAt(i + datesLoc.intValue()+ 1);
				if (i == 0) {
					_numMonths = Integer.parseInt(fields[1]);
				} else {
					if (_numMonths != Integer.parseInt(fields[1])) System.out.println();
				}
				sW[i] = new StudyWrapper(fields[0], fields[4], gui, _wrapperDebug);
				// Save info for constructing interfaces and initiate them with the
				// null object.
				iSIInfo[i] = fields[2];
				fSIInfo[i] = fields[3];
				iSI[i] = null;
				fSI[i] = null;
			}
			_posperiods = MSWUtil.nperiods;
			_numyears = _posperiods / 12;
			_numDVFiles = _numyears;
			if (_position) _numMonths = _posperiods - 12 * _numyears;

			// Instantiate a TimeFactory in the MSWUtil class. The time factory will
			// be used throughout the run to generate objects such as TimeWindow.
			MSWUtil.createTimeFactory();
		} catch (RuntimeException ioe) {
			throw ioe;
		}
	}

	private String getInterfaceInfo(int index) {
		return fSIInfo[index];
	}

	private void setPositionStartMonths(String startMonth, String stopMonth,
			int nperiods) {
		Hashtable monthIndex = new Hashtable(12);
		monthIndex.put("JAN", new Integer(1));
		monthIndex.put("FEB", new Integer(2));
		monthIndex.put("MAR", new Integer(3));
		monthIndex.put("APR", new Integer(4));
		monthIndex.put("MAY", new Integer(5));
		monthIndex.put("JUN", new Integer(6));
		monthIndex.put("JUL", new Integer(7));
		monthIndex.put("AUG", new Integer(8));
		monthIndex.put("SEP", new Integer(9));
		monthIndex.put("OCT", new Integer(10));
		monthIndex.put("NOV", new Integer(11));
		monthIndex.put("DEC", new Integer(12));

		Hashtable monthName = new Hashtable(12);
		monthName.put(new Integer(1), "JAN");
		monthName.put(new Integer(2), "FEB");
		monthName.put(new Integer(3), "MAR");
		monthName.put(new Integer(4), "APR");
		monthName.put(new Integer(5), "MAY");
		monthName.put(new Integer(6), "JUN");
		monthName.put(new Integer(7), "JUL");
		monthName.put(new Integer(8), "AUG");
		monthName.put(new Integer(9), "SEP");
		monthName.put(new Integer(10), "OCT");
		monthName.put(new Integer(11), "NOV");
		monthName.put(new Integer(12), "DEC");

		// Integer bm = (Integer)monthIndex.get(startMonth);
		Integer em = (Integer) monthIndex.get(stopMonth);

		// int bmi = bm.intValue();
		int emi = em.intValue();
		int remainder = nperiods - 12 * (nperiods / 12);

		if (nperiods <= 12 || remainder == 0) {
			MSWUtil.positionStartMonth = startMonth;
			MSWUtil.positionContinueMonth = startMonth;
		} else {
			MSWUtil.positionStartMonth = startMonth;
			int ci = 12 - emi;
			int cmon = 13 - ci;
			if (_wrapperDebug) System.out.println(cmon);
			MSWUtil.positionContinueMonth = (String) monthName.get(new Integer(
					cmon));
		}
		if (_wrapperDebug) System.out.println("Start: " + MSWUtil.positionStartMonth
			+ " Continue " + MSWUtil.positionContinueMonth);
	}

	private boolean createStudyInterface(int ind) throws Exception {
		if (iSIInfo[ind].equalsIgnoreCase("null"))
			iSI[ind] = null;
		else
			iSI[ind] = new StudyInterface(sW[ind].getDVFile(), sW[ind].getTimeStep(), iSIInfo[ind],
					_transferDebug);
		if (fSIInfo[ind].equalsIgnoreCase("null"))
			fSI[ind] = null;
		else
			fSI[ind] = new StudyInterface(sW[ind].getDVFile(), sW[ind].getTimeStep(), fSIInfo[ind],
					_transferDebug);
		return true;
	}

	private StudyInterface getIntermediateInterface(int i) {
		return iSI[i];
	}

	private StudyInterface getFinalInterface(int i) {
		return fSI[i];
	}

	int getLastIndex() {
		return lastIndex;
	}

	// CB - changed to thread run() method to try to wait for completion from GUI
	//      it waits, but locks the gui.
	public void run() {
		_gui.println("Preparing for run", true);
		_gui.println("");
		try {
			_gui.setRunSuccessful(false);
			_gui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			StudyInterface sI;
			MYDate termStartDate = new MYDate();
			MYDate termStopDate;
			int resultCode = 0; // to stop running it when there has been an error or user choice
			int lastIndex = 0;  // the last study index "looked at"
			while (true) {
				int studyIndex = 0;
				while (resultCode == 0 && studyIndex < sW.length) {
					// Save the study file info to Study.sty and
					//   check to see if all necessary info is contained in the study
					lastIndex = studyIndex;
					if (!sW[studyIndex].prepareForRun()) { // ONLY NEED TO CHECK IT HERE; NUMSTEPS NOT YET SET
						resultCode = -1;
						break;
					}
					if (_gui.wasStopPushed()) {
						resultCode = 6;
						break;
					}
					++studyIndex;
				}
				studyIndex = 0;
				while (resultCode == 0 && studyIndex < sW.length) {
					lastIndex = studyIndex;
					// Record whether the study has wresl files newer than the executable
					// If the study has wresl files newer than the executable, parse the files
					if (sW[studyIndex].newerFiles()) {
						_gui.println("Parsing " + sW[studyIndex].getStudyName() + "...", true);
						if (!sW[studyIndex].parseFiles()) {
							resultCode = 1;
							break;
						}
						if (_gui.wasStopPushed()) {
							resultCode = 6;
							break;
						}
						_gui.println("Compiling " + sW[studyIndex].getStudyName() + "...", true);
						if (!sW[studyIndex].compile()) {
							resultCode = 2;
							break;
						}
						if (_gui.wasStopPushed()) {
							resultCode = 6;
							break;
						}
						_gui.println("Linking " + sW[studyIndex].getStudyName() + "...", true);
						if (!sW[studyIndex].link()) {
							resultCode = 3;
							break;
						}
						if (_gui.wasStopPushed()) {
							resultCode = 6;
							break;
						}
					}
					++studyIndex;
				}
				if (resultCode != 0) break; // stop the outer while loop
				if (_gui._hideProgressDetails) _gui.println("");
				termStartDate = getEndDate(_runStartDate, - 12); // initialize it so loop correctly increments (for PA)
				termStopDate = getEndDate(_runStartDate, - 1); // initialize it so loop correctly increments (for non-PA)
				boolean[] interfaceCreated = new boolean[sW.length];
				do {
					boolean firstPass = true;
					// if _gui.isDvFileAsInitFile() is true, position analysis is NOT allowed
					studyIndex = lastIndex = 0;
					if (_position) {
						if (MSWUtil.posyr == 0) {
							_numMonths = _yr0periods;
							termStartDate = getEndDate(termStartDate, 12);
							termStopDate = getEndDate(termStartDate, _numMonths - 1);
							if (_wrapperDebug) System.out.println("Set Number of Months to: " + _numMonths);
							if (!firstPass) {
								if (_wrapperDebug) System.out.println("Setting to Start");
								termStartDate.setMonth(MSWUtil.positionStartMonth);
								termStartDate.setYear(new Integer(MSWUtil.posStartYear));
								if (_wrapperDebug) System.out.println("Term stop date = " + termStartDate.toString());
								termStopDate = getEndDate(_runStartDate, _numMonths - 1);
								if (_wrapperDebug) System.out.println("Term stop date = " + termStopDate.toString());
								MSWUtil.positionStart = termStartDate;
							} else if (calculateNumberSteps(studyIndex, _runStartDate, termStopDate) <= 0)
								termStopDate = getEndDate(_runStartDate, _numMonths - 1);
						} else if (MSWUtil.posyr > 0) {
							_numMonths = _afteryr0periods;
							if (_wrapperDebug) System.out.println("Set Number of Months to: " + _numMonths);
							if (_wrapperDebug) System.out.println("Setting to Continue");
							termStartDate = getEndDate(termStopDate, 1);
							setStartDate(studyIndex, termStartDate);
							termStopDate = getEndDate(termStartDate, _numMonths - 1);
						}
					} else {
						termStartDate = getEndDate(termStopDate, 1);
						 // for position, _numMonths & stop date set later
						termStopDate = getEndDate(termStopDate, _numMonths);
					}
					while (resultCode == 0 && studyIndex < sW.length) {
						setStartDate(studyIndex, termStartDate); // Fortran runs based on values in Study, "sty"
						if (_runStopDate.compareTo(termStopDate) < 0) { // next term extends beyond simulation end
							setStopDate(studyIndex, _runStopDate); // Fortran runs based on values in Study, "sty"
						} else setStopDate(studyIndex, termStopDate); // Fortran runs based on values in Study, "sty"
						if (_position) {
							if (MSWUtil.posyr == 0) {
								sW[studyIndex].setIsStudyPositionStartYear(new Boolean(true));
								sW[studyIndex].setStudyInitFile(sW[studyIndex].getInitFile());
								String posStart = new String(MSWUtil.positionStart.getMonth()
									+ MSWUtil.positionStart.getYear());
								String currentStart = new String(_runStartDate.getMonth()
										+ _runStartDate.getYear());
								if (_wrapperDebug) System.out.println(posStart + " " + currentStart);
							} else if (MSWUtil.posyr > 0) {
								setIsStudyPositionStartYear(studyIndex, new Boolean(false));
							}
							if (_numDVFiles > 1) {
								setStudyDvFilename(studyIndex, getPositionDVFilename(studyIndex,
										MSWUtil.dvnum));
								if (_wrapperDebug) System.out.println("Setting DV to: "
										+ getStudyDVFilename(studyIndex));
							}
							AppUtils.nperiods = calculateNumberSteps(studyIndex, termStartDate,
									termStopDate);
						} else {
							setNumberSteps(studyIndex, calculateNumberSteps(studyIndex,
									termStartDate, termStopDate));
							if (_gui.isDvFileAsInitFile()) { // cannot do this here as it depends on the index of the last study
								sW[studyIndex].setStudyInitFile(sW[sW[studyIndex].getInitIndex()].getDVFile());
								if (_wrapperDebug) System.out.println("Set init file to : " + sW[studyIndex].getInitFile());
							}
						}
						lastIndex = studyIndex;
						sW[studyIndex].saveStudyFile();  // need to save here to have num steps????!!!!!!!!!!!

						firstPass = false;
						if (_gui.wasStopPushed()) {
							resultCode = 6;
							break;
						}
						_gui.println("Running " + sW[studyIndex].getStudyName() + "(Study "
								+ studyIndex + ") from " + termStartDate.toString() + " to "
								+ termStopDate.toString() + ".", true);
						if (!sW[studyIndex].execute()) {
							resultCode = 4;
							break;
						}
						if (_gui.wasStopPushed()) {
							resultCode = 6;
							break;
						}

						_gui.setRunSuccessful(true);
						// Check for existence of a file called (ignore case on Windows platform) "error.log".
						// If it exists, there was a fortran error which cannot be "caught" by Java source code
						if (sW[studyIndex].doesErrorFileExist()) {
							resultCode = 5;
							break;
						}

						if (_wrapperDebug) System.out.println("Successful study execution");
						if (_wrapperDebug) System.out.flush();
						// Activate the intermediate interface.
						TimeWindow timeWindow;
						if (!interfaceCreated[studyIndex] && !_position) {
							interfaceCreated[studyIndex] = createStudyInterface(studyIndex);
							if (_transferDebug) System.out.println("Successful intermediate study interface creation");
							if (_transferDebug) System.out.flush();
						}
						if (_transferDebug) System.out.println("Interface: " + getInterfaceInfo(studyIndex));
						if (_transferDebug) System.out.flush();
						if (_transferDebug) System.out.println("It: " + new Boolean(_termNumber < _numDVFiles) + " Pos "
							+ new Boolean(MSWUtil.posyr == 0) + " interface "
							+ new Boolean(!getInterfaceInfo(studyIndex).equals("null")));
						if (_transferDebug) System.out.flush(); //CB - to isolate Leaf's GUI crash
						if (_termNumber < _numDVFiles && MSWUtil.posyr == 0 && _position) {
							System.out.println("Setting Interface to iteration: " + _termNumber);
							_gui.println(sW[studyIndex].getStudyName() + "(Study " + studyIndex +
									") Intermediate Data Transfer from " + termStartDate.toString()
									+ " to " + termStopDate.toString() + ".");
							if (!getInterfaceInfo(studyIndex).equals("null")) {
								StudyInterface s = new StudyInterface(getPositionDVFilename(studyIndex,
										MSWUtil.dvnum), getTimeStep(studyIndex),
										getInterfaceInfo(studyIndex), _transferDebug);
								if (_transferDebug)
									System.out.println("Successful intermediate study interface object creation");
								if (_transferDebug) System.out.flush();
								setPositionInterface(studyIndex, _termNumber, s);
							}
						}
						if ((sI = getIntermediateInterface(studyIndex)) != null) {
							timeWindow = createTimeWindow(studyIndex, termStartDate, termStopDate);
							if (_transferDebug)
								System.out.println("Successful intermediate interface time window creation");
							if (_transferDebug) System.out.flush();
							_gui.println(sW[studyIndex].getStudyName() + "(Study " + studyIndex
									+ ") Intermediate Data Transfer from " + termStartDate.toString()
									+ " to " + termStopDate.toString() + ".", true);
							_gui.println("");
							sI.activate(timeWindow);
							if (_transferDebug) System.out.println("Successful intermediate interface time window activation"); //CB - to isolate Leaf's GUI crash
							if (_transferDebug) System.out.flush();
						}
						// if NOT the last study in the multistudy, transfer the dss data to the next study using the final interface
						if (studyIndex < sW.length - 1) {
							if ((sI = getFinalInterface(studyIndex)) != null && !_position) {
								timeWindow = createTimeWindow(studyIndex, termStartDate, termStopDate);
								if (_transferDebug) System.out.println("Successful final interface time window creation"); //CB - to isolate Leaf's GUI crash
								if (_transferDebug) System.out.flush();
								_gui.println(sW[studyIndex].getStudyName() + "(Study " + studyIndex
										+ ") Data Transfer from " + termStartDate.toString()
										+ " to " + termStopDate.toString() + ".", true);
								_gui.println("");
								sI.activate(timeWindow);
								if (_transferDebug) System.out.println("Successful final interface time window activation"); //CB - to isolate Leaf's GUI crash
								if (_transferDebug) System.out.flush();
							} else if (!getInterfaceInfo(studyIndex).equals("null")) {
								timeWindow = createTimeWindow(studyIndex, termStartDate, termStopDate);
								if (_transferDebug) System.out.println("Successful final interface time window creation"); //CB - to isolate Leaf's GUI crash
								if (_transferDebug) System.out.flush();
								_gui.println(sW[studyIndex].getStudyName() + "(Study " + studyIndex
										+ ") Data Transfer from " + termStartDate.toString()
										+ " to " + termStopDate.toString() + ".", true);
								_gui.println("");
								getPositionInterface(studyIndex, MSWUtil.dvnum).activate(timeWindow);
								if (_transferDebug) System.out.println("Successful final interface time window activation"); //CB - to isolate Leaf's GUI crash
								if (_transferDebug) System.out.flush();
							}

			 				if (!_position) setStudyInitFile(studyIndex,
			 						getStudyDVFilename(sW[studyIndex].getInitIndex())); // TODO: weird??
							else {
								if (MSWUtil.posyr == _numyears - 1) {
									setStudyInitFile(studyIndex, sW[studyIndex].getInitFile());
									if (studyIndex == 0) {
										MSWUtil.posyr = 0;
										MSWUtil.firstPass = false;
										MSWUtil.posStartYear++;
										if (MSWUtil.dvnum == _numyears - 1) MSWUtil.dvnum = 0;
										else MSWUtil.dvnum++;
									}
								} else {
									setStudyInitFile(studyIndex,
											sW[sW[studyIndex].getInitIndex()].getPositionDV(MSWUtil.dvnum));
									if (studyIndex == 0) MSWUtil.posyr++;
								}
							}
						}
						++studyIndex;
						if (studyIndex == sW.length) {
							System.out.print(""); // CB for breakpoint only
						}
					}
					_termNumber++;
					if (resultCode != 0) break; // redundant for protection - stop the inner while loop
				} while (_runStopDate.compareTo(termStopDate) > 0);
				break;
			} // end while (true) block

			if (resultCode == 0) {
				_gui.setRunSuccessful(true);
				_gui.println("");
				_gui.println("Run was successful!");
			} else {
				_gui.setRunSuccessful(false);
				String problem;
				if (resultCode == 1) problem = "a parsing problem";
				else if (resultCode == 2) problem = "a compiling problem";
				else if (resultCode == 3) problem = "a linking problem";
				else if (resultCode == 4) problem = "a non-infeasibility execution problem";
				else if (resultCode == 5) {
					problem = "Infeasibility";
					// Check for existence of a file called (ignore case on Windows platform) "error.log".
					// If it exists, there was a fortran error which cannot be "caught" by Java source code
					if (sW[lastIndex] != null && sW[lastIndex].doesErrorFileExist()) {
						BufferedReader errorReader
							= new BufferedReader(new FileReader(sW[lastIndex].getErrorFileAbsolutePath()));
					    String line = errorReader.readLine();
					    //String msg = "";
					    if (line != null) problem = line; // first line is to contain the primary message
					    line = errorReader.readLine();
					    while (line != null) { // in case there is more than one line
					    	_gui.println(line);
					    	line = errorReader.readLine();
					    }
					    errorReader.close();
					}
				}
				else problem = "user choice";
				String date = " at ";
				if (resultCode == 4 || resultCode == 5 ||(resultCode == 6
						&& termStartDate.getYearIndex() > 1900))
					date += termStartDate.toString();
				String message = "STOPPED at " + sW[lastIndex].getStudyName() + "(Study " + lastIndex
					+ ")" + date + " due to " + problem;	// stop the run early

			    JOptionPane.showMessageDialog(_gui, "Stopped running due to " + problem, "WRIMS Run Incomplete",
				    	JOptionPane.ERROR_MESSAGE);
				_gui.println(message, true);
				_gui.setRunDoneMessage(message);
			}
		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog(null, "Null Pointer found: " + npe, "Null",
				JOptionPane.ERROR_MESSAGE);
			npe.printStackTrace(System.err);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Runtime Exception: " + e, e.getMessage(),
				JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(System.err);
		} finally {
			for (int i = 0; i < sW.length; ++i)
				sW[i] = null;
			sW = null;
			_gui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private String getTimeStep(int studyIndex) {
		return sW[studyIndex].getTimeStep();
	}

	private void setStartDate(int studyIndex, MYDate runStopDate) {
		sW[studyIndex].setStartDate(runStopDate);
	}

	private void setStopDate(int index, MYDate runStopDate) {
		sW[index].setStopDate(runStopDate);
	}

	private String getPositionDVFilename(int studyIndex, int dvIndex) {
		return sW[studyIndex].getDVFile(dvIndex);
	}

	private void setIsStudyPositionStartYear(int studyIndex, Boolean isStartYear) {
		sW[studyIndex].setIsStudyPositionStartYear(isStartYear);
	}

	private String getStudyDVFilename(int studyIndex) {
		return sW[studyIndex].getStudyDVFilename();
	}

	private void setStudyDvFilename(int studyIndex, String DVFilename) {
		sW[studyIndex].setStudyDVFilename(DVFilename);
	}

	private void setStudyInitFile(int studyIndex, String filename) {
		sW[studyIndex].setStudyInitFile(filename);
	}

	private MYDate getEndDate(MYDate sD, int nMonths) {
		MYDate eD = new MYDate();
		int mon;
		int nYears = (int) (nMonths / 12);
		nMonths = nMonths - nYears * 12;
		if ((mon = sD.getMonthIndex() + nMonths) > 12) {
			mon = mon - 12;
			nYears = nYears + 1;
		}
		eD.setMonth(mon);
		eD.setYear(sD.getYearIndex() + nYears);
		return eD;
	}

	void setNumberSteps(int studyIndex, int number) {
		sW[studyIndex].setNumberSteps(String.valueOf(number));
	}

	public int calculateNumberSteps(int studyIndex, MYDate date1, MYDate date2) {
		int numberSteps = 0;
		int day1 = 1;
		int mon1 = date1.getMonthIndex();
		int year1 = date1.getYearIndex();
		int day2 = new Integer(getDaysInMonth(date2)).intValue();
		int mon2 = date2.getMonthIndex();
		int year2 = date2.getYearIndex();
		if (year1 > year2 || (year1 == year2 && mon1 > mon2)) numberSteps = 0;
		else if (sW[studyIndex].getTimeStep().equalsIgnoreCase("1MON")) numberSteps
			= (mon2 - mon1 + 1) + (year2 - year1) * 12;
		else {
			if (year1 == year2) {
				if (mon1 == mon2) numberSteps = day2 - day1 + 1;
				else {
					numberSteps = getDaysInMonth(mon1, year1) - day1 + 1;
					for (int m = mon1 + 1; m < mon2; m++) {
						numberSteps += getDaysInMonth(m, year1);
					}
					numberSteps += day2;
				}
			} else {
				numberSteps = getDaysInMonth(mon1, year1) - day1 + 1;
				for (int m = mon1 + 1; m < 13; m++) {
					numberSteps += getDaysInMonth(m, year1);
				}
				for (int y = year1 + 1; y < year2; y++) {
					numberSteps += 365;
					if (leapYear(y)) numberSteps++;
				}
				for (int m = 1; m < mon2; m++) {
					numberSteps += getDaysInMonth(m, year2);
				}
				numberSteps += day2;
			}
		}
		return numberSteps;
	}

	public String getDaysInMonth(MYDate d) {
		int month = d.getMonthIndex();
		int year = d.getYearIndex();
		return new Integer(getDaysInMonth(month, year)).toString();
	}

	public int getDaysInMonth(int month, int year) {
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

	private void setPositionInterface(int studyIndex, int dvIndex, StudyInterface sI) {
		sW[studyIndex].setPositionInterface(dvIndex, sI);
	}

	private StudyInterface getPositionInterface(int studyIndex, int dvIndex) {
		return sW[studyIndex].getPositionInterface(dvIndex);
	}

	/**
	 * The following method returns a TimeWindow object which is a necessary argument for the
	 * StudyInterface.activate method.
	 *
	 *
	 */

	private TimeWindow createTimeWindow(int studyIndex, MYDate startDate, MYDate endDate) {
		return sW[studyIndex].createTimeWindow(startDate, endDate);
	}

	/**
	 * For testing or possibly automation
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			new MultiStudyWrapper(args[0], new MSWGui());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception" + e,
					e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}
}
