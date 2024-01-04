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

package calsim.app;
import java.util.*;
import java.io.*;
//import javax.swing.JOptionPane;
import com.sun.xml.tree.XmlDocument;
//import com.sun.xml.tree.TreeWalker;
import org.w3c.dom.Element;
import calsim.debug.DebugSetting;

/**
	* Contains all necessary properties required to run a study
 * @author Armin Munevar
 * @version $Id: Study.java,v 1.1.2.13.2.2 2002/06/20 19:12:46 adraper Exp $
 */

public class Study {
  public static boolean DEBUG = false;


  public Study() {
    _name = "";
    _author = "";
    _date = new Date();
    _desc = "";
    _hydrology = "hydrology version";//remove text when working
    _studyDir = "";
    _commonPath = "";
    _filename = "";
    _wreslFile = "";
    _svFile = "";
    _svFileFPart = "";    
    _dvFile = "";
    _initFile = "";
    _initFileFPart = "";
    //DJE*************************
    _timeStep = "";
    _numberSteps = "";
    _startDay = "";
    //****************************
    _startMonth = "";
    _startYear = new Integer(1921);
    _stopDay = "";   //DJE*****
    _stopMonth = "";
    _stopYear = new Integer(1994);
    _simOption="SLP";
    _numberSequences = new Integer(1);
    _solverReport = new Boolean(false);
    _solverList = "NONE";
    _slackReport = new Boolean(false);
    _slackSave = new Boolean(false);
    _addXaOptions = "";
    _svReport = new Boolean(false);
    _svSave = new Boolean(false);
    _dssDebug = new Boolean(false);
    _dssSave = new Boolean(false);
    _genWsiDi = new Boolean(false); //CB set to true for Jianzhong's version that runs many,many WSI-DI runs in a row w/o needing check selection or having popup windows
//    _pricingRHSSensitivity = false; //CB added
//	_activitySensitivity = false; //CB added
    _posAnalysis = new Boolean(false);
    _genNewRandomTable = new Boolean(false);
    _useRestart = new Boolean(false);
    _genRestart = new Boolean(false);
    _dialogWindow = new Boolean(true);
    _posstartyear = new Boolean(false);
  }

  /**
   * Get methods for each property
   */
  public String getCommonPath() {
		return _commonPath;
	}
  public String getName() {
    return _name;
  }
  public String getAuthor() {
    return _author;
  }
  public Date getDate() {
    return _date;
  }
  public String getDescription() {
    return _desc;
  }
  public String getHydrologyVersion() {
    return _hydrology;
  }
  public String getStudyDir() {
    return _studyDir;
  }
  public String getFileName() {
    return _filename;
  }
  public String getWreslFile() {
    return _wreslFile;
  }
  public String getSvFile() {
    return _svFile;
  }

  public String getSvDvFileAPartOption() {
	    return _svdvFileAPartOption;
	  }

  public String getSvFileFPart() {
	    return _svFileFPart;
	  }  
  
  public String getDvFile() {
    return _dvFile;
  }

  public String getInitFile() {
    return _initFile;
  }
  public String getInitFileFPart() {
    return _initFileFPart;
  }
  //DJE*******************************************************
  public String getTimeStep() {
    return _timeStep;
  }
  public String getNumberSteps() {
    return _numberSteps;
  }
  public String getStartDay() {
    return _startDay;
  }
  public String getStopDay() {
    return _stopDay;
  }
  //*********************************************************
  public String getStartMonth() {
    return _startMonth;
  }
  public Integer getStartYear() {
    return _startYear;
  }
  public String getStopMonth() {
    return _stopMonth;
  }
  public Integer getStopYear() {
    return _stopYear;
  }
  public String getSimOption() {
    return _simOption;
  }
  public Integer getNumberSequences() {
    return _numberSequences;
  }
  public Boolean getSolverReportOption() {
    return _solverReport;
  }
  public String getSolverReportList() {
    return _solverList;
  }
  public Boolean getSlackReportOption() {
    return _slackReport;
  }
  public Boolean getSlackReportSaveOption() {
    return _slackSave;
  }
  public String getAddXaOptions() {
    return _addXaOptions;
  }
  public Boolean getSvReportOption() {
    return _svReport;
  }
  public Boolean getSvReportSaveOption() {
    return _svSave;
  }
  public Boolean getDssDebugOption() {
    return _dssDebug;
  }
  public Boolean getDssSaveOption() {
    return _dssSave;
  }
  public Boolean getWsiDiOption() {
    return _genWsiDi;
  }
  public Boolean getPosAnalysisOption() {
    return _posAnalysis;
  }
  public Boolean getGenNewRandomTableOption() {
	    return _genNewRandomTable;
  }
  public Boolean getDialogWindowOption() {
	  return _dialogWindow;
  }
  public Boolean getUseRestartOption() {
    return _useRestart;
  }
  public Boolean getGenerateRestartOption() {
    return _genRestart;
  }
  //CB added
  public boolean getPricingAndRHSSensitivityOption() {
    return _pricingRHSSensitivity;
  }
  //CB added
  public boolean getActivitySensitivityOption() {
    return _activitySensitivity;
  }
  public Boolean getPositionStartYear() {
    return _posstartyear;
  }
  /**
   * Set methods for each property
   */
  public void setCommonPath(String common) {
    _commonPath = common;
	}
  public void setName(String name) {
    _name = name;
  }
  public void setAuthor(String author) {
    _author = author;
  }
  public void setDate(Date date) {
    _date = date;
  }
  public void setDescription(String desc) {
    _desc = desc;
  }
  public void setHydrologyVersion(String hydro) {
    _hydrology = hydro;
  }
  public void setStudyDir(String dir) {
    _studyDir = dir;
  }
  public void setFileName(String fname) {
    _filename = fname;
  }
  public void setWreslFile(String fname) {
    _wreslFile = fname;
    setStudyDir(new File(_wreslFile).getParent());
  }
  public void setSvFile(String fname) {
    _svFile = fname;
  }
  public void setSvFileFPart(String fname) {
	_svFileFPart = fname;
  } 
  public void setDvFile(String fname) {
    _dvFile = fname;
  }
  public void setInitFile(String fname) {
    _initFile = fname;
  }
  public void setInitFileFPart(String fname) {
    _initFileFPart = fname;
  }
  //DJE*********************************************
  public void setTimeStep(String ts) {
    _timeStep = ts;
  }
  public void setNumberSteps(String ns) {
    _numberSteps = ns;
  }
  public void setStartDay(String day) {
    _startDay = day;
  }
  public void setStopDay(String day) {
    _stopDay = day;
  }
  //***********************************************
  public void setStartMonth(String month) {
    _startMonth = month;
  }
  public void setStartYear(Integer year) {
    _startYear = year;
  }
  public void setStopMonth(String month) {
    _stopMonth = month;
  }
  public void setStopYear(Integer year) {
    _stopYear = year;
  }
  public void setSimOption(String option) {
    _simOption = option;
  }
  public void setSvDvFileAPartOption(String option) {
	    _svdvFileAPartOption = option;
	  }
  public void setNumberSequences(Integer number) {
    _numberSequences = number;
  }
  public void setSolverReportOption(Boolean b) {
    _solverReport = b;
  }
  public void setSolverReportList(String s) {
    _solverList = s;
  }
  public void setSlackReportOption(Boolean b) {
    _slackReport = b;
  }
  public void setSlackReportSaveOption(Boolean b) {
    _slackSave = b;
  }
  public void setAddXaOptions(String options) {
    _addXaOptions = options;
  }
  public void setSvReportOption(Boolean b) {
    _svReport = b;
  }
  public void setSvReportSaveOption(Boolean b) {
    _svSave = b;
  }
  public void setDssDebugOption(Boolean b) {
    _dssDebug = b;
  }
  public void setDssSaveOption(Boolean b) {
    _dssSave = b;
  }
  public void setWsiDiOption(Boolean b) {
    _genWsiDi = b;
  }
  public void setPosAnalysisOption(Boolean b) {
    _posAnalysis = b;
  }
  public void setDialogWindowOption(Boolean b) {
	  _dialogWindow = b;
  }
  public void setUseRestartOption(Boolean b) {
    _useRestart = b;
  }
  public void setGenerateRestartOption(Boolean b) {
    _genRestart = b;
  }
  //CB added
  public void setPricingAndRHSSensitivityOption(boolean b) {
    _pricingRHSSensitivity = b;
  }
  //CB added
  public void setActivitySensitivityOption(boolean b) {
    _activitySensitivity = b;
  }
  public void setPositionStartYear(Boolean b) {
    _posstartyear = b;
  }

  public Vector getAllProperties() {
    Vector v = new Vector();
    v.addElement(_name);
    v.addElement(_author);
    v.addElement(_date);
    v.addElement(_desc);
    v.addElement(_hydrology);
    v.addElement(_studyDir);
    v.addElement(_filename);
    v.addElement(_wreslFile);
    v.addElement(_svFile);
    v.addElement(_dvFile);
    v.addElement(_initFile);
    //DJE********************************************************
    v.addElement(_timeStep);

    if (!AppUtils.position) {
		  v.addElement(_numberSteps);
	  } else {
		  v.addElement(Integer.toString(AppUtils.nperiods));
	  }
    v.addElement(_startDay);
    //******************************************
    v.addElement(_startMonth);
    v.addElement(_startYear);
    //DJE removed stop date parameters
    v.addElement(_simOption);
    v.addElement(_numberSequences);
    v.addElement(_solverReport);
    v.addElement(_solverList);
    v.addElement(_slackReport);
    v.addElement(_slackSave);
    v.addElement(_addXaOptions);
    v.addElement(_svReport);
    v.addElement(_svSave);
    v.addElement(_dssDebug);
    v.addElement(_dssSave);
    v.addElement(_genWsiDi);
    v.addElement(_useRestart);
    v.addElement(_genRestart);
 //CB not yet   v.addElement(_pricingRHSSensitivity);
 //CB not yet   v.addElement(_activitySensitivity);
    v.addElement(_svdvFileAPartOption);
    v.addElement(_svFileFPart);    
    v.addElement(_initFileFPart);
    v.addElement(_posAnalysis);
    v.addElement(_dialogWindow);
    v.addElement(_posstartyear);
    return v;
  }

  /**
   * saves all the study data in a binary format
   */
  public void save(String saveFile) throws IOException{
    XmlDocument doc = new XmlDocument();
    _filename = saveFile;
    this.toXml(doc);
    PrintWriter pw = new PrintWriter(new FileOutputStream(saveFile));
    doc.write(pw); pw.close();
    _modified = false;
  }
  
  public Vector getAllPropertiesDescription() {
	    Vector vDesc = new Vector();
	    vDesc.addElement("study name");
	    vDesc.addElement("author");
	    vDesc.addElement("date");
	    vDesc.addElement("desc");
	    vDesc.addElement("hydrology");
	    vDesc.addElement("study dir");
	    vDesc.addElement("study filename");
	    vDesc.addElement("main wreslFile");
	    vDesc.addElement("SV DSS file");
	    vDesc.addElement("DV DSS file");
	    vDesc.addElement("INIT DSS file");
	    //DJE********************************************************
	    vDesc.addElement("timeStep");

	    if (!AppUtils.position) {
			  vDesc.addElement("number of steps");
		  } else {
			  vDesc.addElement("number of steps");
		  }
	    vDesc.addElement("start day");
	    //******************************************
	    vDesc.addElement("start month");
	    vDesc.addElement("start year");
	    //DJE removed stop date parameters
	    vDesc.addElement("sim option");
	    vDesc.addElement("number of sequences");
	    vDesc.addElement("solver report");
	    vDesc.addElement("solver list");
	    vDesc.addElement("slack report");
	    vDesc.addElement("slack save");
	    vDesc.addElement("add XA options");
	    vDesc.addElement("SV report");
	    vDesc.addElement("SV save");
	    vDesc.addElement("DSS debug");
	    vDesc.addElement("DSS save");
	    vDesc.addElement("gen WsiDi");
	    vDesc.addElement("use restart");
	    vDesc.addElement("gen restart");
	 //CB not yet   vDesc.addElement(_pricingRHSSensitivity);
	 //CB not yet   vDesc.addElement(_activitySensitivity);
	    vDesc.addElement("SV DV DSS file A part");
	    vDesc.addElement("SV    DSS file F part");    
	    vDesc.addElement("Init  DSS file F part");
	    vDesc.addElement("pos analysis");
	    vDesc.addElement("show dialog window");
	    vDesc.addElement("pos start year");
	    return vDesc;
	  }

  /**
   * reads all the study data from a file in binary format
   */
  public void load(String loadFile) throws IOException{
    Study sty = this;
    try {
      XmlDocument doc = XmlDocument.createXmlDocument(new FileInputStream(loadFile),false);
      sty.fromXml(doc.getDocumentElement());
    }catch(Exception e){
      e.printStackTrace(System.err);
      throw new IOException("Invalid study file: " + loadFile);
    }
    setFileName(loadFile);
  }
  /**
   * @return true if the study has been changed since a save
   * had been executed
   */
  public boolean isModified(){
    return _modified;
  }
  public boolean isUpdatedStudyObject(){
    return _newStudyObject;
  }
  public void updateStudyObject(){
    _newStudyObject = true;
  }
  /**
   *
   */
  public void fromXml(Element se){
//    TreeWalker tw = new TreeWalker(se);
    setName(se.getAttribute("name"));
    setAuthor(se.getAttribute("author"));
    //    setDate(se.getAttribute("date"));
    setDescription(se.getAttribute("desc"));
    setHydrologyVersion(se.getAttribute("hydrology"));
    setStudyDir(se.getAttribute("dir"));
    setFileName(se.getAttribute("filename"));
    setWreslFile(se.getAttribute("wreslfile"));
    setSvFile(se.getAttribute("svfile"));
    setDvFile(se.getAttribute("dvfile"));
    setSvDvFileAPartOption(se.getAttribute("svdvfileapart"));
    DebugSetting.setDebugOption(se.getAttribute("debug")); //debug setting
    
    if (se.getAttribute("svfilefpart").equals("")){ 
    	_newStudyObject = false;
    	setSvFileFPart(se.getAttribute("name"));
    }
    else{
    	setSvFileFPart(se.getAttribute("svfilefpart"));
    }    
    setInitFile(se.getAttribute("initfile"));
    setInitFileFPart(se.getAttribute("initfilefpart"));
    //DJE*************************************************
    if (se.getAttribute("ts").equals("")) _newStudyObject = false;
    setTimeStep(se.getAttribute("ts"));
    setStartDay(se.getAttribute("startday"));
    setStopDay(se.getAttribute("stopday"));
    //****************************************************
    setStartMonth(se.getAttribute("startmo"));
    setStartYear(new Integer(se.getAttribute("startyr")));
    setStopMonth(se.getAttribute("stopmo"));
    setStopYear(new Integer(se.getAttribute("stopyr")));
    setSimOption(se.getAttribute("simopt"));
    setNumberSequences(new Integer(se.getAttribute("numseq")));
    setCommonPath(se.getAttribute("common"));
  }
  /**
   * Returns a element of an xml document
   */
  public void toXml(XmlDocument doc){
    Element styElement = doc.createElement("study");
    styElement.appendChild(doc.createComment("study xml format"));
    styElement.setAttribute("name", getName());
    styElement.setAttribute("author", getAuthor());
    styElement.setAttribute("date", getDate().toString());
    styElement.setAttribute("desc", getDescription());
    styElement.setAttribute("hydrology", getHydrologyVersion());
    styElement.setAttribute("dir", getStudyDir());
    styElement.setAttribute("filename", getFileName());
    styElement.setAttribute("wreslfile", getWreslFile());
    styElement.setAttribute("svfile", getSvFile());
    styElement.setAttribute("dvfile", getDvFile());
    styElement.setAttribute("svdvfileapart", getSvDvFileAPartOption());    
    styElement.setAttribute("svfilefpart", getSvFileFPart());    
    styElement.setAttribute("initfile", getInitFile());
    styElement.setAttribute("initfilefpart", getInitFileFPart());
    //DJE********************************************************
    styElement.setAttribute("ts", getTimeStep());
    styElement.setAttribute("startday", getStartDay());
    styElement.setAttribute("stopday", getStopDay());
    //***********************************************************
    styElement.setAttribute("startmo", getStartMonth());
    styElement.setAttribute("startyr", getStartYear().toString());
    styElement.setAttribute("stopmo", getStopMonth());
    styElement.setAttribute("stopyr", getStopYear().toString());
    styElement.setAttribute("simopt", getSimOption());
    styElement.setAttribute("numseq", getNumberSequences().toString());
    styElement.setAttribute("common", getCommonPath());
    doc.appendChild(styElement);
  }

  // Study properties
  private String _name;
  private String _author;
  private Date _date;
  private String _desc;
  private String _hydrology;
  private String _studyDir;
  private String _filename;
  private String _wreslFile;
  private String _svFile;
  private String _svFileFPart;  
  private String _dvFile;
  private String _initFile;
  private String _initFileFPart;
  private String _timeStep; //DJE
  private String _numberSteps, _startDay, _stopDay; //DJE
  private boolean _newStudyObject = true;
  private String _startMonth;
  private Integer _startYear;
  private String _stopMonth;
  private Integer _stopYear;
  private String _simOption;
  private String _svdvFileAPartOption;
  private Integer _numberSequences;
  private Boolean _solverReport;
  private String _solverList;
  private Boolean _slackReport;
  private Boolean _slackSave;
  private String _addXaOptions;
  private Boolean _svReport;
  private Boolean _svSave;
  private Boolean _dssDebug;
  private Boolean _dssSave;
  private Boolean _genWsiDi;
  private Boolean _posAnalysis;
  private Boolean _genNewRandomTable;
  private Boolean _useRestart;
  private Boolean _genRestart;
  private boolean _pricingRHSSensitivity; //CB added
  private boolean _activitySensitivity; //CB added
  private Boolean _dialogWindow;
  private Boolean _posstartyear;
  private String _commonPath;
  private boolean _modified;

}

