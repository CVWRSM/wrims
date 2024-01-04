package wrims.schematic;

import hec.hecmath.DSS;
import hec.hecmath.DSSFile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import vista.db.dss.DSSUtil;
import vista.set.Group;
import wrims.dss.CondensedReference;
import wrims.dss.DashboardVariableWindow;
import wrims.dss.DssViewer;
import wrims.dss.dts.DTSWrapper;
import wrims.dss.dts.DerivedTimeSeries;

public class FilterPanel extends JPanel {
	public static final String DASHBOARD_VARIABLE = "Dash"; // CB added for
	// preferences

	/**
	 * CB added. Names order and quantity must correspond to
	 * DASHBOARD_BUILT_IN_VALUES
	 */
	public static final String[] DASHBOARD_BUILT_IN_NAMES = {
			"SWP Table A Delivery from Delta",
			"SWP Table A Delivery North of Delta", "SWP Article 21 Delivery",
			"CVP Delivery North of Delta", "CVP Delivery South of Delta",
			"Banks Pumping", "Tracy Pumping", "Delta outflow",
			"Sacramento River at Hood", "Total Sacramento Basin Seepage Gain",
			"Shasta Storage", "Oroville Storage", "Folsom Storage",
			"Trinity Storage", "Trinity Export to Sacramento R." };

	/**
	 * CB added. Values order and quantity must correspond to
	 * DASHBOARD_BUILT_IN_NAMES
	 */
	public static final String[] DASHBOARD_BUILT_IN_VALUES = {
		    "+, , 1, swp_TA_total, +, , 1, swp_CO_total, -, , 1, swp_TA_feath, -, , 1, swp_CO_feath",
			"+, , 1, swp_TA_feath, +, , 1, swp_CO_feath",
			"+, , 1, del_swp_pin", "+, , 1, del_cvp_total_n",
			"+, , 1, del_cvp_total_s, +, , 1, d855", "+, , 1, d419",
			"+, , 1, d418", "+, , 1, C_SAC000_MIN, + , , 1, C_SAC000_ADD", "+, , 1, c_sac041",
			"+, , 1, sg_total", "+, , 1, S_SHSTA", "+, , 1, S_OROVL",
			"+, , 1, S_FOLSM", "+, , 1, S_TRNTY", "+, , 1, D_LWSTN_CCT011" };

	private Hashtable<String, String> _dashboardBuiltIns = new Hashtable<String, String>();

	public static final int DVAR = 1;
	public static final int SVAR = 2;
	public static final String DVAR_STRING = "Dvar";
	public static final String SVAR_STRING = "Svar";
	public static boolean DEBUG = false;
	public final String DEFAULT_TIME_WINDOW = "31Oct1921 2400 30Sep2009 2400"; // CB
	// added
	String _timeWindow = DEFAULT_TIME_WINDOW;
	String[][] _SVpathnameParts;
	String[][] _DVpathnameParts;
	Vector _SVcondensedList = null;
	Vector _DVcondensedList = null;
	private JTextField[] _pathText;
	// private JComboBox _varTypeBox;
	// private JButton _retrieveBtn = null;
	private JButton _monthlyTableBtn = null;
	private JButton _filterBtn = null;
	private JButton _tableBtn = null;
	private JButton _plotBtn = null;
	private JButton _exceedBtn = null;
	private JButton _annualTotBtn = null;
	private JButton _annualTotExceedBtn = null;
	private JButton _monthlyAvgBtn = null; // CB added
	JTable _table = null;
	private PathnameTable _dvarTable;
	private PathnameTable _svarTable;
	private PathnameTable[] _dvarTableComp;

	private PathnameTable[] _svarTableComp;

	//
	
	// CB private JPanel _upperPanel;
	private JList _monthlist;
	private JTabbedPane _resultsPane;

	// private boolean isVariableDataLoaded = false; //CB added

	MainPanel _mainPanel;
	DssAppAction _aa;
	HashMap<Integer, Object> _fparts = new HashMap<Integer, Object>();

	String[] DVkeys = { "BaseDV", "Comp1DV", "Comp2DV", "Comp3DV" }; // CB moved
	// here;
	// later
	// replaced
	// most
	// uses
	// with
	// integer
	// constants
	String[] SVkeys = { "BaseSV", "Comp1SV", "Comp2SV", "Comp3SV" }; // CB moved
	// here;
	// later
	// replaced
	// most
	// uses
	// with
	// integer
	// constants

	// CB TODO: Replace DVkeys and SVkeys with integer constants!!!!!!!!!!!!!!!!
	public static final int BASE_DV_FILE_INT = 0;
	public static final int COMP1_DV_FILE_INT = 4;
	public static final int COMP2_DV_FILE_INT = 8;
	public static final int COMP3_DV_FILE_INT = 12;
	public static final int BASE_SV_FILE_INT = 1;
	public static final int COMP1_SV_FILE_INT = 5;
	public static final int COMP2_SV_FILE_INT = 9;
	public static final int COMP3_SV_FILE_INT = 13;

	public static final int[] DVFileKeys = { BASE_DV_FILE_INT,
			COMP1_DV_FILE_INT, COMP2_DV_FILE_INT, COMP3_DV_FILE_INT };

	public static final int[] SVFileKeys = { BASE_SV_FILE_INT,
			COMP1_SV_FILE_INT, COMP2_SV_FILE_INT, COMP3_SV_FILE_INT };

	static Hashtable<String, Integer> stringToIntKeyMap = new Hashtable<String, Integer>();

	private DssViewer _periodValueViewer; // CB added to avoid time to recreate
	// over and over

	private Hashtable<String, DerivedTimeSeries> _commonToDerivedTimeseriesMap; // CB
	// added

	private JTable _dashboard;
	private int _iCurrentRow = -1;//added by Liheng Zhong to clear selection

	private Preferences _userPrefs; // CB added

	// public FilterPanel(MainPanel mp) {
	public FilterPanel(DssAppAction aa, MainPanel mp) {
		_aa = aa;
		_mainPanel = mp;
		_userPrefs = Preferences.userNodeForPackage(DerivedTimeSeries.class); // CB
																				// added
		loadDashboardHashtable();
		setLayout(new BorderLayout());
		add(createUpperPanel(), BorderLayout.NORTH);
		add(createLowerPanel(), BorderLayout.CENTER); // CB replaced code

		for (int i = 0; i < DVkeys.length; ++i) {
			stringToIntKeyMap.put(DVkeys[i], DVFileKeys[i]);
		}
		for (int i = 0; i < SVkeys.length; ++i) {
			stringToIntKeyMap.put(SVkeys[i], SVFileKeys[i]);
		}
	}

	/**
	 * CB added.
	 */
	private void loadDashboardHashtable() {
		if (DASHBOARD_BUILT_IN_NAMES.length != DASHBOARD_BUILT_IN_VALUES.length) {
			JOptionPane
					.showMessageDialog(
							this,
							"Clay needs to fix the dashboard built-in names and/or values",
							"Dashboard names and values have different count!",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (int i = 0; i < DASHBOARD_BUILT_IN_NAMES.length; ++i) {
			// System.out.println(DASHBOARD_VARIABLE +
			// DASHBOARD_BUILT_IN_NAMES[i]);
			// System.out.println(DASHBOARD_BUILT_IN_VALUES[i]);
			_dashboardBuiltIns.put(DASHBOARD_BUILT_IN_NAMES[i],
					DASHBOARD_BUILT_IN_VALUES[i]);
		}
	}

	/**
	 * CB added.
	 */
	private void loadDashboardUserPreferences() {
		for (int i = 0; i < DASHBOARD_BUILT_IN_NAMES.length; ++i) {
			// System.out.println(DASHBOARD_BUILT_IN_NAMES[i]);
			// System.out.println(_dashboardBuiltIns.get(DASHBOARD_BUILT_IN_NAMES[i]));
			_userPrefs.put(DASHBOARD_VARIABLE + DASHBOARD_BUILT_IN_NAMES[i],
					_dashboardBuiltIns.get(DASHBOARD_BUILT_IN_NAMES[i])
							.toUpperCase());
		}
		// _userPrefs.put(DASHBOARD_VARIABLE + "Shasta", "+, , 1, S_SHSTA");
		// _userPrefs.put(DASHBOARD_VARIABLE + "Oroville", "+, , 1, S_OROVL");
		// _userPrefs.put(DASHBOARD_VARIABLE + "Folsom", "+, , 1, S_FOLSM");
	}

	/**
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JCheckBox) {
			String name = ((JCheckBox) e.getSource()).getText();
			// TODO: figure out what I was planning or trying to do here
		}
	}

	/**
	 * CB added
	 */
	AppAction DeleteDTSAction = new AppAction("Delete DTS",
			FilterPanel.this._dashboard,
			"Display monthly averages plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			int[] selectedRows = FilterPanel.this._dashboard.getSelectedRows();
			if (selectedRows.length > 0) {
				int result = JOptionPane.showConfirmDialog(FilterPanel.this,
						"Are you sure?", "Confirm Derived Timeseries Deletion",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					for (int row = 0; row < selectedRows.length; ++row) {
						String dtsName = (String) FilterPanel.this._dashboard
								.getValueAt(selectedRows[row], 0);
						_userPrefs.remove("Dash" + dtsName);
					}
					((DefaultTableModel) FilterPanel.this._dashboard.getModel())
							.fireTableDataChanged();
					AppAction.updateAllActions();
				}
			}
		}

		public boolean canAct() {
			return super.canAct();
		}
	};

	/**
	 * CB added. Retrieves the date range from the D-parts of the two base dss
	 * files (dv and sv).
	 */
	String[] retrieveDateRangeFromBase() {
		String[] rangePair = new String[2];
		Hashtable<String, Integer> months = new Hashtable<String, Integer>();
		months.put("JAN", 1);
		months.put("FEB", 2);
		months.put("MAR", 3);
		months.put("APR", 4);
		months.put("MAY", 5);
		months.put("JUN", 6);
		months.put("JUL", 7);
		months.put("AUG", 8);
		months.put("SEP", 9);
		months.put("OCT", 10);
		months.put("NOV", 11);
		months.put("DEC", 12);
		Vector<?> pathNames = null;
		if (_baseDVfn != null) {
			DSSFile dvFile = DSS.open(_baseDVfn);
			dvFile.close();
			pathNames = dvFile.getCatalogedPathnames();
		}
		if (_baseSVfn != null) {
			DSSFile svFile = DSS.open(_baseSVfn);
			svFile.close();
			pathNames.addAll(svFile.getCatalogedPathnames());
		}
		if (pathNames != null) {
			// // int minday = 31;
			int maxday = 1;
			String minMonth = "DEC";
			String maxMonth = "JAN";
			int minYear = Integer.MAX_VALUE;
			int maxYear = Integer.MIN_VALUE;
			Enumeration<?> paths = pathNames.elements();
			// CB for now, this only works for monthly data. TO DO: add daily
			// (and other?) if desired.
			while (paths.hasMoreElements()) {
				String[] parts = ((String) paths.nextElement()).split("/"); // CB
				// FIRST
				// PATH
				// ONLY
				// -
				// WON"T
				// WORK
				// IN
				// FOR
				// WRIMS
				// 2
				String year = parts[4].substring(5, parts[4].length());
				String day = parts[4].substring(0, 2);
				String month = parts[4].substring(2, 5);
				int intYear;
				if ((intYear = Integer.parseInt(year)) < minYear) {
					minYear = intYear;
					minMonth = month;
				} else if (intYear > maxYear) {
					maxYear = intYear;
					maxMonth = month;
				} else if (intYear == minYear) {
					if (!minMonth.equals("JAN")) {
						if ((Integer) months.get(month) < (Integer) months
								.get(minMonth)) {
							minMonth = month;
						}
					}
				} else if (intYear == maxYear) {
					if (!maxMonth.equals("DEC")) {
						if ((Integer) months.get(month) > (Integer) months
								.get(maxMonth)) {
							maxMonth = month;
						}
					}
				}
			}
			rangePair[0] = minMonth + minYear;
			rangePair[1] = maxMonth + maxYear;
			return rangePair;
		} else
			return null;
	}

	/**
	 * CB added. Retrieves the date range from _timeWindow. This is much faster
	 * than retrieveDateRangeFromBase().
	 * 
	 * @return
	 */
	String[] retrieveDateRangeFromTimeWindow() {
		// time window format: "31Oct1921 2400 30Sep2003 2400"
		String[] rangePair = new String[2];
		String[] split = _timeWindow.split(" +");
		rangePair[0] = split[0].substring(2);
		rangePair[1] = split[2].substring(2);
		return rangePair;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public String[] getDashboardDTSListAsArray() {
		String[] dtsList = null;
		int validSize = 0;
		if (_dashboard.getRowCount() > 0
				&& ((String) _dashboard.getValueAt(
						_dashboard.getRowCount() - 1, 0)).trim().equals(""))
			validSize = _dashboard.getRowCount();
		else
			validSize = _dashboard.getRowCount() + 1;
		dtsList = new String[validSize];
		dtsList[0] = " ";
		for (int row = 1; row < dtsList.length; ++row) {
			dtsList[row] = (String) _dashboard.getModel()
					.getValueAt(row - 1, 0);
		}
		return dtsList;
	}

	// public void setFpart(String dssfn, String type) {
	public void setFpart(String dssfn, int type) {

		// "Comp1DV"
		Group group = DSSUtil.createGroup("local", dssfn);
		String pathName = group.getDataReference(0).getPathname().getFullPath();
		// sample the fpart
		String[] parts = pathName.split("/"); // CB FIRST
		// PATH ONLY
		// - WON"T
		// WORK IN
		// FOR WRIMS
		// 2

		if (DEBUG)
			System.out.println("dss type: " + type);
		if (DEBUG)
			System.out.println("fpart: " + parts[6]);

		_fparts.put(type, parts[6]);
	}

	public HashMap<Integer, Object> getFparts(String type) {
		HashMap<Integer, Object> fparts = new HashMap<Integer, Object>();
		// CB String[] DVkeys = {"BaseDV","Comp1DV","Comp2DV","Comp3DV"};
		// CB String[] SVkeys = {"BaseSV","Comp1SV","Comp2SV","Comp3SV"};
		// CB String[] keys = null;
		int[] keys = null;

		if (type.equals("DVAR"))
			keys = DVFileKeys;
		// CB else keys = SVkeys;
		else if (type.equals("SVAR"))
			keys = SVFileKeys; // CB added BOTH type so I altered line
		else {
			keys = new int[DVFileKeys.length + SVFileKeys.length]; // CB added
			for (int i = 0; i < DVFileKeys.length; ++i) { // CB added
				keys[i] = DVFileKeys[i];
			}
			for (int i = DVFileKeys.length; i < keys.length; ++i) { // CB added
				keys[i] = SVFileKeys[i - DVFileKeys.length];
			}
		}
		String f = null;
		// int f = -1;
		for (int j = 0; j < keys.length; j++) {
			if (!_fparts.containsKey(keys[j]))
				continue;
			f = (String) _fparts.get(keys[j]);
			if (DEBUG)
				System.out.println("getFparts() " + keys[j]);
			if (DEBUG)
				System.out.println("getFparts() " + f);

			/*
			 * if (keys[j].substring(0,4).equals("Base")) if (fparts.get("Base")
			 * == null) //CB added check for multiple fparts per key (SV and DV)
			 * fparts.put("Base", f); else { //CB added else block if
			 * (!fparts.get("Base").equals(f)) { //can only be two Fparts
			 * maximum per key, so this is OK Vector<Object> Fparts = new
			 * Vector<Object>(); Fparts.add(fparts.get("Base")); Fparts.add(f);
			 * fparts.put("Base", Fparts); } } else { if
			 * (fparts.get(keys[j].substring(4,5)) == null) //CB added check for
			 * multiple fparts per key (SV and DV)
			 * fparts.put(keys[j].substring(4,5), f); else { //CB added else
			 * block if (!fparts.get(keys[j].substring(4,5)).equals(f)) { //can
			 * only be two Fparts maximum per key, so this is OK Vector<Object>
			 * Fparts = new Vector<Object>();
			 * Fparts.add(fparts.get(keys[j].substring(4,5))); Fparts.add(f);
			 * fparts.put(keys[j].substring(4,5), Fparts); } } }
			 */
			// CB replaced above block with this one
			if (fparts.get(keys[j] / 4) == null) // CB added check for multiple
				// fparts per key (SV and
				// DV)
				fparts.put(keys[j] / 4, f);
			else { // CB added else block
				// if (!fparts.get(keys[j]/4).equals(f)) { //can only be two
				// Fparts
				// maximum per key, so this is OK
				Vector<Object> Fparts = new Vector<Object>();
				Fparts.add(fparts.get(keys[j] / 4));
				Fparts.add(f);
				fparts.put(keys[j] / 4, Fparts);
				// }
			}
		}
		return fparts;
	}

	public void newViewerFile() {
		_dvarTable.resetTable();
		_svarTable.resetTable();
		
		for(int ci=1; ci<4; ci++){
			_dvarTableComp[ci].resetTable();
			_svarTableComp[ci].resetTable();
		}
		
		_baseDVfn = null;
		_baseSVfn = null;
		updateTabs(0, 0);
		_resultsPane.setEnabledAt(0, false);
		_resultsPane.setEnabledAt(1, false);
		AppAction.updateAllActions();
	}

	// CB public void setBaseDSSName(String fn, String type) {
	public void setBaseDSSName(String fn, int dssType) { // CB
		_mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (dssType / 4 != 0)
			JOptionPane.showMessageDialog(this,
					"setBaseDSSName called with wrong dss type argument",
					"Incorrect DSS File Type setBaseDSName Arg",
					JOptionPane.ERROR_MESSAGE);
		// CB if (type.equals("BaseDV")) {
		if (dssType % 4 == 0) {
			setBaseDVFileName(fn);
			_dvarTable.catalog(fn);
			_resultsPane.setEnabledAt(0, true);
			_resultsPane.setSelectedIndex(0);
		} else {
			setBaseSVFileName(fn);
			_svarTable.catalog(fn);
			_resultsPane.setEnabledAt(1, true);
			if (!_resultsPane.isEnabledAt(0)) // CB huh? After setting it to 1!?
				_resultsPane.setSelectedIndex(1);
		}
		// refreshFilter(type);
		refreshFilter(dssType);
		AppAction.updateAllActions();
		_mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void setCompDSSName(String fn, int dssType) {
		_mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		int index = dssType/4;
		if (dssType % 4 == 0) {
			_dvarTableComp[index].catalog(fn);
		} else {
			_svarTableComp[index].catalog(fn);
		}
		_mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * CB ??????????????????????? Not finished by Tom?
	 * 
	 * @param type
	 */
	// CB public void refreshFilter(String type) {
	public void refreshFilter(int dssType) {
		for (int i = 0; i < 6; i++)
			_pathText[i].setText("");
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public JTextField getBaseDVFileField() {
		return _pathText[0];
	}

	public int getActiveTab() {
		return _resultsPane.getSelectedIndex();
	}

	public boolean isFilterActive() {
		if (getActiveTab() == 0 && _baseDVfn != null)
			return true;
		else if (getActiveTab() == 1 && _baseSVfn != null)
			return true;
		return false;

	}

	private String getCurrentType() {
		String type;
		if (getActiveTab() == 0)
			type = "DVAR";
		else
			type = "SVAR";
		return type;
	}

	private PathnameTable getActiveTable() {
		// check if any selections in active table
		PathnameTable table;
		if (getActiveTab() == 0)
			table = _dvarTable;
		else
			table = _svarTable;
		return table;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	private PathnameTable getInactiveTable() {
		// check if any selections in active table
		PathnameTable table;
		if (getActiveTab() == 0)
			table = _svarTable;
		else
			table = _dvarTable;
		return table;
	}

	public boolean rowsSelected() {
		PathnameTable table = getActiveTable();
		if (table.getTable() == null)
			return false;
		if (table.getTable().getSelectedRowCount() == 0)
			return false;
		return true;
	}

	public boolean isFilterValid() {

		// make sure modes jive
		if (_mainPanel.getMessagePanel().isCompMode()) {
			if (getActiveTab() == 0
					&& _mainPanel.getMessagePanel().isDvarCompActive())
				return true;
			if (getActiveTab() == 1
					&& _mainPanel.getMessagePanel().isSvarCompActive())
				return true;
		} else {
			if (getActiveTab() == 0
					&& _mainPanel.getMessagePanel().isDvarDiffActive())
				return true;
			if (getActiveTab() == 1
					&& _mainPanel.getMessagePanel().isSvarDiffActive())
				return true;
		}
		return false;
	}

	public boolean isMonthlyTimeStep() {
		PathnameTable table = getActiveTable();
		return table.isMonthlyTimeStep();
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public DssViewer getValueViewer() {
		return _periodValueViewer;
	}

	/**
	 * CB added.
	 * 
	 * @param studyNumber
	 * @return
	 */
	/*
	 * CB needed? public boolean loadVariableData(int studyNumber,
	 * HashMap<Integer, Object> dssFileNames, boolean selectedFiles) { if
	 * (_periodValueViewer == null || studyNumber < 0 || studyNumber > 3) return
	 * false; _periodValueViewer.loadVariableData(studyNumber, dssFileNames,
	 * selectedFiles); return true; }
	 */

	// CB void retrieve(int type, boolean exceedence, boolean annualTot) {
	public void retrieve(int type) { // CB
		DssViewer viewer = null;
		PathnameTable table = null;
		Vector<String> paths = null;
		String listOfMissingDTSVariables = null;
		try {
			PathnameTable activeTable = getActiveTable();
			Vector<String> activePaths = activeTable.getSelectedPaths(); // A -
			// F
			// parts
			// //CB
			// no
			// selected
			// paths
			// if
			// type
			// =
			// 8
			PathnameTable inactiveTable = getInactiveTable();
			Vector<String> inactivePaths = inactiveTable.getSelectedPaths(); // A
			// -
			// F
			// parts
			// //CB
			// no
			// selected
			// paths
			// if
			// type
			// =
			// 8
			int[] ignoreParts = new int[1]; // for not considering the D-part
			// when comparing paths below
			ignoreParts[0] = 4; // D = 4

			// int BpartIndex = activeTable.getColumnIndex("B part"); //CB TODO
			// add instance variable constant instead of hard code

			// CB added dashboard table - add those paths not selected in
			// pathname table
			Vector<DTSWrapper> dtsWraps = new Vector<DTSWrapper>();
			boolean dtsFoundInTable = false;
			for (int i = 0; i < _dashboard.getSelectedRowCount(); ++i) {
				DerivedTimeSeries dts = _commonToDerivedTimeseriesMap
						.get((String) _dashboard.getValueAt(_dashboard
								.getSelectedRows()[i], 0));
				Vector<String[]> rowsCopy = dts.getRowsCopy();
				Enumeration<String[]> rows = rowsCopy.elements();
				Hashtable<String, String> varNameToPathMap = new Hashtable<String, String>();
				Vector<DTSWrapper> dtsList = new Vector<DTSWrapper>();

				while (rows.hasMoreElements()) {
					String[] row = rows.nextElement();
					String bpart = row[3];
					if (bpart != null && !bpart.trim().equals("")) { // if the
						// row
						// has a
						// bpart
						int index = binarySearch(activeTable._pathnameParts,
								bpart, 1); // index of where it is OR where it
						// should be, if present!
						if (activeTable._pathnameParts[index][1].equals(bpart)) {
							dtsFoundInTable = true;
							table = activeTable;
							paths = activePaths;
						} else {
							index = binarySearch(inactiveTable._pathnameParts,
									bpart, 1);
							if (inactiveTable._pathnameParts[index][1]
									.equals(bpart)) {
								dtsFoundInTable = true;
								table = inactiveTable;
								paths = inactivePaths;
							}
						}

						if (dtsFoundInTable) {
							Enumeration<String> tablePathsEnum = paths
									.elements();
							boolean isAlreadyPresent = false;
							while (tablePathsEnum.hasMoreElements()) {
								if (table
										.comparePaths(
												tablePathsEnum.nextElement(),
												((CondensedReference) table._condensedList
														.get(index))
														.getNominalPathname(),
												ignoreParts)) {
									isAlreadyPresent = true;
									break;
								}
							}
							// CB NO causes errors sometimes when both dts and
							// var present if (!isAlreadyPresent)
							varNameToPathMap.put(bpart,
									((CondensedReference) table._condensedList
											.get(index)).getNominalPathname());
						} else {
							if (listOfMissingDTSVariables == null)
								listOfMissingDTSVariables = bpart;
							else
								listOfMissingDTSVariables += ", " + bpart;
						}
					} else {
						String dtsname = row[1];
						System.out.println(dtsname);
						// handleDTS(dtsname);
						DTSWrapper wrapper = generateDtsWrapper(_commonToDerivedTimeseriesMap
								.get(dtsname));

						dtsList.add(wrapper);
						// DTSWrapper dtsWrap = new DTSWrapper(dtsname,
						// _commonToDerivedTimeseriesMap.get(dtsname),
						// varNameToPathMap); //TODO CHECK IT!!!!!!

						// dtsWraps.add(wrapper);
						System.out.print("");
					}
				}
				DTSWrapper dtsWrap = new DTSWrapper((String) _dashboard
						.getValueAt(_dashboard.getSelectedRows()[i], 0), dts,
						varNameToPathMap, dtsList);
				dtsWraps.add(dtsWrap);
			}
			// "Base","Comp","Diff" //CB no more "Comp" mode, as "Base" mode was
			// expanded to include it
			String mode = _mainPanel.getMessagePanel().getMode();
			int annualMode = _mainPanel.getMessagePanel().getAnnualType();
			String units = _mainPanel.getMessagePanel().getUnits();

			paths = new Vector();
			paths.addAll(activePaths);
			paths.addAll(inactivePaths);

			// CB HashMap<Integer, Object> DssFiles =
			// _mainPanel.getMessagePanel().getDSSFileNames(getCurrentType());
			// //CB added, then commented. D'oh!
			HashMap<Integer, Object> DssFiles = _mainPanel.getMessagePanel()
					.getDSSFileNames("BOTH"); // CB replaced above line
			// CB HashMap DssFparts = getFparts(getCurrentType());
			HashMap DssFparts = getFparts("BOTH"); // CB replaced above line
			// TODO - replace String
			// constant with class i.v.
			// int constants!
			// CB String[] keys =
			// _mainPanel.getMessagePanel().getSelectedFilesKeys();
			int[] keys = _mainPanel.getMessagePanel().getSelectedFilesKeys();
			ArrayList monthList = getMonthList(); // CB The month numbers
			// (OCT=10) 2B displayed in
			// order
			_mainPanel
					.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			viewer = new DssViewer(mode, annualMode, _timeWindow, paths,
					dtsWraps, DssFiles, DssFparts, keys, monthList);
			// CB viewer.show(type, units, exceedence, annualTot);
			viewer.show(type, units);
			_mainPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * CB added.
	 * 
	 * @param dtsname
	 *            ???????????????????????????????????????????????????????????????
	 *            ????????????????????????????
	 * @return
	 */
	DTSWrapper generateDtsWrapper(DerivedTimeSeries dts) {
		DssViewer viewer = null;
		PathnameTable table = null;
		Vector<String> paths = null;
		String listOfMissingDTSVariables = null;
		PathnameTable activeTable = getActiveTable();
		Vector<String> activePaths = activeTable.getSelectedPaths(); // A - F
		// parts
		// //CB
		// no
		// selected
		// paths
		// if
		// type
		// = 8
		PathnameTable inactiveTable = getInactiveTable();
		Vector<String> inactivePaths = inactiveTable.getSelectedPaths(); // A -
		// F
		// parts
		// //CB
		// no
		// selected
		// paths
		// if
		// type
		// =
		// 8
		int[] ignoreParts = new int[1]; // for not considering the D-part when
		// comparing paths below
		ignoreParts[0] = 4; // D = 4
		// CB added dashboard table - add those paths not selected in pathname
		// table
		Vector<DTSWrapper> dtsWraps = new Vector<DTSWrapper>();
		boolean dtsFoundInTable = false;
		Vector<String[]> rowsCopy = dts.getRowsCopy();
		Enumeration<String[]> rows = rowsCopy.elements();
		Hashtable<String, String> varNameToPathMap = new Hashtable<String, String>();

		while (rows.hasMoreElements()) {
			String[] row = rows.nextElement();
			String bpart = row[3];
			if (!bpart.trim().equals("")) { // if the row has a bpart
				int index = binarySearch(activeTable._pathnameParts, bpart, 1); // index
				// of
				// where
				// it
				// is
				// OR
				// where
				// it
				// should
				// be,
				// if
				// present!
				if (activeTable._pathnameParts[index][1].equals(bpart)) {
					dtsFoundInTable = true;
					table = activeTable;
					paths = activePaths;
				} else {
					index = binarySearch(inactiveTable._pathnameParts, bpart, 1);
					if (inactiveTable._pathnameParts[index][1].equals(bpart)) {
						dtsFoundInTable = true;
						table = inactiveTable;
						paths = inactivePaths;
					}
				}
				if (dtsFoundInTable) {
					Enumeration<String> tablePathsEnum = paths.elements();
					boolean isAlreadyPresent = false;
					while (tablePathsEnum.hasMoreElements()) {
						if (table.comparePaths(tablePathsEnum.nextElement(),
								((CondensedReference) table._condensedList
										.get(index)).getNominalPathname(),
								ignoreParts)) {
							isAlreadyPresent = true;
							break;
						}
					}
					// NO if (!isAlreadyPresent) //CB this line does not work
					// correctly for mixed DTSs and vatriables!!!!!
					varNameToPathMap.put(bpart,
							((CondensedReference) table._condensedList
									.get(index)).getNominalPathname());

				} else {
					if (listOfMissingDTSVariables == null)
						listOfMissingDTSVariables = bpart;
					else
						listOfMissingDTSVariables += ", " + bpart;
				}
			} else {
				String dtsname = row[1];
				System.out.println(dtsname);
				// handleDTS(dtsname);
				// TODO Need loop to go through the dashboard and find matching
				// DTS name = dtsname, then load varNameToPathMap
				DTSWrapper dtsWrap = generateDtsWrapper(_commonToDerivedTimeseriesMap
						.get(dtsname));
				return dtsWrap;
			}
		}
		DTSWrapper dtsWrap = new DTSWrapper(dts.getName(), dts,
				varNameToPathMap);
		// (String)_dashboard.getValueAt(_dashboard.getSelectedRows()[i], 0),
		// dts, varNameToPathMap);
		// dtsWraps.add(dtsWrap);
		return dtsWrap;
	}

	/**
	 * CB added to get a map of names to DerivedTimeSeries for all names and
	 * DerivedTimeSeries in a DerivedTimeSeries.
	 * 
	 * @param dts
	 * @return
	 */
	/*
	 * private Hashtable<String, DerivedTimeSeries>
	 * getNamesToPathsMap(DerivedTimeSeries dts, DSSFile theFile) { //OR PASS IN
	 * VECTOR OF PATHS!!!!!!!!! Hashtable<String, DerivedTimeSeries> map = new
	 * Hashtable<String, DerivedTimeSeries>(); Vector<String[]> rows =
	 * dts.getRowsCopy(); Enumeration<String[]> rowz = rows.elements(); while
	 * (rowz.hasMoreElements()) { String[] row = rowz.nextElement(); if
	 * (row[3].trim().equals("")) { //DTS row if (!row[1].trim().equals("")) {
	 * Hashtable<String, DerivedTimeSeries> maptoo = getNamesToPathsMap(dts,
	 * theFile); if (maptoo != null) { Enumeration<String> keys = maptoo.keys();
	 * while (keys.hasMoreElements()) { String key = keys.nextElement();
	 * map.put(key, maptoo.get(key)); } } } } else { //b-part row String bpart =
	 * row[3]; //need? if (bpart == null || bpart.trim().equals("")) //need?
	 * dtsName = row[1]; //need? if need above two lines, change paths from
	 * Vector of String to Vector of Objects!!!!!!!
	 * 
	 * PathnameTable table = getActiveTable(); int index =
	 * binarySearch(table._pathnameParts, bpart, 1); String name =
	 * (String)table._condensedList.get(index); String[] parts =
	 * table._pathnameParts[index]; Vector pathNames =
	 * theFile.getCatalogedPathnames(); // map.put(name,
	 * (String)pathNames.get(index)); } } }
	 */

	/**
	 * CB added. MAY NOT
	 * NEED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	/*
	 * private Vector<String[]> getAllRows(DerivedTimeSeries dts) {
	 * Vector<String[]> rowsCopy = dts.getRowsCopy(); Enumeration<String[]> rows
	 * = rowsCopy.elements(); Vector<String[]> dtsRowsCopy = null; while
	 * (rows.hasMoreElements()) { Object row = rows.nextElement(); if (row
	 * instanceof String[]) if
	 * (!((String[])row)[DTSTable.DTS_NAME_INDEX].trim().equals("")) { //if row
	 * contains a dts dtsRowsCopy = (new
	 * DerivedTimeSeries(((String[])row)[DTSTable
	 * .DTS_NAME_INDEX].trim())).getRowsCopy(); } } if (dtsRowsCopy != null) {
	 * rowsCopy.addAll(dtsRowsCopy); } return rowsCopy; }
	 */

	/**
	 * CB added.
	 * 
	 * @param array
	 * @param key
	 * @param column
	 * @return
	 */
	public static int binarySearch(String[][] array, String key, int column) {
		return binarySearch(array, 0, array.length, key, column);
	}

	/**
	 * CB added.
	 * 
	 * @param array
	 * @param lowerbound
	 * @param upperbound
	 * @param key
	 * @param column
	 * @return
	 */
	public static int binarySearch(String[][] array, int lowerbound,
			int upperbound, String key, int column) {
		int position;
		int compare_count = 1;

		// calculate initial search position.
		position = (lowerbound + upperbound) / 2;

		while (!(array[position][column].equalsIgnoreCase(key))
				&& (lowerbound < upperbound)) {
			compare_count++;
			// if the value in the search position is greater than the key
			// change upperbound to search position minus one.
			if (array[position][column].compareTo(key) > 0) { // CB
				// compareToIgnoreCase
				// does NOT work
				// here
				upperbound = position - 1; //
			} else { // change lowerbound to search position plus one.
				lowerbound = position + 1;
			}
			position = (lowerbound + upperbound) / 2;
		}
		if (lowerbound <= upperbound) {
			// System.out.println(key + " was found in array subscript " +
			// position);
		} else {
			// System.out.println(key + "  not found by binary search");
		}
		return position;
	}

	/**
	 * CB added for updating period value boxes.
	 * 
	 * @param type
	 * @param names
	 */
	public Hashtable<String, String>[] retrieve(int type, String periodDate,
			Hashtable<String, Object> names) {
		if (type != DssViewer.VALUES)
			return null;
		try {
			String units = _mainPanel.getMessagePanel().getUnits();
			if (names != null) {
				Vector<String> allPaths = new Vector<String>();
				if (_dvarTable != null && _dvarTable.getTableModel() != null) {
					PathnameTableModel dvarTMclone = (PathnameTableModel) _dvarTable
							.getTableModel().clone();
					dvarTMclone.showAllRows();
					for (int row = 0; row < dvarTMclone.getRowCount(); ++row)
						if (names.get(dvarTMclone.getBpart(row)) != null)
							allPaths.add(dvarTMclone.getPlotPath(row));
				}
				if (_svarTable != null && _svarTable.getTableModel() != null) {
					PathnameTableModel svarTMclone = (PathnameTableModel) _svarTable
							.getTableModel().clone();
					svarTMclone.showAllRows();
					for (int row = 0; row < svarTMclone.getRowCount(); ++row)
						if (names.get(svarTMclone.getBpart(row)) != null)
							allPaths.add(svarTMclone.getPlotPath(row));
				}
				HashMap dssFiles = _mainPanel.getMessagePanel()
						.getDSSFileNames("BOTH");
				HashMap dssFparts = getFparts("BOTH");
				int[] keys = _mainPanel.getMessagePanel()
						.getSelectedFilesKeys();

				_mainPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// "base mode unless someone wants diff to be added, null
				// TimeWindow, Paths, and Month List
				if (_periodValueViewer == null) { // CB had to add instance
					// variable because we
					// cannot afford the time to
					// recreate over and over
					_periodValueViewer = new DssViewer("Base", _mainPanel
							.getMessagePanel().getAnnualType(),
							DEFAULT_TIME_WINDOW, allPaths, dssFiles, dssFparts,
							keys, null, names);
				}
			}
			// Hashtable<String, Double>[] values =
			// _periodValueViewer.show(units, periodDate);
			Hashtable<String, String>[] values = _periodValueViewer.show(units,
					periodDate);
			_mainPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return values;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * CB added.
	 * 
	 * @param names
	 */
	public boolean loadAllVariableData(Hashtable<String, Object> names,
			boolean selectedFiles) {
		return loadAllVariableData(names, -1, selectedFiles, null);
	}

	/**
	 * CB added.
	 * 
	 * @param dssType
	 */
	public boolean loadAllVariableData(int dssType, boolean selectedFiles) {
		return loadAllVariableData(null, dssType, selectedFiles, null);
	}

	/**
	 * CB added.
	 * 
	 * @param names
	 * @param dssType
	 */
	public boolean loadAllVariableData(Hashtable<String, Object> names,
			int dssType, boolean selectedFiles, ProgressMonitor monitor) {
		if (names == null) {
			return false;
		}
		_mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (dssType > 1 && !_mainPanel.getMessagePanel().isSvarBaseActive()
				&& !_mainPanel.getMessagePanel().isDvarBaseActive()) {
			JOptionPane.showMessageDialog(this,
					"Base File(s) Must Be Opened First",
					"Please open on or both of the base files first",
					JOptionPane.ERROR_MESSAGE);
		}
		try {
			HashMap<Integer, Object> dssFiles = _mainPanel.getMessagePanel()
					.getDSSFileNames("BOTH");
			// CB added unselectedDssFiles for loading rest of data for the
			// monthly value boxes in a separate thread
			HashMap<Integer, Object> unselectedDssFiles = _mainPanel
					.getMessagePanel().getUnselectedDSSFileNames("BOTH");
			HashMap<Integer, Object> dssFparts = getFparts("BOTH");
			int[] keys = _mainPanel.getMessagePanel().getSelectedFilesKeys();

			Vector<String> allPaths = null; // = new Vector<String>();
			boolean isNeedToCreate = false;
			if (_periodValueViewer != null){
				_periodValueViewer.setMode(_mainPanel.getMessagePanel().isCompMode() ? "Comp" : "Diff");
			}
			if (dssType >= -1 && dssType <= 1) {
				if (selectedFiles) {
					allPaths = new Vector<String>();
					if (dssType == 1
							&& _mainPanel.getMessagePanel().isDvarBaseActive())
						isNeedToCreate = false;
					// else { //TODO have separate DV and SV paths Vectors
					if (_dvarTable != null
							&& _dvarTable.getTableModel() != null) {
						 List<String> pathsForNames = _dvarTable
							.getTableModel().getPathsForNames(names);
						 for(int ci = 1; ci < _dvarTableComp.length; ci++){
							 pathsForNames.addAll(_dvarTableComp[ci].getTableModel().getPathsForNames(names));
						 }
						 allPaths.addAll(pathsForNames);
					}
					
					// }
					if (dssType == 0
							&& _mainPanel.getMessagePanel().isSvarBaseActive())
						isNeedToCreate = false;
					// else {
					if (_svarTable != null
							&& _svarTable.getTableModel() != null) {
						List<String> pathsForNames = _svarTable.getTableModel().getPathsForNames(names);
						 for(int ci = 1; ci < _svarTableComp.length; ci++){
							 pathsForNames.addAll(_svarTableComp[ci].getTableModel().getPathsForNames(names));
						 }
						allPaths.addAll(pathsForNames);
					}
					// }
				} else {
					isNeedToCreate = false;
				}
			} else {
				isNeedToCreate = false;
			}
			if (_periodValueViewer == null || isNeedToCreate) {
				_periodValueViewer = new DssViewer("Base", _mainPanel
						.getMessagePanel().getAnnualType(),
						DEFAULT_TIME_WINDOW, allPaths, dssFiles, dssFparts,
						keys, null, names);
				_periodValueViewer.setMode(_mainPanel.getMessagePanel().isCompMode() ? "Comp" : "Diff");
			} else {
				if (allPaths != null){
					_periodValueViewer.setPaths(allPaths);
					_periodValueViewer.setVariables(names);
				}
				_periodValueViewer.setDssFiles(dssFiles);
				_periodValueViewer.setFparts(dssFparts);
			}
			_periodValueViewer.setUncheckedDssFiles(unselectedDssFiles);
			String units = _mainPanel.getMessagePanel().getUnits();
			boolean wasSuccessful = _periodValueViewer.loadVariableData(units,
					dssType, selectedFiles, monitor);
			_mainPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return wasSuccessful;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * CB added to retrieve data for variable not currently shown (not filtered)
	 * in either table.
	 * 
	 * @param what
	 * @param exceedence
	 * @param annualTot
	 * @param names
	 */
	public void retrieve(int type, boolean exceedence, boolean annualTot,
			Vector names) {
		DssViewer viewer = null;
		try {
			// PathnameTable table = getActiveTable();
			Vector<String> allPaths = new Vector<String>();
			String varType = "";
			if (_dvarTable != null && _dvarTable.getTableModel() != null) {
				PathnameTableModel dvarTMclone = (PathnameTableModel) _dvarTable
						.getTableModel().clone();
				dvarTMclone.showAllRows();
				Vector<String> dvarPaths = dvarTMclone.getMatchingPaths(names); // A
				// -
				// F
				// parts
				if (dvarPaths.size() > 0) {
					allPaths.addAll(dvarPaths);
					varType = "DVAR";
				}
			}

			if (_svarTable != null && _svarTable.getTableModel() != null) {
				PathnameTableModel svarTMclone = (PathnameTableModel) _svarTable
						.getTableModel().clone();
				svarTMclone.showAllRows();
				Vector svarPaths = svarTMclone.getMatchingPaths(names); // A - F
				// parts
				if (svarPaths.size() > 0) {
					allPaths.addAll(svarPaths);
					if (varType.equals("DVAR"))
						varType = "BOTH"; // CB added "BOTH" option
					else
						varType = "SVAR";
				}
			}
			if (allPaths.size() == 0) {
				if ((_dvarTable == null || _dvarTable.getTableModel() == null)
						&& (_svarTable == null || _svarTable.getTableModel() == null)) {
					// Should NOT be able to get here!
					JOptionPane.showMessageDialog(this,
							"Dvar and svar tables not loaded",
							"Match not found", JOptionPane.WARNING_MESSAGE);
				} else if (_dvarTable == null
						|| _dvarTable.getTableModel() == null) {
					JOptionPane.showMessageDialog(this,
							"Dvar table not loaded", "Match not found",
							JOptionPane.WARNING_MESSAGE);
				} else if (_svarTable == null
						|| _svarTable.getTableModel() == null) {
					JOptionPane.showMessageDialog(this,
							"Svar table not loaded", "Match not found",
							JOptionPane.WARNING_MESSAGE);
				}
				return;
			}
			String mode = _mainPanel.getMessagePanel().getMode();
			int annualMode = _mainPanel.getMessagePanel().getAnnualType(); // CB
			// added
			String units = _mainPanel.getMessagePanel().getUnits();
			// ////////////////////////////////////////////////////////////////////////////////////
			// CB HashMap<String, Object> DssFiles =
			// _mainPanel.getMessagePanel().getDSSFileNames(varType);
			HashMap<Integer, Object> DssFiles = _mainPanel.getMessagePanel()
					.getDSSFileNames(varType);
			// CB TO DO: add the other varType here so dvar AND svar data can be
			// displayed together?????
			HashMap DssFparts = getFparts(varType);
			// CB String[] keys =
			// _mainPanel.getMessagePanel().getSelectedFilesKeys(); //CB added
			int[] keys = _mainPanel.getMessagePanel().getSelectedFilesKeys(); // CB
			// added
			ArrayList monthList = getMonthList(); // CB The month numbers (OCT =
			// 10) to be displayed (in
			// order)
			_mainPanel
					.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			viewer = new DssViewer(mode, annualMode, _timeWindow, allPaths,
					DssFiles, DssFparts, keys, monthList); // CB added
			// annualMode and
			// keys.
			viewer.show(type, units);
			_mainPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<Integer> getMonthList() {
		ArrayList<Integer> l = new ArrayList<Integer>();
		int indices[] = _monthlist.getSelectedIndices();
		int i2m[] = { 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		for (int i = 0; i < indices.length; i++) {
			l.add(i2m[indices[i]]);
		}
		return l;
	}

	/**
	 * CB NO - we need to allow custom user entry (the JComboBox is editable)
	 * public void setTimeWindow(String tw) { if (tw.equals(
	 * "OCT1921 - SEP2003")) _timeWindow = "31Oct1921 2400 30Sep2003 2400"; else
	 * if (tw.equals( "OCT1921 - SEP1994")) _timeWindow =
	 * "31Oct1921 2400 30Sep1994 2400"; else if (tw.equals(
	 * "MAY1928 - OCT1934")) _timeWindow = "31May1928 2400 30Sep1934 2400"; else
	 * if (tw.equals( "JUN1986 - SEP1992")) _timeWindow =
	 * "30Jun1986 2400 30Sep1992 2400"; else if (tw.equals(
	 * "OCT1975 - SEP1977")) _timeWindow = "31Oct1975 2400 30Sep1977 2400"; else
	 * if (tw.equals( "OCT1983 - SEP1993")) _timeWindow =
	 * "31Oct1983 2400 30Sep1993 2400"; else _timeWindow =
	 * "31Oct1921 2400 30Sep2003 2400"; }
	 */

	/**
	 * CB replaced the old one. tw time window in the format like OCT1921 -
	 * SEP2003.
	 */
	public boolean setTimeWindow(String tw) {
		String[] split = tw.split(" +");
		String startMonth = split[0].substring(0, 3);
		String endMonth = split[2].substring(0, 3);
		String startYear = split[0].substring(3);
		String endYear = split[2].substring(3);
		int startMonthIndex = -1;
		int endMonthIndex = -1;
		for (int i = 0; i < DssViewer.months.length; ++i) {
			if (DssViewer.months[i].equalsIgnoreCase(startMonth))
				startMonthIndex = i;
			if (DssViewer.months[i].equalsIgnoreCase(endMonth))
				endMonthIndex = i;
		}
		try {
			int sYear = Integer.parseInt(startYear);
			int eYear = Integer.parseInt(endYear);
			int daysInStartMonth = SchematicUtils.daysInMonth(startMonthIndex,
					sYear);
			int daysInEndMonth = SchematicUtils.daysInMonth(endMonthIndex,
					eYear);
			_timeWindow = daysInStartMonth + DssViewer.months[startMonthIndex]
					+ startYear + " 2400 " + daysInEndMonth
					+ DssViewer.months[endMonthIndex] + endYear + " 2400";
			return true;
		} catch (NumberFormatException nfe) {
			_timeWindow = "31Oct1921 2400 30Sep2009 2400"; // defualt full time
			// window
			return false;
		}
	}

	String _baseDVfn = null;

	public void setBaseDVFileName(String fn) {
		_baseDVfn = fn;
	}

	String _baseSVfn = null;

	public void setBaseSVFileName(String fn) {
		_baseSVfn = fn;
	}

	private JTabbedPane createResultsPane() {

		JTabbedPane tabbedPane = new JTabbedPane();
		// DVAR
		_dvarTable = new PathnameTable();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		tabbedPane.addTab("DVAR", _dvarTable);

		// SVAR
		_svarTable = new PathnameTable();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		tabbedPane.addTab("SVAR", _svarTable);
		
		_dvarTableComp = new PathnameTable[4];
		_svarTableComp = new PathnameTable[4];
		for(int ci=1; ci < 4; ci++){
			_dvarTableComp[ci] = new PathnameTable();
			_svarTableComp[ci] = new PathnameTable();
		}

		_resultsPane = tabbedPane;
		_resultsPane.setEnabledAt(0, false);
		_resultsPane.setEnabledAt(1, false);
		return tabbedPane;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	private JTable createDashBoardArea() {
		// JPanel panel = new JPanel();
		// panel.setLayout(new BorderLayout());
		_commonToDerivedTimeseriesMap = new Hashtable<String, DerivedTimeSeries>();
		JTable dashboardArea = new JTable();
		dashboardArea.setShowVerticalLines(false);
		dashboardArea.setShowHorizontalLines(false);
		Vector<Vector<String>> data = new Vector<Vector<String>>(1); // for
		// dashboard
		// table
		// If no preferences yet, add must haves
		// 2nd String is like "operator type varname [operator type name] etc.
		boolean isDashboardPrefsEmpty = true;
		Vector<String> dashKeys = new Vector<String>();

		// _userPrefs.remove("DashShasta");
		// temp if (isDashboardPrefsEmpty) {
		loadDashboardUserPreferences();
		// temp }
		try {
			String[] keys = _userPrefs.keys();
			for (int i = 0; i < keys.length; ++i) {
				if (keys[i].startsWith(DASHBOARD_VARIABLE)) {
					isDashboardPrefsEmpty = false;
					dashKeys.add(keys[i]);
				}
			}
		} catch (BackingStoreException bse) {
			bse.printStackTrace();
		}
		Vector<String> row = null;
		Enumeration<String> dashkeyz = dashKeys.elements();
		while (dashkeyz.hasMoreElements()) {
			row = new Vector<String>(1);
			String[] keysSplit = dashkeyz.nextElement().split(
					DASHBOARD_VARIABLE);
			for (int i = 0; i < keysSplit.length; ++i) {
				if (!keysSplit[i].trim().equals("")) {
					row.add(keysSplit[i]);
					_commonToDerivedTimeseriesMap.put(keysSplit[i],
							new DerivedTimeSeries(keysSplit[i]));
				}
			}
			data.add(row);
		}
		row = new Vector<String>(1);
		row.add("");
		data.add(row);
		Vector<String> columnNames = new Vector<String>(1);
		columnNames.add("Derived Timeseries");
		dashboardArea.setModel(new DefaultTableModel(data, columnNames));
		// dashboardArea.getColumn(dashboardArea.getColumnName(0)).setCellEditor(null);
		// panel.add(dashboardArea, BorderLayout.NORTH);
		// panel.add(new JLabel("                           "),
		// BorderLayout.CENTER); // NO EFFECT!!!!!
		return dashboardArea;
	}

	private String getPathSpec(boolean useRegex) {
		String pathSpec = "";

		String glob = "*";
		if (useRegex)
			glob = ".*";

		for (int i = 0; i < 6; i++) {
			String txt = _pathText[i].getText().trim().toUpperCase();
			if (txt.length() > 0)
				pathSpec = pathSpec + "/" + txt;
			else
				pathSpec = pathSpec + "/" + glob;
		}
		pathSpec = pathSpec + "/";
		return pathSpec;
	}

	void updateTabs(int dv_rows, int sv_rows) {
		// put # of rows in tab name
		if (_dvarTable.getTable() != null)
			_resultsPane.setTitleAt(0, "DVAR " + _dvarTable.getRowCount());
		if (_svarTable.getTable() != null)
			_resultsPane.setTitleAt(1, "SVAR " + _svarTable.getRowCount());

		// bring forward results
		if (dv_rows > 0 && getActiveTab() == 0)
			return;
		if (sv_rows > 0 && getActiveTab() == 1)
			return;

		if (dv_rows > 0 && getActiveTab() == 1 && sv_rows == 0)
			_resultsPane.setSelectedIndex(0);
		if (sv_rows > 0 && getActiveTab() == 0 && dv_rows == 0)
			_resultsPane.setSelectedIndex(1);

	}

	public void filter() { // CB made public for Schematic to access
		// boolean useRegex = true;
		boolean useRegex = false;

		// PathnameTable table = getActiveTable();
		// table.showFilteredRows(getPathSpec(useRegex), useRegex);

		int dv_rows = _dvarTable.showFilteredRows(getPathSpec(useRegex),
				useRegex);
		int sv_rows = _svarTable.showFilteredRows(getPathSpec(useRegex),
				useRegex);
		updateTabs(dv_rows, sv_rows);
		_dashboard.clearSelection(); // CB added to clear dashboard table
		// selections
	}

	// used by 'Schematic'
	public boolean isValidBpart(Vector variables) {
		boolean dvar = false;
		boolean svar = false;

		if (_dvarTable.getTableModel() != null)
			dvar = _dvarTable.getTableModel().isValidBParts(variables);

		if (_svarTable.getTableModel() != null)
			svar = _svarTable.getTableModel().isValidBParts(variables);

		return (dvar || svar);
	}

	// used by 'Schematic'
	public boolean filterByBpart(Vector variables) {
		// these are the 'base variable' count
		int dv_rows = _dvarTable.showFilteredRows(variables);
		int sv_rows = _svarTable.showFilteredRows(variables);
		updateTabs(dv_rows, sv_rows);

		_dashboard.clearSelection(); // CB added to clear dashboard table
		// selections

		// dv_rows = _dvarTable.getSelectedRowCount();
		// sv_rows = _svarTable.getSelectedRowCount();

		// System.out.println("number of dvars = " + dv_rows); //CB debugging
		// System.out.println("number of svars = " + sv_rows); //CB debugging

		if (dv_rows > 0 || sv_rows > 0)
			return true;
		else
			return false;
	}

	/*
	 * public void selectAllRows() { _table.selectAll(); }
	 */

	JList createMonthList() {
		String months[] = { "Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug", "Sep" };

		JList list = new JList(months);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(1);
		list.setSelectionInterval(0, months.length - 1);
		list.setToolTipText("Select months to extract for plots/tables");

		return list;

	}

	private void setTextFieldAction(JTextField textArea) {
		InputMap inputMap = textArea.getInputMap();
		ActionMap actionMap = textArea.getActionMap();

		Object transferTextActionKey = "TRANSFER_TEXT";
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				transferTextActionKey);
		// Also add shift+enter binding here if you want
		actionMap.put(transferTextActionKey, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				filter();
			}
		});
	}

	// //////////////////// GeneralRetrievePanel.java code
	// /////////////////////////////////////////
	JPanel createUpperPanel() {
		int textwidth = 15;
		// create filter panel
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(1, 7));

		_pathText = new JTextField[6];
		_pathText[0] = new JTextField(textwidth);
		setTextFieldAction(_pathText[0]);
		_pathText[0].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "A"));
		_pathText[1] = new JTextField(textwidth);
		setTextFieldAction(_pathText[1]);
		_pathText[1].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "B"));
		_pathText[2] = new JTextField(textwidth);
		setTextFieldAction(_pathText[2]);
		_pathText[2].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "C"));
		_pathText[3] = new JTextField(textwidth);
		setTextFieldAction(_pathText[3]);
		_pathText[3].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "D"));
		_pathText[4] = new JTextField(textwidth);
		setTextFieldAction(_pathText[4]);
		_pathText[4].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "E"));
		_pathText[5] = new JTextField(textwidth);
		setTextFieldAction(_pathText[5]);
		_pathText[5].setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.lightGray), "F"));

		// filterPanel.add(_varTypeBox);
		for (int i = 0; i < 6; i++) {
			filterPanel.add(_pathText[i]);
		}
		filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.blue), "Filter"));

		// _filterBtn = new JButton(FilterAction);
		_filterBtn = new JButton(_aa.FilterAction);
		// _retrieveBtn = new JButton(_aa.RetrieveAction);
		_monthlyTableBtn = new JButton(_aa.MonthlyAction);
		_tableBtn = new JButton(_aa.TableAction);
		_plotBtn = new JButton(_aa.PlotAction);
		_exceedBtn = new JButton(_aa.ExceedenceAction);
		_annualTotBtn = new JButton(_aa.AnnualTotAction);
		_annualTotExceedBtn = new JButton(_aa.AnnualTotExceedAction);
		_monthlyAvgBtn = new JButton(_aa.MonthlyAverageAction);

		// List of Months for plots/table
		_monthlist = createMonthList();

		Box btnPanel = new Box(BoxLayout.X_AXIS);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(_filterBtn);
		btnPanel.add(_monthlist);
		btnPanel.add(_monthlyTableBtn);
		// btnPanel.add(_retrieveBtn);
		btnPanel.add(_tableBtn);
		btnPanel.add(new JLabel("  "));
		btnPanel.add(_plotBtn);
		btnPanel.add(_exceedBtn);
		btnPanel.add(_annualTotBtn);
		btnPanel.add(_annualTotExceedBtn);
		btnPanel.add(_monthlyAvgBtn); // CB added

		btnPanel.add(Box.createHorizontalGlue());
		//
		Box filterBox = new Box(BoxLayout.Y_AXIS);
		filterBox.add(Box.createVerticalGlue());
		filterBox.add(filterPanel);
		filterBox.add(Box.createVerticalGlue());
		//
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalGlue());
		box.add(filterBox);
		box.add(btnPanel);
		box.add(Box.createVerticalGlue());
		Box box1 = new Box(BoxLayout.X_AXIS);
		box1.add(Box.createHorizontalGlue());
		box1.add(box);
		box1.add(Box.createHorizontalGlue());
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(box1, BorderLayout.CENTER);
		return upperPanel;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	private JPanel createLowerPanel() {
		JPanel resultsPanel = new JPanel(); // CB added

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(new JScrollPane(
				_dashboard = createDashBoardArea()));
		MouseListener dashboardML = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = _dashboard.getSelectedRow();
					String commonName = (String) _dashboard.getValueAt(row, 0);
					if (!commonName.trim().equals("")) {
						DerivedTimeSeries dts = FilterPanel.this._commonToDerivedTimeseriesMap
								.get(commonName);
						if (dts == null) {
							dts = new DerivedTimeSeries(commonName);
							FilterPanel.this._commonToDerivedTimeseriesMap.put(
									commonName, dts);
						}
						DashboardVariableWindow window = new DashboardVariableWindow(
								commonName, dts, FilterPanel.this);
						System.out.println("");
						window.setVisible(true);
						// e.consume(); //no effect - trying to not invoke the
						// sell editor
					}
					// } else if (e.getClickCount() == 1 && e.isPopupTrigger())
					// { //CB e.isPopupTrigger() DOES NOT WORK!!!
				} else if (e.getClickCount() == 1
						&& (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {//single click, not left button
					JPopupMenu menu = new JPopupMenu("DTS Menu");
					JMenuItem delete = new JMenuItem("Delete DTS");
					delete.setAction(DeleteDTSAction);
					menu.add(delete);
					menu.show(_dashboard, e.getX(), e.getY());

				}
			}

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 1 && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0){//single click, left button, added by Liheng Zhong to clear selection
					if (_iCurrentRow >= 0 && _dashboard.getSelectedRowCount() == 1 && _dashboard.getSelectedRow() == _iCurrentRow){
						_iCurrentRow = -1;
						_dashboard.clearSelection();
					} else{
						_iCurrentRow = _dashboard.getSelectedRow();
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		};

		TableModelListener tml = new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					Object obj = _dashboard.getValueAt(_dashboard.getModel()
							.getRowCount() - 1, 0);
					if (_dashboard.getModel().getRowCount() > 0
							&& !((String) _dashboard.getValueAt(_dashboard
									.getModel().getRowCount() - 1, 0)).trim()
									.equals("")) {
						if (_dashboard.getModel() instanceof DefaultTableModel) {
							Vector data = ((DefaultTableModel) _dashboard
									.getModel()).getDataVector();
							Vector row = new Vector(1);
							row.add("");
							((DefaultTableModel) _dashboard.getModel())
									.getDataVector().add(row);
							((DefaultTableModel) _dashboard.getModel())
									.fireTableDataChanged();
						}
					}
				}
			}
		};

		_dashboard.getModel().addTableModelListener(tml);

		/*
		 * JPopupMenu menu = new JPopupMenu("DTS Menu"); JMenuItem delete = new
		 * JMenuItem("Delete DTS"); delete.setAction(DeleteDTSAction);
		 * menu.add(delete); menu.setVisible(true); setComponentPopupMenu(menu);
		 */// DOES NOT WORK

		JTabbedPane tabbedPane;
		splitPane.setRightComponent(tabbedPane = createResultsPane());
		tabbedPane.setPreferredSize(new Dimension(740, tabbedPane
				.getPreferredSize().height));

		_dashboard.addMouseListener(dashboardML);
		_dashboard.setPreferredScrollableViewportSize(new Dimension(150, 150));

		_dashboard.getColumn(_dashboard.getColumnName(0)).setCellEditor(null);

		ListSelectionModel rowSM = _dashboard.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting())
					return;
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					AppAction.updateAllActions();
				} else {
					// CB causes StackOverflowError when from repeated
					// tablemodel change then actiona upated
					AppAction.updateAllActions();
				}
			}
		});

		// resultsPanel.add(new JScrollPane(_dashboard = createDashBoardArea()),
		// BorderLayout.WEST); //CB added
		// resultsPanel.add(createResultsPane(), BorderLayout.CENTER); //CB
		// added
		resultsPanel.add(new JScrollPane(splitPane), BorderLayout.CENTER); // CB
		// added
		// -
		// scrollPane
		// changes
		// nothing!!!

		return resultsPanel;
	}

	public boolean areDashboardRowsSelected() {
		int[] selectedRows = _dashboard.getSelectedRows();
		if (selectedRows.length > 0)
			return true;
		return false;
	}

	public void resetAllCache() {
		_periodValueViewer = null;
	}


	// //////////////////////////////////////////////////////////////////////////////
	// TEST DRIVER
	// [070413] TP
	/*
	 * CB public static void main(String[] args) throws Exception { String
	 * lookAndFeel = UIManager.getSystemLookAndFeelClassName(); //String
	 * lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
	 * UIManager.setLookAndFeel(lookAndFeel);
	 * 
	 * javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run()
	 * { JFrame f = new JFrame("FilterPanel"); f.addComponentListener(new
	 * ComponentAdapter() { public void componentResized(ComponentEvent e) {
	 * JFrame tmp = (JFrame)e.getSource(); if (tmp.getWidth()<800 ||
	 * tmp.getHeight()<200) { tmp.setSize(800, 200); } } });
	 * 
	 * //2005A01ASV.dss* SubsetDV_Exist.dss* SubsetDV_Future.dss*
	 * 
	 * FilterPanel fPanel = new FilterPanel();
	 * 
	 * // set the base files for catalogs//fPanel.setBaseDVFileName(
	 * "D:/workspace/hecdss/wrims/dss/data/SubsetDV_Exist.dss");
	 * //fPanel.setBaseSVFileName
	 * ("D:/workspace/hecdss/wrims/dss/data/2005A01ASV.dss");
	 * 
	 * // get catalog/setup table model
	 * ////fPanel.catalog("D:/workspace/hecdss/wrims/dss/data/SubsetDV_Exist.dss"
	 * , "DV"); //fPanel.setTableModel("DV");
	 * 
	 * f.getContentPane().add(fPanel); f.pack(); f.setVisible(true); } }); }
	 */
}
