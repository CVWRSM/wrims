/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import vista.set.DataReference;
import vista.set.DataSet;
import vista.set.Pathname;
import vista.set.RegularTimeSeries;
import vista.set.TimeSeries;
import vista.time.TimeWindow;

/**
 * A time series representing a time series
 * derived from math operations on other
 * time series.
 * <p>
 * This class allows for incremental addition and editing of the
 * references for the calculation of this time series. Thus at
 * any time a few of the references may be in invalid. These references
 * will be warned about during calculation of the values in the time
 * series however they will stop the calculation unless all the references
 * are invalid.
 * <p>
 * As the user attempts to index i to set any parameter the derived time
 * series is expanded by that amount and all intermediate references are
 * empty and therefore ignored in the calculations. If the index already
 * exists then nothing is done.
 * <p>
 * While the name of this DTS is not explicitly set by the user a name
 * is derived from the combination of b parts or DTS and operations on
 * them.
 * <p>
 * If the first operation id is a subtract sign then -1 is multiplied
 * into the first data reference. All other operations are ignored for
 * the first reference in this DTS.
 * <p>
 * This data reference though similar to the vista data reference
 * optimizes for multiple math operations by not having to
 * calculate and store intermediate results.
 *
 * @author Nicky Sandhu
 * @version $Id: DerivedTimeSeries.java,v 1.1.4.38 2001/12/05 18:25:02 jfenolio Exp $
 * @see vista.set.DataReference
 */
public class DerivedTimeSeries extends DataReference implements Serializable {
    public static boolean DEBUG = false;
    public static String DEFAULT_B_PART = "";
    public static String DEFAULT_C_PART = "";
    boolean recalculate = true;

    /**
     * create an empty derived time series
     */
    public DerivedTimeSeries() {
        _opIds = new Vector();
        _dtsNames = new Vector();
        _varTypes = new Vector();
        _bparts = new Vector();
        _cparts = new Vector();
        setServername("local");
        setFilename("calculated");
        _name = "Untitled " + new Random().nextInt();
    }

    /**
     *
     */
    public DerivedTimeSeries(DerivedTimeSeries dts) {
        DerivedTimeSeries ndts = new DerivedTimeSeries();
        ndts._name = dts.getName();
        ndts._opIds = (Vector) dts._opIds.clone();
        ndts._dtsNames = (Vector) dts._dtsNames.clone();
        ndts._varTypes = (Vector) dts._varTypes.clone();
        ndts._bparts = (Vector) dts._bparts.clone();
        ndts._cparts = (Vector) dts._cparts.clone();
    }

    public DerivedTimeSeries(String name) {
        _opIds = new Vector();
        _dtsNames = new Vector();
        _varTypes = new Vector();
        _bparts = new Vector();
        _cparts = new Vector();
        setServername("local");
        setFilename("calculated");
        _name = name;
    }

    /**
     *
     */
    public static DerivedTimeSeries load(String file) throws IOException {
        InputStream is = new FileInputStream(file);
        DerivedTimeSeries dts = load(is);
        return dts;
    }

    /**
     *
     */
    public static DerivedTimeSeries load(InputStream is) throws IOException {
        DerivedTimeSeries dts = null;
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
        String name = null;
        int opId;
        int count = 0; // counter for number of time series for defining this dts
        String dtsname = "", varType = "", bpart = "", cpart = "";
        String line = null;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.indexOf("name") >= 0) {
                StringTokenizer st = new StringTokenizer(line, ",");
                st.nextToken();
                name = st.nextToken().trim();
                name = name + ".DTS";
                break;
            }
        }
        if (name == null) {
            throw new IOException("Invalid Derived Time Series Input: No name found");
        }
        dts = new DerivedTimeSeries();
        dts._name = name.toUpperCase().trim();
        if (line != null) {
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.toLowerCase();
                if (line.indexOf("perator") >= 0 &&
                    line.indexOf("erived") >= 0 &&
                    line.indexOf("var") >= 0 &&
                    line.indexOf("art") >= 0) {
                    break;
                }
            }
        }
        if (line == null) {
            throw new IOException("No definition's found for " +
                " derived time series till line #: " + reader.getLineNumber());
        }
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            StringTokenizer st = new StringTokenizer(line, ",", true); // get separator tokens also
            // get operator id, this has to exist
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equals(",")) {
                    if (DEBUG) {
                        System.err.println(
                            "No operator id found in first column, ignoring line # " + reader.getLineNumber());
                    }
                    continue;
                } else {
                    opId = AppUtils.getOperationId(token.trim());
                    if (opId == -1) {
                        if (DEBUG) {
                            System.err.println("Invalid operator id, ignoring line #: " + reader.getLineNumber());
                        }
                        continue;
                    } else {
                        if (st.hasMoreTokens()) {
                            st.nextToken(); // skip next separator
                        }
                    }
                }
            } else {
                if (DEBUG) {
                    System.err.println("Illegal format, ignoring line: " + line);
                }
                continue;
            }
            // get DTS if any mentioned. If this exists then other columns are read
            // but will be ignored for calculation purposes
            if (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (token.equals(",")) {
                    dtsname = "";
                } else {
                    dtsname = token;
                    varType = "";
                    bpart = "";
                    cpart = "";
                    if (st.hasMoreTokens()) {
                        st.nextToken();
                    }
                }
            }
            // get svar, b part and c part, these must exist if dtsname is ""
            if (dtsname.equals("")) {
                if (st.hasMoreTokens()) {
                    varType = st.nextToken().trim();
                    if (varType.equals(",")) {
                        if (DEBUG) {
                            System.err.println("Missing variable type on line # : " + reader.getLineNumber());
                        }
                        continue;
                    } else {
                        varType = varType.toUpperCase();
                        if (st.hasMoreTokens()) {
                            st.nextToken();
                        }
                        if (varType.equals(AppUtils.SVAR)) {
                        } else if (varType.equals(AppUtils.DVAR)) {
                        } else {
                            if (DEBUG) {
                                System.err.println(
                                    "Invalid variable type: " + varType + " on line # : " + reader.getLineNumber());
                            }
                            continue;
                        }
                    }
                } else {
                    if (DEBUG) {
                        System.err.println("Missing variable type on line #: " + reader.getLineNumber());
                    }
                    continue;
                }
                // get b part
                if (st.hasMoreTokens()) {
                    bpart = st.nextToken().trim();
                    if (bpart.equals(",")) {
                        if (DEBUG) {
                            System.err.println("Missing bpart on line # : " + reader.getLineNumber());
                        }
                        continue;
                    } else {
                        if (st.hasMoreTokens()) {
                            st.nextToken();
                        }
                    }
                } else {
                    if (DEBUG) {
                        System.err.println("Missing b part on line # : " + reader.getLineNumber());
                    }
                }
                // get c part
                if (st.hasMoreTokens()) {
                    cpart = st.nextToken().trim();
                    if (cpart.equals(",")) {
                        if (DEBUG) {
                            System.err.println("Missing cpart on line: " + reader.getLineNumber());
                        }
                        continue;
                    } else {
                    }
                } else {
                    if (DEBUG) {
                        System.err.println("Missing c part on line : " + reader.getLineNumber());
                    }
                }
            } // if (dtsname.equals("")...
            dts.setOperationIdAt(count, opId);
            dts.setDTSNameAt(count, dtsname);
            dts.setVarTypeAt(count, varType);
            dts.setBPartAt(count, bpart);
            dts.setCPartAt(count, cpart);
            count++;
        }
        is.close();
        return dts;
    }

    /**
     * sets name of this data reference
     */
    public void setName(String name) {
        if (name == null) {
            return;
        }
        name = name.toUpperCase().trim();
        if (name.equals(_name)) {
            return;
        }
        if (name.length() > 0 && AppUtils.nameNotTaken(name)) {
            Project prj = AppUtils.getCurrentProject();
            DerivedTimeSeries dts2 = null;
            if (_name != null) {
                dts2 = prj.getDTS(_name);
            }
            if (dts2 != null) {
                prj.remove(dts2);
            }
            _name = name.toUpperCase().trim();
            _modified = true;
            if (dts2 != null) {
                prj.add(this);
            }
            prj.setDTSMod(true);
        } else {
            if (AppUtils.nameNotTaken(name.toUpperCase())) {
                throw new RuntimeException("Invalid name");
            } else {
                throw new RuntimeException("Name already taken, try another");
            }
        }
    }

    /**
     * remove the i'th data reference.
     */
    public void remove(int i) {
        _opIds.removeElementAt(i);
        _dtsNames.removeElementAt(i);
        _varTypes.removeElementAt(i);
        _bparts.removeElementAt(i);
        _cparts.removeElementAt(i);
    }

    /**
     * expands all the vectors to the given index
     */
    private void expandTo(int i) {
        _modified = true;
        while (_dtsNames.size() - 1 < i) {
            _opIds.addElement(new Integer(0));
            _dtsNames.addElement("");
            _varTypes.addElement(AppUtils.DVAR);
            _bparts.addElement(DEFAULT_B_PART);
            _cparts.addElement(DEFAULT_C_PART);
        }
    }

    /**
     * inserts an empty row at given index
     */
    public void insertAt(int i) {
        if (i >= _dtsNames.size()) {
            return;
        }
        if (i < 0) {
            return;
        }
        _opIds.insertElementAt(new Integer(0), i);
        _dtsNames.insertElementAt("", i);
        _varTypes.insertElementAt("", i);
        _bparts.insertElementAt("", i);
        _cparts.insertElementAt("", i);
    }

    /**
     * checks if access is ok
     */
    private void checkAccessAt(int i) {
        if (i >= _dtsNames.size()) {
            throw new RuntimeException("Element accessed beyond bounds: " + i);
        }
    }

    /**
     * @return the name of the DTS at the given index or "" (empty string) if
     * it is not a DTS.
     */
    public String getDTSNameAt(int i) {
        checkAccessAt(i);
        return (String) _dtsNames.elementAt(i);
    }

    /**
     * set the name of the DTS to be used at the given index. This
     * will cause any set b/c parts to be ignored. If dtsname is
     * null or the empty string then it will not be used and
     * any set b/c parts will still be used.
     */
    public void setDTSNameAt(int i, String dtsname) {
        expandTo(i);
        String nm = dtsname.toUpperCase().trim();
        if (!nm.equals("") && !nm.endsWith(".DTS")) {
            nm = nm + ".DTS";
        }
        if (nm.equals(getName())) {
            return;
        }
        _dtsNames.setElementAt(nm, i);
    }

    /**
     * @return AppUtils.SVAR or AppUtils.DVAR if they are valid
     * else returns null
     */
    public String getVarTypeAt(int i) {
        checkAccessAt(i);
        return (String) _varTypes.elementAt(i);
    }

    /**
     * set the variable type at index to AppUtils.SVAR or .DVAR
     */
    public void setVarTypeAt(int i, String varType) {
        expandTo(i);
        _varTypes.setElementAt(varType.toUpperCase().trim(), i);
    }

    /**
     * @return the b part of pathname or null if invalid
     */
    public String getBPartAt(int i) {
        checkAccessAt(i);
        return (String) _bparts.elementAt(i);
    }

    /**
     * sets the b part at the given index
     */
    public void setBPartAt(int i, String bpart) {
        expandTo(i);
        _bparts.setElementAt(bpart.toUpperCase().trim(), i);
    }

    /**
     * @return the c part of pathname or null if invalid
     */
    public String getCPartAt(int i) {
        checkAccessAt(i);
        return (String) _cparts.elementAt(i);
    }

    /**
     * sets the c part at the given index
     */
    public void setCPartAt(int i, String cpart) {
        expandTo(i);
        _cparts.setElementAt(cpart.toUpperCase().trim(), i);
    }

    /**
     * gets the operation id for the given index
     */
    public int getOperationIdAt(int i) {
        checkAccessAt(i);
        return ((Integer) _opIds.elementAt(i)).intValue();
    }

    /**
     * sets the operation id for the given index
     */
    public void setOperationIdAt(int i, int opId) {
        expandTo(i);
        _opIds.setElementAt(new Integer(opId), i);
    }

    /**
     * returns the number of data references that are involved in the
     * calculation of this time series
     */
    public int getNumberOfDataReferences() {
        return _opIds.size();
    }

    /**
     * reloads data and recalculates its operations
     */
    public void reloadData() {
        // get ready to redo calculations
        _dataSet = null;
//    _dataSet2 = null;
        _modified = true;
    }

    private String _svf1, _svf2, _dvf1, _dvf2;
    //  private boolean _oldMode;
    private Project _oldProject;
    private TimeWindow _oldTimeWindow;

    /**
     * controls whether recalculation is needed or not
     */
    private boolean recalculationNeeded() {
        // recalculation needed if
        // 1. dts has been changed
        // 2. project has been changed
        // 3. project time window has been changed
        // 5. project files have been changed depending upon mode
        //
        if (_dataSet == null) {
            _oldProject = AppUtils.getCurrentProject();
            _oldTimeWindow = _oldProject.getTimeWindow();
            _svf1 = _oldProject.getSVFile();
            _svf2 = _oldProject.getSV2File();
            _dvf1 = _oldProject.getDVFile();
            _dvf2 = _oldProject.getDV2File();
            _modified = false;
            return true;
        }
        if (this._modified) {
            _modified = false;
            return true;
        }
        if (_oldProject != AppUtils.getCurrentProject()) {
            _oldProject = AppUtils.getCurrentProject();
            _oldTimeWindow = _oldProject.getTimeWindow();
            _svf1 = _oldProject.getSVFile();
            _svf2 = _oldProject.getSV2File();
            _dvf1 = _oldProject.getDVFile();
            _dvf2 = _oldProject.getDV2File();
            return true;
        }
        Project prj = AppUtils.getCurrentProject();
        TimeWindow tw = prj.getTimeWindow();
        if (tw != null && !tw.isSameAs(_oldTimeWindow)) {
            _oldTimeWindow = prj.getTimeWindow();
            return true;
        }
        if (!_svf1.equals(prj.getSVFile()) ||
            !_svf2.equals(prj.getSV2File()) ||
            !_dvf1.equals(prj.getDVFile()) ||
            !_dvf2.equals(prj.getDV2File())
        ) {
            _svf1 = _oldProject.getSVFile();
            _svf2 = _oldProject.getSV2File();
            _dvf1 = _oldProject.getDVFile();
            _dvf2 = _oldProject.getDV2File();
            return true;
        }
    /*if (AppUtils.needsRecataloging(_dvf1) || AppUtils.needsRecataloging(_dvf2) ||
    			AppUtils.needsRecataloging(_svf1) || AppUtils.needsRecataloging(_svf2)) return true;*/
        return false/*recalculate*/;
    }

    /**
     * Retrieves data from the data base if data is null. If
     * retrieval fails it throws a RuntimeException. It
     * goes through the list of given references, gets the data
     * and does the math operations
     *
     * @return reference to the initialized data set.
     */
    public DataSet getData(int studyNumber) {
        if (DEBUG) {
            Integer.toString(studyNumber);
        }
        // if calculations have been done then no need to repeat.
        if (recalculationNeeded() || recalculate || updateDataSets()) {
            _dataSet = null;
            recalculate = false;
        } else {
            for (int i = 0; i < AppUtils.getCheckedDSS(); i++) {
                if (studyNumber == i) {
                    return _dataSet[i];
                }
            }
        }
        // get current project
//    Project prj = AppUtils.getCurrentProject();
        // form data references from b/c parts if any
//    Group svg=null, dvg=null;
        DataReference[] refs = getDataReferences(studyNumber);
        // check to make sure some data is available
        if (refs == null) {
            throw new RuntimeException("Empty DTS Table");
        }
//    String apart="",bpart = "",cpart="",epart="";
//    boolean gotOneGood = false;
        DataSet dataSet = null;
        for (int i = 0; i < AppUtils.getCheckedDSS(); i++) {
            if (studyNumber == i) {
                dataSet = doCalculations(studyNumber, refs);
                dataSet.setName(getPathname().toString());
            }
        }
        return dataSet;
    }

    /**
     * Retrieves data from the data base if data is null. If
     * retrieval fails it throws a RuntimeException. It
     * goes through the list of given references, gets the data
     * and does the math operations
     *
     * @return reference to the initialized data set.
     */
    public DataSet getDataW2(int studyNumber) {
        if (DEBUG) {
            Integer.toString(studyNumber);
        }
        DataReference[] refs = getDataReferences(studyNumber);
        // check to make sure some data is available
        if (refs == null) {
            throw new RuntimeException("Empty DTS Table");
        }
        DataSet dataSet;
        dataSet = doCalculations(studyNumber, refs);
        dataSet.setName(getPathname().toString());
        return dataSet;
    }

    /**
     * Updates references to the correct selected dss files from the message panel
     * check box.  Returns true if the different dss's were selected from the last time this
     * method was called or the first time it is called
     */
    public boolean updateDataSets() {
        int[] check = {0, 0, 0, 0};
        boolean changed = false;
        if (AppUtils.baseOn) {
            check[0] = 1;
        }
        if (AppUtils.comp1On) {
            check[1] = 1;
        }
        if (AppUtils.comp2On) {
            check[2] = 1;
        }
        if (AppUtils.comp3On) {
            check[3] = 1;
        }
        for (int i = 0; i < AppUtils.getCheckedDSS(); i++) {
            if (check[i] != dss[i]) {
                changed = true;
            }
        }
        if (changed) {
            dss = check;
        }
        return changed;
    }

    /**
     * Retrieves data from the data base if data is null. If
     * retrieval fails it throws a RuntimeException. It
     * goes through the list of given references, gets the data
     * and does the math operations
     *
     * @return reference to the initialized data set.
     */
    public DataSet getData() {
        // if calculations have been done then no need to repeat. ??
        return getDataW2(1);
    }

    /**
     *
     */
    public Pathname getPathname() {
        Pathname spath = super.getPathname();
        if (_modified || spath == null ||
            !spath.getPart(Pathname.B_PART).equals(getName())) {
            Project prj = AppUtils.getCurrentProject();
            String apart = "CALSIM";
            String bpart = getName();
            String cpart = "";
            String epart = "1MON";
            String fpart = prj.getBaseName();
            if (AppUtils.plotDifference) {
                fpart += " - " + prj.getComp1Name();
            }
            int nref = getNumberOfDataReferences();
            Hashtable cpt = new Hashtable();
            for (int i = 0; i < nref; i++) {
                String cp = "";
                String dtsName = getDTSNameAt(i);
                if (!dtsName.equals("")) {
                    DerivedTimeSeries dts = AppUtils.findDTS(dtsName);
                    if (dts == null) {
                        continue;
                    }
                    cp = dts.getPathname().getPart(Pathname.C_PART);
                } else {
                    cp = getCPartAt(i);
                }
                if (!cp.equals("")) {
                    StringTokenizer st = new StringTokenizer(cp, ",");
                    while (st.hasMoreTokens()) {
                        cp = st.nextToken();
                        if (!cpt.containsKey(cp)) {
                            cpt.put(cp, cp);
                        }
                    }
                }
            }
            for (Enumeration e = cpt.elements(); e.hasMoreElements(); ) {
                if (cpart.equals("")) {
                    cpart = (String) e.nextElement();
                } else {
                    cpart += ", " + e.nextElement();
                }
            }
            Pathname path = super.getPathname();
            if (path == null) {
                path = Pathname.createPathname
                    (new String[] {apart, bpart, cpart, "", epart, fpart});
                super.setPathname(path);
            } else {
                path.setPart(Pathname.A_PART, apart);
                path.setPart(Pathname.B_PART, bpart);
                path.setPart(Pathname.C_PART, cpart);
                path.setPart(Pathname.E_PART, epart);
                path.setPart(Pathname.F_PART, fpart);
                super.setPathname(path);
            }
        }
        return super.getPathname();
    }

    /**
     *
     */
    protected DataSet doCalculations(int studyNumber, DataReference[] refs) {
        // loop over items and do operation if possible, ignore the items
        // for which data is invalid
        boolean copyMade = false;
        int count = refs.length;
        DataSet dataSet = null;
        for (int i = 0; i < count; i++) {
            DataReference ref = refs[i];
            if (ref == null) {
                AppUtils.displayError("Possible invalid data reference for # " + (i + 1));
            } else {
                if (dataSet == null) {
                    AppUtils.changeToCurrentUnits(ref);
                    if (ref instanceof DerivedTimeSeries) {
                        dataSet = ((DerivedTimeSeries) ref).getDataW2(studyNumber);
                    } else {
                        dataSet = ref.getData();
                    }
                    copyMade = false;
                } else {
                    if (!copyMade) {
                        dataSet = TSMath.createCopy((TimeSeries) dataSet);
                        copyMade = true;
                    }
                    DataSet ds2 = null;
                    AppUtils.changeToCurrentUnits(ref);
                    if (ref instanceof DerivedTimeSeries) {
                        ds2 = ((DerivedTimeSeries) ref).getDataW2(studyNumber);
                    } else {
                        ds2 = ref.getData();
                    }
                    dataSet = TSMath.doMath((RegularTimeSeries) dataSet,
                        (RegularTimeSeries) ds2,
                        getOperationIdAt(i));
                }
            }
        }
        return dataSet;
    }

    /**
     *
     */
    private DataReference[] getDataReferences(int studyNumber) {
        System.out.println(_opIds.size());
        if (_opIds.size() == 0) {
            return null;
        }
        DataReference[] refs = new DataReference[_opIds.size()];
        for (int i = 0; i < refs.length; i++) {
            String dtsname = getDTSNameAt(i);
            if (dtsname.length() > 0) {
                refs[i] = AppUtils.findDTS(dtsname);
                if (refs[i] == null) {
                    throw new RuntimeException("No DTS : " + dtsname
                        + " found in current project or global space");
                }
            } else {
                String vt = getVarTypeAt(i);
                String bpart = getBPartAt(i);
                String cpart = getCPartAt(i);
                refs[i] = AppUtils.getDataReference(studyNumber, bpart, cpart, vt);
                if (refs[i] == null) {
                    throw new RuntimeException("No data found for " + cpart + " at " + bpart);
                }
            }
        }
        return refs;
    }

    /**
     *
     */
    public String toString() {
        return "Derived Time Series: " + getName();
    }

    /**
     *
     */
    public boolean equals(Object obj) {
        if (obj instanceof DerivedTimeSeries) {
            DerivedTimeSeries dts = (DerivedTimeSeries) obj;
            return _name.equals(dts.getName());
        } else {
            return false;
        }
    }

    /**
     *
     */
    public void fromXml(Element de) {
        _name = de.getAttribute("name");
        if (!_name.toUpperCase().endsWith(".DTS")) {
            _name = _name.toUpperCase() + ".DTS";
        }

        DocumentTraversal traversal = (DocumentTraversal) de.getOwnerDocument();
        TreeWalker tw = traversal.createTreeWalker(de, NodeFilter.SHOW_ELEMENT, null, false);
        int rindex = 0;
        while (true) {
            Element re = XmlUtilities.getNextElement(tw, "row");
            if (re == null) {
                break;
            }
            int opId = AppUtils.getOperationId(re.getAttribute("operator"));
            setOperationIdAt(rindex, opId);
            setDTSNameAt(rindex, re.getAttribute("dts"));
            setVarTypeAt(rindex, re.getAttribute("vartype"));
            setBPartAt(rindex, re.getAttribute("bpart"));
            setCPartAt(rindex, re.getAttribute("cpart"));
            rindex++;
        }
    }

    /**
     *
     */
    public void toXml(Document doc, Element ae) {
        Element de = doc.createElement("DTS");
        de.setAttribute("name", _name);
        int count = getNumberOfDataReferences();
        for (int i = 0; i < count; i++) {
            Element le = doc.createElement("row");
            le.setAttribute("operator", AppUtils.getOperationName(getOperationIdAt(i)));
            le.setAttribute("dts", getDTSNameAt(i));
            le.setAttribute("vartype", getVarTypeAt(i));
            le.setAttribute("bpart", getBPartAt(i));
            le.setAttribute("cpart", getCPartAt(i));
            de.appendChild(le);
        }
        if (ae != null) {
            ae.appendChild(de);
        } else {
            doc.appendChild(de);
        }
    }

    /**
     * create a clone of itself
     */

    protected DataReference createClone() {
        return new DerivedTimeSeries(this);
    }

    /**
     * gets name of this data reference
     */
    public String getName() {
        return _name;
    }

    /*
      private variables
    */
    private Vector _opIds, _dtsNames, _varTypes, _bparts, _cparts;
    private String _name;
    private transient DataSet[] _dataSet;
    private boolean _modified;
    //Not private variable
    int[] dss = {0, 0, 0, 0};

}
