package wrims.dss;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wrims.dss.dts.DTSTable;
import wrims.dss.dts.DerivedTimeSeries;
import wrims.schematic.AppAction;
import wrims.schematic.FilterPanel;

/**
 * Frame for showing and specifying mathematical equation of the
 * DeriverTimeSeries. Allows addition or deletion of new rows (terms).
 * 
 * @author Clay Booher
 */
public class DashboardVariableWindow extends JFrame implements
		ListSelectionListener {
	// public final static String TYPE = "ALL_LAYER";
	private String _commonName;
	private String _variableName;
	/** Table to display all layers in the database */
	private DTSTable _dtsTable;

	/** TableModel for _allLayerTable */
	// AllLayerTableModel _allLayerTableModel;

	/**
	 * Creates the window.
	 * 
	 * @param title
	 *            the title of the window
	 */
	public// public DashboardVariableWindow(String commonName, String
			// variableName, FilterPanel panel) {
	DashboardVariableWindow(String commonName, DerivedTimeSeries dts,
			FilterPanel panel) {
		super(commonName + " Derived Timeseries Window");
		// buildWindow(commonName, variableName, panel);
		buildWindow(commonName, dts, panel);
		setMinimumSize(new Dimension(550, 400));
	}

	/**
	 * Builds the window, including
	 */
	public void buildWindow(String commonName, DerivedTimeSeries dts,
			FilterPanel panel) {
		buildMenu();
		// _dtsTable = new DTSTable(new DerivedTimeSeries(commonName), panel);
		_dtsTable = new DTSTable(dts, panel);
		setCommonName(commonName);
		// setVariableName(variableName);

		// _allLayerTableModel = new AllLayerTableModel();
		// CB addedg TableSorter
		// TableSorter sorter = new
		// TableSorter(_dtsTable.getTable().getModel()); //CB TODO ? if need,
		// then put into DTSTable class
		// _allLayerTable = new WrimsTable(sorter);

		// _allLayerTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// set column widths
		// _allLayerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// setTableColumnWidths(_dtsTable.getTable());

		ListSelectionModel lsm = _dtsTable.getTable().getSelectionModel();
		lsm.addListSelectionListener(this); // listen for row selection events
											// in _allLayerTable
		// _dtsTable.getTable().addMouseListener(layerML);

		// JScrollPane layerPane = new JScrollPane(_dtsTable.getTable());
		// getContentPane().add(layerPane, BorderLayout.CENTER);
		// getContentPane().add(_dtsTable.getTable(), BorderLayout.CENTER);
		getContentPane().add(_dtsTable, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				writePreferences();
				dispose();
			}
		});
	}

	/**
	 * Creates the menubar
	 */
	public void buildMenu() {
		// "File" Menu
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Close Window");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writePreferences();
				dispose();
			}
		});
		file.add(exit);
		mb.add(file);
		setJMenuBar(mb);
	}

	private void writePreferences() {
		_dtsTable.writePreferences();
	}

	/**
	 *
	 */
	// public void setTableColumnMaxWidths() {
	// GuiUtils.setTableColumnMaxWidths(_allLayerTable);
	// }

	public void setCommonName(String name) {
		_commonName = name;
	}

	public void setDTS(DerivedTimeSeries dts) {
		_dtsTable.setDTS(dts, null);
	}

	// public void setVariableName(String name) {
	// _variableName = name;
	// }

	/**
	 * 
	 * @return
	 */
	/*
	 * public AllLayerTableModel getAllLayerTableModel() { return
	 * _allLayerTableModel; }
	 */

	/**
	 *
	 *
	 */
	/*
	 * void writePreferences() { writeColumnWidthPreferences(_allLayerTable); }
	 */

	/**
	 *
	 */
	/*
	 * public void readColumnWidthPreferences() {
	 * _allLayerTable.readColumnWidthPreferences(); }
	 */

	/**
	 * Refreshes the table and MetaData text area.
	 */
	/*
	 * public void refreshData() { //re-set the table models _allLayerTableModel
	 * = new AllLayerTableModel(); TableSorter sorter = new
	 * TableSorter(_allLayerTableModel); //CB added TableSorter
	 * _allLayerTable.setModel(sorter); setTableColumnWidths(_allLayerTable); }
	 */

	/**
	 * 
	 * @param e
	 *            the ListSelectionEvent which is only used to quickly return if
	 *            the value is being adjusted.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) { // if not currently being adjusted
		/*
		 * if (_dtsTable.getTable().getSelectedRowCount() > 0) { int[]
		 * rowsSelected = _dtsTable.getTable().getSelectedRows(); for (int i =
		 * 0; i < rowsSelected.length; ++i) { } }
		 */

			AppAction.updateAllActions();
		}
	}
}
