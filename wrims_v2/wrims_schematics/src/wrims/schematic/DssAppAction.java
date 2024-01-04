package wrims.schematic;

//import java.awt.Container;
//import javax.swing.AbstractAction;
//import javax.swing.Icon;
//import java.util.Vector;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import wrims.dss.DssViewer;
import wrims.dss.FFilter;
import wrims.dss.ViewerFile;

// Define an Action that knows about state of GUI and supports enabling/disabling
// depending on the current context.
public class DssAppAction {
	public static boolean DEBUG = false;

	String _defaultDssLocation = "c:/ztp/schematic_dss/data/";

	String _lastDssLocation = null;

	String _defaultDsvLocation = "c:/ztp/schematic_dss/data/";

	String _lastDsvLocation = null;

	MainPanel _mp = null;

	public DssAppAction(MainPanel mp) {
		_mp = mp;
	}

	// CB private FilterPanel getFP() { //CB changed name to match AppAction's
	// to reduce errors and confusion
	private FilterPanel getFilterPanel() {
		return _mp.getFilterPanel();
	}

	// CB private MessagePanel getMP() { //CB changed name to match AppAction's
	// to reduce errors and confusion
	private MessagePanel getMessagePanel() {
		return _mp.getMessagePanel();
	}

	// ==============================================================
	// Define all the command actions
	// ==============================================================
	// ================= FilterPanel actions ========================
	public AppAction FilterAction = new AppAction("Filter", _mp,
			"Filter specified A-F parts") {
		public void actionPerformed(ActionEvent e) {
			getFilterPanel().filter();
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			return super.canAct() && (getFilterPanel().isFilterActive());
		}
	};

	/*
	 * AppAction RetrieveAction = new AppAction("Retrieve", _mp,
	 * "Retrieve selected records") { public void actionPerformed(ActionEvent e)
	 * { getFP().retrieve("All"); AppAction.updateAllActions(); } public boolean
	 * canAct() { return super.canAct() && getFP().isFilterActive() &&
	 * getFP()._table.getSelectedRowCount() > 0; } };
	 */

	// CB TODO create abstact classes for the next several "output" type actions
	// and have them use the two canAct() methods "in common"
	// Three of them require monthly time step.

	AppAction MonthlyAction = new AppAction(DssViewer.MONTHLY_STRING, _mp,
			"Display monthly table of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			getFilterPanel().retrieve(DssViewer.MONTHLY);
			AppAction.updateAllActions();
		}

		public boolean canAct() { // CB TODO need to add isCompActive()
									// elsewhere!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// CB return super.canAct() && getFP().isFilterActive() &&
		// getFP().rowsSelected();
			Object filterPanel = getFilterPanel();
			Object messagePanel = getMessagePanel();
			return super.canAct() && getMessagePanel().isCompActive()
					// && !_mp.isSelectedFilesThreadAlive() &&
					// !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected()); // CB altered
		}
	};

	AppAction TableAction = new AppAction(DssViewer.TABLE_STRING, _mp,
			"Display table of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			// System.out.println("in actionPerformed of AppAction TableAction");
			// //CB debugging
			getFilterPanel().retrieve(DssViewer.TABLE);
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterActive() &&
			// getFP().rowsSelected();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected()); // CB
		}
	};

	AppAction PlotAction = new AppAction(DssViewer.PLOT_STRING, _mp,
			"Display plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			getFilterPanel().retrieve(DssViewer.PLOT);
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterActive() &&
			// getFP().rowsSelected();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected()); // CB
		}
	};

	AppAction ExceedenceAction = new AppAction(DssViewer.EXCEEDENCE_STRING,
			_mp, "Display exceedence plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			// CB getFP().retrieve(DssViewer.PLOT, true, false);
			getFilterPanel().retrieve(DssViewer.EXCEEDENCE);
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterActive() &&
			// getFP().rowsSelected();
			// CB && getFP().isMonthlyTimeStep();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected())
					&& getFilterPanel().isMonthlyTimeStep(); // CB
		}
	};

	/**
	 * CB added
	 */
	AppAction MonthlyAverageAction = new AppAction(
			DssViewer.MONTHLY_AVERAGE_STRING, _mp,
			"Display monthly averages plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			getFilterPanel().retrieve(DssViewer.MONTHLY_AVERAGE);
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterActive() &&
			// getFP().rowsSelected();
			// CB && getFP().isMonthlyTimeStep();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected())
					&& getFilterPanel().isMonthlyTimeStep(); // CB
		}
	};

	AppAction AnnualTotAction = new AppAction(DssViewer.ANNUAL_TOTAL_STRING,
			_mp, "Display annual total plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			// CB getFP().retrieve(DssViewer.PLOT, false, true);
			getFilterPanel().retrieve(DssViewer.ANNUAL_TOTAL); // CB
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterValid() &&
			// getFP().rowsSelected()
			// CB && getFP().isMonthlyTimeStep();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected())
					&& getFilterPanel().isMonthlyTimeStep(); // CB

		}
	};

	AppAction AnnualTotExceedAction = new AppAction(
			DssViewer.ANNUAL_TOTAL_EXCEEDENCE_STRING, _mp,
			"Display annual totals in exceedence plot of selected pathnames") {
		public void actionPerformed(ActionEvent e) {
			// getFP().retrieve(DssViewer.PLOT, true, true);
			getFilterPanel().retrieve(DssViewer.ANNUAL_TOTAL_EXCEEDENCE); // CB
			AppAction.updateAllActions();
		}

		public boolean canAct() {
			// CB return super.canAct() && getFP().isFilterValid() &&
			// getFP().rowsSelected()
			// CB && getFP().isMonthlyTimeStep();
			return super.canAct()
					&& getMessagePanel().isCompActive()
					&& !_mp.isSelectedFilesThreadAlive()
					&& !_mp.isUnselectedFilesThreadAlive()
					&& getFilterPanel().isFilterActive()
					&& (getFilterPanel().rowsSelected() || getFilterPanel()
							.areDashboardRowsSelected())
					&& getFilterPanel().isMonthlyTimeStep(); // CB
		}
	};

	// ================= Main Menu Bar actions ========================
	// Project
	AppAction ViewerNewFileAction = new AppAction("New", _mp,
			"Reset DSS file settings") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().resetDSSFiles("");
			getFilterPanel().newViewerFile();
		}
	};

	AppAction ViewerOpenFileAction = new AppAction("Open", _mp,
			"Open a project file") {
		public void actionPerformed(ActionEvent e) {
			openViewerFile();
		}
	};

	AppAction ViewerSaveFileAction = new AppAction("Save As", _mp,
			"Save a viewer project file") {
		public void actionPerformed(ActionEvent e) {
			saveViewerFile();
		}
	};

	// ///////////////////////////////////////////////////////////////////

	AppAction BaseDVOpenFileAction = new AppAction("Dvar File", _mp,
			"Open the Base DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("BaseDV");
			openDSSFile(FilterPanel.BASE_DV_FILE_INT);
		}
	};

	AppAction Comp1DVOpenFileAction = new AppAction("Dvar File", _mp,
			"Open the 1st Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp1DV");
			openDSSFile(FilterPanel.COMP1_DV_FILE_INT);
		}
	};

	AppAction Comp2DVOpenFileAction = new AppAction("Dvar File", _mp,
			"Open the 2nd Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp2DV");
			openDSSFile(FilterPanel.COMP2_DV_FILE_INT);
		}
	};

	AppAction Comp3DVOpenFileAction = new AppAction("Dvar File", _mp,
			"Open the 3rd Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp3DV");
			openDSSFile(FilterPanel.COMP3_DV_FILE_INT);
		}
	};

	AppAction BaseSVOpenFileAction = new AppAction("Svar File", _mp,
			"Open the Base SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("BaseSV");
			openDSSFile(FilterPanel.BASE_SV_FILE_INT);
		}
	};

	AppAction Comp1SVOpenFileAction = new AppAction("Svar File", _mp,
			"Open the 1st Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp1SV");
			openDSSFile(FilterPanel.COMP1_SV_FILE_INT);
		}
	};

	AppAction Comp2SVOpenFileAction = new AppAction("Svar File", _mp,
			"Open the 2nd Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp2SV");
			openDSSFile(FilterPanel.COMP2_SV_FILE_INT);
		}
	};

	AppAction Comp3SVOpenFileAction = new AppAction("Svar File", _mp,
			"Open the 3rd Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp3SV");
			openDSSFile(FilterPanel.COMP3_SV_FILE_INT);
		}
	};

	AppAction ExitAction = new AppAction("Exit", _mp) {
		public void actionPerformed(ActionEvent e) {
			_mp.exit();
		}
	};

	// ================= MessagePanel actions ========================
	AppAction BaseDVOpenAction = new AppAction("DV:", _mp,
			"Open the Base DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("BaseDV");
			openDSSFile(FilterPanel.BASE_DV_FILE_INT); // CB
		}
	};

	AppAction Comp1DVOpenAction = new AppAction("DV:", _mp,
			"Open the 1st Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp1DV");
			openDSSFile(FilterPanel.COMP1_DV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	AppAction Comp2DVOpenAction = new AppAction("DV:", _mp,
			"Open the 2nd Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp2DV");
			openDSSFile(FilterPanel.COMP2_DV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	AppAction Comp3DVOpenAction = new AppAction("DV:", _mp,
			"Open the 3rd Comparison DV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp3DV");
			openDSSFile(FilterPanel.COMP3_DV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	AppAction BaseSVOpenAction = new AppAction("SV:", _mp,
			"Open the Base SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("BaseSV");
			openDSSFile(FilterPanel.BASE_SV_FILE_INT); // CB
		}
	};

	AppAction Comp1SVOpenAction = new AppAction("SV:", _mp,
			"Open the 1st Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp1SV");
			openDSSFile(FilterPanel.COMP1_SV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	AppAction Comp2SVOpenAction = new AppAction("SV:", _mp,
			"Open the 2nd Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp2SV");
			openDSSFile(FilterPanel.COMP2_SV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	AppAction Comp3SVOpenAction = new AppAction("SV:", _mp,
			"Open the 3rd Comparison SV file") {
		public void actionPerformed(ActionEvent e) {
			// CB openDSSFile("Comp3SV");
			openDSSFile(FilterPanel.COMP3_SV_FILE_INT); // CB
		}

		// CB added canAct() so a Base file must always be "open" first
		public boolean canAct() {
			return getMessagePanel().isBaseActive();
		}
	};

	// ///////////// MODE actions
	AppAction CompModeAction = new AppAction("Comp", _mp, "Compare Mode") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().setMode("Comp");
		}

		// public boolean canAct() { return getMP().isBaseActive(); }
		/*
		 * CB public boolean canAct() { if (!getMP().isCompDiffActive() &&
		 * getMP().isBaseActive()) { // getMP().setMode("Base"); return true; }
		 * else if (getMP().isBaseActive()) return true; return false; }
		 */

		public boolean canAct() {
			if (getMessagePanel().isCompActive())
				return true;
			return false;
		}
	};

	// CB added
	AppAction WateryearModeAction = new AppAction("Oct - Sep", _mp,
			"Wateryear Annual Mode") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().setAnnualType(MessagePanel.WATERYEAR);
		}

		public boolean canAct() {
			return true;
		}
	};

	// CB added
	AppAction CalendarYearModeAction = new AppAction("Jan - Dec", _mp,
			"Calendar Year Annual Mode") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().setAnnualType(MessagePanel.CALENDAR_YEAR);
		}

		public boolean canAct() {
			return true;
		}
	};

	// CB added
	AppAction FederalContractYearModeAction = new AppAction("Mar - Feb", _mp,
			"Federal Contract Year Annual Mode") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().setAnnualType(MessagePanel.FEDERAL_CONTRACT_YEAR);
		}

		public boolean canAct() {
			return true;
		}
	};

	/*
	 * AppAction CompModeAction = new AppAction("Comp", _mp, "Compare Mode") {
	 * public void actionPerformed(ActionEvent e) { getMP().setMode("Comp"); }
	 * public boolean canAct() { return getMP().isCompDiffActive(); } };
	 */
	AppAction DiffModeAction = new AppAction("Diff", _mp, "Difference Mode") {
		public void actionPerformed(ActionEvent e) {
			getMessagePanel().setMode("Diff");
		}

		public boolean canAct() {
			return getMessagePanel().isDiffActive();
		}
	};

	// ///////// Action methods
	// called from schematic DSS->Load Defaults
	/*
	 * public void loadDefaultFiles() {
	 * 
	 * // filter for dss files FilenameFilter filter = new FilenameFilter() {
	 * public boolean accept(File dir, String name) { return
	 * name.toLowerCase().endsWith(".dss"); } };
	 * 
	 * // Base DSS files String defaultLocation =
	 * "c:/ztp/schematic_dss/defaults/base/"; setDefaultFile(filter, "Base",
	 * defaultLocation, "Loading Base DSS files ...");
	 * 
	 * // Comp DSS files defaultLocation =
	 * "c:/ztp/schematic_dss/defaults/comp/"; setDefaultFile(filter, "Comp",
	 * defaultLocation, "Loading Comp DSS files ..."); }
	 */

	/*
	 * public void setDefaultFile(FilenameFilter filter, String dssType, String
	 * loc, String message) { _mp.setStatus(message);
	 * 
	 * File dir = new File(loc); String[] children = dir.list(filter);
	 * 
	 * String cnt; String dss; // String end = ""; if (children == null) { //
	 * Either dir does not exist or is not a directory
	 * System.out.println("No dss files"); } else { for (int i = 0; i <
	 * children.length; i++) { // if i > 1: // break; String filename =
	 * children[i]; // System.out.println("file: " + filename);
	 * 
	 * if (dssType.startsWith("Base")) cnt = ""; else cnt = String.valueOf(i +
	 * 1);
	 * 
	 * if (filename.toLowerCase().contains("sv")) dss = dssType + cnt + "SV";
	 * else dss = dssType + cnt + "DV";
	 * 
	 * String fn = loc + filename; setDSSFile(fn, dss); } } _mp.setStatus(); }
	 */

	public String openViewerFile() {
		return openViewerFile(null);
	}

	public String openViewerFile(String filename) {
		_mp.setStatus("Retrieving Viewer File...");
		if (filename == null || filename.trim().length() == 0) {
			FFilter filter = new FFilter("dsv", "DSS viewer project file ");
			filename = getFileName(filter, JFileChooser.OPEN_DIALOG, "dsv");

			if (filename == null) {
				_mp.setStatus();
				return null;
			}
		}
		if (!new File(filename).exists()) {
			JOptionPane.showMessageDialog(_mp, "DSS project file, " + filename
					+ " does not exist", "File Not Found",
					JOptionPane.ERROR_MESSAGE);
			// CB TODO need to remove the menu item from the DSS menu
			return null;
		}
		ViewerFile vf = new ViewerFile(filename);
		vf.read();
		// System.out.println("HASH: "+vf.var2value);
		_mp.getMessagePanel().resetDSSFiles(filename);
		// CB getFP().newViewerFile(); //CB commented out because it does
		// redundant things
		boolean showDV = false;
		String var;
		Set set = vf.var2value.keySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			var = (String) iter.next();
			int intKey = FilterPanel.stringToIntKeyMap.get(var);
			// System.out.println ( " "+var+" ) "+vf.var2value.get(var));
			setDSSFile((String) vf.var2value.get(var), intKey); // CB
			// CB if (var.equals("BaseDV"))
			if (intKey == FilterPanel.BASE_DV_FILE_INT) // CB
				showDV = true;
		}
		if (showDV)
			// CB getFilterPanel().refreshFilter("DV");
			getFilterPanel().refreshFilter(FilterPanel.DVAR); // TODO finish
																// refreshFilter
																// and may sure
																// this call
																// works?

		AppAction.updateAllActions();
		_mp.setStatus();

		if (DEBUG)
			System.out.println("DssAppAction.openViewerFile()");
		if (DEBUG)
			System.out.println(filename);
		_mp.setDateBoxModel(new DefaultComboBoxModel(_mp.loadDateBoxList()));
		// FIXME: _mp.doMonthlyDataWork();
		_mp.updateDSSMenu(filename, true); // TODO test this call with boolean
											// arg added
		return filename;
	}

	void saveViewerFile() {
		_mp.setStatus("Saving Viewer File...");
		FFilter filter = new FFilter("dsv", "DSS viewer project file ");
		String fn = getFileName(filter, JFileChooser.SAVE_DIALOG, "dsv");
		if (fn == null) {
			_mp.setStatus();
			return;
		}
		String loc = fn.toLowerCase();
		if (loc.indexOf(".") == -1) {
			fn += ".dsv";
		}
		ViewerFile vf = new ViewerFile(fn);
		vf.write(_mp.getMessagePanel().getFileNames());
		_mp.getMessagePanel().setProjectName(fn); // CB added to make sure it is
													// there
		_mp.updateDSSMenu(fn, true); // TODO test this call with boolean arg
										// added
		_mp.setStatus();

		if (DEBUG)
			System.out.println("DssAppAction.saveViewerFile()");
		if (DEBUG)
			System.out.println(fn);
	}

	// CB void setDSSFile(String fn, String dss) {
	void setDSSFile(String fn, int dssType) { // CB
		_mp.getMessagePanel().setDSSFile(fn, dssType);
		// if (dss.equals("BaseDV") || dss.equals("BaseSV"))
		if (dssType / 4 == 0)
			// getFilterPanel().setBaseDSSName(fn, dss);
			getFilterPanel().setBaseDSSName(fn, dssType);
		else 
			getFilterPanel().setCompDSSName(fn, dssType);
		// getFilterPanel().setFpart(fn, dss);
		getFilterPanel().setFpart(fn, dssType);
	}

	// CB void openDSSFile(String dss) {
	void openDSSFile(int dssType) {
		_mp.setStatus("Retrieving DSS File...");
		FFilter filter = new FFilter("dss", "HEC-DSS database file ");
		String fn = getFileName(filter, JFileChooser.OPEN_DIALOG, "dss");
		if (fn == null) {
			_mp.setStatus();
			_mp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		_mp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// CB setDSSFile(fn, dss);
		setDSSFile(fn, dssType);

		_mp.setStatus();
		AppAction.updateAllActions();

		if (DEBUG)
			System.out.println("DssAppAction.openDSSFile()");
		if (DEBUG)
			System.out.println(fn);

		// CB added block. TODO: if the filter button is changed to act on more
		// than just the Base file, need to to something here
		if (_mp.isDateBoxListEmpty())
			_mp
					.setDateBoxModel(new DefaultComboBoxModel(_mp
							.loadDateBoxList()));
		/*
		 * _mp.loadAllVariableData(_mp.getAllVariables(), dssType); String date
		 * = _mp.getSchematicDate(); if (date != null &&
		 * !date.trim().equals("")) { if (date.indexOf("-") == -1) //CB
		 * long-term averages need time for recalculation (see below)
		 * _mp.updateSchematicValues(); } calculateLongTermAverages();
		 */
		
		//added by Liheng Zhong to refresh displayed values after a new DSS file is opened. 
		_mp.stopMonthlyDataWork();
		_mp.updateSchematicValues();
		
		doMonthlyDataWork(dssType);

		_mp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * CB added.
	 * 
	 * @param dssType
	 */
	private void doMonthlyDataWork(int dssType) {
		_mp.doMonthlyDataWork(dssType);
	}

	public String getFileName(FFilter filter, int dialogType, String fileType) {
		String fn = null;
		String loc = null;

		// if (DEBUG)
		// System.out.println("FFilter Description:"+filter.getDescription());

		String defaultLocation = null;

		if (fileType.equals("dss")) {
			if ((_lastDssLocation != null) && (!_lastDssLocation.equals("")))
				defaultLocation = _lastDssLocation;
			else
				defaultLocation = _defaultDssLocation;
		} else if (fileType.equals("dsv")) {
			if ((_lastDsvLocation != null) && (!_lastDsvLocation.equals("")))
				defaultLocation = _lastDsvLocation;
			else
				defaultLocation = _defaultDsvLocation;
		}

		JFileChooser _fileChooser = new JFileChooser();
		// _fileChooser.setDialogType(dialogType);
		_fileChooser.setFileFilter(filter);

		if ((defaultLocation != null) && (!defaultLocation.equals(""))) {
			File currentFile = new File(defaultLocation);
			_fileChooser.setCurrentDirectory(currentFile);
		} else
			_fileChooser.setCurrentDirectory(null);
		// _fileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal;
		if (dialogType == JFileChooser.SAVE_DIALOG)
			returnVal = _fileChooser.showSaveDialog(_mp);
		else
			returnVal = _fileChooser.showOpenDialog(_mp);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fn = _fileChooser.getSelectedFile().getName();
			loc = _fileChooser.getSelectedFile().getAbsolutePath();
			loc.toLowerCase();
		} else
			return null;

		if (DEBUG)
			System.out.println("file:" + fn);
		if (DEBUG)
			System.out.println("loc:" + loc);

		if (fileType.equals("dss"))
			_lastDssLocation = loc;
		else if (fileType.equals("dsv"))
			_lastDsvLocation = loc;

		return loc;
	}

}
