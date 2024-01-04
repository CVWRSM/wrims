package wrims.schematic;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import wrims.dss.DssViewer;

public class MainPanel extends JPanel { 
	private static final boolean DISABLE_ALL_THREAD_WORK = true;
	public static boolean DEBUG = false;
	public JLabel _status; // CB TODO make this private too
	private JFrame _frame;
	private FilterPanel _filterPanel;
	private MessagePanel _messagePanel;
	private DssAppAction _aa;
	private ISchematic _schematic; // CB added
	private Timer _threadMonitor; // CB added
	private Thread _checker = null; // CB added

	// CB added. A thread for calculating long-term averages for the Schematic
	// ValueNodes.
	private UpdateSchematicValuesThread _updateSchematicValuesThread = null;

	// CB added.
	class UpdateSchematicValuesThread extends Thread {
		// Hashtable<String, Object> _names = null;

		public UpdateSchematicValuesThread() {
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				if (_selectedFilesThread != null
						&& _selectedFilesThread.isAlive()) {
					_selectedFilesThread.join();
					if (DssViewer.DSS_DEBUG) {
						System.out
								.println("UpdateSchematicValuesThread joined _selectedFilesThread");
						System.out.flush();
					}
				}
				// System.out.println("Calling updateValues at " +
				// System.currentTimeMillis());
				String date = getSchematicDate();
				if (date != null && !date.trim().equals("")) {
					// No longer if (date.indexOf("-") == -1) //CB long-term
					// averages need time for recalculation (see below)
					_schematic.updateValues(date);
				}
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out
							.println("InterruptedException when UpdateSchematicValuesThread is attempting to join SelectedFilesThread");
					System.out.flush();
				}
			}
			_updateSchematicValuesThread = null;
			// System.out.println("_updateSchematicValuesThread duration = " +
			// (System.currentTimeMillis() - start));
		}
	};

	// CB added. A thread for updating non-long-term average list items in date
	// date box
	private UpdateSchematicDateBoxListMonthsThread _updateSchematicDateBoxListMonthsThread = null;

	// CB added.
	class UpdateSchematicDateBoxListMonthsThread extends Thread {

		public UpdateSchematicDateBoxListMonthsThread() {
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				if (_selectedFilesThread != null
						&& _selectedFilesThread.isAlive()) {
					_selectedFilesThread.join();
					if (DssViewer.DSS_DEBUG) {
						System.out
								.println("_updateSchematicDateBoxListMonthsThread joined _selectedFilesThread");
						System.out.flush();
					}
				}
				// System.out.println("Calling selected files updateDateBoxTWs at "
				// + System.currentTimeMillis());
				_schematic.updateDateBoxTWs(true, false);
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out
							.println("InterruptedException when _updateSchematicDateBoxListMonthsThread attempting to join _selectedFilesThread");
					System.out.flush();
				}
			}
			_updateSchematicDateBoxListMonthsThread = null;
			// System.out.println("_updateSchematicDateBoxListMonthsThread duration = "
			// + (System.currentTimeMillis() - start));
		}
	};

	// CB added. A thread for updating long-term average list items in date date
	// box
	private UpdateSchematicDateBoxListLongTermsThread _updateSchematicDateBoxListLongTermsThread = null;

	// CB added.
	class UpdateSchematicDateBoxListLongTermsThread extends Thread {

		public UpdateSchematicDateBoxListLongTermsThread() {
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				if (_selectedFilesLongTermAverageThread != null
						&& _selectedFilesLongTermAverageThread.isAlive()) {
					_selectedFilesLongTermAverageThread.join();
					if (DssViewer.DSS_DEBUG) {
						System.out
								.println("_selectedFilesLongTermAverageThread joined _selectedFilesLongTermAverageThread");
						System.out.flush();
					}
				}
				// System.out.println("Calling long terms updateDateBoxTWs at "
				// + System.currentTimeMillis());
				_schematic.updateDateBoxTWs(true, true);
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out
							.println("InterruptedException when _selectedFilesLongTermAverageThread attempting to join _selectedFilesLongTermAverageThread");
					System.out.flush();
				}
			}
			_updateSchematicDateBoxListLongTermsThread = null;
			// System.out.println("_updateSchematicDateBoxListMonthsThread duration = "
			// + (System.currentTimeMillis() - start));
		}
	};

	// CB added. A thread for calculating long-term averages for the Schematic
	// ValueNodes.
	private SelectedFilesLongTermAveragesThread _selectedFilesLongTermAverageThread = null;;

	// CB added.
	class SelectedFilesLongTermAveragesThread extends Thread {
		int _dssType = -1;

		public SelectedFilesLongTermAveragesThread(int dssType) {
			setDssType(dssType);
		}

		public void run() {
			long start = System.currentTimeMillis();
			disableSchematicDateBoxLongtermItems();
			try {
				if (_selectedFilesThread != null
						&& _selectedFilesThread.isAlive()) {
					_selectedFilesThread.join();
					if (DssViewer.DSS_DEBUG) {
						System.out
								.println("_selectedFilesLongTermAverageThread joined _selectedFilesThread");
						System.out.flush();
					}
				}
				// System.out.println("Calling selected files calculateLongTermAverages at "
				// + System.currentTimeMillis());
				boolean wasSuccessful = getValueViewer()
						.calculateLongTermAverages(getSchematicTimeWindows(),
								_dssType, true, null);
				// System.out.println("Finished selected files calculateLongTermAverages call at "
				// + System.currentTimeMillis());
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out
							.println("InterruptedException when _selectedFilesLongTermAverageThread joining _selectedFilesThread");
					System.out.flush();
				}
			}
			_selectedFilesLongTermAverageThread = null;
			// System.out.println("_selectedFilesLongTermAverageThread duration = "
			// + (System.currentTimeMillis() - start));
		}

		/*
		 * public void interrupt() { super.interrupt();
		 * _schematic.resetProgressBars();
		 * _schematic.setProgressVisibility(false); }
		 */

		void setDssType(int dssType) {
			_dssType = dssType;
		}
	};

	// CB added. A thread for calculating long-term averages for the Schematic
	// ValueNodes.
	private UnselectedFilesLongTermAveragesThread _unselectedFilesLongTermAverageThread = null;;

	// CB added.
	class UnselectedFilesLongTermAveragesThread extends Thread {
		int _dssType = -1;

		public UnselectedFilesLongTermAveragesThread(int dssType) {
			setDssType(dssType);
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				if (_unselectedFilesThread != null
						&& _unselectedFilesThread.isAlive()) {
					_unselectedFilesThread.join();
					if (DssViewer.DSS_DEBUG) {
						System.out
								.println("_unselectedFilesLongTermAverageThread joined _unselectedFilesThread");
						System.out.flush();
					}
				}
				/*
				 * if (_selectedFilesLongTermAverageThread != null &&
				 * _selectedFilesLongTermAverageThread.isAlive()) {
				 * _selectedFilesLongTermAverageThread.join(); if
				 * (DssViewer.DSS_DEBUG) {System.out.println(
				 * "_unselectedFilesLongTermAverageThread joined _selectedFilesLongTermAverageThread"
				 * ); System.out.flush(); } }
				 */
				// System.out.println("Calling unselected files calculateLongTermAverages at "
				// + System.currentTimeMillis());
				boolean wasSuccessful = getValueViewer()
						.calculateLongTermAverages(getSchematicTimeWindows(),
								_dssType, false, null);
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out
							.println("InterruptedException when _unselectedFilesLongTermAverageThread joining _unselectedFilesThread");
					System.out.flush();
				}
			}
			_unselectedFilesLongTermAverageThread = null;
			_schematic.resetProgressBars();
			_schematic.setProgressVisibility(false);
			// System.out.println("_unselectedFilesLongTermAverageThread duration = "
			// + (System.currentTimeMillis() - start));
		}

		public void interrupt() {
			super.interrupt();
			_schematic.resetProgressBars();
			_schematic.setProgressVisibility(false);
		}

		void setDssType(int dssType) {
			_dssType = dssType;
		}
	};

	// CB added. A thread for for loading the data in the
	// <code>_unselectedDssFiles</code>.
	private SelectedFilesThread _selectedFilesThread = null;;

	// CB added.
	class SelectedFilesThread extends Thread {
		Hashtable<String, Object> _names = null;
		int _dssType = -1;

		public SelectedFilesThread(int dssType) {
			setDssType(dssType);
		}

		/**
		 * If the <code>_unselectedFilesDataThread</code> is running, stop it.
		 * If the <code>_longTermAverageThread</code> is running, stop it.
		 */
		public void run() {
			long start = System.currentTimeMillis();
			disableSchematicDateBoxMonthItems();
			_schematic.resetProgressBars();
			_schematic.setProgressVisibility(true);
			if (DssViewer.DSS_DEBUG) {
				System.out.println("Starting _selectedFilesThread");
				System.out.flush();
			}
			// System.out.println("Calling selected files loadAllVariableData at "
			// + System.currentTimeMillis());
			boolean wasSuccessful = getFilterPanel().loadAllVariableData(
					MainPanel.this.getAllVariables(), _dssType, true, null);
			_selectedFilesThread = null;
			// System.out.println("_selectedFilesThread duration = " +
			// (System.currentTimeMillis() - start));
		}

		void setAll(Hashtable<String, Object> names, int dssType) {
			setNames(names);
			setDssType(dssType);
		}

		void setNames(Hashtable<String, Object> names) {
			_names = names;
		}

		void setDssType(int dssType) {
			_dssType = dssType;
		}
	};

	// CB added. A thread for for loading the data in the
	// <code>_unselectedDssFiles</code>.
	private UnselectedFilesThread _unselectedFilesThread = null;

	// CB added.
	class UnselectedFilesThread extends Thread {
		int _dssType = -1;

		public UnselectedFilesThread(int dssType) {
			setDssType(dssType);
		}

		public void run() {
			long start = System.currentTimeMillis();
			try {
				// CB if the _selectedFilesThread is running, this thread must
				// wait until it is done
				// if (_selectedFilesThread != null &&
				// _selectedFilesThread.isAlive()) { //CB changed order to
				// selected load, selected LT calc, unselected load, unselected
				// LT calc
				// _selectedFilesThread.join();
				if (_selectedFilesLongTermAverageThread != null
						&& _selectedFilesLongTermAverageThread.isAlive()) {
					_selectedFilesLongTermAverageThread.join();
					if (DssViewer.DSS_DEBUG) {
						// System.out.println("_unselectedFilesThread joined _selectedFilesThread");
						System.out
								.println("_unselectedFilesThread joined _selectedFilesLongTermAverageThread");
						System.out.flush();
					}
				}
				// System.out.println("Calling unselected files loadAllVariableData at "
				// + System.currentTimeMillis());
				getFilterPanel().loadAllVariableData(getAllVariables(),
						_dssType, false, null);
			} catch (InterruptedException ie) {
				if (DssViewer.DSS_DEBUG) {
					System.out.println(
					// "InterruptedException when _unselectedFilesThread when attempting to join _selectedFilesThread");
							"InterruptedException when _unselectedFilesThread when attempting to join _selectedFilesLongTermAverageThread");
					System.out.flush();
				}
			}
			_unselectedFilesThread = null;
			// System.out.println("_unselectedFilesThread duration = " +
			// (System.currentTimeMillis() - start));
		}

		void setDssType(int dssType) {
			_dssType = dssType;
		}
	};

	public MainPanel(ISchematic schematic, JFrame frame) { // CB added Schematic
															// arg
		_frame = frame;
		_aa = new DssAppAction(this);
		initApp(schematic);
	}

	/**
	 * CB added.
	 * 
	 * @param dssType
	 */
	public void updateSchematicValues() { // CB TODO add String arg version IF
											// needed
		String date = getSchematicDate();
		if (date != null && !date.trim().equals("")) {
			// No longer if (date.indexOf("-") == -1) //CB long-term
			// averages need time for recalculation (see below)
			_schematic.updateValues(date);
		}
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSelectedFilesThreadAlive() {
		return (_selectedFilesThread != null && _selectedFilesThread.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSchematicValuesThreadAlive() {
		return (_updateSchematicValuesThread != null && _updateSchematicValuesThread
				.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isUnselectedFilesThreadAlive() {
		return (_unselectedFilesThread != null && _unselectedFilesThread
				.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSelectedFilesLongTermAverageThreadAlive() {
		return (_selectedFilesLongTermAverageThread != null && _selectedFilesLongTermAverageThread
				.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSchematicDateBoxListMonthsThreadAlive() {
		return (_updateSchematicDateBoxListMonthsThread != null && _updateSchematicDateBoxListMonthsThread
				.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isSchematicDateBoxListLongTermsThreadAlive() {
		return (_updateSchematicDateBoxListLongTermsThread != null && _updateSchematicDateBoxListLongTermsThread
				.isAlive());
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isUnselectedFilesLongTermAverageThreadAlive() {
		return (_unselectedFilesLongTermAverageThread != null && _unselectedFilesLongTermAverageThread
				.isAlive());
	}

	/**
	 * CB added.
	 */
	public void stopMonthlyDataWork() {
		//STOP_DSSVIEWER_METHODS = true;
		_schematic.clearValues();
		getFilterPanel().resetAllCache();
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean isReadyForMonthlyWork() {
		if (!isSelectedFilesThreadAlive() && !isSchematicValuesThreadAlive()
				&& !isUnselectedFilesThreadAlive()
				&& !isSelectedFilesLongTermAverageThreadAlive()
				&& !isSchematicDateBoxListMonthsThreadAlive()
				&& !isUnselectedFilesLongTermAverageThreadAlive()
				&& !isSchematicDateBoxListLongTermsThreadAlive())
			return true;
		return false;
	}

	/**
	 * CB added.
	 */
	void doMonthlyDataWork() {
		doMonthlyDataWork(-1);
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	void doMonthlyDataWork(final int dssType) {
		if (DISABLE_ALL_THREAD_WORK){
			return;
		}
		_checker = new Thread() {
			public void run() {
				while (!isReadyForMonthlyWork()) {
					try {
						_checker.sleep(500);
						System.out
								.println("Not ready to restart monthly data; sleep for half second");
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				startMonthlyDataWork(dssType);
			}
		};
		_checker.start();
	}

	/**
	 * CB added. This should only be called from doMonthlyDataWork(int dssType).
	 * 
	 * @param dssType
	 */
	private void startMonthlyDataWork(int dssType) {
		// loadAllVariableData(dssType);
		// stopMonthlyDataWork();

		// System.out.println("Entered startMonthlyDataWork(dssType)");

		_schematic.resetProgressBars();

		_selectedFilesThread = new SelectedFilesThread(dssType);
		_selectedFilesThread.start();

		_updateSchematicDateBoxListMonthsThread = new UpdateSchematicDateBoxListMonthsThread();
		_updateSchematicDateBoxListMonthsThread.start();

		_updateSchematicValuesThread = new UpdateSchematicValuesThread();
		_updateSchematicValuesThread.start();

		_selectedFilesLongTermAverageThread = new SelectedFilesLongTermAveragesThread(
				dssType);
		//_selectedFilesLongTermAverageThread.start();

		_unselectedFilesThread = new UnselectedFilesThread(dssType);
		_unselectedFilesThread.start();

		// CB changed order to calculate selected files long term averages
		// before the unselected files data loading
		// (otherwise, the unselected files may load fasted and the progress go
		// invisible too soon or never be visible)
		// _selectedFilesLongTermAverageThread = new
		// SelectedFilesLongTermAveragesThread(dssType);
		// _selectedFilesLongTermAverageThread.start();

		_updateSchematicDateBoxListLongTermsThread = new UpdateSchematicDateBoxListLongTermsThread();
		_updateSchematicDateBoxListLongTermsThread.start();

		_unselectedFilesLongTermAverageThread = new UnselectedFilesLongTermAveragesThread(
				dssType);
		_unselectedFilesLongTermAverageThread.start();

		// System.out.println("Exited startMonthlyDataWork(dssType)");
	}

	/**
	 * CB added
	 * 
	 * @return
	 */
	DssViewer getValueViewer() {
		return _filterPanel.getValueViewer();
	}

	/**
	 * CB added.
	 */
	private void setSchematic(ISchematic schematic) {
		_schematic = schematic;
	}

	/**
	 * CB added.
	 */
	// void loadAllVariableData(Hashtable<String, Object> names, boolean
	// selectedFiles) {
	/*
	 * boolean loadUnSelectedVariableData() { return
	 * getFilterPanel().loadAllVariableData(getAllVariables(), false); }
	 */

	/**
	 * CB added.
	 */
	// void loadAllVariableData(Hashtable<String, Object> names, boolean
	// selectedFiles) {
	boolean loadSelectedVariableData(int dssType) {
		return getFilterPanel().loadAllVariableData(getAllVariables(), dssType,
				true, null);
	}

	/**
	 * CB added.
	 */
	// void loadAllVariableData(Hashtable<String, Object> names, boolean
	// selectedFiles) {
	/*
	 * boolean loadUnSelectedVariableData(int dssType) { return
	 * getFilterPanel().loadAllVariableData(getAllVariables(), dssType, false);
	 * }
	 */

	/**
	 * CB added.
	 * 
	 * @return
	 */
	Hashtable<String, Object> getAllVariables() {
		if (_schematic == null)
			return null;
		return _schematic.getAllVariables();
	}

	/**
	 * CB added.
	 */
	public Vector<ComboItem> loadDateBoxList() {
		Vector<ComboItem> datelist = new Vector<ComboItem>();
		String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
				"AUG", "SEP", "OCT", "NOV", "DEC" };
		// v. slow String[] range = _filterPanel.retrieveDateRangeFromBase();
		String[] range = _filterPanel.retrieveDateRangeFromTimeWindow();
		String startMonth = range[0].substring(0, 3);
		int startYear = Integer.parseInt(range[0].substring(3, range[0]
				.length()));
		String endMonth = range[1].substring(0, 3);
		int endYear = Integer
				.parseInt(range[1].substring(3, range[1].length()));
		String month = startMonth;
		int year = startYear;
		int monthIndex = -1;
		int endIndex = -1;
		for (int i = 0; i < months.length; ++i) {
			if (month.toUpperCase().equals(months[i])) {
				monthIndex = i;
				break;
			}
		}
		for (int i = 0; i < months.length; ++i) {
			if (endMonth.toUpperCase().equals(months[i])) {
				endIndex = i;
				break;
			}
		}
		datelist.add(new ComboItem(" ")); // start with a blank first entry.
		Vector<String> list = _messagePanel.getTimeWindowItemList();
		Enumeration<String> timeWindows = list.elements();
		while (timeWindows.hasMoreElements()) {
			ComboItem item = new ComboItem(timeWindows.nextElement());
			// item.setEnabled(false);
			datelist.add(item);
			// datelist.get(datelist.size() - 1).setEnabled(false);
		}
		while (year < endYear || (year == endYear && endIndex >= monthIndex)) {
			datelist.add(new ComboItem(months[monthIndex] + " " + year));
			if (year == endYear && months[monthIndex].equals(endMonth))
				break;
			++monthIndex;
			monthIndex %= 12;
			if (monthIndex == 0)
				++year;
		}
		return datelist;
	}

	/**
	 * CB added.
	 * 
	 * @param model
	 */
	void setDateBoxModel(ComboBoxModel model) {
		ComboBoxModel oldModel= _schematic.getDateBox().getModel();
		if (oldModel != null){
			model.setSelectedItem(oldModel.getSelectedItem());
		}
		_schematic.getDateBox().setModel(model);
		_schematic.getDateBox().setRenderer(new ComboRenderer()); // CB TODO -
																	// find
																	// better
																	// place for
																	// this
		_schematic.getDateBox().addActionListener(
				new ComboListener(_schematic.getDateBox())); // CB TODO - find
																// better place
																// for this
	}

	/**
	 * CB added.
	 */
	// void updateSchematicValues() {
	// _schematic.updateValues();
	// }

	/**
	 * CB added.
	 */
	// void updateSchematicValues(String date) {
	// _schematic.updateValues(date);
	// }

	/**
	 * CB added.
	 * 
	 * @return
	 */
	String getSchematicDate() {
		if (_schematic != null && _schematic.getDateBox() != null
				&& _schematic.getDateBox().getSelectedItem() != null)
			return ((ComboItem) _schematic.getDateBox().getSelectedItem())
					.toString();
		return null;
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	boolean isDateBoxListEmpty() {
		// System.out.println(_schematic.getDateBox().getModel().getSize());
		if (_schematic.getDateBox() == null
				|| _schematic.getDateBox().getModel().getSize() == 0)
			return true;
		return false;
	}

	/**
	 * CB added.
	 * 
	 * @param selectedTimeWindow
	 */
	void updateSchematicDateBox(String selectedTimeWindow) {
		// CB TODO need to check time window for
		// validity!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if (!dateBoxContainsTimeWindow(selectedTimeWindow)) {
			if (_schematic.getDateBox() != null && _schematic.getDateBox().getModel().getSize() > 0
					&& _schematic.getDateBox().getModel() instanceof DefaultComboBoxModel) {
				((DefaultComboBoxModel) _schematic.getDateBox().getModel())
						.insertElementAt(new ComboItem(selectedTimeWindow), 1); // 0 is the
																	// blank one

			}
		}
	}

	/**
	 * CB added.
	 */
	void disableSchematicDateBoxMonthItems() {
		_schematic.disableMonthItems();
	}

	/**
	 * CB added.
	 */
	void disableSchematicDateBoxLongtermItems() {
		_schematic.disableLongtermItems();
	}

	/**
	 * CB added.
	 * 
	 * @param units
	 */
	void updateSchematicUnitsButtons(int units) {
		_schematic.setUnitsButtons(units); // ???????????????????????????????????????????????????????????????????
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	int getSchematicSelectedUnits() {
		return _schematic.getSelectedUnits();
	}

	/**
	 * CB added. The Schematic date combobox model must be of type
	 * DefaultComboBoxModel.
	 * 
	 * @param selectedTimeWindow
	 * @return
	 */
	boolean dateBoxContainsTimeWindow(String tw) {
		if (_schematic.getDateBox() == null
				|| _schematic.getDateBox().getModel() != null) {
			DefaultComboBoxModel model = ((DefaultComboBoxModel) _schematic
					.getDateBox().getModel());
			for (int i = 0; i < model.getSize(); ++i) {
				String timeWindow = ((ComboItem) model.getElementAt(i))
						.toString();
				if (timeWindow.equalsIgnoreCase(tw))
					return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * CB added.
	 */
	Vector<String> getSchematicTimeWindows() {
		return _schematic.getTimeWindows();
	}

	/**
	 * CB added.
	 * 
	 * @param filename
	 */
	void updateDSSMenu(String filename, boolean wasAdded) {
		_schematic.updateDSSMenu(filename, wasAdded);
	}

	/**
	 * CB added.
	 */
	void resetSchematicValueNodeNames() {
		_schematic.resetValueNodeNames();
	}

	/**
	 * CB added.
	 */
	void setSchematicMonthlyProgress(int value) {
		_schematic.setMonthlyProgress(value);
	}

	/**
	 * CB added.
	 */
	void setSchematicLongtermProgress(int value) {
		_schematic.setLongtermProgress(value);
	}

	public DssAppAction getActions() {
		return _aa;
	}

	void initApp(ISchematic schematic) {
		_frame.setJMenuBar(initMenus());
		setLayout(new BorderLayout(5, 5));
		setSchematic(schematic);

		_messagePanel = new MessagePanel(_aa, this, getSchematicSelectedUnits());
		add(_messagePanel.getMessagePanelComp(), BorderLayout.NORTH);

		_filterPanel = new FilterPanel(_aa, this);
		add(_filterPanel, BorderLayout.CENTER);

		JPanel statusPanel = createStatusPanel();
		add(statusPanel, BorderLayout.SOUTH);

		// add(initToolbar(), BorderLayout.LINE_START);

		// testFilter();
		AppAction.updateAllActions();
	}

	public FilterPanel getFilterPanel() {
		return _filterPanel;
	}

	public MessagePanel getMessagePanel() {
		return _messagePanel;
	}

	/*
	 * protected JToolBar initToolbar() { JToolBar toolBar = new
	 * JToolBar(SwingConstants.VERTICAL);
	 * 
	 * JButton button = null; button = toolBar.add(getActions().FilterAction);
	 * button = toolBar.add(getActions().RetrieveAction); button =
	 * toolBar.add(getActions().BaseDVOpenAction);
	 * 
	 * return toolBar; }
	 */

	protected JMenuBar initMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu filemenu = new JMenu();
		// JMenu editmenu = new JMenu();

		JMenuItem item = null;
		JMenu menu = null;

		// 'File'
		filemenu.setText("File");
		filemenu.setMnemonic('F');

		// 'File' -> project
		menu = new JMenu("Project");
		// item = menu.add(new JMenuItem("New"));
		// item = menu.add(new JMenuItem("Open"));
		item = menu.add(getActions().ViewerNewFileAction);
		item = menu.add(getActions().ViewerOpenFileAction);
		// item = menu.add(new JMenuItem("Save"));
		// item = menu.add(new JMenuItem("Save As"));
		item = menu.add(getActions().ViewerSaveFileAction);
		item = filemenu.add(menu);

		// 'File' -> open DV files
		filemenu.addSeparator();

		menu = new JMenu("Open Base");
		item = menu.add(getActions().BaseDVOpenFileAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				Event.CTRL_MASK));
		item = menu.add(getActions().BaseSVOpenFileAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				Event.CTRL_MASK));
		item = filemenu.add(menu);

		menu = new JMenu("Open Comp1");
		item = menu.add(getActions().Comp1DVOpenFileAction);
		item = menu.add(getActions().Comp1SVOpenFileAction);
		item = filemenu.add(menu);

		menu = new JMenu("Open Comp2");
		item = menu.add(getActions().Comp2DVOpenFileAction);
		item = menu.add(getActions().Comp2SVOpenFileAction);
		item = filemenu.add(menu);

		menu = new JMenu("Open Comp3");
		item = menu.add(getActions().Comp3DVOpenFileAction);
		item = menu.add(getActions().Comp3SVOpenFileAction);
		item = filemenu.add(menu);

		// 'File' -> exit
		filemenu.addSeparator();
		item = filemenu.add(getActions().ExitAction);

		menuBar.add(filemenu);

		// 'Edit'
		// editmenu.setText("Edit");
		// editmenu.setMnemonic('E');
		// menuBar.add(editmenu);

		return menuBar;
	}

	private JPanel createStatusPanel() {
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setVisible(false); // // ?? temporarily false //CB wonder
										// what progress Tom wanted to show here
		JLabel name = new JLabel();
		_status = new JLabel();
		setStatus();
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(progressBar);
		panel.add(name);
		panel.add(_status);
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}

	public void setStatus(String s) {
		_status.setText("Status: " + s);
	}

	public void setStatus() {
		_status.setText("Status: Done");
	}

	void exit() {
		// _frame.destroy();
		_frame.dispose();
		// System.exit(0);
	}
}
