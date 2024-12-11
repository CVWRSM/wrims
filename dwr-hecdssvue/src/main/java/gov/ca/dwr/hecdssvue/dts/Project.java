/*
 * Copyright (c) 2024
 * United States Army Corps of Engineers - Hydrologic Engineering Center (USACE/HEC)
 * All Rights Reserved.  USACE PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval from HEC
 */

package gov.ca.dwr.hecdssvue.dts;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import vista.set.Group;
import vista.set.Pathname;
import vista.time.TimeWindow;

/**
 * A project contains a user defined list of DerivedTimeSeries (DTS) and
 * and MultipleTimeSeries (MTS).
 * This project is a container for user defined lists. All globally defined
 * lists (DTS and MTS) are not part of this project and exist independently
 * of this project. This list is a set in that none of the elements are
 * repeated.
 * A project has a study consisting of decision and state variable files from
 * which it gets the data. In the comparative mode there may further be an
 * alternative study against which the study is compared, usually by taking
 * differences.
 * <p>
 * <p>
 * The current project can always be accessed using AppUtils.getCurrentProject()
 *
 * @author Nicky Sandhu
 * @version $Id: Project.java,v 1.1.4.25 2001/10/23 16:28:23 jfenolio Exp $
 * @see DerivedTimeSeries, MultipleTimeSeries
 */
public class Project implements Serializable {
    /**
     * initializes all lists and creates an empty project
     */
    public Project() {
        _dtsList = new Hashtable();
        _mtsList = new Hashtable();
        _svList = new Hashtable();
        _dvList = new Hashtable();
        setTimeWindow(AppUtils.DEFAULT_TIME_WINDOW);
        _dtsmod = true;
    }

    //Sets the b and c parts in the SV file to a Hashtable
    public void setSVHashtable() {
        Group g = getSVBaseGroup();
        if (g != null) {
            //Group g = Group.createGroup(svg);
            int num = g.getNumberOfDataReferences();
            _svList = new Hashtable(num);
            String b, c;
            _bsv = new String[num];
            for (int i = 0; i < num; i++) {
                Pathname p = g.getDataReference(i).getPathname();
                b = p.getPart(Pathname.B_PART);
                c = p.getPart(Pathname.C_PART);
                if (b == null || c == null) {
                    return;
                }
                _bsv[i] = b;
                _svList.put(b, c);
            }
        }
    }

    //Sets the b and c parts in the DV file to a Hashtable
    public void setDVHashtable() {
        Group g = getDVBaseGroup();
        if (g != null) {
            //Group g = Group.createGroup(dvg);
            int num = g.getNumberOfDataReferences();
            _dvList = new Hashtable(num);
            String b, c;
            _bdv = new String[num];
            for (int i = 0; i < num; i++) {
                Pathname p = g.getDataReference(i).getPathname();
                b = p.getPart(Pathname.B_PART);
                c = p.getPart(Pathname.C_PART);
                if (b == null || c == null) {
                    return;
                }
                _bdv[i] = b;
                _dvList.put(b, c);
            }
        }
    }

    //Returns a Hashtable with the b & c parts of the SV table in it
    public Hashtable getSVHashtable() {
        return _svList;
    }

    //Returns a Hashtable with the b & c parts of the DV table in it
    public Hashtable getDVHashtable() {
        return _dvList;
    }

    /**
     * adds a derived time series to this project only if
     * it is not already there.
     */
    public void add(DerivedTimeSeries dts) {
        if (!_dtsList.containsKey(dts.getName())) {
            _dtsList.put(dts.getName(), dts);
            _dtsmod = true;
        }
    }

    /**
     * removes the DTS if it is in the project
     */
    public void remove(DerivedTimeSeries dts) {
        if (_dtsList.contains(dts)) {
            _dtsList.remove(dts.getName());
        }
    }

    /**
     * removes a MTS or DTS by the given name from the project.
     * else prints out an error
     */
    public void remove(String name) {
        if (name == null) {
            return;
        }
        name = name.toUpperCase().trim();
        if (_dtsList.containsKey(name)) {
            _dtsList.remove(name);
        } else if (_mtsList.containsKey(name)) {
            _mtsList.remove(name);
        } else {
            System.err.println("No matching name : " + name + " found in mts or dts list");
        }
    }

    public boolean isInLists(String name) {
        if (name == null) {
            return false;
        }
        name = name.toUpperCase().trim();
        if (_dtsList.containsKey(name)) {
            return true;
        } else {
            return _mtsList.containsKey(name);
        }
    }

    /**
     * adds a MTS if it does not already present
     */
    public void add(MultipleTimeSeries mts) {
        if (!_mtsList.containsKey(mts.getName())) {
            _mtsList.put(mts.getName(), mts);
            _dtsmod = true;
        }
    }

    /**
     * removes the given MTS if present
     */
    public void remove(MultipleTimeSeries mts) {
        if (_mtsList.contains(mts)) {
            _mtsList.remove(mts.getName());
        }
    }

    /**
     * @return the list of DTS as an array or null if none present
     */
    public DerivedTimeSeries[] getDTSList() {
        if (_dtsList == null) {
            return null;
        }
        DerivedTimeSeries[] array = new DerivedTimeSeries[_dtsList.size()];
        if (array == null) {
            System.out.println(true);
        }
        if (_dtsList.size() == 0) {
            return null;
        }
        int count = 0;
        for (Enumeration e = _dtsList.elements(); e.hasMoreElements(); ) {
            array[count] = (DerivedTimeSeries) e.nextElement();
            count++;
        }
        return array;
    }

    /**
     * @return the names of all the DTS in this project
     */
    public String[] getDTSNames() {
        if (_dtsList.size() == 0) {
            return null;
        }
        String[] array = new String[_dtsList.size() + 1];
        array[0] = " ";
        int count = 1;
        for (Enumeration e = _dtsList.elements(); e.hasMoreElements(); ) {
            array[count] = ((DerivedTimeSeries) e.nextElement()).getName();
            count++;
        }
        return array;
    }

    /**
     * @return the DTS with the given name or null if not found
     */
    public DerivedTimeSeries getDTS(String name) {
        DerivedTimeSeries dts;
        if (_dtsList != null) {
            dts = (DerivedTimeSeries) _dtsList.get(name);
        } else {
            dts = null;
        }
        return dts;
    }

    /**
     * @return the MTS with the given name or null if not found
     */
    public MultipleTimeSeries getMTS(String name) {
        MultipleTimeSeries mts = (MultipleTimeSeries) _mtsList.get(name);
        return mts;
    }

    /**
     * sets the decision variable file for the study
     */
    public void setDVFile(String file) {
        _dvf = file;
        _dvg = null;
    }

    /**
     *
     */
    public String getDVFile() {
        return _dvf == null ? "" : _dvf;
    }

    /**
     * sets the state variable file for the study
     */
    public void setSVFile(String file) {
        _svf = file;
        _svg = null;
    }

    /**
     *
     */
    public String getSVFile() {
        return _svf == null ? "" : _svf;
    }

    /**
     * sets the decision variable file for the alternative study
     */
    public void setDV2File(String file) {
        _dv2f = file;
        _dv2g = null;
    }

    /**
     *
     */
    public String getDV2File() {
        return _dv2f == null ? "" : _dv2f;
    }

    /**
     * sets the state variable file for the alternative study
     */
    public void setSV2File(String file) {
        _sv2f = file;
        _sv2g = null;
    }

    /**
     *
     */
    public String getSV2File() {
        return _sv2f == null ? "" : _sv2f;
    }

    /**
     * sets the decision variable file for the alternative study
     */
    public void setDV3File(String file) {
        _dv3f = file;
        _dv3g = null;
    }

    /**
     * sets the state variable file for the alternative study
     */
    public void setSV3File(String file) {
        _sv3f = file;
        _sv3g = null;
    }

    /**
     * sets the decision variable file for the alternative study
     */
    public void setDV4File(String file) {
        _dv4f = file;
        _dv4g = null;
    }

    /**
     * sets the state variable file for the alternative study
     */
    public void setSV4File(String file) {
        _sv4f = file;
        _sv4g = null;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the decision variables in the study
     * @see Group
     */
    public Group getDVBaseGroup() {
        if (AppUtils.selectedStudies[0]) { // &&  AppUtils.needsRecataloging(_dvf)){
            _dvgbase = AppUtils.openDSSFile(_dvf);
        } else if (AppUtils.selectedStudies[1]) { // && AppUtils.needsRecataloging(_dv2f)){
            _dvgbase = AppUtils.openDSSFile(_dv2f);
        } else if (AppUtils.selectedStudies[2]) { // && AppUtils.needsRecataloging(_dv3f)){
            _dvgbase = AppUtils.openDSSFile(_dv3f);
        } else if (AppUtils.selectedStudies[3]) { // && AppUtils.needsRecataloging(_dv4f)){
            _dvgbase = AppUtils.openDSSFile(_dv4f);
        }
        //if ( _dvg == null ){
        //  throw new RuntimeException("No file loaded for decision variables!");
        //}
        return _dvgbase;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the state variables in the study
     * @see Group
     */
    public Group getSVBaseGroup() {
        if (AppUtils.selectedStudies[0]) { // && AppUtils.needsRecataloging(_svf)){
            _svgbase = AppUtils.openDSSFile(_svf);
        } else if (AppUtils.selectedStudies[1]) {// && AppUtils.needsRecataloging(_sv2f)){
            _svgbase = AppUtils.openDSSFile(_sv2f);
        } else if (AppUtils.selectedStudies[2]) {// && AppUtils.needsRecataloging(_sv3f)){
            _svgbase = AppUtils.openDSSFile(_sv3f);
        } else if (AppUtils.selectedStudies[3]) {// && AppUtils.needsRecataloging(_sv4f)){
            _svgbase = AppUtils.openDSSFile(_sv4f);
        }
        //if ( _svg == null ){
        //  throw new RuntimeException("No file loaded for state variables!");
        //}
        return _svgbase;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the decision variables in the alternative study
     * @see Group
     */
    public Group openDVGroup() {
        //if ( _dvg == null || AppUtils.needsRecataloging(_dv2f)){
        _dvg = AppUtils.openDSSFile(_dvf);
        //}
        //if ( _dv2g == null ){
        //  throw new RuntimeException("No file loaded for alternate study decision variables!");
        //}
        return _dvg;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the state variables in the alternative study
     * @see Group
     */
    public Group openSVGroup() {
        //if ( _sv2g == null || AppUtils.needsRecataloging(_sv2f)) {
        _svg = AppUtils.openDSSFile(_svf);
        //}
        //if ( _sv2g == null ){
        //  throw new RuntimeException("No file loaded for alternate study state variables!");
        //}
        return _svg;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the decision variables in the alternative study
     * @see Group
     */
    public Group openDV2Group() {
        //if ( _dv2g == null || AppUtils.needsRecataloging(_dv2f)){
        _dv2g = AppUtils.openDSSFile(_dv2f);
        //}
        //if ( _dv2g == null ){
        //  throw new RuntimeException("No file loaded for alternate study decision variables!");
        //}
        return _dv2g;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the state variables in the alternative study
     * @see Group
     */
    public Group openSV2Group() {
        //if ( _sv2g == null || AppUtils.needsRecataloging(_sv2f)) {
        _sv2g = AppUtils.openDSSFile(_sv2f);
        //}
        //if ( _sv2g == null ){
        //  throw new RuntimeException("No file loaded for alternate study state variables!");
        //}
        return _sv2g;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the decision variables in the alternative study
     * @see Group
     */
    public Group openDV3Group() {
        //if ( _dv3g == null || AppUtils.needsRecataloging(_dv3f)){
        _dv3g = AppUtils.openDSSFile(_dv3f);
        //}
        //if ( _dv2g == null ){
        //  throw new RuntimeException("No file loaded for alternate study decision variables!");
        //}
        return _dv3g;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the state variables in the alternative study
     * @see Group
     */
    public Group openSV3Group() {
        //if ( _sv3g == null || AppUtils.needsRecataloging(_sv3f)) {
        _sv3g = AppUtils.openDSSFile(_sv3f);
        //}
        //if ( _sv3g == null ){
        //  throw new RuntimeException("No file loaded for alternate study state variables!");
        //}
        return _sv3g;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the decision variables in the alternative study
     * @see Group
     */
    public Group openDV4Group() {
        //if ( _dv4g == null || AppUtils.needsRecataloging(_dv4f)){
        _dv4g = AppUtils.openDSSFile(_dv4f);
        //}
        //if ( _dv4g == null ){
        //  throw new RuntimeException("No file loaded for alternate study decision variables!");
        //}
        return _dv4g;
    }

    /**
     * @return a group of all the data references (containing data sets)
     * for the state variables in the alternative study
     * @see Group
     */
    public Group openSV4Group() {
        //if ( _sv4g == null || AppUtils.needsRecataloging(_sv4f)) {
        _sv4g = AppUtils.openDSSFile(_sv4f);
        //}
        //if ( _sv4g == null ){
        //  throw new RuntimeException("No file loaded for alternate study state variables!");
        //}
        return _sv4g;
    }

    public Group getDVGroup() {
        return _dvg;
    }

    public Group getSVGroup() {
        return _svg;
    }

    public Group getDV2Group() {
        return _dv2g;
    }

    public Group getSV2Group() {
        return _sv2g;
    }

    public Group getDV3Group() {
        return _dv3g;
    }

    public Group getSV3Group() {
        return _sv3g;
    }

    public Group getDV4Group() {
        return _dv4g;
    }

    public Group getSV4Group() {
        return _sv4g;
    }

    /**
     * @return the currently valid time window
     * @see TimeWindow
     */
    public TimeWindow getTimeWindow() {
        return _tw;
    }

    /**
     * sets the time window to the given time window string
     *
     * @param twstr is a time window string representation in
     *              the format MMMyyyy - MMMyyyy, e.g.
     *              JAN1990 - NOV1993
     */
    public void setTimeWindow(String twstr) {
        TimeWindow tw = null;
        try {
            tw = AppUtils.createTimeWindowFromString(twstr);
        } catch (Exception e) {
            throw new RuntimeException("Invalid time window string: " + twstr);
        }
        _tw = tw;
    }

    /**
     * gets the study name by using the F part of the data base
     * to guess it
     *
     * @return the study name
     */
    public String getBaseName() {
        return AppUtils.guessStudyNameFromGroup(getDVBaseGroup());
    }

    /**
     * gets the alternative study name by using the F part of the data base
     * to guess it
     *
     * @return the alternative study name
     */
    public String getComp1Name() {
        return AppUtils.guessStudyNameFromGroup(getDV2Group());
    }

    /**
     * gets the alternative study name by using the F part of the data base
     * to guess it
     *
     * @return the alternative study name
     */
    public String getComp2Name() {
        return AppUtils.guessStudyNameFromGroup(getDV3Group());
    }

    /**
     * gets the alternative study name by using the F part of the data base
     * to guess it
     *
     * @return the alternative study name
     */
    public String getComp3Name() {
        return AppUtils.guessStudyNameFromGroup(getDV4Group());
    }


    /**
     * Updates the boolean the says if the current dts list has been modified
     */
    public void setDTSMod(boolean dtsmod) {
        _dtsmod = dtsmod;
    }

    /**
     * Gets the boolean the says if the current dts list has been modified
     */
    public boolean getDTSMod() {
        return _dtsmod;
    }

    // private class variables
    // transient ( not saved )
    private transient Group _svg, _dvg, _sv2g, _dv2g, _sv3g, _dv3g, _sv4g, _dv4g, _svgbase, _dvgbase;
    // non - transient saved
    public Hashtable _dtsList, _mtsList, _dvList, _svList;
    private String _svf, _dvf, _sv2f, _dv2f, _sv3f, _dv3f, _sv4f, _dv4f;
    boolean _dtsmod;
    private TimeWindow _tw;
    String[] _bsv, _bdv = null;
}
