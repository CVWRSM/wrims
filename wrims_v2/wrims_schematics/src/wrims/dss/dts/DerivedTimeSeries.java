package wrims.dss.dts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import wrims.schematic.FilterPanel;

/**
 * 
 * @author Clay Booher after Nicky Sandhu
 * 
 */
public class DerivedTimeSeries {
	public static boolean DEBUG = false;
	public static int ROW_SIZE = 4;
	public static String DEFAULT_B_PART = "";
	public static String DEFAULT_C_PART = "";
	boolean recalculate = true;
	// CB private Vector _opIds, _dtsNames, _varTypes, _bparts, _cparts;
	private Vector<String[]> _rows;
	private String _name;
	// private boolean _nameSetExplicitly;
	// private transient DataSet[] _dataSet;
	private boolean _modified;
	// Not private variable
	// DtsTreeModel _dtm = DtsTreePanel.getCurrentModel();
	int[] dss = { 0, 0, 0, 0 };

	// private Preferences _userPrefs; //CB added
	// _userPrefs = Preferences.userNodeForPackage(getClass()); // CB added
	private static Preferences _userPrefs; // =
											// Preferences.userNodeForPackage(getClass());

	/**
	 * create an empty derived time series
	 */
	public DerivedTimeSeries() {
		// _opIds = new Vector();
		// _dtsNames = new Vector();
		// _varTypes = new Vector();
		// _bparts = new Vector();
		// _cparts = new Vector();
		// _name = "Untitled " + new Random().nextInt();
		this("Untitled " + new Random().nextInt());
	}

	/**
	 *
	 */
	public DerivedTimeSeries(DerivedTimeSeries dts) {
		DerivedTimeSeries ndts = new DerivedTimeSeries();
		ndts._name = new String(dts.getName());
		if (dts._rows instanceof Vector)
			ndts._rows = (Vector<String[]>) dts._rows.clone();
		// ndts._opIds = (Vector) dts._opIds.clone();
		// ndts._dtsNames = (Vector) dts._dtsNames.clone();
		// ndts._varTypes = (Vector) dts._varTypes.clone();
		// ndts._bparts = (Vector) dts._bparts.clone();
		// ndts._cparts = (Vector) dts._cparts.clone();
		_userPrefs = Preferences.userNodeForPackage(getClass()); // CB added

	}

	public DerivedTimeSeries(String name) {
		// _opIds = new Vector();
		// _dtsNames = new Vector();
		// _varTypes = new Vector();
		// _bparts = new Vector();
		// _cparts = new Vector();
		_rows = new Vector<String[]>(4);
		// addRow();
		_name = name;
		_userPrefs = Preferences.userNodeForPackage(getClass()); // CB added
		load(FilterPanel.DASHBOARD_VARIABLE + name);
	}

	public DerivedTimeSeries(DerivedTimeSeries dts, String name) {
		this(dts);
		// DerivedTimeSeries ndts = new DerivedTimeSeries();
		// ndts._name = new String(name);
		// ndts._opIds = (Vector) dts._opIds.clone();
		// ndts._dtsNames = (Vector) dts._dtsNames.clone();
		// ndts._varTypes = (Vector) dts._varTypes.clone();
		// ndts._bparts = (Vector) dts._bparts.clone();
		// ndts._cparts = (Vector) dts._cparts.clone();
	}

	/**
	 *
	 */
	/*
	 * public static DerivedTimeSeries load(String file) throws IOException { //
	 * InputStream is = new FileInputStream(file); _userPrefs.get(_name);
	 * DerivedTimeSeries dts = load(is); return dts; }
	 */

	public void load(String preferencesName) {
		// Vector<String> dashKeys = new Vector<String>();
		String dashKey = null;
		try {
			String[] keys = _userPrefs.keys();
			for (int i = 0; i < keys.length; ++i) {
				if (keys[i].equalsIgnoreCase(preferencesName)) {
					// dashKeys.add(keys[i]);
					dashKey = keys[i];
					break;
				}
			}

		} catch (BackingStoreException bse) {
			bse.printStackTrace();
			return;
		}
		String[] row = null;
		if (dashKey != null) {
			String value = _userPrefs.get(dashKey, "");
			String[] parts = value.split(",");
			for (int i = 0; i < parts.length; ++i) {
				if (i % ROW_SIZE == DTSTable.OPERATOR_INDEX) {
					row = new String[4];
					row[i % ROW_SIZE] = parts[i].trim();
				} else if (i % ROW_SIZE == DTSTable.DTS_NAME_INDEX) {
					row[i % ROW_SIZE] = parts[i].trim();
				} else if (i % ROW_SIZE == DTSTable.TYPE_INDEX) {
					if (parts[i].trim().length() == 1
							&& (parts[i].trim().charAt(0) != '1' || parts[i]
									.trim().charAt(0) != '2')) {
						if (Integer.parseInt(parts[i].trim()) == 1)
							row[i % ROW_SIZE] = FilterPanel.DVAR_STRING;
						else if (Integer.parseInt(parts[i].trim()) == 2)
							row[i % ROW_SIZE] = FilterPanel.SVAR_STRING;
					} else {
						row[i % ROW_SIZE] = parts[i].trim();
					}
				} else if (i % ROW_SIZE == DTSTable.B_PART_INDEX) {
					row[i % ROW_SIZE] = parts[i].trim();
					_rows.add(row);
				}
			}
		}
	}

	/**
	 * CB added.
	 */
	void writePreferences() {
		Enumeration<String[]> rows = _rows.elements();
		StringBuffer value = new StringBuffer();
		while (rows.hasMoreElements()) {
			String[] row = rows.nextElement();
			for (int i = 0; i < row.length; ++i) { // CB no checks here - leave
													// it up to user
				if (row[i] == null)
					value.append(" ");
				else
					value.append(row[i]);
				if (rows.hasMoreElements() || i != row.length - 1) {
					value.append(",");
				}
			}
		}
		// System.out.println(value.toString());
		_userPrefs
				.put(FilterPanel.DASHBOARD_VARIABLE + _name, value.toString());
	}

	/**
	 *
	 */
	public void save(OutputStream os) throws IOException {
		AbstractTableModel model = new DTSTableModel(this);
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
	 * sets name of this data reference
	 */
	public void setName(String name) {
		if (name == null)
			return;
		name = name.toUpperCase().trim();
		// String oldname = _name;
		if (name.equals(_name))
			return;
		if (name.length() > 0) {
			// Project prj = AppUtils.getCurrentProject();
			DerivedTimeSeries dts2 = null;
			// if ( _name != null ) dts2=prj.getDTS(_name);
			// if ( dts2 != null ) prj.remove(dts2);
			// _nameSetExplicitly = true;
			// CB _name = name.toUpperCase().trim();
			_modified = true;
			// if ( dts2 != null )
			// prj.add(this);
			// prj.setDTSMod(true);
			// } else {
			// if ( AppUtils.nameNotTaken(name.toUpperCase()))
			// throw new RuntimeException("Invalid name");
			// else
			// throw new RuntimeException("Name already taken, try another");
		}
	}

	/**
	 * remove the i'th data reference.
	 */
	public boolean remove(int i) {
		// _opIds.removeElementAt(i);
		// _dtsNames.removeElementAt(i);
		// _varTypes.removeElementAt(i);
		// _bparts.removeElementAt(i);
		// _cparts.removeElementAt(i);
		if (checkAccessAt(i)) {
			_rows.removeElementAt(i);
			return true;
		} else
			return false;
	}

	/**
	 * expands all the vectors to the given size.
	 */
	private boolean expandTo(int targetSize) {
		_modified = true;
		// while( _dtsNames.size() - 1 < index ){
		// _opIds.addElement(new Integer(0));
		// _dtsNames.addElement("");
		// _varTypes.addElement(FilterPanel.DVAR);
		// _bparts.addElement(DEFAULT_B_PART);
		// _cparts.addElement(DEFAULT_C_PART);
		// }
		int sizeDiff = targetSize - _rows.size();
		boolean worked = false;
		for (int i = 0; i < sizeDiff; ++i) {
			addRow();
			if (!worked)
				worked = true;
		}
		return worked;
	}

	/**
	 * Add new default row.
	 */
	void addRow() {
		_rows.add(new String[ROW_SIZE]);
	}

	/**
	 * Adds argument if it qualifies.
	 * 
	 * @param row
	 */
	boolean addRow(String[] row) {
		if (row.length != ROW_SIZE) {
			JOptionPane.showMessageDialog(null,
					"Row must be sam size as number of table columns",
					"Wrong Size Row", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			_rows.add(row);
			return true;
		}
	}

	/**
	 * inserts an empty row at given index.
	 */
	public boolean insertRowAt(int i) {
		// if (i >= _dtsNames.size() || i < 0)
		// return;
		// _opIds.insertElementAt(new Integer(0), i);
		// _dtsNames.insertElementAt("", i);
		// _varTypes.insertElementAt("", i);
		// _bparts.insertElementAt("", i);
		// _cparts.insertElementAt("", i);
		if (checkAccessAt(i)) {
			addRow();
			return true;
		}
		return false;
	}

	/**
	 * inserts an empty row at given index.
	 */
	public boolean insertRowAt(int i, String[] row) {
		if (checkAccessAt(i)) {
			_rows.insertElementAt(row, i);
			return true;
		}
		return false;
	}

	/**
	 * checks if access is ok.
	 */
	private boolean checkAccessAt(int i) {
		if (i >= _rows.size() || i < 0)
			return false;
		return true;
	}

	/**
	 * sets the recalculate boolean to either true or false
	 */
	public void setRecalculate(boolean recalc) {
		recalculate = recalc;
	}

	public Vector<String[]> getRowsCopy() {
		return (Vector<String[]>) _rows.clone();
	}

	/**
	 * @return the name of the DTS at the given index or "" (empty string) if it
	 *         is not a DTS.
	 */
	public String getDTSNameAt(int i) {
		if (checkAccessAt(i))
			return ((String[]) _rows.elementAt(i))[DTSTable.DTS_NAME_INDEX];
		else
			return null;
	}

	/**
	 * set the name of the DTS to be used at the given index. This will cause
	 * any set b/c parts to be ignored. If dts name is null or the empty string
	 * then it will not be used and any set b/c parts will still be used.
	 */
	public void setDTSNameAt(int i, String dtsname) {
		expandTo(i + 1);
		// String nm = dtsname.toUpperCase().trim();
		String nm = dtsname.trim();
		// if (!nm.equals("") && !nm.endsWith(".DTS"))
		// nm = new String(nm + ".DTS");
		if (nm.equalsIgnoreCase(getName()))
			return;
		_rows.get(i)[DTSTable.DTS_NAME_INDEX] = nm;
	}

	/**
	 * @return ? if they are valid else returns null.
	 */
	public String getVarTypeAt(int i) {
		if (checkAccessAt(i))
			// return (String) _varTypes.elementAt(i);
			return _rows.get(i)[2];
		else
			return null;
	}

	/**
	 * set the variable type at index to AppUtils.SVAR or .DVAR
	 */
	public void setVarTypeAt(int i, String varType) {
		expandTo(i + 1);
		// _varTypes.setElementAt(varType.toUpperCase().trim(), i);
		_rows.get(i)[2] = varType.trim();
	}

	/**
	 * @return the b part of pathname or null if invalid
	 */
	public String getBPartAt(int i) {
		if (checkAccessAt(i))
			// return (String) _bparts.elementAt(i);
			return _rows.get(i)[3];
		else
			return null;
	}

	/**
	 * sets the b part at the given index
	 */
	public void setBPartAt(int i, String bpart) {
		expandTo(i + 1);
		// _bparts.setElementAt(bpart.toUpperCase().trim(), i);
		_rows.get(i)[3] = bpart.trim();
	}

	/**
	 * @return the c part of pathname or null if invalid
	 */
	/*
	 * public String getCPartAt(int i) { if (checkAccessAt(i)) return (String)
	 * _cparts.elementAt(i); else return null; }
	 */

	/**
	 * sets the c part at the given index
	 */
	/*
	 * public void setCPartAt(int i, String cpart) { expandTo(i); //
	 * _cparts.setElementAt(cpart.toUpperCase().trim(), i); _rows.get(i)[4] =
	 * cpart.trim(); }
	 */

	/**
	 * gets the operation id for the given index
	 */
	// public int getOperationIdAt(int i) {
	public String getOperationAt(int i) {
		if (checkAccessAt(i))
			// return ((Integer) _opIds.elementAt(i)).intValue();
			return _rows.get(i)[0];
		else
			return null;
	}

	/**
	 * sets the operation id for the given index
	 */
	// CB public void setOperationIdAt(int i, int opId) {
	public void setOperationAt(int i, String op) {
		expandTo(i + 1);
		// CB _opIds.setElementAt(new Integer(opId), i);
		_rows.get(i)[0] = op;
	}

	/**
	 * returns the number of data references that are involved in the
	 * calculation of this time series
	 */
	public int getNumberOfDataReferences() {
		return _rows.size();
	}

	/**
	 * reloads data and recalculates its operations
	 */
	public void reloadData() {
		// get ready to redo calculations
		// _dataSet = null;
		// _dataSet2 = null;
		_modified = true;
	}

	// private String _svf1, _svf2, _dvf1, _dvf2;

	// private boolean _oldMode;
	// private Project _oldProject;
	// private TimeWindow _oldTimeWindow;
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
		/*
		 * if ( _dataSet == null ){ _oldProject = AppUtils.getCurrentProject();
		 * _oldTimeWindow = _oldProject.getTimeWindow(); _svf1 =
		 * _oldProject.getSVFile(); _svf2 = _oldProject.getSV2File(); _dvf1 =
		 * _oldProject.getDVFile(); _dvf2 = _oldProject.getDV2File(); _modified
		 * = false; return true; }
		 */
		if (this._modified) {
			_modified = false;
			return true;
		}
		/*
		 * if ( _oldProject != AppUtils.getCurrentProject() ){ _oldProject =
		 * AppUtils.getCurrentProject(); _oldTimeWindow =
		 * _oldProject.getTimeWindow(); _svf1 = _oldProject.getSVFile(); _svf2 =
		 * _oldProject.getSV2File(); _dvf1 = _oldProject.getDVFile(); _dvf2 =
		 * _oldProject.getDV2File(); return true; }
		 */
		// Project prj = AppUtils.getCurrentProject();
		// TimeWindow tw = prj.getTimeWindow();
		/*
		 * if ( tw != null && !tw.isSameAs(_oldTimeWindow) ){ _oldTimeWindow =
		 * prj.getTimeWindow(); return true; }
		 */
		/*
		 * if (! _svf1.equals(prj.getSVFile()) || !
		 * _svf2.equals(prj.getSV2File())|| ! _dvf1.equals(prj.getDVFile()) || !
		 * _dvf2.equals(prj.getDV2File()) ){ _svf1 = _oldProject.getSVFile();
		 * _svf2 = _oldProject.getSV2File(); _dvf1 = _oldProject.getDVFile();
		 * _dvf2 = _oldProject.getDV2File(); return true; }
		 */
		/*
		 * if (AppUtils.needsRecataloging(_dvf1) ||
		 * AppUtils.needsRecataloging(_dvf2) ||
		 * AppUtils.needsRecataloging(_svf1) ||
		 * AppUtils.needsRecataloging(_svf2)) return true;
		 */
		return false/* recalculate */;
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
	 * gets name of this data reference
	 */
	public String getName() {
		return _name;
	}

	// public Vector getBParts() {
	// return _bparts;
	// }

	// public Vector getCParts() {
	// return _cparts;
	// }

	// public Vector getOpIds() {
	// return _opIds;
	// }

	// public Vector getVarTypes() {
	// return _varTypes;
	// }

	// public Vector getDtsNames() {
	// return _dtsNames;
	// }
}
