/*

Copyright (C) 1998, 2000 State of California, Department of Water Resources.

This program is licensed to you under the terms of the GNU General Public
License, version 2, as published by the Free Software Foundation.

You should have received a copy of the GNU General Public License along
with this program; if not, contact Dr. Sushil Arora, below, or the
Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA DEPARTMENT
OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE CALIFORNIA DEPARTMENT OF WATER RESOURCES OR ITS
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

For more information, contact:

Dr. Sushil Arora
California Dept. of Water Resources
Office of State Water Project Planning, Hydrology and Operations Section
1416 Ninth Street
Sacramento, CA  95814
916-653-7921
sushil@water.ca.gov

*/

package calsim.gui;
import calsim.app.*;
//import calsim.app.Study;
//import calsim.app.WsiDiGenerator;
import vista.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
//import javax.swing.table.*;
import calsim.wreslcoder.*;
import calsim.wreslcoder.wresl.WreslParser; //CB added

/**
 * The study tab.  It Provides all the properties in every panel
 * in the study tab and contains the run and check methods
 *
 * @author Armin Munevar
 * @author Clay Booher (altered)
 */

public class StudyTab extends JTabbedPane {
  public static boolean DEBUG = false;

  /**
   * Constructors
   */
  public StudyTab(Study study) {
    _study = study;
    _generalPanel = new GeneralPanel();
    JPanel jPanel = new JPanel(); 
    _inputPanel = new InputPanel();
    jPanel.setBackground(new Color(229,240,203));
    jPanel.setLayout(new BorderLayout());
    jPanel.add(_inputPanel);
    _lookupPanel = new MTab(new LookupUI());
    _optionPanel = new OptionPanel();
    _sensitivityPanel = new SensitivityOptionsPanel();
    _resultPanel = new ResultPanel();
    addTab("General   ", null, _generalPanel, "General panel");
    addTab("System    ", null, jPanel, "System panel");
    addTab("Lookup    ", null, _lookupPanel, "Lookup data Panel");
    addTab("Options  ", null, _optionPanel, "Options panel");
    addTab("Sensitivity", null, _sensitivityPanel, "Sensitivity options panel");
    JPanel resPanel = new JPanel();
    resPanel.setLayout(new BorderLayout());
    resPanel.add(_resultPanel, BorderLayout.CENTER);
    resPanel.add(createRunPanel(), BorderLayout.SOUTH);
    addTab("Run/Result", null, resPanel, "Result panel");
    setLibraryDirectory();
  }

  /**
   * CB added because batch file sets calsim.home Property, but in Eclipse it is in environment variables.
   */
  private void setLibraryDirectory() {
	  if (System.getenv("calsim.home") != null) {
//		  System.out.println("getenv did NOT return null");
		  calsimLibDir = System.getenv("calsim.home") + File.separator+ "lib"; //Eclipse project settings sets
//		  System.out.println("calsimLibDir = " + calsimLibDir);
	  } else {
//		  System.out.println("getenv DID return null");
		  calsimLibDir = System.getProperty("calsim.home") + File.separator+ "lib"; //batch file sets
//		  System.out.println("calsimLibDir = " + calsimLibDir);
	  }
  }

  /**
   * Create the Run and Check panel
   */
  public JPanel createRunPanel(){
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));

    //CB added the following code:
    JCheckBox hideWarnings = new JCheckBox("Hide Warnings");
    hideWarnings.setSelected(true);
    hideWarnings.setBackground(new Color(229,240,203));
    hideWarnings.setToolTipText(
    	"Hides \"local name will be same as global\" warnings for decision variables and timeseries");
    JCheckBox hideProgressDetails = new JCheckBox("Hide Progress Details");
    hideProgressDetails.setBackground(new Color(229,240,203));
    hideProgressDetails.setSelected(true);
    hideProgressDetails.setToolTipText(
    	"Hides the more detailed progress messages such as the successfully parsed file messages");
    JButton run = new JButton("  Run  ");
    JButton check = new JButton("Check");
    JButton compileButton = new JButton("Compile");
    hideWarnings.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			StudyTab.this._hideWarnings =((JCheckBox)e.getSource()).isSelected();
		}
	});
    hideProgressDetails.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			StudyTab.this._hideProgressDetails =((JCheckBox)e.getSource()).isSelected();
		}
	});
	Integer[] delays = {0, 50, 100, 200, 300, 400, 500, 750, 1000};
	JComboBox parseFileDelayBox = new JComboBox(delays);
	parseFileDelayBox.setEditable(true); //allows user-defined entry
	parseFileDelayBox.setSelectedItem(new Integer(_userPrefs.getInt(StudyTab.PARSE_DELAY, 0)));
    parseFileDelayBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int value = ((Integer)((JComboBox)e.getSource()).getSelectedItem()).intValue();
			StudyTab.this._parseFileDelay = value;
			StudyTab.this._userPrefs.putInt(StudyTab.PARSE_DELAY, value);
		}
	});
    run.addActionListener(new GuiTaskListener("Running Study ... ","Done"){
      public void doWork(){
		 runStudy();
      }
    });
    compileButton.addActionListener(new GuiTaskListener("Compiling Study ... ","Done"){
        public void doWork(){
  		 compileStudy();
        }
      });    
    check.addActionListener(new GuiTaskListener("Checking Wresl ... ","Done"){
      public void doWork(){
		 checkStudy();
      }
    });
    //CB added the next three lines
    JPanel parseDelayPanel = new JPanel();
    parseDelayPanel.add(new JLabel("Parse Delay:"));
    parseDelayPanel.add(parseFileDelayBox);
    //panel.add(parseDelayPanel);

    panel.add(hideWarnings);
    panel.add(hideProgressDetails);
    panel.add(check);
    panel.add(compileButton);    
    panel.add(run);
    panel.setBorder(BorderFactory.createEtchedBorder());
    panel.setBackground(new Color(229,240,203));
    return panel;
  }

  /**
   * Set the current study
   */
  public void setStudy(Study study) {
    _study = study;
    _generalPanel.setStudy(_study);
    //_inputPanel.setStudy(_study); //use when we can get a study to have only one set of system tables
    _inputPanel.clearData();
    LookupUI lkui = (LookupUI) _lookupPanel.getMPanel();
    lkui.clearData();
    _optionPanel.setStudy(_study);
  }

  /**
   * Get the current study
   */
  public Study getStudy() {
    updateStudy(_study);
    return _study;
  }
  /**
   * Update the current study
   */
  public void updateStudy(Study study) {
    _study = study;
    _generalPanel.updateStudy(study);
    //		_inputPanel.updateStudy(study);
    _optionPanel.updateStudy(study);
  }

  /**
   * Get the General panel
   */
  public GeneralPanel getGeneralPanel() {
    return _generalPanel;
  }
  /**
   * Get the Input panel
   */
  public InputPanel getInputPanel() {
    return _inputPanel;
  }
  /**
   * Get the Simulate panel
   */
  public OptionPanel getOptionPanel() {
    return _optionPanel;
  }
  /**
   * Get the Result panel
   */
  public ResultPanel getResultPanel() {
    return _resultPanel;
  }

  /**
   * Clears the result text area
   */
  public void clearResultText() {
    _resultPanel.getTextArea().setText("");
  }
  /**
   * Sets a status message in a parallel thread
   */
  public static void setStatusInThread(String msg) {
    GuiUtils.setStatus(msg);
  }
  /**
   * Write study.sty file to current run directory for Fortran
   */
  public void saveStudyFile() {
    Object obj;
    updateStudy(_study);
    Vector v = _study.getAllProperties();
    Vector vDesc = _study.getAllPropertiesDescription();
    try {
      File wreslFile = new File(_study.getWreslFile());
      String studyDir = wreslFile.getParent();
      PrintWriter studyPrintWriter = new PrintWriter(
						     new BufferedWriter(new FileWriter(studyDir+File.separator+"study.sty")));
      PrintWriter studyDescPrintWriter = new PrintWriter(
			                 new BufferedWriter(new FileWriter(studyDir+File.separator+"study.desc")));      
      studyPrintWriter.println("Study File: Generated by WRIMS. Do Not Edit!");
      studyDescPrintWriter.println("Study File Input Variables: Generated by WRIMS");
      for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
				obj = e.nextElement();
				if (obj!=null) studyPrintWriter.println(obj.toString().toUpperCase());
				else studyPrintWriter.println("");
      }
      for (Enumeration eDesc = vDesc.elements() ; eDesc.hasMoreElements() ;) {
			obj = eDesc.nextElement();
			if (obj!=null) studyDescPrintWriter.println(obj.toString());
			else studyDescPrintWriter.println("");
      }
      studyPrintWriter.println("SINGLE");
      studyDescPrintWriter.println("SINGLE");      
      studyPrintWriter.close();
      studyDescPrintWriter.close();      
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  /**
   * Write study.sty file to current run directory for Fortran
   */
  public void saveStudyFile(Study sty) {
    Object obj;
    Vector v = sty.getAllProperties();
    try {
      File wreslFile = new File(sty.getWreslFile());
      String studyDir = wreslFile.getParent();
      PrintWriter studyPrintWriter = new PrintWriter(
						     new BufferedWriter(new FileWriter(studyDir+File.separator+"study.sty")));
      studyPrintWriter.println("Study File: Generated by CALSIM. Do Not Edit!");
      for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
				obj = e.nextElement();
				if (obj!=null) studyPrintWriter.println(obj.toString().toUpperCase());
				else studyPrintWriter.println("");
      }
      studyPrintWriter.println("SINGLE");
      studyPrintWriter.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  /**
   * Check the required study properties
   */
  private boolean containsRequiredProperties() {
    if (_study.getName().length()==0 ||
			_study.getHydrologyVersion().length()==0 ||
			_study.getWreslFile().length()==0 ||
			_study.getSvFile().length()==0 ||
			_study.getDvFile().length()==0)
      return false;
    else return true;
  }
  /**
   * Scans Wresl file dates and compares to executable
   */
  public boolean newerFiles(String wreslFileName) {
    String msg = new String();
    output = new PrintWriter(new DocumentWriter(_resultPanel.getTextArea().getDocument()));
    File wreslFile = new File(wreslFileName);
    String studyDir =	wreslFile.getParent().toUpperCase();
    String common = _study.getCommonPath();
    try {
      _wm = new WreslMaker(studyDir, common, wreslFileName, output, _optionPanel.getLF90OutputOption(), _hideWarnings,
        _hideProgressDetails, _parseFileDelay);
      WreslIncScanner scanner = new WreslIncScanner( wreslFileName);
      if (scanner.getNewerList(WreslMaker.makeExeFile(wreslFileName)).size() > 0)	return true;
    } catch (FileNotFoundException e) {
      msg="File Not Found! "+e.getMessage();
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
    } catch (calsim.wreslcoder.wresl.TokenMgrError e) {
      output.println(e.getMessage());
      msg="Wresl Token Error! ";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
    } finally {
      System.gc();
    }
    return false;
  }
  /**
   * Parses the Wresl files
   */
  public int parseFiles() {
    String msg = new String();
    try {
      msg="Parsing ... ";
      output.println(msg);// CB added - don't worry about using invokeLater or invokeAndWait (msg not read or changed by any code)
      setStatusInThread(msg);
			if (_study.getWreslFile().indexOf("-")!=-1) {
				msg="Illegal Directory Name! No '-' characters are allowed: "+_study.getWreslFile();
				JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
				return 1;
			}
      _wm.parse();
      Integer numberCycles = _study.getNumberSequences();
      Integer numberWreslCycles = new Integer(_wm.getNumberOfWreslCycles());
      if (numberCycles.intValue() != numberWreslCycles.intValue())	{
				msg="Number of sequences must be same as specified in Wresl!";
				JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
				return 1;
      }
    } catch (calsim.wreslcoder.wresl.TokenMgrError e) {
      output.println(e.getMessage());
      if (_hideProgressDetails)
    	  output.println(WreslParser._lastFileParsed.getAbsolutePath()); //CB added
      msg="Wresl Token Error! ";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1; //CB added
    } catch (FileNotFoundException e) {
      if (_hideProgressDetails)
        output.println(WreslParser._lastFileParsed.getAbsolutePath()); //CB added
      msg="File Not Found! "+e.getMessage();
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1; //CB added
    } catch (calsim.wreslcoder.wresl.ParseException e) {
        if (_hideProgressDetails)
      	  output.println(WreslParser._lastFileParsed.getAbsolutePath()); //CB added
      output.println(e.getMessage());
      msg="Wresl Parse Error! ";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1; //CB added
    } finally {
      System.gc();
    }
    return 0;
  }
  /**
   * Compiles the F90 files
   */
  public int compile() {
    String msg = "Compiling ... ";
    setStatusInThread(msg);
    output.println(msg);// CB added - don't worry about using invokeLater or invokeAndWait (msg not read or changed by any code)
    Integer numberCycles = _study.getNumberSequences();
    for (int i=0;i<=numberCycles.intValue();i++) {
      if (!_wm.compile(calsimLibDir, java.lang.String.valueOf(i))) {
				msg="Compilation Error!";
				JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
				output.flush();
				return 1;
      }
    }
    if (!_wm.compileGlobal( calsimLibDir)) {
      msg="Compilation Error!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1;
    }
    return 0;
  }
  /**
   * Links the F90 object code
   */
  public int link() {
    String msg = "Linking ... ";
    setStatusInThread(msg);
    output.println(msg);// CB added - don't worry about using invokeLater or invokeAndWait (msg not read or changed by any code)
    if (!_wm.link( calsimLibDir)) {
      msg="Link Error!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1;
    }
    return 0;
  }
  /**
   * Runs the F90 executable program containing the study
   */
  public int execute() {
    String msg = "Executing ... ";
    setStatusInThread(msg);
    output.println(msg);// CB added - don't worry about using invokeLater or invokeAndWait (msg not read or changed by any code)
    
    // generate random table for position analysis
    if (_optionPanel.getGenNewRandomTableOption()){
    	
    	if (_wm.runTools("RANGEN", "lookup") > 0) {
    		msg="Generate Random Table Error!";
    		JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
    		return 1;
    	}
    }
    
    if (_wm.runModel() > 0) {
      msg="Execution Error!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return 1;
    }
    AppUtils.recalculateDTS();
    Project prj = AppUtils.getCurrentProject();
    if ( prj.getDVFile().equals(_study.getDvFile()) ) prj.setDVHashtable();
    return 0;
  }
  /**
   * Parses, compiles, links, and runs the executable
   */
  public void runall() {
    String msg = new String();
//    int stat;
    saveStudyFile();
    if (containsRequiredProperties()==false) {
      msg="Name, Hydrology Version, Wresl File, and DSS Files are Required!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (newerFiles(_study.getWreslFile())) {
	  long starttime = System.currentTimeMillis();
      if (parseFiles() !=0) return;
	  long finishtime = System.currentTimeMillis();
	  System.out.println("Study parse time = " + (finishtime - starttime)/1000 + " seconds");
	  starttime = System.currentTimeMillis();
      if (compile() !=0) return;
      finishtime = System.currentTimeMillis();
      System.out.println("Study compile time = " + (finishtime - starttime)/1000 + " seconds");
      if (link() !=0) return;
    }
    if (execute() !=0) {
    	return;
		}
  }
  
  public void compileOnly() {
	    String msg = new String();
//	    int stat;
	    saveStudyFile();
	    if (containsRequiredProperties()==false) {
	      msg="Name, Hydrology Version, Wresl File, and DSS Files are Required!";
	      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
	      return;
	    }
	    if (newerFiles(_study.getWreslFile())) {
		  long starttime = System.currentTimeMillis();
	      if (parseFiles() !=0) return;
		  long finishtime = System.currentTimeMillis();
		  System.out.println("Study parse time = " + (finishtime - starttime)/1000 + " seconds");
		  starttime = System.currentTimeMillis();
	      if (compile() !=0) return;
	      finishtime = System.currentTimeMillis();
	      System.out.println("Study compile time = " + (finishtime - starttime)/1000 + " seconds");
	      if (link() !=0) return;
	    }
	  }
  /**
   * Special run for producing Wsi Di Curve
   */
  public void runforWsi() {
    System.out.println("Running for Wsi");
    String msg = new String();
//    int stat;
    saveStudyFile();
    if (containsRequiredProperties()==false) {
      msg="Name, Hydrology Version, Wresl File, and DSS Files are Required!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (newerFiles(_study.getWreslFile())) {
      if (parseFiles() !=0) return;
      if (compile() !=0) return;
      if (link() !=0) return;
    }
	// display dialog to allow entry of info
    JDialog jd = new JDialog();
		jd.setModal(true);
     jd.getContentPane().setLayout(new BorderLayout());
	WsiDiTable table = new WsiDiTable();
    jd.setJMenuBar(table.getJMenuBar());
    jd.setTitle(table.getFrameTitle());
    jd.getContentPane().add(table);
    jd.setSize(500,250);
    jd.setVisible(true);

		// get info from model and initialize generators
    String fname = getStudy().getDvFile();
		WsiDiTableModel model = (WsiDiTableModel) table.getModel();
		if (model.getQuitMode()==true) return;
		String name = new String();
		String wsivar = new String();
		String divar = new String();
		Double wsimax = new Double(0);
		int maxrows = 10;
		int rows = model.getRowCount();
		if (rows==0) {
      msg="No WSI - DI Data!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (rows>maxrows) {
      msg="Too Many Rows! Configured for "+maxrows+" Rows";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
    WsiDiGenerator[] wd = new WsiDiGenerator[maxrows];
		for (int i = 0; i < rows; i++) {
			name =	(String) model.getValueAt(i,0);
			wsivar =(String) model.getValueAt(i,1);
			divar =	(String) model.getValueAt(i,2);
			wsimax = new Double((String) model.getValueAt(i,3));
			wd[i] = new WsiDiGenerator(name,wsivar,divar,wsimax.doubleValue());
 	  	wd[i].setRunDirectory(fname);
		}
		// perform number of runs needed for convergence
		int nruns = 3;
    for (int i=0;i<nruns;i++) {
      System.out.println(i);
      if (i>0) {
        for (int ir=0;ir<rows;ir++)
          wd[ir].load(fname);
      }
      for (int ir=0;ir<rows;ir++)
        wd[ir].execute();
      if (execute() !=0) return;
    }
  }
  /**
   * Special run for position analysis
   */
  public void runPosAnalysis() {
    System.out.println("Running for Position Analysis");
    AppUtils.nperiods = Integer.valueOf(OptionPanel._nper.getText()).intValue();
    //DJE*********************************************
	String msg = new String();
	if (AppUtils.nperiods == 0) {
		msg="Number of position analysis timesteps is zero.";
	    JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
	    return;
	}
	//***************************************************

//		int incyr;
//    int stat;
		updateStudy(_study);
    if (containsRequiredProperties()==false) {
      msg="Name, Hydrology Version, Wresl File, and DSS Files are Required!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
      return;
    }
		int nperiods = AppUtils.nperiods;
    //int nyrs = nperiods/12;
    String dvmaster = _study.getDvFile();
		int dir = dvmaster.lastIndexOf("\\");
		String dirname = dvmaster.substring(0,dir+1);
	/*	Hashtable monthIndex = new Hashtable(12);
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
		Integer bmo = (Integer) monthIndex.get(_study.getStartMonth());
		Integer emo = (Integer) monthIndex.get(_study.getStopMonth());
		System.out.println("Before: "+nyrs);
		System.out.println("Before: "+ emo.intValue() + " " + bmo.intValue());
*/
	//	if (emo.intValue() - bmo.intValue() >= -1) nyrs = nyrs;
		//else nyrs++;
    int ndv = nperiods/12;
    if (nperiods - 12*ndv > 0) ndv++;
		int byr = Integer.valueOf(OptionPanel._start.getSelectedItem().toString()).intValue();
		int eyr = Integer.valueOf(OptionPanel._stop.getSelectedItem().toString()).intValue();
		int dvnum = 1;
		String fname = "";
		_study.setPositionStartYear(new Boolean(true));
		for (int i=byr;i<eyr+1;i++) {
			//System.out.println("Start Year: "+_study.getPositionStartYear());
			_study.setStartYear(new Integer(i));
			//_study.setStopYear(new Integer(i+nyrs));
			//System.out.println("Stop Year is: " + _study.getStopYear());
			if (ndv > 1) {
				fname = new String(dirname + "position" + new Integer(dvnum).toString() + "dv.dss");
				_study.setDvFile(fname);
				System.out.println("DV: "+fname);
				if (dvnum == ndv) dvnum = 1;	else dvnum++;
			}
    	saveStudyFile(_study);
			if (i==byr) {
    		if (newerFiles(_study.getWreslFile())) {
	      	if (parseFiles() !=0) return;
	      	if (compile() !=0) return;
	      	if (link() !=0) return;
	    	}
			}
			System.out.println("Running "+_study.getStartMonth()+_study.getStartYear()+"-"+_study.getStopMonth()+_study.getStopYear());
	    if (execute() !=0) return;
		}
/*
		if (emo.intValue() - bmo.intValue() >= 0) incyr = 0;
		else incyr = 1;
		int byr = _study.getStartYear().intValue();
		int eyr = _study.getStopYear().intValue();
		for (int i=byr;i<eyr;i++) {
			_study.setStartYear(new Integer(i));
			_study.setStopYear(new Integer(i+incyr));
    	saveStudyFile(_study);
			if (i==byr) {
    		if (newerFiles(_study.getWreslFile())) {
	      	if ( (stat=parseFiles()) !=0) return;
	      	if ( (stat=compile()) !=0) return;
	      	if ( (stat=link()) !=0) return;
	    	}
			}
			System.out.println("Running "+_study.getStartMonth()+_study.getStartYear()+"-"+
																		_study.getStopMonth()+_study.getStopYear());
	    if ( (stat=execute()) !=0) return;
		}
*/
  }
  /**
   * Runs the current study
   */
  public void runStudy() {
    clearResultText();
    saveStudyFile();
    //DJE*********************************************
    String msg = new String();
    if (_study.getNumberSteps().equals("NONE")) {
        msg="Number of Time Steps: NONE";
        JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
        return;
    }
    //***************************************************
    if (_study.getWsiDiOption().booleanValue() == true)
    	runforWsi();
    else if (_study.getPosAnalysisOption().booleanValue() == true)
    	runPosAnalysis();
    else runall();
    output.close();
  }
  
  public void compileStudy() {
	    clearResultText();
	    saveStudyFile();
	    //DJE*********************************************
	    String msg = new String();
	    if (_study.getNumberSteps().equals("NONE")) {
	        msg="Number of Time Steps: NONE";
	        JOptionPane.showMessageDialog(_generalPanel,msg,"Error",JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    //***************************************************

	    compileOnly();
	    output.close();
	  }
  /**
   * Checks the current study
   */
  public void checkStudy() {
    String msg = new String();
    clearResultText();
    saveStudyFile();
    if (newerFiles(_study.getWreslFile())) {
      msg="Parsing ... ";
      setStatusInThread(msg);
      parseFiles();
      msg="Parsing Complete!";
      setStatusInThread(msg);
      JOptionPane.showMessageDialog(_generalPanel,msg,"Calsim Info",JOptionPane.INFORMATION_MESSAGE);
    }	else {
      msg="No Changed Files!";
      JOptionPane.showMessageDialog(_generalPanel,msg,"Calsim Info",JOptionPane.INFORMATION_MESSAGE);
    }
    output.close();
  }
  /**
   * Class variables
   */
  private Study _study;
  private GeneralPanel _generalPanel;
  private InputPanel _inputPanel;
  private MTab _lookupPanel;
  private OptionPanel _optionPanel;
  private SensitivityOptionsPanel _sensitivityPanel; //CB added
  private ResultPanel _resultPanel;
  private WreslMaker _wm;
//CB  private static String calsimLibDir = System.getProperty("calsim.home") + File.separator+ "lib";
  private static String calsimLibDir; //CB
  public static PrintWriter output;
  private boolean _hideWarnings = true; //CB added
  private boolean _hideProgressDetails = true;  //CB added
//  private boolean _hasUserMappedOpenProblem = true; //CB added
  private int _parseFileDelay = 0; //CB added to fix the "...user-mapped-section-open..." problem during parsing (on some machines, some of the time)
  public final static String PARSE_DELAY = "Parse Delay"; //CB added to fix the "...user-mapped-section-open..." problem during parsing (on some machines, some of the time)
  protected Preferences _userPrefs = Preferences.userNodeForPackage(getClass());;
}

