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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import vista.set.DataReference;
import vista.set.DefaultReference;
import vista.set.Pathname;

/**
 * A multiple time series is a time series which has multiple
 * values for a given time point.
 *
 * @author Nicky Sandhu
 * @version $Id: MultipleTimeSeries.java,v 1.1.4.21 2001/10/23 16:28:22 jfenolio Exp $
 * Last change:  AM   15 Jun 2000    5:18 pm
 */
public class MultipleTimeSeries implements Serializable {
    public static boolean DEBUG = false;
    private final Vector _dtsNames;
    private final Vector _varTypes;
    private final Vector _bparts;
    private final Vector _cparts;
    private String _name;
//  private boolean _nameSetExplicitly;

    /**
     * create an empty multiple time series
     */
    public MultipleTimeSeries() {
        _dtsNames = new Vector();
        _varTypes = new Vector();
        _bparts = new Vector();
        _cparts = new Vector();
        _name = "Untitled " + new Random().nextInt();
    }

    public MultipleTimeSeries(String name) {
        _dtsNames = new Vector();
        _varTypes = new Vector();
        _bparts = new Vector();
        _cparts = new Vector();
        _name = name;
    }

    /**
     * loads and creates a MTS from a csv file description
     * This file is a comma separted file similar to the one
     * produced by Excel.
     * The name of the MTS is parsed from a line containing
     * "name, mts_name"
     * This is then followed by a line containing the header
     * "Derived Time Series, Svar/Dvar, B part, C part"
     * Every line after this is interpreted to be a part of the
     * description of the MTS's contents. Every part of the following
     * lines are comma separated so that the part belongs to the
     * appropriate header. Furthermore empty parts are separated by
     * commas. Any line that does not meet this format is discarded
     * and the reading process continues.
     * Only  this method adds this list to current projects list while
     * if the series is loaded using input stream method then it
     * is not added to the current project list.
     */
    public static MultipleTimeSeries load(String file) throws IOException {
        InputStream is = new FileInputStream(file);
        MultipleTimeSeries mts = load(is);
        //    AppUtils.getCurrentProject().add(mts);// add this to the current project
        return mts;
    }

    /**
     * loads and creates an MTS from a csv file stream of the same
     * format as described in load(String) method
     *
     * @see load(String)
     */
    public static MultipleTimeSeries load(InputStream is) throws IOException {
        MultipleTimeSeries mts = null;
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
        String name = null;
//    int opId;
        int count = 0; // counter for number of time series for defining this mts
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
                name = name + ".MTS";
                break;
            }
        }
        if (name == null) {
            throw new IOException("Invalid Multiple Time Series Input: No name found");
        }
        mts = new MultipleTimeSeries();
        mts._name = name.toUpperCase();
        if (line != null) {
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.toLowerCase();
                if (line.indexOf("erived") >= 0 &&
                    line.indexOf("var") >= 0 &&
                    line.indexOf("art") >= 0) {
                    break;
                }
            }
        }
        if (line == null) {
            throw new IOException("No definition's found for " +
                " multiple time series till line #: " + reader.getLineNumber());
        }
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            StringTokenizer st = new StringTokenizer(line, ",", true); // get separator tokens also
            // get MTS if any mentioned. If this exists then other columns are read
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
            mts.setDTSNameAt(count, dtsname);
            mts.setVarTypeAt(count, varType);
            mts.setBPartAt(count, bpart);
            mts.setCPartAt(count, cpart);
            count++;
        }
        is.close();
        return mts;
    }

    /**
     *
     */
    public void save(OutputStream os) throws IOException {
        AbstractTableModel model = new MTSTableModel(this);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
        writer.println("name, " + getName());
        for (int i = 0; i < model.getColumnCount(); i++) {
            writer.print(model.getColumnName(i));
            writer.print(",");
        }
        writer.println();
        for (int j = 0; j < model.getRowCount(); j++) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print(model.getValueAt(j, i));
                writer.print(",");
            }
            writer.println();
        }
        writer.close();
    }

    /**
     * gets name of this data reference
     */
    public String getName() {
        return _name;
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
            MultipleTimeSeries mts2 = null;
            if (_name != null) {
                mts2 = prj.getMTS(_name);
            }
            if (mts2 != null) {
                prj.remove(mts2);
            }
//      _nameSetExplicitly = true;
            _name = name.toUpperCase().trim();
            if (mts2 != null) {
                prj.add(this);
            }
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
        _dtsNames.removeElementAt(i);
        _varTypes.removeElementAt(i);
        _bparts.removeElementAt(i);
        _cparts.removeElementAt(i);
    }

    /**
     * expands all the vectors to the given index
     */
    private void expandTo(int i) {
        while (_dtsNames.size() - 1 < i) {
            _dtsNames.addElement("");
            _varTypes.addElement(AppUtils.DVAR);
            _bparts.addElement("");
            _cparts.addElement("");
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
     * @return the name of the DTS at the given index or null if
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
        //if ( !dtsname.equals("") && !dtsname.equals("        ") && !dtsname.endsWith(".DTS") && i != 0 ) dtsname = new String(dtsname.toUpperCase()+".DTS");
        //if ( dtsname.equals(getName())) return;
        _dtsNames.setElementAt(dtsname.toUpperCase().trim(), i);
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
     * returns the number of data references that are involved in the
     * calculation of this time series
     */
    public int getNumberOfDataReferences() {
        return _dtsNames.size();
    }

    /**
     *
     */
    public String toString() {
        return "Multiple Time Series: " + getName();
    }

    /**
     *
     */
    public void fromXml(Element de) {
        _name = de.getAttribute("name");
        if (!_name.toUpperCase().endsWith(".MTS")) {
            _name = _name.toUpperCase() + ".MTS";
        }
        int rindex = 0;
        while (true) {
            Node node = de.getFirstChild();
            if (node == null || !(node instanceof Element)) {
                break;
            }
            Element re = (Element) node;
            String dtsatt = re.getAttribute("dts");
            if (!dtsatt.equals("") && !dtsatt.endsWith(".DTS")) {
                dtsatt = dtsatt + ".DTS";
            }
            setDTSNameAt(rindex, dtsatt);
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
        Element de = doc.createElement("MTS");
        de.setAttribute("name", _name);
        int count = getNumberOfDataReferences();
        for (int i = 0; i < count; i++) {
            Element le = doc.createElement("row");
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
     *
     */
    public DataReference[] getDataReferences(int studyNumber) {
        if (_dtsNames.size() == 0) {
            return null;
        }
        DataReference[] refs = new DataReference[_dtsNames.size()];
        for (int i = 0; i < refs.length; i++) {
            String dtsname = getDTSNameAt(i);
            if (dtsname.length() > 0) {
                refs[i] = AppUtils.findDTS(dtsname);
                if (refs[i] == null) {
                    throw new RuntimeException("No DTS : " + dtsname + " found in current project or global space");
                }
                // get the data and wrap it in a default reference.
                Pathname path = Pathname.createPathname(refs[i].getPathname());
                if (studyNumber == 0) {
                    path.setPart(Pathname.F_PART, AppUtils.getCurrentProject().getBaseName());
                    refs[i] = new DefaultReference("local", "calc.dss", path.toString(),
                        ((DerivedTimeSeries) refs[i]).getDataW2(0));
                } else if (studyNumber == 1) {
                    path.setPart(Pathname.F_PART, AppUtils.getCurrentProject().getComp1Name());
                    refs[i] = new DefaultReference("local", "calc.dss", path.toString(),
                        ((DerivedTimeSeries) refs[i]).getDataW2(1));
                } else if (studyNumber == 2) {
                    path.setPart(Pathname.F_PART, AppUtils.getCurrentProject().getComp2Name());
                    refs[i] = new DefaultReference("local", "calc.dss", path.toString(),
                        ((DerivedTimeSeries) refs[i]).getDataW2(2));
                } else if (studyNumber == 3) {
                    path.setPart(Pathname.F_PART, AppUtils.getCurrentProject().getComp3Name());
                    refs[i] = new DefaultReference("local", "calc.dss", path.toString(),
                        ((DerivedTimeSeries) refs[i]).getDataW2(3));
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

}
