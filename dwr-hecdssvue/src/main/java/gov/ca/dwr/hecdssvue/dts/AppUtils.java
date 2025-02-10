/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import hec.heclib.util.HecTime;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import vista.app.DataGraphFrame;
import vista.app.DataTableFrame;
import vista.app.DefaultGraphBuilder;
import vista.app.MultiDataTableFrame;
import vista.db.dss.DSSUtil;
import vista.graph.Axis;
import vista.graph.AxisAttr;
import vista.graph.Graph;
import vista.graph.MultiPlot;
import vista.graph.Plot;
import vista.gui.VistaUtils;
import vista.set.DataReference;
import vista.set.DataSet;
import vista.set.DefaultReference;
import vista.set.Group;
import vista.set.PathPartPredicate;
import vista.set.Pathname;
import vista.set.RegularTimeSeries;
import vista.set.TimeSeriesMath;
import vista.time.Time;
import vista.time.TimeFactory;
import vista.time.TimeInterval;
import vista.time.TimeWindow;
import wrimsv2_plugin.debugger.core.DebugCorePlugin;

/**
 * Common utility functions for App package
 *
 * @author Nicky Sandhu
 * @version $Id: AppUtils.java,v 1.1.4.74.2.1 2002/06/20 19:12:45 adraper Exp $
 */
public class AppUtils {

    public static ArrayList<Integer> months = new ArrayList<Integer>() {{
        add(10);
        add(11);
        add(12);
        add(1);
        add(2);
        add(3);
        add(4);
        add(5);
        add(6);
        add(7);
        add(8);
        add(9);
    }};

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
    public static boolean viewMonthlyTable = false;
    /**
     *
     */
    public static boolean useCFS = true;
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
     * units string for flow in cubic feet per second
     */
    public static String CFS = "CFS";
    /**
     * On/Off switch for Base DSS File
     */
    public static boolean baseOn = true;
    /**
     * On/Off switch for comp1 DSS File
     */
    public static boolean comp1On = false;
    /**
     * On/Off switch for comp2 DSS File
     */
    public static boolean comp2On = false;
    /**
     * On/Off switch for comp3 DSS File
     */
    public static boolean comp3On = false;
    /**
     * the default time window
     */
    public static String DEFAULT_TIME_WINDOW = "OCT1921 - SEP3000";
    /**
     *
     */
    public static int basenum = 0;
    /**
     * the default sizes for plots
     */
    public static Dimension DEFAULT_PLOT_SIZE = new Dimension(750, 650);
    public static Dimension DEFAULT_TABLE_SIZE = new Dimension(300, 700);
    public static Dimension DEFAULT_MT_SIZE = new Dimension(750, 300);

    /**
     * the current project
     */
    private static Project _currentProject = new Project();
    /**
     * the global lists
     */
    private static final Hashtable _dtsList = new Hashtable();
    private static final Hashtable _mtsList = new Hashtable();
    /**
     * the global list files
     */
    private static final String[] _dtsFiles = null; /*{
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
    private static final String[] _mtsFiles = {
        "exp-compare.csv",
        "ei-compare.csv"
    };

    /**
     * global list initialization code. usually not a good idea to
     * anonymously execute code. Will find a place for this in some
     * main method later ??
     */
    static {
        try {
            initializeGlobalLists();
        } catch (Exception e) {
            System.err.println("Could not load global lists");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * initializes the global lists
     */
    static void initializeGlobalLists() {
        if (_dtsFiles == null) {
            return;
        }
        for (int i = 0; i < _dtsFiles.length; i++) {
            try {
                InputStream is =
                    VistaUtils.getResourceAsStream("/calsim/app/data/" + _dtsFiles[i]);
                DerivedTimeSeries dts = DerivedTimeSeries.load(is);
                _dtsList.put(dts.getName(), dts);
            } catch (Exception ioe) {
                if (DEBUG) {
                    System.err.println("Error in loading dts from : " + _dtsFiles[i]);
                }
            }
        }
        for (int i = 0; i < _mtsFiles.length; i++) {
            try {
                InputStream is =
                    VistaUtils.getResourceAsStream("/calsim/app/data/" + _mtsFiles[i]);
                MultipleTimeSeries mts = MultipleTimeSeries.load(is);
                _mtsList.put(mts.getName(), mts);
                //	if ( prj != null) prj.remove(mts);
            } catch (Exception ioe) {
                if (DEBUG) {
                    System.err.println("Error in loading mts from : " + _mtsFiles[i]);
                }
            }
        }
    }

    public static int getCheckedDSS() {
        int num = 0;
        if (baseOn) {
            basenum = num;
            num++;
        } else {
            basenum = 5;
        }
        if (comp1On) {
            num++;
        }
        if (comp2On) {
            num++;
        }
        if (comp3On) {
            num++;
        }
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
     */
    public static DataReference getDataReference(Group dssGroup,
        String bpart, String cpart, TimeWindow tw) {
        DataReference ref = null;
        if (dssGroup == null) {
            return null;
        }
        //Group gc = Group.createGroup(dssGroup);
        Group gc = (Group) dssGroup.clone();
        // do mapping
        //
        if (bpart != null && !bpart.equals("")) {
            gc.filterBy(new PathPartPredicate("^" + bpart + "$", Pathname.B_PART), true);
        }
        // no bpart found
        if (gc.getNumberOfDataReferences() == 0) {
            System.err.println("No matching reference in " + dssGroup.getName()
                + " for bpart = " + bpart);
            throw new RuntimeException("No matching reference in " + dssGroup.getName()
                + " for bpart = " + bpart);
            //      return null;
        }
        if (cpart != null && !cpart.equals("")) {
            gc.filterBy(new PathPartPredicate("^" + cpart + "$", Pathname.C_PART), true);
        }
        if (gc.getNumberOfDataReferences() > 1) {
            System.err.println("Warning: " + dssGroup.getName()
                + " has more than one references for bpart = " + bpart
                + " and cpart = " + cpart);
            ref = gc.getDataReference(0);
        } else if (gc.getNumberOfDataReferences() == 0) {
            throw new RuntimeException("No matching reference in " + dssGroup.getName()
                + " for cpart = " + cpart + " & bpart = " + bpart);
        } else {
            ref = gc.getDataReference(0);
        }
        // set time window
        if (ref != null && tw != null) {
            ref = DataReference.create(ref, tw);
        }
        return ref;
    }

    /**
     * plots a single data reference. This is a preliminary style of plotting
     * based on vista. This can be improved later ??
     */
    public static JFrame plot(DataReference ref) {
        DefaultGraphBuilder gb = new DefaultGraphBuilder();
        gb.addData(ref);
        Graph[] graph = gb.createGraphs();
        for (int i = 0; i < graph.length; i++) {
            MultiPlot mp = null;
            if (graph[i].getPlot() instanceof MultiPlot) {
                mp = (MultiPlot) graph[i].getPlot();
            } else {
                mp = null;
            }
            Plot[] plots = null;
            if (mp != null) {
                plots = mp.getAllPlots();
            } else {
                plots = new Plot[] {graph[i].getPlot()};
            }
            for (int j = 0; j < plots.length; j++) {
                Axis baxis = plots[j].getAxis(AxisAttr.BOTTOM);
                TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();
                Time stime = tw.getStartTime();
                stime = stime.create(stime);
                stime.incrementBy(TimeFactory.getInstance().createTimeInterval("-1MON"));
                Time etime = tw.getEndTime();
                baxis.setDCRange(stime.getTimeInMinutes(), etime.getTimeInMinutes());
            }
        }
        DataGraphFrame dg = new DataGraphFrame(graph[0], "Graph", false);
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
    public static JFrame plot(DataReference[] refs) {
        if (refs == null) {
            return null;
        }
        if (refs.length == 1) {
            return plot(refs[0]);
        }
        DefaultGraphBuilder gb = new DefaultGraphBuilder();
        for (int i = 0; i < refs.length; i++) {
            if (refs[i] != null) {
                gb.addData(refs[i]);
            }
        }
        Graph[] graph = gb.createGraphs();
        if (graph == null) {
            return null;
        }
        DataGraphFrame dg = new DataGraphFrame(graph[0], "Graph", false);
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
     */
    public static JFrame tabulate(DataReference ref) {
        if (ref == null) {
            return null;
        }
        JFrame fr = new DataTableFrame(ref, false);
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
     */
    public static JFrame tabulate(DataReference[] refs) {
        JFrame fr = null;
        if (refs == null) {
            return fr;
        }
        if (refs.length == 1) {
            fr = new DataTableFrame(refs[0], false);
        }
        fr = new MultiDataTableFrame(refs, false);
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
    public static JFrame monthlyTable(DataReference ref) {
        MonthlyTableDisplay mtd = null;
        if (_show60_20_20 && MT_60_20_20 != null) {
            mtd = new MonthlyTableDisplay(ref, _isWaterYear, _isStartMonth, _startMonth, MT_60_20_20);
        } else if (_show40_30_30 && MT_40_30_30 != null) {
            mtd = new MonthlyTableDisplay(ref, _isWaterYear, _isStartMonth, _startMonth, MT_40_30_30);
        } else {
            mtd = new MonthlyTableDisplay(ref, _isWaterYear, _isStartMonth, _startMonth, null);
        }
        JFrame fr = new DefaultFrame(mtd);
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
    public static JFrame monthlyTable(DataReference[] refs) {
        MonthlyTableDisplay mtd = null;
        if (_show60_20_20 && MT_60_20_20 != null) {
            mtd = new MonthlyTableDisplay(refs, _isWaterYear, _isStartMonth, _startMonth, MT_60_20_20);
        } else if (_show40_30_30 && MT_40_30_30 != null) {
            mtd = new MonthlyTableDisplay(refs, _isWaterYear, _isStartMonth, _startMonth, MT_40_30_30);
        } else {
            mtd = new MonthlyTableDisplay(refs, _isWaterYear, _isStartMonth, _startMonth, null);
        }
        JFrame fr = new DefaultFrame(mtd);
        fr.setSize(DEFAULT_MT_SIZE);
        Toolkit tk = fr.getToolkit();
        Dimension screenSize = tk.getScreenSize();
        Dimension frameSize = fr.getSize();
        fr.setLocation(screenSize.width - frameSize.width,
            screenSize.height - frameSize.height);
        return fr;
    }

    /**
     * gets the global list for DTS
     */
    public static DerivedTimeSeries[] getGlobalDTSList() {
        DerivedTimeSeries[] dts = new DerivedTimeSeries[_dtsList.size()];
        int count = 0;
        for (Enumeration e = _dtsList.elements(); e.hasMoreElements(); ) {
            dts[count] = (DerivedTimeSeries) e.nextElement();
            count++;
        }
        return dts;
    }

    /**
     *
     */
    public static DerivedTimeSeries getGlobalDTS(String name) {
        return (DerivedTimeSeries) _dtsList.get(name.toUpperCase().trim());
    }

    /**
     *
     */
    public static MultipleTimeSeries getGlobalMTS(String name) {
        return (MultipleTimeSeries) _mtsList.get(name.toUpperCase().trim());
    }

    /**
     * get the current project
     */
    public static Project getCurrentProject() {
        return _currentProject;
    }

    /**
     * set the current project. This is done to define the project
     * in the current context
     */
    public static void setCurrentProject(Project p) {
        _currentProject = p;
    }

    /**
     * This function searches thro' the names of the global DTS and
     * MTS lists as well as the current projects lists to check
     * for a name clash
     *
     * @return true if name is taken
     */
    public static boolean nameNotTaken(String name) {
        if (_dtsList.containsKey(name)) {
            return false;
        } else if (_mtsList.containsKey(name)) {
            return false;
        } else if (getCurrentProject().getDTS(name) != null) {
            return false;
        } else {
            return getCurrentProject().getMTS(name) == null;
        }
    }

    /**
     * gets the operation id which is one of the id's defined
     * in TimeSeriesMath class
     *
     * @param operationStr is one of +,-,*,/
     * @return the operation id or -1 if none is found
     */
    public static int getOperationId(String operationStr) {
        String str = operationStr;
        if (str.equals("+")) {
            return TimeSeriesMath.ADD;
        } else if (str.equals("-")) {
            return TimeSeriesMath.SUB;
        } else if (str.equals("*")) {
            return TimeSeriesMath.MUL;
        } else if (str.equals("/")) {
            return TimeSeriesMath.DIV;
        } else {
            return -1;
        }
    }

    /**
     * gets the operation name for the given operation id
     *
     * @param operationId id one of TimeSeriesMath.ADD|SUB|MUL|DIV
     */
    public static String getOperationName(int operationId) {
        switch (operationId) {
            case TimeSeriesMath.ADD:
                return "+";
            case TimeSeriesMath.SUB:
                return "-";
            case TimeSeriesMath.MUL:
                return "*";
            case TimeSeriesMath.DIV:
                return "/";
            default:
                return "?";
        }
    }

    /**
     * displays error by writing it out to the error stream.
     * This can be changed later to display the message to some
     * console or dialog window. ??
     */
    public static void displayError(String msg) {
        System.err.println(msg);
    }

    /**
     * This method attempts to guess the study name for the group
     * by filtering for "FLOW" and reading upto 5 data references
     * F part of the pathname
     */
    public static String guessStudyNameFromGroup(Group g) {
        if (g == null) {
            return "";
        }
        Group gc = Group.createGroup(g);
        gc.filterBy("FLOW");
        if (gc.getNumberOfDataReferences() == 0) {
            return "";
        }
        int count = gc.getNumberOfDataReferences();
        int max5 = Math.max(count, 5);
        String sname = null;
        for (int i = 1; i < max5; i++) {
            DataReference ref = gc.getDataReference(i);
            if (ref == null) {
                return "";
            }
            Pathname path = ref.getPathname();
            if (path == null) {
                return "";
            }
            sname = path.getPart(Pathname.F_PART);
        }
        if (sname == null) {
            return "";
        } else {
            return sname.toUpperCase();
        }
    }

    /**
     * @author Nicky Sandhu
     * @version $Id: AppUtils.java,v 1.1.4.74.2.1 2002/06/20 19:12:45 adraper Exp $
     */
    private static final Comparator _stringComp = new Comparator() {
        public int compare(Object obj1, Object obj2) {
            String str1 = (String) obj1;
            String str2 = (String) obj2;
            return str1.compareTo(str2);
        }

        public boolean equals(Object obj) {
            return false;
        }
    };
    private static Group _dv1g, _sv1g, _dv2g, _sv2g;
    private static TimeWindow _tw;

    /**
     *
     */
    public static boolean projectDataChanged() {
        Project prj = getCurrentProject();
        if (_dv1g == null || _dv1g != prj.getDVGroup()
            || _sv1g == null || _sv1g != prj.getSVGroup()
            || _sv2g == null || _sv2g != prj.getSV2Group()
            || _dv2g == null || _dv2g != prj.getDV2Group()
            || _tw == null || _tw != prj.getTimeWindow()
        ) {
            _dv1g = prj.getDVGroup();
            _sv1g = prj.getSVGroup();
            _dv2g = prj.getDV2Group();
            _sv2g = prj.getSV2Group();
            _tw = prj.getTimeWindow();
            return true;
        } else {
            return false;
        }

    }

    public static String[] _bparts = null;

    /**
     *
     */
    public static String[] getCurrentBParts() {
        if (projectDataChanged() || _bparts == null) {
            String[] dvb = AppUtils.guessListOfBparts(_dv1g);
            String[] svb = AppUtils.guessListOfBparts(_sv1g);
            _bparts = new String[dvb.length + svb.length];
            System.arraycopy(dvb, 0, _bparts, 0, dvb.length);
            System.arraycopy(svb, 0, _bparts, dvb.length, svb.length);
            Arrays.sort(_bparts, _stringComp);
        }
        return _bparts;
    }

    /**
     *
     */
    public static String[] getCurrentCParts() {
        return null;
    }

    /**
     * guesses a unique list of b parts from given group
     *
     * @return a unique list of b parts or a list of exactly one
     * empty string if none found
     */
    public static String[] guessListOfBparts(Group g) {
        if (g == null) {
            return new String[] {""};
        }
        Vector parts = new Vector();
        Group gc = Group.createGroup(g);
        int count = gc.getNumberOfDataReferences();
        if (count == 0) {
            return new String[] {""};
        }
        //
        for (int i = 0; i < count; i++) {
            String str = gc.getDataReference(i).getPathname().getPart(Pathname.B_PART);
            if (!parts.contains(str)) {
                parts.addElement(str);
            }
        }
        //
        if (parts.size() == 0) {
            return new String[] {""};
        } else {
            String[] bparts = new String[parts.size()];
            parts.copyInto(bparts);
            return bparts;
        }
    }

    /**
     * This method queries for a pathname with the given bpart and cpart
     * in the varType Group.
     *
     * @param studyNumber The study #1 or #2
     * @param bpart       The b part
     * @param cpart       The c part
     */
    public static DataReference getDataReference(int studyNumber, String bpart, String cpart, String vt) {
        Project prj = getCurrentProject();
        TimeWindow tw = prj.getTimeWindow();
        DataReference ref = null;
        if (DEBUG) {
            Integer.toString(basenum);
        }
        //if (baseOn && studyNumber == basenum) {
        //if (selectedStudies[0]){
        if (studyNumber == 0) {
            if (DEBUG) {
                System.out.println("In Base");
            }
            if (vt.equals("DVAR")) {
                ref = getDataReference(prj.getDVGroup(), bpart, cpart, tw);
            } else {
                ref = getDataReference(prj.getSVGroup(), bpart, cpart, tw);
            }
        }
        //if (comp1On && studyNumber == comp1num) {
        //if (selectedStudies[1]){
        else if (studyNumber == 1) {
            if (DEBUG) {
                System.out.println("In Comp1");
            }
            if (vt.equals("DVAR")) {
                ref = getDataReference(prj.getDV2Group(), bpart, cpart, tw);
            } else {
                ref = getDataReference(prj.getSV2Group(), bpart, cpart, tw);
            }
        }
        //if (comp2On && studyNumber == comp2num) {
        //if (selectedStudies[2]){
        else if (studyNumber == 2) {
            if (DEBUG) {
                System.out.println("In Comp2");
            }
            if (vt.equals("DVAR")) {
                ref = getDataReference(prj.getDV3Group(), bpart, cpart, tw);
            } else {
                ref = getDataReference(prj.getSV3Group(), bpart, cpart, tw);
            }
        }
        //if (comp3On && studyNumber == comp3num) {
        //if (selectedStudies[3]){
        else if (studyNumber == 3) {
            if (DEBUG) {
                System.out.println("In Comp3");
            }
            if (vt.equals("DVAR")) {
                ref = getDataReference(prj.getDV4Group(), bpart, cpart, tw);
            } else {
                ref = getDataReference(prj.getSV4Group(), bpart, cpart, tw);
            }
        }
        return ref;
    }

    /**
     *
     */
    public static void changeToCurrentUnits(DataReference ref) {
        // first check for unknown units and prompt user for some units, TAF or CFS or NONE
        try {
            DataSet ds = ref.getData();
            // set new attributes for data if this does not cache original units
            if (!(ds.getAttributes() instanceof TSDataAttr)) {
                ds.setAttributes(new TSDataAttr(ds.getAttributes()));
            }
            // convert units from CFS or TAF if desired by user, but do cfs conversion only if not storage.
            if (ds instanceof RegularTimeSeries) {
                RegularTimeSeries rts = (RegularTimeSeries) ds;
                boolean isStorage = ds.getAttributes().getTypeName().toUpperCase().indexOf("STORAGE") > -1;
                String ounits = ((TSDataAttr) ds.getAttributes()).getOriginalUnits();
                boolean isCFS = ounits.equals("CFS");
                boolean isTAF = ounits.equals("TAF");
                if (useCFS && !isStorage && isTAF) {
                    TSMath.taf2cfs(rts);
                } else if (!useCFS && isCFS) {
                    TSMath.cfs2taf(rts);
                }
            }
        } catch (RuntimeException re) {
            System.err.println(re.getMessage());
        }
    }

    /**
     *
     */
    public static Vector<DataContainer> retrieveDTSData(DerivedTimeSeries dts) {
        Vector<DataContainer> v = new Vector<DataContainer>();
        for (int i = 0; i < 4; i++) {
            if (DebugCorePlugin.selectedStudies[i]) {
                RegularTimeSeries ds = (RegularTimeSeries) dts.getDataW2(i);
                String name = dts.getName();
                TimeSeriesContainer tsc = procDataSet(ds, name, i);
                v.add(tsc);
                if (plotDifference) {
                    v = procDiffDataSet(v);
                }
            }
        }
        return v;
    }

    /**
     *
     */
    public static Vector<DataContainer> retrieveMTSData(MultipleTimeSeries mts) {
        Vector<DataContainer> v = new Vector<DataContainer>();
        for (int k = 0; k < 4; k++) {
            if (DebugCorePlugin.selectedStudies[k]) {
                DataReference[] drs = mts.getDataReferences(k);
                for (int l = 0; l < drs.length; l++) {
                    RegularTimeSeries ds = (RegularTimeSeries) drs[l].getData();
                    String name = mts.getDTSNameAt(l);
                    TimeSeriesContainer tsc = procDataSet(ds, name, k);
                    v.add(tsc);
                }
            }
        }
        return v;
    }


    public static TimeSeriesContainer procDataSet(RegularTimeSeries ds, String name, int studyNumber) {
        TimeSeriesContainer tsc = new TimeSeriesContainer();
        tsc.location = name;
        tsc.units = ds.getAttributes().getYUnits();
        double[] yArray = ds.getYArray();
        ArrayList<Integer> times = new ArrayList<Integer>();
        ArrayList<Double> values = new ArrayList<Double>();
        tsc.type = "PER-AVER";
        Date startDate = ds.getStartTime().getDate();
        String startDateStr = "";
        TimeInterval ti = ds.getTimeInterval();
        String tiStr = ti.getIntervalAsString();
        if (tiStr.equalsIgnoreCase("1MON") || tiStr.equalsIgnoreCase("1Month")) {
            startDateStr = TimeOperation.dssTime(startDate.getYear() + 1900, startDate.getMonth() + 1, 1);
        } else if (tiStr.equalsIgnoreCase("1DAY")) {
            startDateStr =
                TimeOperation.dssTime(startDate.getYear() + 1900, startDate.getMonth() + 1, startDate.getDate());
        }
        HecTime startTime = (new HecTime(startDateStr));
        tsc.startTime = startTime.value();
        if (tiStr.equalsIgnoreCase("1MON") || tiStr.equalsIgnoreCase("1Month")) {
            int size = yArray.length;
            for (int j = 0; j < size; j++) {
                int mon = startDate.getMonth() + 1;
                if (months.contains(mon - 1) || (months.contains(12) && mon == 1)) {
                    startDateStr = TimeOperation.dssTime(startDate.getYear() + 1900, mon, 1);
                    startTime = (new HecTime(startDateStr));
                    double yValue = yArray[j];
                    if (yValue != -901.0) {
                        times.add(startTime.value());
                        values.add(yValue);
                    }
                }
                startDate = TimeOperation.addOneMonth(startDate);
            }
        } else if (tiStr.equalsIgnoreCase("1DAY")) {
            int size = yArray.length;
            for (int j = 0; j < size; j++) {
                int mon = startDate.getMonth() + 1;
                if (months.contains(mon - 1) || (months.contains(12) && mon == 1)) {
                    startDateStr = TimeOperation.dssTime(startDate.getYear() + 1900, mon, startDate.getDate());
                    startTime = (new HecTime(startDateStr));
                    double yValue = yArray[j];
                    if (yValue != -901.0) {
                        times.add(startTime.value());
                        values.add(yValue);
                    }
                }
                startDate = TimeOperation.addOneDay(startDate);
            }
        }
        int size1 = times.size();
        tsc.times = new int[size1];
        tsc.values = new double[size1];
        for (int i = 0; i < size1; i++) {
            tsc.times[i] = times.get(i);
            tsc.values[i] = values.get(i);
        }
        tsc.fullName = "//" + tsc.location + "///" + tiStr + "//";
        tsc.fileName = "" + studyNumber;
        tsc.numberValues = tsc.values.length;
        return tsc;
    }

    public static Vector<DataContainer> procDiffDataSet(Vector<DataContainer> v) {
        if (v.size() <= 1) {
            return v;
        } else {
            Vector<DataContainer> v1 = new Vector<DataContainer>();
            Iterator<DataContainer> ie = v.iterator();
            DataContainer dc0 = ie.next();
            while (ie.hasNext()) {
                DataContainer dc = ie.next();
                TimeSeriesContainer tsc = diff((TimeSeriesContainer) dc, (TimeSeriesContainer) dc0);
                v1.add(tsc);
            }
            return v1;
        }
    }

    public static TimeSeriesContainer diff(TimeSeriesContainer tsc1, TimeSeriesContainer tsc0) {
        try {
            HecMath hm0 = HecMath.createInstance(tsc0);
            HecMath hm1 = HecMath.createInstance(tsc1);
            hm1 = hm1.subtract(hm0);
            return (TimeSeriesContainer) hm1.getData();
        } catch (HecMathException e) {
            return tsc1;
        }

    }

    /**
     *
     */
    public static JFrame[] displayDTSData(DerivedTimeSeries dts) {
        if (dts == null) {
            throw new RuntimeException("Got a null DTS!");
        }
        Project prj = AppUtils.getCurrentProject();
        int dssnum = getCheckedDSS();
        if (plotComparitive || plotDifference) {
            if (plotDifference && dssnum != 2) {
                JOptionPane.showMessageDialog(null, "You need to select 2 to do a difference on",
                    "DSS Not Selected", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            Pathname[] path = new Pathname[dssnum];
            for (int i = 0; i < dssnum; i++) {
                path[i] = Pathname.createPathname(dts.getPathname());
            }
            if (plotComparitive) {
                int j = 0;
                if (baseOn) {
                    path[j].setPart(Pathname.F_PART, prj.getBaseName());
                    j++;
                }
                if (comp1On) {
                    path[j].setPart(Pathname.F_PART, prj.getComp1Name());
                    j++;
                }
                if (comp2On) {
                    path[j].setPart(Pathname.F_PART, prj.getComp2Name());
                    j++;
                }
                if (comp3On) {
                    path[j].setPart(Pathname.F_PART, prj.getComp3Name());
                    j++;
                }
                j = 0;
            }
            DataReference[] ref = new DataReference[dssnum];
            for (int i = 0; i < dssnum; i++) {
                ref[i] = new DefaultReference("local", "calc.dss", path[i].toString(), dts.getData(i));
                changeToCurrentUnits(ref[i]);
            }
            if (plotComparitive) {
                return displayData(ref);
            } else {
                return displayData(ref[1].__sub__(ref[0]));
            }
        } else {
            Pathname path1 = Pathname.createPathname(dts.getPathname());
            path1.setPart(Pathname.F_PART, prj.getBaseName());
            DataReference ref1 = new DefaultReference("local", "calc.dss", path1.toString(), dts.getData(0));
            return displayData(ref1);
        }
    }

    /**
     *
     */
    public static JFrame[] displayData(DataReference ref) {
        if (!viewGraph && !viewTable && !viewMonthlyTable) {
            JOptionPane.showMessageDialog(null, "Need to select a view to display data",
                "View Not Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!baseOn && !comp1On && !comp2On && !comp3On) {
            JOptionPane.showMessageDialog(null, "You need to select which dss file(s) to display",
                "DSS Not Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        JFrame[] frarray = null;
        if (ref != null) {
            changeToCurrentUnits(ref);
            int count = 0;
//      if (viewGraph) count++; // CB - changed order to try to fix vista iterator problem - it worked (mostly)!
            if (viewTable) {
                count++;
            }
            if (viewMonthlyTable) {
                count++;
            }
            if (viewGraph) {
                count++;
            }
            frarray = new JFrame[count];
            count = 0;
//      if (viewGraph) frarray[count++] = plot(ref); // CB - changed order to try to fix vista iterator problem - it worked!
            if (viewTable) {
                frarray[count++] = tabulate(ref);
            }
            if (viewMonthlyTable) {
                frarray[count++] = monthlyTable(ref);
            }
            if (viewGraph) {
                frarray[count++] = plot(ref);
            }
        }
        return frarray;
    }

    /**
     *
     */
    public static JFrame[] displayData(MultipleTimeSeries ref) {
        if (ref != null) {
            int dssnum = getCheckedDSS();
            DataReference[] refs1 = ref.getDataReferences(0);
            if (plotDifference && dssnum != 2) {
                JOptionPane.showMessageDialog(null, "You need to select 2 to do a difference on",
                    "DSS Not Selected", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (refs1 == null || refs1.length == 0) {
                throw new RuntimeException("No data found for study 1");
            }
            if (plotComparitive || plotDifference) {
                if (plotDifference && dssnum != 2) {
                    JOptionPane.showMessageDialog(null, "You need to select 2 to do a difference on",
                        "DSS Not Selected", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                Integer.toString(dssnum);
                DataReference[] refs = null;
                Vector refholder = new Vector(1, 1);
                for (int j = 1; j < dssnum; j++) {
                    Integer.toString(j);
                    DataReference[] refs2 = ref.getDataReferences(j);
                    if (refs2 == null || refs2.length == 0) {
                        throw new RuntimeException("No data found for study 2");
                    }
                    if (plotDifference) {
                        refs = new DataReference[refs1.length];
                        for (int i = 0; i < refs.length; i++) {
                            refs[i] = refs1[i].__sub__(refs2[i]);
                        }
                    } else {
                        System.out.println("Compare");
                        System.out.println(refs2.length);
                        for (int i = 0; i < refs2.length; i++) {
                            refholder.addElement(refs2[i]);
                        }
                    }
                }
                if (plotComparitive) {
                    DataReference[] refs2 = new DataReference[refholder.size()];
                    for (int i = 0; i < refs2.length; i++) {
                        refs2[i] = (DataReference) refholder.elementAt(i);
                    }
                    refs = new DataReference[refs1.length + refs2.length];
                    System.arraycopy(refs1, 0, refs, 0, refs1.length);
                    System.arraycopy(refs2, 0, refs, refs1.length, refs2.length);
                }
                return displayData(refs);
            } else {
                DataReference[] refs = ref.getDataReferences(0);
                if (refs.length == 1) {
                    return displayData(refs[0]);
                } else {
                    return displayData(refs);
                }
            }
        }
        return null;
    }

    /**
     *
     */
    public static JFrame[] displayData(DataReference[] refs) {
        if (!viewGraph && !viewTable && !viewMonthlyTable) {
            JOptionPane.showMessageDialog(null, "Need to select a view to display data",
                "View Not Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!baseOn && !comp1On && !comp2On && !comp3On) {
            JOptionPane.showMessageDialog(null, "You need to select which dss file(s) to display",
                "DSS Not Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        JFrame[] frarray = null;
        if (refs == null || refs.length == 0) {
            return null;
        }
        if (refs.length == 1) {
            return displayData(refs[0]);
        } else {
            for (int i = 0; i < refs.length; i++) {
                if (refs[i] != null) {
                    changeToCurrentUnits(refs[i]);
                }
            }
            int count = 0;
// CB        if (viewGraph) count++;
            if (viewTable) {
                count++;
            }
            if (viewMonthlyTable) {
                count++;
            }
            if (viewGraph) {
                count++;
            }
            frarray = new JFrame[count];
            count = 0;
// CB        if (viewGraph) frarray[count++]= plot(refs);
            if (viewTable) {
                frarray[count++] = tabulate(refs);
            }
            if (viewMonthlyTable) {
                frarray[count++] = monthlyTable(refs);
            }
            if (viewGraph) {
                frarray[count++] = plot(refs);
            }
        }
        return frarray;
    }

    /**
     * looks for the dts with the given name first in the current project
     * and then in the global list. If nothing is found null is returned
     */
    public static DerivedTimeSeries findDTS(String name) {
        Project prj = AppUtils.getCurrentProject();
        DerivedTimeSeries dts = prj.getDTS(name);
        if (dts != null) {
            return dts;
        }
        return getGlobalDTS(name);
    }

    /**
     * looks for the mts with the given name first in the current project
     * and then in the global list. If nothing is found null is returned
     */
    public static MultipleTimeSeries findMTS(String name) {
        Project prj = AppUtils.getCurrentProject();
        name = name.toUpperCase().trim();
        MultipleTimeSeries mts = prj.getMTS(name);
        if (mts != null) {
            return mts;
        }
        return getGlobalMTS(name);
    }

    /**
     *
     */
    public static void useUnits(String units) {
        useCFS = units.equals(AppUtils.CFS);
    }

    /**
     *
     */
    public static void exportToDSS(MultipleTimeSeries mts, String file) {
        DataReference[] refs = mts.getDataReferences(1);
        if (refs == null) {
            return;
        }
        for (int i = 0; i < refs.length; i++) {
            DSSUtil.writeData(file, refs[i].getPathname().toString(), refs[i].getData(), true);
        }
    }

    public static int[] MT_40_30_30 = readMTList("MT40-30-30.table");
    public static int[] MT_60_20_20 = readMTList("MT60-20-20.table");

    /**
     *
     */
    public static int[] readMTList(String file) {
        try {
            String dataDir = "data";
            File mtListFile = new File(dataDir, file);
            InputStream is = new FileInputStream(mtListFile);
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
            Vector yearArray = new Vector(73);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                StringTokenizer st = new StringTokenizer(line);
                if (st.countTokens() != 2) {
                    continue;
                }
                try {
                    Integer year = new Integer(st.nextToken());
                    yearArray.addElement(year);
                } catch (Exception e) {
                    continue;
                }
            }
            if (yearArray.size() == 0) {
                return null;
            }
            int[] years = new int[yearArray.size()];
            for (int i = 0; i < years.length; i++) {
                years[i] = ((Integer) yearArray.elementAt(i)).intValue();
            }
            return years;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     */
    public static TimeWindow createTimeWindowFromString(String str) {
        int dIndex = str.indexOf("-");
        if (dIndex < 0) {
            throw new IllegalArgumentException("Invalid string for time window " + str);
        }
        String ststr = str.substring(0, dIndex).trim();
        String etstr = str.substring(dIndex + 1).trim();
        // this initializes to beginning of month
        TimeFactory tf = TimeFactory.getInstance();
        TimeInterval ti = tf.createTimeInterval("1mon");
        TimeInterval timin = tf.createTimeInterval("1min");
        Time stime = tf.createTime(ststr, "MMMyyyy");
        stime = tf.createTime((stime.__add__(timin)).ceiling(ti));
        Time etime = tf.createTime(etstr, "MMMyyyy");
        etime = tf.createTime((etime.__add__(timin)).ceiling(ti));
        return tf.createTimeWindow(stime, etime);
    }


    public static void showDssFileErrorDialog(Exception ex){
        ex.printStackTrace();
        final IWorkbench workbench= PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(() -> {
            Shell shell=workbench.getActiveWorkbenchWindow().getShell();
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
            messageBox.setText("Error:");
            messageBox.setMessage(ex.getMessage());
            messageBox.open();
        });
    }

    /**
     *
     */
    public static void loadProps() {
        viewGraph = Boolean.parseBoolean(AppProps.getProperty("AppUtils.viewGraph"));
        viewTable = Boolean.parseBoolean(AppProps.getProperty("AppUtils.viewTable"));
        viewMonthlyTable = Boolean.parseBoolean(AppProps.getProperty("AppUtils.viewMonthlyTable"));
        _isWaterYear = Boolean.parseBoolean(AppProps.getProperty("AppUtils.isWaterYear"));
        _show60_20_20 = Boolean.parseBoolean(AppProps.getProperty("AppUtils.show60_20_20"));
        _show40_30_30 = Boolean.parseBoolean(AppProps.getProperty("AppUtils.show40_30_30"));
    }

    /**
     *
     */
    static {
        try {
            loadProps();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}
