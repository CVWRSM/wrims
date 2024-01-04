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
import vista.set.*;
import vista.app.*;
import vista.graph.*;
import vista.time.*;
import vista.db.dss.*;
import vista.gui.*;
import calsim.gym.*;
import javax.swing.*;
import java.awt.*;
import calsim.gui.*;
import com.sun.java.util.collections.Arrays;
import com.sun.java.util.collections.Comparator;
/**
 * Common utility functions for App package
 *
 * @author Nicky Sandhu
 * @version $Id: AppUtils.java,v 1.1.4.74.2.1 2002/06/20 19:12:45 adraper Exp $
 */
public class AppUtils{
  public static boolean DEBUG = false;
  /**
   * true if graph is needed
   */
  public static boolean viewGraph = true;
  /**
   *
   */
  public static boolean viewTable = false;
  /**
   *
   */
  public static boolean resTable = false;

  public static boolean position = false;

  public static int _numLevels = 5;

  public static int _numYearsMax = 201;  //CB to 2050 end year  TODO needs to be user-settable
  /**
   *
   */
  public static boolean viewMonthlyTable = false;
  /**
    *
    */
  public static boolean useCFS = true;
  /**
    *
    */
  public static boolean useStoredUnits = false;
  /**
    *
    */
  public static boolean plotComparitive = false;
  /**
    *
    */
  public static boolean plotDifference = false;
  /**
    * true if monthly report shows data by water year
    */
  public static boolean _isWaterYear = true;
  /**
    * Integer (1 - 12 same as Oct - Sep) that says what month to start each year on monthly display
    */
  public static String _startMonth = "Oct";
  /**
    * true if monthly report shows data by user specified month
    */
  public static boolean _isStartMonth = false;
  /**
    * sort by water year type classification
    */
  public static boolean _show60_20_20 = false;
  /**
    * sort by water year type classification
    */
  public static boolean _show40_30_30 = false;
  /**
   * the svar string
   */
  public static String SVAR = "SVAR";
  /**
   * the dvar string
   */
  public static String DVAR = "DVAR";
  /**
   * eliminates bug when diff selected and dv and sv dss files contain same variable
   */
  public static boolean isDvarsFilter = true;
  /**
    * units string for flow in cubic feet per second
    */
  public static String CFS = "CFS";
  /**
    * units string for flow in thousand acre-feet/ month
    */
  public static String TAF = "TAF";
  /**
    * Mode String for Base Mode
    */
  public static String BASE = "BASE";
  /**
    * Mode String for Comp Mode
    */
  public static String COMP = "COMP";
  /**
    * Mode String for Diff Mode
    */
  public static String DIFF = "DIFF";
  /**
    *On/Off switch for Base DSS File
    */
  public static boolean baseOn = true;
  /**
    *On/Off switch for comp1 DSS File
    */
  public static boolean comp1On = false;
  /**
    *On/Off switch for comp2 DSS File
    */
  public static boolean comp2On = false;
  /**
    *On/Off switch for comp3 DSS File
    */
  public static boolean comp3On = false;
  /**
    * the default time window
    */
  public static String DEFAULT_TIME_WINDOW = "OCT1921 - SEP2003";
  /**
    *
    */
  public static int basenum = 0, comp1num = 1, comp2num = 2, comp3num = 3;
  /**
    *Nperiods for position analysis
    */
	public static int nperiods = 0;
  /**
    * the default sizes for plots
    */
  public static Dimension DEFAULT_PLOT_SIZE = new Dimension(750,650);
  public static Dimension DEFAULT_TABLE_SIZE = new Dimension(300,700);
  public static Dimension DEFAULT_MT_SIZE = new Dimension(750,300);

  /**
   * the current project
   */
  private static Project _currentProject = new Project();
  /**
   * the current study
   */
  private static Study _currentStudy = new Study();
  /**
   * the global lists
   */
  private static Hashtable _dtsList = new Hashtable();
  private static Hashtable _mtsList = new Hashtable();
  /**
   * the global list files
   */
  private static String [] _dtsFiles = null; /*{
    "BanksTotalWheel.csv",
    "ComputedEIRatio.csv",
    "ContraCostaExport.csv",
    "DeltaExport.csv",
    "DeltaExportForRatio.csv",
    "DeltaInflowForRatio.csv",
    "BanksTotalSWP.csv",
    "NorthBay.csv",
    "TotalCVPExport.csv",
    "TotalSWPExport.csv",
    "TracyDeltaChannel.csv",
    "BanksDeltaChannel.csv",
    "EIRatioStandard.csv",
    "cvp-deliv-ag.csv",
    "cvp-deliv-ex.csv",
    "cvp-deliv-ls.csv",
    "cvp-deliv-othmi.csv",
    "cvp-deliv-rf.csv",
    "cvp-deliv-total-nolosses.csv",
    "cvp-deliv-total.csv",
    "cvp-dem-ag.csv",
    "cvp-dem-ex.csv",
    "cvp-dem-ls.csv",
    "cvp-dem-othmi.csv",
    "cvp-dem-rf.csv",
    "cvp-dem-total-nolosses.csv",
    "cvp-dem-total.csv",
    "cvp-short-ag.csv",
    "cvp-short-ex.csv",
    "cvp-short-ls.csv",
    "cvp-short-othmi.csv",
    "cvp-short-rf.csv",
    "cvp-short-total-nolosses.csv",
    "cvp-short-total.csv",
    "swp-dem-ag.csv",
    "swp-dem-int.csv",
    "swp-dem-losses.csv",
    "swp-dem-mwdmi.csv",
    "swp-dem-othmi.csv",
    "swp-dem-total-nolosses.csv",
    "swp-dem-total.csv",
    "swp-short-ag.csv",
    "swp-short-int.csv",
    "swp-short-losses.csv",
    "swp-short-othmi.csv",
    "swp-short-mwdmi.csv",
    "swp-short-total-nolosses.csv",
    "swp-short-total.csv",
    "swp-deliv-ag.csv",
    "swp-deliv-alameda-sclara.csv",
    "swp-deliv-antelope.csv",
    "swp-deliv-castaic.csv",
    "swp-deliv-coachella.csv",
    "swp-deliv-crestline.csv",
    "swp-deliv-desert.csv",
    "swp-deliv-devils_den.csv",
    "swp-deliv-dudley-ridge.csv",
    "swp-deliv-empire.csv",
    "swp-deliv-ent.csv",
    "swp-deliv-int.csv",
    "swp-deliv-kcwa.csv",
    "swp-deliv-kings.csv",
    "swp-deliv-littlerock.csv",
    "swp-deliv-losses.csv",
    "swp-deliv-mojave.csv",
    "swp-deliv-mi.csv",
    "swp-deliv-mwdmi.csv",
    "swp-deliv-napa-solano.csv",
    "swp-deliv-oakflat.csv",
    "swp-deliv-othmi.csv",
    "swp-deliv-palmdale.csv",
    "swp-deliv-sbernardino.csv",
    "swp-deliv-sgarbriel.csv",
    "swp-deliv-sgorgonio.csv",
    "swp-deliv-sobispo-sbarbr.csv",
    "swp-deliv-sobispo-tulare.csv",
    "swp-deliv-total-nolosses.csv",
    "swp-deliv-total.csv",
    "swp-deliv-ventura.csv",
    //"dpvampactual.csv",
    //    "DPreqPM.csv",
    //"goodwin.csv",
    //"stanmaze.csv",
    //"mcvampactual.csv",
    //    "MCreqPM.csv",
    //    "NMactualPM.csv",
    //"nmwqactual.csv",
    //"NMreqPM.csv",
    //"nmreqwq.csv",
    //"stan_f&w.csv",
    //"vernec.csv",
    //"vernalispmreq.csv",
    //"vernflow.csv",
    //"wqstd.csv",
    "RioVistaFlow.csv",
    "netdcu.csv",
    "RioVistaStandard.csv",
    "XChanFlow.csv",
    "XChanGatePos.csv"
  };*/
  private static String [] _mtsFiles = {
    "exp-compare.csv",
    "ei-compare.csv"
  };
  /**
   * global list initialization code. usually not a good idea to
   * anonymously execute code. Will find a place for this in some
   * main method later ??
   */
  static{
    try {
      initializeGlobalLists();
    }catch(Exception e){
      System.err.println("Could not load global lists");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
  /**
   * initializes the global lists
   */
  static void initializeGlobalLists(){
//    Project prj = AppUtils.getCurrentProject();
    if (_dtsFiles == null) return;
    for(int i=0; i < _dtsFiles.length; i++){
      try {
				InputStream is =
	  		VistaUtils.getResourceAsStream("/calsim/app/data/"+_dtsFiles[i]);
				DerivedTimeSeries dts = DerivedTimeSeries.load(is);
				_dtsList.put(dts.getName(),dts);
				//	if ( prj != null) prj.remove(dts);
      }catch(Exception ioe){
				if (DEBUG) System.err.println("Error in loading dts from : " + _dtsFiles[i]);
      }
    }
    for(int i=0; i < _mtsFiles.length; i++){
      try {
				InputStream is =
	  		VistaUtils.getResourceAsStream("/calsim/app/data/"+_mtsFiles[i]);
				MultipleTimeSeries mts = MultipleTimeSeries.load(is);
				_mtsList.put(mts.getName(),mts);
				//	if ( prj != null) prj.remove(mts);
      }catch(Exception ioe){
				if (DEBUG) System.err.println("Error in loading mts from : " + _mtsFiles[i]);
      }
    }
  }

  public static String[] getYearArray() {
	    String[] years = new String[AppUtils._numYearsMax];
	    for(int i = 0; i < AppUtils._numYearsMax; i++)
	      years[i] = "" + (1900 + i);
	    return years;
	  }

  /**
   * creates a derived time series from a file
   * Pre-conditions:
   * 1. The file must exist and be of a pre-defined format
   * ... ( to be decided)
   * Post-conditions:
   * 1. If file is in incorrect format or the references
   * described in the file are not found throw a runtime
   * exception ( CalsimException .... to be decided)
   */
  public static DerivedTimeSeries createFromFile(String file){
    try {
      return DerivedTimeSeries.load(file);
    }catch(Exception e){
      System.err.println("Exception: " + e.getMessage() + " reading file: " + file);
      return null;
    }
  }

  public static void recalculateDTS() {
	Project prj = getCurrentProject();
//	String[] names = prj.getDTSNames();
    //System.out.println(names[0]);
 	if (getCurrentProject().getDTSNames() == null) return;
	DerivedTimeSeries[] dts = prj.getDTSList();
	for(int i=0; i < dts.length; i++) {
	  dts[i].setRecalculate(true);
	}
  }

  public static int getCheckedDSS() {
		int num = 0;
		if (baseOn) {basenum = num; num++;} else {basenum = 5;}
		if (comp1On) {comp1num = num; num++;} else {comp1num = 5;}
		if (comp2On) {comp2num = num; num++;} else {comp2num = 5;}
		if (comp3On) {comp3num = num; num++;} else {comp3num = 5;}
		return num;
	}

  /**
   * returns a data reference which exists in a dss file/group with
   * an exact b part "bpart" and and exact c part "cpart" and
   * a time window. If no data reference is found a null is
   * returned.
   * Pre-conditions:
   * 1. If either part is null only one part is matched and the
   * first reference matching is returned.
   * 2. If both parts are null then null is returned.
   * 3. If time window is greater then existing time window a
   * reference with intersecting time window is returned. If
   * no reference is
   * 4. If time window is null the default time window in the
   * group is returned
   * Post-conditions:
   * 1. If more than one data reference is matched then a
   * warning message is printed to the output and the first matching
   * is returned.
   * 2. If an error occurs or if the inputs are not valid or if
   * nothing is found for the given inputs a null reference is
   * returned.
   *
   */
  public static DataReference getDataReference(Group dssGroup,
					       String bpart, String cpart, TimeWindow tw){
    DataReference ref = null;
    if ( dssGroup == null ) return null;
    Group gc = Group.createGroup(dssGroup);
    // do mapping

    //
    if ( bpart != null && !bpart.equals(""))
      gc.filterBy(true, new PathPartPredicate("^"+bpart+"$",Pathname.B_PART));
    // no bpart found
    if ( gc.getNumberOfDataReferences() == 0 ){
      System.err.println("No matching reference in " + dssGroup.getName()
			 + " for bpart = " + bpart);
      throw new RuntimeException("No matching reference in " + dssGroup.getName()
				 + " for bpart = " + bpart);
      //      return null;
    }
    if ( cpart != null && !cpart.equals(""))
      gc.filterBy(true, new PathPartPredicate("^"+cpart+"$",Pathname.C_PART));
    if ( gc.getNumberOfDataReferences() > 1){
      System.err.println("Warning: " + dssGroup.getName()
			 + " has more than one references for bpart = " + bpart
			 + " and cpart = " + cpart);
      ref = gc.getDataReference(0);
    } else if (gc.getNumberOfDataReferences() == 0 ){
      throw new RuntimeException("No matching reference in " + dssGroup.getName()
				 + " for cpart = " + cpart + " & bpart = " + bpart);
    } else{
      ref = gc.getDataReference(0);
    }
    // set time window
    if (ref != null && tw != null) ref = DataReference.create(ref,tw);
    return ref;
  }
	/**
	 * gets the pathnames for the matching parts array and fileType
	 * If any part is null it is ignored for a match.  This method
	 * @param parts The pathname part from A to F
	 * @param fileType AppUtils.SVAR or AppUtils.DVAR
	 * @return an array of data references or null if no file found
	 */
	public static DataReference [] getDataReferences(String [] parts, String fileType) {
		Group dssGroup = null;
		if (fileType.equals(SVAR)) dssGroup = AppUtils.getCurrentProject().getSVGroup();
    	else dssGroup = AppUtils.getCurrentProject().getDVGroup();
		if (dssGroup == null) {
			if (fileType.equals(DVAR))
				JOptionPane.showMessageDialog(null, "Load a base DV dss file before filtering",
					"Base DV dss file Not Loaded", JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Load a base SV dss file before filtering",
					"Base SV dss File Not Loaded", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();

		Group gc = Group.createGroup(dssGroup);
		for(int i=0; i < parts.length; i++) {
			String part = parts[i];
			if (i == Pathname.D_PART) continue;
			if (parts[i] != null) part = replace(parts[i],"*",".*?");
			if (part != null && !part.equals("")) {
				part = "^"+part+"$";
				gc.filterBy(true, new PathPartPredicate(part,i));
			}
			if (gc.getNumberOfDataReferences() == 0){
				throw new RuntimeException("No matching reference in " + dssGroup.getName()
				   + " for part = " + part);
			}
		}
		int count = gc.getNumberOfDataReferences();
		DataReference [] refs = new DataReference[count];
		for (int i=0; i< count; i++) {
			DataReference ref = DataReference.create(gc.getDataReference(i),tw);
			// some references may be non-time series
			refs[i] = ref == null ? gc.getDataReference(i) : ref;
		}
    	return refs;
	}
  /**
   * opens a dss file or returns null if it could not open file
   */
  public static Group openDSSFile(String dssfile){
    if (dssfile == null ) return null;
    try {
      return DSSUtil.createGroup("local",dssfile);
    }catch(Exception e){
      System.err.println("Exception: " + e.getMessage());
      return null;
    }
  }
  /**
   * plots a single data reference. This is a preliminary style of plotting
   * based on vista. This can be improved later ??
   */
  public static JFrame plot(DataReference ref){
    DefaultGraphBuilder gb = new DefaultGraphBuilder();
    gb.addData(ref);
    Graph graph[] = gb.createGraphs();
    for(int i=0; i < graph.length; i++){
      MultiPlot mp = null;
      if ( graph[i].getPlot() instanceof MultiPlot )
	mp = (MultiPlot) graph[i].getPlot();
      else
	mp = null;
      Plot [] plots = null;
      if ( mp != null )
	plots = mp.getAllPlots();
      else
	plots = new Plot[] { graph[i].getPlot() };
      for(int j=0; j < plots.length; j++){
	Axis baxis = plots[j].getAxis(AxisAttr.BOTTOM);
	TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();
	Time stime = tw.getStartTime();
	stime = stime.create(stime);
	stime.incrementBy(TimeFactory.getInstance().createTimeInterval("-1MON"));
	Time etime = tw.getEndTime();
	baxis.setDCRange(stime.getTimeInMinutes(), etime.getTimeInMinutes());
      }
    }
    DataGraph dg = new DataGraph(graph[0],"Graph",false);
    dg.setSize(DEFAULT_PLOT_SIZE); // set to 8.5x11 for landscape printing.
    Toolkit tk = dg.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = dg.getSize();
    dg.setLocation(screenSize.width - frameSize.width,
		   screenSize.height - frameSize.height);
    return dg;
  }
  /**
   * plots a single data reference. This is a preliminary style of plotting
   * based on vista. This can be improved later ??
   */
  public static JFrame plot(DataReference [] refs){
    if ( refs == null ) return null;
    if ( refs.length == 1 ) {
      return plot(refs[0]);
    }
    DefaultGraphBuilder gb = new DefaultGraphBuilder();
    for(int i=0; i < refs.length; i++){
      if ( refs[i] != null )
	gb.addData(refs[i]);
    }
    Graph graph[] = gb.createGraphs();
    if ( graph == null ) return null;
    DataGraph dg = new DataGraph(graph[0],"Graph", false);
    dg.setSize(DEFAULT_PLOT_SIZE); // set to 8.5x11 for landscape printing.
    Toolkit tk = dg.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = dg.getSize();
    dg.setLocation(screenSize.width - frameSize.width,
		   screenSize.height - frameSize.height);
    return dg;
  }
  /**
   * tabulates a single data reference based on vista's DataTable
   * @see vista.app.DataTable
   */
  public static JFrame tabulate(DataReference ref){
    if ( ref == null ) return null;
    JFrame fr = new DataTable(ref, false);
    Toolkit tk = fr.getToolkit();
    fr.setSize(DEFAULT_TABLE_SIZE);
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = fr.getSize();
    fr.setLocation(screenSize.width - frameSize.width,
		   screenSize.height - frameSize.height);
    return fr;
  }
  /**
   * tabulates a single data reference based on vista's DataTable
   * @see vista.app.DataTable
   */
  public static JFrame tabulate(DataReference [] refs){
    JFrame fr = null;
    if ( refs == null ) return fr;
    if ( refs.length == 1 ){
      fr = new DataTable(refs[0], false);
    }
    fr = new MultiDataTable(refs, false);
    Toolkit tk = fr.getToolkit();
    fr.setSize(DEFAULT_TABLE_SIZE);
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = fr.getSize();
    fr.setLocation(screenSize.width - frameSize.width,
		   screenSize.height - frameSize.height);
    return fr;
  }
  /**
   *
   */
  public static JFrame monthlyTable(DataReference ref){
    MonthlyTableDisplay mtd = null;
    if ( _show60_20_20 && MT_60_20_20 != null ){
      mtd = new MonthlyTableDisplay( ref, _isWaterYear, _isStartMonth, _startMonth, MT_60_20_20 );
    } else if ( _show40_30_30 && MT_40_30_30 != null ){
      mtd = new MonthlyTableDisplay( ref, _isWaterYear, _isStartMonth, _startMonth, MT_40_30_30 );
    } else {
      mtd = new MonthlyTableDisplay(ref, _isWaterYear, _isStartMonth, _startMonth, null);
    }
    JFrame fr = new calsim.gui.DefaultFrame(mtd);
    fr.setSize(DEFAULT_MT_SIZE);
    Toolkit tk = fr.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = fr.getSize();
    fr.setLocation(screenSize.width - frameSize.width, screenSize.height - frameSize.height);
    return fr;
  }
  /**
   *
   */
  public static JFrame monthlyTable(DataReference [] refs){
    MonthlyTableDisplay mtd = null;
    if ( _show60_20_20 && MT_60_20_20 != null ){
      mtd = new MonthlyTableDisplay( refs, _isWaterYear, _isStartMonth, _startMonth, MT_60_20_20 );
    } else if ( _show40_30_30 && MT_40_30_30 != null ){
      mtd = new MonthlyTableDisplay( refs, _isWaterYear, _isStartMonth, _startMonth, MT_40_30_30 );
    } else {
      mtd = new MonthlyTableDisplay(refs, _isWaterYear, _isStartMonth, _startMonth, null);
    }
    JFrame fr = new calsim.gui.DefaultFrame(mtd);
    fr.setSize(DEFAULT_MT_SIZE);
    Toolkit tk = fr.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    Dimension frameSize = fr.getSize();
    fr.setLocation(screenSize.width - frameSize.width,
		   screenSize.height - frameSize.height);
    return fr;
  }
  /**
   * @returns true if the given data reference represents a
   * state variable
   */
/*CB  public static boolean isStateVariable(DataReference ref){
    return ref.getFilename().toLowerCase().trim().endsWith("sv.dss");
  } */
  /**
   * @returns true if the given data reference represents a
   * decision variable
   */
/*  public static boolean isDecisionVariable(DataReference ref){
    return ref.getFilename().toLowerCase().trim().endsWith("dv.dss");
  } */
  /**
   * gets the global list for DTS
   */
  public static DerivedTimeSeries [] getGlobalDTSList(){
    DerivedTimeSeries [] dts = new DerivedTimeSeries[_dtsList.size()];
    int count=0;
    for(Enumeration e = _dtsList.elements(); e.hasMoreElements();){
      dts[count]=(DerivedTimeSeries) e.nextElement();
      count++;
    }
    return dts;
  }
  /**
   * gets the global list for MTS
   */
  public static MultipleTimeSeries [] getGlobalMTSList(){
    MultipleTimeSeries [] mts = new MultipleTimeSeries[_mtsList.size()];
    int count=0;
    for(Enumeration e = _mtsList.elements(); e.hasMoreElements();){
      mts[count]=(MultipleTimeSeries)e.nextElement();
      count++;
    }
    return mts;
  }
  /**
    *
    */
  public static DerivedTimeSeries getGlobalDTS(String name){
    return (DerivedTimeSeries) _dtsList.get(name.toUpperCase().trim());
  }

  public static void clearDTSList() {
    _dtsList.clear();
  }

  public static void clearMTSList() {
    _mtsList.clear();
  }

  /**
    *
    */
  public static MultipleTimeSeries getGlobalMTS(String name){
    return (MultipleTimeSeries) _mtsList.get(name.toUpperCase().trim());
  }
  /**
   * get the current project
   */
  public static Project getCurrentProject(){
    return _currentProject;
  }
  /**
   * set the current project. This is done to define the project
   * in the current context
   */
  public static void setCurrentProject(Project p){
    _currentProject = p;
  }
  /**
   * get the current study
   */
  public static Study getCurrentStudy(){
    return _currentStudy;
  }
  /**
   * set the current study.
   */
  public static void setCurrentStudy(Study s){
    _currentStudy = s;
  }
  /**
   * This function searches thro' the names of the global DTS and
   * MTS lists as well as the current projects lists to check
   * for a name clash
   * @return true if name is taken
   */
  public static boolean nameNotTaken(String name){
    if ( _dtsList.containsKey(name) )
      return false;
    else if ( _mtsList.containsKey(name))
      return false;
    else if ( getCurrentProject().getDTS(name) != null )
      return false;
    else if ( getCurrentProject().getMTS(name) != null )
      return false;
    else
      return true;
  }
  /**
   * gets the operation id which is one of the id's defined
   * in TimeSeriesMath class
   * @param operationStr is one of +,-,*,/
   * @return the operation id or -1 if none is found
   * @see vista.set.TimeSeriesMath
   */
  public static int getOperationId(String operationStr){
    String str = operationStr;
    if ( str.equals("+") ){
      return TimeSeriesMath.ADD;
    } else if ( str.equals("-") ){
      return TimeSeriesMath.SUB;
    } else if ( str.equals("*") ){
      return TimeSeriesMath.MUL;
    } else if ( str.equals("/") ){
      return TimeSeriesMath.DIV;
    } else {
      return -1;
    }
  }
  /**
   * gets the operation name for the given operation id
   * @param operation id one of TimeSeriesMath.ADD|SUB|MUL|DIV
   * @see vista.set.TimeSeriesMath
   */
  public static String getOperationName(int operationId){
    switch(operationId){
    case TimeSeriesMath.ADD: return "+";
    case TimeSeriesMath.SUB: return "-";
    case TimeSeriesMath.MUL: return "*";
    case TimeSeriesMath.DIV: return "/";
    default: return "?";
    }
  }
  /**
   * displays error by writing it out to the error stream.
   * This can be changed later to display the message to some
   * console or dialog window. ??
   */
  public static void displayError(String msg){
    System.err.println(msg);
  }
  /**
   * This method attempts the guess the time window from the
   * given group by filtering for "FLOW" and then reading upto
   * five pathnames and taking their intersection
   */
  public static TimeWindow guessTimeWindowFromGroup(Group g){
    if ( g == null ) return null;
    Group gc = Group.createGroup(g);
    TimeWindow tw = null;
    gc.filterBy("FLOW"); // look at flow pathnames
    if ( gc.getNumberOfDataReferences() == 0 ) return null;
    int count = gc.getNumberOfDataReferences();
    int max5 = Math.max(count,5);
    tw = gc.getDataReference(0).getTimeWindow();
    for(int i=1; i < max5; i++){
      TimeWindow tw2 = gc.getDataReference(i).getTimeWindow();
      tw = tw.intersection(tw2);
    }
    return tw;
  }
  /**
   * This method attempts to guess the study name for the group
   * by filtering for "FLOW" and reading upto 5 data references
   * F part of the pathname
   */
  public static String guessStudyNameFromGroup(Group g){
    if ( g == null ) return "";
    Group gc = Group.createGroup(g);
    gc.filterBy("FLOW");
    if ( gc.getNumberOfDataReferences() == 0 ) return "";
    int count = gc.getNumberOfDataReferences();
    int max5 = Math.max(count,5);
    String sname = null;
    for(int i=1; i < max5; i++){
      DataReference ref = gc.getDataReference(i);
      if ( ref == null ) return "";
      Pathname path = ref.getPathname();
      if ( path == null ) return "";
      sname = path.getPart(Pathname.F_PART);
    }
    if ( sname == null )
      return "";
    else
      return sname.toUpperCase();
  }
  /**
    *
    *
    * @author Nicky Sandhu
    * @version $Id: AppUtils.java,v 1.1.4.74.2.1 2002/06/20 19:12:45 adraper Exp $
    */
  private static Comparator _stringComp = new Comparator(){
    public int compare(Object obj1, Object obj2){
      String str1 = (String) obj1;
      String str2 = (String) obj2;
      return str1.compareTo(str2);
    }
    public boolean equals(Object obj){ return false; }
  };
  private static Group _dv1g, _sv1g, _dv2g, _sv2g;
  private static TimeWindow _tw;
  /**
    *
    */
  public static boolean projectDataChanged(){
    Project prj = getCurrentProject();
    if (    _dv1g == null || _dv1g != prj.getDVGroup()
	    || _sv1g == null || _sv1g != prj.getSVGroup()
	    || _sv2g == null || _sv2g != prj.getSV2Group()
	    || _dv2g == null || _dv2g != prj.getDV2Group()
	    || _tw == null || _tw != prj.getTimeWindow()
	 ){
      _dv1g = prj.getDVGroup();
      _sv1g = prj.getSVGroup();
      _dv2g = prj.getDV2Group();
      _sv2g = prj.getSV2Group();
      _tw = prj.getTimeWindow();
      return  true;
    } else{
      return false;
    }

  }
  public static String [] _bparts = null, _cparts = null;
  /**
    *
    */
  public static String [] getCurrentBParts(){
//	  Project prj = AppUtils.getCurrentProject();
    if( projectDataChanged() || _bparts == null ){
      String [] dvb = AppUtils.guessListOfBparts(_dv1g);
      String [] svb = AppUtils.guessListOfBparts(_sv1g);
      _bparts = new String [dvb.length + svb.length];
      System.arraycopy(dvb,0,_bparts,0,dvb.length);
      System.arraycopy(svb,0,_bparts,dvb.length,svb.length);
      Arrays.sort(_bparts,_stringComp);
    }
    return _bparts;
  }
  /**
    *
    */
  public static String [] getCurrentCParts(){
    return null;
/*
    Project prj = AppUtils.getCurrentProject();
    if ( projectDataChanged() || _cparts == null ){
      String [] dvc = AppUtils.guessListOfCparts(_dv1g);
      String [] svc = AppUtils.guessListOfCparts(_sv1g);
      _cparts = new String [dvc.length + svc.length];
      System.arraycopy(dvc,0,_cparts,0,dvc.length);
      System.arraycopy(svc,0,_cparts,dvc.length,svc.length);
      Arrays.sort(_cparts,_stringComp);
    }
    return _cparts;
*/
  }
  /**
   * guesses a unique list of b parts from given group
   *
   * @return a unique list of b parts or a list of exactly one
   * empty string if none found
   */
  public static String [] guessListOfBparts(Group g){
    if ( g == null ) return new String [] {""};
    Vector parts = new Vector();
    Group gc = Group.createGroup(g);
    int count = gc.getNumberOfDataReferences();
    if ( count == 0 ) return new String[] {""};
    //
    for(int i=0; i < count; i++){
      String str = gc.getDataReference(i).getPathname().getPart(Pathname.B_PART);
      if ( ! parts.contains(str) )
	      parts.addElement(str);
    }
    //
    if ( parts.size() == 0 )
      return new String[]{""};
    else{
      String [] bparts = new String[parts.size()];
      parts.copyInto(bparts);
      return bparts;
    }
  }
  /**
   * guesses a unique list of c parts from a given group
   * @return a unique list of c parts or a list of exactly one
   * empty string if none found
   */
  public static String [] guessListOfCparts(Group g){
    if ( g == null ) return new String []{""};
    Vector parts = new Vector();
    Group gc = Group.createGroup(g);
    int count = gc.getNumberOfDataReferences();
    if ( count == 0 ) return new String[]{""};
    //
    for(int i=0; i < count; i++){
      String str = gc.getDataReference(i).getPathname().getPart(Pathname.C_PART);
      if ( ! parts.contains(str) )
	parts.addElement(str);
    }
    //
    if ( parts.size() == 0 )
      return new String[]{""};
    else{
      String [] cparts = new String[parts.size()];
      parts.copyInto(cparts);
      if ( DEBUG ) System.out.println("In guessCpart, cparts.length=" + cparts.length);
      return cparts;
    }
  }
  /**
   *
   */
  public static String getCPartFromArc(Arc arc){
    String cpart = "FLOW-CHANNEL";
    if ( arc instanceof ChannelArc )
      cpart = "FLOW-CHANNEL";
    else if ( arc instanceof DemandArc )
      cpart = "FLOW-DELIVERY";
    else if ( arc instanceof ReturnArc )
      cpart = "FLOW-RETURN";
    else if ( arc instanceof InputArc )
      cpart = "FLOW-INFLOW";
    else if ( arc instanceof FloodArc )
      cpart = "FLOW-SPILL-NON-RECOV";
    else
      throw new RuntimeException("Could not figure out type of arc: " + arc);
    return cpart;
  }
  /**
   * @returns
   */
  public static DataReference getMassBalance(int studyNumber, int nodeId){
    calsim.gym.Network net = AppUtils.getCurrentProject().getNetwork();
    if ( net == null )
      throw new RuntimeException("No connectivity table loaded for current project!");
    calsim.gym.Node node = net.getNode(nodeId);
    if ( node == null )
      throw new RuntimeException("No such node #: " + nodeId + " defined for current project");
    calsim.gym.Arc [] upArcs = calsim.gym.GymUtils.getJustUpstreamArcs(node);
    calsim.gym.Arc [] downArcs = calsim.gym.GymUtils.getJustDownstreamArcs(node);
    // add evaporation, flood flow, storage changes
    // The evaporation and flood flow are not in the connectivity table
    // but are assumed to exist for all nodes with storage. Ditto for storage
    // arcs. Does this have to be generalized as well and written in the connectivity
    // table or somehow be accounted in the Network ??
//    int count = 0;
    // add uparcs
    RegularTimeSeries rts = null;
    for(int i=0; i < upArcs.length; i++){
      // upstream arcs which bring in flow to node
      if ( rts == null ){
	String cpart = getCPartFromArc(upArcs[i]);
	DataReference ref1 = AppUtils.getDataReference(studyNumber,upArcs[i].getName(),cpart);
	changeToCurrentUnits(ref1);
	rts = (RegularTimeSeries) TSMath.createCopy((TimeSeries)ref1.getData());
      }else{
	String cpart = getCPartFromArc(upArcs[i]);
	DataReference ref1 = AppUtils.getDataReference(studyNumber,upArcs[i].getName(),cpart);
	changeToCurrentUnits(ref1);
	RegularTimeSeries rtsy = (RegularTimeSeries) ref1.getData();
	TSMath.doMath(rts,rtsy,TimeSeriesMath.ADD);
      }
    }
    // subtract downarcs
    for(int i=0; i < downArcs.length; i++){
      // skip over storage arcs
      if ( downArcs[i].getName().startsWith("S") ) continue;
      // downstream arcs which bring in flow to node
      if ( rts == null ){
	String cpart = getCPartFromArc(downArcs[i]);
	DataReference ref1 = AppUtils.getDataReference(studyNumber,downArcs[i].getName(),cpart);
	changeToCurrentUnits(ref1);
	rts = (RegularTimeSeries) TSMath.createCopy((TimeSeries) ref1.getData());
	rts = rts.__mul__(-1);
      } else{
	String cpart = getCPartFromArc(downArcs[i]);
	DataReference ref1 = AppUtils.getDataReference(studyNumber,downArcs[i].getName(),cpart);
	changeToCurrentUnits(ref1);
	RegularTimeSeries rtsy = (RegularTimeSeries) ref1.getData();
	TSMath.doMath(rts,rtsy,TimeSeriesMath.SUB);
      }
    }
    // subtract out evaporation and flood flows if node has storage
    if ( node.hasStorage() ){
      if ( rts == null ){
	DataReference ref1 = AppUtils.getDataReference(studyNumber,"E"+nodeId,"EVAPORATION");
	changeToCurrentUnits(ref1);
	rts = (RegularTimeSeries)
	  TSMath.createCopy((TimeSeries)ref1.getData());
	rts = rts.__mul__(-1);
      }
      else{
	DataReference ref1 = AppUtils.getDataReference(studyNumber,"E"+nodeId,"EVAPORATION");
	changeToCurrentUnits(ref1);
	RegularTimeSeries rtsy =
	  (RegularTimeSeries) ref1.getData();
	TSMath.doMath(rts,rtsy,TimeSeriesMath.SUB);
      }
      // flood arc is already accounted for in the connectivity information.
    }
    //
    DataReference storageChange = null;
    // finally adjust the above derived time series
    if ( node.hasStorage() ){
      // change in storage calculations
      storageChange = getStorageChange(studyNumber,nodeId);
      changeToCurrentUnits(storageChange);
    }
    Project prj = AppUtils.getCurrentProject();
    //
    if ( storageChange != null ){
      RegularTimeSeries rts2 = (RegularTimeSeries) storageChange.getData();
      TimeWindow tw = rts.getTimeWindow().intersection(rts2.getTimeWindow());
      rts = (RegularTimeSeries) rts.createSlice(tw);
      rts2 = (RegularTimeSeries) rts2.createSlice(tw);
      TSMath.doMath(rts,rts2,TimeSeriesMath.SUB);
    } else {
    }
    String fpart = prj.getBaseName();
    if ( studyNumber == 2 ) fpart = prj.getComp1Name();
    Pathname path = Pathname.createPathname("/CALSIM/NODE-"+nodeId+"/MASS-BALANCE/"+
					    rts.getTimeWindow()+"/"+rts.getTimeInterval()+"/"+fpart);
    DataReference ref = DSSUtil.createDataReference("local","calc.dss",path.toString(),rts);
    return ref;
  }
  /**
   *
   */
  public static DataReference getStorageChange(int studyNumber, int nodeId){
    Project prj = AppUtils.getCurrentProject();
    calsim.gym.Network net = prj.getNetwork();
    if ( net == null )
      throw new RuntimeException("No connectivity table loaded for current project!");
    calsim.gym.Node node = net.getNode(nodeId);
    if ( node == null )
      throw new RuntimeException("No such node #: " + nodeId + " defined for current project");
    TimeWindow tw = prj.getTimeWindow();
    TimeFactory tf = TimeFactory.getInstance();
    TimeInterval ti = tf.createTimeInterval("1MONTH");
    TimeWindow extw = null;
    if (tw != null ) {
      extw = tf.createTimeWindow(tw.getStartTime().__sub__(ti),
				 tw.getEndTime());
    }
    DataReference ref1 = null;
    if ( studyNumber == 1 )
      ref1 = getDataReference(prj.getDVGroup(), "S"+nodeId, "STORAGE",extw);
    else
      ref1 = getDataReference(prj.getDV2Group(), "S"+nodeId, "STORAGE",extw);
    DataSet ds1 = TimeSeriesMath.forwardDifference((RegularTimeSeries)ref1.getData());
    Pathname p1 = ref1.getPathname();
    String pathname = "/" + p1.getPart(Pathname.A_PART)+
      "/" + p1.getPart(Pathname.B_PART) +
      "/" + p1.getPart(Pathname.C_PART) + "-CHANGE" +
      "/" + ref1.getTimeWindow().toString() +
      "/" + p1.getPart(Pathname.E_PART) +
      "/" + p1.getPart(Pathname.F_PART) +
      "/";
    return DSSUtil.createDataReference("local","calc.dss",pathname,ds1);
  }
  /**
   * returns the filename associated with the given group
   */
  public static String getFilename(Group g){
    if ( g == null ) return "";
    // modify DSSUtil to contain this separator string rather than
    // hardwiring it here ??
    String gname = g.getName();
    if ( gname == null ) return "";
    int index = gname.indexOf("::");
    if ( index < 0 ) return "";
    return gname.substring(index+2,gname.length());
  }
  /**
    *
    */
  static PathPartMapping [] _mapping = new PathPartMapping[2];
  /**
    *
    */
  public static PathPartMapping getMapping(int studyNumber){
    return _mapping[studyNumber-1];
  }
  /**
    *
    */
  public static void loadMapping(int studyNumber, String file) throws IOException{
    _mapping[studyNumber-1] = new PathPartMapping(file);
  }
  /**
    *
    */
  public static void unloadMapping(int studyNumber){
    _mapping[studyNumber-1] = null;
  }
	/**
	 * This method queries for a pathname with the given bpart and cpart
	 * in the varType Group.
	 * @param studyNumber The study #1 or #2
	 * @param bpart The b part
	 * @param cpart The c part
	 */
	public static DataReference getDataReference(int studyNumber, String bpart, String cpart){
		Project prj = getCurrentProject();
		TimeWindow tw = prj.getTimeWindow();
		DataReference ref = null;
		if (DEBUG) {System.out.println("SN is: "  + new Integer(studyNumber).toString() + "Base is" + new Integer(basenum).toString());}
		if (baseOn && studyNumber == basenum) {
			if (DEBUG) System.out.println("In Base");
			if (isDvarsFilter) ref = getDataReference(prj.getDVGroup(), bpart, cpart, tw);
			else ref = getDataReference(prj.getSVGroup(), bpart, cpart, tw);
		}
		if (comp1On && studyNumber == comp1num) {
			if (DEBUG) System.out.println("In Comp1");
			if (isDvarsFilter) ref = getDataReference(prj.getDV2Group(), bpart, cpart, tw);
			else ref = getDataReference(prj.getSV2Group(), bpart, cpart, tw);
		}
		if (comp2On && studyNumber == comp2num) {
			if (DEBUG) System.out.println("In Comp2");
			if (isDvarsFilter) ref = getDataReference(prj.getDV3Group(), bpart, cpart, tw);
			else ref = getDataReference(prj.getSV3Group(), bpart, cpart, tw);
		}
		if (comp3On && studyNumber == comp3num) {
			if (DEBUG) System.out.println("In Comp3");
			if (isDvarsFilter) ref = getDataReference(prj.getDV4Group(), bpart, cpart, tw);
			else ref = getDataReference(prj.getSV4Group(), bpart, cpart, tw);
		}
	    return ref;
	}
	/**
	 * displays data for the given array of data references
	 */
	public static JFrame [] displayArrayData(DataReference[] refs){
		// first look in dv group then in sv group
		int dssnum = getCheckedDSS();
		if (!baseOn && !comp1On && !comp2On && !comp3On) {
			JOptionPane.showMessageDialog(null,"You need to select which dss file(s) to display",
				"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if ( refs == null ) throw new RuntimeException("No rows have been selected");
		else {
			if (plotDifference || plotComparitive){
				if (plotDifference && dssnum != 2) {
					JOptionPane.showMessageDialog(null,"You need to select 2 dss files to do a difference",
						"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
					return null;
				}
//				if ( ! isInComparableState(prj) ) throw new RuntimeException("Cannot compare without loading base and compare files");
//				DataReference ref2 = null;
				if (plotDifference){
					DataReference[] refs1 = new DataReference[refs.length];
					try {
						for (int i=0; i<refs.length; i++) {
							changeToCurrentUnits(refs[i]); // moved here from below
							Pathname p = refs[i].getPathname();
							String b = p.getPart(Pathname.B_PART);
							String c = p.getPart(Pathname.C_PART);
					  		refs1[i] = getDataReference(1,b,c);
							changeToCurrentUnits(refs1[i]); // bug fix when diff and cfs to taf or taf to cfs
						}
					} catch(RuntimeException re){
					  refs1 = null;
					}
					if (refs1 == null ) throw new RuntimeException("No matching data in study 2 found ");
					DataReference[] subref = new DataReference[refs.length];
					for (int i=0; i<refs.length; i++) subref[i] = refs1[i].__sub__(refs[i]);
					return displayData(subref);
				} else {
					Vector refholder = new Vector(1,1);
					try {
						for (int i=1; i<dssnum; i++) {
							for (int j=0; j<refs.length; j++) {
								Pathname p = refs[j].getPathname();
								String b = p.getPart(Pathname.B_PART);
								String c = p.getPart(Pathname.C_PART);
						  		refholder.addElement(getDataReference(i,b,c));
							}
						}
					}catch(RuntimeException re){
						refholder = null;
					}
					if ( refholder == null ) throw new RuntimeException("No matching data in study ");
					int size = refs.length + refholder.size();
					DataReference [] refs1 = new DataReference[size];
					for (int i=0; i<refs.length; i++) refs1[i] = refs[i];
					for (int i=refs.length; i<size; i++) refs1[i] = (DataReference)refholder.elementAt(i-refs.length);
					return displayData(refs1);
				}
			} else {
				return displayData(refs);
			}
		}
	}

  /**
   * displays data for the given bpart and cpart
   */
  public static JFrame [] displayData(String bpart, String cpart){
    // first look in dv group then in sv group
    int dssnum = getCheckedDSS();
    if (!baseOn && !comp1On && !comp2On && !comp3On) {
			JOptionPane.showMessageDialog(null,"You need to select which dss file(s) to display",
																			"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
    DataReference[] ref = new DataReference[dssnum];
    try {
      ref[0] = getDataReference(0,bpart, cpart);
    }catch(RuntimeException e){
      ref[0] = null;
    }
    if (ref[0] != null) changeToCurrentUnits(ref[0]);
    Project prj = AppUtils.getCurrentProject();
    //
    if ( ref[0] == null )
      throw new RuntimeException("No matching data in study 1 found for " + cpart + " at " + bpart);
    else {
      if ( plotDifference || plotComparitive ){
	    	if (plotDifference && dssnum != 2) {
					JOptionPane.showMessageDialog(null,"You need to select 2 DSS's to do a difference on",
																					"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
					return null;
				}
				if ( ! isInComparableState(prj) ) throw new RuntimeException("Cannot compare without loading base and compare files");
//				DataReference ref2 = null;
				if ( plotDifference ){
					try {
					  ref[1] = getDataReference(1,bpart,cpart);
					}catch(RuntimeException re){
					  ref[1] = null;
					}
					if ( ref[1] == null ) throw new RuntimeException("No matching data in study 2 found for " + cpart + " at " + bpart);
					if (ref[1] != null) changeToCurrentUnits(ref[1]);
					return displayData(ref[1].__sub__(ref[0]));
				}else {
					for (int i=1; i<dssnum; i++) {
						try {
						  ref[i] = getDataReference(i,bpart,cpart);
						}catch(RuntimeException re){
						  ref[i] = null;
						}
						if ( ref[i] == null ) throw new RuntimeException("No matching data in study "
																					+ new Integer(i+1).toString() + " found for " + cpart + " at " + bpart);
						if (ref[i] != null) changeToCurrentUnits(ref[i]);
					}
					return displayData(ref);
				}
      } else {
				return displayData(ref[0]);
      }
    }
  }
  /**
    *
    */
  private static void changeUnknownToTAF(DataReference ref){
    // Kludge requested by A. Munevar ????#$!
    // Query user if units are unknown
    DataSet ds = ref.getData();
    DataSetAttr attr = ds.getAttributes();
    if (attr.getYUnits().equals("UNKNOWN") ){
      String [] possibleValues = {AppUtils.TAF,AppUtils.CFS,"NONE"};
      JOptionPane pane = new JOptionPane("Choose Units for " + ref.getPathname().toString(),
					 JOptionPane.INFORMATION_MESSAGE,
					 JOptionPane.OK_OPTION,
					 null, null, null);
      pane.setWantsInput(true);
      pane.setSelectionValues(possibleValues);
      pane.setInitialSelectionValue(AppUtils.getCurrentUnits());
      // show the dialog
      JDialog dialog = pane.createDialog(null,"Choose Units");
      pane.selectInitialValue();
      JComboBox jcbox = (JComboBox) GuiUtils.getComponent(JComboBox.class,
							  dialog.getContentPane());
      jcbox.setEditable(true);
      dialog.setVisible(true);
      String val = jcbox.getEditor().getItem().toString();
      TSMath.setYUnits(ds,val);
    }
  }
  /**
    *
    */
  public static void changeToCurrentUnits(DataReference ref){
    // first check for unknown units and prompt user for some units, TAF or CFS or NONE
    //changeUnknownToTAF(ref);
    try {
      DataSet ds = ref.getData();
      // set new attributes for data if this does not cache original units
      if (!(ds.getAttributes() instanceof TSDataAttr))
				ds.setAttributes(new TSDataAttr(ds.getAttributes()));
      // if stored units are needed use original units to figure out the conversions
//      useStoredUnits = false;
//      if ( useStoredUnits ) {
//				RegularTimeSeries rts = (RegularTimeSeries) ds;
//				String ounits = ((TSDataAttr)ds.getAttributes()).getOriginalUnits();
//				boolean isCFS = ounits.equals("CFS");
//				boolean isTAF = ounits.equals("TAF");
//				if ( isCFS )
//	  			TSMath.taf2cfs(rts);
//				else if ( isTAF )
//	  			TSMath.cfs2taf(rts);
//				else
//	  			System.out.println("Not CFS or TAF units...");
//				return;
//      }
      // convert units from CFS or TAF if desired by user, but do cfs conversion only if not storage.
      if (ds instanceof RegularTimeSeries){
				RegularTimeSeries rts = (RegularTimeSeries) ds;
// CB				boolean isStorage = ds.getAttributes().getTypeName().equals("STORAGE");
				boolean isStorage = ds.getAttributes().getTypeName().toUpperCase().indexOf("STORAGE") > -1;
				String ounits = ((TSDataAttr)ds.getAttributes()).getOriginalUnits();
				boolean isCFS = ounits.equals("CFS");
				boolean isTAF = ounits.equals("TAF");
				if (useCFS && !isStorage && isTAF)
	  				TSMath.taf2cfs(rts);
				else if (!useCFS && isCFS)
	  				TSMath.cfs2taf(rts);
      }
    } catch(RuntimeException re){
      System.err.println(re.getMessage());
    }
  }
  /**
    *
    */
  public static JFrame [] displayDTSData(DerivedTimeSeries dts){
    if ( dts == null ) throw new RuntimeException("Got a null DTS!");
    Project prj = AppUtils.getCurrentProject();
		int dssnum = getCheckedDSS();
    if ( plotComparitive || plotDifference ){
	    if (plotDifference && dssnum != 2) {
				JOptionPane.showMessageDialog(null,"You need to select 2 to do a difference on",
																				"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
				return null;
			}
			if ( ! isInComparableState(prj) )
	  	  throw new RuntimeException("Cannot compare without loading base and compare files");
	  	Pathname[] path = new Pathname[dssnum];
      for (int i=0; i<dssnum; i++) path[i] = Pathname.createPathname(dts.getPathname());
      if ( plotComparitive ){
				int j = 0;
				if (baseOn)  {path[j].setPart(Pathname.F_PART,prj.getBaseName()); j++; }
				if (comp1On) {path[j].setPart(Pathname.F_PART,prj.getComp1Name());j++; }
				if (comp2On) {path[j].setPart(Pathname.F_PART,prj.getComp2Name());j++; }
				if (comp3On) {path[j].setPart(Pathname.F_PART,prj.getComp3Name());j++; }
				j = 0;
			}
			DataReference[] ref = new DataReference[dssnum];
			for (int i=0; i<dssnum; i++) {
				ref[i] = new DefaultReference("local","calc.dss",path[i].toString(),dts.getData(i));
				changeToCurrentUnits(ref[i]);
			}
      if ( plotComparitive )
				return displayData(ref);
      else
				return displayData(ref[1].__sub__(ref[0]));
    } else{
      Pathname path1 = Pathname.createPathname(dts.getPathname());
			path1.setPart(Pathname.F_PART,prj.getBaseName());
      DataReference ref1 = new DefaultReference("local","calc.dss",path1.toString(),dts.getData(0));
      return displayData(ref1);
    }
  }
  /**
   *
   */
  public static JFrame [] displayData(DataReference ref){
    if (!viewGraph && !viewTable && !viewMonthlyTable) {
			JOptionPane.showMessageDialog(null,"Need to select a view to display data",
																			"View Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
    if (!baseOn && !comp1On && !comp2On && !comp3On) {
			JOptionPane.showMessageDialog(null,"You need to select which dss file(s) to display",
																			"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
    JFrame [] frarray = null;
    if ( ref != null ) {
      changeToCurrentUnits(ref);
      int count = 0;
//      if (viewGraph) count++; // CB - changed order to try to fix vista iterator problem - it worked (mostly)!
      if (viewTable) count++;
      if (viewMonthlyTable) count++;
      if (viewGraph) count++;
      frarray = new JFrame [count];
      count = 0;
//      if (viewGraph) frarray[count++] = plot(ref); // CB - changed order to try to fix vista iterator problem - it worked!
      if (viewTable) frarray[count++] = tabulate(ref);
      if (viewMonthlyTable) frarray[count++] =  monthlyTable(ref);
      if (viewGraph) frarray[count++] = plot(ref);
    }
    return frarray;
  }
  /**
   *
   */
  public static JFrame [] displayData(MultipleTimeSeries ref){
    if ( ref != null ) {
      int dssnum = getCheckedDSS();
      DataReference [] refs1 = ref.getDataReferences(0);
	    if (plotDifference && dssnum != 2) {
				JOptionPane.showMessageDialog(null,"You need to select 2 to do a difference on",
																				"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
				return null;
			}
      if ( refs1 == null || refs1.length == 0 )
				throw new RuntimeException("No data found for study 1");
      if ( plotComparitive || plotDifference ){
		    if (plotDifference && dssnum != 2) {
					JOptionPane.showMessageDialog(null,"You need to select 2 to do a difference on",
																					"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
					return null;
				}
				System.out.println("DSS NUM: " + new Integer(dssnum).toString());
  			DataReference [] refs = null;
				Vector refholder = new Vector(1,1);
				for (int j=1; j<dssnum; j++) {
					System.out.println("J is: " + new Integer(j).toString());
  				DataReference [] refs2 = ref.getDataReferences(j);
					if ( refs2 == null || refs2.length == 0 )
					  throw new RuntimeException("No data found for study 2");
					if ( plotDifference ){
					  refs = new DataReference[refs1.length];
					  for(int i=0; i < refs.length; i++ ) refs[i] = refs1[i].__sub__(refs2[i]);
					}else {
						System.out.println("Compare");
					  System.out.println(refs2.length);
            for (int i=0; i<refs2.length; i++) refholder.addElement(refs2[i]);
					}
				}
				if (plotComparitive) {
					DataReference [] refs2 = new DataReference[refholder.size()];
					for (int i=0; i<refs2.length; i++) refs2[i] = (DataReference)refholder.elementAt(i);
					refs = new DataReference[refs1.length+refs2.length];
				  System.arraycopy(refs1, 0, refs, 0, refs1.length);
				  System.arraycopy(refs2, 0, refs, refs1.length, refs2.length);
				}
				return displayData(refs);
      } else {
				DataReference [] refs = ref.getDataReferences(0);
				if (refs.length == 1) return displayData(refs[0]);
				else return displayData(refs);
      }
    }
    return null;
  }
  /**
   *
   */
  public static JFrame [] displayData(DataReference [] refs){
    if (!viewGraph && !viewTable && !viewMonthlyTable) {
			JOptionPane.showMessageDialog(null,"Need to select a view to display data",
																			"View Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
    if (!baseOn && !comp1On && !comp2On && !comp3On) {
			JOptionPane.showMessageDialog(null,"You need to select which dss file(s) to display",
																			"DSS Not Selected",JOptionPane.WARNING_MESSAGE);
			return null;
		}
    JFrame [] frarray = null;
    if ( refs == null || refs.length == 0 ) return null;
    if (refs.length == 1)
      return displayData(refs[0]);
    else {
      for(int i=0; i < refs.length; i++){
	    if ( refs[i] != null )
	      changeToCurrentUnits(refs[i]);
        }
        int count = 0;
// CB        if (viewGraph) count++;
        if (viewTable) count++;
        if (viewMonthlyTable) count++;
        if (viewGraph) count++;
        frarray = new JFrame [count];
        count = 0;
// CB        if (viewGraph) frarray[count++]= plot(refs);
        if (viewTable) frarray[count++]= tabulate(refs);
        if (viewMonthlyTable) frarray[count++] = monthlyTable(refs);
        if (viewGraph) frarray[count++]= plot(refs);
    }
    return frarray;
  }

  /**
   * returns false if any of the sv/dv files for base/compare
   * studies have not been selected.
   */
  public static boolean isInComparableState(Project prj){
//    if ( prj.getSVFile().equals("") ) return false;
//    if ( prj.getDVFile().equals("") ) return false;
//    if ( prj.getSV2File().equals("") ) return false;
//    if ( prj.getDV2File().equals("") ) return false;
    return true;
  }
  /**
    * looks for the dts with the given name first in the current project
    * and then in the global list. If nothing is found null is returned
    */
  public static DerivedTimeSeries findDTS(String name){
    Project prj = AppUtils.getCurrentProject();
    DerivedTimeSeries dts = prj.getDTS(name);
    if ( dts != null ) return dts;
    return getGlobalDTS(name);
  }
  /**
    * looks for the mts with the given name first in the current project
    * and then in the global list. If nothing is found null is returned
    */
  public static MultipleTimeSeries findMTS(String name){
    Project prj = AppUtils.getCurrentProject();
    name = name.toUpperCase().trim();
    MultipleTimeSeries mts = prj.getMTS(name);
    if ( mts != null ) return mts;
    return getGlobalMTS(name);
  }
  /**
    *
    */
  public static void useStoredUnits(){
    useStoredUnits = true;
  }
  /**
    *
    */
  public static void useUnits(String units){
    useStoredUnits = false;
    if ( units.equals(AppUtils.CFS) ){
      useCFS = true;
    } else{
      useCFS = false;
    }
  }
  /**
   * @return a new string copy of "toBe" with string "with" in string "orig"
   */
  public static String replace(String orig, String toBe, String with){
    return SetUtils.createReplacedString(orig,toBe,with);
  }
  /**
    *
    */
  public static void exportToDSS(DerivedTimeSeries dts, String file, String pathname){
    DSSUtil.writeData(file,pathname,dts.getData());
  }
  /**
    *
    */
  public static void exportToDSS(MultipleTimeSeries mts, String file){
    DataReference [] refs = mts.getDataReferences(1);
    if ( refs == null ) return;
    for(int i=0; i < refs.length; i++)
      DSSUtil.writeData(file,refs[i].getPathname().toString(),refs[i].getData());
  }
  	public static int [] MT_40_30_30 = readMTList("MT40-30-30.table");
  	public static int [] MT_60_20_20 = readMTList("MT60-20-20.table");
  /**
    *
    */
  public static int [] readMTList(String file){
    try {
      InputStream is = VistaUtils.getResourceAsStream("/calsim/app/data/"+file);
      LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
      Vector yearArray = new Vector(73);
      while ( true ){
				String line = reader.readLine();
				if ( line == null ) break;
				StringTokenizer st = new StringTokenizer(line);
				if ( st.countTokens() != 2 ) continue;
				try {
				  Integer year = new Integer(st.nextToken());
				  yearArray.addElement(year);
				}catch(Exception e){
				  continue;
				}
      }
      if ( yearArray.size() == 0 ) return null;
      int [] years = new int[yearArray.size()];
      for(int i=0; i < years.length; i++){
				years[i] = ((Integer)yearArray.elementAt(i)).intValue();
      }
      return years;
    }catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }
  /**
   *
   */
  public static DataReference makeOneRef(DataReference ref1){
    RegularTimeSeries rts = (RegularTimeSeries) ref1.getData();
    rts = (RegularTimeSeries) rts.createSlice(rts.getTimeWindow());
    rts.getAttributes().setYUnits("NONE");
    DataSetIterator dsi = rts.getIterator();
    while ( ! dsi.atEnd() ){
      DataSetElement dse = dsi.getElement();
      dse.setY(1);
      dsi.putElement(dse);
      dsi.advance();
    }
    Pathname path = Pathname.createPathname(new String[] {"CALSIM","1","","","1MON",
							  ref1.getPathname().getPart(Pathname.F_PART)});
    return DSSUtil.createDataReference("local","calc.dss",path.toString(),rts);
  }
  /**
   *
   */
  public static DataReference [] getCrossChannelGatePosition(){
    int snum = 1;
    DataReference tmp1 = AppUtils.getDataReference(snum,"FRAC_OPEN","ALIAS");
    DataReference one = AppUtils.makeOneRef(tmp1);
    DataReference tmp2 = AppUtils.getDataReference(snum,"SAC_BELOW_THRESH","LP DVAR");
    DataReference ref1 = one.__sub__(tmp1.__mul__(tmp2));
    // if (ref1 != null) changeToCurrentUnits(ref1);
    if ( ref1 == null )
      throw new RuntimeException("No matching data in study 1 found for Cross Channel gate position");
    else {
      Project prj = AppUtils.getCurrentProject();
      if ( plotDifference || plotComparitive ){
	if ( ! isInComparableState(prj) )
	  throw new RuntimeException("Cannot compare without loading base and compare files");
	DataReference ref2 = null;
	snum = 2;
	tmp1 = AppUtils.getDataReference(snum,"FRAC_OPEN","ALIAS");
	tmp2 = AppUtils.getDataReference(snum,"SAC_BELOW_THRESH","LP DVAR");
	ref2 = one.__sub__(tmp1.__mul__(tmp2));
	if ( ref2 == null )
	  throw new RuntimeException("No matching data in study 1 found for Cross Channel gate position");
	// if (ref2 != null) changeToCurrentUnits(ref2);
	ref1.getData().getAttributes().setYUnits("NONE");
	ref2.getData().getAttributes().setYUnits("NONE");
	if ( plotDifference )
	  return new DataReference [] {ref2.__sub__(ref1)};
	else
	  return new DataReference[]{ref1,ref2};
      }
    }
    ref1.getData().getAttributes().setYUnits("NONE");
    return new DataReference[] {ref1};
  }
  /**
    *
    */
  public static TimeWindow createTimeWindowFromString(String str){
    int dIndex = str.indexOf("-");
    if ( dIndex < 0 )
      throw new IllegalArgumentException("Invalid string for time window "  + str);
    String ststr = str.substring(0,dIndex).trim();
    String etstr = str.substring(dIndex+1,str.length()).trim();
    // this initializes to beginning of month
    TimeFactory tf = TimeFactory.getInstance();
    TimeInterval ti = tf.createTimeInterval("1mon");
    TimeInterval timin = tf.createTimeInterval("1min");
    Time stime = tf.createTime(ststr,"MMMyyyy");
    stime = tf.createTime((stime.__add__(timin)).ceiling(ti));
    Time etime = tf.createTime(etstr,"MMMyyyy");
    etime = tf.createTime((etime.__add__(timin)).ceiling(ti));
    return tf.createTimeWindow(stime,etime);
  }
  /**
   *
   */
  public static String getCurrentUnits(){
    if (useCFS) {
      return CFS;
    } else {
      return TAF;
    }
  }
  /**
    *
    */
  public static void loadProps(){
    viewGraph = new Boolean(AppProps.getProperty("AppUtils.viewGraph")).booleanValue();
    viewTable = new Boolean(AppProps.getProperty("AppUtils.viewTable")).booleanValue();
    viewMonthlyTable = new Boolean(AppProps.getProperty("AppUtils.viewMonthlyTable")).booleanValue();
    //useCFS = new Boolean(AppProps.getProperty("AppUtils.useCFS")).booleanValue();
    //useStoredUnits = new Boolean(AppProps.getProperty("AppUtils.useStoredUnits")).booleanValue();
    _isWaterYear = new Boolean(AppProps.getProperty("AppUtils.isWaterYear")).booleanValue();
    _show60_20_20 = new Boolean(AppProps.getProperty("AppUtils.show60_20_20")).booleanValue();
    _show40_30_30 = new Boolean(AppProps.getProperty("AppUtils.show40_30_30")).booleanValue();
    DEFAULT_PLOT_SIZE = GraphUtils.parseDimensionProperty
      (AppProps.getProperty("AppUtils.DEFAULT_PLOT_SIZE"));
    DEFAULT_TABLE_SIZE = GraphUtils.parseDimensionProperty
      (AppProps.getProperty("AppUtils.DEFAULT_TABLE_SIZE"));
    DEFAULT_MT_SIZE= GraphUtils.parseDimensionProperty
      (AppProps.getProperty("AppUtils.DEFAULT_MT_SIZE"));
  }
  /**
    *
    */
  public static void saveProps(){
    AppProps.setProperty("AppUtils.viewGraph", new Boolean(viewGraph).toString());
    AppProps.setProperty("AppUtils.viewTable", new Boolean(viewTable).toString());
    AppProps.setProperty("AppUtils.viewMonthlyTable",new Boolean(viewMonthlyTable).toString());
    AppProps.setProperty("AppUtils.useCFS",new Boolean(useCFS).toString());
    //AppProps.setProperty("AppUtils.useStoredUnits",new Boolean(useStoredUnits).toString());
    AppProps.setProperty("AppUtils.isWaterYear",new Boolean(_isWaterYear).toString());
    AppProps.setProperty("AppUtils.show60_20_20",new Boolean(_show60_20_20).toString());
    AppProps.setProperty("AppUtils.show40_30_30",new Boolean(_show40_30_30).toString());

    AppProps.setProperty("AppUtils.DEFAULT_PLOT_SIZE",DEFAULT_PLOT_SIZE.toString());
    AppProps.setProperty("AppUtils.DEFAULT_TABLE_SIZE",DEFAULT_TABLE_SIZE.toString());
    AppProps.setProperty("AppUtils.DEFAULT_MT_SIZE",DEFAULT_MT_SIZE.toString());

    AppProps.save();
  }
  /**
    *
    */
  static {
    try {
      loadProps();
    }catch(Exception e){
      e.printStackTrace(System.err);
    }
  }
  /**
   *
   */
  public static boolean needsRecataloging(String dssFile){
    String catalogFile = DSSUtil.getCatalogFilename( dssFile );
    File cf = new File(catalogFile);
    File df = new File(dssFile);
/*    System.out.println(catalogFile + " " + dssFile);
    System.out.println(!cf.exists());
    System.out.println(cf.lastModified());
    System.out.println(df.lastModified());
    System.out.println(cf.lastModified() < df.lastModified());*/
    return (! cf.exists()) || ( cf.lastModified() < df.lastModified());
  }

}
