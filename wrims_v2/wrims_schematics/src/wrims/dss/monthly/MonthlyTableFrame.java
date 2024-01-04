package wrims.dss.monthly;

import hec.gfx2d.G2dMarkerProperties;
import hec.io.DataContainer;
import hec.io.PairedDataContainer;
import hec.io.TimeSeriesContainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.PrintGraphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import rma.awt.table.TableExportOptions;
import rma.util.RMAIO;
import wrims.dss.DssViewer;

public class MonthlyTableFrame extends JFrame implements ActionListener {
	public static String SHOW_COMMAS_PROPERTY = "SHOW COMMAS";

	public static String SHOW_TOTAL_COLUMN_PROPERTY = "SHOW TOTAL COLUMN";

	public static String DECIMAL_PLACES_PROPERTY = "DECIMAL_PLACES";
	/*  */
	public static String SHOW_COMMAS = "showMonthlyCommas";

	public static String SHOW_TOTAL_COLUMN = "showTotalColumn";

	public static String DECIMAL_PLACES = "decimalPlaces";

	protected Outputer _parent;

	protected MonthlyTable _table;

	protected JPanel _tablePanel;

	protected JScrollPane _tableScrollPane;

	protected JMenuBar _menuBar;

	protected JMenu _fileMenu;

	protected JMenuItem _saveItem;

	protected JMenuItem _saveAsItem;

	protected JMenuItem _printItem;

	protected JMenuItem _printPreview;

	protected JMenuItem _export;

	private JMenuItem _tabulateItem;

	protected JMenuItem _plotItem;

	protected JMenuItem _exitItem;

	protected JMenu _viewMenu;

	protected ButtonGroup _decimalGroup;

	protected JMenu _viewDecimals;

	protected JCheckBoxMenuItem _viewCommas;

	protected JCheckBoxMenuItem _viewTotalColumn;

	protected JRadioButtonMenuItem _decimalAuto;

	protected JRadioButtonMenuItem _decimal0;

	protected JRadioButtonMenuItem _decimal1;

	protected JRadioButtonMenuItem _decimal2;

	protected JRadioButtonMenuItem _decimal3;

	protected JRadioButtonMenuItem _decimal4;

	protected JRadioButtonMenuItem _decimal5;

	protected JRadioButtonMenuItem _decimal6;

	protected Clipboard _sysClipboard = null;

	private Preferences _preferences;

	private boolean _showCommas = true;

	private boolean _showTotalColumn = true;

	private int _decimalPlaces = -3;

	class SymWindow extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			Object object = event.getSource();
			if (object == MonthlyTableFrame.this)
				Exit_Action();
		}
	}

	public MonthlyTableFrame(Outputer parent) {
		_preferences = Preferences.userNodeForPackage(getClass());
		_parent = parent;
		setDefaultCloseOperation(0);
		_sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		String[] precisionLevels = new String[6];
		JComboBox _timeZoneBox = new JComboBox(precisionLevels); // for users to
																	// set
																	// timezone??????
		JLabel precisionLabel = new JLabel("Decimal Places: ");
		getContentPane().add(panel, "North");
		// _table = new MonthlyTable(this);
		setShowCommas(_preferences.getBoolean(SHOW_COMMAS, true));
		setShowTotalColumn(_preferences.getBoolean(SHOW_TOTAL_COLUMN, true));
		setDecimalPlaces(_preferences.getInt(DECIMAL_PLACES, 0));
		_table = new MonthlyTable(this);
		_tableScrollPane = _table.getScrollPane();
		getContentPane().add(_tableScrollPane, "Center");
	}

	private void buildMenu() {
		_menuBar = new JMenuBar();
		_fileMenu = new JMenu("File");
		_fileMenu.setMnemonic('F');
		_menuBar.add(_fileMenu);
		_saveItem = new JMenuItem("Save", 'S');
		_saveItem.setAccelerator(KeyStroke.getKeyStroke('S', 2, false));
		_saveItem.addActionListener(this);
		_saveItem.setEnabled(false);
		_fileMenu.add(_saveItem);
		_saveAsItem = new JMenuItem("Save As...", 'a');
		_saveAsItem.addActionListener(this);
		_saveAsItem.setEnabled(false);
		_fileMenu.add(_saveAsItem);
		_fileMenu.addSeparator();
		_printItem = new JMenuItem("Print...", 'P');
		_printItem.setAccelerator(KeyStroke.getKeyStroke('P', 2, false));
		_printItem.addActionListener(this);
		_fileMenu.add(_printItem);
		_printPreview = new JMenuItem("Print Preview...", 'r');
		_printPreview.addActionListener(this);
		_printPreview.setEnabled(false);
		_fileMenu.add(_printPreview);
		_export = new JMenuItem("Export...", 'E');
		_export.addActionListener(this);
		_fileMenu.add(_export);
		_fileMenu.addSeparator();
		_tabulateItem = new JMenuItem("Tabulate", 'T');
		_tabulateItem.addActionListener(this);
		_fileMenu.add(_tabulateItem);
		_plotItem = new JMenuItem("Plot", 108);
		_plotItem.addActionListener(this);
		_fileMenu.add(_plotItem);
		_fileMenu.addSeparator();
		_exitItem = new JMenuItem("Close", 67);
		_exitItem.setAccelerator(KeyStroke.getKeyStroke(87, 2, false));
		_exitItem.addActionListener(this);
		_fileMenu.add(_exitItem);
		_viewMenu = new JMenu("View");
		_viewMenu.setMnemonic('V');

		_viewCommas = new JCheckBoxMenuItem("Commas", _showCommas);
		_viewCommas.setMnemonic('S');
		_viewCommas.addActionListener(this);
		_viewMenu.add(_viewCommas);

		if (_parent instanceof DssViewer
				&& ((DssViewer) _parent).isTAFSelected()) { // No average or
															// total column when
															// selected units
															// are CFS
		// _showAverageColumn = false; // need to set in other places in class
		// as well
			_viewTotalColumn = new JCheckBoxMenuItem("Total Column",
					_showTotalColumn);
			_viewTotalColumn.setMnemonic('A');
			_viewTotalColumn.addActionListener(this);
			_viewTotalColumn.setSelected(_showTotalColumn);
			_viewMenu.add(_viewTotalColumn);
		}

		_viewDecimals = new JMenu("Show Decimal Places");
		_viewDecimals.setMnemonic('D');
		_decimalGroup = new ButtonGroup();
		_decimalAuto = new JRadioButtonMenuItem("auto");
		_decimalAuto.setMnemonic('a');
		_decimalAuto.setSelected(true);
		_decimalGroup.add(_decimalAuto);
		_viewDecimals.add(_decimalAuto);
		_decimalAuto.addActionListener(this);
		_decimal0 = new JRadioButtonMenuItem("0.");
		_decimal0.setMnemonic('0');
		_decimalGroup.add(_decimal0);
		_viewDecimals.add(_decimal0);
		_decimal0.addActionListener(this);
		_decimal1 = new JRadioButtonMenuItem("0.0");
		_decimal1.setMnemonic('1');
		_decimalGroup.add(_decimal1);
		_viewDecimals.add(_decimal1);
		_decimal1.addActionListener(this);
		_decimal2 = new JRadioButtonMenuItem("0.00");
		_decimal2.setMnemonic('2');
		_decimalGroup.add(_decimal2);
		_viewDecimals.add(_decimal2);
		_decimal2.addActionListener(this);
		_decimal3 = new JRadioButtonMenuItem("0.000");
		_decimal3.setMnemonic('3');
		_decimalGroup.add(_decimal3);
		_viewDecimals.add(_decimal3);
		_decimal3.addActionListener(this);
		_decimal4 = new JRadioButtonMenuItem("0.0000");
		_decimal4.setMnemonic('4');
		_decimalGroup.add(_decimal4);
		_viewDecimals.add(_decimal4);
		_decimal4.addActionListener(this);
		_decimal5 = new JRadioButtonMenuItem("0.00000");
		_decimal5.setMnemonic('5');
		_decimalGroup.add(_decimal5);
		_viewDecimals.add(_decimal5);
		_decimal5.addActionListener(this);
		_decimal6 = new JRadioButtonMenuItem("0.000000");
		_decimal6.setMnemonic('6');
		_decimalGroup.add(_decimal6);
		_viewDecimals.add(_decimal6);
		_decimal6.addActionListener(this);
		switch (_decimalPlaces) {
		case 0:
			_decimal0.setSelected(true);
			break;
		case 1:
			_decimal1.setSelected(true);
			;
			break;
		case 2:
			_decimal2.setSelected(true);
			;
			break;
		case 3:
			_decimal3.setSelected(true);
			;
			break;
		case 4:
			_decimal4.setSelected(true);
			;
			break;
		case 5:
			_decimal5.setSelected(true);
			;
			break;
		case 6:
			_decimal6.setSelected(true);
			;
			break;
		default:
			_decimalAuto.setSelected(true);
			;
		}

		_viewMenu.add(_viewDecimals);
		_menuBar.add(_viewMenu);
		setJMenuBar(_menuBar);
		SymWindow aSymWindow = new SymWindow();
		addWindowListener(aSymWindow);
	}

	public int setData(List dataSets, int firstMonth) {
		boolean showTotalColumn = true;
		// CB - No total column when selected units are CFS
		// CB - cannot use units here due to more than one data set. It is
		// handled later
		if (_parent instanceof DssViewer
				&& !((DssViewer) _parent).isTAFSelected())
			showTotalColumn = false;
		int status = _table.setData(dataSets, firstMonth, _showCommas,
				showTotalColumn, _decimalPlaces);
		if (status == 0) {
			showTable();
		}
		return status;
	}

	public int setData(TimeSeriesContainer timeSeriesContainer, int firstMonth) {
		boolean showTotalColumn = true;
		// CB added - No total column when selected units are CFS
		if (_parent instanceof DssViewer
				&& (!((DssViewer) _parent).isTAFSelected() || !(timeSeriesContainer.units
						.trim().equalsIgnoreCase("TAF"))))
			showTotalColumn = false;
		int status = _table.setData(timeSeriesContainer, firstMonth,
				_showCommas, showTotalColumn, _decimalPlaces); // CB altered
		if (status == 0) {
			showTable();
		}
		return status;
	}

	public int addData(DataContainer dc, int firstMonth) {
		int status = -1;
		if (dc instanceof TimeSeriesContainer) {
			TimeSeriesContainer tsc = (TimeSeriesContainer) dc;
			boolean showTotalColumn = true;
			// CB added - No total column when selected units are CFS
			if (_parent instanceof DssViewer
					&& (!((DssViewer) _parent).isTAFSelected() || !((TimeSeriesContainer) dc).units
							.trim().equalsIgnoreCase("TAF")))
				showTotalColumn = false;
			status = _table.addData(tsc, firstMonth, _showCommas,
					showTotalColumn, _decimalPlaces); // CB altered
		}
		return status;
	}

	void setShowCommas(boolean showCommas) {
		if (showCommas != _showCommas) {
			_showCommas = showCommas;
			if (_viewCommas != null) {
				_viewCommas.setSelected(_showCommas);
				ViewCommas_Action();
			} else {
				validate();
				repaint();
				firePropertyChange(SHOW_COMMAS_PROPERTY, !_showCommas,
						_showCommas);
			}
		}
	}

	public boolean showCommas() {
		return _showCommas;
	}

	/**
	 * CB added. Changes the global property if different and depending on
	 * selected units in DssViewer. Units of each data set must be checked
	 * elsewhere.
	 * 
	 * @param showTotalColumn
	 */
	void setShowTotalColumn(boolean showTotalColumn) {
		// No average or total column when selected units are CFS (overriding)
		if (_parent instanceof DssViewer
				&& !((DssViewer) _parent).isTAFSelected())
			if (showTotalColumn != _showTotalColumn) {
				_showTotalColumn = showTotalColumn;
				if (_viewTotalColumn != null) {
					_viewTotalColumn.setSelected(_showTotalColumn);
					ViewTotalColumn_Action();
				} else {
					validate();
					repaint();
					firePropertyChange(SHOW_TOTAL_COLUMN_PROPERTY,
							!_showTotalColumn, _showTotalColumn);
				}
			}
	}

	/**
	 * CB added.
	 * 
	 * @return
	 */
	public boolean showTotalColumn() {
		return _showTotalColumn;
	}

	void setDecimalPlaces(int decimalPlaces) {
		if (decimalPlaces != _decimalPlaces) {
			int oldPlaces = _decimalPlaces;
			_decimalPlaces = decimalPlaces;
			JMenuItem menuitem = null;
			switch (_decimalPlaces) {
			case 0:
				menuitem = _decimal0;
				break;
			case 1:
				menuitem = _decimal1;
				break;
			case 2:
				menuitem = _decimal2;
				break;
			case 3:
				menuitem = _decimal3;
				break;
			case 4:
				menuitem = _decimal4;
				break;
			case 5:
				menuitem = _decimal5;
				break;
			case 6:
				menuitem = _decimal6;
				break;
			default:
				menuitem = _decimalAuto;
			}

			if (menuitem != null) { // then all gui components are null;
									// DecimalPlaces_Action cannot be called
				menuitem.setSelected(true);
				DecimalPlaces_Action(_decimalPlaces);
			} else {
				validate();
				repaint();
				firePropertyChange(DECIMAL_PLACES_PROPERTY, oldPlaces,
						_decimalPlaces);
			}
		}
	}

	public void showTable() {
		setTitle(_table.getName()); // CB - TO DO: check it
		buildMenu();
		setSize(800, 600);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == _exitItem) // CB TODO switch to a switch statement
			Exit_Action();
		// else if (object == _allowEditing)
		// Edit_Action();
		// else if (object == _editInsertRows)
		// insertRows_Action();
		// else if (object == _editAppendRows)
		// appendRows_Action();
		// else if (object == _editDeleteRows)
		// deleteRows_Action();
		else if (object == _viewCommas)
			ViewCommas_Action();
		else if (object == _viewTotalColumn)
			ViewTotalColumn_Action();
		// else if (object == _saveItem)
		// save();
		// else if (object == _saveAsItem)
		// saveAs();
		else if (object == _printItem)
			print();
		else if (object == _printPreview)
			printPreview();
		else if (object == _export)
			export();
		else if (object == _tabulateItem) {
			String title = "BOB";
			tabulate(title);
		} else if (object == _plotItem) {
			boolean exceedence = false;
			boolean annualTot = false;
			boolean monthlyAvg = false;
			String title = _parent.getPlotTitle(exceedence, annualTot,
					monthlyAvg);
			plot(title, exceedence, annualTot, monthlyAvg);
		} else if (object == _decimalAuto)
			DecimalPlaces_Action(-1);
		else if (object == _decimal0)
			DecimalPlaces_Action(0);
		else if (object == _decimal1)
			DecimalPlaces_Action(1);
		else if (object == _decimal2)
			DecimalPlaces_Action(2);
		else if (object == _decimal3)
			DecimalPlaces_Action(3);
		else if (object == _decimal4)
			DecimalPlaces_Action(4);
		else if (object == _decimal5)
			DecimalPlaces_Action(5);
		else if (object == _decimal6)
			DecimalPlaces_Action(6);
	}

	protected void Exit_Action() {
		updateEdits();
		/*
		 * if (_table.hasDataChanged() == true) { int n =
		 * JOptionPane.showConfirmDialog(this, "Save changes to data set?",
		 * "Save Edited Changes", 1); if (n == 0) { int errors = save(); if
		 * (errors != 0) return; } else if (n == 1) terminate(); else return; }
		 */
		terminate();
	}

	protected void terminate() {
		dispose();
	}

	public int updateContainers() {
		updateEdits();
		/*
		 * CB int[] firstError = new int[3]; int numberErrors =
		 * _table.updateContainers(firstError); if (numberErrors > 0) {
		 * StringBuffer message = new StringBuffer(); if (numberErrors > 1)
		 * message.append(numberErrors + " errors found.  First Error:\n"); if
		 * (firstError[2] == 0)
		 * message.append("Invalid date / time at ordinate " + (firstError[0] +
		 * 1)); else { message.append("Dates / times are not ascending");
		 * HecTime time = new HecTime(); time.set(firstError[1]);
		 * message.append("\n    Date / time at ordinate " + firstError[0] +
		 * " is " + time); time.set(firstError[2]);
		 * message.append("\n    Date / time at ordinate " + (firstError[0] + 1)
		 * + " is " + time); } JOptionPane.showMessageDialog(this,
		 * message.toString(), "Table error", 2); return numberErrors; }
		 */
		return 0;
	}

	/*
	 * public void saveAs() { int errors = updateContainers(); if (errors == 0)
	 * _table.saveAs(_parent); validate(); }
	 */

	/*
	 * public int save() { int errors = updateContainers(); if (errors == 0)
	 * _table.save(_parent); validate(); return errors; }
	 */

	private void DecimalPlaces_Action(int numberDecimals) {
		int oldPlaces = _table.getPrecision();
		if (oldPlaces != numberDecimals) {
			_table.setPrecision(numberDecimals);
			_preferences.putInt(DECIMAL_PLACES, numberDecimals);
			validate();
			repaint();
			firePropertyChange(DECIMAL_PLACES_PROPERTY, oldPlaces,
					numberDecimals);
		}
	}

	private void ViewCommas_Action() {
		_showCommas = _viewCommas.getState();
		_table.setShowCommas(_showCommas);
		_preferences.putBoolean(SHOW_COMMAS, _showCommas);
		validate();
		repaint();
		firePropertyChange(SHOW_COMMAS_PROPERTY, !_showCommas, _showCommas);
	}

	private void ViewTotalColumn_Action() {
		_showTotalColumn = _viewTotalColumn.getState();
		_table.setShowTotalColumn(_showTotalColumn);
		_preferences.putBoolean(SHOW_TOTAL_COLUMN, _showTotalColumn);
		validate();
		repaint();
		firePropertyChange(SHOW_TOTAL_COLUMN_PROPERTY, !_showTotalColumn,
				_showTotalColumn);
	}

	public void print() {
		updateContainers();
		_table.print();
	}

	public void printPreview() {
		updateContainers();
		_table.printPreview();
	}

	public void export() {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportData();
		if (_showCommas)
			ViewCommas_Action();
	}

	public void export(String fileName, TableExportOptions options) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportData(fileName, options);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void export(BufferedWriter writer, TableExportOptions options) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportData(writer, options);
		if (_showCommas)
			ViewCommas_Action();
	}

	public String getExportString(TableExportOptions options) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		String s = _table.getExportString(options);
		if (_showCommas)
			ViewCommas_Action();
		return s;
	}

	public void exportAsXML(BufferedWriter writer, String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsXML(writer, title, indent);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsXML(BufferedWriter writer) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsXML(writer);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsXML(String fileName, String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsXML(fileName, title, indent);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsXML(String fileName) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsXML(fileName);
		if (_showCommas)
			ViewCommas_Action();
	}

	public String getXMLExportString(String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		String s = _table.getXMLExportString(title, indent);
		if (_showCommas)
			ViewCommas_Action();
		return s;
	}

	public String getXMLExportString() {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		String s = _table.getXMLExportString();
		if (_showCommas)
			ViewCommas_Action();
		return s;
	}

	public void exportAsHTML(BufferedWriter writer, String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsHTML(writer, title, indent);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsHTML(BufferedWriter writer) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsHTML(writer);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsHTML(String fileName, String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsHTML(fileName, title, indent);
		if (_showCommas)
			ViewCommas_Action();
	}

	public void exportAsHTML(String fileName) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		_table.exportAsHTML(fileName);
		if (_showCommas)
			ViewCommas_Action();
	}

	public String getHTMLExportString(String title, String indent) {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false);
		String s = _table.getHTMLExportString(title, indent);
		if (_showCommas)
			ViewCommas_Action();
		return s;
	}

	public String getHTMLExportString() {
		updateContainers();
		if (_showCommas)
			_table.setShowCommas(false); // temporarily change _table's
											// _showCommas to false
		String s = _table.getHTMLExportString();
		if (_showCommas)
			ViewCommas_Action();// return _table's _showCommas to true
		return s;
	}

	public int getColumn(String columnHeader) {
		String[] columnHeaders = _table.getColumnLabels();
		int col = -1;
		for (int i = 0; i < columnHeaders.length; i++) {
			if (columnHeader.equalsIgnoreCase(columnHeaders[i])) {
				col = i;
				break;
			}
		}
		return col;
	}

	public int getColumn(DataContainer dc) {
		String header = null;
		if (dc instanceof TimeSeriesContainer) {
			TimeSeriesContainer tsc = (TimeSeriesContainer) dc;
			header = tsc.location + "\n" + tsc.parameter + "\n" + tsc.version;
		} else if (dc instanceof PairedDataContainer) {
			PairedDataContainer pd = (PairedDataContainer) dc;
			header = pd.xparameter;
		}
		return getColumn(header);
	}

	public String[] getColumnLabels() {
		return _table.getColumnLabels();
	}

	public String getColumnLabel(int colNum) {
		return _table.getColumnLabel(colNum);
	}

	public void setColumnLabels(String[] labels) {
		_table.setColumnLabels(labels);
	}

	public void setColumnLabel(int colNum, String label) {
		_table.setColumnLabel(colNum, label);
	}

	public int[] getColumnWidths() {
		return _table.getColumnWidths();
	}

	public void setColumnWidths(int[] widths) {
		_table.setColumnWidths(widths);
	}

	public int getColumnWidth(int col) {
		return _table.getColumnWidth(col);
	}

	public void setColumnWidth(int col, int width) {
		_table.setColumnWidth(col, width);
	}

	// public void setColumnPrecision(int col, int precision) {
	// _table.setColumnPrecision(col, precision);
	// }

	public Color getColumnHeaderForeground(int col) {
		return _table.getColumnHeaderForeground(col);
	}

	public String getColumnHeaderForegroundString(int col) {
		return RMAIO.color2String(getColumnHeaderForeground(col));
	}

	public void setColumnHeaderForeground(int colNum, Color color) {
		_table.setColumnHeaderForeground(colNum, color);
	}

	public void setColumnHeaderForeground(int colNum, String color) {
		_table.setColumnHeaderForeground(colNum, RMAIO.parseColorString(color));
	}

	public Color getColumnHeaderBackground(int col) {
		return _table.getColumnHeaderBackground(col);
	}

	public String getColumnHeaderBackgroundString(int col) {
		return RMAIO.color2String(getColumnHeaderBackground(col));
	}

	public void setColumnHeaderBackground(int colNum, Color color) {
		_table.setColumnHeaderBackground(colNum, color);
	}

	public void setColumnHeaderBackground(int colNum, String color) {
		_table.setColumnHeaderBackground(colNum, RMAIO.parseColorString(color));
	}

	public Font getColumnHeaderFont(int colNum) {
		return _table.getColumnHeaderFont(colNum);
	}

	public String getColumnHeaderFontString(int colNum) {
		return G2dMarkerProperties.font2string(_table
				.getColumnHeaderFont(colNum));
	}

	public void setColumnHeaderFont(int colNum, Font font) {
		_table.setColumnHeaderFont(colNum, font);
	}

	public void setColumnHeaderFont(int colNum, String font) {
		_table.setColumnHeaderFont(colNum, G2dMarkerProperties
				.string2font(font));
	}

	public Color getRowForeground(int rowNum) {
		return _table.getRowForeground(rowNum);
	}

	public String getRowForegroundString(int rowNum) {
		return RMAIO.color2String(_table.getRowForeground(rowNum));
	}

	public void setRowForeground(int rowNum, Color color) {
		_table.setRowForeground(rowNum, color);
	}

	public void setRowForeground(int rowNum, String color) {
		_table.setRowForeground(rowNum, RMAIO.parseColorString(color));
	}

	public Color getRowBackground(int rowNum) {
		return _table.getRowBackground(rowNum);
	}

	public String getRowBackgroundString(int rowNum) {
		return RMAIO.color2String(_table.getRowBackground(rowNum));
	}

	public void setRowBackground(int rowNum, Color color) {
		_table.setRowBackground(rowNum, color);
	}

	public void setRowBackground(int rowNum, String color) {
		_table.setRowBackground(rowNum, RMAIO.parseColorString(color));
	}

	public Color getColumnForeground(int columnNum) {
		return _table.getColumnForeground(columnNum);
	}

	public String getColumnForegroundString(int columnNum) {
		return RMAIO.color2String(_table.getColumnForeground(columnNum));
	}

	public void setColumnForeground(int columnNum, Color color) {
		_table.setColumnForeground(columnNum, color);
	}

	public void setColumnForeground(int columnNum, String color) {
		_table.setColumnForeground(columnNum, RMAIO.parseColorString(color));
	}

	public Color getColumnBackground(int columnNum) {
		return _table.getColumnBackground(columnNum);
	}

	public String getColumnBackgroundString(int columnNum) {
		return RMAIO.color2String(_table.getColumnBackground(columnNum));
	}

	public void setColumnBackground(int columnNum, Color color) {
		_table.setColumnBackground(columnNum, color);
	}

	public void setColumnBackground(int columnNum, String color) {
		_table.setColumnBackground(columnNum, RMAIO.parseColorString(color));
	}

	public Color getCellForeground(int row, int col) {
		return _table.getCellForeground(row, col);
	}

	public String getCellForegroundString(int row, int col) {
		return RMAIO.color2String(_table.getCellForeground(row, col));
	}

	public void setCellForeground(int row, int col, Color color) {
		_table.setCellForeground(row, col, color);
	}

	public void setCellForeground(int row, int col, String color) {
		_table.setCellForeground(row, col, RMAIO.parseColorString(color));
	}

	public Color getCellBackground(int row, int col) {
		return _table.getCellBackground(row, col);
	}

	public String getCellBackgroundString(int row, int col) {
		return RMAIO.color2String(_table.getCellBackground(row, col));
	}

	public void setCellBackground(int row, int col, Color color) {
		_table.setCellBackground(row, col, color);
	}

	public void setCellBackground(int row, int col, String color) {
		_table.setCellBackground(row, col, RMAIO.parseColorString(color));
	}

	public void selectAll() {
		updateEdits();
		_table.selectAll();
	}

	public void copyAll() {
		updateEdits();
		String copyString = _table.allDataString();
		StringSelection selection = new StringSelection(copyString);
		_sysClipboard.setContents(selection, null);
	}

	protected void updateEdits() {
		AbstractCellEditor cellEditor = null;
		cellEditor = (AbstractCellEditor) _table.getCellEditor();
		if (cellEditor != null)
			cellEditor.stopCellEditing();
	}

	public void cutSelection() {
		updateEdits();
		_table.cut();
	}

	public void copySelection() {
		updateEdits();
		String copyString = _table.getSelectedDataString();
		StringSelection selection = new StringSelection(copyString);
		_sysClipboard.setContents(selection, null);
	}

	public void pasteSelection() {
		updateEdits();
		_table.paste();
	}

	void printLongString(PrintJob pjob, Graphics pg, String s) {
		int pageNum = 1;
		int linesForThisPage = 0;
		int linesForThisJob = 0;
		if (!(pg instanceof PrintGraphics))
			throw new IllegalArgumentException(
					"Graphics context not PrintGraphics");
		StringReader sr = new StringReader(s);
		LineNumberReader lnr = new LineNumberReader(sr);
		int pageHeight = pjob.getPageDimension().height;
		// int pageWidth = pjob.getPageDimension().width;
		Font helv = new Font("Courier", 0, 10);
		pg.setFont(helv);
		FontMetrics fm = pg.getFontMetrics(helv);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		int curHeight = 0;
		String pathname = "";
		String Header = "";
		boolean first = true;
		try {
			String nextLine;
			do {
				if (first == true) {
					pathname = lnr.readLine();
					Header = lnr.readLine();
					first = false;
				}
				nextLine = lnr.readLine();
				if (nextLine != null) {
					if (curHeight + fontHeight > pageHeight - 4 * fontHeight) {
						pageNum++;
						String t = "";
						t += pageNum - 1;
						linesForThisPage = 0;
						pg.dispose();
						pg = pjob.getGraphics();
						if (pg != null)
							pg.setFont(helv);
						curHeight = 0;
					}
					curHeight += fontHeight;
					if (pg != null) {
						if (curHeight - fontHeight == 0) {
							pg.drawString(pathname, 0, curHeight - fontDescent);
							curHeight += fontHeight;
							pg.drawString(Header, 0, curHeight - fontDescent);
							curHeight += fontHeight;
						}
						pg.drawString(nextLine, 0, curHeight - fontDescent);
						linesForThisPage++;
						linesForThisJob++;
					}
				}
			} while (nextLine != null);
		} catch (EOFException eofexception) {
			/* empty */
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	void tabulate(String title) {
		int numberErrors = updateContainers();
		if (numberErrors == 0)
			_parent.tabulate(title);
		// _table.plot(this);
	}

	void plot(String title, boolean isExceedence, boolean isAnnualTotal,
			boolean isMonthlyTotal) {
		int numberErrors = updateContainers();
		if (numberErrors == 0)
			_parent.plot(title, DssViewer.PLOT);
		// _table.plot(this);
	}

	// public void setTableTitleText(String text) {
	// _title.setText(text);
	// }

	// public String getTableTitleText() {
	// return _title.getText();
	// }

	// public Title getTableTitle() {
	// return _title;
	// }

	public MonthlyTable getTable() {
		return _table;
	}

	public void maximize() {
		setExtendedState(6);
	}

	public void minimize() {
		iconify();
	}

	public void iconify() {
		setExtendedState(1);
	}

	public void restore() {
		setExtendedState(0);
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
		invalidate();
		validate();
		repaint();
	}
}