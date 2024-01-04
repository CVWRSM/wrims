package wrims.dss.dts;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Contains all info about a DerivedTimeSeries that DSSViewer needs to know.
 * 
 * @author Clay Booher
 */
public class DTSWrapper {

	private String _dtsName;

	private DerivedTimeSeries _dts;

	private Hashtable<String, String> _varNameToPathMap;
	// /** A list of DTSWrappers in this DTS */
	// private Vector<DtsWrapper> dtsList;
	/** A list of DerivedTimeSeries in this DTS */
	// private Vector<DerivedTimeSeries> _dtsList = null;
	private Vector<DTSWrapper> _dtsList = null;

	/**
	 * @param dtsName
	 */
	public DTSWrapper(String dtsName) {
		setDTSName(dtsName);
	}

	public DTSWrapper(String dtsName, DerivedTimeSeries dts) {
		this(dtsName);
		setDerivedTimeSeries(dts);
	}

	public DTSWrapper(String dtsName, DerivedTimeSeries dts,
			Hashtable<String, String> varNameToPathMap) {
		this(dtsName, dts);
		setVariableNameToDssPathMap(varNameToPathMap);
	}

	// public DTSWrapper(String dtsName, DerivedTimeSeries dts,
	// Hashtable<String, String> varNameToPathMap, Vector<DerivedTimeSeries>
	// dstList) {
	public DTSWrapper(String dtsName, DerivedTimeSeries dts,
			Hashtable<String, String> varNameToPathMap,
			Vector<DTSWrapper> dstList) {
		this(dtsName, dts, varNameToPathMap);
		setDTSList(dstList);
	}

	public void setDTSName(String dtsName) {
		_dtsName = dtsName;
	}

	public String getDTSName() {
		return _dtsName;
	}

	public void setDerivedTimeSeries(DerivedTimeSeries dts) {
		_dts = dts;
	}

	public DerivedTimeSeries getDerivedTimeSeries() {
		return _dts;
	}

	public void setVariableNameToDssPathMap(
			Hashtable<String, String> varNameToPathMap) {
		_varNameToPathMap = varNameToPathMap;
	}

	public Hashtable<String, String> getVariableNameToDssPathMap() {
		return _varNameToPathMap;
	}

	// public void setDTSList(Vector<DerivedTimeSeries> dtsList) {
	public void setDTSList(Vector<DTSWrapper> dtsList) {
		_dtsList = dtsList;
	}

	// public Vector<DerivedTimeSeries> getDTSList() {
	public Vector<DTSWrapper> getDTSList() {
		return _dtsList;
	}
}
